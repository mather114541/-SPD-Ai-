/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDread;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMysticalEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class ExoticScroll
extends Scroll {
    public static final LinkedHashMap<Class<? extends Scroll>, Class<? extends ExoticScroll>> regToExo = new LinkedHashMap();
    public static final LinkedHashMap<Class<? extends ExoticScroll>, Class<? extends Scroll>> exoToReg = new LinkedHashMap();

    @Override
    public boolean isKnown() {
        return this.anonymous || handler != null && handler.isKnown(exoToReg.get(this.getClass()));
    }

    @Override
    public void setKnown() {
        if (!this.isKnown()) {
            handler.know(exoToReg.get(this.getClass()));
            ExoticScroll.updateQuickslot();
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (handler != null && handler.contains(exoToReg.get(this.getClass()))) {
            this.image = handler.image(exoToReg.get(this.getClass())) + 16;
            this.rune = handler.label(exoToReg.get(this.getClass()));
        }
    }

    @Override
    public int value() {
        return (Reflection.newInstance(exoToReg.get(this.getClass())).value() + 30) * this.quantity;
    }

    @Override
    public int energyVal() {
        return (Reflection.newInstance(exoToReg.get(this.getClass())).energyVal() + 6) * this.quantity;
    }

    static {
        regToExo.put(ScrollOfUpgrade.class, ScrollOfEnchantment.class);
        exoToReg.put(ScrollOfEnchantment.class, ScrollOfUpgrade.class);
        regToExo.put(ScrollOfIdentify.class, ScrollOfDivination.class);
        exoToReg.put(ScrollOfDivination.class, ScrollOfIdentify.class);
        regToExo.put(ScrollOfRemoveCurse.class, ScrollOfAntiMagic.class);
        exoToReg.put(ScrollOfAntiMagic.class, ScrollOfRemoveCurse.class);
        regToExo.put(ScrollOfMirrorImage.class, ScrollOfPrismaticImage.class);
        exoToReg.put(ScrollOfPrismaticImage.class, ScrollOfMirrorImage.class);
        regToExo.put(ScrollOfRecharging.class, ScrollOfMysticalEnergy.class);
        exoToReg.put(ScrollOfMysticalEnergy.class, ScrollOfRecharging.class);
        regToExo.put(ScrollOfTeleportation.class, ScrollOfPassage.class);
        exoToReg.put(ScrollOfPassage.class, ScrollOfTeleportation.class);
        regToExo.put(ScrollOfLullaby.class, ScrollOfSirensSong.class);
        exoToReg.put(ScrollOfSirensSong.class, ScrollOfLullaby.class);
        regToExo.put(ScrollOfMagicMapping.class, ScrollOfForesight.class);
        exoToReg.put(ScrollOfForesight.class, ScrollOfMagicMapping.class);
        regToExo.put(ScrollOfRage.class, ScrollOfChallenge.class);
        exoToReg.put(ScrollOfChallenge.class, ScrollOfRage.class);
        regToExo.put(ScrollOfRetribution.class, ScrollOfPsionicBlast.class);
        exoToReg.put(ScrollOfPsionicBlast.class, ScrollOfRetribution.class);
        regToExo.put(ScrollOfTerror.class, ScrollOfDread.class);
        exoToReg.put(ScrollOfDread.class, ScrollOfTerror.class);
        regToExo.put(ScrollOfTransmutation.class, ScrollOfMetamorphosis.class);
        exoToReg.put(ScrollOfMetamorphosis.class, ScrollOfTransmutation.class);
    }

    public static class ScrollToExotic
    extends Recipe {
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.size() == 1 && regToExo.containsKey(ingredients.get(0).getClass());
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 6;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                i.quantity(i.quantity() - 1);
            }
            return Reflection.newInstance(regToExo.get(ingredients.get(0).getClass()));
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return Reflection.newInstance(regToExo.get(ingredients.get(0).getClass()));
        }
    }
}

