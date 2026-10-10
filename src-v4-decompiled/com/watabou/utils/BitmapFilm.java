/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.utils.Rect;
import java.util.HashMap;

public class BitmapFilm {
    public Pixmap bitmap;
    protected HashMap<Object, Rect> frames = new HashMap();

    public BitmapFilm(Pixmap bitmap) {
        this.bitmap = bitmap;
        this.add(null, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
    }

    public BitmapFilm(Pixmap bitmap, int width) {
        this(bitmap, width, bitmap.getHeight());
    }

    public BitmapFilm(Pixmap bitmap, int width, int height) {
        this.bitmap = bitmap;
        int cols = bitmap.getWidth() / width;
        int rows = bitmap.getHeight() / height;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                Rect rect = new Rect(j * width, i * height, (j + 1) * width, (i + 1) * height);
                this.add(i * cols + j, rect);
            }
        }
    }

    public void add(Object id, Rect rect) {
        this.frames.put(id, rect);
    }

    public Rect get(Object id) {
        return this.frames.get(id);
    }
}

