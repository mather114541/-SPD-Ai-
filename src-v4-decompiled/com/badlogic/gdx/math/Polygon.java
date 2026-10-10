/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.math;

import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public class Polygon
implements Shape2D {
    private float[] localVertices;
    private float[] worldVertices;
    private float x;
    private float y;
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private boolean dirty = true;
    private Rectangle bounds;

    public Polygon() {
        this.localVertices = new float[0];
    }

    public Polygon(float[] vertices) {
        if (vertices.length < 6) {
            throw new IllegalArgumentException("polygons must contain at least 3 points.");
        }
        this.localVertices = vertices;
    }

    public float[] getVertices() {
        return this.localVertices;
    }

    public float[] getTransformedVertices() {
        if (!this.dirty) {
            return this.worldVertices;
        }
        this.dirty = false;
        float[] localVertices = this.localVertices;
        if (this.worldVertices == null || this.worldVertices.length != localVertices.length) {
            this.worldVertices = new float[localVertices.length];
        }
        float[] worldVertices = this.worldVertices;
        float positionX = this.x;
        float positionY = this.y;
        float originX = this.originX;
        float originY = this.originY;
        float scaleX = this.scaleX;
        float scaleY = this.scaleY;
        boolean scale = scaleX != 1.0f || scaleY != 1.0f;
        float rotation = this.rotation;
        float cos = MathUtils.cosDeg(rotation);
        float sin = MathUtils.sinDeg(rotation);
        int n = localVertices.length;
        for (int i = 0; i < n; i += 2) {
            float x = localVertices[i] - originX;
            float y = localVertices[i + 1] - originY;
            if (scale) {
                x *= scaleX;
                y *= scaleY;
            }
            if (rotation != 0.0f) {
                float oldX = x;
                x = cos * x - sin * y;
                y = sin * oldX + cos * y;
            }
            worldVertices[i] = positionX + x + originX;
            worldVertices[i + 1] = positionY + y + originY;
        }
        return worldVertices;
    }

    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        this.dirty = true;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.dirty = true;
    }

    public void setVertices(float[] vertices) {
        if (vertices.length < 6) {
            throw new IllegalArgumentException("polygons must contain at least 3 points.");
        }
        this.localVertices = vertices;
        this.dirty = true;
    }

    public void setVertex(int vertexNum, float x, float y) {
        if (vertexNum < 0 || vertexNum > this.localVertices.length / 2 - 1) {
            throw new IllegalArgumentException("the vertex " + vertexNum + " doesn't exist");
        }
        this.localVertices[2 * vertexNum] = x;
        this.localVertices[2 * vertexNum + 1] = y;
        this.dirty = true;
    }

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
        this.dirty = true;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
        this.dirty = true;
    }

    public void rotate(float degrees) {
        this.rotation += degrees;
        this.dirty = true;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.dirty = true;
    }

    public void scale(float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        this.dirty = true;
    }

    public void dirty() {
        this.dirty = true;
    }

    public float area() {
        float[] vertices = this.getTransformedVertices();
        return GeometryUtils.polygonArea(vertices, 0, vertices.length);
    }

    public int getVertexCount() {
        return this.localVertices.length / 2;
    }

    public Vector2 getVertex(int vertexNum, Vector2 pos) {
        if (vertexNum < 0 || vertexNum > this.getVertexCount()) {
            throw new IllegalArgumentException("the vertex " + vertexNum + " doesn't exist");
        }
        float[] vertices = this.getTransformedVertices();
        return pos.set(vertices[2 * vertexNum], vertices[2 * vertexNum + 1]);
    }

    public Vector2 getCentroid(Vector2 centroid) {
        float[] vertices = this.getTransformedVertices();
        return GeometryUtils.polygonCentroid(vertices, 0, vertices.length, centroid);
    }

    public Rectangle getBoundingRectangle() {
        float[] vertices = this.getTransformedVertices();
        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];
        int numFloats = vertices.length;
        for (int i = 2; i < numFloats; i += 2) {
            minX = minX > vertices[i] ? vertices[i] : minX;
            minY = minY > vertices[i + 1] ? vertices[i + 1] : minY;
            maxX = maxX < vertices[i] ? vertices[i] : maxX;
            maxY = maxY < vertices[i + 1] ? vertices[i + 1] : maxY;
        }
        if (this.bounds == null) {
            this.bounds = new Rectangle();
        }
        this.bounds.x = minX;
        this.bounds.y = minY;
        this.bounds.width = maxX - minX;
        this.bounds.height = maxY - minY;
        return this.bounds;
    }

    @Override
    public boolean contains(float x, float y) {
        float[] vertices = this.getTransformedVertices();
        int numFloats = vertices.length;
        int intersects = 0;
        for (int i = 0; i < numFloats; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x2 = vertices[(i + 2) % numFloats];
            float y2 = vertices[(i + 3) % numFloats];
            if (!(y1 <= y && y < y2) && (!(y2 <= y) || !(y < y1)) || !(x < (x2 - x1) / (y2 - y1) * (y - y1) + x1)) continue;
            ++intersects;
        }
        return intersects & true;
    }

    @Override
    public boolean contains(Vector2 point) {
        return this.contains(point.x, point.y);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getOriginX() {
        return this.originX;
    }

    public float getOriginY() {
        return this.originY;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void resetTransformations() {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.originX = 0.0f;
        this.originY = 0.0f;
        this.x = 0.0f;
        this.y = 0.0f;
        this.rotation = 0.0f;
        this.dirty = true;
    }
}

