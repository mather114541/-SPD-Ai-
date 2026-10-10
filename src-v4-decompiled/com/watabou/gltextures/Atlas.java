/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.gltextures;

import com.watabou.gltextures.SmartTexture;
import com.watabou.utils.RectF;
import java.util.HashMap;

public class Atlas {
    public SmartTexture tx;
    protected HashMap<Object, RectF> namedFrames;
    protected float uvLeft;
    protected float uvTop;
    protected float uvWidth;
    protected float uvHeight;
    protected int cols;

    public Atlas(SmartTexture tx) {
        this.tx = tx;
        tx.atlas = this;
        this.namedFrames = new HashMap();
    }

    public void add(Object key, int left, int top, int right, int bottom) {
        this.add(key, Atlas.uvRect(this.tx, left, top, right, bottom));
    }

    public void add(Object key, RectF rect) {
        this.namedFrames.put(key, rect);
    }

    public void grid(int width) {
        this.grid(width, this.tx.height);
    }

    public void grid(int width, int height) {
        this.grid(0, 0, width, height, this.tx.width / width);
    }

    public void grid(int left, int top, int width, int height, int cols) {
        this.uvLeft = (float)left / (float)this.tx.width;
        this.uvTop = (float)top / (float)this.tx.height;
        this.uvWidth = (float)width / (float)this.tx.width;
        this.uvHeight = (float)height / (float)this.tx.height;
        this.cols = cols;
    }

    public RectF get(int index) {
        float x = index % this.cols;
        float y = index / this.cols;
        float l = this.uvLeft + x * this.uvWidth;
        float t = this.uvTop + y * this.uvHeight;
        return new RectF(l, t, l + this.uvWidth, t + this.uvHeight);
    }

    public RectF get(Object key) {
        return this.namedFrames.get(key);
    }

    public float width(RectF rect) {
        return rect.width() * (float)this.tx.width;
    }

    public float height(RectF rect) {
        return rect.height() * (float)this.tx.height;
    }

    public static RectF uvRect(SmartTexture tx, int left, int top, int right, int bottom) {
        return new RectF((float)left / (float)tx.width, (float)top / (float)tx.height, (float)right / (float)tx.width, (float)bottom / (float)tx.height);
    }
}

