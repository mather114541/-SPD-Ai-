/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;

public class GooWarn
extends Blob {
    protected int pos;

    public GooWarn() {
        this.actPriority = -19;
    }

    @Override
    protected void evolve() {
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                int cell = i + j * Dungeon.level.width();
                int n = this.off[cell] = this.cur[cell] > 0 ? this.cur[cell] - 1 : 0;
                if (this.off[cell] <= 0) continue;
                this.volume += this.off[cell];
            }
        }
    }

    @Override
    public void seed(Level level, int cell, int amount) {
        int toAdd;
        if (this.cur == null) {
            this.cur = new int[level.length()];
        }
        if (this.off == null) {
            this.off = new int[this.cur.length];
        }
        if ((toAdd = amount - this.cur[cell]) > 0) {
            int n = cell;
            this.cur[n] = this.cur[n] + toAdd;
            this.volume += toAdd;
        }
        this.area.union(cell % level.width(), cell / level.width());
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(GooSprite.GooParticle.FACTORY, 0.03f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

