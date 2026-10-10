/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.HashMap;

public class ShopRoom
extends SpecialRoom {
    protected ArrayList<Item> itemsToSpawn;

    @Override
    public int minWidth() {
        return Math.max(7, (int)(Math.sqrt(this.spacesNeeded()) + 3.0));
    }

    @Override
    public int minHeight() {
        return Math.max(7, (int)(Math.sqrt(this.spacesNeeded()) + 3.0));
    }

    public int spacesNeeded() {
        if (this.itemsToSpawn == null) {
            this.itemsToSpawn = ShopRoom.generateItems();
        }
        int spacesNeeded = this.itemsToSpawn.size();
        for (Item i : this.itemsToSpawn) {
            if (!(i instanceof TimekeepersHourglass.sandBag)) continue;
            --spacesNeeded;
        }
        spacesNeeded += 4;
        return ++spacesNeeded;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        this.placeShopkeeper(level);
        this.placeItems(level);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }

    protected void placeShopkeeper(Level level) {
        int pos = level.pointToCell(this.center());
        Shopkeeper shopkeeper = new Shopkeeper();
        shopkeeper.pos = pos;
        level.mobs.add(shopkeeper);
    }

    protected void placeItems(Level level) {
        if (this.itemsToSpawn == null) {
            this.itemsToSpawn = ShopRoom.generateItems();
        }
        Point entryInset = new Point(this.entrance());
        if (entryInset.y == this.top) {
            ++entryInset.y;
        } else if (entryInset.y == this.bottom) {
            --entryInset.y;
        } else {
            entryInset.x = entryInset.x == this.left ? ++entryInset.x : --entryInset.x;
        }
        Point curItemPlace = entryInset.clone();
        int inset = 1;
        for (Item item : this.itemsToSpawn.toArray(new Item[0])) {
            int cell;
            if (curItemPlace.x == this.left + inset && curItemPlace.y != this.top + inset) {
                --curItemPlace.y;
            } else if (curItemPlace.y == this.top + inset && curItemPlace.x != this.right - inset) {
                ++curItemPlace.x;
            } else if (curItemPlace.x == this.right - inset && curItemPlace.y != this.bottom - inset) {
                ++curItemPlace.y;
            } else {
                --curItemPlace.x;
            }
            if (curItemPlace.equals(entryInset)) {
                if (entryInset.y == this.top + inset) {
                    ++entryInset.y;
                } else if (entryInset.y == this.bottom - inset) {
                    --entryInset.y;
                }
                if (entryInset.x == this.left + inset) {
                    ++entryInset.x;
                } else if (entryInset.x == this.right - inset) {
                    --entryInset.x;
                }
                if (++inset > (Math.min(this.width(), this.height()) - 3) / 2) break;
                curItemPlace = entryInset.clone();
                if (curItemPlace.x == this.left + inset && curItemPlace.y != this.top + inset) {
                    --curItemPlace.y;
                } else if (curItemPlace.y == this.top + inset && curItemPlace.x != this.right - inset) {
                    ++curItemPlace.x;
                } else if (curItemPlace.x == this.right - inset && curItemPlace.y != this.bottom - inset) {
                    ++curItemPlace.y;
                } else {
                    --curItemPlace.x;
                }
            }
            if (level.map[cell = level.pointToCell(curItemPlace)] == 15) {
                Level.set(cell, 2, level);
                GameScene.updateMap(cell);
            }
            level.drop((Item)item, (int)cell).type = Heap.Type.FOR_SALE;
            this.itemsToSpawn.remove(item);
        }
        if (!this.itemsToSpawn.isEmpty()) {
            for (Point p : this.getPoints()) {
                int cell = level.pointToCell(p);
                if ((level.map[cell] == 14 || level.map[cell] == 1) && level.heaps.get(cell) == null && level.findMob(cell) == null) {
                    level.drop((Item)this.itemsToSpawn.remove((int)0), (int)level.pointToCell((Point)p)).type = Heap.Type.FOR_SALE;
                }
                if (!this.itemsToSpawn.isEmpty()) continue;
                break;
            }
        }
        if (!this.itemsToSpawn.isEmpty()) {
            ShatteredPixelDungeon.reportException(new RuntimeException("failed to place all items in a shop!"));
        }
    }

    protected static ArrayList<Item> generateItems() {
        Item rare;
        MissileWeapon m;
        MeleeWeapon w;
        ArrayList<Item> itemsToSpawn = new ArrayList<Item>();
        switch (Dungeon.depth) {
            default: {
                w = (MeleeWeapon)Generator.random(Generator.wepTiers[1]);
                m = (MissileWeapon)Generator.random(Generator.misTiers[1]);
                itemsToSpawn.add(new LeatherArmor().identify(false));
                break;
            }
            case 11: {
                w = (MeleeWeapon)Generator.random(Generator.wepTiers[2]);
                m = (MissileWeapon)Generator.random(Generator.misTiers[2]);
                itemsToSpawn.add(new MailArmor().identify(false));
                break;
            }
            case 16: {
                w = (MeleeWeapon)Generator.random(Generator.wepTiers[3]);
                m = (MissileWeapon)Generator.random(Generator.misTiers[3]);
                itemsToSpawn.add(new ScaleArmor().identify(false));
                break;
            }
            case 20: 
            case 21: {
                w = (MeleeWeapon)Generator.random(Generator.wepTiers[4]);
                m = (MissileWeapon)Generator.random(Generator.misTiers[4]);
                itemsToSpawn.add(new PlateArmor().identify(false));
                itemsToSpawn.add(new Torch());
                itemsToSpawn.add(new Torch());
                itemsToSpawn.add(new Torch());
            }
        }
        w.enchant(null);
        w.cursed = false;
        w.level(0);
        w.identify(false);
        itemsToSpawn.add(w);
        m.enchant(null);
        m.cursed = false;
        m.level(0);
        m.identify(false);
        itemsToSpawn.add(m);
        itemsToSpawn.add(TippedDart.randomTipped(2));
        itemsToSpawn.add(new Alchemize().quantity(Random.IntRange(2, 3)));
        Bag bag = ShopRoom.ChooseBag(Dungeon.hero.belongings);
        if (bag != null) {
            itemsToSpawn.add(bag);
        }
        itemsToSpawn.add(new PotionOfHealing());
        itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.POTION));
        itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.POTION));
        itemsToSpawn.add(new ScrollOfIdentify());
        itemsToSpawn.add(new ScrollOfRemoveCurse());
        itemsToSpawn.add(new ScrollOfMagicMapping());
        for (int i = 0; i < 2; ++i) {
            itemsToSpawn.add(Random.Int(2) == 0 ? Generator.randomUsingDefaults(Generator.Category.POTION) : Generator.randomUsingDefaults(Generator.Category.SCROLL));
        }
        itemsToSpawn.add(new SmallRation());
        itemsToSpawn.add(new SmallRation());
        switch (Random.Int(4)) {
            case 0: {
                itemsToSpawn.add(new Bomb());
                break;
            }
            case 1: 
            case 2: {
                itemsToSpawn.add(new Bomb.DoubleBomb());
                break;
            }
            case 3: {
                itemsToSpawn.add(new Honeypot());
            }
        }
        itemsToSpawn.add(new Ankh());
        itemsToSpawn.add(new StoneOfAugmentation());
        TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
        if (hourglass != null && hourglass.isIdentified() && !hourglass.cursed) {
            int bags = 0;
            switch (Dungeon.depth) {
                case 6: {
                    bags = (int)Math.ceil((float)(5 - hourglass.sandBags) * 0.2f);
                    break;
                }
                case 11: {
                    bags = (int)Math.ceil((float)(5 - hourglass.sandBags) * 0.25f);
                    break;
                }
                case 16: {
                    bags = (int)Math.ceil((float)(5 - hourglass.sandBags) * 0.5f);
                    break;
                }
                case 20: 
                case 21: {
                    bags = (int)Math.ceil((float)(5 - hourglass.sandBags) * 0.8f);
                }
            }
            for (int i = 1; i <= bags; ++i) {
                itemsToSpawn.add(new TimekeepersHourglass.sandBag());
                ++hourglass.sandBags;
            }
        }
        switch (Random.Int(10)) {
            case 0: {
                rare = Generator.random(Generator.Category.WAND);
                rare.level(0);
                break;
            }
            case 1: {
                rare = Generator.random(Generator.Category.RING);
                rare.level(0);
                break;
            }
            case 2: {
                rare = Generator.random(Generator.Category.ARTIFACT);
                break;
            }
            default: {
                rare = new Stylus();
            }
        }
        rare.cursed = false;
        rare.cursedKnown = true;
        itemsToSpawn.add(rare);
        Random.pushGenerator(Random.Long());
        Random.shuffle(itemsToSpawn);
        Random.popGenerator();
        return itemsToSpawn;
    }

    protected static Bag ChooseBag(Belongings pack) {
        HashMap<Bag, Integer> bags = new HashMap<Bag, Integer>();
        if (!Dungeon.LimitedDrops.VELVET_POUCH.dropped()) {
            bags.put(new VelvetPouch(), 1);
        }
        if (!Dungeon.LimitedDrops.SCROLL_HOLDER.dropped()) {
            bags.put(new ScrollHolder(), 0);
        }
        if (!Dungeon.LimitedDrops.POTION_BANDOLIER.dropped()) {
            bags.put(new PotionBandolier(), 0);
        }
        if (!Dungeon.LimitedDrops.MAGICAL_HOLSTER.dropped()) {
            bags.put(new MagicalHolster(), 0);
        }
        if (bags.isEmpty()) {
            return null;
        }
        for (Item item : pack.backpack.items) {
            for (Bag bag : bags.keySet()) {
                if (!bag.canHold(item)) continue;
                bags.put(bag, (Integer)bags.get(bag) + 1);
            }
        }
        Bag bestBag = null;
        for (Bag bag : bags.keySet()) {
            if (bestBag == null) {
                bestBag = bag;
                continue;
            }
            if ((Integer)bags.get(bag) <= (Integer)bags.get(bestBag)) continue;
            bestBag = bag;
        }
        if (bestBag instanceof VelvetPouch) {
            Dungeon.LimitedDrops.VELVET_POUCH.drop();
        } else if (bestBag instanceof ScrollHolder) {
            Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
        } else if (bestBag instanceof PotionBandolier) {
            Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
        } else if (bestBag instanceof MagicalHolster) {
            Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
        }
        return bestBag;
    }
}

