package com.hector.engine.graphics;

import com.hector.engine.logging.Logger;
import com.hector.engine.maths.Matrix3f;
import com.hector.engine.maths.Vector2f;
import com.hector.engine.maths.Vector4f;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader {

    private static final String INCLUDE_TAG = "#include";

    private String name;

    private int programId;

    private int vertexShader;
    private int fragmentShader;

    private Map<String, Integer> uniforms = new HashMap<>();
    private Map<String, Attrib> attributes = new HashMap<>();

    public Shader(String name) {
        this.name = name;
        String vertexPath = "shaders/" + name + ".vert";
        String fragmentPath = "shaders/" + name + ".frag";

        programId = createProgram();

        vertexShader = compileShader(vertexPath, GL20.GL_VERTEX_SHADER);
        fragmentShader = compileShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        linkProgram();

        postLoad();

        Logger.info("Graphics", "Compiled shader program: " + name);
    }

    protected void postLoad() {}

    private int createProgram() {
        int program = GL20.glCreateProgram();

        if (program == 0)
            Logger.err("Graphics", "Failed to create new Shader Program");

        return program;
    }

    private int compileShader(String path, int type) {
        String source = ResourceManager.<TextResource>getResource(path).getResource();

        int shader = GL20.glCreateShader(type);
        if (shader == 0)
            Logger.err("Graphics", "Failed to create Shader");

        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        int linkStatus = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        int infoLogLength = GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH);

        String typeString = shaderTypeString(type);
        String err = GL20.glGetShaderInfoLog(shader, infoLogLength);

        if (err.length() != 0)
            Logger.err("Graphics", typeString + " compile log:\n" + err + "\n");

        if (linkStatus == GL11.GL_FALSE)
            Logger.err("Graphics", "Failed to compile " + typeString + ": " + path);

        Logger.debug("Graphics", "Compiled " + typeString + ": " + path);

        return shader;
    }

    private void linkProgram() {
        attachShaders();

        uniforms.clear();
        attributes.clear();

        GL20.glLinkProgram(programId);

        int comp = GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
        int len = GL20.glGetProgrami(programId, GL20.GL_INFO_LOG_LENGTH);
        String err = GL20.glGetProgramInfoLog(programId, len);

        if (err.length() != 0)
            Logger.err("Graphics", "Link error in program " + name + "\n" + err);

        if (comp == GL11.GL_FALSE)
            Logger.err("Graphics", "Failed to link program: " + name);

        fetchUniforms();
        fetchAttributes();
    }

    private void attachShaders() {
        GL20.glAttachShader(programId, vertexShader);
        GL20.glAttachShader(programId, fragmentShader);
    }

    private void fetchUniforms() {
        int len = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_UNIFORMS);
        int strLen = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);

        IntBuffer size = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);

        for (int i = 0; i < len; i++) {
            String name = GL20.glGetActiveUniform(programId, i, strLen, size, type);
            int location = GL20.glGetUniformLocation(programId, name);

            uniforms.put(name, location);
        }
    }

    private void fetchAttributes() {
        int len = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTES);
        int strLen = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);

        IntBuffer type = BufferUtils.createIntBuffer(1);
        IntBuffer size = BufferUtils.createIntBuffer(1);

        for (int i = 0; i < len; i++) {
            Attrib attrib = new Attrib();

            attrib.name = GL20.glGetActiveAttrib(programId, i, strLen, size, type);
            attrib.size = size.get(0);
            attrib.type = type.get(0);
            attrib.location = GL20.glGetAttribLocation(programId, attrib.name);

            attributes.put(attrib.name, attrib);
        }

    }

    public int getUniformLocation(String name) {
        int location;
        if (!uniforms.containsKey(name)) {
            location = GL20.glGetUniformLocation(programId, name);
            uniforms.put(name, location);
        } else {
            location = uniforms.get(name);
        }

        return location;
    }

    public Attrib getAttribute(String name) {
        Attrib attrib = attributes.get(name);

        if (attrib == null)
            Logger.err("Graphics", "Attribute '" + name + "' not found in shader: " + name);

        return attrib;
    }

    public int getAttributeLocation(String name) {
        return getAttribute(name).location;
    }

    public String[] getUniformNames() {
        return uniforms.entrySet().toArray(new String[0]);
    }

    public String[] getAttributeNames() {
        return attributes.entrySet().toArray(new String[0]);
    }

    public boolean hasUniform(String name) {
        return uniforms.containsKey(name);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public void setInt(String name, int value) {
        int location = getUniformLocation(name);

        GL20.glUniform1i(location, value);
    }

    public void setFloat(String name, float value) {
        int location = getUniformLocation(name);

        GL20.glUniform1f(location, value);
    }

    public void setVector2f(String name, Vector2f value) {
        int location = getUniformLocation(name);

        GL20.glUniform2f(location, value.x, value.y);
    }

    public void setVector4f(String name, Vector4f value) {
        int location = getUniformLocation(name);

        GL20.glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    public void setMatrix3f(String name, Matrix3f value) {
        int location = getUniformLocation(name);

        GL20.glUniformMatrix3fv(location, true, value.m);
    }


    public Shader bind() {
        GL20.glUseProgram(programId);

        return this;
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void destroy() {
        if (programId != 0) {
            if (vertexShader != 0) {
                GL20.glDetachShader(programId, vertexShader);
                GL20.glDeleteShader(vertexShader);
                vertexShader = 0;
            }

            if (fragmentShader != 0) {
                GL20.glDetachShader(programId, fragmentShader);
                GL20.glDeleteShader(fragmentShader);
                fragmentShader = 0;
            }

            GL20.glDeleteProgram(programId);
            programId = 0;
        }

        Logger.info("Graphics", "Destroyed shader program: " + name);
    }

    private String shaderTypeString(int type) {
        if (type == GL20.GL_FRAGMENT_SHADER)
            return "FRAGMENT_SHADER";
        else if (type == GL20.GL_VERTEX_SHADER)
            return "VERTEX_SHADER";
        else if (type == GL32.GL_GEOMETRY_SHADER)
            return "GEOMETRY_SHADER";
        else
            return "SHADER OF UNKNOWN TYPE";
    }

    public void bindAttributeLocation(String name, int location) {
        GL20.glBindAttribLocation(programId, location, name);
    }

    protected static class Attrib {
        String name = null;
        int type = -1;
        int size = 0;
        int location = -1;
    }

}
