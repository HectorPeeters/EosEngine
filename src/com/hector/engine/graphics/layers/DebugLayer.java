package com.hector.engine.graphics.layers;

import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.ShaderProgram;
import com.hector.engine.graphics.Texture;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.input.events.MouseButtonEvent;
import com.hector.engine.input.events.MouseMoveEvent;
import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.NuklearFontResource;
import org.lwjgl.nuklear.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class DebugLayer extends AbstractRenderLayer {

    private static final int BUFFER_INITIAL_SIZE = 4 * 1024;
    private static final int MAX_VERTEX_BUFFER = 512 * 1024;
    private static final int MAX_ELEMENT_BUFFER = 128 * 1024;

    private static final NkAllocator ALLOCATOR;

    private static final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;

    static {
        ALLOCATOR = NkAllocator.create()
                .alloc((handle, old, size) -> nmemAllocChecked(size))
                .mfree((handle, ptr) -> nmemFree(ptr));

        VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
                .position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
                .position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
                .position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
                .position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
                .flip();
    }

    private final NkContext ctx = NkContext.create();
    private final NkUserFont default_font = NkUserFont.create();

    private final NkBuffer cmds = NkBuffer.create();
    private final NkDrawNullTexture null_texture = NkDrawNullTexture.create();

    private long win;

    private int width, height;

    private int display_width, display_height;

    private Texture fontTexture;

    private int vbo, vao, ebo;
    private ShaderProgram shader;

    private final List<AbstractDebugWindow> debugWindows = new ArrayList<>();

    public DebugLayer(long window) {
        this.win = window;
    }

    public void addWindow(AbstractDebugWindow debugWindow) {
        debugWindows.add(debugWindow);
    }

    @Override
    public void init() {
        EventSystem.subscribe(this);

        NkContext ctx = setupInput(win);

        setupFont(ctx);

        for (AbstractDebugWindow window : debugWindows)
            window.create();

        glfwShowWindow(win);
    }

    private void setupFont(NkContext ctx) {
        NuklearFontResource resource = ResourceManager.getResource("fonts/Roboto-Regular.ttf");

        if (resource == null) {
            Logger.err("Graphics", "Failed to nuklear font resource");
            return;
        }

        ByteBuffer ttf = resource.getResource();

        int BITMAP_W = 1024;
        int BITMAP_H = 1024;

        int FONT_HEIGHT = 18;

        fontTexture = new Texture(BITMAP_W, BITMAP_H);

        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(95);

        float scale;
        float descent;

        try (MemoryStack stack = stackPush()) {
            stbtt_InitFont(fontInfo, ttf);
            scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

            IntBuffer d = stack.mallocInt(1);
            stbtt_GetFontVMetrics(fontInfo, null, d, null);
            descent = d.get(0) * scale;

            ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

            STBTTPackContext pc = STBTTPackContext.mallocStack(stack);
            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
            stbtt_PackSetOversampling(pc, 4, 4);
            stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
            stbtt_PackEnd(pc);

            // Convert R8 to RGBA8
            ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);
            for (int i = 0; i < bitmap.capacity(); i++) {
                texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
            }
            texture.flip();

            fontTexture.bind();
            fontTexture.setData(0, GL_RGBA8, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);
            fontTexture.setFilter(GL_LINEAR, GL_LINEAR);

            memFree(texture);
            memFree(bitmap);
        }

        default_font
                .width((handle, h, text, len) -> {
                    float text_width = 0;
                    try (MemoryStack stack = stackPush()) {
                        IntBuffer unicode = stack.mallocInt(1);

                        int glyph_len = nnk_utf_decode(text, memAddress(unicode), len);
                        int text_len = glyph_len;

                        if (glyph_len == 0) {
                            return 0;
                        }

                        IntBuffer advance = stack.mallocInt(1);
                        while (text_len <= len && glyph_len != 0) {
                            if (unicode.get(0) == NK_UTF_INVALID) {
                                break;
                            }

                            /* query currently drawn glyph information */
                            stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
                            text_width += advance.get(0) * scale;

                            /* offset next glyph */
                            glyph_len = nnk_utf_decode(text + text_len, memAddress(unicode), len - text_len);
                            text_len += glyph_len;
                        }
                    }
                    return text_width;
                })
                .height(FONT_HEIGHT)
                .query((handle, font_height, glyph, codepoint, next_codepoint) -> {
                    try (MemoryStack stack = stackPush()) {
                        FloatBuffer x = stack.floats(0.0f);
                        FloatBuffer y = stack.floats(0.0f);

                        STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
                        IntBuffer advance = stack.mallocInt(1);

                        stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false);
                        stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

                        NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

                        ufg.width(q.x1() - q.x0());
                        ufg.height(q.y1() - q.y0());
                        ufg.offset().set(q.x0(), q.y0() + (FONT_HEIGHT + descent));
                        ufg.xadvance(advance.get(0) * scale);
                        ufg.uv(0).set(q.s0(), q.t0());
                        ufg.uv(1).set(q.s1(), q.t1());
                    }
                })
                .texture(it -> it
                        .id(fontTexture.getId()));

        nk_style_set_font(ctx, default_font);

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render() {
        newFrame();

        for (AbstractDebugWindow debugWindow : debugWindows) {
            debugWindow.draw(ctx, 10, 10);
        }

        render(NK_ANTI_ALIASING_ON, MAX_VERTEX_BUFFER, MAX_ELEMENT_BUFFER);
    }


    @Override
    public void onEvent(LayerInputEvent event) {

    }

    @Override
    public void destroy() {
        shutdown();
    }

    private void shutdown() {
        for (AbstractDebugWindow window : debugWindows)
            window.destroy();

        fontTexture.destroy();

        Objects.requireNonNull(ctx.clip().copy()).free();
        Objects.requireNonNull(ctx.clip().paste()).free();
        nk_free(ctx);
        {
            shader.destroy();
            glDeleteTextures(default_font.texture().id());
            glDeleteTextures(null_texture.texture().id());
            glDeleteBuffers(vbo);
            glDeleteBuffers(ebo);
            nk_buffer_free(cmds);

            GL.setCapabilities(null);
        }

        Objects.requireNonNull(default_font.query()).free();
        Objects.requireNonNull(default_font.width()).free();

        Objects.requireNonNull(ALLOCATOR.alloc()).free();
        Objects.requireNonNull(ALLOCATOR.mfree()).free();
    }

    private void render(int AA, int max_vertex_buffer, int max_element_buffer) {
        try (MemoryStack stack = stackPush()) {
            // setup global state
            glEnable(GL_BLEND);
            glBlendEquation(GL_FUNC_ADD);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_SCISSOR_TEST);
            glActiveTexture(GL_TEXTURE0);

            // setup program
            shader.bind();
            shader.setInt("texture", 0);
            shader.setMatrix4f("projectionMatrix", stack.floats(
                    2.0f / width, 0.0f, 0.0f, 0.0f,
                    0.0f, -2.0f / height, 0.0f, 0.0f,
                    0.0f, 0.0f, -1.0f, 0.0f,
                    -1.0f, 1.0f, 0.0f, 1.0f
            ));

            glViewport(0, 0, display_width, display_height);
        }

        {
            // convert from command queue into draw list and draw to screen

            // allocate vertex and element buffer
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            glBufferData(GL_ARRAY_BUFFER, max_vertex_buffer, GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, max_element_buffer, GL_STREAM_DRAW);

            // load draw vertices & elements directly into vertex + element buffer
            ByteBuffer vertices = Objects.requireNonNull(glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, max_vertex_buffer, null));
            ByteBuffer elements = Objects.requireNonNull(glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, max_element_buffer, null));
            try (MemoryStack stack = stackPush()) {
                // fill convert configuration
                NkConvertConfig config = NkConvertConfig.callocStack(stack)
                        .vertex_layout(VERTEX_LAYOUT)
                        .vertex_size(20)
                        .vertex_alignment(4)
                        .null_texture(null_texture)
                        .circle_segment_count(22)
                        .curve_segment_count(22)
                        .arc_segment_count(22)
                        .global_alpha(1.0f)
                        .shape_AA(AA)
                        .line_AA(AA);

                // setup buffers to load vertices and elements
                NkBuffer vbuf = NkBuffer.mallocStack(stack);
                NkBuffer ebuf = NkBuffer.mallocStack(stack);

                nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/);
                nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/);
                nk_convert(ctx, cmds, vbuf, ebuf, config);
            }
            glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            glUnmapBuffer(GL_ARRAY_BUFFER);

            // iterate over and execute each draw command
            float fb_scale_x = (float) display_width / (float) width;
            float fb_scale_y = (float) display_height / (float) height;

            long offset = NULL;
            for (NkDrawCommand cmd = nk__draw_begin(ctx, cmds); cmd != null; cmd = nk__draw_next(cmd, cmds, ctx)) {
                if (cmd.elem_count() == 0) {
                    continue;
                }
                glBindTexture(GL_TEXTURE_2D, cmd.texture().id());
                glScissor(
                        (int) (cmd.clip_rect().x() * fb_scale_x),
                        (int) ((height - (int) (cmd.clip_rect().y() + cmd.clip_rect().h())) * fb_scale_y),
                        (int) (cmd.clip_rect().w() * fb_scale_x),
                        (int) (cmd.clip_rect().h() * fb_scale_y)
                );
                glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
                offset += cmd.elem_count() * 2;
            }
            nk_clear(ctx);
        }

        // default OpenGL state
        glUseProgram(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        glDisable(GL_BLEND);
        glDisable(GL_SCISSOR_TEST);
    }

    private void setupContext() {
        nk_buffer_init(cmds, ALLOCATOR, BUFFER_INITIAL_SIZE);

        shader = new ShaderProgram("nuklear");

        int attrib_pos = shader.getAttribute("position").getLocation();
        int attrib_uv = shader.getAttribute("texCoord").getLocation();
        int attrib_col = shader.getAttribute("color").getLocation();

        {
            // buffer setup
            vbo = glGenBuffers();
            ebo = glGenBuffers();
            vao = glGenVertexArrays();

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            glEnableVertexAttribArray(attrib_pos);
            glEnableVertexAttribArray(attrib_uv);
            glEnableVertexAttribArray(attrib_col);

            glVertexAttribPointer(attrib_pos, 2, GL_FLOAT, false, 20, 0);
            glVertexAttribPointer(attrib_uv, 2, GL_FLOAT, false, 20, 8);
            glVertexAttribPointer(attrib_col, 4, GL_UNSIGNED_BYTE, true, 20, 16);
        }

        {
            // null texture setup
            int nullTexID = glGenTextures();

            null_texture.texture().id(nullTexID);
            null_texture.uv().set(0.5f, 0.5f);

            glBindTexture(GL_TEXTURE_2D, nullTexID);
            try (MemoryStack stack = stackPush()) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    @Handler
    private void onMouseMoveEvent(MouseMoveEvent event) {
        nk_input_motion(ctx, event.xPixel, event.yPixel);
    }

    @Handler
    private void onKeyEvent(KeyEvent event) {
        boolean pressed = event.pressed;
        switch (event.keycode) {
            case GLFW_KEY_DELETE:
                nk_input_key(ctx, NK_KEY_DEL, pressed);
                break;
            case GLFW_KEY_ENTER:
                nk_input_key(ctx, NK_KEY_ENTER, pressed);
                break;
            case GLFW_KEY_TAB:
                nk_input_key(ctx, NK_KEY_TAB, pressed);
                break;
            case GLFW_KEY_BACKSPACE:
                nk_input_key(ctx, NK_KEY_BACKSPACE, pressed);
                break;
            case GLFW_KEY_UP:
                nk_input_key(ctx, NK_KEY_UP, pressed);
                break;
            case GLFW_KEY_DOWN:
                nk_input_key(ctx, NK_KEY_DOWN, pressed);
                break;
            case GLFW_KEY_HOME:
                nk_input_key(ctx, NK_KEY_TEXT_START, pressed);
                nk_input_key(ctx, NK_KEY_SCROLL_START, pressed);
                break;
            case GLFW_KEY_END:
                nk_input_key(ctx, NK_KEY_TEXT_END, pressed);
                nk_input_key(ctx, NK_KEY_SCROLL_END, pressed);
                break;
            case GLFW_KEY_PAGE_DOWN:
                nk_input_key(ctx, NK_KEY_SCROLL_DOWN, pressed);
                break;
            case GLFW_KEY_PAGE_UP:
                nk_input_key(ctx, NK_KEY_SCROLL_UP, pressed);
                break;
            case GLFW_KEY_LEFT_SHIFT:
            case GLFW_KEY_RIGHT_SHIFT:
                nk_input_key(ctx, NK_KEY_SHIFT, pressed);
                break;
            case GLFW_KEY_LEFT_CONTROL:
            case GLFW_KEY_RIGHT_CONTROL:
                if (pressed) {
                    nk_input_key(ctx, NK_KEY_COPY, glfwGetKey(win, GLFW_KEY_C) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_PASTE, glfwGetKey(win, GLFW_KEY_P) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_CUT, glfwGetKey(win, GLFW_KEY_X) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_UNDO, glfwGetKey(win, GLFW_KEY_Z) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_REDO, glfwGetKey(win, GLFW_KEY_R) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(win, GLFW_KEY_LEFT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(win, GLFW_KEY_RIGHT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_LINE_START, glfwGetKey(win, GLFW_KEY_B) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_TEXT_LINE_END, glfwGetKey(win, GLFW_KEY_E) == GLFW_PRESS);
                } else {
                    nk_input_key(ctx, NK_KEY_LEFT, glfwGetKey(win, GLFW_KEY_LEFT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_RIGHT, glfwGetKey(win, GLFW_KEY_RIGHT) == GLFW_PRESS);
                    nk_input_key(ctx, NK_KEY_COPY, false);
                    nk_input_key(ctx, NK_KEY_PASTE, false);
                    nk_input_key(ctx, NK_KEY_CUT, false);
                    nk_input_key(ctx, NK_KEY_SHIFT, false);
                }
                break;
        }
    }

    @Handler
    private void onMouseEvent(MouseButtonEvent event) {
        try (MemoryStack stack = stackPush()) {
            DoubleBuffer cx = stack.mallocDouble(1);
            DoubleBuffer cy = stack.mallocDouble(1);

            glfwGetCursorPos(win, cx, cy);

            int x = (int) cx.get(0);
            int y = (int) cy.get(0);

            int nkButton;
            switch (event.button) {
                case GLFW_MOUSE_BUTTON_RIGHT:
                    nkButton = NK_BUTTON_RIGHT;
                    break;
                case GLFW_MOUSE_BUTTON_MIDDLE:
                    nkButton = NK_BUTTON_MIDDLE;
                    break;
                default:
                    nkButton = NK_BUTTON_LEFT;
            }
            nk_input_button(ctx, nkButton, x, y, event.pressed);
        }
    }

    private NkContext setupInput(long win) {
        glfwSetScrollCallback(win, (window, xoffset, yoffset) -> {
            try (MemoryStack stack = stackPush()) {
                NkVec2 scroll = NkVec2.mallocStack(stack)
                        .x((float) xoffset)
                        .y((float) yoffset);
                nk_input_scroll(ctx, scroll);
            }
        });

        glfwSetCharCallback(win, (window, codepoint) -> nk_input_unicode(ctx, codepoint));

        nk_init(ctx, ALLOCATOR, null);
        ctx.clip()
                .copy((handle, text, len) -> {
                    if (len == 0) {
                        return;
                    }

                    try (MemoryStack stack = stackPush()) {
                        ByteBuffer str = stack.malloc(len + 1);
                        memCopy(text, memAddress(str), len);
                        str.put(len, (byte) 0);

                        glfwSetClipboardString(win, str);
                    }
                })
                .paste((handle, edit) -> {
                    long text = nglfwGetClipboardString(win);
                    if (text != NULL) {
                        nnk_textedit_paste(edit, text, nnk_strlen(text));
                    }
                });

        setupContext();
        return ctx;
    }

    @Override
    public void preUpdate(float delta) {
        nk_input_begin(ctx);
    }

    private void newFrame() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetWindowSize(win, w, h);
            width = w.get(0);
            height = h.get(0);

            glfwGetFramebufferSize(win, w, h);
            display_width = w.get(0);
            display_height = h.get(0);
        }

//        nk_input_begin(ctx);
//        glfwPollEvents();

        NkMouse mouse = ctx.input().mouse();

        if (mouse.grab()) {
            glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else if (mouse.grabbed()) {
            float prevX = mouse.prev().x();
            float prevY = mouse.prev().y();
            glfwSetCursorPos(win, prevX, prevY);
            mouse.pos().x(prevX);
            mouse.pos().y(prevY);
        } else if (mouse.ungrab()) {
            glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }

        nk_input_end(ctx);
    }
}
