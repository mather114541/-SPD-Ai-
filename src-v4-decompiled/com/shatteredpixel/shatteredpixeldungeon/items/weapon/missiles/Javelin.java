/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Javelin
extends MissileWeapon {
    public Javelin() {
        this.image = ItemSpriteSheet.JAVELIN;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 4;
    }
}

