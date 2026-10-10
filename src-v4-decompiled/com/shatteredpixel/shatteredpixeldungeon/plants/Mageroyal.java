/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Mageroyal
extends Plant {
    public Mageroyal() {
        this.image = 7;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch != null) {
            PotionOfHealing.cure(ch);
            if (ch instanceof Hero) {
                GLog.i(Messages.get(this, "refreshed", new Object[0]), new Object[0]);
                if (((Hero)ch).subClass == HeroSubClass.WARDEN) {
                    Buff.affect(ch, BlobImmunity.class, 10.0f);
                }
            }
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_MAGEROYAL;
            this.plantClass = Mageroyal.class;
        }
    }
}

