/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ImpShopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ImpShopRoom
extends ShopRoom {
    private boolean impSpawned = false;
    private static final String IMP = "imp_spawned";
    private static final String ITEMS = "items";

    @Override
    public int minWidth() {
        return 9;
    }

    @Override
    public int minHeight() {
        return 9;
    }

    @Override
    public int maxWidth() {
        return 9;
    }

    @Override
    public int maxHeight() {
        return 9;
    }

    @Override
    public int maxConnections(int direction) {
        return 2;
    }

    @Override
    public void paint(Level level) {
        if (this.itemsToSpawn == null) {
            this.itemsToSpawn = ImpShopRoom.generateItems();
        }
    }

    @Override
    protected void placeShopkeeper(Level level) {
        int pos = level.pointToCell(this.center());
        for (Point p : this.getPoints()) {
            if (level.map[level.pointToCell(p)] != 11) continue;
            pos = level.pointToCell(p);
            break;
        }
        ImpShopkeeper shopkeeper = new ImpShopkeeper();
        shopkeeper.pos = pos;
        if (ShatteredPixelDungeon.scene() instanceof GameScene) {
            GameScene.add(shopkeeper);
        } else {
            level.mobs.add(shopkeeper);
        }
    }

    @Override
    public Room.Door entrance() {
        return this.connected.isEmpty() ? new Room.Door((this.left + this.right) / 2 + 1, this.bottom - 1) : super.entrance();
    }

    public void spawnShop(Level level) {
        this.impSpawned = true;
        this.placeShopkeeper(level);
        this.placeItems(level);
    }

    @Override
    protected void placeItems(Level level) {
        ArrayList<Item> existing = new ArrayList<Item>();
        for (Heap h : level.heaps.valueList()) {
            if (!this.inside(level.cellToPoint(h.pos))) continue;
            existing.addAll(h.items);
            level.heaps.remove(h.pos);
            if (h.sprite == null) continue;
            h.sprite.kill();
        }
        super.placeItems(level);
        if (!existing.isEmpty()) {
            int pos = level.pointToCell(new Point(this.left + 2, this.bottom + 2));
            for (Item i : existing) {
                level.drop(i, pos + Random.Int(5));
            }
        }
    }

    public boolean shopSpawned() {
        return this.impSpawned;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(IMP, this.impSpawned);
        if (this.itemsToSpawn != null) {
            bundle.put(ITEMS, this.itemsToSpawn);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.impSpawned = bundle.getBoolean(IMP);
        if (bundle.contains(ITEMS)) {
            this.itemsToSpawn = new ArrayList<Bundlable>(bundle.getCollection(ITEMS));
        }
    }

    @Override
    public void onLevelLoad(Level level) {
        super.onLevelLoad(level);
        if (Imp.Quest.isCompleted() && Imp.Quest.earnedShop() && !this.impSpawned) {
            this.spawnShop(level);
        }
    }
}

