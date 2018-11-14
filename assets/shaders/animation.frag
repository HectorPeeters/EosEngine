#version 330 core

in vec2 outTexCoord;

out vec4 outColor;

uniform sampler2D sampler;

uniform int framesWide;
uniform int framesHigh;
uniform int frameIndex;
uniform int flipped;

void main() {
    float widthPerFrame = 1f / framesWide;
    float heigthPerFrame = 1f / framesHigh;

    int xIndex = frameIndex % framesWide;
    int yIndex = frameIndex / framesWide;

    float xCoord = outTexCoord.x;

    if (flipped == 1) {
        xCoord = 1 - outTexCoord.x;
    }

    float x = (xIndex + xCoord) * widthPerFrame;
    float y = (yIndex + outTexCoord.y) * heigthPerFrame;

    outColor = texture(sampler, vec2(x, y));
}