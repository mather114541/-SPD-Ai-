/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx;

import com.badlogic.gdx.Graphics;

public abstract class AbstractGraphics
implements Graphics {
    @Override
    public float getRawDeltaTime() {
        return this.getDeltaTime();
    }

    @Override
    public float getDensity() {
        float ppiX = this.getPpiX();
        return ppiX > 0.0f && ppiX <= Float.MAX_VALUE ? ppiX / 160.0f : 1.0f;
    }

    @Override
    public float getBackBufferScale() {
        return (float)this.getBackBufferWidth() / (float)this.getWidth();
    }
}

