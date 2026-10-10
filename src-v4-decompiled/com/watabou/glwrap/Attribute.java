/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import java.nio.FloatBuffer;

public class Attribute {
    private int location;

    public Attribute(int location) {
        this.location = location;
    }

    public int location() {
        return this.location;
    }

    public void enable() {
        Gdx.gl.glEnableVertexAttribArray(this.location);
    }

    public void disable() {
        Gdx.gl.glDisableVertexAttribArray(this.location);
    }

    public void vertexPointer(int size, int stride, FloatBuffer ptr) {
        Gdx.gl.glVertexAttribPointer(this.location, size, 5126, false, stride * 4, ptr);
    }

    public void vertexBuffer(int size, int stride, int offset) {
        Gdx.gl.glVertexAttribPointer(this.location, size, 5126, false, stride * 4, offset * 4);
    }
}

