/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Grim
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (defender.isImmune(Grim.class)) {
            return damage;
        }
        int level = Math.max(0, weapon.buffedLvl());
        float maxChance = 0.5f + 0.05f * (float)level;
        Buff.affect((Char)attacker, GrimTracker.class).maxChance = maxChance *= this.procChanceMultiplier(attacker);
        if (attacker.buff(GrimTracker.class) != null && attacker instanceof Hero && weapon.hasEnchant(Grim.class, attacker)) {
            attacker.buff(GrimTracker.class).qualifiesForBadge = true;
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    public static class GrimTracker
    extends Buff {
        public float maxChance;
        public boolean qualifiesForBadge;

        public GrimTracker() {
            this.actPriority = 100;
        }

        @Override
        public boolean act() {
            this.detach();
            return true;
        }
    }
}

