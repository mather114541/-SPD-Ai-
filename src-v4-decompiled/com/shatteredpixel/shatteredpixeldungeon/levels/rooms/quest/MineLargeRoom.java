/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalGuardian;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FungalSentry;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollSapper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GnollRockfallTrap;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class MineLargeRoom
extends CaveRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public int minHeight() {
        return 11;
    }

    @Override
    public int minWidth() {
        return 11;
    }

    @Override
    protected float fill() {
        return 0.55f;
    }

    @Override
    public int mobSpawnWeight() {
        return 1;
    }

    @Override
    public void paint(Level level) {
        super.paint(level);
        if (Blacksmith.Quest.Type() == 1) {
            Painter.fillEllipse(level, this, 3, 35);
            Painter.fillEllipse(level, this, 4, 1);
            Point p = this.random(5);
            ArrayList<Integer> internalcells = new ArrayList<Integer>();
            this.findInternalCells(level, level.pointToCell(p), internalcells);
            block0: for (int i : internalcells) {
                for (int j : PathFinder.CIRCLE8) {
                    if (internalcells.contains(i + j) || level.map[i + j] == 35) continue;
                    level.map[i] = 35;
                    continue block0;
                }
            }
            for (int i = 0; i < this.width() * this.height() / 4; ++i) {
                Point r = this.random(1);
                if (level.map[level.pointToCell(r)] == 4) continue;
                Painter.set(level, r, 35);
            }
            CrystalGuardian m = new CrystalGuardian();
            m.pos = level.pointToCell(p);
            level.mobs.add(m);
            Painter.set(level, p, 1);
        } else if (Blacksmith.Quest.Type() == 2) {
            int guardPos;
            Painter.fillEllipse(level, this, 3, 1);
            for (Object n : this.connected.keySet()) {
                if (n instanceof SecretRoom || ((Room.Door)this.connected.get((Object)n)).type != Room.Door.Type.REGULAR) continue;
                if (Random.Int(10) == 0) {
                    ((Room.Door)this.connected.get(n)).set(Room.Door.Type.EMPTY);
                } else {
                    ((Room.Door)this.connected.get(n)).set(Room.Door.Type.WALL);
                }
                ((Room.Door)this.connected.get(n)).lockTypeChanges(true);
            }
            ArrayList<Room.Door> doors = new ArrayList<Room.Door>();
            for (Room.Door d : this.connected.values()) {
                if (d.type != Room.Door.Type.WALL) continue;
                doors.add(d);
            }
            int sapperPos = level.pointToCell(this.random(5));
            GnollSapper s = new GnollSapper();
            s.spawnPos = s.pos = sapperPos;
            level.mobs.add(s);
            while (level.map[guardPos = sapperPos + PathFinder.NEIGHBOURS8[Random.Int(8)]] != 1) {
            }
            GnollGuard g = new GnollGuard();
            g.pos = guardPos;
            level.mobs.add(g);
            s.linkPartner(g);
            int barricades = Random.Int(2) == 0 ? 2 : 1;
            for (int i = 0; i < barricades; ++i) {
                int barricadePos = sapperPos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                if (level.map[barricadePos] == 1 && barricadePos != guardPos) {
                    Painter.set(level, barricadePos, 13);
                    continue;
                }
                --i;
            }
            int traps = this.square() > 150 ? 3 : 2;
            for (int i = 0; i < traps; ++i) {
                Point r;
                while (level.map[level.pointToCell(r = this.random(2))] != 1 || level.pointToCell(r) == sapperPos || level.pointToCell(r) == guardPos) {
                }
                Painter.set(level, r, 18);
                level.setTrap(new GnollRockfallTrap().reveal(), level.pointToCell(r));
            }
            for (Point p : this.getPoints()) {
                int cell = level.pointToCell(p);
                if (level.map[cell] != 1 || cell == sapperPos || cell == guardPos) continue;
                float dist = 1000.0f;
                for (Room.Door d : doors) {
                    dist = Math.min(dist, Point.distance(p, d));
                }
                float val = Random.Float((float)Math.pow(dist = GameMath.gate(1.0f, dist - 0.5f, 4.0f), 2.0));
                if (val <= 0.75f || dist <= 1.0f) {
                    Painter.set(level, cell, 36);
                    continue;
                }
                if (!(val <= 5.0f) || !(dist <= 3.0f)) continue;
                Painter.set(level, cell, 20);
            }
        } else if (Blacksmith.Quest.Type() == 3) {
            for (Point p : this.getPoints()) {
                int cell = level.pointToCell(p);
                if (level.map[cell] != 1) continue;
                level.map[cell] = 15;
            }
            Painter.fillEllipse(level, this, 3, 2);
            for (int i = 0; i < this.width() * this.height() / 6; ++i) {
                Point r = this.random(1);
                if (level.map[level.pointToCell(r)] == 4) continue;
                Painter.set(level, r, 15);
            }
            Point p = this.center();
            FungalSentry m = new FungalSentry();
            m.pos = level.pointToCell(p);
            level.mobs.add(m);
            Painter.set(level, p, 2);
            --p.y;
            Painter.set(level, p, 2);
        } else {
            Painter.fillEllipse(level, this, 3, 1);
        }
    }

    private void findInternalCells(Level level, int cell, ArrayList<Integer> internalCells) {
        for (int i : PathFinder.NEIGHBOURS4) {
            if (internalCells.contains(cell + i) || level.map[cell + i] == 35) continue;
            internalCells.add(cell + i);
            this.findInternalCells(level, cell + i, internalCells);
        }
    }
}

