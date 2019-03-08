package com.hector.engine.maths.geometry;

import com.hector.engine.maths.Vector2f;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Circle implements Intersectable {

    public Vector2f position;
    public float radius;

    @Override
    public IntersectInfo intersects(Ray ray) {
        throw new NotImplementedException();
//        return new IntersectInfo(false, null, null, 0);
    }

}
