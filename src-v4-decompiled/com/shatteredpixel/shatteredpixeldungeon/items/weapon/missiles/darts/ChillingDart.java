/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ChillingDart
extends TippedDart {
    public ChillingDart() {
        this.image = ItemSpriteSheet.CHILLING_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (!this.processingChargedShot || attacker.alignment != defender.alignment) {
            if (Dungeon.level.water[defender.pos]) {
                Buff.prolong(defender, Chill.class, 10.0f);
            } else {
                Buff.prolong(defender, Chill.class, 6.0f);
            }
        }
        return super.proc(attacker, defender, damage);
    }
}

