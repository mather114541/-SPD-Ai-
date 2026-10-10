/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class GeyserTrap
extends Trap {
    public int centerKnockBackDirection;
    public Object source;

    public GeyserTrap() {
        this.color = 4;
        this.shape = 4;
        this.centerKnockBackDirection = -1;
        this.source = this;
    }

    @Override
    public void activate() {
        Splash.at(DungeonTilemap.tileCenterToWorld(this.pos), -1.5707963f, 1.5707963f, 6013411, 100, 0.01f);
        Sample.INSTANCE.play("sounds/gas.mp3", 1.0f, 0.75f);
        Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
        PathFinder.buildDistanceMap(this.pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] == 2 && Random.Int(3) > 0) {
                Dungeon.level.setCellToWater(true, i);
                if (fire == null) continue;
                fire.clear(i);
                continue;
            }
            if (PathFinder.distance[i] >= 2) continue;
            Dungeon.level.setCellToWater(true, i);
            if (fire == null) continue;
            fire.clear(i);
        }
        for (int i : PathFinder.NEIGHBOURS8) {
            Char ch = Actor.findChar(this.pos + i);
            if (ch == null) continue;
            if (this.source == this && ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
            }
            if (Char.hasProp(ch, Char.Property.FIERY)) {
                int dmg = Random.NormalIntRange(5 + this.scalingDepth(), 10 + this.scalingDepth() * 2);
                dmg = (int)((float)dmg * 0.67f);
                if (!ch.isImmune(GeyserTrap.class)) {
                    ch.damage(dmg, this);
                }
            }
            if (!ch.isAlive()) continue;
            if (ch.buff(Burning.class) != null) {
                ch.buff(Burning.class).detach();
            }
            Ballistica trajectory = new Ballistica(this.pos, ch.pos, 1);
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), 7);
            WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, this.source);
        }
        Char ch = Actor.findChar(this.pos);
        if (ch != null) {
            if (this.source == this && ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
            }
            int targetpos = -1;
            if (this.centerKnockBackDirection != -1) {
                targetpos = this.centerKnockBackDirection;
            } else if (ch == Dungeon.hero) {
                ArrayList<Integer> candidates = new ArrayList<Integer>();
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (Dungeon.level.avoid[this.pos + i] || Dungeon.level.avoid[this.pos + i + i]) continue;
                    candidates.add(this.pos + i);
                }
                if (!candidates.isEmpty()) {
                    targetpos = (Integer)Random.element(candidates);
                }
            } else {
                targetpos = this.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
            }
            if (Char.hasProp(ch, Char.Property.FIERY)) {
                int dmg = Random.NormalIntRange(5 + this.scalingDepth(), 10 + this.scalingDepth() * 2);
                if (!ch.isImmune(GeyserTrap.class)) {
                    ch.damage(dmg, this);
                }
            }
            if (ch.isAlive() && targetpos != -1) {
                if (ch.buff(Burning.class) != null) {
                    ch.buff(Burning.class).detach();
                }
                Ballistica trajectory = new Ballistica(this.pos, targetpos, 6);
                WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, this.source);
            }
        }
    }
}

