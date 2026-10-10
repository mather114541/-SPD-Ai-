/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HealingDart
extends TippedDart {
    public HealingDart() {
        this.image = ItemSpriteSheet.HEALING_DART;
        this.usesTargeting = false;
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
        if (this.processingChargedShot && (defender == attacker || attacker.alignment != defender.alignment)) {
            return super.proc(attacker, defender, damage);
        }
        PotionOfHealing.cure(defender);
        Buff.affect(defender, Healing.class).setHeal((int)(0.5f * (float)defender.HT + 30.0f), 0.25f, 0);
        if (attacker.alignment == defender.alignment) {
            return 0;
        }
        return super.proc(attacker, defender, damage);
    }
}

