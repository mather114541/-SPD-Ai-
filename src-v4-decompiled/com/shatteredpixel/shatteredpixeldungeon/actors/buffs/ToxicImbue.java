/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class ToxicImbue
extends Buff {
    public static final float DURATION = 50.0f;
    protected float left;
    private static final String LEFT = "left";

    public ToxicImbue() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
        this.immunities.add(ToxicGas.class);
        this.immunities.add(Poison.class);
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

    public void set(float duration) {
        this.left = duration;
    }

    public void extend(float duration) {
        this.left += duration;
    }

    @Override
    public boolean act() {
        if (this.left > 0.0f) {
            int centerVolume = 6;
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.solid[this.target.pos + i]) {
                    GameScene.add(Blob.seed(this.target.pos + i, 6, ToxicGas.class));
                    continue;
                }
                centerVolume += 6;
            }
            GameScene.add(Blob.seed(this.target.pos, centerVolume, ToxicGas.class));
        }
        this.spend(1.0f);
        this.left -= 1.0f;
        if (this.left <= -5.0f) {
            this.detach();
        }
        return true;
    }

    @Override
    public int icon() {
        return this.left > 0.0f ? 55 : 127;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 1.5f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (50.0f - this.left) / 50.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns(this.left));
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            Buff.detach(target, Poison.class);
            return true;
        }
        return false;
    }
}

