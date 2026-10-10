/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class VaultMirrorSprite
extends MobSprite {
    public VaultMirrorSprite() {
        this.renderShadow = false;
        this.visibleOutOfFFOV = true;
        this.texture("sprites/vault_mirror.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 23);
        this.idle = new MovieClip.Animation(15, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.die = this.idle.clone();
        this.play(this.idle);
    }

    @Override
    public void turnTo(int from, int to) {
        this.flipHorizontal = false;
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.renderShadow = false;
    }
}

