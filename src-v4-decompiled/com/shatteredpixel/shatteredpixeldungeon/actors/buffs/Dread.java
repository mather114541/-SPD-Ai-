/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Dread
extends Buff {
    protected int left = 20;
    public int object = 0;
    public static final float DURATION = 20.0f;
    private static final String LEFT = "left";
    private static final String OBJECT = "object";

    public Dread() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
        this.immunities.add(Terror.class);
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            Buff.detach(target, Terror.class);
            return true;
        }
        return false;
    }

    @Override
    public boolean act() {
        if (!Dungeon.level.heroFOV[this.target.pos] && Dungeon.level.distance(this.target.pos, Dungeon.hero.pos) >= 6) {
            if (this.target instanceof Mob) {
                ((Mob)this.target).EXP /= 2;
            }
            this.target.destroy();
            this.target.sprite.killAndErase();
            Dungeon.level.mobs.remove(this.target);
        } else {
            --this.left;
            if (this.left <= 0) {
                this.detach();
            }
        }
        this.spend(1.0f);
        return true;
    }

    public void extend(float duration) {
        this.left = (int)((float)this.left + duration);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
        bundle.put(OBJECT, this.object);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.object = bundle.getInt(OBJECT);
        this.left = bundle.getInt(LEFT);
    }

    @Override
    public int icon() {
        return 10;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - (float)this.left) / 20.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.left);
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.0f, 0.0f);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.left);
    }

    public void recover() {
        this.left -= 5;
        if (this.left <= 0) {
            this.detach();
        }
    }
}

