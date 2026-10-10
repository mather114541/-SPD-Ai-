/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalWisp;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.TorchHalo;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PointF;

public abstract class CrystalWispSprite
extends MobSprite {
    private TorchHalo light;
    private float baseY = Float.NaN;

    public CrystalWispSprite() {
        int c = this.texOffset();
        this.texture("sprites/crystal_wisp.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 14);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(frames, c + 0);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, c + 0, c + 0, c + 0, c + 1);
        this.attack = new MovieClip.Animation(16, false);
        this.attack.frames(frames, c + 2, c + 3, c + 4, c + 5);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, c + 6, c + 7, c + 8, c + 9, c + 10, c + 11, c + 12, c + 11);
        this.play(this.idle);
    }

    @Override
    public void zap(final int cell) {
        super.zap(cell);
        this.parent.add(new AlphaTweener(this.light, 1.0f, 0.2f){

            @Override
            public void onComplete() {
                CrystalWispSprite.this.light.alpha(0.3f);
                ((CrystalWisp)CrystalWispSprite.this.ch).onZapComplete();
                Beam.LightRay ray = new Beam.LightRay(CrystalWispSprite.this.center(), DungeonTilemap.raisedTileCenterToWorld(cell));
                Sample.INSTANCE.play("sounds/ray.mp3");
                ray.hardlight(CrystalWispSprite.this.blood() & 0xFFFFFF);
                this.parent.add(ray);
            }
        });
    }

    @Override
    public synchronized void attack(int cell) {
        super.attack(cell);
        this.parent.add(new AlphaTweener(this.light, 1.0f, 0.2f){

            @Override
            public void onComplete() {
                CrystalWispSprite.this.light.alpha(0.3f);
            }
        });
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.light = new TorchHalo(this);
        this.light.hardlight(this.blood() & 0xFFFFFF);
        this.light.alpha(0.3f);
        this.light.radius(10.0f);
        GameScene.effect(this.light);
    }

    @Override
    public void die() {
        super.die();
        if (this.light != null) {
            this.light.putOut();
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.light != null) {
            this.light.killAndErase();
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap) {
            this.idle();
        }
        super.onComplete(anim);
    }

    @Override
    public void place(int cell) {
        super.place(cell);
        this.baseY = this.y;
    }

    @Override
    public PointF point(PointF p) {
        super.point(p);
        this.baseY = this.y;
        return p;
    }

    @Override
    public void update() {
        super.update();
        if (!this.paused && this.curAnim != this.die) {
            if (Float.isNaN(this.baseY)) {
                this.baseY = this.y;
            }
            this.y = this.baseY + Math.abs((float)Math.sin(Game.timeTotal));
            this.shadowOffset = 0.25f - 0.8f * Math.abs((float)Math.sin(Game.timeTotal));
        }
        if (this.light != null) {
            this.light.visible = this.visible;
            this.light.point(this.center());
        }
    }

    @Override
    public void turnTo(int from, int to) {
    }

    protected abstract int texOffset();

    public static class Red
    extends CrystalWispSprite {
        @Override
        protected int texOffset() {
            return 26;
        }

        @Override
        public int blood() {
            return -33024;
        }
    }

    public static class Green
    extends CrystalWispSprite {
        @Override
        protected int texOffset() {
            return 13;
        }

        @Override
        public int blood() {
            return -13703634;
        }
    }

    public static class Blue
    extends CrystalWispSprite {
        @Override
        protected int texOffset() {
            return 0;
        }

        @Override
        public int blood() {
            return -10046465;
        }
    }
}

