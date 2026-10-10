/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ToxicGas
extends Blob
implements Hero.Doom {
    @Override
    protected void evolve() {
        super.evolve();
        int damage = 1 + Dungeon.scalingDepth() / 5;
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                Char ch;
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] <= 0 || (ch = Actor.findChar(cell)) == null || ch.isImmune(this.getClass())) continue;
                ch.damage(damage, this);
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(107), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }

    @Override
    public void onDeath() {
        Badges.validateDeathFromGas();
        Dungeon.fail(this);
        GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
    }
}

