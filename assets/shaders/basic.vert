#version 330 core

in vec3 position;

out vec3 outPosition;

uniform mat3 transformationMatrix;

void main() {
    vec3 transformedPos = transformationMatrix * position;

    gl_Position = vec4(transformedPos, 1.0);

    outPosition = position;
}