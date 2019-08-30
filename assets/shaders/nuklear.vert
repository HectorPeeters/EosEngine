#version 330 core

uniform mat4 projectionMatrix;

in vec2 position;
in vec2 texCoord;
in vec4 color;

out vec2 frag_TexCoord;
out vec4 frag_Color;

void main() {
    frag_TexCoord = texCoord;
    frag_Color = color;
    gl_Position = projectionMatrix * vec4(position.xy, 0, 1);
}