/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ChaoticCenser;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.watabou.utils.Bundle;

public class Regeneration
extends Buff {
    private float partialRegen;
    private static final float REGENERATION_DELAY = 10.0f;
    public static final String PARTIAL_REGEN = "partial_regen";

    public Regeneration() {
        this.actPriority = -1;
        this.partialRegen = 0.0f;
    }

    @Override
    public boolean act() {
        if (this.target.isAlive()) {
            if (ChaoticCenser.averageTurnsUntilGas() != -1) {
                Buff.affect(Dungeon.hero, ChaoticCenser.CenserGasTracker.class);
            }
            if (Regeneration.regenOn() && this.target.HP < this.regencap() && !((Hero)this.target).isStarving()) {
                boolean chaliceCursed = false;
                int chaliceLevel = -1;
                if (this.target.buff(MagicImmune.class) == null) {
                    if (Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class) != null) {
                        chaliceCursed = Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class).isCursed();
                        chaliceLevel = Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class).itemLevel();
                    } else if (Dungeon.hero.buff(SpiritForm.SpiritFormBuff.class) != null && Dungeon.hero.buff(SpiritForm.SpiritFormBuff.class).artifact() instanceof ChaliceOfBlood) {
                        chaliceLevel = SpiritForm.artifactLevel();
                    }
                }
                float delay = 10.0f;
                if (chaliceLevel != -1 && this.target.buff(MagicImmune.class) == null) {
                    if (chaliceCursed) {
                        delay *= 1.5f;
                    } else {
                        delay -= 1.33f + (float)chaliceLevel * 0.667f;
                        delay /= RingOfEnergy.artifactChargeMultiplier(this.target);
                    }
                }
                if (this.target.buff(LockedFloor.class) == null) {
                    delay /= SaltCube.healthRegenMultiplier();
                }
                this.partialRegen += 1.0f / delay;
                if (this.partialRegen >= 1.0f) {
                    this.target.HP += (int)this.partialRegen;
                    this.partialRegen -= (float)((int)this.partialRegen);
                    if (this.target.HP >= this.regencap()) {
                        this.target.HP = this.regencap();
                        ((Hero)this.target).resting = false;
                    }
                }
            }
            this.spend(1.0f);
        } else {
            this.diactivate();
        }
        return true;
    }

    public int regencap() {
        return this.target.HT;
    }

    public static boolean regenOn() {
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        return lock == null || lock.regenOn();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PARTIAL_REGEN, this.partialRegen);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.partialRegen = bundle.getFloat(PARTIAL_REGEN);
    }
}

