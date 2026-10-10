/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.BitmapText;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.CurrencyIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RadialMenu;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuickBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import java.util.ArrayList;

public class Toolbar
extends Component {
    private Tool btnWait;
    private Tool btnSearch;
    private Tool btnInventory;
    private Tool btnAutopilot;
    private QuickslotTool[] btnQuick;
    private SlotSwapTool btnSwap;
    private PickedUpItem pickedUp;
    private boolean lastEnabled = true;
    public boolean examining = false;
    private static Toolbar instance;
    private static CellSelector.Listener informer;
    public static boolean swappedQuickslots;
    public static SlotSwapTool SWAP_INSTANCE;

    public Toolbar() {
        instance = this;
        this.height = this.btnInventory.height();
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (instance == this) {
            instance = null;
        }
    }

    @Override
    protected void createChildren() {
        this.btnSwap = new SlotSwapTool(128, 0, 21, 23);
        this.add(this.btnSwap);
        this.btnQuick = new QuickslotTool[QuickSlot.SIZE];
        for (int i = 0; i < this.btnQuick.length; ++i) {
            this.btnQuick[i] = new QuickslotTool(64, 0, 22, 24, i);
            this.add(this.btnQuick[i]);
        }
        this.add(new Button(){

            @Override
            protected void onClick() {
                if (QuickSlotButton.targetingSlot != -1) {
                    int cell = QuickSlotButton.autoAim(QuickSlotButton.lastTarget, Dungeon.quickslot.getItem(QuickSlotButton.targetingSlot));
                    if (cell != -1) {
                        GameScene.handleCell(cell);
                    } else {
                        GameScene.handleCell(QuickSlotButton.lastTarget.pos);
                    }
                    return;
                }
                if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
                    String[] slotNames = new String[6];
                    Image[] slotIcons = new Image[6];
                    for (int i = 0; i < 6; ++i) {
                        Item item = Dungeon.quickslot.getItem(i);
                        if (!(item == null || Dungeon.quickslot.isPlaceholder(i).booleanValue() || Dungeon.hero.belongings.lostInventory() && !item.keptThroughLostInventory())) {
                            slotNames[i] = Messages.titleCase(item.name());
                            slotIcons[i] = new ItemSprite(item);
                            continue;
                        }
                        slotNames[i] = Messages.get(Toolbar.class, "quickslot_assign", new Object[0]);
                        slotIcons[i] = new ItemSprite(ItemSpriteSheet.SOMETHING);
                    }
                    Object info = "";
                    if (ControllerHandler.controllerActive) {
                        info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_select", new Object[0]) + "\n";
                        info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.RIGHT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_assign", new Object[0]) + "\n";
                        info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_cancel", new Object[0]);
                    } else {
                        info = (String)info + Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name(), new Object[0]) + ": " + Messages.get(Toolbar.class, "quickslot_select", new Object[0]) + "\n";
                        info = (String)info + Messages.get(WndKeyBindings.class, SPDAction.RIGHT_CLICK.name(), new Object[0]) + ": " + Messages.get(Toolbar.class, "quickslot_assign", new Object[0]) + "\n";
                        info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "quickslot_cancel", new Object[0]);
                    }
                    Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "quickslot_prompt", new Object[0]), (String)info, slotNames, slotIcons){

                        @Override
                        public void onSelect(final int idx, boolean alt) {
                            Item item = Dungeon.quickslot.getItem(idx);
                            if (item == null || Dungeon.quickslot.isPlaceholder(idx).booleanValue() || Dungeon.hero.belongings.lostInventory() && !item.keptThroughLostInventory() || alt) {
                                GameScene.selectItem(new WndBag.ItemSelector(){

                                    @Override
                                    public String textPrompt() {
                                        return Messages.get(QuickSlotButton.class, "select_item", new Object[0]);
                                    }

                                    @Override
                                    public boolean itemSelectable(Item item) {
                                        return item.defaultAction() != null;
                                    }

                                    @Override
                                    public void onSelect(Item item) {
                                        if (item != null) {
                                            QuickSlotButton.set(idx, item);
                                        }
                                    }
                                });
                            } else {
                                item.execute(Dungeon.hero);
                                if (item.usesTargeting) {
                                    QuickSlotButton.useTargeting(idx);
                                }
                            }
                            super.onSelect(idx, alt);
                        }
                    });
                }
            }

            @Override
            public GameAction keyAction() {
                if (Toolbar.this.btnWait.active) {
                    return SPDAction.QUICKSLOT_SELECTOR;
                }
                return null;
            }
        });
        this.btnWait = new Tool(24, 0, 20, 26){

            @Override
            protected void onClick() {
                if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
                    Toolbar.this.examining = false;
                    Dungeon.hero.rest(false);
                }
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.WAIT;
            }

            @Override
            public GameAction secondaryTooltipAction() {
                return SPDAction.WAIT_OR_PICKUP;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "wait", new Object[0]));
            }

            @Override
            protected boolean onLongClick() {
                if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
                    Toolbar.this.examining = false;
                    Dungeon.hero.rest(true);
                }
                return true;
            }
        };
        this.add(this.btnWait);
        this.btnWait.icon(176, 0, 16, 16);
        this.btnAutopilot = new AutoTool(24, 0, 20, 26){
            @Override
            protected void onClick() {
                rl.RLAutoPilot.toggle();
            }
            @Override
            protected String hoverText() {
                return "AI托管";
            }
        };
        this.add(this.btnAutopilot);
        this.add(new Button(){

            @Override
            protected void onClick() {
                if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
                    Toolbar.this.examining = false;
                    Dungeon.hero.rest(true);
                }
            }

            @Override
            public GameAction keyAction() {
                if (Toolbar.this.btnWait.active) {
                    return SPDAction.REST;
                }
                return null;
            }
        });
        this.add(new Button(){

            @Override
            protected void onClick() {
                if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
                    Dungeon.hero.waitOrPickup = true;
                    if ((Dungeon.level.heaps.get(Dungeon.hero.pos) != null || Dungeon.hero.canSelfTrample()) && Dungeon.hero.handle(Dungeon.hero.pos)) {
                        if (Dungeon.hero.hasTalent(Talent.HOLD_FAST)) {
                            Buff.affect((Char)Dungeon.hero, HoldFast.class).pos = Dungeon.hero.pos;
                        }
                        if (Dungeon.hero.hasTalent(Talent.PATIENT_STRIKE)) {
                            Buff.affect((Char)Dungeon.hero, Talent.PatientStrikeTracker.class).pos = Dungeon.hero.pos;
                        }
                        Dungeon.hero.next();
                    } else {
                        Toolbar.this.examining = false;
                        Dungeon.hero.rest(false);
                    }
                }
            }

            @Override
            protected boolean onLongClick() {
                if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
                    Toolbar.this.examining = false;
                    Dungeon.hero.rest(true);
                }
                return true;
            }

            @Override
            public GameAction keyAction() {
                if (Toolbar.this.btnWait.active) {
                    return SPDAction.WAIT_OR_PICKUP;
                }
                return null;
            }
        });
        this.btnSearch = new Tool(44, 0, 20, 26){

            @Override
            protected void onClick() {
                if (Dungeon.hero != null && Dungeon.hero.ready) {
                    if (!Toolbar.this.examining && !GameScene.cancel()) {
                        GameScene.selectCell(informer);
                        Toolbar.this.examining = true;
                    } else if (Toolbar.this.examining) {
                        informer.onSelect(null);
                        Dungeon.hero.search(true);
                    }
                }
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.EXAMINE;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "examine", new Object[0]));
            }

            @Override
            protected boolean onLongClick() {
                Dungeon.hero.search(true);
                return true;
            }
        };
        this.add(this.btnSearch);
        this.btnSearch.icon(192, 0, 16, 16);
        this.btnInventory = new Tool(0, 0, 24, 26){
            private CurrencyIndicator ind;
            private Image arrow;

            @Override
            protected void onClick() {
                if (Dungeon.hero != null && (Dungeon.hero.ready || !Dungeon.hero.isAlive())) {
                    if (SPDSettings.interfaceSize() == 2) {
                        GameScene.toggleInvPane();
                    } else if (!GameScene.cancel()) {
                        GameScene.show(new WndBag(Dungeon.hero.belongings.backpack));
                    }
                }
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.INVENTORY;
            }

            @Override
            public GameAction secondaryTooltipAction() {
                return SPDAction.INVENTORY_SELECTOR;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "inventory", new Object[0]));
            }

            @Override
            protected boolean onLongClick() {
                GameScene.show(new WndQuickBag(null));
                return true;
            }

            @Override
            protected void createChildren() {
                super.createChildren();
                this.arrow = Icons.get(Icons.COMPASS);
                this.arrow.originToCenter();
                this.arrow.visible = SPDSettings.interfaceSize() == 2;
                this.arrow.tint(4009496, 1.0f);
                this.add(this.arrow);
                this.ind = new CurrencyIndicator();
                this.add(this.ind);
            }

            @Override
            protected void layout() {
                super.layout();
                this.ind.fill(this);
                this.bringToFront(this.ind);
                this.arrow.x = this.left() + (this.width - this.arrow.width()) / 2.0f;
                this.arrow.y = this.bottom() - this.arrow.height - 1.0f;
                this.arrow.angle = this.bottom() == (float)this.camera().height ? 0.0f : 180.0f;
                PixelScene.align(this.arrow);
            }

            @Override
            public void enable(boolean value) {
                if (value != this.active) {
                    this.arrow.alpha(value ? 1.0f : 0.4f);
                }
                super.enable(value);
            }
        };
        this.add(this.btnInventory);
        this.btnInventory.icon(160, 0, 16, 16);
        this.add(new Button(){

            @Override
            protected void onClick() {
                if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
                    final ArrayList<Bag> bags = Dungeon.hero.belongings.getBags();
                    String[] names = new String[bags.size()];
                    Image[] images = new Image[bags.size()];
                    for (int i = 0; i < bags.size(); ++i) {
                        names[i] = Messages.titleCase(bags.get(i).name());
                        images[i] = new ItemSprite(bags.get(i));
                    }
                    Object info = "";
                    if (ControllerHandler.controllerActive) {
                        info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "container_select", new Object[0]) + "\n";
                        info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "container_cancel", new Object[0]);
                    } else {
                        info = (String)info + Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name(), new Object[0]) + ": " + Messages.get(Toolbar.class, "container_select", new Object[0]) + "\n";
                        info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "container_cancel", new Object[0]);
                    }
                    Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "container_prompt", new Object[0]), (String)info, names, images){

                        @Override
                        public void onSelect(int idx, boolean alt) {
                            super.onSelect(idx, alt);
                            Bag bag = (Bag)bags.get(idx);
                            final ArrayList items = (ArrayList)bag.items.clone();
                            for (Item i : bag.items) {
                                if (i instanceof Bag) {
                                    items.remove(i);
                                }
                                if (!Dungeon.hero.belongings.lostInventory() || i.keptThroughLostInventory()) continue;
                                items.remove(i);
                            }
                            if (idx == 0) {
                                Belongings b = Dungeon.hero.belongings;
                                if (b.ring() != null) {
                                    items.add(0, b.ring());
                                }
                                if (b.misc() != null) {
                                    items.add(0, b.misc());
                                }
                                if (b.artifact() != null) {
                                    items.add(0, b.artifact());
                                }
                                if (b.armor() != null) {
                                    items.add(0, b.armor());
                                }
                                if (b.secondWep() != null) {
                                    items.add(0, b.secondWep());
                                }
                                if (b.weapon() != null) {
                                    items.add(0, b.weapon());
                                }
                            }
                            if (items.size() == 0) {
                                GameScene.show(new WndMessage(Messages.get(Toolbar.class, "container_empty", new Object[0])));
                                return;
                            }
                            String[] itemNames = new String[items.size()];
                            Image[] itemIcons = new Image[items.size()];
                            for (int i = 0; i < items.size(); ++i) {
                                itemNames[i] = Messages.titleCase(((Item)items.get(i)).name());
                                itemIcons[i] = new ItemSprite((Item)items.get(i));
                            }
                            Object info = "";
                            if (ControllerHandler.controllerActive) {
                                info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "item_select", new Object[0]) + "\n";
                                info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.RIGHT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "item_use", new Object[0]) + "\n";
                                info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "item_cancel", new Object[0]);
                            } else {
                                info = (String)info + Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name(), new Object[0]) + ": " + Messages.get(Toolbar.class, "item_select", new Object[0]) + "\n";
                                info = (String)info + Messages.get(WndKeyBindings.class, SPDAction.RIGHT_CLICK.name(), new Object[0]) + ": " + Messages.get(Toolbar.class, "item_use", new Object[0]) + "\n";
                                info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "item_cancel", new Object[0]);
                            }
                            Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "item_prompt", new Object[0]), (String)info, itemNames, itemIcons){

                                @Override
                                public void onSelect(int idx, boolean alt) {
                                    super.onSelect(idx, alt);
                                    Item item = (Item)items.get(idx);
                                    if (alt && item.defaultAction() != null) {
                                        item.execute(Dungeon.hero);
                                    } else {
                                        InventoryPane.clearTargetingSlot();
                                        Game.scene().addToFront(new WndUseItem(null, item));
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public GameAction keyAction() {
                if (Toolbar.this.btnWait.active) {
                    return SPDAction.INVENTORY_SELECTOR;
                }
                return null;
            }
        });
        this.pickedUp = new PickedUpItem();
        this.add(this.pickedUp);
    }

    @Override
    protected void layout() {
        Mode mode;
        int i;
        int startingSlot;
        float right = this.width;
        int quickslotsToShow = 4;
        if (PixelScene.uiCamera.width > 152) {
            ++quickslotsToShow;
        }
        if (PixelScene.uiCamera.width > 170) {
            ++quickslotsToShow;
        }
        if (SPDSettings.quickSwapper() && quickslotsToShow < 6) {
            quickslotsToShow = 3;
            startingSlot = swappedQuickslots ? 3 : 0;
            this.btnSwap.visible = true;
            this.btnSwap.active = this.lastEnabled;
            QuickSlotButton.lastVisible = 6;
        } else {
            startingSlot = 0;
            this.btnSwap.active = false;
            this.btnSwap.visible = false;
            this.btnSwap.setPos(0.0f, PixelScene.uiCamera.height);
            QuickSlotButton.lastVisible = quickslotsToShow;
        }
        int endingSlot = startingSlot + quickslotsToShow - 1;
        for (i = 0; i < this.btnQuick.length; ++i) {
            this.btnQuick[i].visible = i >= startingSlot && i <= endingSlot;
            this.btnQuick[i].enable(this.btnQuick[i].visible && this.lastEnabled);
            if (i >= startingSlot && i <= endingSlot) continue;
            this.btnQuick[i].setPos(this.btnQuick[i].left(), PixelScene.uiCamera.height);
        }
        if (SPDSettings.interfaceSize() > 0) {
            this.btnInventory.setPos(right - this.btnInventory.width(), this.y);
            this.btnWait.setPos(this.btnInventory.left() - this.btnWait.width(), this.y);
            this.btnSearch.setPos(this.btnWait.left() - this.btnSearch.width(), this.y);
            this.btnAutopilot.setPos(this.btnSearch.left() - this.btnAutopilot.width(), this.y);
            right = this.btnAutopilot.left();
            for (i = endingSlot; i >= startingSlot; --i) {
                if (i == endingSlot) {
                    this.btnQuick[i].border(0, 2);
                    this.btnQuick[i].frame(106, 0, 19, 24);
                } else if (i == 0) {
                    this.btnQuick[i].border(2, 1);
                    this.btnQuick[i].frame(86, 0, 20, 24);
                } else {
                    this.btnQuick[i].border(0, 1);
                    this.btnQuick[i].frame(88, 0, 18, 24);
                }
                this.btnQuick[i].setPos(right - this.btnQuick[i].width(), this.y + 2.0f);
                right = this.btnQuick[i].left();
            }
            return;
        }
        for (i = startingSlot; i <= endingSlot; ++i) {
            if (i == startingSlot && !SPDSettings.flipToolbar() || i == endingSlot && SPDSettings.flipToolbar()) {
                this.btnQuick[i].border(0, 2);
                this.btnQuick[i].frame(106, 0, 19, 24);
                continue;
            }
            if (i == startingSlot && SPDSettings.flipToolbar() || i == endingSlot && !SPDSettings.flipToolbar()) {
                this.btnQuick[i].border(2, 1);
                this.btnQuick[i].frame(86, 0, 20, 24);
                continue;
            }
            this.btnQuick[i].border(0, 1);
            this.btnQuick[i].frame(88, 0, 18, 24);
        }
        float shift = 0.0f;
        try {
            mode = Mode.valueOf(SPDSettings.toolbarMode());
        }
        catch (Exception e) {
            Game.reportException(e);
            mode = PixelScene.landscape() ? Mode.GROUP : Mode.SPLIT;
        }
        switch (mode.ordinal()) {
            case 0: {
                this.btnWait.setPos(this.x, this.y);
                this.btnSearch.setPos(this.btnWait.right(), this.y);
                this.btnInventory.setPos(right - this.btnInventory.width(), this.y);
                float left = 0.0f;
                this.btnQuick[startingSlot].setPos(this.btnInventory.left() - this.btnQuick[startingSlot].width(), this.y + 2.0f);
                for (int i2 = startingSlot + 1; i2 <= endingSlot; ++i2) {
                    this.btnQuick[i2].setPos(this.btnQuick[i2 - 1].left() - this.btnQuick[i2].width(), this.y + 2.0f);
                    shift = this.btnSearch.right() - this.btnQuick[i2].left();
                }
                if (!this.btnSwap.visible) break;
                this.btnSwap.setPos(this.btnQuick[endingSlot].left() - (this.btnSwap.width() - 2.0f), this.y + 3.0f);
                shift = this.btnSearch.right() - this.btnSwap.left();
                break;
            }
            case 2: {
                float toolbarWidth = this.btnWait.width() + this.btnSearch.width() + this.btnInventory.width();
                for (QuickslotTool slot : this.btnQuick) {
                    if (!slot.visible) continue;
                    toolbarWidth += slot.width();
                }
                if (this.btnSwap.visible) {
                    toolbarWidth += this.btnSwap.width() - 2.0f;
                }
                right = (this.width + toolbarWidth) / 2.0f;
            }
            case 1: {
                this.btnWait.setPos(right - this.btnWait.width(), this.y);
                this.btnSearch.setPos(this.btnWait.left() - this.btnSearch.width(), this.y);
                this.btnInventory.setPos(this.btnSearch.left() - this.btnInventory.width(), this.y);
                this.btnQuick[startingSlot].setPos(this.btnInventory.left() - this.btnQuick[startingSlot].width(), this.y + 2.0f);
                for (int i3 = startingSlot + 1; i3 <= endingSlot; ++i3) {
                    this.btnQuick[i3].setPos(this.btnQuick[i3 - 1].left() - this.btnQuick[i3].width(), this.y + 2.0f);
                    shift = -this.btnQuick[i3].left();
                }
                if (!this.btnSwap.visible) break;
                this.btnSwap.setPos(this.btnQuick[endingSlot].left() - (this.btnSwap.width() - 2.0f), this.y + 3.0f);
                shift = -this.btnSwap.left();
            }
        }
        if (shift > 0.0f) {
            shift /= 2.0f;
            for (int i4 = startingSlot; i4 <= endingSlot; ++i4) {
                this.btnQuick[i4].setPos(this.btnQuick[i4].left() + shift, this.btnQuick[i4].top());
            }
            if (this.btnSwap.visible) {
                this.btnSwap.setPos(this.btnSwap.left() + shift, this.btnSwap.top());
            }
        }
        right = this.width;
        if (SPDSettings.flipToolbar()) {
            this.btnWait.setPos(right - this.btnWait.right(), this.y);
            this.btnSearch.setPos(right - this.btnSearch.right(), this.y);
            this.btnInventory.setPos(right - this.btnInventory.right(), this.y);
            for (int i5 = startingSlot; i5 <= endingSlot; ++i5) {
                this.btnQuick[i5].setPos(right - this.btnQuick[i5].right(), this.y + 2.0f);
            }
            if (this.btnSwap.visible) {
                this.btnSwap.setPos(right - this.btnSwap.right(), this.y + 3.0f);
            }
        }
        if (this.btnAutopilot != null) {
            this.btnAutopilot.setPos(this.btnWait.left() - this.btnAutopilot.width(), this.y);
        }
    }

    public static void updateLayout() {
        if (instance != null) {
            instance.layout();
        }
    }

    @Override
    public void update() {
        super.update();
        boolean rlToolbarEnabled = (Dungeon.hero.ready && Dungeon.hero.isAlive()) || rl.RLAutoPilot.active;
        if (this.lastEnabled != rlToolbarEnabled) {
            this.lastEnabled = rlToolbarEnabled;
            for (Gizmo tool : this.members.toArray(new Gizmo[0])) {
                if (!(tool instanceof Tool)) continue;
                ((Tool)tool).enable(this.lastEnabled);
            }
        }
        if (!Dungeon.hero.isAlive()) {
            this.btnInventory.enable(true);
        }
    }

    public void alpha(float value) {
        this.btnWait.alpha(value);
        this.btnSearch.alpha(value);
        this.btnInventory.alpha(value);
        for (QuickslotTool tool : this.btnQuick) {
            tool.alpha(value);
        }
        this.btnSwap.alpha(value);
    }

    public void pickup(Item item, int cell) {
        this.pickedUp.reset(item, cell, this.btnInventory.centerX(), this.btnInventory.centerY());
    }

    static {
        informer = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer cell) {
                if (instance != null) {
                    Toolbar.instance.examining = false;
                    GameScene.examineCell(cell);
                }
            }

            @Override
            public String prompt() {
                return Messages.get(Toolbar.class, "examine_prompt", new Object[0]);
            }
        };
        swappedQuickslots = false;
    }

    private static class Tool
    extends Button {
        private static final int BGCOLOR = 8093811;
        private Image base;
        private Image icon;

        public Tool(int x, int y, int width, int height) {
            this.hotArea.blockLevel = 0;
            this.frame(x, y, width, height);
        }

        public void frame(int x, int y, int width, int height) {
            this.base.frame(x, y, width, height);
            this.width = width;
            this.height = height;
        }

        public void icon(int x, int y, int width, int height) {
            if (this.icon == null) {
                this.icon = new Image("interfaces/toolbar.png");
            }
            this.add(this.icon);
            this.icon.frame(x, y, width, height);
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.base = new Image("interfaces/toolbar.png");
            this.add(this.base);
        }

        @Override
        protected void layout() {
            super.layout();
            this.base.x = this.x;
            this.base.y = this.y;
            if (this.icon != null) {
                this.icon.x = this.x + (this.width() - this.icon.width()) / 2.0f;
                this.icon.y = this.y + (this.height() - this.icon.height()) / 2.0f;
            }
        }

        public void alpha(float value) {
            this.base.alpha(value);
            if (this.icon != null) {
                this.icon.alpha(value);
            }
        }

        @Override
        protected void onPointerDown() {
            this.base.brightness(1.4f);
        }

        @Override
        protected void onPointerUp() {
            if (this.active) {
                this.base.resetColor();
            } else {
                this.base.tint(8093811, 0.7f);
            }
        }

        public void enable(boolean value) {
            if (value != this.active) {
                if (this.icon != null) {
                    this.icon.alpha(value ? 1.0f : 0.4f);
                }
                this.active = value;
            }
        }
    }

    /** AI 托管按钮：Tool + "AI" 文字图标（大脑智能指示）。 */
    private static class AutoTool
    extends Tool {
        private BitmapText label;
        AutoTool(int x, int y, int width, int height) {
            super(x, y, width, height);
        }
        @Override
        protected void createChildren() {
            super.createChildren();
            label = new BitmapText("AI", PixelScene.pixelFont);
            label.measure();
            this.add(label);
        }
        @Override
        protected void layout() {
            super.layout();
            if (label != null) {
                label.x = this.x + (this.width() - label.width()) / 2.0f;
                label.y = this.y + (this.height() - label.height()) / 2.0f;
            }
        }
        @Override
        public void alpha(float value) {
            super.alpha(value);
            if (label != null) label.alpha(value);
        }
    }

    public static class SlotSwapTool
    extends Tool {
        private Image[] icons = new Image[4];
        private Item[] items = new Item[4];

        public SlotSwapTool(int x, int y, int width, int height) {
            super(x, y, width, height);
            SWAP_INSTANCE = this;
            this.updateVisuals();
        }

        @Override
        public synchronized void destroy() {
            super.destroy();
            if (SWAP_INSTANCE == this) {
                SWAP_INSTANCE = null;
            }
        }

        @Override
        protected void onClick() {
            super.onClick();
            swappedQuickslots = !swappedQuickslots;
            Toolbar.updateLayout();
            this.updateVisuals();
        }

        public void updateVisuals() {
            int slotDir;
            int slot;
            if (this.icons[0] == null) {
                this.icons[0] = Icons.get(Icons.CHANGES);
                this.icons[0].scale.set(PixelScene.align(0.45f));
                this.add(this.icons[0]);
            }
            if (SPDSettings.flipToolbar()) {
                slot = swappedQuickslots ? 0 : 3;
                slotDir = 1;
            } else {
                slot = swappedQuickslots ? 2 : 5;
                slotDir = -1;
            }
            for (int i = 1; i < 4; ++i) {
                if (this.items[i] == Dungeon.quickslot.getItem(slot)) {
                    slot += slotDir;
                    continue;
                }
                this.items[i] = Dungeon.quickslot.getItem(slot);
                if (this.icons[i] != null) {
                    this.icons[i].killAndErase();
                    this.icons[i] = null;
                }
                if (this.items[i] != null) {
                    this.icons[i] = new ItemSprite(this.items[i]);
                    this.icons[i].scale.set(PixelScene.align(0.45f));
                    if (Dungeon.quickslot.isPlaceholder(slot).booleanValue()) {
                        this.icons[i].alpha(0.29f);
                    }
                    this.add(this.icons[i]);
                }
                slot += slotDir;
            }
            this.icons[0].x = this.x + 2.0f + (8.0f - this.icons[0].width()) / 2.0f;
            this.icons[0].y = this.y + 2.0f + (9.0f - this.icons[0].height()) / 2.0f;
            PixelScene.align(this.icons[0]);
            if (this.icons[1] != null) {
                this.icons[1].x = this.x + 11.0f + (8.0f - this.icons[1].width()) / 2.0f;
                this.icons[1].y = this.y + 2.0f + (9.0f - this.icons[1].height()) / 2.0f;
                PixelScene.align(this.icons[1]);
            }
            if (this.icons[2] != null) {
                this.icons[2].x = this.x + 2.0f + (8.0f - this.icons[2].width()) / 2.0f;
                this.icons[2].y = this.y + 12.0f + (9.0f - this.icons[2].height()) / 2.0f;
                PixelScene.align(this.icons[2]);
            }
            if (this.icons[3] != null) {
                this.icons[3].x = this.x + 11.0f + (8.0f - this.icons[3].width()) / 2.0f;
                this.icons[3].y = this.y + 12.0f + (9.0f - this.icons[3].height()) / 2.0f;
                PixelScene.align(this.icons[3]);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            this.updateVisuals();
        }

        @Override
        public void alpha(float value) {
            super.alpha(value);
            for (Image im : this.icons) {
                if (im == null) continue;
                im.alpha(value);
            }
        }

        @Override
        public void enable(boolean value) {
            super.enable(value);
            for (Image ic : this.icons) {
                if (ic == null || !(ic.alpha() >= 0.3f)) continue;
                ic.alpha(value ? 1.0f : 0.3f);
            }
        }
    }

    private static class QuickslotTool
    extends Tool {
        private QuickSlotButton slot;
        private int borderLeft = 2;
        private int borderRight = 2;

        public QuickslotTool(int x, int y, int width, int height, int slotNum) {
            super(x, y, width, height);
            this.slot = new QuickSlotButton(slotNum);
            this.add(this.slot);
        }

        public void border(int left, int right) {
            this.borderLeft = left;
            this.borderRight = right;
            this.layout();
        }

        @Override
        protected void layout() {
            super.layout();
            this.slot.setRect(this.x, this.y, this.width, this.height);
            this.slot.slotMargins(this.borderLeft, 2, this.borderRight, 2);
        }

        @Override
        public void alpha(float value) {
            super.alpha(value);
            this.slot.alpha(value);
        }

        @Override
        public void enable(boolean value) {
            super.enable(value && this.visible);
            this.slot.enable(value && this.visible);
        }
    }

    public static class PickedUpItem
    extends ItemSprite {
        private static final float DURATION = 0.5f;
        private float startScale;
        private float startX;
        private float startY;
        private float endX;
        private float endY;
        private float left;

        public PickedUpItem() {
            this.originToCenter();
            this.visible = false;
            this.active = false;
        }

        public void reset(Item item, int cell, float endX, float endY) {
            this.view(item);
            this.visible = true;
            this.active = true;
            PointF tile = DungeonTerrainTilemap.raisedTileCenterToWorld(cell);
            Point screen = Camera.main.cameraToScreen(tile.x, tile.y);
            PointF start = this.camera().screenToCamera(screen.x, screen.y);
            this.x = this.startX = start.x - this.width() / 2.0f;
            this.y = this.startY = start.y - this.width() / 2.0f;
            this.endX = endX - this.width() / 2.0f;
            this.endY = endY - this.width() / 2.0f;
            this.left = 0.5f;
            this.startScale = Camera.main.zoom / this.camera().zoom;
            this.scale.set(this.startScale);
        }

        @Override
        public void update() {
            super.update();
            this.left -= Game.elapsed;
            if (this.left <= 0.0f) {
                this.active = false;
                this.visible = false;
                if (this.emitter != null) {
                    this.emitter.on = false;
                }
            } else {
                float p = this.left / 0.5f;
                this.scale.set(this.startScale * (float)Math.sqrt(p));
                this.x = this.startX * p + this.endX * (1.0f - p);
                this.y = this.startY * p + this.endY * (1.0f - p);
            }
        }
    }

    public static enum Mode {
        SPLIT,
        GROUP,
        CENTER;

    }
}

