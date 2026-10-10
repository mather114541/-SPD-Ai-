/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;

public class WandmakerSprite
extends MobSprite {
    private ShieldHalo shield;

    public WandmakerSprite() {
        this.texture("sprites/wandmaker.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 14);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1);
        this.run = new MovieClip.Animation(20, true);
        this.run.frames(frames, 0);
        this.die = new MovieClip.Animation(20, false);
        this.die.frames(frames, 0);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.add(CharSprite.State.SHIELDED);
    }

    @Override
    public void die() {
        super.die();
        this.processStateRemoval(CharSprite.State.SHIELDED);
        this.emitter().start(ElmoParticle.FACTORY, 0.03f, 60);
        if (this.visible) {
            Sample.INSTANCE.play("sounds/burning.mp3");
        }
    }
}

