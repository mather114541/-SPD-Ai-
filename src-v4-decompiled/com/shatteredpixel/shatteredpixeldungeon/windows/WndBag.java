/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.RightClickMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;

public class WndBag
extends WndTabbed {
    public static Window INSTANCE;
    protected static final int COLS_P = 5;
    protected static final int COLS_L = 5;
    protected static int SLOT_WIDTH_P;
    protected static int SLOT_WIDTH_L;
    protected static int SLOT_HEIGHT_P;
    protected static int SLOT_HEIGHT_L;
    protected static final int SLOT_MARGIN = 1;
    protected static final int TITLE_HEIGHT = 14;
    private ItemSelector selector;
    private int nCols;
    private int nRows;
    private int slotWidth;
    private int slotHeight;
    protected int count;
    protected int col;
    protected int row;
    private static Bag lastBag;

    public WndBag(Bag bag) {
        this(bag, null);
    }

    public WndBag(Bag bag, ItemSelector selector) {
        if (INSTANCE != null) {
            INSTANCE.hide();
        }
        INSTANCE = this;
        this.selector = selector;
        lastBag = bag;
        this.slotWidth = PixelScene.landscape() ? SLOT_WIDTH_L : SLOT_WIDTH_P;
        this.slotHeight = PixelScene.landscape() ? SLOT_HEIGHT_L : SLOT_HEIGHT_P;
        this.nCols = PixelScene.landscape() ? 5 : 5;
        this.nRows = (int)Math.ceil(25.0f / (float)this.nCols);
        int windowWidth = this.slotWidth * this.nCols + 1 * (this.nCols - 1);
        int windowHeight = 14 + this.slotHeight * this.nRows + 1 * (this.nRows - 1);
        if (PixelScene.landscape()) {
            while (this.slotHeight >= 24 && windowHeight + 20 + this.chrome.marginTop() > PixelScene.uiCamera.height) {
                --this.slotHeight;
                windowHeight -= this.nRows;
            }
        } else {
            while (this.slotWidth >= 26 && windowWidth + this.chrome.marginHor() > PixelScene.uiCamera.width) {
                --this.slotWidth;
                windowWidth -= this.nCols;
            }
        }
        this.placeTitle(bag, windowWidth);
        this.placeItems(bag);
        this.resize(windowWidth, windowHeight);
        int i = 1;
        for (Bag b : Dungeon.hero.belongings.getBags()) {
            if (b == null) continue;
            BagTab tab = new BagTab(b, i++);
            this.add(tab);
            tab.select(b == bag);
            if (b != bag) continue;
            this.selected = tab;
        }
        this.layoutTabs();
    }

    public ItemSelector getSelector() {
        return this.selector;
    }

    public static WndBag lastBag(ItemSelector selector) {
        if (lastBag != null && Dungeon.hero.belongings.backpack.contains(lastBag)) {
            return new WndBag(lastBag, selector);
        }
        return new WndBag(Dungeon.hero.belongings.backpack, selector);
    }

    public static WndBag getBag(ItemSelector selector) {
        if (selector.preferredBag() == Belongings.Backpack.class) {
            return new WndBag(Dungeon.hero.belongings.backpack, selector);
        }
        if (selector.preferredBag() != null) {
            Bag bag = Dungeon.hero.belongings.getItem(selector.preferredBag());
            if (bag != null) {
                return new WndBag(bag, selector);
            }
            return new WndBag(Dungeon.hero.belongings.backpack, selector);
        }
        return WndBag.lastBag(selector);
    }

    protected void placeTitle(Bag bag, int width) {
        float titleWidth;
        BitmapText amt;
        Image gold;
        if (Dungeon.energy == 0) {
            gold = new ItemSprite(ItemSpriteSheet.GOLD, null);
            ((ItemSprite)gold).x = (float)width - gold.width();
            ((ItemSprite)gold).y = (14.0f - gold.height()) / 2.0f;
            PixelScene.align(gold);
            this.add(gold);
            amt = new BitmapText(Integer.toString(Dungeon.gold), PixelScene.pixelFont);
            amt.hardlight(0xFFFF44);
            amt.measure();
            amt.x = (float)width - gold.width() - amt.width() - 1.0f;
            amt.y = (14.0f - amt.baseLine()) / 2.0f - 1.0f;
            PixelScene.align(amt);
            this.add(amt);
            titleWidth = amt.x;
        } else {
            gold = Icons.get(Icons.COIN_SML);
            gold.x = (float)width - gold.width() - 0.5f;
            gold.y = 0.0f;
            PixelScene.align(gold);
            this.add(gold);
            amt = new BitmapText(Integer.toString(Dungeon.gold), PixelScene.pixelFont);
            amt.hardlight(0xFFFF44);
            amt.measure();
            amt.x = (float)width - gold.width() - amt.width() - 2.0f;
            amt.y = 0.0f;
            PixelScene.align(amt);
            this.add(amt);
            titleWidth = amt.x;
            Image energy = Icons.get(Icons.ENERGY_SML);
            energy.x = (float)width - energy.width();
            energy.y = gold.height();
            PixelScene.align(energy);
            this.add(energy);
            amt = new BitmapText(Integer.toString(Dungeon.energy), PixelScene.pixelFont);
            amt.hardlight(0x44CCFF);
            amt.measure();
            amt.x = (float)width - energy.width() - amt.width() - 1.0f;
            amt.y = energy.y;
            PixelScene.align(amt);
            this.add(amt);
            titleWidth = Math.min(titleWidth, amt.x);
        }
        String title = this.selector != null ? this.selector.textPrompt() : null;
        RenderedTextBlock txtTitle = PixelScene.renderTextBlock(title != null ? Messages.titleCase(title) : Messages.titleCase(bag.name()), 8);
        txtTitle.hardlight(0xFFFF44);
        txtTitle.maxWidth((int)titleWidth - 2);
        txtTitle.setPos(1.0f, (14.0f - txtTitle.height()) / 2.0f - 1.0f);
        PixelScene.align(txtTitle);
        this.add(txtTitle);
    }

    protected void placeItems(Bag container) {
        Belongings stuff = Dungeon.hero.belongings;
        this.placeItem(stuff.weapon != null ? stuff.weapon : new Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
        this.placeItem(stuff.armor != null ? stuff.armor : new Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
        this.placeItem(stuff.artifact != null ? stuff.artifact : new Placeholder(ItemSpriteSheet.ARTIFACT_HOLDER));
        this.placeItem(stuff.misc != null ? stuff.misc : new Placeholder(ItemSpriteSheet.SOMETHING));
        this.placeItem(stuff.ring != null ? stuff.ring : new Placeholder(ItemSpriteSheet.RING_HOLDER));
        int equipped = 5;
        if (container != Dungeon.hero.belongings.backpack) {
            this.placeItem(container);
            --this.count;
        } else if (stuff.secondWep != null) {
            this.placeItem(stuff.secondWep);
            ++equipped;
        }
        for (Item item : container.items.toArray(new Item[0])) {
            if (!(item instanceof Bag)) {
                this.placeItem(item);
                continue;
            }
            ++this.count;
        }
        while (this.count - equipped < container.capacity()) {
            this.placeItem(null);
        }
    }

    protected void placeItem(Item item) {
        ++this.count;
        int x = this.col * (this.slotWidth + 1);
        int y = 14 + this.row * (this.slotHeight + 1);
        InventorySlot slot = new InventorySlot(item){

            @Override
            protected void onClick() {
                if (lastBag != this.item && !lastBag.contains(this.item) && !this.item.isEquipped(Dungeon.hero)) {
                    WndBag.this.hide();
                } else if (WndBag.this.selector != null) {
                    if (WndBag.this.selector.hideAfterSelecting()) {
                        WndBag.this.hide();
                    }
                    WndBag.this.selector.onSelect(this.item);
                } else {
                    Game.scene().addToFront(new WndUseItem(WndBag.this, this.item));
                }
            }

            @Override
            protected void onRightClick() {
                if (lastBag != this.item && !lastBag.contains(this.item) && !this.item.isEquipped(Dungeon.hero)) {
                    WndBag.this.hide();
                } else if (WndBag.this.selector != null) {
                    if (WndBag.this.selector.hideAfterSelecting()) {
                        WndBag.this.hide();
                    }
                    WndBag.this.selector.onSelect(this.item);
                } else {
                    RightClickMenu r = new RightClickMenu(this.item){

                        @Override
                        public void onSelect(int index) {
                            WndBag.this.hide();
                        }
                    };
                    this.parent.addToFront(r);
                    r.camera = this.camera();
                    PointF mousePos = PointerEvent.currentHoverPos();
                    mousePos = this.camera.screenToCamera((int)mousePos.x, (int)mousePos.y);
                    r.setPos(mousePos.x - 3.0f, mousePos.y - 3.0f);
                }
            }

            @Override
            protected boolean onLongClick() {
                if (WndBag.this.selector == null && this.item.defaultAction() != null) {
                    WndBag.this.hide();
                    QuickSlotButton.set(this.item);
                    return true;
                }
                if (WndBag.this.selector != null) {
                    Game.scene().addToFront(new WndInfoItem(this.item));
                    return true;
                }
                return false;
            }
        };
        slot.setRect(x, y, this.slotWidth, this.slotHeight);
        this.add(slot);
        if (item == null || this.selector != null && !this.selector.itemSelectable(item)) {
            slot.enable(false);
        }
        if (++this.col >= this.nCols) {
            this.col = 0;
            ++this.row;
        }
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed && KeyBindings.getActionForKey(event) == SPDAction.INVENTORY) {
            this.onBackPressed();
            return true;
        }
        return super.onSignal(event);
    }

    @Override
    public void onBackPressed() {
        if (this.selector != null) {
            this.selector.onSelect(null);
        }
        super.onBackPressed();
    }

    @Override
    protected void onClick(WndTabbed.Tab tab) {
        this.hide();
        WndBag w = new WndBag(((BagTab)tab).bag, this.selector);
        if (Game.scene() instanceof GameScene) {
            GameScene.show(w);
        } else {
            Game.scene().addToFront(w);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (INSTANCE == this) {
            INSTANCE = null;
        }
    }

    @Override
    protected int tabHeight() {
        return 20;
    }

    private Image icon(Bag bag) {
        if (bag instanceof VelvetPouch) {
            return Icons.get(Icons.SEED_POUCH);
        }
        if (bag instanceof ScrollHolder) {
            return Icons.get(Icons.SCROLL_HOLDER);
        }
        if (bag instanceof MagicalHolster) {
            return Icons.get(Icons.WAND_HOLSTER);
        }
        if (bag instanceof PotionBandolier) {
            return Icons.get(Icons.POTION_BANDOLIER);
        }
        return Icons.get(Icons.BACKPACK);
    }

    static {
        SLOT_WIDTH_P = 28;
        SLOT_WIDTH_L = 28;
        SLOT_HEIGHT_P = 28;
        SLOT_HEIGHT_L = 28;
    }

    public static abstract class ItemSelector {
        public abstract String textPrompt();

        public Class<? extends Bag> preferredBag() {
            return null;
        }

        public boolean hideAfterSelecting() {
            return true;
        }

        public abstract boolean itemSelectable(Item var1);

        public abstract void onSelect(Item var1);
    }

    private class BagTab
    extends WndTabbed.IconTab {
        private Bag bag;
        private int index;

        public BagTab(Bag bag, int index) {
            super(WndBag.this, WndBag.this.icon(bag));
            this.bag = bag;
            this.index = index;
        }

        @Override
        public GameAction keyAction() {
            switch (this.index) {
                default: {
                    return SPDAction.BAG_1;
                }
                case 2: {
                    return SPDAction.BAG_2;
                }
                case 3: {
                    return SPDAction.BAG_3;
                }
                case 4: {
                    return SPDAction.BAG_4;
                }
                case 5: 
            }
            return SPDAction.BAG_5;
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(this.bag.name());
        }
    }

    public static class Placeholder
    extends Item {
        public Placeholder(int image) {
            this.image = image;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public boolean isEquipped(Hero hero) {
            return true;
        }
    }
}

