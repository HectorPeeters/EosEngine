package com.hector.engine.graphics;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShaderProgram {

    /**
     * The name given to the shaderprogram by the user
     */
    private String name;

    /**
     * The program id of the shaderprogram used for binding and unbinding
     */
    private int programId;

    /**
     * The id of the vertex shader
     */
    private int vertexId;
    /**
     * The id of the fragment shader
     */
    private int fragmentId;

    /**
     * A map of all the uniforms in the vertex and fragment shader
     */
    private Map<String, Integer> uniforms = new HashMap<>();

    /**
     * A map of all the attributes in the vertex and fragment shader
     */
    private Map<String, ShaderAttribute> attributes = new HashMap<>();

    /**
     * Constructor taking in a name and the shader source as a {@link String}
     *
     * @param name           The name of the shaderprogram
     * @param vertexSource   The vertex source
     * @param fragmentSource The fragment source
     */
    public ShaderProgram(String name, String vertexSource, String fragmentSource) {
        this.name = name;

        programId = createProgram();

        vertexId = compileShader(vertexSource, GL20.GL_VERTEX_SHADER);
        fragmentId = compileShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);

        linkProgram();

        Logger.info("Graphics", "Compiled shader program: " + name);
    }

    /**
     * Constructor loading shader from a file (name.vert and name.frag)
     *
     * @param name The name of the shaderprogram
     */
    public ShaderProgram(String name) {
        this.name = name;
        String vertexPath = "shaders/" + name + ".vert";
        String fragmentPath = "shaders/" + name + ".frag";

        programId = createProgram();

        vertexId = compileShader(getShaderSource(vertexPath), GL20.GL_VERTEX_SHADER);
        fragmentId = compileShader(getShaderSource(fragmentPath), GL20.GL_FRAGMENT_SHADER);

        linkProgram();

        Logger.info("Graphics", "Compiled shader program: " + name);
    }

    /**
     * Creates the shaderprogram id and checks for failed creation
     *
     * @return The id of the shaderprogram
     */
    private int createProgram() {
        int program = GL20.glCreateProgram();

        if (program == 0)
            Logger.err("Graphics", "Failed to create new ShaderProgram");

        return program;
    }

    /**
     * Loads the shader sourcecode from the asset bundle
     *
     * @param path The path of the shader source
     * @return The contents of the source file
     */
    private String getShaderSource(String path) {
        TextResource sourceResource = ResourceManager.getResource(path);

        if (sourceResource == null) {
            Logger.err("Graphics", "Failed to get text resource: " + path);
            return null;
        }

        return sourceResource.getResource();
    }

    /**
     * Compiles a shader of a given type and checks for errors
     *
     * @param source The source code of the shader
     * @param type   The type of shader (VERTEX/FRAGMENT)
     * @return The id of the created shader
     */
    private int compileShader(String source, int type) {
        int shader = GL20.glCreateShader(type);
        if (shader == 0)
            Logger.err("Graphics", "Failed to create ShaderProgram");

        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        int compileStatus = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        int infoLogLength = GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH);

        String typeString = shaderTypeString(type);
        String err = GL20.glGetShaderInfoLog(shader, infoLogLength);

        if (err.length() != 0)
            Logger.err("Graphics", typeString + " compile log:\n" + err + "\n");

        if (compileStatus == GL11.GL_FALSE)
            Logger.err("Graphics", "Failed to compile " + typeString + ": " + name);

        Logger.debug("Graphics", "Compiled " + typeString + ": " + name);

        return shader;
    }

    /**
     * Links the shaders to the shaderprogram, checks for errors and loads the uniforms and attributes
     */
    private void linkProgram() {
        attachShaders();

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

    /**
     * Attaches the shader to shaderprogram
     */
    private void attachShaders() {
        GL20.glAttachShader(programId, vertexId);
        GL20.glAttachShader(programId, fragmentId);
    }

    /**
     * Fetches the uniform names and locations and stores them in a HashMap
     */
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

    /**
     * Fetches the attribute names, sizes, types and locations and stores them in a HashMap
     */
    private void fetchAttributes() {
        attributes.clear();

        int len = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTES);
        int strLen = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);

        IntBuffer type = BufferUtils.createIntBuffer(1);
        IntBuffer size = BufferUtils.createIntBuffer(1);

        for (int i = 0; i < len; i++) {

            String name = GL20.glGetActiveAttrib(programId, i, strLen, size, type);
            int typeValue = type.get(0);
            int sizeValue = size.get(0);
            int location = GL20.glGetAttribLocation(programId, name);

            attributes.put(name, new ShaderAttribute(name, typeValue, sizeValue, location));
        }

    }

    /**
     * Gets a uniform location of a uniform variable and creates if location wasn't already loaded
     *
     * @param name The name of the uniform variable
     * @return The location of the uniform variable in the shader
     */
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

    /**
     * Returns the attribute struct with the given name
     *
     * @param name The name of the attribute
     * @return The attribute struct with all the data provided
     */
    public ShaderAttribute getAttribute(String name) {
        ShaderAttribute attrib = attributes.get(name);

        if (attrib == null)
            Logger.err("Graphics", "Attribute '" + name + "' not found in shader: " + name);

        return attrib;
    }

    /**
     * Fetches all the uniform variables of the shader program
     *
     * @return A set containing all the uniform variables
     */
    public Set<Map.Entry<String, Integer>> getUniformNames() {
        return uniforms.entrySet();
    }

    public Set<Map.Entry<String, ShaderAttribute>> getAttributeNames() {
        return attributes.entrySet();
    }

    public boolean hasUniform(String name) {
        return uniforms.containsKey(name);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    /**
     * Set an integer uniform variable
     *
     * @param name  The name of the uniform variable
     * @param value The value of the uniform variable
     */
    public void setInt(String name, int value) {
        int location = getUniformLocation(name);

        GL20.glUniform1i(location, value);
    }

    /**
     * Sets a float uniform variable
     *
     * @param name  The name of the uniform variable
     * @param value The value of the uniform variable
     */
    public void setFloat(String name, float value) {
        int location = getUniformLocation(name);

        GL20.glUniform1f(location, value);
    }

    /**
     * Sets a Vector2f uniform variable
     *
     * @param name  The name of the uniform variable
     * @param value The value of the uniform variable
     */
    public void setVector2f(String name, Vector2f value) {
        int location = getUniformLocation(name);

        GL20.glUniform2f(location, value.x, value.y);
    }

    /**
     * Sets a Vector3f uniform variable
     *
     * @param name  The name of the uniform variable
     * @param value The value of the uniform variable
     */
    public void setVector3f(String name, Vector3f value) {
        int location = getUniformLocation(name);

        GL20.glUniform3f(location, value.x, value.y, value.z);
    }

    /**
     * Sets a Vector4f uniform variable
     *
     * @param name  The name of the uniform variable
     * @param value The value of the uniform variable
     */
    public void setVector4f(String name, Vector4f value) {
        int location = getUniformLocation(name);

        GL20.glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    /**
     * Sets a Matrix3f uniform variable
     *
     * @param name  The name of the uniform variable
     * @param value The value of the uniform variable
     */
    public void setMatrix3f(String name, Matrix3f value) {
        int location = getUniformLocation(name);

        FloatBuffer fb = BufferUtils.createFloatBuffer(9);
        value.get(fb);
        GL20.glUniformMatrix3fv(location, false, fb);
    }

    /**
     * Sets a Matrix4f uniform variable
     *
     * @param name  The name of the uniform variable
     * @param value The value of the uniform variable
     */
    public void setMatrix4f(String name, Matrix4f value) {
        int location = getUniformLocation(name);

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        value.get(fb);
        GL20.glUniformMatrix4fv(location, false, fb);
    }

    public void setMatrix4f(String name, FloatBuffer value) {
        int location = getUniformLocation(name);

        GL20.glUniformMatrix4fv(location, false, value);
    }

    /**
     * Binds the shaderprogram
     *
     * @return The current shaderprogram
     */
    public ShaderProgram bind() {
        GL20.glUseProgram(programId);

        return this;
    }

    /**
     * Unbind the shader program
     */
    public void unbind() {
        GL20.glUseProgram(0);
    }

    /**
     * Destroys the shaders and shaderprogram
     */
    public void destroy() {
        if (programId != 0) {
            if (vertexId != 0) {
                GL20.glDetachShader(programId, vertexId);
                GL20.glDeleteShader(vertexId);
                vertexId = 0;
            }

            if (fragmentId != 0) {
                GL20.glDetachShader(programId, fragmentId);
                GL20.glDeleteShader(fragmentId);
                fragmentId = 0;
            }

            GL20.glDeleteProgram(programId);
            programId = 0;
        }

        Logger.info("Graphics", "Destroyed shader program: " + name);
    }

    /**
     * Converts an integer type to a String for easy error handling
     *
     * @param type The integer shader type
     * @return The name String of the shader
     */
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

    /**
     * Binds an attribute to an attribute location
     *
     * @param name     The name of the attribute
     * @param location The location of the attribute
     */
    public void bindAttributeLocation(String name, int location) {
        GL20.glBindAttribLocation(programId, location, name);
    }

    /**
     * This class represents a shader attribute.
     */
    public static class ShaderAttribute {

        public static final int TYPE_F_VEC2 = 35664;
        public static final int TYPE_F_VEC3 = 35665;
        public static final int TYPE_F_VEC4 = 35666;

        /**
         * The name of the shader attribute.
         */
        private String name;

        /**
         * The type of the shader attribute.
         */
        private int type;

        /**
         * The size of the shader attribute.
         */
        private int size;

        /**
         * The location of the shader attribute.
         */
        private int location;

        ShaderAttribute(String name, int type, int size, int location) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

        public int getSize() {
            return size;
        }

        public int getLocation() {
            return location;
        }
    }

}
