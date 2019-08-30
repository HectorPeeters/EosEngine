#version 330 core

precision mediump float;

uniform sampler2D texture2D;

in vec2 frag_TexCoord;
in vec4 frag_Color;

out vec4 outColor;

void main() {
    outColor = frag_Color * texture(texture2D, frag_TexCoord.st);
}