/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;

public class Shuriken
extends MissileWeapon {
    public Shuriken() {
        this.image = ItemSpriteSheet.SHURIKEN;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 1.2f;
        this.tier = 2;
        this.baseUses = 5.0f;
    }

    @Override
    public int max(int lvl) {
        return 4 * this.tier + (this.tier == 1 ? 2 * lvl : this.tier * lvl);
    }

    @Override
    protected void onThrow(int cell) {
        super.onThrow(cell);
        if (curUser.buff(ShurikenInstantTracker.class) == null) {
            FlavourBuff.affect(curUser, ShurikenInstantTracker.class, ShurikenInstantTracker.DURATION - 1);
        }
    }

    @Override
    public float castDelay(Char user, int cell) {
        return user.buff(ShurikenInstantTracker.class) != null ? super.castDelay(user, cell) : 0.0f;
    }

    public static class ShurikenInstantTracker
    extends FlavourBuff {
        public static int DURATION = 20;

        @Override
        public int icon() {
            return 85;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.6f, 0.6f, 0.6f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, ((float)DURATION - this.visualcooldown()) / (float)DURATION);
        }
    }
}

