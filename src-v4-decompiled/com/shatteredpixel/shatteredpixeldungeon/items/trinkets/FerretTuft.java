/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FerretTuft
extends Trinket {
    public FerretTuft() {
        this.image = ItemSpriteSheet.FERRET_TUFT;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100.0f * (FerretTuft.evasionMultiplier(this.buffedLvl()) - 1.0f)));
        }
        return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100.0f * (FerretTuft.evasionMultiplier(0) - 1.0f)));
    }

    public static float evasionMultiplier() {
        return FerretTuft.evasionMultiplier(FerretTuft.trinketLevel(FerretTuft.class));
    }

    public static float evasionMultiplier(int level) {
        if (level <= -1) {
            return 1.0f;
        }
        return 1.0f + 0.125f * (float)(level + 1);
    }
}

