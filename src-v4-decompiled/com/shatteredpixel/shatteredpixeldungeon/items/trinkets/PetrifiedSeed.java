/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PetrifiedSeed
extends Trinket {
    public PetrifiedSeed() {
        this.image = ItemSpriteSheet.PETRIFIED_SEED;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * PetrifiedSeed.stoneInsteadOfSeedChance(this.buffedLvl())), Messages.decimalFormat("#.##", 100.0f * (PetrifiedSeed.grassLootMultiplier(this.buffedLvl()) - 1.0f)));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * PetrifiedSeed.stoneInsteadOfSeedChance(0)), Messages.decimalFormat("#.##", 100.0f * (PetrifiedSeed.grassLootMultiplier(0) - 1.0f)));
    }

    public static float grassLootMultiplier() {
        return PetrifiedSeed.grassLootMultiplier(PetrifiedSeed.trinketLevel(PetrifiedSeed.class));
    }

    public static float grassLootMultiplier(int level) {
        if (level <= 0) {
            return 1.0f;
        }
        return 1.0f + 0.25f * (float)level / 3.0f;
    }

    public static float stoneInsteadOfSeedChance() {
        return PetrifiedSeed.stoneInsteadOfSeedChance(PetrifiedSeed.trinketLevel(PetrifiedSeed.class));
    }

    public static float stoneInsteadOfSeedChance(int level) {
        switch (level) {
            default: {
                return 0.0f;
            }
            case 0: {
                return 0.25f;
            }
            case 1: {
                return 0.46f;
            }
            case 2: {
                return 0.65f;
            }
            case 3: 
        }
        return 0.8f;
    }
}

