/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class FireImbue
extends Buff {
    public static final float DURATION = 50.0f;
    protected float left;
    private static final String LEFT = "left";

    public FireImbue() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
        this.immunities.add(Burning.class);
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
        if (Dungeon.level.map[this.target.pos] == 2) {
            Level.set(this.target.pos, 9);
            GameScene.updateMap(this.target.pos);
        }
        this.spend(1.0f);
        this.left -= 1.0f;
        if (this.left <= 0.0f) {
            this.detach();
        }
        return true;
    }

    public void proc(Char enemy) {
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }
        enemy.sprite.emitter().burst(FlameParticle.FACTORY, 2);
    }

    @Override
    public int icon() {
        return 55;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(2.0f, 0.75f, 0.0f);
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
            Buff.detach(target, Burning.class);
            return true;
        }
        return false;
    }
}

