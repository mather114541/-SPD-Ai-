/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.utils.Rect;

public class ItemSlot
extends Button {
    public static final int DEGRADED = 0xFF4444;
    public static final int UPGRADED = 0x44FF44;
    public static final int FADED = 0x999999;
    public static final int WARNING = 0xFF8800;
    public static final int ENHANCED = 0x3399FF;
    public static final int MASTERED = 0xFFFF44;
    public static final int CURSE_INFUSED = 0x8800FF;
    private static final float ENABLED = 1.0f;
    private static final float DISABLED = 0.3f;
    private Rect margin = new Rect();
    protected ItemSprite sprite;
    protected Item item;
    protected BitmapText status;
    protected BitmapText extra;
    protected Image itemIcon;
    protected BitmapText level;
    private static final String TXT_STRENGTH = ":%d";
    private static final String TXT_TYPICAL_STR = "%d?";
    private static final String TXT_LEVEL = "%+d";
    public static final Item CHEST = new Item(){

        @Override
        public int image() {
            return ItemSpriteSheet.CHEST;
        }

        @Override
        public String name() {
            return Messages.get(Heap.class, "chest", new Object[0]);
        }
    };
    public static final Item LOCKED_CHEST = new Item(){

        @Override
        public int image() {
            return ItemSpriteSheet.LOCKED_CHEST;
        }

        @Override
        public String name() {
            return Messages.get(Heap.class, "locked_chest", new Object[0]);
        }
    };
    public static final Item CRYSTAL_CHEST = new Item(){

        @Override
        public int image() {
            return ItemSpriteSheet.CRYSTAL_CHEST;
        }

        @Override
        public String name() {
            return Messages.get(Heap.class, "crystal_chest", new Object[0]);
        }
    };
    public static final Item TOMB = new Item(){

        @Override
        public int image() {
            return ItemSpriteSheet.TOMB;
        }

        @Override
        public String name() {
            return Messages.get(Heap.class, "tomb", new Object[0]);
        }
    };
    public static final Item SKELETON = new Item(){

        @Override
        public int image() {
            return ItemSpriteSheet.BONES;
        }

        @Override
        public String name() {
            return Messages.get(Heap.class, "skeleton", new Object[0]);
        }
    };
    public static final Item REMAINS = new Item(){

        @Override
        public int image() {
            return ItemSpriteSheet.REMAINS;
        }

        @Override
        public String name() {
            return Messages.get(Heap.class, "remains", new Object[0]);
        }
    };

    public ItemSlot() {
        this.sprite.visible(false);
        this.enable(false);
    }

    public ItemSlot(Item item) {
        this();
        this.item(item);
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.sprite = new ItemSprite();
        this.add(this.sprite);
        this.status = new BitmapText(PixelScene.pixelFont);
        this.add(this.status);
        this.extra = new BitmapText(PixelScene.pixelFont);
        this.add(this.extra);
        this.level = new BitmapText(PixelScene.pixelFont);
        this.add(this.level);
    }

    @Override
    protected void layout() {
        super.layout();
        this.sprite.x = this.x + (float)this.margin.left + (this.width - this.sprite.width - (float)(this.margin.left + this.margin.right)) / 2.0f;
        this.sprite.y = this.y + (float)this.margin.top + (this.height - this.sprite.height - (float)(this.margin.top + this.margin.bottom)) / 2.0f;
        PixelScene.align(this.sprite);
        if (this.status != null) {
            this.status.measure();
            if (this.status.width > this.width - (float)(this.margin.left + this.margin.right)) {
                this.status.scale.set(PixelScene.align(0.8f));
            } else {
                this.status.scale.set(1.0f);
            }
            this.status.x = this.x + (float)this.margin.left;
            this.status.y = this.y + (float)this.margin.top;
            PixelScene.align(this.status);
        }
        if (this.extra != null) {
            this.extra.x = this.x + (this.width - this.extra.width()) - (float)this.margin.right;
            this.extra.y = this.y + (float)this.margin.top;
            PixelScene.align(this.extra);
            if (this.status.width() + this.extra.width() > this.width) {
                this.extra.visible = false;
            } else if (this.item != null) {
                this.extra.visible = true;
            }
        }
        if (this.itemIcon != null) {
            if (this.width >= 24.0f || this.height >= 24.0f) {
                this.itemIcon.x = this.x + this.width - (8.0f + this.itemIcon.width()) / 2.0f - (float)this.margin.right;
                this.itemIcon.y = this.y + (8.0f - this.itemIcon.height) / 2.0f + (float)this.margin.top;
            } else {
                this.itemIcon.x = this.x + this.width - this.itemIcon.width() - (float)this.margin.right;
                this.itemIcon.y = this.y + (float)this.margin.top;
            }
            PixelScene.align(this.itemIcon);
        }
        if (this.level != null) {
            this.level.x = this.x + (this.width - this.level.width()) - (float)this.margin.right;
            this.level.y = this.y + (this.height - this.level.baseLine() - 1.0f) - (float)this.margin.bottom;
            PixelScene.align(this.level);
        }
    }

    public void alpha(float value) {
        if (!this.active) {
            value *= 0.3f;
        }
        if (this.sprite != null) {
            this.sprite.alpha(value);
        }
        if (this.extra != null) {
            this.extra.alpha(value);
        }
        if (this.status != null) {
            this.status.alpha(value);
        }
        if (this.itemIcon != null) {
            this.itemIcon.alpha(value);
        }
        if (this.level != null) {
            this.level.alpha(value);
        }
    }

    @Override
    public void clear() {
        this.item(null);
        this.enable(true);
        this.sprite.visible(true);
        this.sprite.view(ItemSpriteSheet.SOMETHING, null);
        this.layout();
    }

    public void item(Item item) {
        if (this.item == item) {
            if (item != null) {
                this.sprite.view(item);
            }
            this.updateText();
            return;
        }
        this.item = item;
        if (item == null) {
            this.enable(false);
            this.sprite.visible(false);
            this.updateText();
        } else {
            this.enable(true);
            this.sprite.visible(true);
            this.sprite.view(item);
            this.updateText();
        }
    }

    public void updateText() {
        if (this.itemIcon != null) {
            this.remove(this.itemIcon);
            this.itemIcon = null;
        }
        if (this.item == null) {
            this.level.visible = false;
            this.extra.visible = false;
            this.status.visible = false;
            return;
        }
        this.level.visible = true;
        this.extra.visible = true;
        this.status.visible = true;
        this.status.text(this.item.status());
        if (this.item instanceof MissileWeapon && ((MissileWeapon)this.item).durabilityLeft() <= 50.0f && ((MissileWeapon)this.item).durabilityLeft() <= ((MissileWeapon)this.item).durabilityPerUse()) {
            this.status.hardlight(0xFF8800);
        } else {
            this.status.resetColor();
        }
        if (this.item.icon != -1 && (this.item.isIdentified() || this.item instanceof Ring && ((Ring)this.item).isKnown())) {
            this.extra.text(null);
            this.itemIcon = new Image("sprites/item_icons.png");
            this.itemIcon.frame(ItemSpriteSheet.Icons.film.get(this.item.icon));
            this.add(this.itemIcon);
        } else if (this.item instanceof Weapon || this.item instanceof Armor) {
            int str;
            if (this.item.levelKnown) {
                str = this.item instanceof Weapon ? ((Weapon)this.item).STRReq() : ((Armor)this.item).STRReq();
                this.extra.text(Messages.format(TXT_STRENGTH, str));
                if (Dungeon.hero != null && str > Dungeon.hero.STR()) {
                    this.extra.hardlight(0xFF4444);
                } else if (this.item instanceof Weapon && ((Weapon)this.item).masteryPotionBonus) {
                    this.extra.hardlight(0xFFFF44);
                } else if (this.item instanceof Armor && ((Armor)this.item).masteryPotionBonus) {
                    this.extra.hardlight(0xFFFF44);
                } else {
                    this.extra.resetColor();
                }
            } else {
                str = this.item instanceof Weapon ? ((Weapon)this.item).STRReq(0) : ((Armor)this.item).STRReq(0);
                this.extra.text(Messages.format(TXT_TYPICAL_STR, str));
                this.extra.hardlight(0xFF8800);
            }
            this.extra.measure();
        } else {
            this.extra.text(null);
        }
        int trueLvl = this.item.visiblyUpgraded();
        int buffedLvl = this.item.buffedVisiblyUpgraded();
        if (trueLvl != 0 || buffedLvl != 0) {
            this.level.text(Messages.format(TXT_LEVEL, buffedLvl));
            this.level.measure();
            if (trueLvl == buffedLvl || buffedLvl <= 0) {
                if (buffedLvl > 0) {
                    if (this.item instanceof Weapon && ((Weapon)this.item).curseInfusionBonus || this.item instanceof Armor && ((Armor)this.item).curseInfusionBonus || this.item instanceof Wand && ((Wand)this.item).curseInfusionBonus) {
                        this.level.hardlight(0x8800FF);
                    } else {
                        this.level.hardlight(0x44FF44);
                    }
                } else {
                    this.level.hardlight(0xFF4444);
                }
            } else {
                this.level.hardlight(buffedLvl > trueLvl ? 0x3399FF : 0xFF8800);
            }
        } else {
            this.level.text(null);
        }
        this.layout();
    }

    public void enable(boolean value) {
        this.active = value;
        if (!this.active && pressedButton == this) {
            this.hotArea.reset();
            pressedButton = null;
            this.clickReady = false;
            this.onPointerUp();
        }
        float alpha = value ? 1.0f : 0.3f;
        this.sprite.alpha(alpha);
        this.status.alpha(alpha);
        this.extra.alpha(alpha);
        this.level.alpha(alpha);
        if (this.itemIcon != null) {
            this.itemIcon.alpha(alpha);
        }
    }

    public void showExtraInfo(boolean show) {
        if (show) {
            this.add(this.extra);
        } else {
            this.remove(this.extra);
        }
    }

    public void textVisible(boolean visible) {
        if (visible) {
            this.add(this.status);
            this.add(this.extra);
            this.add(this.level);
        } else {
            this.remove(this.status);
            this.remove(this.extra);
            this.remove(this.level);
        }
    }

    public void setMargins(int left, int top, int right, int bottom) {
        this.margin.set(left, top, right, bottom);
        this.layout();
    }

    @Override
    protected String hoverText() {
        if (this.item != null && this.item.name() != null) {
            return Messages.titleCase(this.item.name());
        }
        return super.hoverText();
    }
}

