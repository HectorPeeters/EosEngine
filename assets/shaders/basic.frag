#version 330 core

in vec3 outPosition;

out vec4 outColor;

void main() {
    outColor = vec4(outPosition + vec3(0.5, 0.5, 0), 1.0);
}