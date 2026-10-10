/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WebParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class Web
extends Blob {
    public Web() {
        this.actPriority = 1;
    }

    @Override
    protected void evolve() {
        Level l = Dungeon.level;
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                int cell = i + j * l.width();
                this.off[cell] = this.cur[cell] > 0 ? this.cur[cell] - 1 : 0;
                this.volume += this.off[cell];
                if (this.off[cell] != 0 || this.cur[cell] <= 0) continue;
                this.cellsToFlagUpdate.add(cell);
            }
        }
    }

    @Override
    public void seed(Level level, int cell, int amount) {
        super.seed(level, cell, amount);
        level.updateCellFlags(cell);
    }

    public static void affectChar(Char ch) {
        Buff.prolong(ch, Roots.class, 5.0f);
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(WebParticle.FACTORY, 0.25f);
    }

    @Override
    public void clear(int cell) {
        super.clear(cell);
        if (this.cur == null) {
            return;
        }
        Dungeon.level.updateCellFlags(cell);
    }

    @Override
    public void fullyClear() {
        super.fullyClear();
        Dungeon.level.buildFlagMaps();
    }

    @Override
    public void onBuildFlagMaps(Level l) {
        if (this.volume > 0) {
            for (int i = 0; i < l.length(); ++i) {
                this.onUpdateCellFlags(l, i);
            }
        }
    }

    @Override
    public void onUpdateCellFlags(Level l, int cell) {
        if (this.volume > 0 && this.cur[cell] > 0) {
            l.solid[cell] = true;
            l.flamable[cell] = true;
        }
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

