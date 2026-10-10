/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class Fire
extends Blob {
    @Override
    protected void evolve() {
        boolean[] flamable = Dungeon.level.flamable;
        Freezing freeze = (Freezing)Dungeon.level.blobs.get(Freezing.class);
        boolean observe = false;
        for (int i = this.area.left - 1; i <= this.area.right; ++i) {
            for (int j = this.area.top - 1; j <= this.area.bottom; ++j) {
                int fire;
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] > 0) {
                    if (freeze != null && freeze.volume > 0 && freeze.cur[cell] > 0) {
                        freeze.clear(cell);
                        this.cur[cell] = 0;
                        this.off[cell] = 0;
                        continue;
                    }
                    Fire.burn(cell);
                    fire = this.cur[cell] - 1;
                    if (fire <= 0 && flamable[cell]) {
                        Dungeon.level.destroy(cell);
                        observe = true;
                        GameScene.updateMap(cell);
                    }
                } else if (freeze == null || freeze.volume <= 0 || freeze.cur[cell] <= 0) {
                    if (flamable[cell] && (this.cur[cell - 1] > 0 || this.cur[cell + 1] > 0 || this.cur[cell - Dungeon.level.width()] > 0 || this.cur[cell + Dungeon.level.width()] > 0)) {
                        fire = 4;
                        Fire.burn(cell);
                        this.area.union(i, j);
                    } else {
                        fire = 0;
                    }
                } else {
                    fire = 0;
                }
                this.off[cell] = fire;
                this.volume += this.off[cell];
            }
        }
        if (observe) {
            Dungeon.observe();
        }
    }

    public static void burn(int pos) {
        Plant plant;
        Heap heap;
        Char ch = Actor.findChar(pos);
        if (ch != null && !ch.isImmune(Fire.class)) {
            Buff.affect(ch, Burning.class).reignite(ch);
        }
        if ((heap = (Heap)Dungeon.level.heaps.get(pos)) != null) {
            heap.burn();
        }
        if ((plant = (Plant)Dungeon.level.plants.get(pos)) != null) {
            plant.wither();
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(FlameParticle.FACTORY, 0.03f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

