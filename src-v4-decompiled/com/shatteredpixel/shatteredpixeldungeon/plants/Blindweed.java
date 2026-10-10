/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Blindweed
extends Plant {
    public Blindweed() {
        this.image = 11;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch != null) {
            if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Invisibility.class, 10.0f);
            } else {
                Buff.prolong(ch, Blindness.class, 10.0f);
                Buff.prolong(ch, Cripple.class, 10.0f);
                if (ch instanceof Mob) {
                    Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
                    if (((Mob)ch).state == ((Mob)ch).HUNTING) {
                        ((Mob)ch).state = ((Mob)ch).WANDERING;
                    }
                    ((Mob)ch).beckon(Dungeon.level.randomDestination(ch));
                }
            }
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.get(this.pos).burst(Speck.factory(2), 4);
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_BLINDWEED;
            this.plantClass = Blindweed.class;
        }
    }
}

