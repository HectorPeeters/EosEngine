#version 330 core

in vec2 outTexCoord;
in vec3 outNormal;

out vec4 outColor;

uniform sampler2D sampler;

void main() {
    outColor = vec4(outNormal, 1.0);//texture(sampler, vec2(x, y));
}