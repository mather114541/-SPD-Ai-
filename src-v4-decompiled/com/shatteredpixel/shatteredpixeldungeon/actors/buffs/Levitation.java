/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Image;

public class Levitation
extends FlavourBuff {
    public static final float DURATION = 20.0f;

    public Levitation() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.flying = true;
            Roots.detach(target, Roots.class);
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        this.target.flying = false;
        super.detach();
        if (ShatteredPixelDungeon.scene() instanceof GameScene) {
            Dungeon.level.occupyCell(this.target);
        }
    }

    public boolean detachesWithinDelay(float delay) {
        if (this.target.buff(Swiftthistle.TimeBubble.class) != null) {
            return false;
        }
        if (this.target.buff(TimekeepersHourglass.timeFreeze.class) != null) {
            return false;
        }
        return this.cooldown() < delay;
    }

    @Override
    public int icon() {
        return 1;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 2.1f, 2.5f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.LEVITATING);
        } else {
            this.target.sprite.remove(CharSprite.State.LEVITATING);
        }
    }
}

