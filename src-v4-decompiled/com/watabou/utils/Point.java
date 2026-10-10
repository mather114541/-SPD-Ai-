/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

public class Point {
    public int x;
    public int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Point set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Point set(Point p) {
        this.x = p.x;
        this.y = p.y;
        return this;
    }

    public Point clone() {
        return new Point(this);
    }

    public Point scale(float f) {
        this.x = (int)((float)this.x * f);
        this.y = (int)((float)this.y * f);
        return this;
    }

    public Point offset(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Point offset(Point d) {
        this.x += d.x;
        this.y += d.y;
        return this;
    }

    public boolean isZero() {
        return this.x == 0 && this.y == 0;
    }

    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public static float distance(Point a, Point b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point p = (Point)obj;
            return p.x == this.x && p.y == this.y;
        }
        return false;
    }
}

