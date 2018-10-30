package com.hector.engine.maths;

public class Matrix3f {

    public float[] m = new float[9];

    public Matrix3f(float[] m) {
        this.m = m;
    }

    public void initIdentity() {
        m[0] = 1;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;
        m[4] = 1;
        m[5] = 0;
        m[6] = 0;
        m[7] = 0;
        m[8] = 1;
    }

    public void initTranslation(Vector2f transform) {
        initIdentity();

        m[2] = transform.x;
        m[5] = transform.y;
    }

    public void initScale(Vector2f scale) {
        initIdentity();

        m[0] = scale.x;
        m[4] = scale.y;
    }

    public void initRotation(float angle) {
        initIdentity();

        float radians = (float) Math.toRadians(angle);

        m[0] = (float) Math.cos(radians);
        m[1] = (float) Math.sin(radians);
        m[3] = (float) -Math.sin(radians);
        m[4] = (float) Math.cos(radians);
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
