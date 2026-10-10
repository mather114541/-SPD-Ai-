/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class MassGraveRoom
extends SpecialRoom {
    @Override
    public int minWidth() {
        return 11;
    }

    @Override
    public int maxWidth() {
        return 11;
    }

    @Override
    public int minHeight() {
        return 10;
    }

    @Override
    public int maxHeight() {
        return 10;
    }

    @Override
    public void paint(Level level) {
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.BARRICADE);
        level.addItemToSpawn(new PotionOfLiquidFlame());
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 32);
        Painter.fill(level, this.left + 1, this.top + 1, 3, 1, 4);
        Painter.fill(level, this.left + 1, this.top + 2, 2, 1, 4);
        Painter.fill(level, this.right - 3, this.top + 1, 3, 1, 4);
        Painter.fill(level, this.right - 2, this.top + 2, 2, 1, 4);
        Painter.set(level, this.left + 5, this.top, 12);
        Painter.set(level, this.left + 3, this.top + 2, 25);
        Painter.set(level, this.right - 3, this.top + 2, 25);
        MassGraveDeco b = new MassGraveDeco();
        b.setRect(this.left + 1, this.top, this.width() - 2, this.height() - 1);
        level.customTiles.add(b);
        StatueRaised statue = new StatueRaised();
        statue.setRect(this.left + 3, this.top + 2, 1, 1);
        level.customTerrain.add(statue);
        statue = new StatueRaised();
        statue.setRect(this.right - 3, this.top + 2, 1, 1);
        level.customTerrain.add(statue);
        for (int i = 0; i <= Random.Int(2); ++i) {
            int pos;
            Point p;
            Skeleton skele = new Skeleton();
            do {
                p = this.random(1);
                if (p.y <= this.top + 2) {
                    p.x = Random.IntRange(this.left + 4, this.right - 4);
                }
                pos = level.pointToCell(p);
            } while (p.y > this.top + 3 || level.findMob(pos) != null);
            skele.pos = pos;
            level.mobs.add(skele);
        }
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new CorpseDust());
        items.add(new Gold(1));
        items.add(new Gold(1));
        if (Random.Float() <= 0.3f) {
            items.add(new Gold());
        }
        if (Random.Float() <= 0.3f) {
            items.add(new Gold());
        }
        if (Random.Float() <= 0.6f) {
            items.add(Generator.random());
        }
        if (Random.Float() <= 0.3f) {
            items.add(Generator.randomArmor());
        }
        for (Item item : items) {
            int pos;
            Point p;
            do {
                p = this.random(1);
                if (p.y <= this.top + 2) {
                    p.x = Random.IntRange(this.left + 4, this.right - 4);
                }
                pos = level.pointToCell(p);
            } while (p.y > this.top + 5 || level.heaps.get(pos) != null);
            Heap h = level.drop(item, pos);
            h.setHauntedIfCursed();
            h.type = Heap.Type.SKELETON;
        }
    }

    @Override
    public boolean canConnect(int direction) {
        return super.canConnect(direction) && direction == 4;
    }

    @Override
    public boolean canConnect(Point p) {
        return Math.abs(p.x - this.center().x) <= 2;
    }

    @Override
    public boolean canConnect(Room r) {
        if (r.isEntrance()) {
            return false;
        }
        for (Room r1 : r.connected.keySet()) {
            if (r1.isEntrance()) {
                return false;
            }
            for (Room r2 : r1.connected.keySet()) {
                if (r2.isEntrance()) {
                    return false;
                }
                for (Room r3 : r2.connected.keySet()) {
                    if (!r3.isEntrance()) continue;
                    return false;
                }
            }
        }
        return super.canConnect(r);
    }

    public static class MassGraveDeco
    extends CustomTilemap {
        private static byte[] render = new byte[]{0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1};

        public MassGraveDeco() {
            this.texture = "environment/custom_tiles/prison_quest.png";
            this.tileW = 9;
            this.tileH = 9;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = this.mapSimpleImage(5, 0, 256);
            for (int i = 0; i < data.length; ++i) {
                if (render[i] != 0) continue;
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            if (render[tileX + tileY * this.tileH] == 1) {
                return Messages.get(this, "name", new Object[0]);
            }
            return super.name(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            if (render[tileX + tileY * this.tileH] == 1) {
                return Messages.get(this, "desc", new Object[0]);
            }
            return super.desc(tileX, tileY);
        }
    }

    public static class StatueRaised
    extends CustomTilemap {
        public StatueRaised() {
            this.texture = "environment/custom_tiles/prison_quest.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            Arrays.fill(data, 4);
            v.map(data, this.tileW);
            return v;
        }
    }

    public static class Bones
    extends CustomTilemap {
        private static final int WALL_OVERLAP = 3;
        private static final int FLOOR = 7;

        public Bones() {
            this.texture = "environment/custom_tiles/prison_quest.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            for (int i = 0; i < data.length; ++i) {
                data[i] = i < this.tileW ? 3 : 7;
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            if (tileY == 0) {
                return null;
            }
            return super.image(tileX, tileY);
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(MassGraveDeco.class, "name", new Object[0]);
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(MassGraveDeco.class, "desc", new Object[0]);
        }
    }
}

