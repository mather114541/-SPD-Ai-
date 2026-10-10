/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.noosa.Image;

public class BannerSprites {
    public static Image get(Type type) {
        Image icon = new Image("interfaces/banners.png");
        switch (type.ordinal()) {
            case 0: {
                icon.frame(icon.texture.uvRect(0.0f, 0.0f, 139.0f, 100.0f));
                break;
            }
            case 1: {
                icon.frame(icon.texture.uvRect(139.0f, 0.0f, 278.0f, 100.0f));
                break;
            }
            case 2: {
                icon.frame(icon.texture.uvRect(0.0f, 100.0f, 240.0f, 157.0f));
                break;
            }
            case 3: {
                icon.frame(icon.texture.uvRect(240.0f, 100.0f, 480.0f, 157.0f));
                break;
            }
            case 4: {
                icon.frame(icon.texture.uvRect(0.0f, 157.0f, 127.0f, 225.0f));
                break;
            }
            case 5: {
                icon.frame(icon.texture.uvRect(128.0f, 157.0f, 256.0f, 192.0f));
            }
        }
        return icon;
    }

    public static enum Type {
        TITLE_PORT,
        TITLE_GLOW_PORT,
        TITLE_LAND,
        TITLE_GLOW_LAND,
        BOSS_SLAIN,
        GAME_OVER;

    }
}

