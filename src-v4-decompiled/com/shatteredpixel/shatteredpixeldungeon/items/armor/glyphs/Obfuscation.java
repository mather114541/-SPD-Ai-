/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Obfuscation
extends Armor.Glyph {
    private static ItemSprite.Glowing GREY = new ItemSprite.Glowing(0x888888);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        return damage;
    }

    public static float stealthBoost(Char owner, int level) {
        if (level == -1) {
            return 0.0f;
        }
        return (1.0f + (float)level / 3.0f) * Obfuscation.genericProcChanceMultiplier(owner);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREY;
    }
}

