/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public abstract class EquipableItem
extends Item {
    public static final String AC_EQUIP = "EQUIP";
    public static final String AC_UNEQUIP = "UNEQUIP";
    protected static int slotOfUnequipped = -1;

    public EquipableItem() {
        this.bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(this.isEquipped(hero) ? AC_UNEQUIP : AC_EQUIP);
        return actions;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (super.doPickUp(hero, pos)) {
            if (!this.isIdentified() && !Document.ADVENTURERS_GUIDE.isPageRead("Identifying")) {
                GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, "Identifying");
            }
            return true;
        }
        return false;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_EQUIP)) {
            int slot = Dungeon.quickslot.getSlot(this);
            slotOfUnequipped = -1;
            this.doEquip(hero);
            if (slot != -1) {
                Dungeon.quickslot.setSlot(slot, this);
                EquipableItem.updateQuickslot();
            } else if (slotOfUnequipped != -1 && this.defaultAction() != null) {
                Dungeon.quickslot.setSlot(slotOfUnequipped, this);
                EquipableItem.updateQuickslot();
            }
        } else if (action.equals(AC_UNEQUIP)) {
            this.doUnequip(hero, true);
        }
    }

    @Override
    public void doDrop(Hero hero) {
        if (!this.isEquipped(hero) || this.doUnequip(hero, false, false)) {
            super.doDrop(hero);
        }
    }

    @Override
    public void cast(Hero user, int dst) {
        if (this.isEquipped(user) && this.quantity == 1 && !this.doUnequip(user, false, false)) {
            return;
        }
        super.cast(user, dst);
    }

    public static void equipCursed(Hero hero) {
        hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
        Sample.INSTANCE.play("sounds/cursed.mp3");
    }

    protected float timeToEquip(Hero hero) {
        return 1.0f;
    }

    public abstract boolean doEquip(Hero var1);

    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (this.cursed && hero.buff(MagicImmune.class) == null && (!hero.belongings.lostInventory() || this.keptThroughLostInventory())) {
            GLog.w(Messages.get(EquipableItem.class, "unequip_cursed", new Object[0]), new Object[0]);
            return false;
        }
        if (single) {
            hero.spendAndNext(this.timeToEquip(hero));
        } else {
            hero.spend(this.timeToEquip(hero));
        }
        slotOfUnequipped = Dungeon.quickslot.getSlot(this);
        boolean wasKept = this.keptThoughLostInvent;
        this.keptThoughLostInvent = true;
        if (!collect || !this.collect(hero.belongings.backpack)) {
            this.onDetach();
            Dungeon.quickslot.clearItem(this);
            EquipableItem.updateQuickslot();
            if (collect) {
                Dungeon.level.drop((Item)this, (int)hero.pos).sprite.drop();
            }
        }
        this.keptThoughLostInvent = wasKept;
        return true;
    }

    public final boolean doUnequip(Hero hero, boolean collect) {
        return this.doUnequip(hero, collect, true);
    }

    public void activate(Char ch) {
    }
}

