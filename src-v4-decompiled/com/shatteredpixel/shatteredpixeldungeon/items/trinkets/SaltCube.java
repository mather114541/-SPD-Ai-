/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SaltCube
extends Trinket {
    public SaltCube() {
        this.image = ItemSpriteSheet.SALT_CUBE;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * (1.0f / SaltCube.hungerGainMultiplier(this.buffedLvl()) - 1.0f)), Messages.decimalFormat("#.##", 100.0f * (1.0f - SaltCube.healthRegenMultiplier(this.buffedLvl()))));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * (1.0f / SaltCube.hungerGainMultiplier(0) - 1.0f)), Messages.decimalFormat("#.##", 100.0f * (1.0f - SaltCube.healthRegenMultiplier(0))));
    }

    public static float hungerGainMultiplier() {
        return SaltCube.hungerGainMultiplier(SaltCube.trinketLevel(SaltCube.class));
    }

    public static float hungerGainMultiplier(int level) {
        if (level == -1) {
            return 1.0f;
        }
        return 1.0f / (1.0f + 0.25f * (float)(level + 1));
    }

    public static float healthRegenMultiplier() {
        return SaltCube.healthRegenMultiplier(SaltCube.trinketLevel(SaltCube.class));
    }

    public static float healthRegenMultiplier(int level) {
        switch (level) {
            default: {
                return 1.0f;
            }
            case 0: {
                return 0.84f;
            }
            case 1: {
                return 0.73f;
            }
            case 2: {
                return 0.66f;
            }
            case 3: 
        }
        return 0.6f;
    }
}

