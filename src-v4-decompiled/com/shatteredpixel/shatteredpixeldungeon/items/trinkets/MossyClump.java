/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class MossyClump
extends Trinket {
    private ArrayList<Boolean> levelFeels;
    private int shuffles;
    private static final String FEELS = "feels";
    private static final String SHUFFLES = "shuffles";

    public MossyClump() {
        this.image = ItemSpriteSheet.MOSSY_CLUMP;
        this.levelFeels = new ArrayList();
        this.shuffles = 0;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 10 + 5 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", (int)(100.0f * MossyClump.overrideNormalLevelChance(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", (int)(100.0f * MossyClump.overrideNormalLevelChance(0)));
    }

    public static float overrideNormalLevelChance() {
        return MossyClump.overrideNormalLevelChance(MossyClump.trinketLevel(MossyClump.class));
    }

    public static float overrideNormalLevelChance(int level) {
        if (level == -1) {
            return 0.0f;
        }
        return 0.25f + 0.25f * (float)level;
    }

    public static Level.Feeling getNextFeeling() {
        MossyClump clump = Dungeon.hero.belongings.getItem(MossyClump.class);
        if (clump == null) {
            return Level.Feeling.NONE;
        }
        if (clump.levelFeels.isEmpty()) {
            Random.pushGenerator(Dungeon.seed + 1L);
            clump.levelFeels.add(true);
            clump.levelFeels.add(true);
            clump.levelFeels.add(false);
            clump.levelFeels.add(false);
            clump.levelFeels.add(false);
            clump.levelFeels.add(false);
            for (int i = 0; i <= clump.shuffles; ++i) {
                Random.shuffle(clump.levelFeels);
            }
            ++clump.shuffles;
            Random.popGenerator();
        }
        return clump.levelFeels.remove(0) != false ? Level.Feeling.GRASS : Level.Feeling.WATER;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (!this.levelFeels.isEmpty()) {
            boolean[] storeFeels = new boolean[this.levelFeels.size()];
            for (int i = 0; i < storeFeels.length; ++i) {
                storeFeels[i] = this.levelFeels.get(i);
            }
            bundle.put(FEELS, storeFeels);
        }
        bundle.put(SHUFFLES, this.shuffles);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.levelFeels.clear();
        if (bundle.contains(FEELS)) {
            for (boolean b : bundle.getBooleanArray(FEELS)) {
                this.levelFeels.add(b);
            }
        }
        this.shuffles = bundle.getInt(SHUFFLES);
    }
}

