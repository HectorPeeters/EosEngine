#version 330 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 texCoord;

out vec2 outTexCoord;

void main() {
    gl_Position = vec4(position * 2, 0.0, 1.0);

    //TODO: maybe fix 1-texCoord.y?
    outTexCoord = vec2(texCoord.x, 1 - texCoord.y);
}