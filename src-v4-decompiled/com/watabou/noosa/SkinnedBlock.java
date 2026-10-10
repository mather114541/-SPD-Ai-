/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.glwrap.Texture;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class SkinnedBlock
extends Image {
    protected float scaleX;
    protected float scaleY;
    protected float offsetX;
    protected float offsetY;
    public boolean autoAdjust = false;

    public SkinnedBlock(float width, float height, Object tx) {
        super(tx);
        this.texture.wrap(Texture.REPEAT, Texture.REPEAT);
        this.size(width, height);
    }

    @Override
    public void frame(RectF frame) {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        super.frame(new RectF(0.0f, 0.0f, 1.0f, 1.0f));
    }

    @Override
    protected void updateFrame() {
        if (this.autoAdjust) {
            while (this.offsetX > (float)this.texture.width) {
                this.offsetX -= (float)this.texture.width;
            }
            while (this.offsetX < (float)(-this.texture.width)) {
                this.offsetX += (float)this.texture.width;
            }
            while (this.offsetY > (float)this.texture.height) {
                this.offsetY -= (float)this.texture.height;
            }
            while (this.offsetY < (float)(-this.texture.height)) {
                this.offsetY += (float)this.texture.height;
            }
        }
        float tw = 1.0f / (float)this.texture.width;
        float th = 1.0f / (float)this.texture.height;
        float u0 = this.offsetX * tw;
        float v0 = this.offsetY * th;
        float u1 = u0 + this.width * tw / this.scaleX;
        float v1 = v0 + this.height * th / this.scaleY;
        this.vertices[2] = u0;
        this.vertices[3] = v0;
        this.vertices[6] = u1;
        this.vertices[7] = v0;
        this.vertices[10] = u1;
        this.vertices[11] = v1;
        this.vertices[14] = u0;
        this.vertices[15] = v1;
        this.dirty = true;
    }

    public void offsetTo(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
        this.updateFrame();
    }

    public void offset(float x, float y) {
        this.offsetX += x;
        this.offsetY += y;
        this.updateFrame();
    }

    public float offsetX() {
        return this.offsetX;
    }

    public float offsetY() {
        return this.offsetY;
    }

    public void scale(float x, float y) {
        this.scaleX = x;
        this.scaleY = y;
        this.updateFrame();
    }

    public void size(float w, float h) {
        this.width = w;
        this.height = h;
        this.updateFrame();
        this.updateVertices();
    }
}

