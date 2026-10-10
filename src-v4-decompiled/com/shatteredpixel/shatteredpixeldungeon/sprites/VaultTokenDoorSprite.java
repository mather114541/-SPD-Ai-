/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class VaultTokenDoorSprite
extends MobSprite {
    public VaultTokenDoorSprite() {
        this.perspectiveRaise = 0.25f;
        this.renderShadow = false;
        this.visibleOutOfFFOV = true;
        this.texture("sprites/vault_tokens_door.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(1, false);
        this.idle.frames(frames, 0);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.die = this.idle.clone();
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.renderShadow = false;
    }
}

