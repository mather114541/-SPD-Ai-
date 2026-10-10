/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class BlacksmithSprite
extends MobSprite {
    private Emitter emitter;

    public BlacksmithSprite() {
        this.texture("sprites/blacksmith.png");
        TextureFilm frames = new TextureFilm(this.texture, 13, 16);
        this.idle = new MovieClip.Animation(15, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3);
        this.run = new MovieClip.Animation(20, true);
        this.run.frames(frames, 0);
        this.die = new MovieClip.Animation(20, false);
        this.die.frames(frames, 0);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.emitter = new Emitter();
        this.emitter.autoKill = false;
        this.emitter.pos(this.x + 7.0f, this.y + 12.0f);
        this.parent.add(this.emitter);
    }

    @Override
    public void update() {
        super.update();
        if (this.emitter != null) {
            this.emitter.visible = this.visible;
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (this.visible && this.emitter != null && anim == this.idle) {
            this.emitter.burst(Speck.factory(112), 3);
            if (!Music.INSTANCE.paused()) {
                float volume = 0.2f / (float)Dungeon.level.distance(this.ch.pos, Dungeon.hero.pos);
                Sample.INSTANCE.play("sounds/evoke.mp3", volume, volume, 0.8f);
            }
        }
    }
}

