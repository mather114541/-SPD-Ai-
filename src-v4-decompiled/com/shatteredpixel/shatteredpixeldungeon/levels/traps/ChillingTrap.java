/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class ChillingTrap
extends Trap {
    public ChillingTrap() {
        this.color = 6;
        this.shape = 0;
    }

    @Override
    public void activate() {
        if (Dungeon.level.heroFOV[this.pos]) {
            Splash.at(this.pos, -5056769, 5);
            Sample.INSTANCE.play("sounds/shatter.mp3");
        }
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[this.pos + i]) continue;
            GameScene.add(Blob.seed(this.pos + i, 10, Freezing.class));
            if (!(Actor.findChar(this.pos + i) instanceof Mob)) continue;
            Buff.prolong(Actor.findChar(this.pos + i), Trap.HazardAssistTracker.class, 50.0f);
        }
    }
}

