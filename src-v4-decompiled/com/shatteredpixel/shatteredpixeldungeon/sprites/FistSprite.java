/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public abstract class FistSprite
extends MobSprite {
    private static final float SLAM_TIME = 0.33f;
    protected int boltType;
    private Emitter particles;

    protected abstract int texOffset();

    protected abstract Emitter createEmitter();

    public FistSprite() {
        int c = this.texOffset();
        this.texture("sprites/yog_fists.png");
        TextureFilm frames = new TextureFilm(this.texture, 24, 17);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, c + 0, c + 0, c + 1);
        this.run = new MovieClip.Animation(3, true);
        this.run.frames(frames, c + 0, c + 1);
        this.attack = new MovieClip.Animation(Math.round(3.030303f), false);
        this.attack.frames(frames, c + 0);
        this.zap = new MovieClip.Animation(8, false);
        this.zap.frames(frames, c + 0, c + 5, c + 6);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, c + 0, c + 2, c + 3, c + 4);
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
    public void attack(int cell) {
        super.attack(cell);
        this.jump(this.ch.pos, this.ch.pos, 9.0f, 0.33f, null);
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, this.boltType, this, cell, new Callback(){

            @Override
            public void call() {
                ((YogFist)FistSprite.this.ch).onZapComplete();
            }
        });
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (anim == this.attack) {
            PixelScene.shake(4.0f, 0.2f);
        } else if (anim == this.zap) {
            this.idle();
        }
    }

    public static class Dark
    extends FistSprite {
        public Dark() {
            this.boltType = 7;
        }

        @Override
        protected int texOffset() {
            return 50;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(ShadowParticle.MISSILE, 0.06f);
            return emitter;
        }

        @Override
        public int blood() {
            return -11915437;
        }
    }

    public static class Bright
    extends FistSprite {
        public Bright() {
            this.boltType = 8;
        }

        @Override
        protected int texOffset() {
            return 40;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(SparkParticle.STATIC, 0.06f);
            return emitter;
        }

        @Override
        public void zap(int cell) {
            super.zap(cell, null);
            ((YogFist)this.ch).onZapComplete();
            this.parent.add(new Beam.LightRay(this.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
            Sample.INSTANCE.play("sounds/ray.mp3");
        }

        @Override
        public int blood() {
            return -1;
        }
    }

    public static class Rusted
    extends FistSprite {
        public Rusted() {
            this.boltType = 3;
        }

        @Override
        protected int texOffset() {
            return 30;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(CorrosionParticle.MISSILE, 0.06f);
            return emitter;
        }

        @Override
        public int blood() {
            return -8421505;
        }
    }

    public static class Rotting
    extends FistSprite {
        public Rotting() {
            this.boltType = 1107;
        }

        @Override
        protected int texOffset() {
            return 20;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(Speck.factory(107), 0.25f);
            return emitter;
        }

        @Override
        public int blood() {
            return -4670559;
        }
    }

    public static class Soiled
    extends FistSprite {
        public Soiled() {
            this.boltType = 4;
        }

        @Override
        protected int texOffset() {
            return 10;
        }

        @Override
        protected Emitter createEmitter() {
            Emitter emitter = this.emitter();
            emitter.pour(LeafParticle.GENERAL, 0.06f);
            return emitter;
        }

        @Override
        public int blood() {
            return -8432604;
        }
    }

    public static class Burning
    extends FistSprite {
        public Burning() {
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
            return -8908;
        }
    }
}

