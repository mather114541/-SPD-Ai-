/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Corrosion
extends Armor.Glyph {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        float procChance = 0.1f * this.procChanceMultiplier(defender);
        if (Random.Float() < procChance) {
            int pos = defender.pos;
            for (int i : PathFinder.NEIGHBOURS9) {
                Splash.at(pos + i, 0, 5);
                if (Actor.findChar(pos + i) == null) continue;
                Buff.affect(Actor.findChar(pos + i), Ooze.class).set(10.0f);
            }
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}

