/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FetidRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class StenchGas
extends Blob {
    @Override
    protected void evolve() {
        super.evolve();
        boolean fetidRatSpawned = false;
        for (Mob m : Dungeon.level.mobs) {
            if (!(m instanceof FetidRat)) continue;
            fetidRatSpawned = true;
            break;
        }
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                Char ch;
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] <= 0 || (ch = Actor.findChar(cell)) == null || ch.isImmune(this.getClass())) continue;
                if (ch == Dungeon.hero && ch.buff(Paralysis.class) == null && fetidRatSpawned) {
                    Statistics.questScores[0] = Statistics.questScores[0] - 100;
                }
                Buff.prolong(ch, Paralysis.class, 2.0f);
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(111), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

