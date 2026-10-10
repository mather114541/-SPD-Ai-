/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Bulk
extends Armor.Glyph {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        return damage;
    }

    public static float speedBoost(Char owner, int level) {
        if (level == -1 || Dungeon.level.map[owner.pos] != 5 && Dungeon.level.map[owner.pos] != 6) {
            return 1.0f;
        }
        if (owner.sprite != null) {
            owner.sprite.emitter().startDelayed(ShadowParticle.UP, 0.02f, 5, 0.05f);
        }
        return 0.33333334f * Bulk.genericProcChanceMultiplier(owner);
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

