/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Bleeding
extends Buff
implements Buff.DOTbuff {
    protected float level;
    private Class source;
    private static final String LEVEL = "level";
    private static final String SOURCE = "source";

    public Bleeding() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    public float level() {
        return this.level;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, this.level);
        bundle.put(SOURCE, this.source);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.level = bundle.getFloat(LEVEL);
        this.source = bundle.getClass(SOURCE);
    }

    public void set(float level) {
        this.set(level, null);
    }

    public void set(float level, Class source) {
        if (this.level < (level = Random.NormalFloat(level / 2.0f, level))) {
            this.level = Math.max(this.level, level);
            this.source = source;
        }
        if (this.target != null) {
            this.target.needsIncomingDOTUpdate = true;
        }
    }

    public void extend(float amount) {
        this.level += amount;
    }

    @Override
    public int icon() {
        return 26;
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(Math.round(this.level));
    }

    @Override
    public boolean act() {
        if (this.target.isAlive()) {
            int dmg = Math.round(this.level);
            if (dmg > 0) {
                this.target.damage(dmg, this);
                if (this.target.sprite != null && this.target.sprite.visible) {   // headless sprite null 保护
                    Splash.at(this.target.sprite.center(), -1.5707963f, 0.52359873f, this.target.sprite.blood(), Math.min(10 * dmg / this.target.HT, 10));
                }
                if (this.target == Dungeon.hero && !this.target.isAlive()) {
                    if (this.source == Chasm.class) {
                        Badges.validateDeathFromFalling();
                    } else if (this.source == Sacrificial.class) {
                        Badges.validateDeathFromFriendlyMagic();
                    }
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                }
                if (this.source == Sickle.HarvestBleedTracker.class && !this.target.isAlive()) {
                    MeleeWeapon.onAbilityKill(Dungeon.hero, this.target);
                }
                this.spend(1.0f);
            }
            this.level = Random.NormalFloat(this.level / 2.0f, this.level);
            if (Math.round(this.level) <= 0) {
                this.detach();
            }
            this.target.needsIncomingDOTUpdate = true;
        } else {
            this.detach();
        }
        return true;
    }

    @Override
    public void detach() {
        this.target.needsIncomingDOTUpdate = true;
        super.detach();
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Math.round(this.level));
    }

    @Override
    public int totalIncomingDMG() {
        return Math.round(this.level * 4.0f);
    }
}

