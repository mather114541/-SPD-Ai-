/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Potential
extends Armor.Glyph {
    private static ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF, 0.6f);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        float powerMulti;
        int wands;
        int level = Math.max(0, armor.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 6.0f) * this.procChanceMultiplier(defender);
        if (Random.Float() < procChance && defender instanceof Hero && (wands = ((Hero)defender).belongings.charge(powerMulti = Math.max(1.0f, procChance))) > 0) {
            defender.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WHITE;
    }
}

