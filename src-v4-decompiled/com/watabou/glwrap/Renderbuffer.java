/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;

public class Renderbuffer {
    public static final int RGBA8 = 6408;
    public static final int DEPTH16 = 33189;
    public static final int STENCIL8 = 36168;
    private int id = Gdx.gl.glGenRenderbuffer();

    public int id() {
        return this.id;
    }

    public void bind() {
        Gdx.gl.glBindRenderbuffer(36161, this.id);
    }

    public void delete() {
        Gdx.gl.glDeleteRenderbuffer(this.id);
    }

    public void storage(int format, int width, int height) {
        Gdx.gl.glRenderbufferStorage(36161, format, width, height);
    }
}

