/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class Belongings
implements Iterable<Item> {
    private Hero owner;
    public Backpack backpack;
    public KindOfWeapon weapon = null;
    public Armor armor = null;
    public Artifact artifact = null;
    public KindofMisc misc = null;
    public Ring ring = null;
    public KindOfWeapon thrownWeapon = null;
    public KindOfWeapon abilityWeapon = null;
    public KindOfWeapon secondWep = null;
    private boolean lostInvent;
    private static final String WEAPON = "weapon";
    private static final String ARMOR = "armor";
    private static final String ARTIFACT = "artifact";
    private static final String MISC = "misc";
    private static final String RING = "ring";
    private static final String SECOND_WEP = "second_wep";
    public static boolean bundleRestoring = false;

    public Belongings(Hero owner) {
        this.owner = owner;
        this.backpack = new Backpack();
        this.backpack.owner = owner;
    }

    public KindOfWeapon attackingWeapon() {
        if (this.thrownWeapon != null) {
            return this.thrownWeapon;
        }
        if (this.abilityWeapon != null) {
            return this.abilityWeapon;
        }
        return this.weapon();
    }

    public void lostInventory(boolean val) {
        this.lostInvent = val;
    }

    public boolean lostInventory() {
        return this.lostInvent;
    }

    public KindOfWeapon weapon() {
        if (!this.lostInventory() || this.weapon != null && this.weapon.keptThroughLostInventory()) {
            return this.weapon;
        }
        return null;
    }

    public Armor armor() {
        if (!this.lostInventory() || this.armor != null && this.armor.keptThroughLostInventory()) {
            return this.armor;
        }
        return null;
    }

    public Artifact artifact() {
        if (!this.lostInventory() || this.artifact != null && this.artifact.keptThroughLostInventory()) {
            return this.artifact;
        }
        return null;
    }

    public KindofMisc misc() {
        if (!this.lostInventory() || this.misc != null && this.misc.keptThroughLostInventory()) {
            return this.misc;
        }
        return null;
    }

    public Ring ring() {
        if (!this.lostInventory() || this.ring != null && this.ring.keptThroughLostInventory()) {
            return this.ring;
        }
        return null;
    }

    public KindOfWeapon secondWep() {
        if (!this.lostInventory() || this.secondWep != null && this.secondWep.keptThroughLostInventory()) {
            return this.secondWep;
        }
        return null;
    }

    public void storeInBundle(Bundle bundle) {
        this.backpack.storeInBundle(bundle);
        bundle.put(WEAPON, this.weapon);
        bundle.put(ARMOR, this.armor);
        bundle.put(ARTIFACT, this.artifact);
        bundle.put(MISC, this.misc);
        bundle.put(RING, this.ring);
        bundle.put(SECOND_WEP, this.secondWep);
    }

    public void restoreFromBundle(Bundle bundle) {
        bundleRestoring = true;
        this.backpack.clear();
        this.backpack.restoreFromBundle(bundle);
        this.weapon = (KindOfWeapon)bundle.get(WEAPON);
        if (this.weapon() != null) {
            this.weapon().activate(this.owner);
        }
        this.armor = (Armor)bundle.get(ARMOR);
        if (this.armor() != null) {
            this.armor().activate(this.owner);
        }
        this.artifact = (Artifact)bundle.get(ARTIFACT);
        if (this.artifact() != null) {
            this.artifact().activate(this.owner);
        }
        this.misc = (KindofMisc)bundle.get(MISC);
        if (this.misc() != null) {
            this.misc().activate(this.owner);
        }
        this.ring = (Ring)bundle.get(RING);
        if (this.ring() != null) {
            this.ring().activate(this.owner);
        }
        this.secondWep = (KindOfWeapon)bundle.get(SECOND_WEP);
        if (this.secondWep() != null) {
            this.secondWep().activate(this.owner);
        }
        bundleRestoring = false;
    }

    public void clear() {
        this.backpack.clear();
        this.secondWep = null;
        this.weapon = null;
        this.armor = null;
        this.artifact = null;
        this.misc = null;
        this.ring = null;
    }

    public static void preview(GamesInProgress.Info info, Bundle bundle) {
        Armor armor;
        info.armorTier = bundle.contains(ARMOR) ? ((armor = (Armor)bundle.get(ARMOR)) instanceof ClassArmor ? 6 : armor.tier) : 0;
    }

    public ArrayList<Bag> getBags() {
        ArrayList<Bag> result = new ArrayList<Bag>();
        result.add(this.backpack);
        for (Item i : this) {
            if (!(i instanceof Bag)) continue;
            result.add((Bag)i);
        }
        return result;
    }

    public <T extends Item> T getItem(Class<T> itemClass) {
        boolean lostInvent = this.lostInventory();
        for (Item item : this) {
            if (!itemClass.isInstance(item) || lostInvent && !item.keptThroughLostInventory()) continue;
            return (T)item;
        }
        return null;
    }

    public <T extends Item> ArrayList<T> getAllItems(Class<T> itemClass) {
        ArrayList<Item> result = new ArrayList<Item>();
        boolean lostInvent = this.lostInventory();
        for (Item item : this) {
            if (!itemClass.isInstance(item) || lostInvent && !item.keptThroughLostInventory()) continue;
            result.add(item);
        }
        return result;
    }

    public boolean contains(Item contains) {
        boolean lostInvent = this.lostInventory();
        for (Item item : this) {
            if (contains != item || lostInvent && !item.keptThroughLostInventory()) continue;
            return true;
        }
        return false;
    }

    public Item getSimilar(Item similar) {
        boolean lostInvent = this.lostInventory();
        for (Item item : this) {
            if (similar == item || !similar.isSimilar(item) || lostInvent && !item.keptThroughLostInventory()) continue;
            return item;
        }
        return null;
    }

    public ArrayList<Item> getAllSimilar(Item similar) {
        ArrayList<Item> result = new ArrayList<Item>();
        boolean lostInvent = this.lostInventory();
        for (Item item : this) {
            if (item == similar || !similar.isSimilar(item) || lostInvent && !item.keptThroughLostInventory()) continue;
            result.add(item);
        }
        return result;
    }

    public void identify() {
        for (Item item : this) {
            item.identify(false);
        }
    }

    public void observe() {
        if (this.weapon() != null) {
            if (ShardOfOblivion.passiveIDDisabled() && this.weapon() instanceof Weapon) {
                ((Weapon)this.weapon()).setIDReady();
            } else {
                this.weapon().identify();
                Badges.validateItemLevelAquired(this.weapon());
            }
        }
        if (this.secondWep() != null) {
            if (ShardOfOblivion.passiveIDDisabled() && this.secondWep() instanceof Weapon) {
                ((Weapon)this.secondWep()).setIDReady();
            } else {
                this.secondWep().identify();
                Badges.validateItemLevelAquired(this.secondWep());
            }
        }
        if (this.armor() != null) {
            if (ShardOfOblivion.passiveIDDisabled()) {
                this.armor().setIDReady();
            } else {
                this.armor().identify();
                Badges.validateItemLevelAquired(this.armor());
            }
        }
        if (this.artifact() != null) {
            this.artifact().identify();
            Badges.validateItemLevelAquired(this.artifact());
        }
        if (this.misc() != null) {
            if (ShardOfOblivion.passiveIDDisabled() && this.misc() instanceof Ring) {
                ((Ring)this.misc()).setIDReady();
            } else {
                this.misc().identify();
                Badges.validateItemLevelAquired(this.misc());
            }
        }
        if (this.ring() != null) {
            if (ShardOfOblivion.passiveIDDisabled()) {
                this.ring().setIDReady();
            } else {
                this.ring().identify();
                Badges.validateItemLevelAquired(this.ring());
            }
        }
        if (ShardOfOblivion.passiveIDDisabled()) {
            GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready_worn", new Object[0]), new Object[0]);
        }
        for (Item item : this.backpack) {
            if (!(item instanceof EquipableItem) && !(item instanceof Wand)) continue;
            item.cursedKnown = true;
        }
        Item.updateQuickslot();
    }

    public void uncurseEquipped() {
        ScrollOfRemoveCurse.uncurse(this.owner, this.armor(), this.weapon(), this.artifact(), this.misc(), this.ring(), this.secondWep());
    }

    public Item randomUnequipped() {
        if (this.owner.buff(LostInventory.class) != null) {
            return null;
        }
        return (Item)Random.element(this.backpack.items);
    }

    public int charge(float charge) {
        int count = 0;
        for (Wand.Charger charger : this.owner.buffs(Wand.Charger.class)) {
            charger.gainCharge(charge);
            ++count;
        }
        return count;
    }

    @Override
    public Iterator<Item> iterator() {
        return new ItemIterator();
    }

    public static class Backpack
    extends Bag {
        public Backpack() {
            this.image = ItemSpriteSheet.BACKPACK;
        }

        @Override
        public int capacity() {
            int cap = super.capacity();
            for (Item item : this.items) {
                if (!(item instanceof Bag)) continue;
                ++cap;
            }
            if (Dungeon.hero != null && Dungeon.hero.belongings.secondWep != null) {
                --cap;
            }
            return cap;
        }
    }

    private class ItemIterator
    implements Iterator<Item> {
        private int index = 0;
        private Iterator<Item> backpackIterator;
        private Item[] equipped;
        private int backpackIndex;

        private ItemIterator() {
            this.backpackIterator = Belongings.this.backpack.iterator();
            this.equipped = new Item[]{Belongings.this.weapon, Belongings.this.armor, Belongings.this.artifact, Belongings.this.misc, Belongings.this.ring, Belongings.this.secondWep};
            this.backpackIndex = this.equipped.length;
        }

        @Override
        public boolean hasNext() {
            for (int i = this.index; i < this.backpackIndex; ++i) {
                if (this.equipped[i] == null) continue;
                return true;
            }
            return this.backpackIterator.hasNext();
        }

        @Override
        public Item next() {
            while (this.index < this.backpackIndex) {
                Item item;
                if ((item = this.equipped[this.index++]) == null) continue;
                return item;
            }
            return this.backpackIterator.next();
        }

        @Override
        public void remove() {
            switch (this.index) {
                case 0: {
                    Belongings.this.weapon = null;
                    this.equipped[0] = null;
                    break;
                }
                case 1: {
                    Belongings.this.armor = null;
                    this.equipped[1] = null;
                    break;
                }
                case 2: {
                    Belongings.this.artifact = null;
                    this.equipped[2] = null;
                    break;
                }
                case 3: {
                    Belongings.this.misc = null;
                    this.equipped[3] = null;
                    break;
                }
                case 4: {
                    Belongings.this.ring = null;
                    this.equipped[4] = null;
                    break;
                }
                case 5: {
                    Belongings.this.secondWep = null;
                    this.equipped[5] = null;
                    break;
                }
                default: {
                    this.backpackIterator.remove();
                }
            }
        }
    }
}

