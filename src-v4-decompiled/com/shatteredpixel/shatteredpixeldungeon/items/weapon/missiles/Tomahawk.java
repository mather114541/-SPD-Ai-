/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Tomahawk
extends MissileWeapon {
    public Tomahawk() {
        this.image = ItemSpriteSheet.TOMAHAWK;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 0.9f;
        this.tier = 4;
        this.baseUses = 5.0f;
    }

    @Override
    public int min(int lvl) {
        return Math.round(1.5f * (float)this.tier) + lvl;
    }

    @Override
    public int max(int lvl) {
        return Math.round(4.0f * (float)this.tier) + (this.tier - 1) * lvl;
    }

    public float minBleed() {
        return this.minBleed(this.buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero));
    }

    public float minBleed(int lvl) {
        return 3.0f + (float)lvl / 2.0f;
    }

    public float maxBleed() {
        return this.maxBleed(this.buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero));
    }

    public float maxBleed(int lvl) {
        return 6 + lvl;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect(defender, Bleeding.class).set(this.augment.damageFactor(Random.NormalFloat(this.minBleed(), this.maxBleed())));
        return super.proc(attacker, defender, damage);
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", Math.round(this.augment.damageFactor(this.minBleed())), Math.round(this.augment.damageFactor(this.maxBleed())));
        }
        return Messages.get(this, "typical_stats_desc", Math.round(this.augment.damageFactor(this.minBleed(0))), Math.round(this.augment.damageFactor(this.maxBleed(0))));
    }
}

