/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glwrap;

import com.badlogic.gdx.Gdx;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Vertexbuffer {
    private int id;
    private FloatBuffer vertices;
    private int updateStart;
    private int updateEnd;
    private static final ArrayList<Vertexbuffer> buffers = new ArrayList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vertexbuffer(FloatBuffer vertices) {
        ArrayList<Vertexbuffer> arrayList = buffers;
        synchronized (arrayList) {
            this.id = Gdx.gl.glGenBuffer();
            this.vertices = vertices;
            buffers.add(this);
            this.updateStart = 0;
            this.updateEnd = vertices.limit();
        }
    }

    public void updateVertices() {
        this.updateVertices(this.vertices);
    }

    public void updateVertices(FloatBuffer vertices) {
        this.updateVertices(vertices, 0, vertices.limit());
    }

    public void updateVertices(FloatBuffer vertices, int start, int end) {
        this.vertices = vertices;
        this.updateStart = this.updateStart == -1 ? start : Math.min(start, this.updateStart);
        this.updateEnd = this.updateEnd == -1 ? end : Math.max(end, this.updateEnd);
    }

    public void updateGLData() {
        if (this.updateStart == -1) {
            return;
        }
        ((Buffer)this.vertices).position(this.updateStart);
        this.bind();
        if (this.updateStart == 0 && this.updateEnd == this.vertices.limit()) {
            Gdx.gl.glBufferData(34962, this.vertices.limit() * 4, this.vertices, 35048);
        } else {
            Gdx.gl.glBufferSubData(34962, this.updateStart * 4, (this.updateEnd - this.updateStart) * 4, this.vertices);
        }
        this.release();
        this.updateEnd = -1;
        this.updateStart = -1;
    }

    public void bind() {
        Gdx.gl.glBindBuffer(34962, this.id);
    }

    public void release() {
        Gdx.gl.glBindBuffer(34962, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void delete() {
        ArrayList<Vertexbuffer> arrayList = buffers;
        synchronized (arrayList) {
            Gdx.gl.glDeleteBuffer(this.id);
            buffers.remove(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clear() {
        ArrayList<Vertexbuffer> arrayList = buffers;
        synchronized (arrayList) {
            for (Vertexbuffer buf : buffers.toArray(new Vertexbuffer[0])) {
                buf.delete();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void reload() {
        ArrayList<Vertexbuffer> arrayList = buffers;
        synchronized (arrayList) {
            for (Vertexbuffer buf : buffers) {
                buf.updateVertices();
                buf.updateGLData();
            }
        }
    }
}

