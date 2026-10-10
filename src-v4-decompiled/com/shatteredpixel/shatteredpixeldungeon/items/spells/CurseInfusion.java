/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.InventorySpell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class CurseInfusion
extends InventorySpell {
    public CurseInfusion() {
        this.image = ItemSpriteSheet.CURSE_INFUSE;
        this.talentChance = 0.25f;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item instanceof EquipableItem && item.isUpgradable() || item instanceof Wand || item instanceof SpiritBow;
    }

    @Override
    protected void onItemSelected(Item item) {
        CellEmitter.get(CurseInfusion.curUser.pos).burst(ShadowParticle.UP, 5);
        Sample.INSTANCE.play("sounds/cursed.mp3");
        item.cursed = true;
        if (item instanceof Weapon) {
            Weapon w = (Weapon)item;
            if (w.enchantment != null) {
                if (w.hasGoodEnchant() || w.curseInfusionBonus) {
                    w.enchant(Weapon.Enchantment.randomCurse(w.enchantment.getClass()));
                }
            } else {
                w.enchant(Weapon.Enchantment.randomCurse(new Class[0]));
            }
            w.curseInfusionBonus = true;
            if (w instanceof MagesStaff) {
                ((MagesStaff)w).updateWand(true);
            }
        } else if (item instanceof Armor) {
            Armor a = (Armor)item;
            if (a.glyph != null) {
                if (a.hasGoodGlyph() || a.curseInfusionBonus) {
                    a.inscribe(Armor.Glyph.randomCurse(a.glyph.getClass()));
                }
            } else {
                a.inscribe(Armor.Glyph.randomCurse(new Class[0]));
            }
            a.curseInfusionBonus = true;
        } else if (item instanceof Wand) {
            ((Wand)item).curseInfusionBonus = true;
            ((Wand)item).updateLevel();
        } else if (item instanceof RingOfMight) {
            curUser.updateHT(false);
        }
        Badges.validateItemLevelAquired(item);
        CurseInfusion.updateQuickslot();
    }

    @Override
    public int value() {
        return (int)(60.0f * ((float)this.quantity / 4.0f));
    }

    @Override
    public int energyVal() {
        return (int)(12.0f * ((float)this.quantity / 4.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 4;

        public Recipe() {
            this.inputs = new Class[]{ScrollOfRemoveCurse.class, MetalShard.class};
            this.inQuantity = new int[]{1, 1};
            this.cost = 6;
            this.output = CurseInfusion.class;
            this.outQuantity = 4;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(MetalShard.class);
            return super.brew(ingredients);
        }
    }
}

