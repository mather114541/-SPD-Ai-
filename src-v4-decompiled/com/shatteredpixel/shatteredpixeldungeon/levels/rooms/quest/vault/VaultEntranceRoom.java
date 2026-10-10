/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.VaultBeacon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.AmbitiousImpRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class VaultEntranceRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        int entrance;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 2, 1);
        Point c = this.center();
        Painter.set(level, c.x - 3, c.y - 3, 4);
        Painter.set(level, c.x + 3, c.y - 3, 4);
        Painter.set(level, c.x - 3, c.y + 3, 4);
        Painter.set(level, c.x + 3, c.y + 3, 4);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 3, 1);
        }
        Painter.set(level, c.x - 2, c.y - 2, 33);
        Painter.set(level, c.x + 2, c.y - 2, 33);
        Painter.set(level, c.x - 2, c.y + 2, 33);
        Painter.set(level, c.x + 2, c.y + 2, 33);
        Painter.fill(level, this.left + 2, this.top + 4, 7, 3, 32);
        Painter.fill(level, this.left + 4, this.top + 2, 3, 7, 32);
        Carpet carpet = new Carpet();
        carpet.setRect(this.left + 2, this.top + 4, 7, 3);
        level.customTiles.add(carpet);
        carpet = new Carpet();
        carpet.setRect(this.left + 4, this.top + 2, 3, 7);
        level.customTiles.add(carpet);
        QuestEntranceInternal vis = new QuestEntranceInternal();
        vis.pos(c.x - 1, c.y - 1);
        level.customTiles.add(vis);
        AmbitiousImpRoom.WallBanners vis2 = new AmbitiousImpRoom.WallBanners();
        vis2.pos(this.left + 2, this.top + 1);
        level.customTerrain.add(vis2);
        ArrayList<Point> pedestalCandidates = new ArrayList<Point>();
        pedestalCandidates.add(new Point(this.left + 2, c.y));
        pedestalCandidates.add(new Point(this.right - 2, c.y));
        pedestalCandidates.add(new Point(c.x, this.top + 2));
        pedestalCandidates.add(new Point(c.x, this.bottom - 2));
        Point furthest = null;
        float furthestDist = 0.0f;
        for (Point p : pedestalCandidates) {
            float dist = 0.0f;
            for (Room.Door d : this.connected.values()) {
                dist += Point.distance(p, d);
            }
            if (furthest != null && !(dist > furthestDist)) continue;
            furthest = p;
            furthestDist = dist;
        }
        int ofs = 0;
        ofs = furthest.x == c.x ? 1 : level.width();
        boolean addTorch = Dungeon.isChallenged(32);
        switch (Random.Int(3)) {
            case 0: {
                if (addTorch) {
                    level.drop(new Torch(), level.pointToCell(furthest) - ofs);
                }
                level.drop(new VaultBeacon(), level.pointToCell(furthest));
                level.drop(new VaultBeacon(), level.pointToCell(furthest) + ofs);
                break;
            }
            case 1: {
                level.drop(new VaultBeacon(), level.pointToCell(furthest) - ofs);
                if (addTorch) {
                    level.drop(new Torch(), level.pointToCell(furthest));
                }
                level.drop(new VaultBeacon(), level.pointToCell(furthest) + ofs);
                break;
            }
            case 2: {
                level.drop(new VaultBeacon(), level.pointToCell(furthest) - ofs);
                level.drop(new VaultBeacon(), level.pointToCell(furthest));
                if (!addTorch) break;
                level.drop(new Torch(), level.pointToCell(furthest) + ofs);
            }
        }
        while (level.findMob(entrance = level.pointToCell(this.center())) != null) {
        }
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.BRANCH_ENTRANCE, Dungeon.depth, 0, LevelTransition.Type.BRANCH_EXIT));
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public boolean canConnect(Point p) {
        return p.x > this.left + 1 && p.x < this.right - 1 || p.y > this.top + 1 && p.y < this.bottom - 1;
    }

    public static class QuestEntranceInternal
    extends CustomTilemap {
        final int TEX_WIDTH = 256;

        public QuestEntranceInternal() {
            this.texture = "environment/custom_tiles/city_quest.png";
            this.tileH = 3;
            this.tileW = 3;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(this.mapSimpleImage(8, 1, 256), 3);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(this, "name", new Object[0]);
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(this, "desc", new Object[0]);
        }
    }
}

