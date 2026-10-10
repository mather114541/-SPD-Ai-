/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShopkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CurrencyIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;

public class Shopkeeper
extends NPC {
    public static int MAX_BUYBACK_HISTORY = 3;
    public ArrayList<Item> buybackItems;
    private int turnsSinceHarmed;
    private static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return Messages.get(Shopkeeper.class, "sell", new Object[0]);
        }

        @Override
        public boolean itemSelectable(Item item) {
            return Shopkeeper.canSell(item);
        }

        @Override
        public void onSelect(Item item) {
            if (item != null && Dungeon.hero != null && Dungeon.hero.isAlive()) {
                WndBag parentWnd = Shopkeeper.sell();
                GameScene.show(new WndTradeItem(item, parentWnd));
            }
        }
    };
    public static String BUYBACK_ITEMS = "buyback_items";
    public static String TURNS_SINCE_HARMED = "turns_since_harmed";

    public Shopkeeper() {
        this.spriteClass = ShopkeeperSprite.class;
        this.properties.add(Char.Property.IMMOVABLE);
        this.buybackItems = new ArrayList();
        this.turnsSinceHarmed = -1;
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.SHOP;
    }

    @Override
    protected boolean act() {
        if (this.turnsSinceHarmed >= 0) {
            ++this.turnsSinceHarmed;
        }
        this.sprite.turnTo(this.pos, Dungeon.hero.pos);
        this.spend(1.0f);
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        this.processHarm();
    }

    @Override
    public boolean add(Buff buff) {
        if (buff.type == Buff.buffType.NEGATIVE) {
            this.processHarm();
        }
        return false;
    }

    public void processHarm() {
        if (!Dungeon.level.heroFOV[this.pos]) {
            return;
        }
        if (this.turnsSinceHarmed == -1) {
            this.turnsSinceHarmed = 0;
            this.yell(Messages.get(this, "warn", new Object[0]));
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    ArrayList<Blob> blobs = new ArrayList<Blob>();
                    for (Class c : new BlobImmunity().immunities()) {
                        Blob b = Dungeon.level.blobs.get(c);
                        if (b == null || b.volume <= 0) continue;
                        blobs.add(b);
                    }
                    PathFinder.buildDistanceMap(Shopkeeper.this.pos, BArray.not(Dungeon.level.solid, null), 4);
                    for (int i = 0; i < Dungeon.level.length(); ++i) {
                        if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
                        boolean affected = false;
                        for (Blob blob : blobs) {
                            if (blob.cur[i] <= 0) continue;
                            blob.clear(i);
                            affected = true;
                        }
                        if (!affected || !Dungeon.level.heroFOV[i]) continue;
                        CellEmitter.get(i).burst(Speck.factory(101), 2);
                    }
                    Actor.remove(this);
                    return true;
                }
            });
        } else if (this.turnsSinceHarmed >= 1) {
            this.flee();
        }
    }

    public void flee() {
        this.destroy();
        Notes.remove(this.landmark());
        GLog.newLine();
        GLog.n(Messages.get(this, "flee", new Object[0]), new Object[0]);
        if (this.sprite != null) {
            this.sprite.killAndErase();
            CellEmitter.get(this.pos).burst(ElmoParticle.FACTORY, 6);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.type != Heap.Type.FOR_SALE) continue;
            if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
            }
            if (heap.size() == 1) {
                heap.destroy();
                continue;
            }
            heap.items.remove(heap.size() - 1);
            heap.type = Heap.Type.HEAP;
        }
    }

    @Override
    public boolean reset() {
        return true;
    }

    public static int sellPrice(Item item) {
        return item.value() * 5 * (Dungeon.depth / 5 + 1);
    }

    public static WndBag sell() {
        return GameScene.selectItem(itemSelector);
    }

    public static boolean canSell(Item item) {
        if (item.value() <= 0) {
            return false;
        }
        if (item.unique && !item.stackable) {
            return false;
        }
        if (item instanceof Armor && ((Armor)item).checkSeal() != null) {
            return false;
        }
        return !item.isEquipped(Dungeon.hero) || !item.cursed;
    }

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero) {
            return true;
        }
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                String[] options = new String[2 + Shopkeeper.this.buybackItems.size()];
                int maxLen = PixelScene.landscape() ? 30 : 25;
                int i = 0;
                options[i++] = Messages.get(Shopkeeper.this, "sell", new Object[0]);
                options[i++] = Messages.get(Shopkeeper.this, "talk", new Object[0]);
                for (Item item : Shopkeeper.this.buybackItems) {
                    options[i] = Messages.get(Heap.class, "for_sale", item.value(), Messages.titleCase(item.title()));
                    if (options[i].length() > maxLen) {
                        options[i] = options[i].substring(0, maxLen - 3) + "...";
                    }
                    ++i;
                }
                CurrencyIndicator.showGold = true;
                GameScene.show(new WndOptions(Shopkeeper.this.sprite(), Messages.titleCase(Shopkeeper.this.name()), Shopkeeper.this.description(), options){

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            Shopkeeper.sell();
                        } else if (index == 1) {
                            GameScene.show(new WndTitledMessage(Shopkeeper.this.sprite(), Messages.titleCase(Shopkeeper.this.name()), Shopkeeper.this.chatText()));
                        } else if (index > 1) {
                            GLog.i(Messages.get(Shopkeeper.this, "buyback", new Object[0]), new Object[0]);
                            Item returned = Shopkeeper.this.buybackItems.remove(index - 2);
                            Dungeon.gold -= returned.value();
                            Statistics.goldCollected -= returned.value();
                            if (returned instanceof MissileWeapon && returned.isUpgradable()) {
                                Buff.affect((Char)Dungeon.hero, MissileWeapon.UpgradedSetTracker.class).levelThresholds.put(((MissileWeapon)returned).setID, returned.level());
                            }
                            if (!returned.doPickUp(Dungeon.hero)) {
                                Dungeon.level.drop(returned, Dungeon.hero.pos);
                            }
                        }
                    }

                    @Override
                    protected boolean enabled(int index) {
                        if (index > 1) {
                            return Dungeon.gold >= Shopkeeper.this.buybackItems.get(index - 2).value();
                        }
                        return super.enabled(index);
                    }

                    @Override
                    protected boolean hasIcon(int index) {
                        return index > 1;
                    }

                    @Override
                    protected Image getIcon(int index) {
                        if (index > 1) {
                            return new ItemSprite(Shopkeeper.this.buybackItems.get(index - 2));
                        }
                        return null;
                    }

                    @Override
                    public void hide() {
                        super.hide();
                        CurrencyIndicator.showGold = false;
                    }
                });
            }
        });
        return true;
    }

    public String chatText() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            return Messages.get(this, "talk_ascent", new Object[0]);
        }
        switch (Dungeon.depth) {
            default: {
                return Messages.get(this, "talk_prison_intro", new Object[0]) + "\n\n" + Messages.get(this, "talk_prison_" + Dungeon.hero.heroClass.name(), new Object[0]);
            }
            case 11: {
                return Messages.get(this, "talk_caves", new Object[0]);
            }
            case 16: {
                return Messages.get(this, "talk_city", new Object[0]);
            }
            case 20: 
        }
        return Messages.get(this, "talk_halls", new Object[0]);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BUYBACK_ITEMS, this.buybackItems);
        bundle.put(TURNS_SINCE_HARMED, this.turnsSinceHarmed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.buybackItems.clear();
        if (bundle.contains(BUYBACK_ITEMS)) {
            for (Bundlable i : bundle.getCollection(BUYBACK_ITEMS)) {
                this.buybackItems.add((Item)i);
            }
        }
        this.turnsSinceHarmed = bundle.contains(TURNS_SINCE_HARMED) ? bundle.getInt(TURNS_SINCE_HARMED) : -1;
    }
}

