/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingClub
extends MissileWeapon {
    public ThrowingClub() {
        this.image = ItemSpriteSheet.THROWING_CLUB;
        this.hitSound = "sounds/hit_crush.mp3";
        this.hitSoundPitch = 1.1f;
        this.tier = 2;
        this.baseUses = 12.0f;
        this.sticky = false;
    }

    @Override
    public float pickupDelay() {
        return 0.0f;
    }

    @Override
    public int max(int lvl) {
        return 4 * this.tier + this.tier * lvl;
    }
}

