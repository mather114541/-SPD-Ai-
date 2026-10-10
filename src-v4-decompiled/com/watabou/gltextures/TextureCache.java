/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.gltextures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import java.util.HashMap;

public class TextureCache {
    private static HashMap<Object, SmartTexture> all = new HashMap();

    public static synchronized SmartTexture createSolid(int color) {
        String key = "1x1:" + color;
        if (all.containsKey(key)) {
            return all.get(key);
        }
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color << 8 | color >>> 24);
        pixmap.fill();
        SmartTexture tx = new SmartTexture(pixmap);
        all.put(key, tx);
        return tx;
    }

    public static synchronized SmartTexture createGradient(int ... colors) {
        String key = String.valueOf(colors);
        if (all.containsKey(key)) {
            return all.get(key);
        }
        Pixmap pixmap = new Pixmap(colors.length, 1, Pixmap.Format.RGBA8888);
        for (int i = 0; i < colors.length; ++i) {
            pixmap.drawPixel(i, 0, colors[i] << 8 | colors[i] >>> 24);
        }
        SmartTexture tx = new SmartTexture(pixmap);
        tx.filter(Texture.LINEAR, Texture.LINEAR);
        tx.wrap(Texture.CLAMP, Texture.CLAMP);
        all.put(key, tx);
        return tx;
    }

    public static synchronized SmartTexture create(Object key, int width, int height) {
        if (all.containsKey(key)) {
            return all.get(key);
        }
        SmartTexture tx = new SmartTexture(new Pixmap(width, height, Pixmap.Format.RGBA8888));
        tx.filter(Texture.LINEAR, Texture.LINEAR);
        tx.wrap(Texture.CLAMP, Texture.CLAMP);
        all.put(key, tx);
        return tx;
    }

    public static synchronized void remove(Object key) {
        SmartTexture tx = all.get(key);
        if (tx != null) {
            all.remove(key);
            tx.delete();
        }
    }

    public static synchronized SmartTexture get(Object src) {
        if (all.containsKey(src)) {
            return all.get(src);
        }
        if (src instanceof SmartTexture) {
            return (SmartTexture)src;
        }
        SmartTexture tx = new SmartTexture(TextureCache.getBitmap(src));
        all.put(src, tx);
        return tx;
    }

    public static synchronized void clear() {
        for (Texture texture : all.values()) {
            texture.delete();
        }
        all.clear();
    }

    public static synchronized void reload() {
        for (SmartTexture tx : all.values()) {
            tx.reload();
        }
    }

    public static Pixmap getBitmap(Object src) {
        try {
            if (src instanceof Integer) {
                return null;
            }
            if (src instanceof String) {
                return new Pixmap(Gdx.files.internal((String)src));
            }
            if (src instanceof Pixmap) {
                return (Pixmap)src;
            }
            return null;
        }
        catch (Exception e) {
            Game.reportException(e);
            return null;
        }
    }

    public static synchronized boolean contains(Object key) {
        return all.containsKey(key);
    }
}

