/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LineBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Group;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class LastShopLevel
extends RegularLevel {
    public LastShopLevel() {
        this.color1 = 4941366;
        this.color2 = 0xF2F2F2;
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_city.png";
    }

    @Override
    public String waterTex() {
        return "environment/water3.png";
    }

    @Override
    protected boolean build() {
        this.feeling = Level.Feeling.CHASM;
        if (super.build()) {
            for (int i = 0; i < this.length(); ++i) {
                if (this.map[i] != 16) continue;
                this.map[i] = 5;
            }
            return true;
        }
        return false;
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> rooms = new ArrayList<Room>();
        this.roomEntrance = new EntranceRoom();
        rooms.add(this.roomEntrance);
        rooms.add(new ImpShopRoom());
        this.roomExit = new ExitRoom();
        rooms.add(this.roomExit);
        return rooms;
    }

    @Override
    protected Builder builder() {
        return new LineBuilder().setPathVariance(0.0f).setPathLength(1.0f, new float[]{1.0f}).setTunnelLength(new float[]{0.0f, 0.0f, 1.0f}, new float[]{1.0f});
    }

    @Override
    protected Painter painter() {
        return new CityPainter().setWater(0.1f, 4).setGrass(0.1f, 3);
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            int pos;
            while ((pos = this.pointToCell(this.roomEntrance.random())) == this.entrance()) {
            }
            for (Item i : bonesItems) {
                this.drop((Item)i, (int)pos).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();
    }

    @Override
    public int randomRespawnCell(Char ch) {
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (Point p : this.roomEntrance.getPoints()) {
            int cell = this.pointToCell(p);
            if (!this.passable[cell] || Actor.findChar(cell) != null || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell]) continue;
            candidates.add(cell);
        }
        if (candidates.isEmpty()) {
            return -1;
        }
        return (Integer)Random.element(candidates);
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(CityLevel.class, "water_name", new Object[0]);
            }
            case 15: {
                return Messages.get(CityLevel.class, "high_grass_name", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(CityLevel.class, "region_deco_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case 7: 
            case 37: {
                return Messages.get(CityLevel.class, "entrance_desc", new Object[0]);
            }
            case 8: {
                return Messages.get(CityLevel.class, "exit_desc", new Object[0]);
            }
            case 12: 
            case 20: {
                return Messages.get(CityLevel.class, "deco_desc", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(CityLevel.class, "statue_desc", new Object[0]);
            }
            case 27: {
                return Messages.get(CityLevel.class, "bookshelf_desc", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(CityLevel.class, "region_deco_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        CityLevel.addCityVisuals(this, this.visuals);
        return this.visuals;
    }

    @Override
    public Group addWallVisuals() {
        super.addWallVisuals();
        CityLevel.addCityWallVisuals(this, this.wallVisuals);
        return this.wallVisuals;
    }
}

