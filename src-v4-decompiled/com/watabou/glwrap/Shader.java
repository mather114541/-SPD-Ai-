/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.IntBuffer;

public class Shader {
    public static final int VERTEX = 35633;
    public static final int FRAGMENT = 35632;
    private int handle;

    public Shader(int type) {
        this.handle = Gdx.gl.glCreateShader(type);
    }

    public int handle() {
        return this.handle;
    }

    public void source(String src) {
        Gdx.gl.glShaderSource(this.handle, src);
    }

    public void compile() {
        Gdx.gl.glCompileShader(this.handle);
        IntBuffer status = BufferUtils.newIntBuffer(1);
        Gdx.gl.glGetShaderiv(this.handle, 35713, status);
        if (status.get() == 0) {
            throw new Error(Gdx.gl.glGetShaderInfoLog(this.handle));
        }
    }

    public void delete() {
        Gdx.gl.glDeleteShader(this.handle);
    }

    public static Shader createCompiled(int type, String src) {
        Shader shader = new Shader(type);
        shader.source(src);
        shader.compile();
        return shader;
    }
}

