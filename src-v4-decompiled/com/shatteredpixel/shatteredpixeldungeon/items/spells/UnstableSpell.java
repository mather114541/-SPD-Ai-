/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UnstableSpell
extends Spell {
    private static HashMap<Class<? extends Scroll>, Float> scrollChances = new HashMap();
    private static HashSet<Class<? extends Scroll>> nonCombatScrolls;
    private static HashSet<Class<? extends Scroll>> combatScrolls;

    public UnstableSpell() {
        this.image = ItemSpriteSheet.UNSTABLE_SPELL;
    }

    @Override
    protected void onCast(Hero hero) {
        this.detach(UnstableSpell.curUser.belongings.backpack);
        UnstableSpell.updateQuickslot();
        Scroll s = Reflection.newInstance(Random.chances(scrollChances));
        if (hero.visibleEnemies() == 0) {
            while (!nonCombatScrolls.contains(s.getClass())) {
                s = Reflection.newInstance(Random.chances(scrollChances));
            }
        } else {
            while (!combatScrolls.contains(s.getClass())) {
                s = Reflection.newInstance(Random.chances(scrollChances));
            }
        }
        s.anonymize();
        s.talentFactor = 1.0f;
        s.talentChance = 1.0f;
        curItem = s;
        s.doRead();
        Catalog.countUse(this.getClass());
    }

    @Override
    public int value() {
        return 40 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 8 * this.quantity;
    }

    static {
        scrollChances.put(ScrollOfIdentify.class, Float.valueOf(3.0f));
        scrollChances.put(ScrollOfRemoveCurse.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfMagicMapping.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfMirrorImage.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfRecharging.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfLullaby.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfRetribution.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfRage.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfTeleportation.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfTerror.class, Float.valueOf(2.0f));
        scrollChances.put(ScrollOfTransmutation.class, Float.valueOf(1.0f));
        nonCombatScrolls = new HashSet();
        nonCombatScrolls.add(ScrollOfIdentify.class);
        nonCombatScrolls.add(ScrollOfRemoveCurse.class);
        nonCombatScrolls.add(ScrollOfMagicMapping.class);
        nonCombatScrolls.add(ScrollOfRecharging.class);
        nonCombatScrolls.add(ScrollOfLullaby.class);
        nonCombatScrolls.add(ScrollOfTeleportation.class);
        nonCombatScrolls.add(ScrollOfTransmutation.class);
        combatScrolls = new HashSet();
        combatScrolls.add(ScrollOfMirrorImage.class);
        combatScrolls.add(ScrollOfRecharging.class);
        combatScrolls.add(ScrollOfLullaby.class);
        combatScrolls.add(ScrollOfRetribution.class);
        combatScrolls.add(ScrollOfRage.class);
        combatScrolls.add(ScrollOfTeleportation.class);
        combatScrolls.add(ScrollOfTerror.class);
    }

    public static class Recipe
    extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean scroll = false;
            boolean stone = false;
            for (Item i : ingredients) {
                if (i instanceof Runestone) {
                    stone = true;
                    continue;
                }
                if (!ExoticScroll.regToExo.containsKey(i.getClass()) && !ExoticScroll.regToExo.containsValue(i.getClass())) continue;
                scroll = true;
            }
            return scroll && stone;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 1;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                i.quantity(i.quantity() - 1);
            }
            return this.sampleOutput(null);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return new UnstableSpell();
        }
    }
}

