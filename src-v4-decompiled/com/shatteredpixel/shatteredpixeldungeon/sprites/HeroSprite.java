/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HeroDisguise;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;

public class HeroSprite
extends CharSprite {
    private static final int FRAME_WIDTH = 12;
    private static final int FRAME_HEIGHT = 15;
    private static final int RUN_FRAMERATE = 20;
    private static TextureFilm tiers;
    private MovieClip.Animation fly;
    private MovieClip.Animation read;

    public HeroSprite() {
        this.texture(Dungeon.hero.heroClass.spritesheet());
        this.updateArmor();
        this.link(Dungeon.hero);
        if (this.ch.isAlive()) {
            this.idle();
        } else {
            this.die();
        }
    }

    public void disguise(HeroClass cls) {
        this.texture(cls.spritesheet());
        this.updateArmor();
    }

    public void updateArmor() {
        TextureFilm film = new TextureFilm(HeroSprite.tiers(), (Object)Dungeon.hero.tier(), 12, 15);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);
        this.run = new MovieClip.Animation(20, true);
        this.run.frames(film, 2, 3, 4, 5, 6, 7);
        this.die = new MovieClip.Animation(20, false);
        this.die.frames(film, 8, 9, 10, 11, 12, 11);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(film, 13, 14, 15, 0);
        this.zap = this.attack.clone();
        this.operate = new MovieClip.Animation(8, false);
        this.operate.frames(film, 16, 17, 16, 17);
        this.fly = new MovieClip.Animation(1, true);
        this.fly.frames(film, 18);
        this.read = new MovieClip.Animation(20, false);
        this.read.frames(film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19);
        if (Dungeon.hero.isAlive()) {
            this.idle();
        } else {
            this.die();
        }
    }

    @Override
    public void place(int p) {
        super.place(p);
        if (Game.scene() instanceof GameScene) {
            Camera.main.panFollow(this, 5.0f);
        }
    }

    @Override
    public void move(int from, int to) {
        super.move(from, to);
        if (this.ch != null && this.ch.flying) {
            this.play(this.fly);
        }
        Camera.main.panFollow(this, 20.0f);
    }

    @Override
    public void idle() {
        super.idle();
        if (this.ch != null && this.ch.flying) {
            this.play(this.fly);
        }
    }

    @Override
    public void jump(int from, int to, float height, float duration, Callback callback) {
        super.jump(from, to, height, duration, callback);
        this.play(this.fly);
        Camera.main.panFollow(this, 20.0f);
    }

    public synchronized void read() {
        this.animCallback = new Callback(){

            @Override
            public void call() {
                HeroSprite.this.idle();
                HeroSprite.this.ch.onOperateComplete();
            }
        };
        this.play(this.read);
    }

    @Override
    public void bloodBurstA(PointF from, int damage) {
    }

    @Override
    public void update() {
        this.sleeping = this.ch.isAlive() && ((Hero)this.ch).resting;
        super.update();
    }

    public void sprint(float speed) {
        this.run.delay = 1.0f / speed / 20.0f;
    }

    public static TextureFilm tiers() {
        if (tiers == null) {
            SmartTexture texture = TextureCache.get("sprites/rogue.png");
            tiers = new TextureFilm(texture, texture.width, 15);
        }
        return tiers;
    }

    public static Image avatar(Hero hero) {
        if (hero.buff(HeroDisguise.class) != null) {
            return HeroSprite.avatar(hero.buff(HeroDisguise.class).getDisguise(), hero.tier());
        }
        return HeroSprite.avatar(hero.heroClass, hero.tier());
    }

    public static Image avatar(HeroClass cl, int armorTier) {
        RectF patch = HeroSprite.tiers().get(armorTier);
        Image avatar = new Image(cl.spritesheet());
        RectF frame = avatar.texture.uvRect(1.0f, 0.0f, 12.0f, 15.0f);
        frame.shift(patch.left, patch.top);
        avatar.frame(frame);
        return avatar;
    }
}

