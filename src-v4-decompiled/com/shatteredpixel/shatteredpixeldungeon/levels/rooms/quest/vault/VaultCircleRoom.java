/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultSentry;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class VaultCircleRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 2, 1);
        Painter.fill(level, this, 4, 1, 4, 1, 1);
        Painter.fill(level, this, 1, 4, 1, 4, 1);
        Painter.set(level, this.center(), 11);
        VaultSentry sentry = new VaultSentry();
        sentry.pos = level.pointToCell(this.center());
        sentry.scanLength = 4.49f;
        int w = level.width();
        switch (Random.Int(4)) {
            case 0: {
                sentry.scanWidth = 90.0f;
                sentry.scanDirs = new int[][]{{sentry.pos - 1}, {sentry.pos - 1 - w}, {sentry.pos - w}, {sentry.pos + 1 - w}, {sentry.pos + 1}, {sentry.pos + 1 + w}, {sentry.pos + w}, {sentry.pos + w - 1}};
                break;
            }
            case 1: 
            case 2: {
                sentry.scanWidth = 45.0f;
                sentry.scanDirs = new int[][]{{sentry.pos - 2, sentry.pos + 2}, {sentry.pos - 2 - level.width(), sentry.pos + 2 + level.width()}, {sentry.pos - 2 - 2 * level.width(), sentry.pos + 2 + 2 * level.width()}, {sentry.pos - 1 - 2 * level.width(), sentry.pos + 1 + 2 * level.width()}, {sentry.pos - 2 * level.width(), sentry.pos + 2 * level.width()}, {sentry.pos + 1 - 2 * level.width(), sentry.pos - 1 + 2 * level.width()}, {sentry.pos + 2 - 2 * level.width(), sentry.pos - 2 + 2 * level.width()}, {sentry.pos + 2 - level.width(), sentry.pos - 2 + level.width()}};
                break;
            }
            case 3: {
                sentry.scanWidth = 22.5f;
                sentry.scanDirs = new int[][]{{sentry.pos - 3, sentry.pos - 3 * w, sentry.pos + 3, sentry.pos + 3 * w}, {sentry.pos - 3 - 1 * w, sentry.pos + 1 - 3 * w, sentry.pos + 3 + 1 * w, sentry.pos - 1 + 3 * w}, {sentry.pos - 3 - 2 * w, sentry.pos + 2 - 3 * w, sentry.pos + 3 + 2 * w, sentry.pos - 2 + 3 * w}, {sentry.pos - 3 - 3 * w, sentry.pos + 3 - 3 * w, sentry.pos + 3 + 3 * w, sentry.pos - 3 + 3 * w}, {sentry.pos - 2 - 3 * w, sentry.pos + 3 - 2 * w, sentry.pos + 2 + 3 * w, sentry.pos - 3 + 2 * w}, {sentry.pos - 1 - 3 * w, sentry.pos + 3 - 1 * w, sentry.pos + 1 + 3 * w, sentry.pos - 3 + 1 * w}};
            }
        }
        level.mobs.add(sentry);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 4, 1);
        }
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return false;
    }
}

