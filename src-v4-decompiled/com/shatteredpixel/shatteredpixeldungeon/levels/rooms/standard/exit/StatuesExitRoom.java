/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StatuesRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class StatuesExitRoom
extends StatuesRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{3.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        super.paint(level);
        int exit = -1;
        if (this.width() >= 11 || this.height() >= 11) {
            Point c = this.center();
            exit = level.pointToCell(c);
            for (int i : PathFinder.NEIGHBOURS8) {
                if (level.map[exit + i] == 25) continue;
                Painter.set(level, exit + i, 20);
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
            carpet.overrideTile(exit, level, -1);
            level.customTiles.add(0, carpet);
        } else {
            for (Point p : this.getPoints()) {
                if (level.map[level.pointToCell(p)] != 8) continue;
                exit = level.pointToCell(p);
                break;
            }
        }
        Painter.set(level, exit, 8);
        level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

