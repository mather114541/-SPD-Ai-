/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;

public class Swap
extends Actor {
    private Char ch1;
    private Char ch2;
    private Effect eff1;
    private Effect eff2;
    private float delay;

    public Swap(Char ch1, Char ch2) {
        this.ch1 = ch1;
        this.ch2 = ch2;
        this.delay = (float)Dungeon.level.distance(ch1.pos, ch2.pos) * 0.1f;
        this.eff1 = new Effect(ch1.sprite, ch1.pos, ch2.pos);
        this.eff2 = new Effect(ch2.sprite, ch2.pos, ch1.pos);
        Sample.INSTANCE.play("sounds/teleport.mp3");
    }

    @Override
    protected boolean act() {
        return false;
    }

    private void finish(Effect eff) {
        if (eff == this.eff1) {
            this.eff1 = null;
        }
        if (eff == this.eff2) {
            this.eff2 = null;
        }
        if (this.eff1 == null && this.eff2 == null) {
            Actor.remove(this);
            this.next();
            int pos = this.ch1.pos;
            this.ch1.pos = this.ch2.pos;
            this.ch2.pos = pos;
            Dungeon.level.occupyCell(this.ch1);
            Dungeon.level.occupyCell(this.ch2);
            if (this.ch1 == Dungeon.hero || this.ch2 == Dungeon.hero) {
                Dungeon.observe();
                GameScene.updateFog();
            }
        }
    }

    private class Effect
    extends Visual {
        private CharSprite sprite;
        private PointF end;
        private float passed;

        public Effect(CharSprite sprite, int from, int to) {
            super(0.0f, 0.0f, 0.0f, 0.0f);
            this.sprite = sprite;
            this.point(sprite.worldToCamera(from));
            this.end = sprite.worldToCamera(to);
            this.speed.set(2.0f * (this.end.x - this.x) / Swap.this.delay, 2.0f * (this.end.y - this.y) / Swap.this.delay);
            this.acc.set(-this.speed.x / Swap.this.delay, -this.speed.y / Swap.this.delay);
            this.passed = 0.0f;
            sprite.parent.add(this);
        }

        @Override
        public void update() {
            float f;
            super.update();
            this.passed += Game.elapsed;
            if (f < Swap.this.delay) {
                this.sprite.x = this.x;
                this.sprite.y = this.y;
            } else {
                this.sprite.point(this.end);
                this.killAndErase();
                Swap.this.finish(this);
            }
        }
    }
}

