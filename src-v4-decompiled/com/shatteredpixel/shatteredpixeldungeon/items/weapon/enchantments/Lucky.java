/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Visual;
import com.watabou.utils.Random;

public class Lucky
extends Weapon.Enchantment {
    private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing(65280);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 4.0f) / ((float)level + 40.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Buff.affect((Char)defender, LuckProc.class).ringLevel = -10 + Math.round(5.0f * powerMulti);
        } else if (defender.buff(LuckProc.class) != null) {
            defender.buff(LuckProc.class).detach();
        }
        return damage;
    }

    public static Item genLoot() {
        return RingOfWealth.genConsumableDrop(-5);
    }

    public static void showFlare(Visual vis) {
        RingOfWealth.showFlareForBonusDrop(vis);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREEN;
    }

    public static class LuckProc
    extends Buff {
        private int ringLevel = -5;

        @Override
        public boolean act() {
            this.detach();
            return true;
        }

        public Item genLoot() {
            this.detach();
            return RingOfWealth.genConsumableDrop(this.ringLevel);
        }
    }
}

