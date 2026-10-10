/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ExoticCrystals
extends Trinket {
    public ExoticCrystals() {
        this.image = ItemSpriteSheet.EXOTIC_CRYSTALS;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * ExoticCrystals.consumableExoticChance(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * ExoticCrystals.consumableExoticChance(0)));
    }

    public static float consumableExoticChance() {
        return ExoticCrystals.consumableExoticChance(ExoticCrystals.trinketLevel(ExoticCrystals.class));
    }

    public static float consumableExoticChance(int level) {
        if (level == -1) {
            return 0.0f;
        }
        return 0.2f + 0.2f * (float)level;
    }
}

