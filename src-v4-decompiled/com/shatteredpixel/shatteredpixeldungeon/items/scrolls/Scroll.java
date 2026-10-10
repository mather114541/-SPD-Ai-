/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public abstract class Scroll
extends Item {
    public static final String AC_READ = "READ";
    protected static final float TIME_TO_READ = 1.0f;
    private static final LinkedHashMap<String, Integer> runes = new LinkedHashMap<String, Integer>(){
        {
            this.put("KAUNAN", ItemSpriteSheet.SCROLL_KAUNAN);
            this.put("SOWILO", ItemSpriteSheet.SCROLL_SOWILO);
            this.put("LAGUZ", ItemSpriteSheet.SCROLL_LAGUZ);
            this.put("YNGVI", ItemSpriteSheet.SCROLL_YNGVI);
            this.put("GYFU", ItemSpriteSheet.SCROLL_GYFU);
            this.put("RAIDO", ItemSpriteSheet.SCROLL_RAIDO);
            this.put("ISAZ", ItemSpriteSheet.SCROLL_ISAZ);
            this.put("MANNAZ", ItemSpriteSheet.SCROLL_MANNAZ);
            this.put("NAUDIZ", ItemSpriteSheet.SCROLL_NAUDIZ);
            this.put("BERKANAN", ItemSpriteSheet.SCROLL_BERKANAN);
            this.put("ODAL", ItemSpriteSheet.SCROLL_ODAL);
            this.put("TIWAZ", ItemSpriteSheet.SCROLL_TIWAZ);
        }
    };
    protected static ItemStatusHandler<Scroll> handler;
    protected String rune;
    public float talentFactor = 1.0f;
    public float talentChance = 1.0f;
    protected boolean anonymous;

    public static void initLabels() {
        handler = new ItemStatusHandler(Generator.Category.SCROLL.classes, runes);
    }

    public static void clearLabels() {
        handler = null;
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        ArrayList<Class<? extends Item>> classes = new ArrayList<Class<? extends Item>>();
        for (Item i : items) {
            if (i instanceof ExoticScroll) {
                if (classes.contains(ExoticScroll.exoToReg.get(i.getClass()))) continue;
                classes.add(ExoticScroll.exoToReg.get(i.getClass()));
                continue;
            }
            if (!(i instanceof Scroll) || classes.contains(i.getClass())) continue;
            classes.add(i.getClass());
        }
        handler.saveClassesSelectively(bundle, classes);
    }

    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler(Generator.Category.SCROLL.classes, runes, bundle);
    }

    public Scroll() {
        this.stackable = true;
        this.defaultAction = AC_READ;
        this.anonymous = false;
        this.reset();
    }

    public void anonymize() {
        if (!this.isKnown()) {
            this.image = ItemSpriteSheet.SCROLL_HOLDER;
        }
        this.anonymous = true;
    }

    @Override
    public void reset() {
        super.reset();
        if (handler != null && handler.contains(this)) {
            this.image = handler.image(this);
            this.rune = handler.label(this);
        } else {
            this.image = ItemSpriteSheet.SCROLL_KAUNAN;
            this.rune = "KAUNAN";
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_READ);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_READ)) {
            if (hero.buff(MagicImmune.class) != null) {
                GLog.w(Messages.get(this, "no_magic", new Object[0]), new Object[0]);
            } else if (hero.buff(Blindness.class) != null) {
                GLog.w(Messages.get(this, "blinded", new Object[0]), new Object[0]);
            } else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null && hero.buff(UnstableSpellbook.bookRecharge.class).isCursed() && !(this instanceof ScrollOfRemoveCurse) && !(this instanceof ScrollOfAntiMagic)) {
                GLog.n(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else {
                this.doRead();
            }
        }
    }

    public abstract void doRead();

    public void readAnimation() {
        Invisibility.dispel();
        curUser.spend(1.0f);
        curUser.busy();
        ((HeroSprite)Scroll.curUser.sprite).read();
        if (!this.anonymous) {
            Catalog.countUse(this.getClass());
        }
        if (Random.Float() < this.talentChance) {
            Talent.onScrollUsed(curUser, Scroll.curUser.pos, this.talentFactor, this.getClass());
        }
    }

    public boolean isKnown() {
        return this.anonymous || handler != null && handler.isKnown(this);
    }

    public void setKnown() {
        if (!this.anonymous) {
            if (!this.isKnown()) {
                handler.know(this);
                Scroll.updateQuickslot();
            }
            if (Dungeon.hero.isAlive()) {
                Catalog.setSeen(this.getClass());
                Statistics.itemTypesDiscovered.add(this.getClass());
            }
        }
    }

    @Override
    public Item identify(boolean byHero) {
        super.identify(byHero);
        if (!this.isKnown()) {
            this.setKnown();
        }
        return this;
    }

    @Override
    public String name() {
        return this.isKnown() ? super.name() : Messages.get(this, this.rune, new Object[0]);
    }

    @Override
    public String info() {
        return this.anonymous && (handler == null || !handler.isKnown(this)) ? this.desc() : super.info();
    }

    @Override
    public String desc() {
        return this.isKnown() ? super.desc() : Messages.get(this, "unknown_desc", new Object[0]);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return this.isKnown();
    }

    public static HashSet<Class<? extends Scroll>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Scroll>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler != null && handler.known().size() == Generator.Category.SCROLL.classes.length;
    }

    @Override
    public int value() {
        return 30 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 6 * this.quantity;
    }

    public static class ScrollToStone
    extends Recipe {
        private static HashMap<Class<? extends Scroll>, Class<? extends Runestone>> stones = new HashMap();

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.size() == 1 && ingredients.get(0) instanceof Scroll && stones.containsKey(ingredients.get(0).getClass());
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 0;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            if (!this.testIngredients(ingredients)) {
                return null;
            }
            Scroll s = (Scroll)ingredients.get(0);
            s.quantity(s.quantity() - 1);
            if (ShatteredPixelDungeon.scene() instanceof AlchemyScene) {
                if (!s.isIdentified()) {
                    ((AlchemyScene)ShatteredPixelDungeon.scene()).showIdentify(s);
                }
            } else {
                s.identify();
            }
            return Reflection.newInstance(stones.get(s.getClass())).quantity(2);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            if (!this.testIngredients(ingredients)) {
                return null;
            }
            Scroll s = (Scroll)ingredients.get(0);
            if (!s.isKnown()) {
                return new Runestone.PlaceHolder().quantity(2);
            }
            return Reflection.newInstance(stones.get(s.getClass())).quantity(2);
        }

        static {
            stones.put(ScrollOfIdentify.class, StoneOfIntuition.class);
            stones.put(ScrollOfLullaby.class, StoneOfDeepSleep.class);
            stones.put(ScrollOfMagicMapping.class, StoneOfClairvoyance.class);
            stones.put(ScrollOfMirrorImage.class, StoneOfFlock.class);
            stones.put(ScrollOfRetribution.class, StoneOfBlast.class);
            stones.put(ScrollOfRage.class, StoneOfAggression.class);
            stones.put(ScrollOfRecharging.class, StoneOfShock.class);
            stones.put(ScrollOfRemoveCurse.class, StoneOfDetectMagic.class);
            stones.put(ScrollOfTeleportation.class, StoneOfBlink.class);
            stones.put(ScrollOfTerror.class, StoneOfFear.class);
            stones.put(ScrollOfTransmutation.class, StoneOfAugmentation.class);
            stones.put(ScrollOfUpgrade.class, StoneOfEnchantment.class);
        }
    }

    public static class PlaceHolder
    extends Scroll {
        public PlaceHolder() {
            this.image = ItemSpriteSheet.SCROLL_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return ExoticScroll.regToExo.containsKey(item.getClass()) || ExoticScroll.regToExo.containsValue(item.getClass());
        }

        @Override
        public void doRead() {
        }

        @Override
        public String info() {
            return "";
        }
    }
}

