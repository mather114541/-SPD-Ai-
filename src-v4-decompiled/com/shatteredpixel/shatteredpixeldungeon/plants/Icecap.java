/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;

public class Icecap
extends Plant {
    public Icecap() {
        this.image = 4;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, FrostImbue.class, 15.000001f);
        }
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[this.pos + i]) continue;
            Freezing.affect(this.pos + i);
            if (!(Actor.findChar(this.pos + i) instanceof Mob)) continue;
            Buff.prolong(Actor.findChar(this.pos + i), Trap.HazardAssistTracker.class, 50.0f);
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_ICECAP;
            this.plantClass = Icecap.class;
        }
    }
}

