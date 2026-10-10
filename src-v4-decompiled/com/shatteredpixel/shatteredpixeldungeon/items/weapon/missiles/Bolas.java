/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Bolas
extends MissileWeapon {
    public Bolas() {
        this.image = ItemSpriteSheet.BOLAS;
        this.hitSound = "sounds/hit.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 3;
        this.baseUses = 5.0f;
    }

    @Override
    public int min(int lvl) {
        return 2 * (this.tier - 1) + 0 * lvl;
    }

    @Override
    public int max(int lvl) {
        return 3 * this.tier + (this.tier - 1) * lvl;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.prolong(defender, Cripple.class, 5.0f);
        return super.proc(attacker, defender, damage);
    }
}

