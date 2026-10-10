/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;

public class MirrorSprite
extends MobSprite {
    private static final int FRAME_WIDTH = 12;
    private static final int FRAME_HEIGHT = 15;

    public MirrorSprite() {
        this.texture(Dungeon.hero != null ? Dungeon.hero.heroClass.spritesheet() : HeroClass.WARRIOR.spritesheet());
        this.updateArmor(0);
        this.idle();
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.updateArmor();
    }

    @Override
    public void bloodBurstA(PointF from, int damage) {
    }

    public void updateArmor() {
        this.updateArmor(((MirrorImage)this.ch).armTier);
    }

    public void updateArmor(int tier) {
        TextureFilm film = new TextureFilm(HeroSprite.tiers(), (Object)tier, 12, 15);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);
        this.run = new MovieClip.Animation(20, true);
        this.run.frames(film, 2, 3, 4, 5, 6, 7);
        this.die = new MovieClip.Animation(20, false);
        this.die.frames(film, 0);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(film, 13, 14, 15, 0);
        this.idle();
    }
}

