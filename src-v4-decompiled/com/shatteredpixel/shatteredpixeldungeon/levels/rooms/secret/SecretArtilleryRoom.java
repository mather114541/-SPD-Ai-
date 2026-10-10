/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;

public class SecretArtilleryRoom
extends SecretRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        Painter.set(level, this.center(), 26);
        for (int i = 0; i < 3; ++i) {
            int itemPos;
            while (level.map[itemPos = level.pointToCell(this.random())] != 14 || level.heaps.get(itemPos) != null) {
            }
            if (i == 0) {
                level.drop(new Bomb.DoubleBomb(), itemPos);
                continue;
            }
            level.drop(Generator.randomMissile(true), itemPos);
        }
        this.entrance().set(Room.Door.Type.HIDDEN);
    }
}

