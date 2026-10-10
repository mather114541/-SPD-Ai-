/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Chilling
extends Weapon.Enchantment {
    private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing(65535);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 4.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            float durationToAdd = 3.0f * powerMulti;
            Chill existing = defender.buff(Chill.class);
            if (existing != null) {
                durationToAdd = Math.min(durationToAdd, 6.0f * powerMulti - existing.cooldown());
            }
            if (durationToAdd > 0.0f) {
                Buff.affect(defender, Chill.class, durationToAdd);
            }
            Splash.at(defender.sprite.center(), -5056769, 5);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return TEAL;
    }
}

