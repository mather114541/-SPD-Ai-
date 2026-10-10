/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CrystalPathRoom
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
        Painter.fill(level, this, 4);
        EmptyRoom[] rooms = new EmptyRoom[6];
        for (int i = 0; i < rooms.length; ++i) {
            rooms[i] = new EmptyRoom();
        }
        Point entry = new Point(this.entrance());
        int prize1 = 0;
        int prize2 = 0;
        if (entry.x == this.left || entry.x == this.right) {
            int roomH;
            Painter.drawInside(level, this, entry, this.width() > 8 ? 5 : 3, 1);
            int roomW1 = this.width() >= 9 ? 2 : 1;
            int roomW2 = this.width() % 2 == 0 ? 2 : 1;
            int n = roomH = this.height() >= 9 ? 2 : 1;
            if (entry.x == this.left) {
                rooms[0].setPos(this.left + 1, entry.y - roomH - 1).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[0].left, rooms[0].bottom + 1, 31);
                rooms[1].setPos(this.left + 1, entry.y + 2).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[1].left, rooms[1].top - 1, 31);
                rooms[2].setPos(rooms[1].right + 2, entry.y - roomH - 1).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[2].left, rooms[2].bottom + 1, 31);
                rooms[3].setPos(rooms[1].right + 2, entry.y + 2).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[3].left, rooms[3].top - 1, 31);
                rooms[4].setPos(rooms[3].right + 2, entry.y - roomH - 1).resize(roomW2 - 1, roomH);
                Painter.set(level, rooms[4].left - 1, rooms[4].bottom - 1, 31);
                rooms[5].setPos(rooms[3].right + 2, entry.y + 1).resize(roomW2 - 1, roomH);
                Painter.set(level, rooms[5].left - 1, rooms[5].top + 1, 31);
                prize1 = level.pointToCell(new Point(rooms[4].left, rooms[4].bottom));
                prize2 = level.pointToCell(new Point(rooms[5].left, rooms[5].top));
            } else {
                rooms[0].setPos(this.right - roomW1, entry.y - roomH - 1).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[0].right, rooms[0].bottom + 1, 31);
                rooms[1].setPos(this.right - roomW1, entry.y + 2).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[1].right, rooms[1].top - 1, 31);
                rooms[2].setPos(rooms[1].left - roomW1 - 1, entry.y - roomH - 1).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[2].right, rooms[2].bottom + 1, 31);
                rooms[3].setPos(rooms[1].left - roomW1 - 1, entry.y + 2).resize(roomW1 - 1, roomH - 1);
                Painter.set(level, rooms[3].right, rooms[3].top - 1, 31);
                rooms[4].setPos(rooms[3].left - roomW2 - 1, entry.y - roomH - 1).resize(roomW2 - 1, roomH);
                Painter.set(level, rooms[4].right + 1, rooms[4].bottom - 1, 31);
                rooms[5].setPos(rooms[3].left - roomW2 - 1, entry.y + 1).resize(roomW2 - 1, roomH);
                Painter.set(level, rooms[5].right + 1, rooms[5].top + 1, 31);
                prize1 = level.pointToCell(new Point(rooms[4].right, rooms[4].bottom));
                prize2 = level.pointToCell(new Point(rooms[5].right, rooms[5].top));
            }
        } else {
            int roomH2;
            Painter.drawInside(level, this, entry, this.height() > 8 ? 5 : 3, 1);
            int roomW = this.width() >= 9 ? 2 : 1;
            int roomH1 = this.height() >= 9 ? 2 : 1;
            int n = roomH2 = this.height() % 2 == 0 ? 2 : 1;
            if (entry.y == this.top) {
                rooms[0].setPos(entry.x - roomW - 1, this.top + 1).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[0].right + 1, rooms[0].top, 31);
                rooms[1].setPos(entry.x + 2, this.top + 1).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[1].left - 1, rooms[1].top, 31);
                rooms[2].setPos(entry.x - roomW - 1, rooms[1].bottom + 2).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[2].right + 1, rooms[2].top, 31);
                rooms[3].setPos(entry.x + 2, rooms[1].bottom + 2).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[3].left - 1, rooms[3].top, 31);
                rooms[4].setPos(entry.x - roomW - 1, rooms[3].bottom + 2).resize(roomW, roomH2 - 1);
                Painter.set(level, rooms[4].right - 1, rooms[4].top - 1, 31);
                rooms[5].setPos(entry.x + 1, rooms[3].bottom + 2).resize(roomW, roomH2 - 1);
                Painter.set(level, rooms[5].left + 1, rooms[5].top - 1, 31);
                prize1 = level.pointToCell(new Point(rooms[4].right, rooms[4].top));
                prize2 = level.pointToCell(new Point(rooms[5].left, rooms[5].top));
            } else {
                rooms[0].setPos(entry.x - roomW - 1, this.bottom - roomH1).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[0].right + 1, rooms[0].bottom, 31);
                rooms[1].setPos(entry.x + 2, this.bottom - roomH1).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[1].left - 1, rooms[1].bottom, 31);
                rooms[2].setPos(entry.x - roomW - 1, rooms[1].top - roomH1 - 1).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[2].right + 1, rooms[2].bottom, 31);
                rooms[3].setPos(entry.x + 2, rooms[1].top - roomH1 - 1).resize(roomW - 1, roomH1 - 1);
                Painter.set(level, rooms[3].left - 1, rooms[3].bottom, 31);
                rooms[4].setPos(entry.x - roomW - 1, rooms[3].top - roomH2 - 1).resize(roomW, roomH2 - 1);
                Painter.set(level, rooms[4].right - 1, rooms[4].bottom + 1, 31);
                rooms[5].setPos(entry.x + 1, rooms[3].top - roomH2 - 1).resize(roomW, roomH2 - 1);
                Painter.set(level, rooms[5].left + 1, rooms[5].bottom + 1, 31);
                prize1 = level.pointToCell(new Point(rooms[4].right, rooms[4].bottom));
                prize2 = level.pointToCell(new Point(rooms[5].left, rooms[5].bottom));
            }
        }
        for (EmptyRoom room : rooms) {
            Painter.fill(level, room, 14);
        }
        Painter.set(level, prize1, 11);
        Painter.set(level, prize2, 11);
        ArrayList<Item> potions = new ArrayList<Item>();
        ArrayList<Item> scrolls = new ArrayList<Item>();
        ArrayList<Item> duplicates = new ArrayList<Item>();
        if (Random.Int(2) == 0) {
            this.addRewardItem(Generator.Category.POTION, potions, duplicates);
            scrolls.add(Random.Float() < ExoticCrystals.consumableExoticChance() ? new ScrollOfMetamorphosis() : new ScrollOfTransmutation());
        } else {
            potions.add(Random.Float() < ExoticCrystals.consumableExoticChance() ? new PotionOfDivineInspiration() : new PotionOfExperience());
            this.addRewardItem(Generator.Category.SCROLL, scrolls, duplicates);
        }
        this.addRewardItem(Generator.Category.POTION, potions, duplicates);
        this.addRewardItem(Generator.Category.SCROLL, scrolls, duplicates);
        this.addRewardItem(Generator.Category.POTION, potions, duplicates);
        this.addRewardItem(Generator.Category.SCROLL, scrolls, duplicates);
        for (Item i : duplicates) {
            if (i instanceof ExoticPotion) {
                Generator.undoDrop(ExoticPotion.exoToReg.get(i.getClass()));
                continue;
            }
            if (i instanceof ExoticScroll) {
                Generator.undoDrop(ExoticScroll.exoToReg.get(i.getClass()));
                continue;
            }
            Generator.undoDrop(i);
        }
        Collections.sort(potions, new Comparator<Item>(){

            @Override
            public int compare(Item a, Item b) {
                int aVal = 0;
                int bVal = 0;
                Class<Object> aCls = a.getClass();
                Class<Object> bCls = b.getClass();
                if (a instanceof ExoticPotion) {
                    aCls = ExoticPotion.exoToReg.get(aCls);
                }
                if (b instanceof ExoticPotion) {
                    bCls = ExoticPotion.exoToReg.get(bCls);
                }
                for (int i = 0; i < Generator.Category.POTION.classes.length; ++i) {
                    if (aCls == Generator.Category.POTION.classes[i]) {
                        aVal = (int)Generator.Category.POTION.defaultProbsTotal[i];
                    }
                    if (bCls != Generator.Category.POTION.classes[i]) continue;
                    bVal = (int)Generator.Category.POTION.defaultProbsTotal[i];
                }
                return bVal - aVal;
            }
        });
        Collections.sort(scrolls, new Comparator<Item>(){

            @Override
            public int compare(Item a, Item b) {
                int aVal = 0;
                int bVal = 0;
                Class<Object> aCls = a.getClass();
                Class<Object> bCls = b.getClass();
                if (a instanceof ExoticScroll) {
                    aCls = ExoticScroll.exoToReg.get(aCls);
                }
                if (b instanceof ExoticScroll) {
                    bCls = ExoticScroll.exoToReg.get(bCls);
                }
                for (int i = 0; i < Generator.Category.SCROLL.classes.length; ++i) {
                    if (aCls == Generator.Category.SCROLL.classes[i]) {
                        aVal = (int)Generator.Category.SCROLL.defaultProbsTotal[i];
                    }
                    if (bCls != Generator.Category.SCROLL.classes[i]) continue;
                    bVal = (int)Generator.Category.SCROLL.defaultProbsTotal[i];
                }
                return bVal - aVal;
            }
        });
        int shuffle = Random.Int(2);
        level.drop(potions.remove(0), level.pointToCell(rooms[shuffle == 1 ? 2 : 3].center()));
        level.drop(scrolls.remove(0), level.pointToCell(rooms[shuffle == 1 ? 3 : 2].center()));
        level.drop(potions.remove(0), level.pointToCell(rooms[shuffle == 1 ? 0 : 1].center()));
        level.drop(scrolls.remove(0), level.pointToCell(rooms[shuffle == 1 ? 1 : 0].center()));
        level.drop((Item)potions.remove((int)0), (int)(shuffle == 1 ? prize1 : prize2)).autoExplored = true;
        level.drop((Item)scrolls.remove((int)0), (int)(shuffle == 1 ? prize2 : prize1)).autoExplored = true;
        level.addItemToSpawn(new CrystalKey(Dungeon.depth));
        level.addItemToSpawn(new CrystalKey(Dungeon.depth));
        level.addItemToSpawn(new CrystalKey(Dungeon.depth));
        this.entrance().set(Room.Door.Type.REGULAR);
    }

    public void addRewardItem(Generator.Category cat, ArrayList<Item> items, ArrayList<Item> dupes) {
        Item reward;
        boolean dupe;
        block0: do {
            reward = Generator.random(cat);
            Class<Object> rewardClass = reward.getClass();
            if (reward instanceof ExoticPotion) {
                rewardClass = ExoticPotion.exoToReg.get(rewardClass);
            } else if (reward instanceof ExoticScroll) {
                rewardClass = ExoticScroll.exoToReg.get(rewardClass);
            }
            dupe = false;
            for (Item i : items) {
                Class<Object> iClass = i.getClass();
                if (i instanceof ExoticPotion) {
                    iClass = ExoticPotion.exoToReg.get(iClass);
                } else if (i instanceof ExoticScroll) {
                    iClass = ExoticScroll.exoToReg.get(iClass);
                }
                if (iClass != rewardClass) continue;
                dupes.add(reward);
                dupe = true;
                continue block0;
            }
        } while (dupe);
        items.add(reward);
    }

    @Override
    public boolean canConnect(Point p) {
        if (!super.canConnect(p)) {
            return false;
        }
        if (Math.abs((float)p.x - ((float)this.right - (float)(this.width() - 1) / 2.0f)) < 1.0f) {
            return true;
        }
        return Math.abs((float)p.y - ((float)this.bottom - (float)(this.height() - 1) / 2.0f)) < 1.0f;
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return false;
    }
}

