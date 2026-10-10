/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Rect {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public Rect() {
        this(0, 0, 0, 0);
    }

    public Rect(Rect rect) {
        this(rect.left, rect.top, rect.right, rect.bottom);
    }

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int width() {
        return this.right - this.left;
    }

    public int height() {
        return this.bottom - this.top;
    }

    public int square() {
        return this.width() * this.height();
    }

    public Rect set(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        return this;
    }

    public Rect set(Rect rect) {
        return this.set(rect.left, rect.top, rect.right, rect.bottom);
    }

    public Rect setPos(int x, int y) {
        return this.set(x, y, x + (this.right - this.left), y + (this.bottom - this.top));
    }

    public Rect shift(int x, int y) {
        return this.set(this.left + x, this.top + y, this.right + x, this.bottom + y);
    }

    public Rect resize(int w, int h) {
        return this.set(this.left, this.top, this.left + w, this.top + h);
    }

    public boolean isEmpty() {
        return this.right <= this.left || this.bottom <= this.top;
    }

    public Rect setEmpty() {
        this.bottom = 0;
        this.top = 0;
        this.right = 0;
        this.left = 0;
        return this;
    }

    public Rect intersect(Rect other) {
        Rect result = new Rect();
        result.left = Math.max(this.left, other.left);
        result.right = Math.min(this.right, other.right);
        result.top = Math.max(this.top, other.top);
        result.bottom = Math.min(this.bottom, other.bottom);
        return result;
    }

    public Rect union(Rect other) {
        Rect result = new Rect();
        result.left = Math.min(this.left, other.left);
        result.right = Math.max(this.right, other.right);
        result.top = Math.min(this.top, other.top);
        result.bottom = Math.max(this.bottom, other.bottom);
        return result;
    }

    public Rect union(int x, int y) {
        if (this.isEmpty()) {
            return this.set(x, y, x + 1, y + 1);
        }
        if (x < this.left) {
            this.left = x;
        } else if (x >= this.right) {
            this.right = x + 1;
        }
        if (y < this.top) {
            this.top = y;
        } else if (y >= this.bottom) {
            this.bottom = y + 1;
        }
        return this;
    }

    public Rect union(Point p) {
        return this.union(p.x, p.y);
    }

    public boolean inside(Point p) {
        return p.x >= this.left && p.x < this.right && p.y >= this.top && p.y < this.bottom;
    }

    public Point center() {
        return new Point((this.left + this.right) / 2 + ((this.right - this.left) % 2 == 0 ? Random.Int(2) : 0), (this.top + this.bottom) / 2 + ((this.bottom - this.top) % 2 == 0 ? Random.Int(2) : 0));
    }

    public Rect shrink(int d) {
        return new Rect(this.left + d, this.top + d, this.right - d, this.bottom - d);
    }

    public Rect shrink() {
        return this.shrink(1);
    }

    public Rect scale(int d) {
        return new Rect(this.left * d, this.top * d, this.right * d, this.bottom * d);
    }

    public ArrayList<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = this.left; i <= this.right; ++i) {
            for (int j = this.top; j <= this.bottom; ++j) {
                points.add(new Point(i, j));
            }
        }
        return points;
    }
}

