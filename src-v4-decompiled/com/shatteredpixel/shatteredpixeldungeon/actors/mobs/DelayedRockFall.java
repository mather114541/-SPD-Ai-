/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import java.util.ArrayList;
import java.util.List;

public class DelayedRockFall
extends FlavourBuff {
    private int[] rockPositions;
    private ArrayList<Emitter> rockEmitters = new ArrayList();
    private static final String POSITIONS = "positions";

    public void setRockPositions(List<Integer> rockPositions) {
        this.rockPositions = new int[rockPositions.size()];
        for (int i = 0; i < rockPositions.size(); ++i) {
            this.rockPositions[i] = rockPositions.get(i);
        }
        this.fx(true);
    }

    @Override
    public boolean act() {
        for (int i : this.rockPositions) {
            CellEmitter.get(i).start(Speck.factory(8), 0.07f, 10);
            Char ch = Actor.findChar(i);
            if (ch != null) {
                this.affectChar(ch);
                continue;
            }
            this.affectCell(i);
        }
        PixelScene.shake(3.0f, 0.7f);
        Sample.INSTANCE.play("sounds/rocks.mp3");
        this.detach();
        return super.act();
    }

    public void affectChar(Char ch) {
    }

    public void affectCell(int cell) {
    }

    @Override
    public void fx(boolean on) {
        if (on && this.rockPositions != null) {
            for (int i : this.rockPositions) {
                Emitter e = CellEmitter.get(i);
                e.y -= 3.2f;
                e.height *= 0.4f;
                e.pour(EarthParticle.FALLING, 0.1f);
                this.rockEmitters.add(e);
            }
        } else {
            for (Emitter e : this.rockEmitters) {
                e.on = false;
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POSITIONS, this.rockPositions);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.rockPositions = bundle.getIntArray(POSITIONS);
    }
}

