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

public class TrapMechanism
extends Trinket {
    private ArrayList<Boolean> levelFeels;
    private int shuffles;
    private static final String FEELS = "feels";
    private static final String SHUFFLES = "shuffles";

    public TrapMechanism() {
        this.image = ItemSpriteSheet.TRAP_MECHANISM;
        this.levelFeels = new ArrayList();
        this.shuffles = 0;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", (int)(100.0f * TrapMechanism.overrideNormalLevelChance(this.buffedLvl())), (int)(100.0f * TrapMechanism.revealHiddenTrapChance(this.buffedLvl())));
        }
        return Messages.get(this, "typical_stats_desc", (int)(100.0f * TrapMechanism.overrideNormalLevelChance(0)), (int)(100.0f * TrapMechanism.revealHiddenTrapChance(0)));
    }

    public static float overrideNormalLevelChance() {
        return TrapMechanism.overrideNormalLevelChance(TrapMechanism.trinketLevel(TrapMechanism.class));
    }

    public static float overrideNormalLevelChance(int level) {
        if (level == -1) {
            return 0.0f;
        }
        return 0.25f + 0.25f * (float)level;
    }

    public static float revealHiddenTrapChance() {
        return TrapMechanism.revealHiddenTrapChance(TrapMechanism.trinketLevel(TrapMechanism.class));
    }

    public static float revealHiddenTrapChance(int level) {
        if (level == -1) {
            return 0.0f;
        }
        return 0.1f + 0.1f * (float)level;
    }

    public static Level.Feeling getNextFeeling() {
        TrapMechanism mech = Dungeon.hero.belongings.getItem(TrapMechanism.class);
        if (mech == null) {
            return Level.Feeling.NONE;
        }
        if (mech.levelFeels.isEmpty()) {
            Random.pushGenerator(Dungeon.seed + 1L);
            mech.levelFeels.add(true);
            mech.levelFeels.add(true);
            mech.levelFeels.add(true);
            mech.levelFeels.add(false);
            mech.levelFeels.add(false);
            mech.levelFeels.add(false);
            for (int i = 0; i <= mech.shuffles; ++i) {
                Random.shuffle(mech.levelFeels);
            }
            ++mech.shuffles;
            Random.popGenerator();
        }
        return mech.levelFeels.remove(0) != false ? Level.Feeling.TRAPS : Level.Feeling.CHASM;
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

