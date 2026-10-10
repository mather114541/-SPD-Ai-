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

public class VaultCrossRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 4, 1, 4, 1, 1);
        Painter.fill(level, this, 1, 4, 1, 4, 1);
        Painter.set(level, this.center(), 11);
        VaultSentry sentry = new VaultSentry();
        sentry.pos = level.pointToCell(this.center());
        sentry.scanLength = 4.0f;
        sentry.scanWidth = 90.0f;
        sentry.afterScanCooldown = 3;
        sentry.scanDirs = new int[][]{{sentry.pos - 1}, {sentry.pos - level.width()}, {sentry.pos + 1}, {sentry.pos + level.width()}};
        level.mobs.add(sentry);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }

    @Override
    public boolean canConnect(Point p) {
        Point c = this.center();
        return (Math.abs(c.x - p.x) <= 1 || Math.abs(c.y - p.y) <= 1) && super.canConnect(p);
    }
}

