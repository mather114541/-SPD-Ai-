/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StatuesRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class StatuesEntranceRoom
extends StatuesRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{3.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void paint(Level level) {
        super.paint(level);
        int entrance = -1;
        if (this.width() >= 11 || this.height() >= 11) {
            Point c = this.center();
            entrance = level.pointToCell(c);
            for (int i : PathFinder.NEIGHBOURS8) {
                if (level.map[entrance + i] == 25) continue;
                Painter.set(level, entrance + i, 20);
            }
            Carpet carpet = new Carpet();
            Rect carpetRect = new Rect(c.x - 1, c.y - 1, c.x + 1, c.y + 1);
            if (this.width() % 2 == 0) {
                if ((float)c.x < (float)(this.left + this.right) / 2.0f) {
                    ++carpetRect.right;
                } else {
                    --carpetRect.left;
                }
            }
            if (this.height() % 2 == 0) {
                if ((float)c.y < (float)(this.top + this.bottom) / 2.0f) {
                    ++carpetRect.bottom;
                } else {
                    --carpetRect.top;
                }
            }
            carpet.setRect(carpetRect.left, carpetRect.top, carpetRect.width() + 1, carpetRect.height() + 1);
            carpet.overrideTile(entrance, level, 82);
            level.customTiles.add(0, carpet);
        } else {
            for (Point p : this.getPoints()) {
                if (level.map[level.pointToCell(p)] != 7) continue;
                entrance = level.pointToCell(p);
                break;
            }
        }
        Painter.set(level, entrance, 7);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

