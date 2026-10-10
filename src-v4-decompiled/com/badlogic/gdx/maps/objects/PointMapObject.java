/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;

public class PointMapObject
extends MapObject {
    private final Vector2 point;

    public PointMapObject() {
        this(0.0f, 0.0f);
    }

    public PointMapObject(float x, float y) {
        this.point = new Vector2(x, y);
    }

    public Vector2 getPoint() {
        return this.point;
    }
}

