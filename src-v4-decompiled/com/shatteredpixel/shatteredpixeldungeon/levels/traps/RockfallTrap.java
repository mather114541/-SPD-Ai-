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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class RockfallTrap
extends Trap {
    public RockfallTrap() {
        this.color = 7;
        this.shape = 4;
        this.canBeHidden = false;
        this.avoidsHallways = true;
    }

    @Override
    public void activate() {
        ArrayList<Integer> rockCells = new ArrayList<Integer>();
        boolean onGround = Dungeon.level.traps.get(this.pos) == this;
        Room r = null;
        if (Dungeon.level instanceof RegularLevel) {
            r = ((RegularLevel)Dungeon.level).room(this.pos);
        }
        if (onGround && r != null) {
            for (Point p : r.getPoints()) {
                int cell = Dungeon.level.pointToCell(p);
                if (Dungeon.level.solid[cell]) continue;
                rockCells.add(cell);
            }
        } else {
            PathFinder.buildDistanceMap(this.pos, BArray.not(Dungeon.level.solid, null), 2);
            for (int i = 0; i < PathFinder.distance.length; ++i) {
                if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
                rockCells.add(i);
            }
        }
        boolean seen = false;
        Iterator<Point> iterator = rockCells.iterator();
        while (iterator.hasNext()) {
            Char ch;
            int cell = (Integer)((Object)iterator.next());
            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.get(cell - Dungeon.level.width()).start(Speck.factory(8), 0.07f, 10);
                seen = true;
            }
            if ((ch = Actor.findChar(cell)) == null || !ch.isAlive()) continue;
            if (ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
            }
            int damage = Random.NormalIntRange(5 + this.scalingDepth(), 10 + this.scalingDepth() * 2);
            ch.damage(Math.max(damage -= ch.drRoll(), 0), this);
            if (ch.isActive()) {
                Buff.prolong(ch, Paralysis.class, 10.0f);
                continue;
            }
            if (ch.isAlive() || ch != Dungeon.hero) continue;
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
            if (!this.reclaimed) continue;
            Badges.validateDeathFromFriendlyMagic();
        }
        if (seen) {
            PixelScene.shake(3.0f, 0.7f);
            Sample.INSTANCE.play("sounds/rocks.mp3");
        }
    }
}

