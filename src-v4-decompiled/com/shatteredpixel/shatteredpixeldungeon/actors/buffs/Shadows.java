/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Shadows
extends Invisibility {
    protected float left;
    private static final String LEFT = "left";

    public Shadows() {
        this.announced = false;
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getFloat(LEFT);
    }

    @Override
    public boolean attachTo(Char target) {
        if (Dungeon.level != null) {
            for (Mob m : Dungeon.level.mobs) {
                if (!Dungeon.level.adjacent(m.pos, target.pos) || m.alignment == target.alignment) continue;
                return false;
            }
        }
        if (super.attachTo(target)) {
            if (Dungeon.level != null) {
                Sample.INSTANCE.play("sounds/meld.mp3");
                Dungeon.observe();
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        super.detach();
        Dungeon.observe();
    }

    @Override
    public boolean act() {
        if (this.target.isAlive()) {
            float f;
            this.spend(1.0f);
            this.left -= 1.0f;
            if (f <= 0.0f) {
                this.detach();
                return true;
            }
            for (Mob m : Dungeon.level.mobs) {
                if (!Dungeon.level.adjacent(m.pos, this.target.pos) || m.alignment == this.target.alignment) continue;
                this.detach();
                return true;
            }
        } else {
            this.detach();
        }
        return true;
    }

    public void prolong() {
        this.left = 2.0f;
    }

    @Override
    public int icon() {
        return 13;
    }

    @Override
    public float iconFadePercent() {
        return 0.0f;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

