/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Venomous
extends Weapon.Enchantment {
    private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x4400AA);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = 0.33333334f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Poison poison = defender.buff(Poison.class);
            if (poison == null) {
                poison = Buff.affect(defender, Poison.class);
                poison.delay(3.0f);
                poison = defender.buff(Poison.class);
            }
            if (poison != null) {
                poison.extend(powerMulti * ((float)level / 2.0f + 3.0f));
            }
            CellEmitter.center(defender.pos).burst(PoisonParticle.SPLASH, 5);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return PURPLE;
    }
}

