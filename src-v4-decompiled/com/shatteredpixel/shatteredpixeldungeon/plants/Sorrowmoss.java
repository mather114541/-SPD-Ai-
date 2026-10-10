/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Sorrowmoss
extends Plant {
    public Sorrowmoss() {
        this.image = 6;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, ToxicImbue.class).set(15.000001f);
        }
        if (ch != null) {
            if (ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
            }
            Buff.affect(ch, Poison.class).set(5 + Math.round((float)(2 * Dungeon.scalingDepth()) / 3.0f));
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.center(this.pos).burst(PoisonParticle.SPLASH, 3);
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_SORROWMOSS;
            this.plantClass = Sorrowmoss.class;
        }
    }
}

