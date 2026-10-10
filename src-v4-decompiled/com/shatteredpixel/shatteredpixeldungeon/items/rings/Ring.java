/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class Ring
extends KindofMisc {
    protected Buff buff;
    protected Class<? extends RingBuff> buffClass;
    private static final LinkedHashMap<String, Integer> gems = new LinkedHashMap<String, Integer>(){
        {
            this.put("garnet", ItemSpriteSheet.RING_GARNET);
            this.put("ruby", ItemSpriteSheet.RING_RUBY);
            this.put("topaz", ItemSpriteSheet.RING_TOPAZ);
            this.put("emerald", ItemSpriteSheet.RING_EMERALD);
            this.put("onyx", ItemSpriteSheet.RING_ONYX);
            this.put("opal", ItemSpriteSheet.RING_OPAL);
            this.put("tourmaline", ItemSpriteSheet.RING_TOURMALINE);
            this.put("sapphire", ItemSpriteSheet.RING_SAPPHIRE);
            this.put("amethyst", ItemSpriteSheet.RING_AMETHYST);
            this.put("quartz", ItemSpriteSheet.RING_QUARTZ);
            this.put("agate", ItemSpriteSheet.RING_AGATE);
            this.put("diamond", ItemSpriteSheet.RING_DIAMOND);
        }
    };
    private static ItemStatusHandler<Ring> handler;
    private String gem;
    private float levelsToID = 1.0f;
    protected boolean anonymous = false;
    private static final String LEVELS_TO_ID = "levels_to_ID";

    public static void initGems() {
        handler = new ItemStatusHandler(Generator.Category.RING.classes, gems);
    }

    public static void clearGems() {
        handler = null;
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        handler.saveSelectively(bundle, items);
    }

    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler(Generator.Category.RING.classes, gems, bundle);
    }

    public Ring() {
        this.reset();
    }

    public void anonymize() {
        if (!this.isKnown()) {
            this.image = ItemSpriteSheet.RING_HOLDER;
        }
        this.anonymous = true;
    }

    @Override
    public void reset() {
        super.reset();
        this.levelsToID = 1.0f;
        if (handler != null && handler.contains(this)) {
            this.image = handler.image(this);
            this.gem = handler.label(this);
        } else {
            this.image = ItemSpriteSheet.RING_GARNET;
            this.gem = "garnet";
        }
    }

    @Override
    public void activate(Char ch) {
        if (this.buff != null) {
            this.buff.detach();
            this.buff = null;
        }
        this.buff = this.buff();
        this.buff.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (this.buff != null) {
                this.buff.detach();
                this.buff = null;
            }
            return true;
        }
        return false;
    }

    public boolean isKnown() {
        return this.anonymous || handler != null && handler.isKnown(this);
    }

    public void setKnown() {
        if (!this.anonymous) {
            if (!this.isKnown()) {
                handler.know(this);
            }
            if (Dungeon.hero.isAlive()) {
                Catalog.setSeen(this.getClass());
                Statistics.itemTypesDiscovered.add(this.getClass());
            }
        }
    }

    @Override
    public String name() {
        return this.isKnown() ? super.name() : Messages.get(Ring.class, this.gem, new Object[0]);
    }

    @Override
    public String desc() {
        return this.isKnown() ? super.desc() : Messages.get(this, "unknown_desc", new Object[0]);
    }

    @Override
    public String info() {
        Object desc = this.anonymous && (handler == null || !handler.isKnown(this)) ? this.desc() : super.info();
        if (this.cursed && this.isEquipped(Dungeon.hero)) {
            desc = (String)desc + "\n\n" + Messages.get(Ring.class, "cursed_worn", new Object[0]);
        } else if (this.cursed && this.cursedKnown) {
            desc = (String)desc + "\n\n" + Messages.get(Ring.class, "curse_known", new Object[0]);
        } else if (!this.isIdentified() && this.cursedKnown) {
            desc = (String)desc + "\n\n" + Messages.get(Ring.class, "not_cursed", new Object[0]);
        }
        if (this.isKnown()) {
            desc = (String)desc + "\n\n" + this.statsInfo();
        }
        return desc;
    }

    protected String statsInfo() {
        return "";
    }

    public String upgradeStat1(int level) {
        return null;
    }

    public String upgradeStat2(int level) {
        return null;
    }

    public String upgradeStat3(int level) {
        return null;
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        if (Random.Int(3) == 0) {
            this.cursed = false;
        }
        return this;
    }

    @Override
    public boolean isIdentified() {
        return super.isIdentified() && this.isKnown();
    }

    @Override
    public Item identify(boolean byHero) {
        this.setKnown();
        this.levelsToID = 0.0f;
        return super.identify(byHero);
    }

    public void setIDReady() {
        this.levelsToID = -1.0f;
    }

    public boolean readyToIdentify() {
        return !this.isIdentified() && this.levelsToID <= 0.0f;
    }

    @Override
    public Item random() {
        int n = 0;
        if (Random.Int(3) == 0) {
            ++n;
            if (Random.Int(5) == 0) {
                ++n;
            }
        }
        this.level(n);
        if (Random.Float() < 0.3f) {
            this.cursed = true;
        }
        return this;
    }

    public static HashSet<Class<? extends Ring>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Ring>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler != null && handler.known().size() == Generator.Category.RING.classes.length;
    }

    @Override
    public int value() {
        int price = 75;
        if (this.cursed && this.cursedKnown) {
            price /= 2;
        }
        if (this.levelKnown) {
            if (this.level() > 0) {
                price *= this.level() + 1;
            } else if (this.level() < 0) {
                price /= 1 - this.level();
            }
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    protected RingBuff buff() {
        return null;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVELS_TO_ID, this.levelsToID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.levelsToID = bundle.getFloat(LEVELS_TO_ID);
    }

    @Override
    public void onHeroGainExp(float levelPercent, Hero hero) {
        if (this.isIdentified() || !this.isEquipped(hero)) {
            return;
        }
        this.levelsToID -= (levelPercent *= Talent.itemIDSpeedFactor(hero, this));
        if (this.levelsToID <= 0.0f) {
            if (ShardOfOblivion.passiveIDDisabled()) {
                if (this.levelsToID > -1.0f) {
                    GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready", new Object[0]), this.name());
                }
                this.setIDReady();
            } else {
                this.identify();
                GLog.p(Messages.get(Ring.class, "identify", new Object[0]), new Object[0]);
                Badges.validateItemLevelAquired(this);
            }
        }
    }

    @Override
    public int buffedLvl() {
        int lvl = super.buffedLvl();
        if (Dungeon.hero.buff(EnhancedRings.class) != null) {
            ++lvl;
        }
        return lvl;
    }

    public static int getBonus(Char target, Class<? extends RingBuff> type) {
        if (target.buff(MagicImmune.class) != null) {
            return 0;
        }
        int bonus = 0;
        for (RingBuff ringBuff : target.buffs(type)) {
            bonus += ringBuff.level();
        }
        SpiritForm.SpiritFormBuff spiritForm = target.buff(SpiritForm.SpiritFormBuff.class);
        if (bonus == 0 && spiritForm != null && spiritForm.ring() != null && spiritForm.ring().buffClass == type) {
            bonus += spiritForm.ring().soloBonus();
        }
        return bonus;
    }

    public static int getBuffedBonus(Char target, Class<? extends RingBuff> type) {
        if (target.buff(MagicImmune.class) != null) {
            return 0;
        }
        int bonus = 0;
        for (RingBuff ringBuff : target.buffs(type)) {
            bonus += ringBuff.buffedLvl();
        }
        if (bonus == 0 && target.buff(SpiritForm.SpiritFormBuff.class) != null && target.buff(SpiritForm.SpiritFormBuff.class).ring() != null && target.buff(SpiritForm.SpiritFormBuff.class).ring().buffClass == type) {
            bonus += target.buff(SpiritForm.SpiritFormBuff.class).ring().soloBuffedBonus();
        }
        return bonus;
    }

    public int soloBonus() {
        if (this.cursed) {
            return Math.min(0, this.level() - 2);
        }
        return this.level() + 1;
    }

    public int soloBuffedBonus() {
        if (this.cursed) {
            return Math.min(0, this.buffedLvl() - 2);
        }
        return this.buffedLvl() + 1;
    }

    public int combinedBonus(Hero hero) {
        int bonus = 0;
        if (hero.belongings.ring() != null && hero.belongings.ring().getClass() == this.getClass()) {
            bonus += hero.belongings.ring().soloBonus();
        }
        if (hero.belongings.misc() != null && hero.belongings.misc().getClass() == this.getClass()) {
            bonus += ((Ring)hero.belongings.misc()).soloBonus();
        }
        return bonus;
    }

    public int combinedBuffedBonus(Hero hero) {
        int bonus = 0;
        if (hero.belongings.ring() != null && hero.belongings.ring().getClass() == this.getClass()) {
            bonus += hero.belongings.ring().soloBuffedBonus();
        }
        if (hero.belongings.misc() != null && hero.belongings.misc().getClass() == this.getClass()) {
            bonus += ((Ring)hero.belongings.misc()).soloBuffedBonus();
        }
        return bonus;
    }

    public class RingBuff
    extends Buff {
        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (target instanceof Hero && Dungeon.hero == null && this.cooldown() == 0.0f && target.cooldown() > 0.0f) {
                    this.spend(1.0f);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            this.spend(1.0f);
            return true;
        }

        public int level() {
            return Ring.this.soloBonus();
        }

        public int buffedLvl() {
            return Ring.this.soloBuffedBonus();
        }
    }
}

