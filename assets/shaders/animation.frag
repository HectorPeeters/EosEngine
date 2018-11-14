#version 330 core

in vec2 outTexCoord;

out vec4 outColor;

uniform sampler2D sampler;

uniform int framesWide;
uniform int framesHigh;
uniform int frameIndex;

void main() {
    float widthPerFrame = 1f / framesWide;
    float heigthPerFrame = 1f / framesHigh;

    int xIndex = frameIndex % framesWide;
    int yIndex = frameIndex / framesWide;

    float x = (xIndex + outTexCoord.x) * widthPerFrame;
    float y = (yIndex + outTexCoord.y) * heigthPerFrame;

    outColor = texture(sampler, vec2(x, y));
}