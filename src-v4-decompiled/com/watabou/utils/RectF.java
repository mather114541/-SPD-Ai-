/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class RectF {
    public float left;
    public float top;
    public float right;
    public float bottom;

    public RectF() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public RectF(RectF rect) {
        this(rect.left, rect.top, rect.right, rect.bottom);
    }

    public RectF(Rect rect) {
        this(rect.left, rect.top, rect.right, rect.bottom);
    }

    public RectF(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public float width() {
        return this.right - this.left;
    }

    public float height() {
        return this.bottom - this.top;
    }

    public float square() {
        return this.width() * this.height();
    }

    public RectF set(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        return this;
    }

    public RectF set(Rect rect) {
        return this.set(rect.left, rect.top, rect.right, rect.bottom);
    }

    public RectF setPos(float x, float y) {
        return this.set(x, y, x + (this.right - this.left), y + (this.bottom - this.top));
    }

    public RectF shift(float x, float y) {
        return this.set(this.left + x, this.top + y, this.right + x, this.bottom + y);
    }

    public RectF resize(float w, float h) {
        return this.set(this.left, this.top, this.left + w, this.top + h);
    }

    public boolean isEmpty() {
        return this.right <= this.left || this.bottom <= this.top;
    }

    public RectF setEmpty() {
        this.bottom = 0.0f;
        this.top = 0.0f;
        this.right = 0.0f;
        this.left = 0.0f;
        return this;
    }

    public RectF intersect(RectF other) {
        RectF result = new RectF();
        result.left = Math.max(this.left, other.left);
        result.right = Math.min(this.right, other.right);
        result.top = Math.max(this.top, other.top);
        result.bottom = Math.min(this.bottom, other.bottom);
        return result;
    }

    public RectF union(RectF other) {
        RectF result = new RectF();
        result.left = Math.min(this.left, other.left);
        result.right = Math.max(this.right, other.right);
        result.top = Math.min(this.top, other.top);
        result.bottom = Math.max(this.bottom, other.bottom);
        return result;
    }

    public RectF union(float x, float y) {
        if (this.isEmpty()) {
            return this.set(x, y, x + 1.0f, y + 1.0f);
        }
        if (x < this.left) {
            this.left = x;
        } else if (x >= this.right) {
            this.right = x + 1.0f;
        }
        if (y < this.top) {
            this.top = y;
        } else if (y >= this.bottom) {
            this.bottom = y + 1.0f;
        }
        return this;
    }

    public RectF union(Point p) {
        return this.union(p.x, p.y);
    }

    public boolean inside(Point p) {
        return (float)p.x >= this.left && (float)p.x < this.right && (float)p.y >= this.top && (float)p.y < this.bottom;
    }

    public RectF shrink(float d) {
        return new RectF(this.left + d, this.top + d, this.right - d, this.bottom - d);
    }

    public RectF shrink() {
        return this.shrink(1.0f);
    }

    public RectF scale(float d) {
        return new RectF(this.left * d, this.top * d, this.right * d, this.bottom * d);
    }
}

