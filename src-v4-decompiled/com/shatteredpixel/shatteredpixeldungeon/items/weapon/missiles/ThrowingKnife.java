/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingKnife
extends MissileWeapon {
    public ThrowingKnife() {
        this.image = ItemSpriteSheet.THROWING_KNIFE;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.2f;
        this.bones = false;
        this.tier = 1;
        this.baseUses = 5.0f;
    }

    @Override
    public int max(int lvl) {
        return 6 * this.tier + (this.tier == 1 ? 2 * lvl : this.tier * lvl);
    }

    @Override
    public int damageRoll(Char owner) {
        Hero hero;
        Char enemy;
        if (owner instanceof Hero && (enemy = (hero = (Hero)owner).attackTarget()) instanceof Mob && ((Mob)enemy).surprisedBy(hero)) {
            int diff = this.max() - this.min();
            int damage = this.augment.damageFactor(Hero.heroDamageIntRange(this.min() + Math.round((float)diff * 0.75f), this.max()));
            int exStr = hero.STR() - this.STRReq();
            if (exStr > 0) {
                damage += Hero.heroDamageIntRange(0, exStr);
            }
            return damage;
        }
        return super.damageRoll(owner);
    }
}

