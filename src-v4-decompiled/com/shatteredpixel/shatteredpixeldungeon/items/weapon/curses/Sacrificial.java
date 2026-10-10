/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Sacrificial
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float procChance = 0.1f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float missingPercent = (float)attacker.HP / (float)attacker.HT;
            float bleedAmt = (float)(Math.pow(missingPercent, 2.0) * (double)attacker.HT) / 8.0f;
            if (Random.Float() < bleedAmt) {
                Buff.affect(attacker, Bleeding.class).set(Math.max(1.0f, bleedAmt), this.getClass());
            }
        }
        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }
}

