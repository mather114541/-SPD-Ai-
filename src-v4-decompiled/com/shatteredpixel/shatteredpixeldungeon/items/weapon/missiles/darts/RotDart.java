/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RotDart
extends TippedDart {
    public RotDart() {
        this.image = ItemSpriteSheet.ROT_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (!this.processingChargedShot || attacker.alignment != defender.alignment) {
            if (defender.properties().contains((Object)Char.Property.BOSS) || defender.properties().contains((Object)Char.Property.MINIBOSS)) {
                Buff.affect(defender, Corrosion.class).set(5.0f, Dungeon.scalingDepth() / 3);
            } else {
                Buff.affect(defender, Corrosion.class).set(10.0f, Dungeon.scalingDepth());
            }
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public float durabilityPerUse(int level) {
        return 20.0f;
    }
}

