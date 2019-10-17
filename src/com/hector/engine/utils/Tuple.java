package com.hector.engine.utils;

/**
 * This is a very basic Tuple implementation. Pretty self explanatory.
 * @author HectorPeeters
 * @param <X>   The type of the first value of the tuple
 * @param <Y>   The type of the second value of the tuple
 */
public class Tuple<X, Y> {

    /**
     * The first value of the tuple
     */
    private final X x;

    /**
     * The second value of the tuple
     */
    private final Y y;

    /**
     * The constructor of the Tuple class which just sets the values
     *
     * @param x The first tuple value
     * @param y The second tuple value
     */
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for the first value
     *
     * @return The first value
     */
    public X getX() {
        return x;
    }

    /**
     * Getter for the second value
     * @return The second value
     */
    public Y getY() {
        return y;
    }

    /**
     * Alternative getter for the first value
     *
     * @return The first value
     */
    public X getKey() {
        return x;
    }

    /**
     * Alternative getter for the second value
     *
     * @return The second value
     */
    public Y getValue() {
        return y;
    }

    /**
     * Basic toString method which just prints the two values
     * @return The {@link String} representation of the Tuple class
     */
    @Override
    public String toString() {
        return "{ " + x.toString() + " | " + y.toString() + " }";
    }
}
