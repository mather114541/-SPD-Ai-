/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingSpike
extends MissileWeapon {
    public ThrowingSpike() {
        this.image = ItemSpriteSheet.THROWING_SPIKE;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 1.2f;
        this.bones = false;
        this.baseUses = 12.0f;
        this.tier = 1;
    }
}

