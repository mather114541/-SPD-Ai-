/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Corrosion
extends Buff
implements Hero.Doom,
Buff.DOTbuff {
    private float damage = 1.0f;
    protected float left;
    private Class source;
    private static final String DAMAGE = "damage";
    private static final String LEFT = "left";
    private static final String SOURCE = "source";

    public Corrosion() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DAMAGE, this.damage);
        bundle.put(LEFT, this.left);
        bundle.put(SOURCE, this.source);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.damage = bundle.getFloat(DAMAGE);
        this.left = bundle.getFloat(LEFT);
        this.source = bundle.getClass(SOURCE);
    }

    public void set(float duration, int damage) {
        this.set(duration, damage, null);
    }

    public void set(float duration, int damage, Class source) {
        this.left = Math.max(duration, this.left);
        if (this.damage < (float)damage) {
            this.damage = damage;
        }
        this.source = source;
        if (this.target != null) {
            this.target.needsIncomingDOTUpdate = true;
        }
    }

    public void extend(float duration) {
        this.left += duration;
        if (this.target != null) {
            this.target.needsIncomingDOTUpdate = true;
        }
    }

    @Override
    public int icon() {
        return 3;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.5f, 0.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.damage);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns(this.left), (int)this.damage);
    }

    @Override
    public boolean act() {
        if (this.target.isAlive()) {
            float f;
            this.target.damage((int)this.damage, this);
            this.damage = this.damage < (float)(Dungeon.scalingDepth() / 2 + 2) ? (this.damage += 1.0f) : (this.damage += 0.5f);
            this.spend(1.0f);
            this.left -= 1.0f;
            if (f <= 0.0f) {
                this.detach();
            }
        } else {
            this.detach();
        }
        this.target.needsIncomingDOTUpdate = true;
        return true;
    }

    @Override
    public void detach() {
        this.target.needsIncomingDOTUpdate = true;
        super.detach();
    }

    @Override
    public void onDeath() {
        if (this.source == WandOfCorrosion.class) {
            Badges.validateDeathFromFriendlyMagic();
        }
        Dungeon.fail(this);
        GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
    }

    @Override
    public int totalIncomingDMG() {
        int total = 0;
        float curDMG = this.damage;
        for (int i = (int)Math.ceil(this.left); i > 0; --i) {
            total += (int)curDMG;
            if (curDMG < (float)(Dungeon.scalingDepth() / 2 + 2)) {
                curDMG += 1.0f;
                continue;
            }
            curDMG += 0.5f;
        }
        return total;
    }
}

