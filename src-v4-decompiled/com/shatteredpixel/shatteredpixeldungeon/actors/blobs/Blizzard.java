/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class Blizzard
extends Blob {
    @Override
    protected void evolve() {
        super.evolve();
        Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
        Freezing freeze = (Freezing)Dungeon.level.blobs.get(Freezing.class);
        Inferno inf = (Inferno)Dungeon.level.blobs.get(Inferno.class);
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] <= 0) continue;
                if (fire != null) {
                    fire.clear(cell);
                }
                if (freeze != null) {
                    freeze.clear(cell);
                }
                if (inf != null && inf.volume > 0 && inf.cur[cell] > 0) {
                    inf.clear(cell);
                    this.cur[cell] = 0;
                    this.off[cell] = 0;
                    continue;
                }
                Freezing.freeze(cell);
                Freezing.freeze(cell);
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(119, true), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

