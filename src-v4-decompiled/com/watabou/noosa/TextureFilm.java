/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.utils.RectF;
import java.util.HashMap;

public class TextureFilm {
    private static final RectF FULL = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    private int texWidth;
    private int texHeight;
    protected HashMap<Object, RectF> frames = new HashMap();

    public TextureFilm(Object tx) {
        SmartTexture texture = TextureCache.get(tx);
        this.texWidth = texture.width;
        this.texHeight = texture.height;
        this.add(null, FULL);
    }

    public TextureFilm(SmartTexture texture, int width) {
        this(texture, width, texture.height);
    }

    public TextureFilm(Object tx, int width, int height) {
        SmartTexture texture = TextureCache.get(tx);
        this.texWidth = texture.width;
        this.texHeight = texture.height;
        float uw = (float)width / (float)this.texWidth;
        float vh = (float)height / (float)this.texHeight;
        int cols = this.texWidth / width;
        int rows = this.texHeight / height;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                RectF rect = new RectF((float)j * uw, (float)i * vh, (float)(j + 1) * uw, (float)(i + 1) * vh);
                this.add(i * cols + j, rect);
            }
        }
    }

    public TextureFilm(TextureFilm atlas, Object key, int width, int height) {
        this.texWidth = atlas.texWidth;
        this.texHeight = atlas.texHeight;
        RectF patch = atlas.get(key);
        float uw = (float)width / (float)this.texWidth;
        float vh = (float)height / (float)this.texHeight;
        int cols = (int)(this.width(patch) / (float)width);
        int rows = (int)(this.height(patch) / (float)height);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                RectF rect = new RectF((float)j * uw, (float)i * vh, (float)(j + 1) * uw, (float)(i + 1) * vh);
                rect.shift(patch.left, patch.top);
                this.add(i * cols + j, rect);
            }
        }
    }

    public TextureFilm(int txWidth, int txHeight, int width, int height) {
        this.texWidth = txWidth;
        this.texHeight = txHeight;
        float uw = (float)width / (float)this.texWidth;
        float vh = (float)height / (float)this.texHeight;
        int cols = this.texWidth / width;
        int rows = this.texHeight / height;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                RectF rect = new RectF((float)j * uw, (float)i * vh, (float)(j + 1) * uw, (float)(i + 1) * vh);
                this.add(i * cols + j, rect);
            }
        }
    }

    public void add(Object id, RectF rect) {
        this.frames.put(id, rect);
    }

    public void add(Object id, float left, float top, float right, float bottom) {
        this.frames.put(id, new RectF(left / (float)this.texWidth, top / (float)this.texHeight, right / (float)this.texWidth, bottom / (float)this.texHeight));
    }

    public RectF get(Object id) {
        return this.frames.get(id);
    }

    public float width(Object id) {
        return this.width(this.get(id));
    }

    public float width(RectF frame) {
        return frame.width() * (float)this.texWidth;
    }

    public float height(Object id) {
        return this.height(this.get(id));
    }

    public float height(RectF frame) {
        return frame.height() * (float)this.texHeight;
    }
}

