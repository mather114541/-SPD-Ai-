/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MimicTooth
extends Trinket {
    public MimicTooth() {
        this.image = ItemSpriteSheet.MIMIC_TOOTH;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", MimicTooth.mimicChanceMultiplier(this.buffedLvl())), Messages.decimalFormat("#.##", 100.0f * MimicTooth.ebonyMimicChance(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", MimicTooth.mimicChanceMultiplier(0)), Messages.decimalFormat("#.##", 100.0f * MimicTooth.ebonyMimicChance(0)));
    }

    public static float mimicChanceMultiplier() {
        return MimicTooth.mimicChanceMultiplier(MimicTooth.trinketLevel(MimicTooth.class));
    }

    public static float mimicChanceMultiplier(int level) {
        if (level == -1) {
            return 1.0f;
        }
        return 1.5f + 0.5f * (float)level;
    }

    public static boolean stealthyMimics() {
        return MimicTooth.trinketLevel(MimicTooth.class) >= 0;
    }

    public static float ebonyMimicChance() {
        return MimicTooth.ebonyMimicChance(MimicTooth.trinketLevel(MimicTooth.class));
    }

    public static float ebonyMimicChance(int level) {
        if (level >= 0) {
            return 0.125f + 0.125f * (float)level;
        }
        return 0.0f;
    }
}

