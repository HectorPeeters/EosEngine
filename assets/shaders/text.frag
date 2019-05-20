#version 330 core

in vec2 outTexCoord;

out vec4 outColor;

uniform sampler2D sampler;

//framesWide, framesHigh, frameIndex, flipped
//uniform vec4 animationData;
uniform int characterIndex;

void main() {
    float widthPerFrame = 1f / animationData.x;
    float heigthPerFrame = 1f / animationData.y;

    int xIndex = int(animationData.z) % int(animationData.x);
    int yIndex = int(animationData.z) / int(animationData.x);

    float xCoord = outTexCoord.x;

    if (animationData.w == 1) {
        xCoord = 1 - outTexCoord.x;
    }

    float x = (xIndex + xCoord) * widthPerFrame;
    float y = (yIndex + outTexCoord.y) * heigthPerFrame;

    outColor = texture(sampler, vec2(x, y));
}