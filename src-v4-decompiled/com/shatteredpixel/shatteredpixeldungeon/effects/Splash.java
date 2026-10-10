/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Visual;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.HashMap;

public class Splash {
    private static final HashMap<Integer, SplashFactory> FACTORIES = new HashMap();

    public static void at(int cell, int color, int n) {
        Splash.at(DungeonTilemap.tileCenterToWorld(cell), color, n);
    }

    public static void at(PointF p, int color, int n) {
        if (n <= 0) {
            return;
        }
        Emitter emitter = GameScene.emitter();
        if (emitter == null) {
            return;
        }
        emitter.pos(p);
        if (!FACTORIES.containsKey(color)) {
            FACTORIES.put(color, new SplashFactory());
        }
        SplashFactory fact = FACTORIES.get(color);
        fact.color = color;
        fact.dir = -1.5707963f;
        fact.cone = 3.1415925f;
        emitter.burst(fact, n);
    }

    public static void at(PointF p, float dir, float cone, int color, int n) {
        if (n <= 0) {
            return;
        }
        Emitter emitter = GameScene.emitter();
        if (emitter == null) {
            return;
        }
        emitter.pos(p);
        if (!FACTORIES.containsKey(color)) {
            FACTORIES.put(color, new SplashFactory());
        }
        SplashFactory fact = FACTORIES.get(color);
        fact.color = color;
        fact.dir = dir;
        fact.cone = cone;
        emitter.burst(fact, n);
    }

    public static void around(Visual v, int color, int n) {
        if (n <= 0) {
            return;
        }
        Emitter emitter = GameScene.emitter();
        if (emitter == null) {
            return;
        }
        emitter.pos(v);
        if (!FACTORIES.containsKey(color)) {
            FACTORIES.put(color, new SplashFactory());
        }
        SplashFactory fact = FACTORIES.get(color);
        fact.color = color;
        fact.dir = -1.5707963f;
        fact.cone = 3.1415925f;
        emitter.burst(fact, n);
    }

    public static void at(PointF p, float dir, float cone, int color, int n, float interval) {
        if (n <= 0) {
            return;
        }
        Emitter emitter = GameScene.emitter();
        if (emitter == null) {
            return;
        }
        emitter.pos(p);
        if (!FACTORIES.containsKey(color)) {
            FACTORIES.put(color, new SplashFactory());
        }
        SplashFactory fact = FACTORIES.get(color);
        fact.color = color;
        fact.dir = dir;
        fact.cone = cone;
        emitter.start(fact, interval, n);
    }

    private static class SplashFactory
    extends Emitter.Factory {
        public int color;
        public float dir;
        public float cone;

        private SplashFactory() {
        }

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            PixelParticle p = (PixelParticle)emitter.recycle(PixelParticle.Shrinking.class);
            p.reset(x, y, this.color, 4.0f, Random.Float(0.5f, 1.0f));
            p.speed.polar(Random.Float(this.dir - this.cone / 2.0f, this.dir + this.cone / 2.0f), Random.Float(40.0f, 80.0f));
            p.acc.set(0.0f, 100.0f);
        }
    }
}

