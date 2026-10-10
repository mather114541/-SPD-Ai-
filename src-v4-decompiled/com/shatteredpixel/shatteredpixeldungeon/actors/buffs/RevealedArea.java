/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class RevealedArea
extends FlavourBuff {
    public int pos;
    public int depth;
    public int branch;
    private static final String BRANCH = "branch";
    private static final String DEPTH = "depth";
    private static final String POS = "pos";

    public RevealedArea() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public void detach() {
        GameScene.updateFog(this.pos, 2);
        super.detach();
    }

    @Override
    public int icon() {
        return 0;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.0f, 1.0f, 1.0f);
    }

    @Override
    public float iconFadePercent() {
        float max = 5 * Dungeon.hero.pointsInTalent(Talent.SEER_SHOT);
        return Math.max(0.0f, (max - this.visualcooldown()) / max);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", (int)this.visualcooldown());
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, this.depth);
        bundle.put(BRANCH, this.branch);
        bundle.put(POS, this.pos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.depth = bundle.getInt(DEPTH);
        this.branch = bundle.getInt(BRANCH);
        this.pos = bundle.getInt(POS);
    }
}

