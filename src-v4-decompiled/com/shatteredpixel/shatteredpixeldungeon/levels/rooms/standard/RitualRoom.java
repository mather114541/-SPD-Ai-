/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PatchRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class RitualRoom
extends PatchRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 9);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 9);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{6.0f, 3.0f, 1.0f};
    }

    @Override
    protected float fill() {
        int scale = Math.min(this.width() * this.height(), 324);
        return 0.3f + (float)scale / 1024.0f;
    }

    @Override
    protected int clustering() {
        return 0;
    }

    @Override
    protected boolean ensurePath() {
        return this.connected.size() > 0;
    }

    @Override
    protected boolean cleanEdges() {
        return true;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Point c = this.center();
        this.setupPatch(level);
        this.fillPatch(level, 33);
        Painter.fill(level, c.x - 3, c.y - 3, 7, 7, 1);
        Painter.set(level, c.x - 2, c.y - 1, 25);
        Painter.set(level, c.x - 1, c.y - 2, 25);
        Painter.set(level, c.x + 2, c.y - 1, 25);
        Painter.set(level, c.x + 1, c.y - 2, 25);
        Painter.set(level, c.x - 2, c.y + 1, 25);
        Painter.set(level, c.x - 1, c.y + 2, 25);
        Painter.set(level, c.x + 2, c.y + 1, 25);
        Painter.set(level, c.x + 1, c.y + 2, 25);
        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, 9);
        Painter.set(level, c, 11);
        this.placeloot(level, c);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }

    protected void placeloot(Level level, Point p) {
        Item prize;
        Item item = prize = Random.Int(2) == 0 ? level.findPrizeItem() : null;
        if (prize == null) {
            prize = Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.SCROLL));
        }
        level.drop(prize, level.pointToCell(p));
    }
}

