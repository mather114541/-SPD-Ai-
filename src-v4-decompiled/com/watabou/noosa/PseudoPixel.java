/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;

public class PseudoPixel
extends Image {
    public PseudoPixel() {
        super(TextureCache.createSolid(-1));
    }

    public PseudoPixel(float x, float y, int color) {
        this();
        this.x = x;
        this.y = y;
        this.color(color);
    }

    public void size(float w, float h) {
        this.scale.set(w, h);
    }

    public void size(float value) {
        this.scale.set(value);
    }
}

