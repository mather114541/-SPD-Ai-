/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.Visual;
import com.watabou.utils.PointF;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class CircleArc
extends Visual {
    private float duration = 0.0f;
    private float lifespan;
    private float sweep;
    private boolean dirty;
    private boolean lightMode = true;
    private SmartTexture texture = TextureCache.createSolid(-1);
    protected float[] vertices;
    private FloatBuffer verticesBuffer;
    private ShortBuffer indices;
    private int nTris;
    private float rad;

    public CircleArc(int triangles, float radius) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.nTris = triangles;
        this.rad = radius;
        this.vertices = new float[4];
        this.verticesBuffer = ByteBuffer.allocateDirect((this.nTris * 2 + 1) * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.indices = ByteBuffer.allocateDirect(this.nTris * 3 * 16 / 8).order(ByteOrder.nativeOrder()).asShortBuffer();
        this.sweep = 1.0f;
        this.updateTriangles();
    }

    public CircleArc color(int color, boolean lightMode) {
        this.lightMode = lightMode;
        this.hardlight(color);
        return this;
    }

    public CircleArc show(Visual visual, float duration) {
        this.point(visual.center());
        visual.parent.addToBack(this);
        this.lifespan = this.duration = duration;
        return this;
    }

    public CircleArc show(Group parent, PointF pos, float duration) {
        this.point(pos);
        parent.add(this);
        this.lifespan = this.duration = duration;
        return this;
    }

    public void setSweep(float sweep) {
        if (sweep != this.sweep) {
            this.sweep = sweep;
            this.dirty = true;
        }
    }

    public float getSweep() {
        return this.sweep;
    }

    private void updateTriangles() {
        this.dirty = false;
        ((Buffer)this.indices).position(0);
        ((Buffer)this.verticesBuffer).position(0);
        this.vertices[0] = 0.0f;
        this.vertices[1] = 0.0f;
        this.vertices[2] = 0.25f;
        this.vertices[3] = 0.0f;
        this.verticesBuffer.put(this.vertices);
        this.vertices[2] = 0.75f;
        this.vertices[3] = 0.0f;
        double start = 2.0 * (Math.PI - Math.PI * (double)this.sweep) - 1.5707963267948966;
        for (int i = 0; i < this.nTris; ++i) {
            double a = start + (double)i * Math.PI * 2.0 / (double)this.nTris * (double)this.sweep;
            this.vertices[0] = (float)Math.cos(a) * this.rad;
            this.vertices[1] = (float)Math.sin(a) * this.rad;
            this.verticesBuffer.put(this.vertices);
            this.vertices[0] = (float)Math.cos(a += (double)(6.283185f / (float)this.nTris * this.sweep)) * this.rad;
            this.vertices[1] = (float)Math.sin(a) * this.rad;
            this.verticesBuffer.put(this.vertices);
            this.indices.put((short)0);
            this.indices.put((short)(1 + i * 2));
            this.indices.put((short)(2 + i * 2));
        }
        ((Buffer)this.indices).position(0);
    }

    @Override
    public void update() {
        super.update();
        if (this.duration > 0.0f) {
            float f;
            this.lifespan -= Game.elapsed;
            if (f > 0.0f) {
                this.sweep = this.lifespan / this.duration;
                this.dirty = true;
            } else {
                this.killAndErase();
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (this.dirty) {
            this.updateTriangles();
        }
        if (this.lightMode) {
            Blending.setLightMode();
        }
        NoosaScript script = NoosaScript.get();
        this.texture.bind();
        script.uModel.valueM4(this.matrix);
        script.lighting(this.rm, this.gm, this.bm, this.am, this.ra, this.ga, this.ba, this.aa);
        script.camera(this.camera);
        script.drawElements(this.verticesBuffer, this.indices, this.nTris * 3);
        if (this.lightMode) {
            Blending.setNormalMode();
        }
    }
}

