/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public class Barkskin
extends Buff {
    private int level;
    private int interval;
    private static final String LEVEL = "level";
    private static final String INTERVAL = "interval";

    public Barkskin() {
        this.type = Buff.buffType.POSITIVE;
        this.level = 0;
        this.interval = 1;
    }

    @Override
    public boolean act() {
        if (this.target.isAlive()) {
            this.spend(this.interval);
            if (--this.level <= 0) {
                this.detach();
            }
        } else {
            this.detach();
        }
        return true;
    }

    public int level() {
        return this.level;
    }

    public void set(int value, int time) {
        if (this.level <= value) {
            this.level = value;
            this.interval = time;
            this.spend((float)time - this.cooldown() - 1.0f);
        }
    }

    public void delay(float value) {
        this.spend(value);
    }

    @Override
    public int icon() {
        return 24;
    }

    @Override
    public float iconFadePercent() {
        if (this.target instanceof Hero) {
            float max = ((Hero)this.target).lvl * ((Hero)this.target).pointsInTalent(Talent.BARKSKIN) / 2;
            max = Math.max(max, (float)(2 + ((Hero)this.target).lvl / 3));
            return Math.max(0.0f, (max - (float)this.level) / max);
        }
        return 0.0f;
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.level);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.level, this.dispTurns(this.visualcooldown()));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTERVAL, this.interval);
        bundle.put(LEVEL, this.level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.interval = bundle.getInt(INTERVAL);
        this.level = bundle.getInt(LEVEL);
    }

    public static int currentLevel(Char ch) {
        int level = 0;
        for (Barkskin b : ch.buffs(Barkskin.class)) {
            level = Math.max(level, b.level);
        }
        return level;
    }

    public static void conditionallyAppend(Char ch, int level, int interval) {
        for (Barkskin b : ch.buffs(Barkskin.class)) {
            if (b.interval != interval) continue;
            b.set(level, interval);
            return;
        }
        Buff.append(ch, Barkskin.class).set(level, interval);
    }
}

