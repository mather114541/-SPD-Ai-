/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;

public class Alchemy
extends Blob {
    protected int pos;

    public Alchemy() {
        this.alwaysVisible = true;
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.ALCHEMY;
    }

    @Override
    protected void evolve() {
        for (int i = this.area.top - 1; i <= this.area.bottom; ++i) {
            for (int j = this.area.left - 1; j <= this.area.right; ++j) {
                int cell = j + i * Dungeon.level.width();
                if (!Dungeon.level.insideMap(cell)) continue;
                this.off[cell] = this.cur[cell];
                this.volume += this.off[cell];
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(12), 0.33f, 0);
    }
}

