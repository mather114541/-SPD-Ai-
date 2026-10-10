/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.mechanics;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import java.util.ArrayList;
import java.util.List;

public class Ballistica {
    public ArrayList<Integer> path = new ArrayList();
    public Integer sourcePos = null;
    public Integer collisionPos = null;
    public Integer collisionProperties = null;
    public Integer dist = 0;
    public static final int STOP_TARGET = 1;
    public static final int STOP_CHARS = 2;
    public static final int STOP_SOLID = 4;
    public static final int IGNORE_SOFT_SOLID = 8;
    public static final int PROJECTILE = 7;
    public static final int MAGIC_BOLT = 6;
    public static final int WONT_STOP = 0;

    public Ballistica(int from, int to, int params) {
        this.sourcePos = from;
        this.collisionProperties = params;
        this.build(from, to, (params & 1) > 0, (params & 2) > 0, (params & 4) > 0, (params & 8) > 0);
        if (this.collisionPos != null) {
            this.dist = this.path.indexOf(this.collisionPos);
        } else if (!this.path.isEmpty()) {
            this.dist = this.path.size() - 1;
            this.collisionPos = this.path.get(this.dist);
        } else {
            this.path.add(from);
            this.collisionPos = from;
            this.dist = 0;
        }
    }

    private void build(int from, int to, boolean stopTarget, boolean stopChars, boolean stopTerrain, boolean ignoreSoftSolid) {
        int dB;
        int dA;
        int stepB;
        int stepA;
        int w = Dungeon.level.width();
        int x0 = from % w;
        int x1 = to % w;
        int y0 = from / w;
        int y1 = to / w;
        int dx = x1 - x0;
        int dy = y1 - y0;
        int stepX = dx > 0 ? 1 : -1;
        int stepY = dy > 0 ? 1 : -1;
        if ((dx = Math.abs(dx)) > (dy = Math.abs(dy))) {
            stepA = stepX;
            stepB = stepY * w;
            dA = dx;
            dB = dy;
        } else {
            stepA = stepY * w;
            stepB = stepX;
            dA = dy;
            dB = dx;
        }
        int cell = from;
        int err = dA / 2;
        while (Dungeon.level.insideMap(cell)) {
            if (this.collisionPos == null && stopTerrain && cell != this.sourcePos && !Dungeon.level.passable[cell] && !Dungeon.level.avoid[cell] && Actor.findChar(cell) == null) {
                this.collide(this.path.get(this.path.size() - 1));
            }
            this.path.add(cell);
            if (this.collisionPos == null && stopTerrain && cell != this.sourcePos && Dungeon.level.solid[cell] && (!ignoreSoftSolid || !Dungeon.level.passable[cell] && !Dungeon.level.avoid[cell])) {
                this.collide(cell);
            }
            if (this.collisionPos == null && cell != this.sourcePos && stopChars && Actor.findChar(cell) != null) {
                this.collide(cell);
            }
            if (this.collisionPos == null && cell == to && stopTarget) {
                this.collide(cell);
            }
            cell += stepA;
            if ((err += dB) < dA) continue;
            err -= dA;
            cell += stepB;
        }
    }

    private void collide(int cell) {
        if (this.collisionPos == null) {
            this.collisionPos = cell;
        }
    }

    public List<Integer> subPath(int start, int end) {
        try {
            end = Math.min(end, this.path.size() - 1);
            return this.path.subList(start, end + 1);
        }
        catch (Exception e) {
            ShatteredPixelDungeon.reportException(e);
            return new ArrayList<Integer>();
        }
    }
}

