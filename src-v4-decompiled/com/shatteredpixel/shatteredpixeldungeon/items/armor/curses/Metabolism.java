/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Metabolism
extends Armor.Glyph {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        Hunger hunger;
        int healing;
        float procChance = 0.16666667f * this.procChanceMultiplier(defender);
        if (Random.Float() < procChance && defender instanceof Hero && (healing = Math.min(4, defender.HT - defender.HP)) > 0 && !(hunger = Buff.affect(defender, Hunger.class)).isStarving()) {
            hunger.affectHunger(healing * -10);
            defender.HP += healing;
            defender.sprite.showStatusWithIcon(65280, Integer.toString(healing), FloatingText.HEALING, new Object[0]);
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

