/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

public class RecallInscription
extends ClericSpell {
    public static RecallInscription INSTANCE = new RecallInscription();

    @Override
    public int icon() {
        return 45;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Dungeon.hero.pointsInTalent(Talent.RECALL_INSCRIPTION) == 2 ? 300 : 10) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public void onCast(HolyTome tome, final Hero hero) {
        if (hero.buff(UsedItemTracker.class) == null) {
            return;
        }
        final Item item = Reflection.newInstance(hero.buff(UsedItemTracker.class).item);
        item.setCurrent(hero);
        hero.sprite.operate(hero.pos);
        Enchanting.show(hero, item);
        if (item instanceof Scroll) {
            ((Scroll)item).anonymize();
            ((Scroll)item).talentChance = 0.0f;
            ((Scroll)item).doRead();
        } else if (item instanceof Runestone) {
            ((Runestone)item).anonymize();
            if (item instanceof InventoryStone) {
                ((InventoryStone)item).directActivate();
            } else {
                ShatteredPixelDungeon.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        item.doThrow(hero);
                    }
                });
            }
        }
        this.onSpellCast(tome, hero);
        if (hero.buff(UsedItemTracker.class) != null) {
            hero.buff(UsedItemTracker.class).detach();
        }
    }

    @Override
    public float chargeUse(Hero hero) {
        if (hero.buff(UsedItemTracker.class) != null) {
            Class<? extends Item> item = hero.buff(UsedItemTracker.class).item;
            if (ExoticScroll.class.isAssignableFrom(item)) {
                if (item == ScrollOfMetamorphosis.class || item == ScrollOfEnchantment.class) {
                    return 8.0f;
                }
                return 4.0f;
            }
            if (Scroll.class.isAssignableFrom(item)) {
                if (item == ScrollOfTransmutation.class) {
                    return 6.0f;
                }
                return 3.0f;
            }
            if (Runestone.class.isAssignableFrom(item)) {
                if (item == StoneOfAugmentation.class || item == StoneOfEnchantment.class) {
                    return 4.0f;
                }
                return 2.0f;
            }
        }
        return 0.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.RECALL_INSCRIPTION) && hero.buff(UsedItemTracker.class) != null;
    }

    public static class UsedItemTracker
    extends FlavourBuff {
        public Class<? extends Item> item;
        private static String ITEM = "item";

        public UsedItemTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 78;
        }

        @Override
        public float iconFadePercent() {
            float duration = Dungeon.hero.pointsInTalent(Talent.RECALL_INSCRIPTION) == 2 ? 300.0f : 10.0f;
            return Math.max(0.0f, (duration - this.visualcooldown()) / duration);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.titleCase(Reflection.newInstance(this.item).name()), this.dispTurns());
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ITEM, this.item);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.item = bundle.getClass(ITEM);
        }
    }
}

