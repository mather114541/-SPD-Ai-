/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Rat
extends Mob {
    private static final String RAT_ALLY = "rat_ally";

    public Rat() {
        this.spriteClass = RatSprite.class;
        this.HT = 8;
        this.HP = 8;
        this.defenseSkill = 2;
        this.maxLvl = 5;
    }

    @Override
    protected boolean act() {
        if (this.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[this.pos] && Dungeon.hero.armorAbility instanceof Ratmogrify) {
            this.alignment = Char.Alignment.NEUTRAL;
            if (this.enemy == Dungeon.hero) {
                this.enemy = null;
            }
            if (this.state == this.SLEEPING) {
                this.state = this.WANDERING;
            }
        }
        return super.act();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int attackSkill(Char target) {
        return 8;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 1);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.alignment == Char.Alignment.ALLY) {
            bundle.put(RAT_ALLY, true);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(RAT_ALLY)) {
            this.alignment = Char.Alignment.ALLY;
        }
    }
}

