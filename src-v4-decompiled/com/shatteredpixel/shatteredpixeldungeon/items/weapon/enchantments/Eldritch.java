/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Eldritch
extends Weapon.Enchantment {
    private static ItemSprite.Glowing GREY = new ItemSprite.Glowing(0x222222);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 5.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            for (Char ch : Actor.chars()) {
                if (ch == attacker || ch == defender || ch.alignment == attacker.alignment || ch.fieldOfView == null || !ch.fieldOfView[attacker.pos] && !ch.fieldOfView[defender.pos]) continue;
                if (ch == Dungeon.hero) {
                    Buff.affect(defender, Vertigo.class, 5.0f);
                    continue;
                }
                Buff.affect((Char)ch, Terror.class, (float)(powerMulti * 5.0f)).object = attacker.id();
            }
            new Flare(5, 24.0f).color(0xFF0000, true).show(attacker.sprite, 1.0f);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREY;
    }
}

