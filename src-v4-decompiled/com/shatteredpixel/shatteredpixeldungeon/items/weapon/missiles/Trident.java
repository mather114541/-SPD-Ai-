/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Trident
extends MissileWeapon {
    public Trident() {
        this.image = ItemSpriteSheet.TRIDENT;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 0.9f;
        this.tier = 5;
    }
}

