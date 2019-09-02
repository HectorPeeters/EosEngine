#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 transformationMatrix;
uniform mat4 orthographicMatrix;
uniform mat4 cameraMatrix;

void main() {
    vec4 transformedPos = orthographicMatrix * cameraMatrix * transformationMatrix * vec4(position, 1.0);

    gl_Position = transformedPos;

    outTexCoord = texCoord;
}