/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class SummonElemental
extends Spell {
    public static final String AC_IMBUE = "IMBUE";
    private Class<? extends Elemental> summonClass;
    private static final String SUMMON_CLASS = "summon_class";
    public WndBag.ItemSelector selector;

    public SummonElemental() {
        this.image = ItemSpriteSheet.SUMMON_ELE;
        this.talentChance = 0.16666667f;
        this.summonClass = Elemental.AllyNewBornElemental.class;
        this.selector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(SummonElemental.class, "imbue_prompt", new Object[0]);
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item.isIdentified() && (item instanceof PotionOfLiquidFlame || item instanceof PotionOfFrost || item instanceof ScrollOfRecharging || item instanceof ScrollOfTransmutation);
            }

            @Override
            public void onSelect(Item item) {
                if (item == null) {
                    return;
                }
                item.detach(Dungeon.hero.belongings.backpack);
                if (item instanceof PotionOfLiquidFlame) {
                    Sample.INSTANCE.play("sounds/burning.mp3");
                    curUser.sprite.emitter().burst(FlameParticle.FACTORY, 12);
                    SummonElemental.this.summonClass = Elemental.FireElemental.class;
                    SummonElemental.this.image = ItemSpriteSheet.SUMMON_ELE_FIRE;
                } else if (item instanceof PotionOfFrost) {
                    Sample.INSTANCE.play("sounds/shatter.mp3");
                    curUser.sprite.emitter().burst(MagicMissile.MagicParticle.FACTORY, 12);
                    SummonElemental.this.summonClass = Elemental.FrostElemental.class;
                    SummonElemental.this.image = ItemSpriteSheet.SUMMON_ELE_FROST;
                } else if (item instanceof ScrollOfRecharging) {
                    Sample.INSTANCE.play("sounds/zap.mp3");
                    curUser.sprite.emitter().burst(ShaftParticle.FACTORY, 12);
                    SummonElemental.this.summonClass = Elemental.ShockElemental.class;
                    SummonElemental.this.image = ItemSpriteSheet.SUMMON_ELE_SHOCK;
                } else if (item instanceof ScrollOfTransmutation) {
                    Sample.INSTANCE.play("sounds/read.mp3");
                    curUser.sprite.emitter().burst(RainbowParticle.BURST, 12);
                    SummonElemental.this.summonClass = Elemental.ChaosElemental.class;
                    SummonElemental.this.image = ItemSpriteSheet.SUMMON_ELE_CHAOS;
                }
                curUser.sprite.operate(curUser.pos);
                Item.updateQuickslot();
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_IMBUE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_IMBUE)) {
            GameScene.selectItem(this.selector);
        }
    }

    @Override
    protected void onCast(Hero hero) {
        ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
            int p = hero.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) != null || !Dungeon.level.passable[p]) continue;
            spawnPoints.add(p);
        }
        if (!spawnPoints.isEmpty()) {
            for (Char ch : Actor.chars()) {
                if (!(ch instanceof Elemental) || ch.buff(InvisAlly.class) == null) continue;
                ScrollOfTeleportation.appear(ch, (Integer)Random.element(spawnPoints));
                ((Elemental)ch).state = ((Elemental)ch).HUNTING;
                curUser.spendAndNext(1.0f);
                return;
            }
            Elemental elemental = Reflection.newInstance(this.summonClass);
            GameScene.add(elemental);
            Buff.affect(elemental, InvisAlly.class);
            elemental.setSummonedALly();
            elemental.HP = elemental.HT;
            ScrollOfTeleportation.appear(elemental, (Integer)Random.element(spawnPoints));
            Invisibility.dispel(curUser);
            SummonElemental.curUser.sprite.operate(SummonElemental.curUser.pos);
            curUser.spendAndNext(1.0f);
            this.detach(Dungeon.hero.belongings.backpack);
            Catalog.countUse(this.getClass());
            if (Random.Float() < this.talentChance) {
                Talent.onScrollUsed(curUser, SummonElemental.curUser.pos, this.talentFactor, this.getClass());
            }
        } else {
            GLog.w(Messages.get(SpiritHawk.class, "no_space", new Object[0]), new Object[0]);
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (this.summonClass == Elemental.FireElemental.class) {
            return new ItemSprite.Glowing(0xFFBB33);
        }
        if (this.summonClass == Elemental.FrostElemental.class) {
            return new ItemSprite.Glowing(9364479);
        }
        if (this.summonClass == Elemental.ShockElemental.class) {
            return new ItemSprite.Glowing(0xFFFF85);
        }
        if (this.summonClass == Elemental.ChaosElemental.class) {
            return new ItemSprite.Glowing(0xE3E3E3, 0.5f);
        }
        return super.glowing();
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        desc = (String)desc + "\n\n";
        if (this.summonClass == Elemental.AllyNewBornElemental.class) {
            desc = (String)desc + Messages.get(this, "desc_newborn", new Object[0]);
        }
        if (this.summonClass == Elemental.FireElemental.class) {
            desc = (String)desc + Messages.get(this, "desc_fire", new Object[0]);
        }
        if (this.summonClass == Elemental.FrostElemental.class) {
            desc = (String)desc + Messages.get(this, "desc_frost", new Object[0]);
        }
        if (this.summonClass == Elemental.ShockElemental.class) {
            desc = (String)desc + Messages.get(this, "desc_shock", new Object[0]);
        }
        if (this.summonClass == Elemental.ChaosElemental.class) {
            desc = (String)desc + Messages.get(this, "desc_chaos", new Object[0]);
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SUMMON_CLASS, this.summonClass);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(SUMMON_CLASS)) {
            this.summonClass = bundle.getClass(SUMMON_CLASS);
        }
        if (this.summonClass == Elemental.AllyNewBornElemental.class) {
            this.image = ItemSpriteSheet.SUMMON_ELE;
        }
        if (this.summonClass == Elemental.FireElemental.class) {
            this.image = ItemSpriteSheet.SUMMON_ELE_FIRE;
        }
        if (this.summonClass == Elemental.FrostElemental.class) {
            this.image = ItemSpriteSheet.SUMMON_ELE_FROST;
        }
        if (this.summonClass == Elemental.ShockElemental.class) {
            this.image = ItemSpriteSheet.SUMMON_ELE_SHOCK;
        }
        if (this.summonClass == Elemental.ChaosElemental.class) {
            this.image = ItemSpriteSheet.SUMMON_ELE_CHAOS;
        }
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 6;

        public Recipe() {
            this.inputs = new Class[]{Embers.class};
            this.inQuantity = new int[]{1};
            this.cost = 10;
            this.output = SummonElemental.class;
            this.outQuantity = 6;
        }
    }

    public static class InvisAlly
    extends AllyBuff {
        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.HEARTS);
            } else {
                this.target.sprite.remove(CharSprite.State.HEARTS);
            }
        }
    }
}

