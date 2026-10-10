/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class Regrowth
extends Blob {
    @Override
    protected void evolve() {
        super.evolve();
        if (this.volume > 0) {
            for (int i = this.area.left; i < this.area.right; ++i) {
                for (int j = this.area.top; j < this.area.bottom; ++j) {
                    Char ch;
                    int c;
                    int cell = i + j * Dungeon.level.width();
                    if (this.off[cell] <= 0) continue;
                    int c1 = c = Dungeon.level.map[cell];
                    if (c == 1 || c == 9 || c == 20) {
                        c1 = this.cur[cell] > 9 && Actor.findChar(cell) == null ? 15 : 2;
                    } else if ((c == 2 || c == 30) && this.cur[cell] > 9 && Dungeon.level.plants.get(cell) == null && Actor.findChar(cell) == null) {
                        c1 = 15;
                    }
                    if (c1 != c) {
                        Level.set(cell, c1);
                        GameScene.updateMap(cell);
                    }
                    if ((ch = Actor.findChar(cell)) == null || ch.isImmune(this.getClass()) || this.off[cell] <= 1) continue;
                    Buff.prolong(ch, Roots.class, 1.0f);
                }
            }
            Dungeon.observe();
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(LeafParticle.LEVEL_SPECIFIC, 0.2f, 0);
    }
}

