/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class QuickSlotButton
extends Button {
    private static QuickSlotButton[] instance = new QuickSlotButton[QuickSlot.SIZE];
    private int slotNum;
    private ItemSlot slot;
    private Image crossB;
    private Image crossM;
    public static int targetingSlot = -1;
    public static Char lastTarget = null;
    private WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

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
                QuickSlotButton.set(QuickSlotButton.this.slotNum, item);
            }
        }
    };
    public static int lastVisible = instance.length;

    public QuickSlotButton(int slotNum) {
        this.slotNum = slotNum;
        this.item(QuickSlotButton.select(slotNum));
        QuickSlotButton.instance[slotNum] = this;
    }

    @Override
    public void destroy() {
        super.destroy();
        QuickSlotButton.reset();
    }

    public static void reset() {
        instance = new QuickSlotButton[QuickSlot.SIZE];
        lastTarget = null;
        targetingSlot = -1;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.slot = new ItemSlot(){

            @Override
            protected void onClick() {
                if (!Dungeon.hero.isAlive() || !Dungeon.hero.ready) {
                    return;
                }
                if (targetingSlot == QuickSlotButton.this.slotNum && lastTarget != null) {
                    int cell = QuickSlotButton.autoAim(lastTarget, QuickSlotButton.select(QuickSlotButton.this.slotNum));
                    if (cell != -1) {
                        GameScene.handleCell(cell);
                    } else {
                        GameScene.handleCell(QuickSlotButton.lastTarget.pos);
                    }
                } else {
                    Item item = QuickSlotButton.select(QuickSlotButton.this.slotNum);
                    if (Dungeon.hero.belongings.contains(item) && !GameScene.cancel()) {
                        GameScene.centerNextWndOnInvPane();
                        item.execute(Dungeon.hero);
                        if (item.usesTargeting) {
                            QuickSlotButton.this.useTargeting();
                        }
                    }
                }
            }

            @Override
            protected void onRightClick() {
                QuickSlotButton.this.onLongClick();
            }

            @Override
            protected void onMiddleClick() {
                this.onClick();
            }

            @Override
            public GameAction keyAction() {
                return QuickSlotButton.this.keyAction();
            }

            @Override
            public GameAction secondaryTooltipAction() {
                return QuickSlotButton.this.secondaryTooltipAction();
            }

            @Override
            protected boolean onLongClick() {
                return QuickSlotButton.this.onLongClick();
            }

            @Override
            protected void onPointerDown() {
                this.sprite.lightness(0.7f);
            }

            @Override
            protected void onPointerUp() {
                this.sprite.resetColor();
            }

            @Override
            protected String hoverText() {
                if (this.item == null) {
                    return Messages.titleCase(Messages.get(WndKeyBindings.class, "quickslot_" + (QuickSlotButton.this.slotNum + 1), new Object[0]));
                }
                return super.hoverText();
            }
        };
        this.slot.showExtraInfo(false);
        this.add(this.slot);
        this.crossB = Icons.TARGET.get();
        this.crossB.visible = false;
        this.add(this.crossB);
        this.crossM = new Image();
        this.crossM.copy(this.crossB);
    }

    @Override
    protected void layout() {
        super.layout();
        this.slot.fill(this);
        this.crossB.x = this.x + (this.width - this.crossB.width) / 2.0f;
        this.crossB.y = this.y + (this.height - this.crossB.height) / 2.0f;
        PixelScene.align(this.crossB);
    }

    public void alpha(float value) {
        this.slot.alpha(value);
    }

    @Override
    public void update() {
        super.update();
        if (targetingSlot != -1 && lastTarget != null && QuickSlotButton.lastTarget.sprite != null) {
            this.crossM.point(QuickSlotButton.lastTarget.sprite.center(this.crossM));
        }
    }

    @Override
    public GameAction keyAction() {
        switch (this.slotNum) {
            case 0: {
                return SPDAction.QUICKSLOT_1;
            }
            case 1: {
                return SPDAction.QUICKSLOT_2;
            }
            case 2: {
                return SPDAction.QUICKSLOT_3;
            }
            case 3: {
                return SPDAction.QUICKSLOT_4;
            }
            case 4: {
                return SPDAction.QUICKSLOT_5;
            }
            case 5: {
                return SPDAction.QUICKSLOT_6;
            }
        }
        return super.keyAction();
    }

    @Override
    public GameAction secondaryTooltipAction() {
        return SPDAction.QUICKSLOT_SELECTOR;
    }

    @Override
    protected String hoverText() {
        if (this.slot.item == null) {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "quickslot_" + (this.slotNum + 1), new Object[0]));
        }
        return super.hoverText();
    }

    @Override
    protected void onClick() {
        if (Dungeon.hero.ready && !GameScene.cancel()) {
            GameScene.selectItem(this.itemSelector);
        }
    }

    @Override
    protected void onRightClick() {
        this.onClick();
    }

    @Override
    protected void onMiddleClick() {
        this.onClick();
    }

    @Override
    protected boolean onLongClick() {
        this.onClick();
        return true;
    }

    public static void set(Item item) {
        for (int i = 0; i < lastVisible; ++i) {
            if (QuickSlotButton.select(i) != null && QuickSlotButton.select(i) != item) continue;
            QuickSlotButton.set(i, item);
            return;
        }
        QuickSlotButton.set(0, item);
    }

    public static void set(int slotNum, Item item) {
        Dungeon.quickslot.setSlot(slotNum, item);
        QuickSlotButton.refresh();
        if (Statistics.duration + Actor.now() <= 10.0f) {
            boolean containsWaterskin = false;
            for (int i = 0; i < instance.length; ++i) {
                if (!(QuickSlotButton.select(i) instanceof Waterskin)) continue;
                containsWaterskin = true;
            }
            if (containsWaterskin) {
                SPDSettings.quickslotWaterskin(true);
            }
        }
    }

    private static Item select(int slotNum) {
        return Dungeon.quickslot.getItem(slotNum);
    }

    public void item(Item item) {
        this.slot.item(item);
        this.enableSlot();
    }

    public void enable(boolean value) {
        this.active = value;
        if (value) {
            this.enableSlot();
        } else {
            this.slot.enable(false);
        }
    }

    private void enableSlot() {
        this.slot.enable(Dungeon.quickslot.isNonePlaceholder(this.slotNum) != false && (!Dungeon.hero.belongings.lostInventory() || Dungeon.quickslot.getItem(this.slotNum).keptThroughLostInventory()));
    }

    public void slotMargins(int left, int top, int right, int bottom) {
        this.slot.setMargins(left, top, right, bottom);
    }

    public static void useTargeting(int idx) {
        instance[idx].useTargeting();
    }

    private void useTargeting() {
        if (lastTarget != null && Actor.chars().contains(lastTarget) && lastTarget.isAlive() && QuickSlotButton.lastTarget.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[QuickSlotButton.lastTarget.pos]) {
            targetingSlot = this.slotNum;
            CharSprite sprite = QuickSlotButton.lastTarget.sprite;
            if (sprite.parent != null) {
                sprite.parent.addToFront(this.crossM);
                this.crossM.point(sprite.center(this.crossM));
            }
            this.crossB.point(this.slot.sprite.center(this.crossB));
            this.crossB.visible = true;
        } else {
            lastTarget = null;
            targetingSlot = -1;
        }
    }

    public static int autoAim(Char target) {
        return QuickSlotButton.autoAim(target, new Item());
    }

    public static int autoAim(Char target, Item item) {
        if (Dungeon.hero == null || target == null) {
            return -1;
        }
        if (item.targetingPos(Dungeon.hero, target.pos) == target.pos) {
            return target.pos;
        }
        PathFinder.buildDistanceMap(target.pos, BArray.not(new boolean[Dungeon.level.length()], null), 2);
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE || item.targetingPos(Dungeon.hero, i) != target.pos) continue;
            return i;
        }
        return -1;
    }

    public static void refresh() {
        for (int i = 0; i < instance.length; ++i) {
            if (instance[i] == null) continue;
            instance[i].item(QuickSlotButton.select(i));
            instance[i].enable(QuickSlotButton.instance[i].active);
        }
        if (Toolbar.SWAP_INSTANCE != null) {
            Toolbar.SWAP_INSTANCE.updateVisuals();
        }
        if (Statistics.duration + Actor.now() <= 10.0f) {
            boolean containsWaterskin = false;
            for (int i = 0; i < instance.length; ++i) {
                if (!(QuickSlotButton.select(i) instanceof Waterskin)) continue;
                containsWaterskin = true;
            }
            if (!containsWaterskin) {
                SPDSettings.quickslotWaterskin(false);
            }
        }
    }

    public static void target(Char target) {
        if (target != null && target.alignment != Char.Alignment.ALLY && !Char.hasProp(target, Char.Property.OBJECT)) {
            lastTarget = target;
            TargetHealthIndicator.instance.target(target);
            InventoryPane.lastTarget = target;
        }
    }

    public static void cancel() {
        if (targetingSlot != -1) {
            for (QuickSlotButton btn : instance) {
                btn.crossB.visible = false;
                btn.crossM.remove();
                targetingSlot = -1;
            }
        }
    }
}

