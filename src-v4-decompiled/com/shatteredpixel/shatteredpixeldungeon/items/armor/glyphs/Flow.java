/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Flow
extends Armor.Glyph {
    private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing(255);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        return damage;
    }

    public static float speedBoost(Char owner, int level) {
        if (level == -1 || !Dungeon.level.water[owner.pos]) {
            return 1.0f;
        }
        if (owner.sprite != null && owner.sprite.visible) {
            int particles = 2 + (int)Random.Float(1.0f + (float)level / 2.0f);
            owner.sprite.emitter().startDelayed(Speck.factory(121), 0.02f, particles, 0.05f);
        }
        return (2.0f + 0.5f * (float)level) * Flow.genericProcChanceMultiplier(owner);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLUE;
    }
}

