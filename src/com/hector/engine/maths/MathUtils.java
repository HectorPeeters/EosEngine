package com.hector.engine.maths;

public class MathUtils {

    private static long xorshift_last;
    private static long xorshift_inc;

    static {
        long seed = System.currentTimeMillis();
        xorshift_last = seed | 1;
        xorshift_inc = seed;
    }

    public static int xorShiftRandom(int max) {
        xorshift_last ^= (xorshift_last << 21);
        xorshift_last ^= (xorshift_last >>> 35);
        xorshift_last ^= (xorshift_last << 4);

        xorshift_inc += 123456789123456789L;
        int out = (int) ((xorshift_last + xorshift_inc) % max);

        return (out < 0) ? -out : out;
    }

}
