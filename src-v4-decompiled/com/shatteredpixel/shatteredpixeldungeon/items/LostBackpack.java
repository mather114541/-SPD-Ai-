/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class LostBackpack
extends Item {
    public LostBackpack() {
        this.image = ItemSpriteSheet.BACKPACK;
        this.unique = true;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (hero.buff(LostInventory.class) != null) {
            hero.buff(LostInventory.class).detach();
        }
        MagicalHolster holster = hero.belongings.getItem(MagicalHolster.class);
        for (Item i : hero.belongings) {
            if (i.keptThroughLostInventory()) {
                i.keptThoughLostInvent = false;
                continue;
            }
            if (i instanceof EquipableItem && i.isEquipped(hero)) {
                ((EquipableItem)i).activate(hero);
                continue;
            }
            if (i instanceof CloakOfShadows && hero.hasTalent(Talent.LIGHT_CLOAK)) {
                ((CloakOfShadows)i).activate(hero);
                continue;
            }
            if (i instanceof HolyTome && hero.hasTalent(Talent.LIGHT_READING)) {
                ((HolyTome)i).activate(hero);
                continue;
            }
            if (i instanceof Wand) {
                if (holster != null && holster.contains(i)) {
                    ((Wand)i).charge(hero, 0.85f);
                    continue;
                }
                ((Wand)i).charge(hero);
                continue;
            }
            if (!(i instanceof MagesStaff)) continue;
            ((MagesStaff)i).applyWandChargeBuff(hero);
        }
        hero.updateHT(false);
        Item.updateQuickslot();
        Sample.INSTANCE.play("sounds/dewdrop.mp3");
        hero.spendAndNext(this.pickupDelay());
        GameScene.pickUp(this, pos);
        ((HeroSprite)hero.sprite).updateArmor();
        Notes.remove(Notes.Landmark.LOST_PACK);
        return true;
    }
}

