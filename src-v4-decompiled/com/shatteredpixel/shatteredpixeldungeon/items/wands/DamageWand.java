/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;

public abstract class DamageWand
extends Wand {
    public int min() {
        return this.min(this.buffedLvl());
    }

    public abstract int min(int var1);

    public int max() {
        return this.max(this.buffedLvl());
    }

    public abstract int max(int var1);

    public int damageRoll() {
        return this.damageRoll(this.buffedLvl());
    }

    public int damageRoll(int lvl) {
        int dmg = Hero.heroDamageIntRange(this.min(lvl), this.max(lvl));
        WandEmpower emp = Dungeon.hero.buff(WandEmpower.class);
        if (emp != null) {
            dmg += emp.dmgBoost;
            --emp.left;
            if (emp.left <= 0) {
                emp.detach();
            }
            Sample.INSTANCE.play("sounds/hit_strong.mp3", 0.75f, 1.2f);
        }
        return dmg;
    }

    @Override
    public String statsDesc() {
        if (this.levelKnown) {
            return Messages.get(this, "stats_desc", this.min(), this.max());
        }
        return Messages.get(this, "stats_desc", this.min(0), this.max(0));
    }

    @Override
    public String upgradeStat1(int level) {
        return this.min(level) + "-" + this.max(level);
    }
}

