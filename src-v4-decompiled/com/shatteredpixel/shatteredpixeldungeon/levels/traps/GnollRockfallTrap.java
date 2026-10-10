/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class GnollRockfallTrap
extends RockfallTrap {
    @Override
    public void activate() {
        ArrayList<Integer> rockCells = new ArrayList<Integer>();
        PathFinder.buildDistanceMap(this.pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            if (Dungeon.level instanceof MiningLevel) {
                boolean barricade = false;
                for (int j : PathFinder.NEIGHBOURS9) {
                    if (Dungeon.level.map[i + j] != 13) continue;
                    barricade = true;
                }
                if (barricade) continue;
            }
            rockCells.add(i);
        }
        boolean seen = false;
        Iterator iterator = rockCells.iterator();
        while (iterator.hasNext()) {
            Char ch;
            int cell = (Integer)iterator.next();
            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.get(cell - Dungeon.level.width()).start(Speck.factory(8), 0.07f, 10);
                seen = true;
            }
            if ((ch = Actor.findChar(cell)) != null && ch.isAlive() && !(ch instanceof GnollGeomancer)) {
                if (ch instanceof Mob) {
                    Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
                }
                int damage = Random.NormalIntRange(6, 12);
                ch.damage(Math.max(damage, 0), this);
                Buff.prolong(ch, Paralysis.class, ch instanceof GnollGuard ? 10.0f : 3.0f);
                if (ch.isAlive() || ch != Dungeon.hero) continue;
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                if (!this.reclaimed) continue;
                Badges.validateDeathFromFriendlyMagic();
                continue;
            }
            if (ch != null || !(Dungeon.level instanceof MiningLevel) || Dungeon.level.traps.get(cell) != null || Dungeon.level.plants.get(cell) != null || Random.Int(2) != 0) continue;
            Level.set(cell, 36);
            GameScene.updateMap(cell);
        }
        if (seen) {
            PixelScene.shake(3.0f, 0.7f);
            Sample.INSTANCE.play("sounds/rocks.mp3");
        }
    }
}

