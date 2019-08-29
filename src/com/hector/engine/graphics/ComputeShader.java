package com.hector.engine.graphics;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.glGetProgramiv;
import static org.lwjgl.opengl.GL43C.GL_COMPUTE_WORK_GROUP_SIZE;

public class ComputeShader {

    private String name;

    private int programId;
    private int shaderId;

    private Map<String, Integer> uniforms = new HashMap<>();

    public ComputeShader(String name) {
        this.name = name;
        String path = "shaders/" + name + ".comp";

        this.programId = createProgram();

        this.shaderId = compileShader(getShaderSource(path));

        linkProgram();

        fetchUniforms();
    }

    private int createProgram() {
        int program = GL20.glCreateProgram();

        if (program == 0)
            Logger.err("Graphics", "Failed to create new ComputeShader");

        return program;
    }

    private void fetchUniforms() {
        uniforms.clear();

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

    private String getShaderSource(String path) {
        TextResource sourceResource = ResourceManager.<TextResource>getResource(path);

        return sourceResource.getResource();
    }

    private int compileShader(String source) {
        int shader = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);
        if (shader == 0)
            Logger.err("Graphics", "Failed to create ComputeProgram: " + name);

        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        int compileStatus = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        int infoLogLength = GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH);

        String err = GL20.glGetShaderInfoLog(shader, infoLogLength);

        if (err.length() != 0)
            Logger.err("Graphics", "compute compile log:\n" + err + "\n");

        if (compileStatus == GL11.GL_FALSE)
            Logger.err("Graphics", "Failed to link compute: " + name);

        Logger.debug("Graphics", "Compiled compute: " + name);

        return shader;
    }

    private void linkProgram() {
        GL20.glAttachShader(programId, shaderId);

        GL20.glLinkProgram(programId);

        int comp = GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
        int len = GL20.glGetProgrami(programId, GL20.GL_INFO_LOG_LENGTH);
        String err = GL20.glGetProgramInfoLog(programId, len);

        if (err.length() != 0)
            Logger.err("Graphics", "Link error in program " + name + "\n" + err);

        if (comp == GL11.GL_FALSE)
            Logger.err("Graphics", "Failed to link program: " + name);
    }


    public void dispatch(int groups_x, int groups_y, int groups_z) {
        bind();
        GL43.glDispatchCompute(groups_x, groups_y, groups_z);
        unbind();
    }

    public void uniform1i(String name, int value) {
        GL43.glUniform1i(GL43.glGetUniformLocation(programId, name), value);
    }

    public void uniform3f(String name, Vector3f value) {
        int location = getUniformLocation(name);

        GL43.glUniform3f(location, value.x, value.y, value.z);
    }

    public ComputeShader bind() {
        GL20.glUseProgram(programId);

        return this;
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void destroy() {
        if (programId != 0) {
            if (shaderId != 0) {
                GL20.glDetachShader(programId, shaderId);
                GL20.glDeleteShader(shaderId);
                shaderId = 0;
            }

            GL20.glDeleteProgram(programId);
            programId = 0;
        }

        Logger.info("Graphics", "Destroyed compute shader: " + name);
    }

    public String getName() {
        return name;
    }

    public int getProgramID() {
        return programId;
    }

    public Vector2f getWorkGroupSize() {
        IntBuffer workGroupSize = BufferUtils.createIntBuffer(3);
        glGetProgramiv(programId, GL_COMPUTE_WORK_GROUP_SIZE, workGroupSize);
        return new Vector2f(workGroupSize.get(0), workGroupSize.get(1));
    }
}
