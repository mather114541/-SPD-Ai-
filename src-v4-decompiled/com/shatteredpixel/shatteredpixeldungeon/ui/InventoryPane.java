/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.RightClickMenu;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.gltextures.TextureCache;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;
import java.util.ArrayList;

public class InventoryPane
extends Component {
    private NinePatch bg;
    private PointerArea blocker;
    private Signal.Listener<KeyEvent> keyBlocker;
    private static InventoryPane instance;
    private ArrayList<InventorySlot> equipped;
    private ArrayList<InventorySlot> bagItems;
    private Image gold;
    private BitmapText goldTxt;
    private Image energy;
    private BitmapText energyTxt;
    private RenderedTextBlock promptTxt;
    private ArrayList<BagButton> bags;
    public static final int WIDTH = 187;
    public static final int HEIGHT = 82;
    private static final int SLOT_WIDTH = 17;
    private static final int SLOT_HEIGHT = 24;
    private WndBag.ItemSelector selector;
    public static Bag lastBag;
    private boolean lastEnabled = true;
    private static Image crossB;
    private static Image crossM;
    private static boolean targeting;
    private static InventorySlot targetingSlot;
    public static Char lastTarget;

    public InventoryPane() {
        instance = this;
    }

    @Override
    public synchronized void destroy() {
        KeyEvent.removeKeyListener(this.keyBlocker);
        super.destroy();
        if (instance == this) {
            instance = null;
        }
    }

    @Override
    protected void createChildren() {
        Button btn;
        int i;
        this.bg = Chrome.get(Chrome.Type.TOAST_TR_HEAVY);
        this.add(this.bg);
        this.blocker = new PointerArea(0.0f, 0.0f, PixelScene.uiCamera.width, PixelScene.uiCamera.height){

            @Override
            protected void onClick(PointerEvent event) {
                if (InventoryPane.this.selector != null && !InventoryPane.this.bg.overlapsScreenPoint((int)event.current.x, (int)event.current.y)) {
                    GameScene.centerNextWndOnInvPane();
                    InventoryPane.this.selector.onSelect(null);
                    InventoryPane.this.selector = null;
                    InventoryPane.this.updateInventory();
                }
            }
        };
        this.blocker.target = this.bg;
        this.add(this.blocker);
        this.keyBlocker = new Signal.Listener<KeyEvent>(){

            @Override
            public boolean onSignal(KeyEvent keyEvent) {
                if (keyEvent.pressed && InventoryPane.this.isSelecting() && InventoryPane.this.visible && KeyBindings.getActionForKey(keyEvent) != SPDAction.BAG_1 && KeyBindings.getActionForKey(keyEvent) != SPDAction.BAG_2 && KeyBindings.getActionForKey(keyEvent) != SPDAction.BAG_3 && KeyBindings.getActionForKey(keyEvent) != SPDAction.BAG_4 && KeyBindings.getActionForKey(keyEvent) != SPDAction.BAG_5) {
                    GameScene.centerNextWndOnInvPane();
                    InventoryPane.this.selector.onSelect(null);
                    InventoryPane.this.selector = null;
                    InventoryPane.this.updateInventory();
                    return true;
                }
                return false;
            }
        };
        this.equipped = new ArrayList();
        for (i = 0; i < 5; ++i) {
            btn = new InventoryPaneSlot(null);
            this.equipped.add((InventorySlot)btn);
            this.add(btn);
        }
        this.gold = Icons.get(Icons.COIN_SML);
        this.add(this.gold);
        this.goldTxt = new BitmapText(PixelScene.pixelFont);
        this.goldTxt.hardlight(0xFFFF44);
        this.add(this.goldTxt);
        this.energy = Icons.get(Icons.ENERGY_SML);
        this.add(this.energy);
        this.energyTxt = new BitmapText(PixelScene.pixelFont);
        this.energyTxt.hardlight(0x44CCFF);
        this.add(this.energyTxt);
        this.promptTxt = PixelScene.renderTextBlock(6);
        this.promptTxt.hardlight(0xFFFF44);
        this.add(this.promptTxt);
        this.bagItems = new ArrayList();
        for (i = 0; i < 20; ++i) {
            btn = new InventoryPaneSlot(null);
            this.bagItems.add((InventorySlot)btn);
            this.add(btn);
        }
        this.bags = new ArrayList();
        for (i = 0; i < 5; ++i) {
            btn = new BagButton(null, i + 1);
            this.bags.add((BagButton)btn);
            this.add(btn);
        }
        crossB = Icons.TARGET.get();
        InventoryPane.crossB.visible = false;
        this.add(crossB);
        crossM = new Image();
        crossM.copy(crossB);
        this.lastEnabled = true;
        this.updateInventory();
        this.width = 187.0f;
        this.height = 82.0f;
    }

    @Override
    protected void layout() {
        this.width = 187.0f;
        this.height = 82.0f;
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.bg.size(this.width, this.height);
        float left = this.x + 4.0f;
        for (InventorySlot i : this.equipped) {
            i.setRect(left, this.y + 4.0f, 17.0f, 24.0f);
            left = i.right() + 1.0f;
        }
        this.promptTxt.maxWidth((int)(this.width - (left - this.x) - (float)this.bg.marginRight()));
        if (this.promptTxt.height() > 10.0f) {
            this.promptTxt.setPos(left, this.y + 2.0f + (12.0f - this.promptTxt.height()) / 2.0f);
        } else {
            this.promptTxt.setPos(left, this.y + 4.0f + (10.0f - this.promptTxt.height()) / 2.0f);
        }
        this.goldTxt.x = left;
        this.goldTxt.y = this.y + 5.5f;
        PixelScene.align(this.goldTxt);
        this.gold.x = this.goldTxt.x + this.goldTxt.width() + 1.0f;
        this.gold.y = this.goldTxt.y;
        this.energyTxt.x = this.gold.x + this.gold.width() + 2.0f;
        this.energyTxt.y = this.y + 5.5f;
        PixelScene.align(this.energyTxt);
        this.energy.x = this.energyTxt.x + this.energyTxt.width() + 1.0f;
        this.energy.y = this.energyTxt.y;
        for (BagButton b : this.bags) {
            b.setRect(left, this.y + 14.0f, 17.0f, 14.0f);
            left = b.right() + 1.0f;
        }
        left = this.x + 4.0f;
        float top = this.y + 4.0f + 24.0f + 1.0f;
        for (InventorySlot b : this.bagItems) {
            b.setRect(left, top, 17.0f, 24.0f);
            left = b.right() + 1.0f;
            if (!(left - this.x > this.width - 17.0f)) continue;
            left = this.x + 4.0f;
            top += 25.0f;
        }
        super.layout();
    }

    public void alpha(float value) {
        this.bg.alpha(value);
        for (InventorySlot slot : this.equipped) {
            slot.alpha(value);
        }
        for (InventorySlot slot : this.bagItems) {
            slot.alpha(value);
        }
        this.gold.alpha(value);
        this.goldTxt.alpha(value);
        this.energy.alpha(value);
        this.energyTxt.alpha(value);
        for (BagButton bag : this.bags) {
            bag.alpha(value);
        }
    }

    public static void refresh() {
        if (instance != null) {
            instance.updateInventory();
        }
    }

    public void updateInventory() {
        if (this.selector == null) {
            this.blocker.target = this.bg;
            KeyEvent.removeKeyListener(this.keyBlocker);
        } else {
            this.blocker.target = this.blocker;
            KeyEvent.addKeyListener(this.keyBlocker);
        }
        Belongings stuff = Dungeon.hero.belongings;
        if (lastBag == null || !stuff.getBags().contains(lastBag)) {
            lastBag = stuff.backpack;
        }
        this.equipped.get(0).item(stuff.weapon == null ? new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER) : stuff.weapon);
        this.equipped.get(1).item(stuff.armor == null ? new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER) : stuff.armor);
        this.equipped.get(2).item(stuff.artifact == null ? new WndBag.Placeholder(ItemSpriteSheet.ARTIFACT_HOLDER) : stuff.artifact);
        this.equipped.get(3).item(stuff.misc == null ? new WndBag.Placeholder(ItemSpriteSheet.SOMETHING) : stuff.misc);
        this.equipped.get(4).item(stuff.ring == null ? new WndBag.Placeholder(ItemSpriteSheet.RING_HOLDER) : stuff.ring);
        ArrayList items = (ArrayList)InventoryPane.lastBag.items.clone();
        if (lastBag == stuff.backpack && stuff.secondWep != null) {
            items.add(0, stuff.secondWep);
        }
        int j = 0;
        for (int i = 0; i < 20; ++i) {
            if (i == 0 && lastBag != stuff.backpack) {
                this.bagItems.get(i).item(lastBag);
                continue;
            }
            if (items.size() > j) {
                if (items.get(j) instanceof Bag) {
                    ++j;
                    --i;
                    continue;
                }
                this.bagItems.get(i).item((Item)items.get(j));
                ++j;
                continue;
            }
            this.bagItems.get(i).item(null);
        }
        if (this.selector == null) {
            this.promptTxt.visible = false;
            this.goldTxt.text(Integer.toString(Dungeon.gold));
            this.goldTxt.measure();
            this.gold.visible = true;
            this.goldTxt.visible = true;
            this.energyTxt.text(Integer.toString(Dungeon.energy));
            this.energyTxt.measure();
            this.energy.visible = Dungeon.energy > 0;
            this.energyTxt.visible = this.energy.visible;
        } else {
            this.promptTxt.text(this.selector.textPrompt());
            this.promptTxt.visible = true;
            this.gold.visible = false;
            this.goldTxt.visible = false;
            this.energy.visible = false;
            this.energyTxt.visible = false;
        }
        ArrayList<Bag> inventBags = stuff.getBags();
        for (int i = 0; i < this.bags.size(); ++i) {
            if (inventBags.size() > i) {
                this.bags.get(i).bag(inventBags.get(i));
                continue;
            }
            this.bags.get(i).bag(null);
        }
        boolean lostInvent = Dungeon.hero.belongings.lostInventory();
        for (InventorySlot inventorySlot : this.equipped) {
            inventorySlot.enable(!(!this.lastEnabled || inventorySlot.item() instanceof WndBag.Placeholder || this.selector != null && !this.selector.itemSelectable(inventorySlot.item()) || lostInvent && !inventorySlot.item().keptThroughLostInventory()));
        }
        for (InventorySlot inventorySlot : this.bagItems) {
            inventorySlot.enable(!(!this.lastEnabled || inventorySlot.item() == null || this.selector != null && !this.selector.itemSelectable(inventorySlot.item()) || lostInvent && !inventorySlot.item().keptThroughLostInventory()));
        }
        for (BagButton bagButton : this.bags) {
            bagButton.enable(this.lastEnabled);
        }
        this.goldTxt.alpha(this.lastEnabled ? 1.0f : 0.3f);
        this.gold.alpha(this.lastEnabled ? 1.0f : 0.3f);
        this.energyTxt.alpha(this.lastEnabled ? 1.0f : 0.3f);
        this.energy.alpha(this.lastEnabled ? 1.0f : 0.3f);
        this.layout();
    }

    public void setSelector(WndBag.ItemSelector selector) {
        this.selector = selector;
        if (selector.preferredBag() == Belongings.Backpack.class) {
            lastBag = Dungeon.hero.belongings.backpack;
        } else if (selector.preferredBag() != null) {
            Bag preferred = Dungeon.hero.belongings.getItem(selector.preferredBag());
            lastBag = preferred != null ? preferred : Dungeon.hero.belongings.backpack;
        }
        this.updateInventory();
    }

    public WndBag.ItemSelector getSelector() {
        return this.selector;
    }

    public boolean isSelecting() {
        return this.selector != null;
    }

    public static void clearTargetingSlot() {
        targetingSlot = null;
    }

    public static void useTargeting() {
        if (instance != null && InventoryPane.instance.visible && lastTarget != null && targetingSlot != null && Actor.chars().contains(lastTarget) && lastTarget.isAlive() && InventoryPane.lastTarget.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[InventoryPane.lastTarget.pos]) {
            targeting = true;
            CharSprite sprite = InventoryPane.lastTarget.sprite;
            if (sprite.parent != null) {
                sprite.parent.addToFront(crossM);
                crossM.point(sprite.center(crossM));
            }
            crossB.point(InventoryPane.targetingSlot.sprite.center(crossB));
            InventoryPane.crossB.visible = true;
        } else {
            lastTarget = null;
            targeting = false;
        }
    }

    public static void cancelTargeting() {
        if (targeting) {
            InventoryPane.crossB.visible = false;
            crossM.remove();
            targeting = false;
        }
    }

    @Override
    public synchronized void update() {
        super.update();
        if (this.lastEnabled != (Dungeon.hero.ready || !Dungeon.hero.isAlive())) {
            this.lastEnabled = Dungeon.hero.ready || !Dungeon.hero.isAlive();
            boolean lostInvent = Dungeon.hero.belongings.lostInventory();
            for (InventorySlot inventorySlot : this.equipped) {
                inventorySlot.enable(!(!this.lastEnabled || inventorySlot.item() instanceof WndBag.Placeholder || this.selector != null && !this.selector.itemSelectable(inventorySlot.item()) || lostInvent && !inventorySlot.item().keptThroughLostInventory()));
            }
            for (InventorySlot inventorySlot : this.bagItems) {
                inventorySlot.enable(!(!this.lastEnabled || inventorySlot.item() == null || this.selector != null && !this.selector.itemSelectable(inventorySlot.item()) || lostInvent && !inventorySlot.item().keptThroughLostInventory()));
            }
            for (BagButton bagButton : this.bags) {
                bagButton.enable(this.lastEnabled);
            }
            this.goldTxt.alpha(this.lastEnabled ? 1.0f : 0.3f);
            this.gold.alpha(this.lastEnabled ? 1.0f : 0.3f);
            this.energyTxt.alpha(this.lastEnabled ? 1.0f : 0.3f);
            this.energy.alpha(this.lastEnabled ? 1.0f : 0.3f);
        }
    }

    private Image bagIcon(Bag bag) {
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
        targeting = false;
        targetingSlot = null;
        lastTarget = null;
    }

    private class InventoryPaneSlot
    extends InventorySlot {
        private InventoryPaneSlot(Item item) {
            super(item);
        }

        @Override
        protected void onClick() {
            if (lastBag != this.item && !lastBag.contains(this.item) && !this.item.isEquipped(Dungeon.hero)) {
                InventoryPane.this.updateInventory();
                return;
            }
            if (targeting) {
                if (targetingSlot == this) {
                    int cell = QuickSlotButton.autoAim(lastTarget, this.item());
                    if (cell != -1) {
                        GameScene.handleCell(cell);
                    } else {
                        GameScene.handleCell(InventoryPane.lastTarget.pos);
                    }
                    return;
                }
                InventoryPane.cancelTargeting();
            }
            GameScene.centerNextWndOnInvPane();
            if (InventoryPane.this.selector != null) {
                WndBag.ItemSelector activating = InventoryPane.this.selector;
                InventoryPane.this.selector = null;
                activating.onSelect(this.item);
                InventoryPane.this.updateInventory();
            } else {
                targetingSlot = this;
                GameScene.show(new WndUseItem(null, this.item));
            }
        }

        @Override
        protected boolean onLongClick() {
            if (InventoryPane.this.selector == null && this.item.defaultAction() != null) {
                QuickSlotButton.set(this.item);
                return true;
            }
            if (InventoryPane.this.selector != null) {
                GameScene.centerNextWndOnInvPane();
                GameScene.show(new WndInfoItem(this.item));
                return true;
            }
            return false;
        }

        @Override
        protected void onMiddleClick() {
            if (lastBag != this.item && !lastBag.contains(this.item) && !this.item.isEquipped(Dungeon.hero)) {
                InventoryPane.this.updateInventory();
                return;
            }
            if (!Dungeon.hero.isAlive() || !Dungeon.hero.ready) {
                return;
            }
            if (targeting) {
                if (targetingSlot == this) {
                    this.onClick();
                }
                return;
            }
            if (InventoryPane.this.selector == null && this.item.defaultAction() != null) {
                GameScene.centerNextWndOnInvPane();
                this.item.execute(Dungeon.hero);
                if (this.item != null && this.item.usesTargeting) {
                    targetingSlot = this;
                    InventoryPane.useTargeting();
                }
            } else {
                this.onClick();
            }
        }

        @Override
        protected void onRightClick() {
            if (lastBag != this.item && !lastBag.contains(this.item) && !this.item.isEquipped(Dungeon.hero)) {
                InventoryPane.this.updateInventory();
                return;
            }
            if (!Dungeon.hero.isAlive() || !Dungeon.hero.ready) {
                return;
            }
            if (targeting) {
                return;
            }
            if (InventoryPane.this.selector == null) {
                targetingSlot = this;
                RightClickMenu r = new RightClickMenu(this.item);
                this.parent.addToFront(r);
                r.camera = this.camera();
                PointF mousePos = PointerEvent.currentHoverPos();
                mousePos = this.camera.screenToCamera((int)mousePos.x, (int)mousePos.y);
                r.setPos(mousePos.x - 3.0f, mousePos.y - 3.0f);
                r.setChildWindowOffset(new Point((int)this.centerX() - this.camera().width / 2, (int)this.centerY() - this.camera().height / 2));
            }
        }
    }

    private class BagButton
    extends IconButton {
        private static final int ACTIVE = -1722591667;
        private static final int INACTIVE = -1723710403;
        private ColorBlock bgTop;
        private ColorBlock bgBottom;
        private Bag bag;
        private int index;

        public BagButton(Bag bag, int index) {
            super(InventoryPane.this.bagIcon(bag));
            this.bag = bag;
            this.index = index;
            this.active = bag != null;
            this.visible = this.active;
        }

        public void bag(Bag bag) {
            this.bag = bag;
            this.icon(InventoryPane.this.bagIcon(bag));
            this.active = bag != null;
            this.visible = this.active;
            if (lastBag == bag) {
                this.bgTop.texture(TextureCache.createSolid(-1722591667));
                this.bgBottom.texture(TextureCache.createSolid(-1722591667));
            } else {
                this.bgTop.texture(TextureCache.createSolid(-1723710403));
                this.bgBottom.texture(TextureCache.createSolid(-1723710403));
            }
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.bgTop = new ColorBlock(1.0f, 1.0f, -1722591667);
            this.add(this.bgTop);
            this.bgBottom = new ColorBlock(1.0f, 1.0f, -1722591667);
            this.add(this.bgBottom);
        }

        @Override
        protected void layout() {
            super.layout();
            this.bgTop.size(this.width - 2.0f, 1.0f);
            this.bgTop.y = this.y;
            this.bgTop.x = this.x + 1.0f;
            this.bgBottom.size(this.width, this.height - 1.0f);
            this.bgBottom.y = this.y + 1.0f;
            this.bgBottom.x = this.x;
        }

        public void alpha(float value) {
            this.bgTop.alpha(value);
            this.bgBottom.alpha(value);
            this.icon.alpha(value);
        }

        @Override
        protected void onClick() {
            super.onClick();
            GameScene.cancel();
            lastBag = this.bag;
            InventoryPane.refresh();
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
        public GameAction secondaryTooltipAction() {
            return SPDAction.INVENTORY_SELECTOR;
        }

        @Override
        protected String hoverText() {
            if (this.bag != null) {
                return Messages.titleCase(this.bag.name());
            }
            return null;
        }
    }
}

