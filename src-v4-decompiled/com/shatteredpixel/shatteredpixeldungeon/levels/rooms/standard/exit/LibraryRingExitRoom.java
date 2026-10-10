/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.LibraryRingRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class LibraryRingExitRoom
extends LibraryRingRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 13);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 13);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        super.paint(level);
        Painter.fill(level, this, 5, 14);
        Point p = this.center();
        Painter.set(level, p, 8);
        level.transitions.add(new LevelTransition(level, level.pointToCell(p), LevelTransition.Type.REGULAR_EXIT));
        Carpet carpet = new Carpet();
        carpet.setRect(this.left + 5, this.top + 5, this.width() - 10, this.height() - 10);
        carpet.overrideTile(level, p.x, p.y, -1);
        level.customTiles.add(carpet);
        int dirX = 0;
        int dirY = 0;
        if (Random.Int(2) == 0) {
            dirX = Random.Int(2) == 0 ? 1 : -1;
        } else {
            dirY = Random.Int(2) == 0 ? 1 : -1;
        }
        p.x += dirX;
        p.y += dirY;
        while (level.map[level.pointToCell(p)] != 1) {
            Painter.set(level, p, 14);
            p.x += dirX;
            p.y += dirY;
        }
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

