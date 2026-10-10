/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;

public class Halo
extends Image {
    private static final Object CACHE_KEY = Halo.class;
    protected static final int RADIUS = 128;
    protected float radius = 128.0f;
    protected float brightness = 1.0f;

    public Halo() {
        if (!TextureCache.contains(CACHE_KEY)) {
            Pixmap pixmap = TextureCache.create((Object)Halo.CACHE_KEY, (int)257, (int)257).bitmap;
            pixmap.setColor(0);
            pixmap.fill();
            pixmap.setColor(-248);
            for (int i = 0; i < 128; i += 2) {
                pixmap.fillCircle(128, 128, 128 - i);
            }
        }
        this.texture(CACHE_KEY);
    }

    public Halo(float radius, int color, float brightness) {
        this();
        this.hardlight(color);
        this.brightness = brightness;
        this.alpha(this.brightness);
        this.radius(radius);
    }

    public Halo point(float x, float y) {
        this.x = x - this.width() / 2.0f;
        this.y = y - this.height() / 2.0f;
        return this;
    }

    @Override
    public void alpha(float value) {
        this.brightness = value;
        super.alpha(value);
    }

    public void radius(float value) {
        this.radius = value;
        this.scale.set(this.radius / 128.0f);
    }
}

