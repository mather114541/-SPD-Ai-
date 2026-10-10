/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class Texture {
    public static final int NEAREST = 9728;
    public static final int LINEAR = 9729;
    public static final int REPEAT = 10497;
    public static final int MIRROR = 33648;
    public static final int CLAMP = 33071;
    public int id = -1;
    private static int bound_id = 0;
    public boolean premultiplied = false;

    protected void generate() {
        this.id = Gdx.gl.glGenTexture();
    }

    public static void activate(int index) {
        Gdx.gl.glActiveTexture(33984 + index);
    }

    public void bind() {
        if (this.id == -1) {
            this.generate();
        }
        if (this.id != bound_id) {
            Gdx.gl.glBindTexture(3553, this.id);
            bound_id = this.id;
        }
    }

    public static void clear() {
        bound_id = 0;
    }

    public void filter(int minMode, int maxMode) {
        this.bind();
        Gdx.gl.glTexParameterf(3553, 10241, minMode);
        Gdx.gl.glTexParameterf(3553, 10240, maxMode);
    }

    public void wrap(int s, int t) {
        this.bind();
        Gdx.gl.glTexParameterf(3553, 10242, s);
        Gdx.gl.glTexParameterf(3553, 10243, t);
    }

    public void delete() {
        if (bound_id == this.id) {
            bound_id = 0;
        }
        Gdx.gl.glDeleteTexture(this.id);
    }

    public void bitmap(Pixmap pixmap) {
        this.bind();
        Gdx.gl.glTexImage2D(3553, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
        this.premultiplied = true;
    }

    public void pixels(int w, int h, int[] pixels) {
        this.bind();
        IntBuffer imageBuffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        imageBuffer.put(pixels);
        ((Buffer)imageBuffer).position(0);
        Gdx.gl.glTexImage2D(3553, 0, 6408, w, h, 0, 6408, 5121, imageBuffer);
    }

    public void pixels(int w, int h, byte[] pixels) {
        this.bind();
        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(w * h).order(ByteOrder.nativeOrder());
        imageBuffer.put(pixels);
        ((Buffer)imageBuffer).position(0);
        Gdx.gl.glPixelStorei(3317, 1);
        Gdx.gl.glTexImage2D(3553, 0, 6406, w, h, 0, 6406, 5121, imageBuffer);
    }

    public static Texture create(Pixmap pix) {
        Texture tex = new Texture();
        tex.bitmap(pix);
        return tex;
    }

    public static Texture create(int width, int height, int[] pixels) {
        Texture tex = new Texture();
        tex.pixels(width, height, pixels);
        return tex;
    }

    public static Texture create(int width, int height, byte[] pixels) {
        Texture tex = new Texture();
        tex.pixels(width, height, pixels);
        return tex;
    }
}

