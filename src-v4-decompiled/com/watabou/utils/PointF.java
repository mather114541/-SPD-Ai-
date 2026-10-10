/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.utils.Point;

public class PointF {
    public static final float PI = 3.1415925f;
    public static final float PI2 = 6.283185f;
    public static final float G2R = (float)Math.PI / 180;
    public float x;
    public float y;

    public PointF() {
    }

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    public PointF(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public PointF clone() {
        return new PointF(this);
    }

    public PointF scale(float f) {
        this.x *= f;
        this.y *= f;
        return this;
    }

    public PointF invScale(float f) {
        this.x /= f;
        this.y /= f;
        return this;
    }

    public PointF set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public PointF set(PointF p) {
        this.x = p.x;
        this.y = p.y;
        return this;
    }

    public PointF set(float v) {
        this.x = v;
        this.y = v;
        return this;
    }

    public PointF polar(float a, float l) {
        this.x = l * (float)Math.cos(a);
        this.y = l * (float)Math.sin(a);
        return this;
    }

    public PointF offset(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public PointF offset(PointF p) {
        this.x += p.x;
        this.y += p.y;
        return this;
    }

    public PointF negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public PointF normalize() {
        float l = this.length();
        this.x /= l;
        this.y /= l;
        return this;
    }

    public Point floor() {
        return new Point((int)this.x, (int)this.y);
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f;
    }

    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public static PointF sum(PointF a, PointF b) {
        return new PointF(a.x + b.x, a.y + b.y);
    }

    public static PointF diff(PointF a, PointF b) {
        return new PointF(a.x - b.x, a.y - b.y);
    }

    public static PointF inter(PointF a, PointF b, float d) {
        return new PointF(a.x + (b.x - a.x) * d, a.y + (b.y - a.y) * d);
    }

    public static float distance(PointF a, PointF b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static float angle(float x, float y) {
        return (float)Math.atan2(y, x);
    }

    public static float angle(PointF start, PointF end) {
        return (float)Math.atan2(end.y - start.y, end.x - start.x);
    }

    public static float angle(Point start, Point end) {
        return (float)Math.atan2(end.y - start.y, end.x - start.x);
    }

    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        }
        return o instanceof PointF && ((PointF)o).x == this.x && ((PointF)o).y == this.y;
    }
}

