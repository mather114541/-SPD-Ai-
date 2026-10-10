/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public class CorrosiveGas
extends Blob {
    private int strength = 0;
    private Class source;
    private static final String STRENGTH = "strength";
    private static final String SOURCE = "source";

    @Override
    protected void evolve() {
        super.evolve();
        if (this.volume == 0) {
            this.strength = 0;
            this.source = null;
        } else {
            for (int i = this.area.left; i < this.area.right; ++i) {
                for (int j = this.area.top; j < this.area.bottom; ++j) {
                    Char ch;
                    int cell = i + j * Dungeon.level.width();
                    if (this.cur[cell] <= 0 || (ch = Actor.findChar(cell)) == null || ch.isImmune(this.getClass())) continue;
                    Buff.affect(ch, Corrosion.class).set(2.0f, this.strength, this.source);
                }
            }
        }
    }

    public CorrosiveGas setStrength(int str) {
        return this.setStrength(str, null);
    }

    public CorrosiveGas setStrength(int str, Class source) {
        if (str > this.strength) {
            this.strength = str;
            this.source = source;
        }
        return this;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.strength = bundle.getInt(STRENGTH);
        this.source = bundle.getClass(SOURCE);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STRENGTH, this.strength);
        bundle.put(SOURCE, this.source);
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(108), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

