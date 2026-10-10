/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Vampiric
extends Weapon.Enchantment {
    private static ItemSprite.Glowing RED = new ItemSprite.Glowing(0x660022);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float missingPercent = (float)(attacker.HT - attacker.HP) / (float)attacker.HT;
        float healChance = 0.05f + 0.25f * missingPercent;
        healChance *= this.procChanceMultiplier(attacker);
        if (Random.Float() < healChance && attacker.alignment != defender.alignment && (defender.alignment != Char.Alignment.NEUTRAL || defender instanceof Mimic)) {
            float powerMulti = Math.max(1.0f, healChance);
            int healAmt = Math.round((float)damage * 0.5f * powerMulti);
            if ((healAmt = Math.min(healAmt, attacker.HT - attacker.HP)) > 0 && attacker.isAlive()) {
                attacker.HP += healAmt;
                attacker.sprite.showStatusWithIcon(65280, Integer.toString(healAmt), FloatingText.HEALING, new Object[0]);
            }
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return RED;
    }
}

