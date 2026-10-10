/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class Pushing
extends Actor {
    private CharSprite sprite;
    private int from;
    private int to;
    private Effect effect;
    private Char ch;
    private Callback callback;

    public Pushing(Char ch, int from, int to) {
        this.actPriority = 110;
        this.ch = ch;
        this.sprite = ch.sprite;
        this.from = from;
        this.to = to;
        this.callback = null;
        if (ch == Dungeon.hero) {
            Camera.main.panFollow(ch.sprite, 20.0f);
        }
    }

    public Pushing(Char ch, int from, int to, Callback callback) {
        this(ch, from, to);
        this.callback = callback;
    }

    @Override
    protected boolean act() {
        Actor.remove(this);
        if (this.sprite != null && this.sprite.parent != null) {
            if (Dungeon.level.heroFOV[this.from] || Dungeon.level.heroFOV[this.to]) {
                this.sprite.visible = true;
            }
            if (this.effect == null) {
                new Effect();
            }
        } else {
            return true;
        }
        for (Actor actor : Actor.all()) {
            if (!(actor instanceof Pushing) || actor.cooldown() != 0.0f) continue;
            return true;
        }
        return false;
    }

    public static boolean pushingExistsForChar(Char ch) {
        for (Actor a : Pushing.all()) {
            if (!(a instanceof Pushing) || ((Pushing)a).ch != ch) continue;
            return true;
        }
        return false;
    }

    public class Effect
    extends Visual {
        private static final float DELAY = 0.15f;
        private PointF end;
        private float delay;

        public Effect() {
            super(0.0f, 0.0f, 0.0f, 0.0f);
            this.point(Pushing.this.sprite.worldToCamera(Pushing.this.from));
            this.end = Pushing.this.sprite.worldToCamera(Pushing.this.to);
            this.speed.set(2.0f * (this.end.x - this.x) / 0.15f, 2.0f * (this.end.y - this.y) / 0.15f);
            this.acc.set(-this.speed.x / 0.15f, -this.speed.y / 0.15f);
            this.delay = 0.0f;
            if (Pushing.this.sprite.parent != null) {
                Pushing.this.sprite.parent.add(this);
            }
        }

        @Override
        public void update() {
            float f;
            super.update();
            this.delay += Game.elapsed;
            if (f < 0.15f) {
                Pushing.this.sprite.x = this.x;
                Pushing.this.sprite.y = this.y;
            } else {
                Pushing.this.sprite.point(this.end);
                this.killAndErase();
                Actor.remove(Pushing.this);
                if (Pushing.this.callback != null) {
                    Pushing.this.callback.call();
                }
                GameScene.sortMobSprites();
                Pushing.this.next();
            }
        }
    }
}

