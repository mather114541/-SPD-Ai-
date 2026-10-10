/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Pressurized
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float procChance = 0.125f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            GeyserTrap geyser = new GeyserTrap();
            geyser.pos = defender.pos;
            if (weapon instanceof MeleeWeapon) {
                Ballistica aim = new Ballistica(attacker.pos, defender.pos, 1);
                if (aim.path.size() > aim.dist + 1) {
                    geyser.centerKnockBackDirection = aim.path.get(aim.dist + 1);
                }
            }
            geyser.source = this;
            geyser.activate();
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

