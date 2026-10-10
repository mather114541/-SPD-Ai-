/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AdrenalineDart
extends TippedDart {
    public AdrenalineDart() {
        this.image = ItemSpriteSheet.ADRENALINE_DART;
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero && ((Hero)owner).attackTarget().alignment == owner.alignment) {
            return 0;
        }
        return super.damageRoll(owner);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (!this.processingChargedShot || defender != attacker) {
            if (attacker.alignment == defender.alignment) {
                Buff.prolong(defender, Adrenaline.class, 10.0f);
                return 0;
            }
            Buff.prolong(defender, Cripple.class, 5.0f);
        }
        return super.proc(attacker, defender, damage);
    }
}

