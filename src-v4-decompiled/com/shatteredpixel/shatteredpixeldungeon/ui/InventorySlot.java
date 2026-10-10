/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.audio.Sample;

public class InventorySlot
extends ItemSlot {
    private static final int NORMAL = -1722591667;
    private static final int EQUIPPED = -1718512756;
    private ColorBlock bg;

    public InventorySlot(Item item) {
        super(item);
    }

    @Override
    protected void createChildren() {
        this.bg = new ColorBlock(1.0f, 1.0f, -1722591667);
        this.add(this.bg);
        super.createChildren();
    }

    @Override
    protected void layout() {
        this.bg.size(this.width, this.height);
        this.bg.x = this.x;
        this.bg.y = this.y;
        super.layout();
    }

    @Override
    public void alpha(float value) {
        super.alpha(value);
        this.bg.alpha(value);
    }

    @Override
    public void item(Item item) {
        super.item(item);
        boolean bl = this.bg.visible = !(item instanceof Gold) && !(item instanceof Bag);
        if (item != null) {
            boolean equipped = item.isEquipped(Dungeon.hero) || item == Dungeon.hero.belongings.weapon || item == Dungeon.hero.belongings.armor || item == Dungeon.hero.belongings.artifact || item == Dungeon.hero.belongings.misc || item == Dungeon.hero.belongings.ring || item == Dungeon.hero.belongings.secondWep;
            this.bg.texture(TextureCache.createSolid(equipped ? -1718512756 : -1722591667));
            this.bg.resetColor();
            if (item.cursed && item.cursedKnown) {
                this.bg.ra = 0.3f;
                this.bg.ga = -0.15f;
                this.bg.ba = -0.15f;
            } else if (!item.isIdentified()) {
                if ((item instanceof EquipableItem || item instanceof Wand) && item.cursedKnown) {
                    this.bg.ba = 0.3f;
                    this.bg.ra = -0.1f;
                } else {
                    this.bg.ra = 0.35f;
                    this.bg.ba = 0.35f;
                }
            }
            if (item.name() == null) {
                this.enable(false);
            } else if (Dungeon.hero.belongings.lostInventory() && !item.keptThroughLostInventory()) {
                this.enable(false);
            }
        } else {
            this.bg.texture(TextureCache.createSolid(-1722591667));
            this.bg.resetColor();
        }
    }

    public Item item() {
        return this.item;
    }

    @Override
    protected void onPointerDown() {
        this.bg.brightness(1.5f);
        Sample.INSTANCE.play("sounds/click.mp3", 0.7f, 0.7f, 1.2f);
    }

    @Override
    protected void onPointerUp() {
        this.bg.brightness(1.0f);
    }
}

