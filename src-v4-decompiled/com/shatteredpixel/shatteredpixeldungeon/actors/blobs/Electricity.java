/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Electricity
extends Blob {
    private boolean[] water;

    public Electricity() {
        this.actPriority = -21;
    }

    @Override
    protected void evolve() {
        int cell;
        int j;
        int i;
        this.water = Dungeon.level.water;
        for (i = this.area.left - 1; i <= this.area.right; ++i) {
            for (j = this.area.top - 1; j <= this.area.bottom; ++j) {
                cell = i + j * Dungeon.level.width();
                if (this.cur[cell] <= 0) continue;
                this.spreadFromCell(cell, this.cur[cell]);
            }
        }
        for (i = this.area.left - 1; i <= this.area.right; ++i) {
            for (j = this.area.top - 1; j <= this.area.bottom; ++j) {
                cell = i + j * Dungeon.level.width();
                if (this.cur[cell] > 0) {
                    Heap h;
                    Char ch = Actor.findChar(cell);
                    if (ch != null && !ch.isImmune(this.getClass())) {
                        if (ch.buff(Paralysis.class) == null) {
                            Buff.prolong(ch, Paralysis.class, this.cur[cell]);
                        }
                        if (this.cur[cell] % 2 == 1) {
                            ch.damage(Math.round(Random.Float(2.0f + (float)Dungeon.scalingDepth() / 5.0f)), this);
                            if (!ch.isAlive() && ch == Dungeon.hero) {
                                Dungeon.fail(this);
                                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                            }
                        }
                    }
                    if ((h = (Heap)Dungeon.level.heaps.get(cell)) != null) {
                        Item toShock = h.peek();
                        if (toShock instanceof Wand) {
                            ((Wand)toShock).gainCharge(0.333f);
                        } else if (toShock instanceof MagesStaff) {
                            ((MagesStaff)toShock).gainCharge(0.333f);
                        }
                    }
                    this.off[cell] = this.cur[cell] - 1;
                    this.volume += this.off[cell];
                    continue;
                }
                this.off[cell] = 0;
            }
        }
    }

    private void spreadFromCell(int cell, int power) {
        if (this.cur[cell] == 0) {
            this.area.union(cell % Dungeon.level.width(), cell / Dungeon.level.width());
        }
        this.cur[cell] = Math.max(this.cur[cell], power);
        for (int c : PathFinder.NEIGHBOURS4) {
            if (!this.water[cell + c] || this.cur[cell + c] >= power) continue;
            this.spreadFromCell(cell + c, power);
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(SparkParticle.FACTORY, 0.05f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

