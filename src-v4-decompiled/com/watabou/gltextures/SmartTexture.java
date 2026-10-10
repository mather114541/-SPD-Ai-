/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.gltextures;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.Atlas;
import com.watabou.glwrap.Texture;
import com.watabou.utils.RectF;

public class SmartTexture
extends Texture {
    public int width;
    public int height;
    public int fModeMin;
    public int fModeMax;
    public int wModeH;
    public int wModeV;
    public Pixmap bitmap;
    public Atlas atlas;

    public SmartTexture(Pixmap bitmap) {
        this(bitmap, NEAREST, CLAMP, false);
    }

    public SmartTexture(Pixmap bitmap, int filtering, int wrapping, boolean premultiplied) {
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.fModeMin = this.fModeMax = filtering;
        this.wModeH = this.wModeV = wrapping;
        this.premultiplied = premultiplied;
    }

    @Override
    protected void generate() {
        super.generate();
        this.bitmap(this.bitmap);
        this.filter(this.fModeMin, this.fModeMax);
        this.wrap(this.wModeH, this.wModeV);
    }

    @Override
    public void filter(int minMode, int maxMode) {
        this.fModeMin = minMode;
        this.fModeMax = maxMode;
        if (this.id != -1) {
            super.filter(this.fModeMin, this.fModeMax);
        }
    }

    @Override
    public void wrap(int s, int t) {
        this.wModeH = s;
        this.wModeV = t;
        if (this.id != -1) {
            super.wrap(this.wModeH, this.wModeV);
        }
    }

    @Override
    public void bitmap(Pixmap bitmap) {
        super.bitmap(bitmap);
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    public int getPixel(int x, int y) {
        int color = this.bitmap.getPixel(x, y);
        return color << 24 | color >>> 8;
    }

    public void reload() {
        this.id = -1;
        this.generate();
    }

    @Override
    public void delete() {
        super.delete();
        if (this.bitmap != null) {
            this.bitmap.dispose();
        }
        this.bitmap = null;
    }

    public RectF uvRect(float left, float top, float right, float bottom) {
        return new RectF(left / (float)this.width, top / (float)this.height, right / (float)this.width, bottom / (float)this.height);
    }

    public RectF uvRectBySize(float left, float top, float width, float height) {
        return this.uvRect(left, top, left + width, top + height);
    }
}

