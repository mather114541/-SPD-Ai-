/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class CrackedSpyglass
extends Trinket {
    public CrackedSpyglass() {
        this.image = ItemSpriteSheet.SPYGLASS;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            if (this.buffedLvl() >= 2) {
                return Messages.get(this, "stats_desc_upgraded", Messages.decimalFormat("#.##", 100.0f * (CrackedSpyglass.extraLootChance(this.buffedLvl()) - 1.0f)));
            }
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * CrackedSpyglass.extraLootChance(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * CrackedSpyglass.extraLootChance(0)));
    }

    public static float extraLootChance() {
        return CrackedSpyglass.extraLootChance(CrackedSpyglass.trinketLevel(CrackedSpyglass.class));
    }

    public static float extraLootChance(int level) {
        if (level <= -1) {
            return 0.0f;
        }
        return 0.375f * (float)(level + 1);
    }
}

