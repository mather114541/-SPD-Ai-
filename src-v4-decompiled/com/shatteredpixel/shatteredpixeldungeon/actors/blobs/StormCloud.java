/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class StormCloud
extends Blob {
    @Override
    protected void evolve() {
        super.evolve();
        Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                Char ch;
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] <= 0) continue;
                Dungeon.level.setCellToWater(true, cell);
                if (fire != null) {
                    fire.clear(cell);
                }
                if ((ch = Actor.findChar(cell)) == null || ch.isImmune(this.getClass()) || !Char.hasProp(ch, Char.Property.FIERY)) continue;
                ch.damage(1 + Dungeon.scalingDepth() / 5, this);
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(117), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

