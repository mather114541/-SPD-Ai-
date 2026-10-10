/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class ThirteenLeafClover
extends Trinket {
    private static float MAX_CHANCE = 0.6f;

    public ThirteenLeafClover() {
        this.image = ItemSpriteSheet.CLOVER;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Math.round(MAX_CHANCE * 100.0f * ThirteenLeafClover.alterHeroDamageChance(this.buffedLvl())), Math.round((1.0f - MAX_CHANCE) * 100.0f * ThirteenLeafClover.alterHeroDamageChance(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", Math.round(MAX_CHANCE * 100.0f * ThirteenLeafClover.alterHeroDamageChance(0)), Math.round((1.0f - MAX_CHANCE) * 100.0f * ThirteenLeafClover.alterHeroDamageChance(0)));
    }

    public static float alterHeroDamageChance() {
        return ThirteenLeafClover.alterHeroDamageChance(ThirteenLeafClover.trinketLevel(ThirteenLeafClover.class));
    }

    public static float alterHeroDamageChance(int level) {
        if (level <= -1) {
            return 0.0f;
        }
        return 0.25f + 0.25f * (float)level;
    }

    public static int alterDamageRoll(int min, int max) {
        if (Random.Float() < MAX_CHANCE) {
            return max;
        }
        return min;
    }
}

