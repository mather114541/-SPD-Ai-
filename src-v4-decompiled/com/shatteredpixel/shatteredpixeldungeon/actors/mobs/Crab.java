/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrabSprite;
import com.watabou.utils.Random;

public class Crab
extends Mob {
    public Crab() {
        this.spriteClass = CrabSprite.class;
        this.HT = 15;
        this.HP = 15;
        this.defenseSkill = 5;
        this.baseSpeed = 2.0f;
        this.EXP = 4;
        this.maxLvl = 9;
        this.loot = MysteryMeat.class;
        this.lootChance = 0.167f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 7);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 4);
    }
}

