/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class ToxicGasRoom
extends SpecialRoom {
    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        int i;
        int cell;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.set(level, this.center(), 25);
        for (Point p : this.getPoints()) {
            cell = level.pointToCell(p);
            if (level.map[cell] != 1) continue;
            Blob.seed(cell, 30, ToxicGas.class, level);
        }
        int traps = Math.min(this.width() - 2, this.height() - 2);
        for (int i2 = 0; i2 < traps; ++i2) {
            while (level.map[cell = level.pointToCell(this.random(2))] != 1) {
            }
            level.setTrap(new ToxicVent().reveal(), cell);
            Blob.seed(cell, 12, ToxicGasSeed.class, level);
            Painter.set(level, cell, 19);
        }
        ArrayList<Integer> goldPositions = new ArrayList<Integer>();
        for (int i3 = 0; i3 < 8; ++i3) {
            int posToAdd;
            while (level.map[posToAdd = level.pointToCell(this.random(2))] == 25 || goldPositions.contains(posToAdd)) {
            }
            goldPositions.add(posToAdd);
        }
        int furthestPos = -1;
        int entryPos = level.pointToCell(this.entrance());
        Iterator iterator = goldPositions.iterator();
        while (iterator.hasNext()) {
            i = (Integer)iterator.next();
            if (furthestPos != -1 && !(level.trueDistance(entryPos, i) > level.trueDistance(entryPos, furthestPos))) continue;
            furthestPos = i;
        }
        goldPositions.remove((Object)furthestPos);
        Item mainGold = new Gold().random();
        mainGold.quantity(mainGold.quantity() * 2);
        level.drop((Item)mainGold, (int)furthestPos).type = Heap.Type.SKELETON;
        for (i = 0; i < 2; ++i) {
            Item item = level.findPrizeItem(TrinketCatalyst.class);
            if (item == null) {
                item = new Gold().random();
            }
            level.drop((Item)item, (int)((Integer)goldPositions.remove((int)0)).intValue()).type = Heap.Type.CHEST;
        }
        level.addItemToSpawn(new PotionOfPurity());
        this.entrance().set(Room.Door.Type.REGULAR);
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        if (!super.canPlaceCharacter(p, l)) {
            return false;
        }
        Blob gas = l.blobs.get(ToxicGas.class);
        return gas == null || gas.volume == 0 || gas.cur[l.pointToCell(p)] == 0;
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return false;
    }

    public static class ToxicVent
    extends Trap {
        public ToxicVent() {
            this.color = 8;
            this.shape = 2;
            this.canBeHidden = false;
            this.active = false;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", new Object[0]);
        }

        @Override
        public void activate() {
        }
    }

    public static class ToxicGasSeed
    extends Blob {
        @Override
        protected void evolve() {
            ToxicGas gas = (ToxicGas)Dungeon.level.blobs.get(ToxicGas.class);
            for (int i = this.area.top - 1; i <= this.area.bottom; ++i) {
                for (int j = this.area.left - 1; j <= this.area.right; ++j) {
                    int cell = j + i * Dungeon.level.width();
                    if (!Dungeon.level.insideMap(cell)) continue;
                    if (Dungeon.level.map[cell] != 19) {
                        this.off[cell] = 0;
                        continue;
                    }
                    this.off[cell] = this.cur[cell];
                    this.volume += this.off[cell];
                    if (gas == null || gas.volume == 0) {
                        GameScene.add(Blob.seed(cell, this.off[cell], ToxicGas.class));
                        continue;
                    }
                    if (gas.cur[cell] > 9 * this.off[cell]) continue;
                    GameScene.add(Blob.seed(cell, this.off[cell], ToxicGas.class));
                }
            }
        }
    }
}

