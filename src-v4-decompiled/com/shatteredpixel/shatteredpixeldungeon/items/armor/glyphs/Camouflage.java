/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;

public class Camouflage
extends Armor.Glyph {
    private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x448822);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        return damage;
    }

    public static void activate(Char ch, int level) {
        if (level == -1) {
            return;
        }
        Buff.prolong(ch, Invisibility.class, Math.round((3.0f + (float)level / 2.0f) * Camouflage.genericProcChanceMultiplier(ch)));
        if (Dungeon.level.heroFOV[ch.pos]) {
            Sample.INSTANCE.play("sounds/meld.mp3");
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREEN;
    }
}

