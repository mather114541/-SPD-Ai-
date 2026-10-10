/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Thorns
extends Armor.Glyph {
    private static ItemSprite.Glowing RED = new ItemSprite.Glowing(0x660022);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        int level = Math.max(0, armor.buffedLvl());
        float procChance = ((float)level + 2.0f) / ((float)level + 12.0f) * this.procChanceMultiplier(defender);
        if (attacker.alignment != defender.alignment && Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Buff.affect(attacker, Bleeding.class).set(Math.round((float)(4 + level) * powerMulti));
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return RED;
    }
}

