/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ParchmentScrap
extends Trinket {
    public ParchmentScrap() {
        this.image = ItemSpriteSheet.PARCHMENT_SCRAP;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 10 + 5 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", (int)ParchmentScrap.enchantChanceMultiplier(this.buffedLvl()), Messages.decimalFormat("#.##", ParchmentScrap.curseChanceMultiplier(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", (int)ParchmentScrap.enchantChanceMultiplier(0), Messages.decimalFormat("#.##", ParchmentScrap.curseChanceMultiplier(0)));
    }

    public static float enchantChanceMultiplier() {
        return ParchmentScrap.enchantChanceMultiplier(ParchmentScrap.trinketLevel(ParchmentScrap.class));
    }

    public static float enchantChanceMultiplier(int level) {
        switch (level) {
            default: {
                return 1.0f;
            }
            case 0: {
                return 2.0f;
            }
            case 1: {
                return 4.0f;
            }
            case 2: {
                return 7.0f;
            }
            case 3: 
        }
        return 10.0f;
    }

    public static float curseChanceMultiplier() {
        return ParchmentScrap.curseChanceMultiplier(ParchmentScrap.trinketLevel(ParchmentScrap.class));
    }

    public static float curseChanceMultiplier(int level) {
        switch (level) {
            default: {
                return 1.0f;
            }
            case 0: {
                return 1.5f;
            }
            case 1: {
                return 2.0f;
            }
            case 2: {
                return 1.0f;
            }
            case 3: 
        }
        return 0.0f;
    }
}

