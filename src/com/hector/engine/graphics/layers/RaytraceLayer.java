package com.hector.engine.graphics.layers;

import com.hector.engine.graphics.ComputeShader;
import com.hector.engine.graphics.FrameBuffer;
import com.hector.engine.graphics.Model;
import com.hector.engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL42C.*;

public class RaytraceLayer extends AbstractRenderLayer {

    private final float[] vertices = new float[]{
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,

            1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f,
    };

    private boolean resetFramebuffer = true;

    private FrameBuffer frameBuffer;

    private Model quadMesh;

    private ShaderProgram quadShader;

    private int framebufferImageBinding;

    private Vector2f workGroupSize;

    private Matrix4f projMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f invViewProjMatrix = new Matrix4f();
    private Vector3f tmpVector = new Vector3f(0, 0, 0);
    private Vector3f cameraPosition = new Vector3f(0, 0, 0);
    private Vector3f cameraLookAt = new Vector3f(0.0f, 0.5f, 0.0f);
    private Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

    private ComputeShader computeShader;

    @Override
    public void init() {
        frameBuffer = new FrameBuffer(1920, 1080);
        quadMesh = new Model(vertices);

        computeShader = new ComputeShader("raytracing");
        initComputeProgram();
        quadShader = new ShaderProgram("quad");
        initQuadProgram();
    }

    private void initComputeProgram() {
        computeShader.bind();

        workGroupSize = computeShader.getWorkGroupSize();

        /* Query the "image binding point" of the image uniform */
        IntBuffer params = BufferUtils.createIntBuffer(1);
        int loc = glGetUniformLocation(computeShader.getProgramID(), "framebufferImage");
        glGetUniformiv(computeShader.getProgramID(), loc, params);
        framebufferImageBinding = params.get(0);

        computeShader.unbind();
    }

    private void initQuadProgram() {
        quadShader.bind();
        quadShader.setInt("tex", 0);
        quadShader.unbind();
    }

    @Override
    public void update(float delta) {
        computeShader.bind();

        cameraPosition.set(0, 2.0f, 3.0f);
        viewMatrix.setLookAt(cameraPosition, cameraLookAt, cameraUp);

        /*
         * If the framebuffer size has changed, because the GLFW window was resized, we
         * need to reset the camera's projection matrix and recreate our framebuffer
         * texture.
         */
        if (resetFramebuffer) {
            projMatrix.setPerspective((float) Math.toRadians(60.0f), (float) 1920 / (float) 1080, 1f, 2f);
            resetFramebuffer = false;
        }
        /*
         * Invert the view-projection matrix to unproject NDC-space coordinates to
         * world-space vectors. See next few statements.
         */
        projMatrix.invertPerspectiveView(viewMatrix, invViewProjMatrix);

        /*
         * Compute and set the view frustum corner rays in the shader for the shader to
         * compute the direction from the eye through a framebuffer's pixel center for a
         * given shader work item.
         */
        computeShader.uniform3f("eye", cameraPosition);
        invViewProjMatrix.transformProject(tmpVector.set(-1, -1, 0)).sub(cameraPosition);
        computeShader.uniform3f("ray00", tmpVector);
        invViewProjMatrix.transformProject(tmpVector.set(-1, 1, 0)).sub(cameraPosition);
        computeShader.uniform3f("ray01", tmpVector);
        invViewProjMatrix.transformProject(tmpVector.set(1, -1, 0)).sub(cameraPosition);
        computeShader.uniform3f("ray10", tmpVector);
        invViewProjMatrix.transformProject(tmpVector.set(1, 1, 0)).sub(cameraPosition);
        computeShader.uniform3f("ray11", tmpVector);

        /*
         * Bind level 0 of framebuffer texture as writable image in the shader. This
         * tells OpenGL that any writes to the image defined in our shader is going to
         * go to the first level of the texture 'tex'.
         */
        glBindImageTexture(framebufferImageBinding, frameBuffer.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);

        /*
         * Compute appropriate global work size dimensions.
         */
        int numGroupsX = (int) Math.ceil((double) 1920 / workGroupSize.x);
        int numGroupsY = (int) Math.ceil((double) 1080 / workGroupSize.y);

        /* Invoke the compute shader. */
        computeShader.dispatch(numGroupsX, numGroupsY, 1);

        /*
         * Synchronize all writes to the framebuffer image before we let OpenGL source
         * texels from it afterwards when rendering the final image with the full-screen
         * quad.
         */
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

        /* Reset bindings. */
        glBindImageTexture(framebufferImageBinding, 0, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
        computeShader.unbind();
    }

    @Override
    public void render() {
        /*
         * Draw the rendered image on the screen using a textured full-screen quad.
         */
        quadShader.bind();

        quadMesh.bind();
        quadMesh.enableVertexAttribArrays();

        frameBuffer.bind();

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quadMesh.getVertexCount());

        frameBuffer.unbind();

        quadMesh.disableVertexAttribArrays();
        quadMesh.unbind();

        quadShader.unbind();
    }

    @Override
    public void onEvent(LayerInputEvent event) {

    }

    @Override
    public void destroy() {
        frameBuffer.destroy();
        quadMesh.destroy();
        quadShader.destroy();
        computeShader.destroy();
    }
}
