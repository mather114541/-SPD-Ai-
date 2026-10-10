/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FishingSpear
extends MissileWeapon {
    public FishingSpear() {
        this.image = ItemSpriteSheet.FISHING_SPEAR;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 1.1f;
        this.tier = 2;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender instanceof Piranha) {
            damage = Math.max(damage, defender.HP / 2);
        }
        return super.proc(attacker, defender, damage);
    }
}

