/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class HornOfPlenty
extends Artifact {
    private int storedFoodEnergy;
    public static final String AC_SNACK = "SNACK";
    public static final String AC_EAT = "EAT";
    public static final String AC_STORE = "STORE";
    private static final String STORED = "stored";
    protected static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return Messages.get(HornOfPlenty.class, "prompt", new Object[0]);
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Food;
        }

        @Override
        public void onSelect(Item item) {
            if (item != null && item instanceof Food) {
                if (item instanceof Blandfruit && ((Blandfruit)item).potionAttrib == null) {
                    GLog.w(Messages.get(HornOfPlenty.class, "reject", new Object[0]), new Object[0]);
                } else {
                    Hero hero = Dungeon.hero;
                    hero.sprite.operate(hero.pos);
                    hero.busy();
                    hero.spend(3.0f);
                    ((HornOfPlenty)curItem).gainFoodValue((Food)item);
                    item.detach(hero.belongings.backpack);
                }
            }
        }
    };

    public HornOfPlenty() {
        this.image = ItemSpriteSheet.ARTIFACT_HORN1;
        this.levelCap = 10;
        this.charge = 0;
        this.partialCharge = 0.0f;
        this.chargeCap = 5 + this.level() / 2;
        this.defaultAction = AC_SNACK;
        this.storedFoodEnergy = 0;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.buff(MagicImmune.class) != null) {
            return actions;
        }
        if (this.isEquipped(hero) && this.charge > 0) {
            actions.add(AC_SNACK);
            actions.add(AC_EAT);
        }
        if (this.isEquipped(hero) && this.level() < this.levelCap && !this.cursed) {
            actions.add(AC_STORE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_EAT) || action.equals(AC_SNACK)) {
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.charge == 0) {
                GLog.i(Messages.get(this, "no_food", new Object[0]), new Object[0]);
            } else {
                Hunger hunger;
                int chargesToUse;
                int satietyPerCharge = 90;
                if (Dungeon.isChallenged(1)) {
                    satietyPerCharge /= 3;
                }
                if ((chargesToUse = Math.max(1, (hunger = Buff.affect(Dungeon.hero, Hunger.class)).hunger() / satietyPerCharge)) > this.charge) {
                    chargesToUse = this.charge;
                }
                if (action.equals(AC_SNACK)) {
                    chargesToUse = 1;
                }
                this.doEatEffect(hero, chargesToUse);
            }
        } else if (action.equals(AC_STORE)) {
            GameScene.selectItem(itemSelector);
        }
    }

    public void doEatEffect(Hero hero, int chargesToUse) {
        int satietyPerCharge = 90;
        if (Dungeon.isChallenged(1)) {
            satietyPerCharge /= 3;
        }
        Buff.affect(hero, Hunger.class).satisfy(satietyPerCharge * chargesToUse);
        ++Statistics.foodEaten;
        this.charge -= chargesToUse;
        Talent.onArtifactUsed(hero);
        hero.sprite.operate(hero.pos);
        hero.busy();
        SpellSprite.show(hero, 0);
        Sample.INSTANCE.play("sounds/eat.mp3");
        GLog.i(Messages.get(this, "eat", new Object[0]), new Object[0]);
        if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH) || Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL) || Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL) || Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL) || Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL) || Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)) {
            hero.spend(1.0f);
        } else {
            hero.spend(3.0f);
        }
        Talent.onFoodEaten(hero, satietyPerCharge * chargesToUse, this);
        Badges.validateFoodEaten();
        this.image = this.charge >= 8 ? ItemSpriteSheet.ARTIFACT_HORN4 : (this.charge >= 5 ? ItemSpriteSheet.ARTIFACT_HORN3 : (this.charge >= 2 ? ItemSpriteSheet.ARTIFACT_HORN2 : ItemSpriteSheet.ARTIFACT_HORN1));
        HornOfPlenty.updateQuickslot();
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new hornRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.charge < this.chargeCap && !this.cursed && target.buff(MagicImmune.class) == null) {
            this.partialCharge += 0.25f * amount;
            while (this.partialCharge >= 1.0f) {
                this.partialCharge -= 1.0f;
                ++this.charge;
                if (this.charge == this.chargeCap) {
                    GLog.p(Messages.get(HornOfPlenty.class, "full", new Object[0]), new Object[0]);
                    this.partialCharge = 0.0f;
                }
                this.image = this.charge >= 8 ? ItemSpriteSheet.ARTIFACT_HORN4 : (this.charge >= 5 ? ItemSpriteSheet.ARTIFACT_HORN3 : (this.charge >= 2 ? ItemSpriteSheet.ARTIFACT_HORN2 : ItemSpriteSheet.ARTIFACT_HORN1));
                HornOfPlenty.updateQuickslot();
            }
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            if (!this.cursed) {
                if (this.level() < this.levelCap) {
                    desc = (String)desc + "\n\n" + Messages.get(this, "desc_hint", new Object[0]);
                }
            } else {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]);
            }
        }
        return desc;
    }

    @Override
    public Item level(int value) {
        super.level(value);
        this.chargeCap = 5 + this.level() / 2;
        return this;
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        this.chargeCap = 5 + this.level() / 2;
        return this;
    }

    public void gainFoodValue(Food food) {
        if (this.level() >= 10) {
            return;
        }
        this.storedFoodEnergy = (int)((float)this.storedFoodEnergy + food.energy);
        if (food instanceof Pasty || food instanceof PhantomMeat) {
            this.storedFoodEnergy = (int)((float)this.storedFoodEnergy + 150.0f);
        } else if (food instanceof MeatPie) {
            this.storedFoodEnergy = (int)((float)this.storedFoodEnergy + 300.0f);
        }
        if ((float)this.storedFoodEnergy >= 300.0f) {
            int upgrades = this.storedFoodEnergy / 300;
            upgrades = Math.min(upgrades, 10 - this.level());
            this.upgrade(upgrades);
            Catalog.countUse(HornOfPlenty.class);
            this.storedFoodEnergy = (int)((float)this.storedFoodEnergy - (float)upgrades * 300.0f);
            if (this.level() == 10) {
                this.storedFoodEnergy = 0;
                GLog.p(Messages.get(this, "maxlevel", new Object[0]), new Object[0]);
            } else {
                GLog.p(Messages.get(this, "levelup", new Object[0]), new Object[0]);
            }
        } else {
            GLog.i(Messages.get(this, "feed", new Object[0]), new Object[0]);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STORED, this.storedFoodEnergy);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.storedFoodEnergy = bundle.getInt(STORED);
        if (this.charge >= 8) {
            this.image = ItemSpriteSheet.ARTIFACT_HORN4;
        } else if (this.charge >= 5) {
            this.image = ItemSpriteSheet.ARTIFACT_HORN3;
        } else if (this.charge >= 2) {
            this.image = ItemSpriteSheet.ARTIFACT_HORN2;
        }
    }

    public class hornRecharge
    extends Artifact.ArtifactBuff {
        public void gainCharge(float levelPortion) {
            if (HornOfPlenty.this.cursed || this.target.buff(MagicImmune.class) != null) {
                return;
            }
            if (HornOfPlenty.this.charge < HornOfPlenty.this.chargeCap) {
                float chargeGain = 450.0f * levelPortion * (0.25f + 0.125f * (float)HornOfPlenty.this.level());
                chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target);
                HornOfPlenty.this.partialCharge += (chargeGain /= 90.0f);
                while (HornOfPlenty.this.partialCharge >= 1.0f) {
                    ++HornOfPlenty.this.charge;
                    HornOfPlenty.this.partialCharge -= 1.0f;
                    HornOfPlenty.this.image = HornOfPlenty.this.charge >= 8 ? ItemSpriteSheet.ARTIFACT_HORN4 : (HornOfPlenty.this.charge >= 5 ? ItemSpriteSheet.ARTIFACT_HORN3 : (HornOfPlenty.this.charge >= 2 ? ItemSpriteSheet.ARTIFACT_HORN2 : ItemSpriteSheet.ARTIFACT_HORN1));
                    Item.updateQuickslot();
                    if (HornOfPlenty.this.charge != HornOfPlenty.this.chargeCap) continue;
                    GLog.p(Messages.get(HornOfPlenty.class, "full", new Object[0]), new Object[0]);
                    HornOfPlenty.this.partialCharge = 0.0f;
                }
            } else {
                HornOfPlenty.this.partialCharge = 0.0f;
            }
        }
    }
}

