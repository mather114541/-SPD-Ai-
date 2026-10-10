/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import com.watabou.glwrap.Renderbuffer;
import com.watabou.glwrap.Texture;

public class Framebuffer {
    public static final int COLOR = 36064;
    public static final int DEPTH = 36096;
    public static final int STENCIL = 36128;
    public static final Framebuffer system = new Framebuffer(0);
    private int id;

    public Framebuffer() {
        this.id = Gdx.gl.glGenBuffer();
    }

    private Framebuffer(int n) {
    }

    public void bind() {
        Gdx.gl.glBindFramebuffer(36160, this.id);
    }

    public void delete() {
        Gdx.gl.glDeleteBuffer(this.id);
    }

    public void attach(int point, Texture tex) {
        this.bind();
        Gdx.gl.glFramebufferTexture2D(36160, point, 3553, tex.id, 0);
    }

    public void attach(int point, Renderbuffer buffer) {
        this.bind();
        Gdx.gl.glFramebufferRenderbuffer(36161, point, 3553, buffer.id());
    }

    public boolean status() {
        this.bind();
        return Gdx.gl.glCheckFramebufferStatus(36160) == 36053;
    }
}

