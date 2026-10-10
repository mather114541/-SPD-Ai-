/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Starflower
extends Plant {
    public Starflower() {
        this.image = 9;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch != null) {
            Buff.prolong(ch, Bless.class, 30.0f);
            if (Dungeon.level.heroFOV[ch.pos]) {
                new Flare(6, 32.0f).color(0xFFFF00, true).show(ch.sprite, 2.0f);
            }
            if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
                Buff.prolong(ch, Recharging.class, 30.0f);
                SpellSprite.show(ch, 2);
            }
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_STARFLOWER;
            this.plantClass = Starflower.class;
        }

        @Override
        public int value() {
            return 30 * this.quantity;
        }

        @Override
        public int energyVal() {
            return 3 * this.quantity;
        }
    }
}

