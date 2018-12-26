#version 330 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat3 transformationMatrix;
uniform mat3 orthographicMatrix;
uniform mat3 cameraMatrix;

void main() {
    vec3 transformedPos = orthographicMatrix * cameraMatrix * transformationMatrix * vec3(position, 1.0);

    gl_Position = vec4(transformedPos, 1.0);

    outTexCoord = texCoord;
}