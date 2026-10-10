/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class WondrousResin
extends Trinket {
    public static boolean forcePositive = false;

    public WondrousResin() {
        this.image = ItemSpriteSheet.WONDROUS_RESIN;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 10 + 5 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * WondrousResin.positiveCurseEffectChance(this.buffedLvl())), Messages.decimalFormat("#.##", 100.0f * WondrousResin.extraCurseEffectChance(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * WondrousResin.positiveCurseEffectChance(0)), Messages.decimalFormat("#.##", 100.0f * WondrousResin.extraCurseEffectChance(0)));
    }

    public static float positiveCurseEffectChance() {
        if (forcePositive) {
            return 1.0f;
        }
        return WondrousResin.positiveCurseEffectChance(WondrousResin.trinketLevel(WondrousResin.class));
    }

    public static float positiveCurseEffectChance(int level) {
        if (level >= 0) {
            return 0.25f + 0.25f * (float)level;
        }
        return 0.0f;
    }

    public static float extraCurseEffectChance() {
        return WondrousResin.extraCurseEffectChance(WondrousResin.trinketLevel(WondrousResin.class));
    }

    public static float extraCurseEffectChance(int level) {
        if (level >= 0) {
            return 0.125f + 0.125f * (float)level;
        }
        return 0.0f;
    }
}

