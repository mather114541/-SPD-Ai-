/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import rl.RLMode;

public class Wound
extends Image {
    private static final float TIME_TO_FADE = 1.0f;
    private float time;

    public Wound() {
        super(Effects.get(Effects.Type.WOUND));
        this.hardlight(1.0f, 0.0f, 0.0f);
        this.origin.set(this.width / 2.0f, this.height / 2.0f);
    }

    public void reset(int p) {
        this.revive();
        this.x = (float)(p % Dungeon.level.width() * 16) + (16.0f - this.width) / 2.0f;
        this.y = (float)(p / Dungeon.level.width() * 16) + (16.0f - this.height) / 2.0f;
        this.time = 1.0f;
    }

    public void reset(Visual v) {
        this.revive();
        this.point(v.center(this));
        this.time = 1.0f;
    }

    @Override
    public void update() {
        super.update();
        this.time -= Game.elapsed;
        if (this.time <= 0.0f) {
            this.kill();
        } else {
            float p = this.time / 1.0f;
            this.alpha((float)Math.sqrt(p));
            this.scale.x = 1.0f + p;
        }
    }

    public static void hit(Char ch) {
        Wound.hit(ch, 0.0f);
    }

    public static void hit(Char ch, float angle) {
        if (RLMode.headless) return;   // headless 无渲染 sprite，短路伤口特效
        if (ch.sprite.parent != null) {
            Wound w = (Wound)ch.sprite.parent.recycle(Wound.class);
            ch.sprite.parent.bringToFront(w);
            w.reset(ch.sprite);
            w.angle = angle;
        }
    }

    public static void hit(int pos) {
        Wound.hit(pos, 0.0f);
    }

    public static void hit(int pos, float angle) {
        if (RLMode.headless) return;   // headless 无渲染 sprite，短路伤口特效
        Group parent = Dungeon.hero.sprite.parent;
        Wound w = (Wound)parent.recycle(Wound.class);
        parent.bringToFront(w);
        w.reset(pos);
        w.angle = angle;
    }
}

