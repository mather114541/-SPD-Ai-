/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ArtifactRecharge
extends Buff {
    public static final float DURATION = 30.0f;
    private float left;
    public boolean ignoreHornOfPlenty;
    private static final String LEFT = "left";
    private static final String IGNORE_HORN = "ignore_horn";

    public ArtifactRecharge() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public boolean act() {
        float chargeAmount;
        if (this.target instanceof Hero && (chargeAmount = Math.min(1.0f, this.left)) > 0.0f) {
            for (Buff b : this.target.buffs()) {
                if (!(b instanceof Artifact.ArtifactBuff) || b instanceof HornOfPlenty.hornRecharge && this.ignoreHornOfPlenty || ((Artifact.ArtifactBuff)b).isCursed()) continue;
                ((Artifact.ArtifactBuff)b).charge((Hero)this.target, chargeAmount);
            }
        }
        this.left -= 1.0f;
        if (this.left < 0.0f) {
            this.detach();
        } else {
            this.spend(1.0f);
        }
        return true;
    }

    public ArtifactRecharge set(float amount) {
        if (this.left < amount) {
            this.left = amount;
        }
        return this;
    }

    public ArtifactRecharge extend(float amount) {
        this.left += amount;
        return this;
    }

    public float left() {
        return this.left;
    }

    @Override
    public int icon() {
        return 34;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.0f, 1.0f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (30.0f - this.left) / 30.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.left + 1);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns(this.left + 1.0f));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
        bundle.put(IGNORE_HORN, this.ignoreHornOfPlenty);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getFloat(LEFT);
        this.ignoreHornOfPlenty = bundle.getBoolean(IGNORE_HORN);
    }

    public static void chargeArtifacts(Hero hero, float turns) {
        for (Buff b : hero.buffs()) {
            if (!(b instanceof Artifact.ArtifactBuff) || ((Artifact.ArtifactBuff)b).isCursed() || ((Artifact.ArtifactBuff)b).isCursed()) continue;
            ((Artifact.ArtifactBuff)b).charge(hero, turns);
        }
    }
}

