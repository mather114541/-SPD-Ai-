/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Friendly
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (attacker.buff(Charm.class) != null && attacker.buff(Charm.class).object == defender.id()) {
            damage = 0;
        }
        float procChance = 0.1f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            Buff.affect((Char)attacker, Charm.class, (float)10.0f).object = defender.id();
            attacker.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 5);
            Charm c = Buff.affect(defender, Charm.class, 5.0f);
            c.ignoreNextHit = true;
            c.object = attacker.id();
            defender.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 5);
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

