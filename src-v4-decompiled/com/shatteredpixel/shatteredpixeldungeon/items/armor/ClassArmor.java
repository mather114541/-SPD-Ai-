/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClericArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.DuelistArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RogueArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseAbility;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public abstract class ClassArmor
extends Armor {
    private static final String AC_ABILITY = "ABILITY";
    private static final String AC_TRANSFER = "TRANSFER";
    private Charger charger;
    public float charge;
    private static final String ARMOR_TIER = "armortier";
    private static final String CHARGE = "charge";

    public ClassArmor() {
        super(5);
        this.levelKnown = true;
        this.cursedKnown = true;
        this.defaultAction = AC_ABILITY;
        this.bones = false;
        this.charge = 0.0f;
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        this.charger = new Charger();
        this.charger.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (this.charger != null) {
                this.charger.detach();
                this.charger = null;
            }
            return true;
        }
        return false;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return user.armorAbility.targetedPos(user, dst);
    }

    public static ClassArmor upgrade(Hero owner, Armor armor) {
        ClassArmor classArmor = null;
        switch (owner.heroClass) {
            case WARRIOR: {
                classArmor = new WarriorArmor();
                break;
            }
            case ROGUE: {
                classArmor = new RogueArmor();
                break;
            }
            case MAGE: {
                classArmor = new MageArmor();
                break;
            }
            case HUNTRESS: {
                classArmor = new HuntressArmor();
                break;
            }
            case DUELIST: {
                classArmor = new DuelistArmor();
                break;
            }
            case CLERIC: {
                classArmor = new ClericArmor();
            }
        }
        classArmor.level(armor.trueLevel());
        classArmor.tier = armor.tier;
        classArmor.augment = armor.augment;
        classArmor.inscribe(armor.glyph);
        if (armor.seal != null) {
            classArmor.seal = armor.seal;
        }
        classArmor.glyphHardened = armor.glyphHardened;
        classArmor.cursed = armor.cursed;
        classArmor.curseInfusionBonus = armor.curseInfusionBonus;
        classArmor.masteryPotionBonus = armor.masteryPotionBonus;
        if (armor.levelKnown && armor.cursedKnown) {
            classArmor.identify();
        } else {
            classArmor.levelKnown = armor.levelKnown;
            classArmor.cursedKnown = true;
        }
        classArmor.charge = 50.0f;
        return classArmor;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ARMOR_TIER, this.tier);
        bundle.put(CHARGE, this.charge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.tier = bundle.getInt(ARMOR_TIER);
        this.charge = bundle.getFloat(CHARGE);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero)) {
            actions.add(AC_ABILITY);
        }
        actions.add(AC_TRANSFER);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (hero.armorAbility != null && action.equals(AC_ABILITY)) {
            return Messages.upperCase(hero.armorAbility.name());
        }
        return super.actionName(action, hero);
    }

    @Override
    public String status() {
        return Messages.format("%.0f%%", Math.floor(this.charge));
    }

    @Override
    public void execute(final Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ABILITY)) {
            if (hero.armorAbility == null) {
                GameScene.show(new WndChooseAbility(null, this, hero));
            } else if (!this.isEquipped(hero)) {
                this.usesTargeting = false;
                GLog.w(Messages.get(this, "not_equipped", new Object[0]), new Object[0]);
            } else if (this.charge < hero.armorAbility.chargeUse(hero)) {
                this.usesTargeting = false;
                GLog.w(Messages.get(this, "low_charge", new Object[0]), new Object[0]);
            } else {
                this.usesTargeting = hero.armorAbility.useTargeting();
                hero.armorAbility.use(this, hero);
            }
        } else if (action.equals(AC_TRANSFER)) {
            GameScene.show(new WndOptions(new ItemSprite(ItemSpriteSheet.CROWN), Messages.get(ClassArmor.class, "transfer_title", new Object[0]), Messages.get(ClassArmor.class, "transfer_desc", new Object[0]), new String[]{Messages.get(ClassArmor.class, "transfer_prompt", new Object[0]), Messages.get(ClassArmor.class, "transfer_cancel", new Object[0])}){

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        GameScene.selectItem(new WndBag.ItemSelector(){

                            @Override
                            public String textPrompt() {
                                return Messages.get(ClassArmor.class, "transfer_prompt", new Object[0]);
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof Armor;
                            }

                            @Override
                            public void onSelect(Item item) {
                                if (item == null || item == ClassArmor.this) {
                                    return;
                                }
                                Armor armor = (Armor)item;
                                armor.detach(hero.belongings.backpack);
                                if (hero.belongings.armor == armor) {
                                    hero.belongings.armor = null;
                                    if (hero.sprite instanceof HeroSprite) {
                                        ((HeroSprite)hero.sprite).updateArmor();
                                    }
                                }
                                ClassArmor.this.level(armor.trueLevel());
                                ClassArmor.this.tier = armor.tier;
                                ClassArmor.this.augment = armor.augment;
                                ClassArmor.this.cursed = armor.cursed;
                                ClassArmor.this.curseInfusionBonus = armor.curseInfusionBonus;
                                ClassArmor.this.masteryPotionBonus = armor.masteryPotionBonus;
                                if (armor.checkSeal() != null) {
                                    ClassArmor.this.inscribe(armor.glyph);
                                    ClassArmor.this.seal = armor.checkSeal();
                                } else if (ClassArmor.this.checkSeal() != null) {
                                    if (ClassArmor.this.seal.level() > 0) {
                                        int newLevel = ClassArmor.this.trueLevel() + 1;
                                        ClassArmor.this.level(newLevel);
                                        Badges.validateItemLevelAquired(ClassArmor.this);
                                    }
                                    if (armor.glyph != null || !ClassArmor.this.seal.canTransferGlyph()) {
                                        ClassArmor.this.inscribe(armor.glyph);
                                        ClassArmor.this.seal.setGlyph(null);
                                    }
                                } else {
                                    ClassArmor.this.inscribe(armor.glyph);
                                }
                                if (armor.levelKnown && armor.cursedKnown) {
                                    ClassArmor.this.identify();
                                } else {
                                    ClassArmor.this.levelKnown = armor.levelKnown;
                                    ClassArmor.this.cursedKnown = true;
                                }
                                GLog.p(Messages.get(ClassArmor.class, "transfer_complete", new Object[0]), new Object[0]);
                                hero.sprite.operate(hero.pos);
                                hero.sprite.emitter().burst(Speck.factory(104), 12);
                                Sample.INSTANCE.play("sounds/evoke.mp3");
                                hero.spend(1.0f);
                                hero.busy();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (Dungeon.hero != null && Dungeon.hero.belongings.contains(this)) {
            ArmorAbility ability = Dungeon.hero.armorAbility;
            if (ability != null) {
                desc = (String)desc + "\n\n" + ability.shortDesc();
                float chargeUse = ability.chargeUse(Dungeon.hero);
                if (!(ability instanceof Trinity)) {
                    desc = (String)desc + " " + Messages.get(this, "charge_use", Messages.decimalFormat("#.##", chargeUse));
                }
            } else {
                desc = (String)desc + "\n\n_" + Messages.get(this, "no_ability", new Object[0]) + "_";
            }
        }
        return desc;
    }

    @Override
    public int value() {
        return 0;
    }

    public class Charger
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
            if (Regeneration.regenOn()) {
                float chargeGain = 0.2f;
                ClassArmor.this.charge += (chargeGain *= RingOfEnergy.armorChargeMultiplier(this.target));
                Item.updateQuickslot();
                if (ClassArmor.this.charge > 100.0f) {
                    ClassArmor.this.charge = 100.0f;
                }
            }
            this.spend(1.0f);
            return true;
        }
    }
}

