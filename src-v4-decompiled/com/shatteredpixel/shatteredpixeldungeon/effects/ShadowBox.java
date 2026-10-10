/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.gltextures.SmartTexture;
import com.watabou.noosa.NinePatch;

public class ShadowBox
extends NinePatch {
    public static final float SIZE = 16.0f;

    public ShadowBox() {
        super("interfaces/shadow.png", 1);
        if (this.texture.id == -1) {
            this.texture.filter(SmartTexture.LINEAR, SmartTexture.LINEAR);
        }
        this.scale.set(16.0f, 16.0f);
    }

    @Override
    public void size(float width, float height) {
        super.size(width / 16.0f, height / 16.0f);
    }

    public void boxRect(float x, float y, float width, float height) {
        this.x = x - 16.0f;
        this.y = y - 16.0f;
        this.size(width + 32.0f, height + 32.0f);
    }
}

