/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class CorrosionTrap
extends Trap {
    public CorrosionTrap() {
        this.color = 7;
        this.shape = 2;
    }

    @Override
    public void activate() {
        CorrosiveGas corrosiveGas = Blob.seed(this.pos, 80 + 5 * this.scalingDepth(), CorrosiveGas.class);
        Sample.INSTANCE.play("sounds/gas.mp3");
        corrosiveGas.setStrength(1 + this.scalingDepth() / 4);
        for (int i : PathFinder.NEIGHBOURS9) {
            if (!(Actor.findChar(this.pos + i) instanceof Mob)) continue;
            Buff.prolong(Actor.findChar(this.pos + i), Trap.HazardAssistTracker.class, 50.0f);
        }
        GameScene.add(corrosiveGas);
    }
}

