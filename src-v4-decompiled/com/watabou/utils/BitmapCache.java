/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.noosa.Game;
import java.util.HashMap;

public class BitmapCache {
    private static final String DEFAULT = "__default";
    private static HashMap<String, Layer> layers = new HashMap();

    public static Pixmap get(String assetName) {
        return BitmapCache.get(DEFAULT, assetName);
    }

    public static Pixmap get(String layerName, String assetName) {
        Layer layer;
        if (!layers.containsKey(layerName)) {
            layer = new Layer();
            layers.put(layerName, layer);
        } else {
            layer = layers.get(layerName);
        }
        if (layer.containsKey(assetName)) {
            return (Pixmap)layer.get(assetName);
        }
        try {
            Pixmap bmp = new Pixmap(Gdx.files.internal(assetName));
            layer.put(assetName, bmp);
            return bmp;
        }
        catch (Exception e) {
            Game.reportException(e);
            return null;
        }
    }

    public static void clear(String layerName) {
        if (layers.containsKey(layerName)) {
            layers.get(layerName).clear();
            layers.remove(layerName);
        }
    }

    public static void clear() {
        for (Layer layer : layers.values()) {
            layer.clear();
        }
        layers.clear();
    }

    private static class Layer
    extends HashMap<Object, Pixmap> {
        private Layer() {
        }

        @Override
        public void clear() {
            for (Pixmap bmp : this.values()) {
                bmp.dispose();
            }
            super.clear();
        }
    }
}

