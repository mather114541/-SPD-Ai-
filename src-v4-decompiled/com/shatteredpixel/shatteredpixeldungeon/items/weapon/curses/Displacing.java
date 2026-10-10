/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Displacing
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float procChance = 0.083333336f * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance && !defender.properties().contains((Object)Char.Property.IMMOVABLE)) {
            int oldpos = defender.pos;
            if (ScrollOfTeleportation.teleportChar(defender)) {
                if (Dungeon.level.heroFOV[oldpos]) {
                    CellEmitter.get(oldpos).start(Speck.factory(2), 0.2f, 3);
                }
                if (defender instanceof Mob && ((Mob)defender).state == ((Mob)defender).HUNTING) {
                    ((Mob)defender).state = ((Mob)defender).WANDERING;
                }
            }
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

