/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class Freezing
extends Blob {
    @Override
    protected void evolve() {
        Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
        for (int i = this.area.left - 1; i <= this.area.right; ++i) {
            for (int j = this.area.top - 1; j <= this.area.bottom; ++j) {
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] > 0) {
                    if (fire != null && fire.volume > 0 && fire.cur[cell] > 0) {
                        fire.clear(cell);
                        this.cur[cell] = 0;
                        this.off[cell] = 0;
                        continue;
                    }
                    Freezing.freeze(cell);
                    this.off[cell] = this.cur[cell] - 1;
                    this.volume += this.off[cell];
                    continue;
                }
                this.off[cell] = 0;
            }
        }
    }

    public static void freeze(int cell) {
        Heap heap;
        Char ch = Actor.findChar(cell);
        if (ch != null && !ch.isImmune(Freezing.class)) {
            if (ch.buff(Frost.class) != null) {
                Buff.affect(ch, Frost.class, 2.0f);
            } else {
                float turnsToAdd;
                Chill chill = ch.buff(Chill.class);
                float f = turnsToAdd = Dungeon.level.water[cell] ? 5.0f : 3.0f;
                if (chill != null) {
                    float chillToCap = 10.0f - chill.cooldown();
                    turnsToAdd = Math.min(turnsToAdd, chillToCap /= ch.resist(Chill.class));
                }
                if (turnsToAdd > 0.0f) {
                    Buff.affect(ch, Chill.class, turnsToAdd);
                }
                if (chill != null && chill.cooldown() >= 10.0f && !ch.isImmune(Frost.class)) {
                    Buff.affect(ch, Frost.class, 10.0f);
                }
            }
        }
        if ((heap = (Heap)Dungeon.level.heaps.get(cell)) != null) {
            heap.freeze();
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(SnowParticle.FACTORY, 0.05f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }

    public static boolean affect(int cell) {
        Heap heap;
        MagicalFireRoom.EternalFire eternalFire;
        Fire fire;
        Char ch = Actor.findChar(cell);
        if (ch != null) {
            if (Dungeon.level.water[ch.pos]) {
                Buff.prolong(ch, Frost.class, 30.0f);
            } else {
                Buff.prolong(ch, Frost.class, 10.0f);
            }
        }
        if ((fire = (Fire)Dungeon.level.blobs.get(Fire.class)) != null && fire.volume > 0) {
            fire.clear(cell);
        }
        if ((eternalFire = (MagicalFireRoom.EternalFire)Dungeon.level.blobs.get(MagicalFireRoom.EternalFire.class)) != null && eternalFire.volume > 0) {
            eternalFire.clear(cell);
        }
        if ((heap = (Heap)Dungeon.level.heaps.get(cell)) != null) {
            heap.freeze();
        }
        if (Dungeon.level.heroFOV[cell]) {
            CellEmitter.get(cell).start(SnowParticle.FACTORY, 0.2f, 6);
            return true;
        }
        return false;
    }
}

