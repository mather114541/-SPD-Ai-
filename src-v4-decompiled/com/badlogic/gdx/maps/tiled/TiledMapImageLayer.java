/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;

public class TiledMapImageLayer
extends MapLayer {
    private TextureRegion region;
    private float x;
    private float y;
    private boolean repeatX;
    private boolean repeatY;
    private boolean supportsTransparency;

    public TiledMapImageLayer(TextureRegion region, float x, float y, boolean repeatX, boolean repeatY) {
        this.region = region;
        this.x = x;
        this.y = y;
        this.repeatX = repeatX;
        this.repeatY = repeatY;
        this.supportsTransparency = this.checkTransparencySupport(region);
    }

    private boolean checkTransparencySupport(TextureRegion region) {
        Pixmap.Format format = region.getTexture().getTextureData().getFormat();
        return format != null && this.formatHasAlpha(format);
    }

    private boolean formatHasAlpha(Pixmap.Format format) {
        switch (format) {
            case Alpha: 
            case LuminanceAlpha: 
            case RGBA4444: 
            case RGBA8888: {
                return true;
            }
        }
        return false;
    }

    public boolean supportsTransparency() {
        return this.supportsTransparency;
    }

    public TextureRegion getTextureRegion() {
        return this.region;
    }

    public void setTextureRegion(TextureRegion region) {
        this.region = region;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isRepeatX() {
        return this.repeatX;
    }

    public void setRepeatX(boolean repeatX) {
        this.repeatX = repeatX;
    }

    public boolean isRepeatY() {
        return this.repeatY;
    }

    public void setRepeatY(boolean repeatY) {
        this.repeatY = repeatY;
    }
}

