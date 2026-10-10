/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.GrassyGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class PlantsRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 5);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 5);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{3.0f, 1.0f, 0.0f};
    }

    @Override
    public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
        if (mergeTerrain == 1 && (other instanceof PlantsRoom || other instanceof GrassyGraveRoom)) {
            super.merge(l, other, merge, 2);
        } else {
            super.merge(l, other, merge, mergeTerrain);
        }
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 2);
        Painter.fill(level, this, 2, 15);
        if (Math.min(this.width(), this.height()) >= 7) {
            Painter.fill(level, this, 3, 2);
        }
        Point center = this.center();
        if (Math.max(this.width(), this.height()) >= 9) {
            if (Math.min(this.width(), this.height()) >= 11) {
                Painter.drawLine(level, new Point(this.left + 2, center.y), new Point(this.right - 2, center.y), 15);
                Painter.drawLine(level, new Point(center.x, this.top + 2), new Point(center.x, this.bottom - 2), 15);
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x - 1, center.y - 1)));
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x + 1, center.y - 1)));
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x - 1, center.y + 1)));
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x + 1, center.y + 1)));
            } else if (this.width() > this.height() || this.width() == this.height() && Random.Int(2) == 0) {
                Painter.drawLine(level, new Point(center.x, this.top + 2), new Point(center.x, this.bottom - 2), 15);
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x - 1, center.y)));
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x + 1, center.y)));
            } else {
                Painter.drawLine(level, new Point(this.left + 2, center.y), new Point(this.right - 2, center.y), 15);
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x, center.y - 1)));
                level.plant(PlantsRoom.randomSeed(), level.pointToCell(new Point(center.x, center.y + 1)));
            }
        } else {
            level.plant(PlantsRoom.randomSeed(), level.pointToCell(center));
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return super.canPlaceItem(p, l) && l.plants.get(l.pointToCell(p)) == null;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.plants.get(l.pointToCell(p)) == null;
    }

    private static Plant.Seed randomSeed() {
        Plant.Seed result;
        while ((result = (Plant.Seed)Generator.randomUsingDefaults(Generator.Category.SEED)) instanceof Firebloom.Seed) {
        }
        return result;
    }
}

