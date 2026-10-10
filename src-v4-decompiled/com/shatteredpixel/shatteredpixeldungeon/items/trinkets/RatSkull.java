/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RatSkull
extends Trinket {
    public RatSkull() {
        this.image = ItemSpriteSheet.RAT_SKULL;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", (int)RatSkull.exoticChanceMultiplier(this.buffedLvl()));
        }
        return Messages.get(this, "typical_stats_desc", (int)RatSkull.exoticChanceMultiplier(0));
    }

    public static float exoticChanceMultiplier() {
        return RatSkull.exoticChanceMultiplier(RatSkull.trinketLevel(RatSkull.class));
    }

    public static float exoticChanceMultiplier(int level) {
        if (level == -1) {
            return 1.0f;
        }
        return 2.0f + 1.0f * (float)level;
    }
}

