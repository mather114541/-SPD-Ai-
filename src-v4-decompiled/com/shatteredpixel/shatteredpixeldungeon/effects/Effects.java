/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.noosa.Image;

public class Effects {
    public static Image get(Type type) {
        Image icon = new Image("effects/effects.png");
        switch (type.ordinal()) {
            case 0: {
                icon.frame(icon.texture.uvRect(0.0f, 0.0f, 16.0f, 16.0f));
                break;
            }
            case 1: {
                icon.frame(icon.texture.uvRect(16.0f, 0.0f, 32.0f, 8.0f));
                break;
            }
            case 2: {
                icon.frame(icon.texture.uvRect(16.0f, 8.0f, 32.0f, 16.0f));
                break;
            }
            case 3: {
                icon.frame(icon.texture.uvRect(0.0f, 16.0f, 6.0f, 25.0f));
                break;
            }
            case 4: {
                icon.frame(icon.texture.uvRect(6.0f, 16.0f, 11.0f, 22.0f));
                break;
            }
            case 5: {
                icon.frame(icon.texture.uvRect(11.0f, 16.0f, 16.0f, 22.0f));
                break;
            }
            case 6: {
                icon.frame(icon.texture.uvRect(16.0f, 16.0f, 32.0f, 24.0f));
                break;
            }
            case 7: {
                icon.frame(icon.texture.uvRect(16.0f, 23.0f, 32.0f, 31.0f));
                break;
            }
            case 8: {
                icon.frame(icon.texture.uvRect(16.0f, 30.0f, 32.0f, 38.0f));
            }
        }
        return icon;
    }

    public static enum Type {
        RIPPLE,
        LIGHTNING,
        WOUND,
        EXCLAMATION,
        CHAIN,
        ETHEREAL_CHAIN,
        DEATH_RAY,
        LIGHT_RAY,
        HEALTH_RAY;

    }
}

