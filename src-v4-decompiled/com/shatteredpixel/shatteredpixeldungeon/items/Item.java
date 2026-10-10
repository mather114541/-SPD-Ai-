/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Item
implements Bundlable {
    protected static final String TXT_TO_STRING_LVL = "%s %+d";
    protected static final String TXT_TO_STRING_X = "%s x%d";
    protected static final float TIME_TO_THROW = 1.0f;
    protected static final float TIME_TO_PICK_UP = 1.0f;
    protected static final float TIME_TO_DROP = 1.0f;
    public static final String AC_DROP = "DROP";
    public static final String AC_THROW = "THROW";
    protected String defaultAction;
    public boolean usesTargeting;
    public int image = 0;
    public int icon = -1;
    public boolean stackable = false;
    protected int quantity = 1;
    public boolean dropsDownHeap = false;
    private int level = 0;
    public boolean levelKnown = false;
    public boolean cursed;
    public boolean cursedKnown;
    public boolean unique = false;
    public boolean keptThoughLostInvent = false;
    public boolean bones = false;
    public int customNoteID = -1;
    public static final Comparator<Item> itemComparator = new Comparator<Item>(){

        @Override
        public int compare(Item lhs, Item rhs) {
            return Generator.Category.order(lhs) - Generator.Category.order(rhs);
        }
    };
    private static final String QUANTITY = "quantity";
    private static final String LEVEL = "level";
    private static final String LEVEL_KNOWN = "levelKnown";
    private static final String CURSED = "cursed";
    private static final String CURSED_KNOWN = "cursedKnown";
    private static final String QUICKSLOT = "quickslotpos";
    private static final String KEPT_LOST = "kept_lost";
    private static final String CUSTOM_NOTE_ID = "custom_note_id";
    protected static Hero curUser = null;
    protected static Item curItem = null;
    protected static CellSelector.Listener thrower = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                curItem.cast(curUser, target);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Item.class, "prompt", new Object[0]);
        }
    };

    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = new ArrayList<String>();
        actions.add(AC_DROP);
        actions.add(AC_THROW);
        return actions;
    }

    public String actionName(String action, Hero hero) {
        return Messages.get(this, "ac_" + action, new Object[0]);
    }

    public final boolean doPickUp(Hero hero) {
        return this.doPickUp(hero, hero.pos);
    }

    public boolean doPickUp(Hero hero, int pos) {
        if (this.collect(hero.belongings.backpack)) {
            GameScene.pickUp(this, pos);
            Sample.INSTANCE.play("sounds/item.mp3");
            hero.spendAndNext(this.pickupDelay());
            return true;
        }
        return false;
    }

    public void doDrop(Hero hero) {
        hero.spendAndNext(1.0f);
        int pos = hero.pos;
        Dungeon.level.drop((Item)this.detachAll((Bag)hero.belongings.backpack), (int)pos).sprite.drop(pos);
    }

    public void reset() {
        this.keptThoughLostInvent = false;
    }

    public boolean keptThroughLostInventory() {
        return this.keptThoughLostInvent;
    }

    public void doThrow(Hero hero) {
        GameScene.selectCell(thrower);
    }

    public void execute(Hero hero, String action) {
        GameScene.cancel();
        curUser = hero;
        curItem = this;
        if (action.equals(AC_DROP)) {
            if (hero.belongings.backpack.contains(this) || this.isEquipped(hero)) {
                this.doDrop(hero);
            }
        } else if (action.equals(AC_THROW) && (hero.belongings.backpack.contains(this) || this.isEquipped(hero))) {
            this.doThrow(hero);
        }
    }

    public String defaultAction() {
        return this.defaultAction;
    }

    public void execute(Hero hero) {
        String action = this.defaultAction();
        if (action != null) {
            this.execute(hero, this.defaultAction());
        }
    }

    protected void onThrow(int cell) {
        Heap heap = Dungeon.level.drop(this, cell);
        if (!heap.isEmpty() && heap.sprite != null) {
            heap.sprite.drop(cell);
        }
    }

    /** RL 环境专用：程序化投掷（绕过依赖精灵动画的 cast）。 */
    public void rlOnThrow(int cell) {
        this.onThrow(cell);
    }

    public Item merge(Item other) {
        if (this.isSimilar(other)) {
            this.quantity += other.quantity;
            other.quantity = 0;
        }
        return this;
    }

    public boolean collect(Bag container) {
        if (this.quantity <= 0) {
            return true;
        }
        ArrayList<Item> items = container.items;
        if (items.contains(this)) {
            return true;
        }
        for (Item item : items) {
            if (!(item instanceof Bag) || !((Bag)item).canHold(this) || !this.collect((Bag)item)) continue;
            return true;
        }
        if (!container.canHold(this)) {
            return false;
        }
        if (this.stackable) {
            for (Item item : items) {
                if (!this.isSimilar(item)) continue;
                item.merge(this);
                item.updateQuickslot();
                if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
                    Badges.validateItemLevelAquired(this);
                    Talent.onItemCollected(Dungeon.hero, item);
                    if (this.isIdentified()) {
                        Catalog.setSeen(this.getClass());
                        Statistics.itemTypesDiscovered.add(this.getClass());
                    }
                }
                if (TippedDart.lostDarts > 0) {
                    final Dart d = new Dart();
                    d.quantity(TippedDart.lostDarts);
                    TippedDart.lostDarts = 0;
                    if (!d.collect()) {
                        Actor.add(new Actor(){
                            {
                                this.actPriority = 100;
                            }

                            @Override
                            protected boolean act() {
                                Dungeon.level.drop((Item)d, (int)Dungeon.hero.pos).sprite.drop();
                                Actor.remove(this);
                                return true;
                            }
                        });
                    }
                }
                return true;
            }
        }
        if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
            Badges.validateItemLevelAquired(this);
            Talent.onItemCollected(Dungeon.hero, this);
            if (this.isIdentified()) {
                Catalog.setSeen(this.getClass());
                Statistics.itemTypesDiscovered.add(this.getClass());
            }
        }
        items.add(this);
        Dungeon.quickslot.replacePlaceholder(this);
        Collections.sort(items, itemComparator);
        Item.updateQuickslot();
        return true;
    }

    public final boolean collect() {
        return this.collect(Dungeon.hero.belongings.backpack);
    }

    public Item split(int amount) {
        if (amount <= 0 || amount >= this.quantity()) {
            return null;
        }
        Item split = (Item)Reflection.newInstance(this.getClass());
        if (split == null) {
            return null;
        }
        Bundle copy = new Bundle();
        this.storeInBundle(copy);
        split.restoreFromBundle(copy);
        split.quantity(amount);
        this.quantity -= amount;
        return split;
    }

    public Item duplicate() {
        Item dupe = (Item)Reflection.newInstance(this.getClass());
        if (dupe == null) {
            return null;
        }
        Bundle copy = new Bundle();
        this.storeInBundle(copy);
        dupe.restoreFromBundle(copy);
        return dupe;
    }

    public final Item detach(Bag container) {
        if (this.quantity <= 0) {
            return null;
        }
        if (this.quantity == 1) {
            if (this.stackable) {
                Dungeon.quickslot.convertToPlaceholder(this);
            }
            return this.detachAll(container);
        }
        Item detached = this.split(1);
        Item.updateQuickslot();
        if (detached != null) {
            detached.onDetach();
        }
        return detached;
    }

    public final Item detachAll(Bag container) {
        Dungeon.quickslot.clearItem(this);
        for (Item item : container.items) {
            Bag bag;
            if (item == this) {
                container.items.remove(this);
                item.onDetach();
                container.grabItems();
                Item.updateQuickslot();
                return this;
            }
            if (!(item instanceof Bag) || !(bag = (Bag)item).contains(this)) continue;
            return this.detachAll(bag);
        }
        Item.updateQuickslot();
        return this;
    }

    public boolean isSimilar(Item item) {
        return this.getClass() == item.getClass();
    }

    protected void onDetach() {
    }

    public final int trueLevel() {
        return this.level;
    }

    public int level() {
        return this.level;
    }

    public int buffedLvl() {
        if (Dungeon.hero != null && Dungeon.hero.buff(Degrade.class) != null && (this.isEquipped(Dungeon.hero) || Dungeon.hero.belongings.contains(this))) {
            return Degrade.reduceLevel(this.level());
        }
        return this.level();
    }

    public Item level(int value) {
        this.level = value;
        Item.updateQuickslot();
        return this;
    }

    public Item upgrade() {
        ++this.level;
        Item.updateQuickslot();
        return this;
    }

    public final Item upgrade(int n) {
        for (int i = 0; i < n; ++i) {
            this.upgrade();
        }
        return this;
    }

    public Item degrade() {
        --this.level;
        return this;
    }

    public final Item degrade(int n) {
        for (int i = 0; i < n; ++i) {
            this.degrade();
        }
        return this;
    }

    public int visiblyUpgraded() {
        return this.levelKnown ? this.level() : 0;
    }

    public int buffedVisiblyUpgraded() {
        return this.levelKnown ? this.buffedLvl() : 0;
    }

    public boolean visiblyCursed() {
        return this.cursed && this.cursedKnown;
    }

    public boolean isUpgradable() {
        return true;
    }

    public boolean isIdentified() {
        return this.levelKnown && this.cursedKnown;
    }

    public boolean isEquipped(Hero hero) {
        return false;
    }

    public final Item identify() {
        return this.identify(true);
    }

    public Item identify(boolean byHero) {
        if (byHero && Dungeon.hero != null && Dungeon.hero.isAlive()) {
            Catalog.setSeen(this.getClass());
            Statistics.itemTypesDiscovered.add(this.getClass());
        }
        this.levelKnown = true;
        this.cursedKnown = true;
        Item.updateQuickslot();
        return this;
    }

    public void onHeroGainExp(float levelPercent, Hero hero) {
    }

    public static void evoke(Hero hero) {
        hero.sprite.emitter().burst(Speck.factory(102), 5);
    }

    public String title() {
        String name = this.name();
        if (this.visiblyUpgraded() != 0) {
            name = Messages.format(TXT_TO_STRING_LVL, name, this.visiblyUpgraded());
        }
        if (this.quantity > 1) {
            name = Messages.format(TXT_TO_STRING_X, name, this.quantity);
        }
        return name;
    }

    public String name() {
        return this.trueName();
    }

    public final String trueName() {
        return Messages.get(this, "name", new Object[0]);
    }

    public int image() {
        return this.image;
    }

    public ItemSprite.Glowing glowing() {
        return null;
    }

    public Emitter emitter() {
        return null;
    }

    public String info() {
        if (Dungeon.hero != null) {
            Notes.CustomRecord note = Notes.findCustomRecord(this.customNoteID);
            if (note != null) {
                return Messages.get(this, "custom_note", note.title().replace('_', '\u02cd')) + "\n\n" + this.desc();
            }
            note = Notes.findCustomRecord(this.getClass());
            if (note != null) {
                return Messages.get(this, "custom_note_type", note.title().replace('_', '\u02cd')) + "\n\n" + this.desc();
            }
        }
        return this.desc();
    }

    public String desc() {
        return Messages.get(this, "desc", new Object[0]);
    }

    public int quantity() {
        return this.quantity;
    }

    public Item quantity(int value) {
        this.quantity = value;
        return this;
    }

    public int value() {
        return 0;
    }

    public int energyVal() {
        return 0;
    }

    public Item virtual() {
        Item item = (Item)Reflection.newInstance(this.getClass());
        if (item == null) {
            return null;
        }
        item.quantity = 0;
        item.level = this.level;
        return item;
    }

    public Item random() {
        return this;
    }

    public String status() {
        return this.quantity != 1 ? Integer.toString(this.quantity) : null;
    }

    public static void updateQuickslot() {
        GameScene.updateItemDisplays = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(QUANTITY, this.quantity);
        bundle.put(LEVEL, this.level);
        bundle.put(LEVEL_KNOWN, this.levelKnown);
        bundle.put(CURSED, this.cursed);
        bundle.put(CURSED_KNOWN, this.cursedKnown);
        if (Dungeon.quickslot.contains(this)) {
            bundle.put(QUICKSLOT, Dungeon.quickslot.getSlot(this));
        }
        bundle.put(KEPT_LOST, this.keptThoughLostInvent);
        if (this.customNoteID != -1) {
            bundle.put(CUSTOM_NOTE_ID, this.customNoteID);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.quantity = bundle.getInt(QUANTITY);
        this.levelKnown = bundle.getBoolean(LEVEL_KNOWN);
        this.cursedKnown = bundle.getBoolean(CURSED_KNOWN);
        int level = bundle.getInt(LEVEL);
        if (level > 0) {
            this.upgrade(level);
        } else if (level < 0) {
            this.degrade(-level);
        }
        this.cursed = bundle.getBoolean(CURSED);
        if (Belongings.bundleRestoring && bundle.contains(QUICKSLOT)) {
            Dungeon.quickslot.setSlot(bundle.getInt(QUICKSLOT), this);
        }
        this.keptThoughLostInvent = bundle.getBoolean(KEPT_LOST);
        if (bundle.contains(CUSTOM_NOTE_ID)) {
            this.customNoteID = bundle.getInt(CUSTOM_NOTE_ID);
        }
    }

    public int targetingPos(Hero user, int dst) {
        return this.throwPos(user, dst);
    }

    public int throwPos(Hero user, int dst) {
        return new Ballistica((int)user.pos, (int)dst, (int)7).collisionPos;
    }

    public void throwSound() {
        Sample.INSTANCE.play("sounds/miss.mp3", 0.6f, 0.6f, 1.5f);
    }

    public void cast(final Hero user, int dst) {
        final int cell = this.throwPos(user, dst);
        user.sprite.zap(cell);
        user.busy();
        this.throwSound();
        final Char enemy = Actor.findChar(cell);
        QuickSlotButton.target(enemy);
        final float delay = this.castDelay(user, cell);
        if (enemy != null) {
            ((MissileSprite)user.sprite.parent.recycle(MissileSprite.class)).reset((Visual)user.sprite, (Visual)enemy.sprite, this, new Callback(){

                @Override
                public void call() {
                    curUser = user;
                    Item i = Item.this.detach(user.belongings.backpack);
                    if (i != null) {
                        i.onThrow(cell);
                    }
                    if (curUser.hasTalent(Talent.IMPROVISED_PROJECTILES) && !(Item.this instanceof MissileWeapon) && curUser.buff(Talent.ImprovisedProjectileCooldown.class) == null && enemy != null && enemy.alignment != Item.curUser.alignment) {
                        Sample.INSTANCE.play("sounds/hit.mp3");
                        Buff.affect(enemy, Blindness.class, 1.0f + (float)curUser.pointsInTalent(Talent.IMPROVISED_PROJECTILES));
                        Buff.affect(curUser, Talent.ImprovisedProjectileCooldown.class, 50.0f);
                    }
                    if (user.buff(Talent.LethalMomentumTracker.class) != null) {
                        user.buff(Talent.LethalMomentumTracker.class).detach();
                        user.next();
                    } else {
                        user.spendAndNext(delay);
                    }
                }
            });
        } else {
            ((MissileSprite)user.sprite.parent.recycle(MissileSprite.class)).reset((Visual)user.sprite, cell, this, new Callback(){

                @Override
                public void call() {
                    curUser = user;
                    Item i = Item.this.detach(user.belongings.backpack);
                    user.spend(delay);
                    if (i != null) {
                        i.onThrow(cell);
                    }
                    user.next();
                }
            });
        }
    }

    public float castDelay(Char user, int cell) {
        return 1.0f;
    }

    public float pickupDelay() {
        return 1.0f;
    }

    public void setCurrent(Hero hero) {
        curUser = hero;
        curItem = this;
    }
}

