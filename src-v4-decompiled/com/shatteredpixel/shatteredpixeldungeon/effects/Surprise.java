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

public class Surprise
extends Image {
    private static final float TIME_TO_FADE = 1.0f;
    private float time;

    public Surprise() {
        super(Effects.get(Effects.Type.EXCLAMATION));
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
        float f;
        super.update();
        this.time -= Game.elapsed;
        if (f <= 0.0f) {
            this.kill();
        } else {
            float p = this.time / 1.0f;
            this.alpha((float)Math.sqrt(p));
            this.scale.y = 1.0f + p;
            this.scale.x = 1.0f + p / 4.0f;
        }
    }

    public static void hit(Char ch) {
        Surprise.hit(ch, 0.0f);
    }

    public static void hit(Char ch, float angle) {
        if (ch.sprite != null && ch.sprite.parent != null) {
            Surprise s = (Surprise)ch.sprite.parent.recycle(Surprise.class);
            ch.sprite.parent.bringToFront(s);
            s.reset(ch.sprite);
            s.angle = angle;
        }
    }

    public static void hit(int pos) {
        Surprise.hit(pos, 0.0f);
    }

    public static void hit(int pos, float angle) {
        Group parent = Dungeon.hero.sprite.parent;
        Surprise s = (Surprise)parent.recycle(Surprise.class);
        parent.bringToFront(s);
        s.reset(pos);
        s.angle = angle;
    }
}

