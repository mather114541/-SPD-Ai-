/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.Visual;
import com.watabou.utils.PointF;
import rl.RLMode;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Flare
extends Visual {
    private float duration = 0.0f;
    private float lifespan;
    private boolean lightMode = true;
    private SmartTexture texture;
    private FloatBuffer vertices;
    private ShortBuffer indices;
    private int nRays;

    public Flare(int nRays, float radius) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        int[] gradient = new int[]{-1, -1140850689, -1996488705, 65535, 0xFFFFFF};
        this.texture = TextureCache.createGradient(gradient);
        this.nRays = nRays;
        this.angle = 45.0f;
        this.angularSpeed = 180.0f;
        this.vertices = ByteBuffer.allocateDirect((nRays * 2 + 1) * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.indices = ByteBuffer.allocateDirect(nRays * 3 * 16 / 8).order(ByteOrder.nativeOrder()).asShortBuffer();
        float[] v = new float[]{0.0f, 0.0f, 0.25f, 0.0f};
        this.vertices.put(v);
        v[2] = 0.75f;
        v[3] = 0.0f;
        for (int i = 0; i < nRays; ++i) {
            float a = (float)i * 3.1415925f * 2.0f / (float)nRays;
            v[0] = (float)Math.cos(a) * radius;
            v[1] = (float)Math.sin(a) * radius;
            this.vertices.put(v);
            v[0] = (float)Math.cos(a += 6.283185f / (float)nRays / 2.0f) * radius;
            v[1] = (float)Math.sin(a) * radius;
            this.vertices.put(v);
            this.indices.put((short)0);
            this.indices.put((short)(1 + i * 2));
            this.indices.put((short)(2 + i * 2));
        }
        ((Buffer)this.indices).position(0);
    }

    public Flare color(int color, boolean lightMode) {
        this.lightMode = lightMode;
        this.hardlight(color);
        return this;
    }

    public Flare show(Visual visual, float duration) {
        if (RLMode.headless) return this;   // headless 无渲染视觉层，短路特效
        if (visual instanceof CharSprite) {
            this.point(((CharSprite)visual).destinationCenter());
        } else {
            this.point(visual.center());
        }
        visual.parent.addToBack(this);
        this.lifespan = this.duration = duration;
        if (this.lifespan > 0.0f) {
            this.scale.set(0.0f);
        }
        return this;
    }

    public Flare show(Group parent, PointF pos, float duration) {
        if (RLMode.headless) return this;   // headless 无渲染视觉层，短路特效
        this.point(pos);
        parent.add(this);
        this.lifespan = this.duration = duration;
        if (this.lifespan > 0.0f) {
            this.scale.set(0.0f);
        }
        return this;
    }

    @Override
    public void update() {
        super.update();
        if (this.duration > 0.0f) {
            float f = this.lifespan;   // 反编译补回：f 为剩余寿命
            this.lifespan -= Game.elapsed;
            if (f > 0.0f) {
                float p = 1.0f - this.lifespan / this.duration;
                p = p < 0.25f ? p * 4.0f : (1.0f - p) * 1.333f;
                this.scale.set(p);
                this.alpha(p);
            } else {
                this.killAndErase();
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (this.lightMode) {
            Blending.setLightMode();
            this.drawRays();
            Blending.setNormalMode();
        } else {
            this.drawRays();
        }
    }

    private void drawRays() {
        NoosaScript script = NoosaScript.get();
        this.texture.bind();
        script.uModel.valueM4(this.matrix);
        script.lighting(this.rm, this.gm, this.bm, this.am, this.ra, this.ga, this.ba, this.aa);
        script.camera(this.camera);
        script.drawElements(this.vertices, this.indices, this.nRays * 3);
    }
}

