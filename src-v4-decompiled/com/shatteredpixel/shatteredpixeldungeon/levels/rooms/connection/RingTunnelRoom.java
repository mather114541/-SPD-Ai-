/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.TunnelRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class RingTunnelRoom
extends TunnelRoom {
    private Rect connSpace;

    @Override
    public int minWidth() {
        return Math.max(5, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(5, super.minHeight());
    }

    @Override
    public void paint(Level level) {
        super.paint(level);
        int floor = level.tunnelTile();
        Rect ring = this.getConnectionSpace();
        Painter.fill(level, ring.left, ring.top, 3, 3, floor);
        Painter.fill(level, ring.left + 1, ring.top + 1, 1, 1, 4);
    }

    @Override
    protected Rect getConnectionSpace() {
        if (this.connSpace == null) {
            Point c = this.getDoorCenter();
            c.x = (int)GameMath.gate(this.left + 2, c.x, this.right - 2);
            c.y = (int)GameMath.gate(this.top + 2, c.y, this.bottom - 2);
            this.connSpace = new Rect(c.x - 1, c.y - 1, c.x + 1, c.y + 1);
        }
        return this.connSpace;
    }
}

