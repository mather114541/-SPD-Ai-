/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class LevelTransition
extends Rect
implements Bundlable {
    public Type type;
    public int destDepth;
    public int destBranch;
    public Type destType;
    public int centerCell;
    public static final String TYPE = "type";
    public static final String DEST_DEPTH = "dest_depth";
    public static final String DEST_BRANCH = "dest_branch";
    public static final String DEST_TYPE = "dest_type";

    public LevelTransition() {
    }

    public LevelTransition(Level level, int cell, Type type, int destDepth, int destBranch, Type destType) {
        this.centerCell = cell;
        Point p = level.cellToPoint(cell);
        this.set(p.x, p.y, p.x, p.y);
        this.type = type;
        this.destDepth = destDepth;
        this.destBranch = destBranch;
        this.destType = destType;
    }

    public LevelTransition(Level level, int cell, Type type) {
        this.centerCell = cell;
        Point p = level.cellToPoint(cell);
        this.set(p.x, p.y, p.x, p.y);
        this.type = type;
        switch (type.ordinal()) {
            default: {
                this.destDepth = Dungeon.depth - 1;
                this.destBranch = Dungeon.branch;
                this.destType = Type.REGULAR_EXIT;
                break;
            }
            case 2: {
                this.destDepth = Dungeon.depth + 1;
                this.destBranch = Dungeon.branch;
                this.destType = Type.REGULAR_ENTRANCE;
                break;
            }
            case 0: {
                this.destDepth = 0;
                this.destBranch = 0;
                this.destType = null;
            }
        }
    }

    public int cell() {
        return this.centerCell;
    }

    @Override
    public int width() {
        return super.width() + 1;
    }

    @Override
    public int height() {
        return super.height() + 1;
    }

    @Override
    public boolean inside(Point p) {
        return p.x >= this.left && p.x <= this.right && p.y >= this.top && p.y <= this.bottom;
    }

    public boolean inside(int cell) {
        return this.inside(new Point(Dungeon.level.cellToPoint(cell)));
    }

    @Override
    public Point center() {
        return new Point((this.left + this.right) / 2 + ((this.right - this.left) % 2 == 1 ? Random.Int(2) : 0), (this.top + this.bottom) / 2 + ((this.bottom - this.top) % 2 == 1 ? Random.Int(2) : 0));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("left", this.left);
        bundle.put("top", this.top);
        bundle.put("right", this.right);
        bundle.put("bottom", this.bottom);
        bundle.put("center", this.centerCell);
        bundle.put(TYPE, this.type);
        bundle.put(DEST_DEPTH, this.destDepth);
        bundle.put(DEST_BRANCH, this.destBranch);
        bundle.put(DEST_TYPE, this.destType);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.left = bundle.getInt("left");
        this.top = bundle.getInt("top");
        this.right = bundle.getInt("right");
        this.bottom = bundle.getInt("bottom");
        this.centerCell = bundle.getInt("center");
        this.type = bundle.getEnum(TYPE, Type.class);
        this.destDepth = bundle.getInt(DEST_DEPTH);
        this.destBranch = bundle.getInt(DEST_BRANCH);
        if (bundle.contains(DEST_TYPE)) {
            this.destType = bundle.getEnum(DEST_TYPE, Type.class);
        }
    }

    public static enum Type {
        SURFACE,
        REGULAR_ENTRANCE,
        REGULAR_EXIT,
        BRANCH_ENTRANCE,
        BRANCH_EXIT;

    }
}

