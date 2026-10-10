/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ParalyticDart
extends TippedDart {
    public ParalyticDart() {
        this.image = ItemSpriteSheet.PARALYTIC_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (!this.processingChargedShot || attacker.alignment != defender.alignment) {
            Buff.prolong(defender, Paralysis.class, 5.0f);
        }
        return super.proc(attacker, defender, damage);
    }
}

