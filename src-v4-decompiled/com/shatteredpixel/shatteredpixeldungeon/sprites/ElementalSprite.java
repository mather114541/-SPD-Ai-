/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public abstract class ElementalSprite
extends MobSprite {
    protected int boltType;
    private Emitter particles;

    protected abstract int texOffset();

    protected abstract Emitter createEmitter();

    public ElementalSprite() {
        int c = this.texOffset();
        this.texture("sprites/elemental.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 14);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, c + 0, c + 1, c + 2);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, c + 0, c + 1, c + 3);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, c + 4, c + 5, c + 6);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, c + 7, c + 8, c + 9, c + 10, c + 11, c + 12, c + 13, c + 12);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (this.particles == null) {
            this.particles = this.createEmitter();
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.particles != null) {
            this.particles.visible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.particles != null) {
            this.particles.on = false;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.particles != null) {
            this.particles.killAndErase();
        }
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, this.boltType, this, cell, new Callback(){

            @Override
            public void call() {
                ((Elemental)ElementalSprite.this.ch).onZapComplete();
            }
        });
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap) {
            this.idle();
        }
        super.onComplete(anim);
    }

    public static class Chaos
    extends ElementalSprite {
        @Override
        public void zap(int cell) {
            this.zap(cell, null);
            ((Elemental)this.ch).onZapComplete();
        }

        @Override
        protected int texOffset() {
            return 56;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(RainbowParticle.BURST, 0.025f);
            return emitter;
        }

        @Override
        public int blood() {
            return -1842205;
        }
    }

    public static class Shock
    extends ElementalSprite {
        @Override
        public void zap(int cell) {
            super.zap(cell, null);
            ((Elemental)this.ch).onZapComplete();
            this.parent.add(new Beam.LightRay(this.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
            Sample.INSTANCE.play("sounds/ray.mp3");
        }

        @Override
        protected int texOffset() {
            return 42;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(SparkParticle.STATIC, 0.06f);
            return emitter;
        }

        @Override
        public int blood() {
            return -123;
        }
    }

    public static class Frost
    extends ElementalSprite {
        public Frost() {
            this.boltType = 1;
        }

        @Override
        protected int texOffset() {
            return 28;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(MagicMissile.MagicParticle.FACTORY, 0.06f);
            return emitter;
        }

        @Override
        public int blood() {
            return -7412737;
        }
    }

    public static class NewbornFire
    extends ElementalSprite {
        public NewbornFire() {
            this.boltType = 15;
        }

        @Override
        protected int texOffset() {
            return 14;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(ElmoParticle.FACTORY, 0.06f);
            return emitter;
        }

        @Override
        public int blood() {
            return -7995448;
        }
    }

    public static class Fire
    extends ElementalSprite {
        public Fire() {
            this.boltType = 2;
        }

        @Override
        protected int texOffset() {
            return 0;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(FlameParticle.FACTORY, 0.06f);
            return emitter;
        }

        @Override
        public int blood() {
            return -17613;
        }
    }
}

