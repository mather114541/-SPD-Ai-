/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PatchRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class BurnedRoom
extends PatchRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{4.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        int cell = l.pointToCell(this.pointInside(p, 1));
        return l.map[cell] == 1;
    }

    @Override
    protected float fill() {
        return Math.min(1.0f, 1.48f - (float)(this.width() + this.height()) * 0.03f);
    }

    @Override
    protected int clustering() {
        return 2;
    }

    @Override
    protected boolean ensurePath() {
        return false;
    }

    @Override
    protected boolean cleanEdges() {
        return false;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        this.setupPatch(level);
        float revealedChance = TrapMechanism.revealHiddenTrapChance();
        float revealInc = 0.0f;
        for (int i = this.top + 1; i < this.bottom; ++i) {
            for (int j = this.left + 1; j < this.right; ++j) {
                int t;
                if (!this.patch[this.xyToPatchCoords(j, i)]) continue;
                int cell = i * level.width() + j;
                switch (Random.Int(5)) {
                    default: {
                        t = 1;
                        break;
                    }
                    case 1: {
                        t = 9;
                        break;
                    }
                    case 2: {
                        t = 18;
                        level.setTrap(new BurningTrap().reveal(), cell);
                        break;
                    }
                    case 3: {
                        revealInc += revealedChance;
                        if (revealInc >= 1.0f) {
                            t = 18;
                            level.setTrap(new BurningTrap().reveal(), cell);
                            revealInc -= 1.0f;
                            break;
                        }
                        t = 17;
                        level.setTrap(new BurningTrap().hide(), cell);
                        break;
                    }
                    case 4: {
                        t = 19;
                        BurningTrap trap = new BurningTrap();
                        trap.reveal().active = false;
                        level.setTrap(trap, cell);
                    }
                }
                level.map[cell] = t;
            }
        }
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return !this.inside(p) || !this.patch[this.xyToPatchCoords(p.x, p.y)];
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return !this.inside(p) || !this.patch[this.xyToPatchCoords(p.x, p.y)];
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return !this.inside(p) || !this.patch[this.xyToPatchCoords(p.x, p.y)];
    }
}

