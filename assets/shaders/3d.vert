#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;

out vec2 outTexCoord;
out vec3 outNormal;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 cameraMatrix;

void main() {
    vec4 transformedPos = projectionMatrix * cameraMatrix * transformationMatrix * vec4(position, 1.0);

    gl_Position = transformedPos;

    outTexCoord = texCoord;
    outNormal = normal;
}