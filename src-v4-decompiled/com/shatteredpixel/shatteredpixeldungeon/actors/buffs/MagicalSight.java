/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Image;

public class MagicalSight
extends FlavourBuff {
    public static final float DURATION = 50.0f;
    public static final int DISTANCE = 12;

    public MagicalSight() {
        this.type = Buff.buffType.POSITIVE;
        this.immunities.add(Blindness.class);
    }

    @Override
    public int icon() {
        return 0;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 1.67f, 1.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (50.0f - this.visualcooldown()) / 50.0f);
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            Buff.detach(target, Blindness.class);
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        super.detach();
        Dungeon.observe();
        GameScene.updateFog();
    }
}

