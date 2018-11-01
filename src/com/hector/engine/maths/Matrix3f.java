package com.hector.engine.maths;

public class Matrix3f {

    public float[] m = new float[9];

    public Matrix3f() {
    }

    public Matrix3f(float[] m) {
        this.m = m;
    }

    public Matrix3f initIdentity() {
        m[0] = 1;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;
        m[4] = 1;
        m[5] = 0;
        m[6] = 0;
        m[7] = 0;
        m[8] = 1;

        return this;
    }

    public Matrix3f initTranslation(Vector2f transform) {
        initIdentity();

        m[2] = transform.x;
        m[5] = transform.y;

        return this;
    }

    public Matrix3f initScale(Vector2f scale) {
        initIdentity();

        m[0] = scale.x;
        m[4] = scale.y;

        return this;
    }

    public Matrix3f initRotation(float angle) {
        initIdentity();

        float radians = (float) Math.toRadians(angle);

        m[0] = (float) Math.cos(radians);
        m[1] = (float) Math.sin(radians);
        m[3] = (float) -Math.sin(radians);
        m[4] = (float) Math.cos(radians);

        return this;
    }

    public Matrix3f initTransformation(Vector2f pos, Vector2f scale, float rotation) {
        initIdentity();

        float radiansAngle = (float) Math.toRadians(rotation);

        m[0] = (float) (Math.cos(radiansAngle) * scale.x);
        m[1] = (float) (-Math.sin(radiansAngle) * scale.x);
        m[2] = pos.x;

        m[3] = (float) (Math.sin(radiansAngle) * scale.y);
        m[4] = (float) (Math.cos(radiansAngle) * scale.y);
        m[5] = pos.y;

        return this;
    }

    public Matrix3f initOrtho(float left, float right, float top, float bottom, float near, float far) {
        initIdentity();

        m[0] = 2 / (right - left);
        m[4] = 2 / (top - bottom);
        m[8] = 2 / (far - near);

        return this;
    }

    public Matrix3f multiply(Matrix3f other) {
        float[] result = new float[9];

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {

                float sum = 0.0f;

                for (int e = 0; e < 3; e++)
                    sum += m[e + row * 3] * other.m[col + e * 3];

                result[col + row * 4] = sum;
            }
        }

        return new Matrix3f(result);
    }
}
