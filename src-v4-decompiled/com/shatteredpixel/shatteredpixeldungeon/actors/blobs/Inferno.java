/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class Inferno
extends Blob {
    @Override
    protected void evolve() {
        super.evolve();
        boolean observe = false;
        Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
        Freezing freeze = (Freezing)Dungeon.level.blobs.get(Freezing.class);
        Blizzard bliz = (Blizzard)Dungeon.level.blobs.get(Blizzard.class);
        for (int i = this.area.left - 1; i <= this.area.right; ++i) {
            for (int j = this.area.top - 1; j <= this.area.bottom; ++j) {
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] > 0) {
                    if (fire != null) {
                        fire.clear(cell);
                    }
                    if (freeze != null) {
                        freeze.clear(cell);
                    }
                    if (bliz != null && bliz.volume > 0 && bliz.cur[cell] > 0) {
                        bliz.clear(cell);
                        this.cur[cell] = 0;
                        this.off[cell] = 0;
                        continue;
                    }
                    Fire.burn(cell);
                    if (!Dungeon.level.flamable[cell]) continue;
                    Dungeon.level.destroy(cell);
                    observe = true;
                    GameScene.updateMap(cell);
                    continue;
                }
                if (!Dungeon.level.flamable[cell] || this.cur[cell - 1] <= 0 && this.cur[cell + 1] <= 0 && this.cur[cell - Dungeon.level.width()] <= 0 && this.cur[cell + Dungeon.level.width()] <= 0 || fire != null && fire.cur != null && fire.cur[cell] != 0) continue;
                GameScene.add(Fire.seed(cell, 4, Fire.class));
            }
        }
        if (observe) {
            Dungeon.observe();
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(118, true), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

