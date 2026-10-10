/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class EyeOfNewt
extends Trinket {
    public EyeOfNewt() {
        this.image = ItemSpriteSheet.EYE_OF_NEWT;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * (1.0f - EyeOfNewt.visionRangeMultiplier(this.buffedLvl()))), EyeOfNewt.mindVisionRange(this.buffedLvl()));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * (1.0f - EyeOfNewt.visionRangeMultiplier(0))), EyeOfNewt.mindVisionRange(0));
    }

    public static float visionRangeMultiplier() {
        return EyeOfNewt.visionRangeMultiplier(EyeOfNewt.trinketLevel(EyeOfNewt.class));
    }

    public static float visionRangeMultiplier(int level) {
        if (level < 0) {
            return 1.0f;
        }
        return 0.875f - 0.125f * (float)level;
    }

    public static int mindVisionRange() {
        return EyeOfNewt.mindVisionRange(EyeOfNewt.trinketLevel(EyeOfNewt.class));
    }

    public static int mindVisionRange(int level) {
        if (level < 0) {
            return 0;
        }
        return 2 + level;
    }
}

