/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Wondrous
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float procChance = 0.125f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            boolean positiveOnly = Random.Float() < WondrousResin.positiveCurseEffectChance();
            Ballistica aim = new Ballistica(attacker.pos, defender.pos, 1);
            defender.sprite.emitter().burst(RainbowParticle.BURST, 25);
            CursedWand.randomValidEffect(weapon, attacker, aim, positiveOnly).effect(weapon, attacker, aim, positiveOnly);
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

