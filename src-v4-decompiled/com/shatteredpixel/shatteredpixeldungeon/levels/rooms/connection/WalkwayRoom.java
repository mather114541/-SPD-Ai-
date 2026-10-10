/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.BridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.PerimeterRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.RingBridgeRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class WalkwayRoom
extends PerimeterRoom {
    @Override
    public void paint(Level level) {
        if (Math.min(this.width(), this.height()) > 3) {
            Painter.fill(level, this, 1, 0);
        }
        super.paint(level);
        for (Room r : this.neigbours) {
            if (!(r instanceof BridgeRoom) && !(r instanceof RingBridgeRoom) && !(r instanceof WalkwayRoom)) continue;
            Rect i = this.intersect(r);
            if (i.width() != 0) {
                ++i.left;
                --i.right;
            } else {
                ++i.top;
                --i.bottom;
            }
            Painter.fill(level, i.left, i.top, i.width() + 1, i.height() + 1, 0);
        }
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return mergeTerrain == 0;
    }
}

