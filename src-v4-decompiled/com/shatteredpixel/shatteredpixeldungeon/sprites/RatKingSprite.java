/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class RatKingSprite
extends MobSprite {
    public boolean festive;

    public RatKingSprite() {
        this.resetAnims();
    }

    public void resetAnims() {
        int c;
        int height = 16;
        switch (Holiday.getCurrentHoliday()) {
            default: {
                c = 0;
                break;
            }
            case APRIL_FOOLS: {
                height = 17;
                c = 16;
                break;
            }
            case WINTER_HOLIDAYS: {
                height = 17;
                c = 24;
            }
        }
        if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify) {
            c = 8;
            if (this.parent != null) {
                this.aura(0xFFFF00, 5);
            }
        }
        this.texture("sprites/ratking.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, height);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, c + 0, c + 0, c + 0, c + 1);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, c + 2, c + 3, c + 4, c + 5, c + 6);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, c + 0);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, c + 0);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify) {
            this.aura(0xFFFF00, 5);
        }
    }
}

