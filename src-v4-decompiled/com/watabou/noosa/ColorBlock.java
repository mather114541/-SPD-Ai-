/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.Resizable;

public class ColorBlock
extends Image
implements Resizable {
    public ColorBlock(float width, float height, int color) {
        super(TextureCache.createSolid(color));
        this.scale.set(width, height);
        this.origin.set(0.0f, 0.0f);
    }

    @Override
    public void size(float width, float height) {
        this.scale.set(width, height);
    }

    @Override
    public float width() {
        return this.scale.x;
    }

    @Override
    public float height() {
        return this.scale.y;
    }
}

