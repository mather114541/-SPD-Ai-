/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.ui;

import com.watabou.noosa.Group;

public class Component
extends Group {
    protected float x;
    protected float y;
    protected float width;
    protected float height;

    public Component() {
        this.createChildren();
    }

    public Component setPos(float x, float y) {
        this.x = x;
        this.y = y;
        this.layout();
        return this;
    }

    public Component setSize(float width, float height) {
        this.width = width;
        this.height = height;
        this.layout();
        return this;
    }

    public Component setRect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layout();
        return this;
    }

    public boolean inside(float x, float y) {
        return x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
    }

    public void fill(Component c) {
        this.setRect(c.x, c.y, c.width, c.height);
    }

    public float left() {
        return this.x;
    }

    public float right() {
        return this.x + this.width;
    }

    public float centerX() {
        return this.x + this.width / 2.0f;
    }

    public float top() {
        return this.y;
    }

    public float bottom() {
        return this.y + this.height;
    }

    public float centerY() {
        return this.y + this.height / 2.0f;
    }

    public float width() {
        return this.width;
    }

    public float height() {
        return this.height;
    }

    protected void createChildren() {
    }

    protected void layout() {
    }
}

