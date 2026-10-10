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
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class FrostTrap
extends Trap {
    public FrostTrap() {
        this.color = 6;
        this.shape = 3;
    }

    @Override
    public void activate() {
        if (Dungeon.level.heroFOV[this.pos]) {
            Splash.at(this.pos, -5056769, 5);
            Sample.INSTANCE.play("sounds/shatter.mp3");
        }
        PathFinder.buildDistanceMap(this.pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            GameScene.add(Blob.seed(i, 20, Freezing.class));
            if (!(Actor.findChar(i) instanceof Mob)) continue;
            Buff.prolong(Actor.findChar(i), Trap.HazardAssistTracker.class, 50.0f);
        }
    }
}

