#version 330 core

in vec2 position;

out vec3 outPosition;

uniform mat3 transformationMatrix;
uniform mat3 orthographicMatrix;

void main() {
    vec3 transformedPos = orthographicMatrix * transformationMatrix * vec3(position, 1.0);

    gl_Position = vec4(transformedPos, 1.0);

    outPosition = vec3(position, 0);
}