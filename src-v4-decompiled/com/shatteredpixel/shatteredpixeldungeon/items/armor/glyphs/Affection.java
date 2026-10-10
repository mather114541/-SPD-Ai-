/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Affection
extends Armor.Glyph {
    private static ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xFF4488);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        int level = Math.max(0, armor.buffedLvl());
        float procChance = ((float)level + 3.0f) / ((float)level + 20.0f) * this.procChanceMultiplier(defender);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Buff.affect((Char)attacker, Charm.class, (float)((float)Math.round((float)(10.0f * powerMulti)))).object = defender.id();
            attacker.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 5);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return PINK;
    }
}

