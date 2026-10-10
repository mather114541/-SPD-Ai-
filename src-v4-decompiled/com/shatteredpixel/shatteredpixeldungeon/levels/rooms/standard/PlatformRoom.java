/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ChasmRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;

public class PlatformRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 6);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 6);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{6.0f, 3.0f, 1.0f};
    }

    @Override
    public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
        if (mergeTerrain != 0 && this.connected.containsKey(other) && (other instanceof PlatformRoom || other instanceof ChasmRoom)) {
            super.merge(l, other, merge, 0);
            Painter.set(l, (Point)this.connected.get(other), 14);
        } else {
            super.merge(l, other, merge, mergeTerrain);
        }
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 0);
        ArrayList<Rect> platforms = new ArrayList<Rect>();
        this.splitPlatforms(new Rect(this.left + 2, this.top + 2, this.right - 2, this.bottom - 2), platforms);
        for (Rect platform : platforms) {
            Painter.fill(level, platform.left, platform.top, platform.width() + 1, platform.height() + 1, 14);
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 2, 14);
        }
    }

    private void splitPlatforms(Rect curPlatform, ArrayList<Rect> allPlatforms) {
        int curArea = (curPlatform.width() + 1) * (curPlatform.height() + 1);
        if (Random.Float() < (float)(curArea - 25) / 11.0f) {
            if (curPlatform.width() > curPlatform.height() || curPlatform.width() == curPlatform.height() && Random.Int(2) == 0) {
                int splitX = Random.IntRange(curPlatform.left + 2, curPlatform.right - 2);
                this.splitPlatforms(new Rect(curPlatform.left, curPlatform.top, splitX - 1, curPlatform.bottom), allPlatforms);
                this.splitPlatforms(new Rect(splitX + 1, curPlatform.top, curPlatform.right, curPlatform.bottom), allPlatforms);
                int bridgeY = Random.NormalIntRange(curPlatform.top, curPlatform.bottom);
                allPlatforms.add(new Rect(splitX - 1, bridgeY, splitX + 1, bridgeY));
            } else {
                int splitY = Random.IntRange(curPlatform.top + 2, curPlatform.bottom - 2);
                this.splitPlatforms(new Rect(curPlatform.left, curPlatform.top, curPlatform.right, splitY - 1), allPlatforms);
                this.splitPlatforms(new Rect(curPlatform.left, splitY + 1, curPlatform.right, curPlatform.bottom), allPlatforms);
                int bridgeX = Random.NormalIntRange(curPlatform.left, curPlatform.right);
                allPlatforms.add(new Rect(bridgeX, splitY - 1, bridgeX, splitY + 1));
            }
        } else {
            allPlatforms.add(curPlatform);
        }
    }
}

