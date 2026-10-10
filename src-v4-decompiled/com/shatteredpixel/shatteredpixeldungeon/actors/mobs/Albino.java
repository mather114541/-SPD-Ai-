/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AlbinoSprite;
import com.watabou.utils.Random;

public class Albino
extends Rat {
    public Albino() {
        this.spriteClass = AlbinoSprite.class;
        this.HT = 12;
        this.HP = 12;
        this.EXP = 2;
        this.loot = MysteryMeat.class;
        this.lootChance = 1.0f;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if ((damage = super.attackProc(enemy, damage)) > 0 && Random.Int(2) == 0) {
            Buff.affect(enemy, Bleeding.class).set(Random.NormalFloat(2.0f, 3.0f));
        }
        return damage;
    }
}

