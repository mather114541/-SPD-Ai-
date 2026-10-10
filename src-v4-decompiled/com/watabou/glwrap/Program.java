/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;
import com.watabou.glwrap.Attribute;
import com.watabou.glwrap.Shader;
import com.watabou.glwrap.Uniform;
import com.watabou.noosa.Game;
import java.nio.IntBuffer;

public class Program {
    private int handle = Gdx.gl.glCreateProgram();

    public int handle() {
        return this.handle;
    }

    public void attach(Shader shader) {
        Gdx.gl.glAttachShader(this.handle, shader.handle());
    }

    public void link() {
        Gdx.gl.glLinkProgram(this.handle);
        IntBuffer status = BufferUtils.newIntBuffer(1);
        Gdx.gl.glGetProgramiv(this.handle, 35714, status);
        if (status.get() == 0) {
            Game.reportException(new RuntimeException(Gdx.gl.glGetProgramInfoLog(this.handle)));
        }
    }

    public Attribute attribute(String name) {
        return new Attribute(Gdx.gl.glGetAttribLocation(this.handle, name));
    }

    public Uniform uniform(String name) {
        return new Uniform(Gdx.gl.glGetUniformLocation(this.handle, name));
    }

    public void use() {
        Gdx.gl.glUseProgram(this.handle);
    }

    public void delete() {
        Gdx.gl.glDeleteProgram(this.handle);
    }

    public static Program create(Shader ... shaders) {
        Program program = new Program();
        for (int i = 0; i < shaders.length; ++i) {
            program.attach(shaders[i]);
        }
        program.link();
        return program;
    }
}

