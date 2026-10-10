/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Eldritch;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Venomous;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vorpal;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Unstable
extends Weapon.Enchantment {
    private static ItemSprite.Glowing GREY = new ItemSprite.Glowing(0x999999);
    private static Class<? extends Weapon.Enchantment>[] randomEnchants = new Class[]{Blazing.class, Blocking.class, Blooming.class, Chilling.class, Kinetic.class, Corrupting.class, Elastic.class, Grim.class, Lucky.class, Shocking.class, Vampiric.class, Venomous.class, Vorpal.class, Eldritch.class};

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int conservedDamage = 0;
        if (attacker.buff(Kinetic.ConservedDamage.class) != null) {
            conservedDamage = attacker.buff(Kinetic.ConservedDamage.class).damageBonus();
            attacker.buff(Kinetic.ConservedDamage.class).detach();
        }
        damage = Reflection.newInstance(Random.oneOf(randomEnchants)).proc(weapon, attacker, defender, damage);
        return damage + conservedDamage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREY;
    }
}

