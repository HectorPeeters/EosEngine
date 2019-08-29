package com.hector.engine.graphics;

public class ErrorCodes {

    private static final String[] errors = {
            "GL_INVALID_ENUM",
            "GL_INVALID_VALUE",
            "GL_INVALID_OPERATION",
            "GL_STACK_OVERFLOW",
            "GL_STACK_UNDERFLOW",
            "GL_OUT_OF_MEMORY",
            "GL_INVALID_FRAMEBUFFER_OPERATION"
    };

    public static String getErrorString(int error) {
        return errors[error - 0x0500];
    }

}
