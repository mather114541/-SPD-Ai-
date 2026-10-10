/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingStone
extends MissileWeapon {
    public ThrowingStone() {
        this.image = ItemSpriteSheet.THROWING_STONE;
        this.hitSound = "sounds/hit.mp3";
        this.hitSoundPitch = 1.1f;
        this.bones = false;
        this.tier = 1;
        this.baseUses = 5.0f;
        this.sticky = false;
    }

    @Override
    public int value() {
        return Math.round((float)super.value() / 2.0f);
    }
}

