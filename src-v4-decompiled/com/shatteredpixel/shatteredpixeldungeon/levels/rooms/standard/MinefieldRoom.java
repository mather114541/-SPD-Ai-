/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class MinefieldRoom
extends StandardRoom {
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
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        int mines = (int)Math.round(Math.sqrt(this.square()));
        switch (this.sizeCat) {
            case NORMAL: {
                mines -= 3;
                break;
            }
            case LARGE: {
                mines += 3;
                break;
            }
            case GIANT: {
                mines += 9;
            }
        }
        float revealedChance = TrapMechanism.revealHiddenTrapChance();
        float revealInc = 0.0f;
        for (int i = 0; i < mines; ++i) {
            int pos;
            while (level.traps.get(pos = level.pointToCell(this.random(1))) != null) {
            }
            for (int j = 0; j < 8; ++j) {
                int c = PathFinder.NEIGHBOURS8[Random.Int(8)];
                if (level.traps.get(pos + c) != null || level.map[pos + c] != 1) continue;
                Painter.set(level, pos + c, 9);
            }
            if ((revealInc += revealedChance) >= 1.0f) {
                Painter.set(level, pos, 18);
                level.setTrap(new ExplosiveTrap().reveal(), pos);
                revealInc -= 1.0f;
                continue;
            }
            Painter.set(level, pos, 17);
            level.setTrap(new ExplosiveTrap().hide(), pos);
        }
    }
}

