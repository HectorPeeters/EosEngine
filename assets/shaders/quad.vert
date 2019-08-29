#version 330

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
/* The position of the vertex as two-dimensional vector */
in vec3 vertex;

/* Write interpolated texture coordinate to fragment shader */
out vec2 texcoord;

void main(void) {
    gl_Position = vec4(vertex, 1.0);
    /*
     * Compute texture coordinate by simply
     * interval-mapping from [-1..+1] to [0..1]
     */
    texcoord = vertex.xy * 0.5 + vec2(0.5, 0.5);
}
