/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Elastic
extends Weapon.Enchantment {
    private static ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xFF00FF);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 5.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, 1);
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), 7);
            WandOfBlastWave.throwChar(defender, trajectory, Math.round(2.0f * powerMulti), !(weapon instanceof MissileWeapon) && !(weapon instanceof SpiritBow), true, this);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return PINK;
    }
}

