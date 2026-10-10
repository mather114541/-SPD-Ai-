/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.watabou.utils.Point;

public class SecretSummoningRoom
extends SecretRoom {
    @Override
    public int maxWidth() {
        return 8;
    }

    @Override
    public int maxHeight() {
        return 8;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 17);
        Point center = this.center();
        level.drop((Item)Generator.random(), (int)level.pointToCell((Point)center)).setHauntedIfCursed().type = Heap.Type.SKELETON;
        float revealedChance = TrapMechanism.revealHiddenTrapChance();
        float revealInc = 0.0f;
        for (Point p : this.getPoints()) {
            int cell = level.pointToCell(p);
            if (level.map[cell] != 17) continue;
            if ((revealInc += revealedChance) >= 1.0f) {
                level.setTrap(new SummoningTrap().reveal(), cell);
                Painter.set(level, cell, 18);
                revealInc -= 1.0f;
                continue;
            }
            level.setTrap(new SummoningTrap().hide(), cell);
        }
        this.entrance().set(Room.Door.Type.HIDDEN);
    }
}

