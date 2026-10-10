/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class VialOfBlood
extends Trinket {
    public VialOfBlood() {
        this.image = ItemSpriteSheet.BLOOD_VIAL;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * (VialOfBlood.totalHealMultiplier(this.buffedLvl()) - 1.0f)), Integer.toString(VialOfBlood.maxHealPerTurn(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * (VialOfBlood.totalHealMultiplier(0) - 1.0f)), Integer.toString(VialOfBlood.maxHealPerTurn(0)));
    }

    public static boolean delayBurstHealing() {
        return VialOfBlood.trinketLevel(VialOfBlood.class) != -1;
    }

    public static int bloodVialLevel() {
        return VialOfBlood.trinketLevel(VialOfBlood.class);
    }

    public static float totalHealMultiplier() {
        return VialOfBlood.totalHealMultiplier(VialOfBlood.trinketLevel(VialOfBlood.class));
    }

    public static float totalHealMultiplier(int level) {
        if (level == -1) {
            return 1.0f;
        }
        return 1.0f + 0.125f * (float)(level + 1);
    }

    public static int maxHealPerTurn() {
        return VialOfBlood.maxHealPerTurn(VialOfBlood.trinketLevel(VialOfBlood.class));
    }

    public static int maxHealPerTurn(int level) {
        int maxHP;
        int n = maxHP = Dungeon.hero == null ? 20 : Dungeon.hero.HT;
        if (level == -1) {
            return maxHP;
        }
        switch (level) {
            default: {
                return 4 + Math.round(0.15f * (float)maxHP);
            }
            case 1: {
                return 3 + Math.round(0.1f * (float)maxHP);
            }
            case 2: {
                return 2 + Math.round(0.07f * (float)maxHP);
            }
            case 3: 
        }
        return 1 + Math.round(0.05f * (float)maxHP);
    }
}

