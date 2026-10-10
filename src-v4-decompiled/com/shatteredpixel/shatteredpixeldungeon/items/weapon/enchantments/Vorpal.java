/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Vorpal
extends Weapon.Enchantment {
    private static ItemSprite.Glowing RED = new ItemSprite.Glowing(0xAA6666);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (defender.isImmune(Bleeding.class)) {
            return damage;
        }
        float procChance = 0.25f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti;
            Buff.affect((Char)attacker, VorpalTracker.class).powerMulti = powerMulti = Math.max(1.0f, procChance);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return RED;
    }

    public static class VorpalTracker
    extends Buff {
        public float powerMulti;

        public VorpalTracker() {
            this.actPriority = 100;
        }

        @Override
        public boolean act() {
            this.detach();
            return true;
        }
    }
}

