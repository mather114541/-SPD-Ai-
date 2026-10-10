/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Stormvine
extends Plant {
    public Stormvine() {
        this.image = 5;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch != null) {
            if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Levitation.class, 10.0f);
            } else {
                if (ch instanceof Mob) {
                    Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
                }
                Buff.affect(ch, Vertigo.class, 10.0f);
            }
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_STORMVINE;
            this.plantClass = Stormvine.class;
        }
    }
}

