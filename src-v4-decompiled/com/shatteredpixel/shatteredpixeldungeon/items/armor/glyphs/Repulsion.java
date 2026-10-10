/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Repulsion
extends Armor.Glyph {
    private static ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        int level = Math.max(0, armor.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 5.0f) * this.procChanceMultiplier(defender);
        if (Dungeon.level.adjacent(attacker.pos, defender.pos) && Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            int oppositeHero = attacker.pos + (attacker.pos - defender.pos);
            Ballistica trajectory = new Ballistica(attacker.pos, oppositeHero, 6);
            WandOfBlastWave.throwChar(attacker, trajectory, Math.round(2.0f * powerMulti), true, true, this);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WHITE;
    }
}

