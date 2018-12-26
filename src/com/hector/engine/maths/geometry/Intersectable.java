package com.hector.engine.maths.geometry;

import com.hector.engine.maths.Vector2f;

public interface Intersectable {

    IntersectInfo intersects(Ray ray);

    class IntersectInfo {
        public final boolean intersects;
        public final Vector2f position;
        public final Vector2f normal;
        public final float distance;

        public IntersectInfo(boolean intersects, Vector2f position, Vector2f normal, float distance) {
            this.intersects = intersects;
            this.position = position;
            this.normal = normal;
            this.distance = distance;
        }
    }

}
