/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;
import java.util.Collections;

public class CursingTrap
extends Trap {
    public CursingTrap() {
        this.color = 5;
        this.shape = 1;
    }

    @Override
    public void activate() {
        Heap heap;
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.get(this.pos).burst(ShadowParticle.UP, 5);
            Sample.INSTANCE.play("sounds/cursed.mp3");
        }
        if ((heap = (Heap)Dungeon.level.heaps.get(this.pos)) != null) {
            for (Item item : heap.items) {
                if (!item.isUpgradable() || item instanceof MissileWeapon) continue;
                CursingTrap.curse(item);
            }
        }
        if (Dungeon.hero.pos == this.pos && !Dungeon.hero.flying) {
            CursingTrap.curse(Dungeon.hero);
        }
    }

    public static void curse(Hero hero) {
        Armor armor;
        ArrayList<EquipableItem> priorityCurse = new ArrayList<EquipableItem>();
        ArrayList<EquipableItem> canCurse = new ArrayList<EquipableItem>();
        KindOfWeapon weapon = hero.belongings.weapon();
        if (weapon instanceof Weapon && !(weapon instanceof MagesStaff)) {
            if (((Weapon)weapon).enchantment == null) {
                priorityCurse.add(weapon);
            } else {
                canCurse.add(weapon);
            }
        }
        if ((weapon = hero.belongings.secondWep()) instanceof Weapon && !(weapon instanceof MagesStaff)) {
            if (((Weapon)weapon).enchantment == null) {
                priorityCurse.add(weapon);
            } else {
                canCurse.add(weapon);
            }
        }
        if ((armor = hero.belongings.armor()) != null) {
            if (armor.glyph == null) {
                priorityCurse.add(armor);
            } else {
                canCurse.add(armor);
            }
        }
        if (!priorityCurse.isEmpty()) {
            Collections.shuffle(priorityCurse);
            CursingTrap.curse((Item)priorityCurse.remove(0));
        } else if (!canCurse.isEmpty()) {
            Collections.shuffle(canCurse);
            CursingTrap.curse((Item)canCurse.remove(0));
        }
        EquipableItem.equipCursed(hero);
        GLog.n(Messages.get(CursingTrap.class, "curse", new Object[0]), new Object[0]);
    }

    private static void curse(Item item) {
        item.cursedKnown = true;
        item.cursed = true;
        if (item instanceof Weapon) {
            Weapon w = (Weapon)item;
            if (w.enchantment == null) {
                w.enchant(Weapon.Enchantment.randomCurse(new Class[0]));
            }
        }
        if (item instanceof Armor) {
            Armor a = (Armor)item;
            if (a.glyph == null) {
                a.inscribe(Armor.Glyph.randomCurse(new Class[0]));
            }
        }
        Item.updateQuickslot();
    }
}

