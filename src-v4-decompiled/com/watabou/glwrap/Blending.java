/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;

public class Blending {
    public static void useDefault() {
        Blending.enable();
        Blending.setNormalMode();
    }

    public static void enable() {
        Gdx.gl.glEnable(3042);
    }

    public static void disable() {
        Gdx.gl.glDisable(3042);
    }

    public static void setNormalMode() {
        Gdx.gl.glBlendFunc(770, 771);
    }

    public static void setLightMode() {
        Gdx.gl.glBlendFunc(770, 1);
    }
}

