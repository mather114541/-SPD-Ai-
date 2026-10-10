/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.Game;
import com.watabou.utils.RectF;
import java.util.HashMap;

public abstract class PlatformSupport {
    public static final int INSET_ALL = 3;
    public static final int INSET_LRG = 2;
    public static final int INSET_BLK = 1;
    protected static HashMap<FreeTypeFontGenerator, HashMap<Integer, BitmapFont>> fonts;
    protected int pageSize;
    protected PixmapPacker packer;
    protected boolean systemfont;

    public abstract void updateDisplaySize();

    public boolean supportsFullScreen() {
        return true;
    }

    public RectF getSafeInsets(int level) {
        return new RectF(Gdx.graphics.getSafeInsetLeft(), Gdx.graphics.getSafeInsetTop(), Gdx.graphics.getSafeInsetRight(), Gdx.graphics.getSafeInsetBottom());
    }

    public RectF getDisplayCutout() {
        return new RectF();
    }

    public abstract void updateSystemUI();

    public abstract boolean connectedToUnmeteredNetwork();

    public abstract boolean supportsVibration();

    public void vibrate(int millis) {
        if (ControllerHandler.isControllerConnected()) {
            ControllerHandler.vibrate(millis);
        } else {
            Gdx.input.vibrate(millis);
        }
    }

    public void setHonorSilentSwitch(boolean value) {
    }

    public boolean openURI(String uri) {
        return Gdx.net.openURI(uri);
    }

    public void setOnscreenKeyboardVisible(boolean value, boolean multiline) {
        Gdx.input.setOnscreenKeyboardVisible(value, Input.OnscreenKeyboardType.Default);
    }

    public abstract void setupFontGenerators(int var1, boolean var2);

    protected abstract FreeTypeFontGenerator getGeneratorForString(String var1);

    public abstract String[] splitforTextBlock(String var1, boolean var2);

    public void resetGenerators() {
        this.resetGenerators(true);
    }

    public void resetGenerators(boolean setupAfter) {
        if (fonts != null) {
            for (FreeTypeFontGenerator generator : fonts.keySet()) {
                for (BitmapFont f : fonts.get(generator).values()) {
                    f.dispose();
                }
                fonts.get(generator).clear();
                generator.dispose();
            }
            fonts.clear();
            if (this.packer != null) {
                for (PixmapPacker.Page p : this.packer.getPages()) {
                    p.getTexture().dispose();
                }
                this.packer.dispose();
            }
            fonts = null;
        }
        if (setupAfter) {
            this.setupFontGenerators(this.pageSize, this.systemfont);
        }
    }

    public void reloadGenerators() {
        if (this.packer != null) {
            for (FreeTypeFontGenerator generator : fonts.keySet()) {
                for (BitmapFont f : fonts.get(generator).values()) {
                    f.dispose();
                }
                fonts.get(generator).clear();
            }
            if (this.packer != null) {
                for (PixmapPacker.Page p : this.packer.getPages()) {
                    p.getTexture().dispose();
                }
                this.packer.dispose();
            }
            this.packer = new PixmapPacker(this.pageSize, this.pageSize, Pixmap.Format.RGBA8888, 1, false);
        }
    }

    public BitmapFont getFont(int size, String text, boolean flipped, boolean border) {
        FreeTypeFontGenerator generator = this.getGeneratorForString(text);
        if (generator == null) {
            return null;
        }
        int key = size;
        if (border) {
            key += Short.MAX_VALUE;
        }
        if (flipped) {
            key = -key;
        }
        if (!fonts.get(generator).containsKey(key)) {
            FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameters.size = size;
            parameters.flip = flipped;
            if (border) {
                parameters.borderWidth = (float)parameters.size / 10.0f;
            }
            parameters.renderCount = size >= 20 ? 2 : 3;
            parameters.hinting = FreeTypeFontGenerator.Hinting.None;
            parameters.spaceX = -((int)parameters.borderWidth);
            parameters.incremental = true;
            parameters.characters = "\ufffd";
            parameters.packer = this.packer;
            try {
                BitmapFont font = generator.generateFont(parameters);
                font.getData().missingGlyph = font.getData().getGlyph('\ufffd');
                fonts.get(generator).put(key, font);
            }
            catch (Exception e) {
                Game.reportException(e);
                return null;
            }
        }
        return fonts.get(generator).get(key);
    }
}

