/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LostBackpack;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Heap
implements Bundlable {
    public Type type = Type.HEAP;
    public int pos = 0;
    public ItemSprite sprite;
    public boolean seen = false;
    public boolean haunted = false;
    public boolean autoExplored = false;
    public boolean hidden = false;
    public LinkedList<Item> items = new LinkedList();
    private static final String POS = "pos";
    private static final String SEEN = "seen";
    private static final String TYPE = "type";
    private static final String ITEMS = "items";
    private static final String HAUNTED = "haunted";
    private static final String AUTO_EXPLORED = "auto_explored";
    private static final String HIDDEN = "hidden";

    public void open(Hero hero) {
        switch (this.type.ordinal()) {
            case 5: {
                Wraith.spawnAround(hero.pos);
                break;
            }
            case 6: 
            case 7: {
                CellEmitter.center(this.pos).start(Speck.factory(105), 0.1f, 3);
                break;
            }
        }
        if (this.haunted) {
            if (Wraith.spawnAt(this.pos) == null) {
                hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
                hero.damage(hero.HP / 2, this);
                if (!hero.isAlive()) {
                    Dungeon.fail(Wraith.class);
                    GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", Messages.get(Wraith.class, "name", new Object[0]))), new Object[0]);
                }
            }
            Sample.INSTANCE.play("sounds/cursed.mp3");
        }
        this.type = Type.HEAP;
        ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(hero, 1);
        if (bonus != null && !bonus.isEmpty()) {
            this.items.addAll(0, bonus);
            RingOfWealth.showFlareForBonusDrop(this.sprite);
        }
        this.sprite.link();
        this.sprite.drop();
    }

    public Heap setHauntedIfCursed() {
        for (Item item : this.items) {
            if (!item.cursed) continue;
            this.haunted = true;
            item.cursedKnown = true;
            break;
        }
        return this;
    }

    public int size() {
        return this.items.size();
    }

    public Item pickUp() {
        if (this.items.isEmpty()) {
            this.destroy();
            return null;
        }
        Item item = this.items.removeFirst();
        if (this.items.isEmpty()) {
            this.destroy();
        } else if (this.sprite != null) {
            this.sprite.view(this).place(this.pos);
        }
        return item;
    }

    public Item peek() {
        return this.items.peek();
    }

    public void drop(Item item) {
        this.hidden = false;
        if (item.stackable && this.type != Type.FOR_SALE) {
            for (Item i : this.items) {
                if (!i.isSimilar(item)) continue;
                item = i.merge(item);
                break;
            }
            this.items.remove(item);
        }
        if (item.dropsDownHeap && this.type != Type.FOR_SALE) {
            this.items.add(item);
        } else if (this.peek() instanceof LostBackpack) {
            this.items.add(1, item);
        } else {
            this.items.addFirst(item);
        }
        if (this.sprite != null) {
            this.sprite.view(this).place(this.pos);
        }
        if (TippedDart.lostDarts > 0) {
            Dart d = new Dart();
            d.quantity(TippedDart.lostDarts);
            TippedDart.lostDarts = 0;
            this.drop(d);
        }
    }

    public void replace(Item a, Item b) {
        this.hidden = false;
        int index = this.items.indexOf(a);
        if (index != -1) {
            this.items.remove(index);
            for (Item i : this.items) {
                if (!i.isSimilar(b)) continue;
                i.merge(b);
                return;
            }
            this.items.add(index, b);
        }
    }

    public void remove(Item a) {
        this.hidden = false;
        this.items.remove(a);
        if (this.items.isEmpty()) {
            this.destroy();
        } else if (this.sprite != null) {
            this.sprite.view(this).place(this.pos);
        }
    }

    public void burn() {
        this.hidden = false;
        if (this.type != Type.HEAP) {
            return;
        }
        boolean burnt = false;
        boolean evaporated = false;
        for (Item item : this.items.toArray(new Item[0])) {
            if (item instanceof Scroll && !item.unique) {
                this.items.remove(item);
                burnt = true;
                continue;
            }
            if (item instanceof Dewdrop) {
                this.items.remove(item);
                evaporated = true;
                continue;
            }
            if (item instanceof MysteryMeat || item instanceof FrozenCarpaccio) {
                this.replace(item, ChargrilledMeat.cook(item.quantity));
                burnt = true;
                continue;
            }
            if (!(item instanceof Bomb)) continue;
            this.items.remove(item);
            ((Bomb)item).explode(this.pos);
            if (((Bomb)item).explodesDestructively()) {
                return;
            }
            burnt = true;
        }
        if (burnt || evaporated) {
            if (Dungeon.level.heroFOV[this.pos]) {
                if (burnt) {
                    Heap.burnFX(this.pos);
                } else {
                    Heap.evaporateFX(this.pos);
                }
            }
            if (this.isEmpty()) {
                this.destroy();
            } else if (this.sprite != null) {
                this.sprite.view(this).place(this.pos);
            }
        }
    }

    public void explode() {
        this.hidden = false;
        if (this.type == Type.CHEST || this.type == Type.SKELETON) {
            this.type = Type.HEAP;
            this.sprite.link();
            this.sprite.drop();
            return;
        }
        if (this.type != Type.HEAP) {
            return;
        }
        for (Item item : this.items.toArray(new Item[0])) {
            if (item.unique || item.isUpgradable() || item instanceof EquipableItem) continue;
            if (item instanceof Potion) {
                this.items.remove(item);
                ((Potion)item).shatter(this.pos);
                continue;
            }
            if (item instanceof Honeypot.ShatteredPot) {
                this.items.remove(item);
                ((Honeypot.ShatteredPot)item).destroyPot(this.pos);
                continue;
            }
            if (item instanceof Bomb) {
                this.items.remove(item);
                ((Bomb)item).explode(this.pos);
                if (!((Bomb)item).explodesDestructively()) continue;
                return;
            }
            this.items.remove(item);
        }
        if (this.isEmpty()) {
            this.destroy();
        } else if (this.sprite != null) {
            this.sprite.view(this).place(this.pos);
        }
    }

    public void freeze() {
        if (this.type != Type.HEAP) {
            return;
        }
        boolean frozen = false;
        for (Item item : this.items.toArray(new Item[0])) {
            if (item instanceof MysteryMeat) {
                this.replace(item, FrozenCarpaccio.cook((MysteryMeat)item));
                frozen = true;
                continue;
            }
            if (item instanceof Potion && !item.unique) {
                this.items.remove(item);
                ((Potion)item).shatter(this.pos);
                frozen = true;
                continue;
            }
            if (!(item instanceof Bomb) || ((Bomb)item).fuse == null) continue;
            frozen = frozen || ((Bomb)item).fuse.freeze();
        }
        if (frozen) {
            if (this.isEmpty()) {
                this.destroy();
            } else if (this.sprite != null) {
                this.sprite.view(this).place(this.pos);
            }
        }
    }

    public static void burnFX(int pos) {
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
        Sample.INSTANCE.play("sounds/burning.mp3");
    }

    public static void evaporateFX(int pos) {
        CellEmitter.get(pos).burst(Speck.factory(13), 5);
    }

    public boolean isEmpty() {
        return this.items == null || this.items.size() == 0;
    }

    public void destroy() {
        Dungeon.level.heaps.remove(this.pos);
        if (this.sprite != null) {
            this.sprite.kill();
        }
        this.items.clear();
    }

    public String title() {
        switch (this.type.ordinal()) {
            case 1: {
                Item i = this.peek();
                if (this.size() == 1) {
                    return Messages.get(this, "for_sale", Shopkeeper.sellPrice(i), i.title());
                }
                return i.title();
            }
            case 2: {
                return Messages.get(this, "chest", new Object[0]);
            }
            case 3: {
                return Messages.get(this, "locked_chest", new Object[0]);
            }
            case 4: {
                return Messages.get(this, "crystal_chest", new Object[0]);
            }
            case 5: {
                return Messages.get(this, "tomb", new Object[0]);
            }
            case 6: {
                return Messages.get(this, "skeleton", new Object[0]);
            }
            case 7: {
                return Messages.get(this, "remains", new Object[0]);
            }
        }
        return this.peek().title();
    }

    public String info() {
        switch (this.type.ordinal()) {
            case 2: {
                return Messages.get(this, "chest_desc", new Object[0]);
            }
            case 3: {
                return Messages.get(this, "locked_chest_desc", new Object[0]);
            }
            case 4: {
                if (this.peek() instanceof Artifact) {
                    return Messages.get(this, "crystal_chest_desc", Messages.get(this, "artifact", new Object[0]));
                }
                if (this.peek() instanceof Wand) {
                    return Messages.get(this, "crystal_chest_desc", Messages.get(this, "wand", new Object[0]));
                }
                return Messages.get(this, "crystal_chest_desc", Messages.get(this, "ring", new Object[0]));
            }
            case 5: {
                return Messages.get(this, "tomb_desc", new Object[0]);
            }
            case 6: {
                return Messages.get(this, "skeleton_desc", new Object[0]);
            }
            case 7: {
                return Messages.get(this, "remains_desc", new Object[0]);
            }
        }
        return this.peek().info();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.pos = bundle.getInt(POS);
        this.seen = bundle.getBoolean(SEEN);
        this.type = Type.valueOf(bundle.getString(TYPE));
        this.items = new LinkedList<Bundlable>(bundle.getCollection(ITEMS));
        this.items.removeAll(Collections.singleton(null));
        for (Item item : this.items.toArray(new Item[0])) {
            if (item instanceof DocumentPage && (!((DocumentPage)item).document().pageNames().contains(((DocumentPage)item).page()) || ((DocumentPage)item).document().isPageFound(((DocumentPage)item).page()))) {
                this.items.remove(item);
            }
            if (!(item instanceof Guidebook) || !Document.ADVENTURERS_GUIDE.isPageRead(0)) continue;
            this.items.remove(item);
        }
        this.haunted = bundle.getBoolean(HAUNTED);
        this.autoExplored = bundle.getBoolean(AUTO_EXPLORED);
        this.hidden = bundle.getBoolean(HIDDEN);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, this.pos);
        bundle.put(SEEN, this.seen);
        bundle.put(TYPE, this.type);
        bundle.put(ITEMS, this.items);
        bundle.put(HAUNTED, this.haunted);
        bundle.put(AUTO_EXPLORED, this.autoExplored);
        bundle.put(HIDDEN, this.hidden);
    }

    public static enum Type {
        HEAP,
        FOR_SALE,
        CHEST,
        LOCKED_CHEST,
        CRYSTAL_CHEST,
        TOMB,
        SKELETON,
        REMAINS;

    }
}

