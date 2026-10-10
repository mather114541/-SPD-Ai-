/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import java.util.ArrayList;
import java.util.Arrays;

public class BrokenSeal
extends Item {
    public static final String AC_AFFIX = "AFFIX";
    public static final String AC_INFO = "INFO_WINDOW";
    private Armor.Glyph glyph;
    protected static WndBag.ItemSelector armorSelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return Messages.get(BrokenSeal.class, "prompt", new Object[0]);
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Armor;
        }

        @Override
        public void onSelect(Item item) {
            if (item instanceof Armor) {
                BrokenSeal seal = (BrokenSeal)Item.curItem;
                seal.affixToArmor((Armor)item, seal);
            }
        }
    };
    private static final String GLYPH = "glyph";

    public BrokenSeal() {
        this.image = ItemSpriteSheet.SEAL;
        this.levelKnown = true;
        this.cursedKnown = true;
        this.unique = true;
        this.bones = false;
        this.defaultAction = AC_INFO;
    }

    public boolean canTransferGlyph() {
        if (this.glyph == null) {
            return false;
        }
        if (Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE) == 2) {
            return true;
        }
        return Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE) == 1 && (Arrays.asList(Armor.Glyph.common).contains(this.glyph.getClass()) || Arrays.asList(Armor.Glyph.uncommon).contains(this.glyph.getClass()));
    }

    public Armor.Glyph getGlyph() {
        return this.glyph;
    }

    public void setGlyph(Armor.Glyph glyph) {
        this.glyph = glyph;
    }

    public int maxShield(int armTier, int armLvl) {
        return 3 + 2 * armTier + Dungeon.hero.pointsInTalent(Talent.IRON_WILL);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return this.glyph != null ? this.glyph.glowing() : null;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_AFFIX);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_AFFIX)) {
            curItem = this;
            GameScene.selectItem(armorSelector);
        } else if (action.equals(AC_INFO)) {
            GameScene.show(new WndUseItem(null, this));
        }
    }

    public void affixToArmor(final Armor armor, final Item outgoing) {
        if (armor != null) {
            if (!armor.cursedKnown) {
                GLog.w(Messages.get(BrokenSeal.class, "unknown_armor", new Object[0]), new Object[0]);
            } else if (armor.cursed && (this.getGlyph() == null || !this.getGlyph().curse())) {
                GLog.w(Messages.get(BrokenSeal.class, "cursed_armor", new Object[0]), new Object[0]);
            } else if (armor.glyph != null && this.getGlyph() != null && (this.canTransferGlyph() || outgoing instanceof BrokenSeal) && armor.glyph.getClass() != this.getGlyph().getClass()) {
                GameScene.show(new WndOptions(new ItemSprite(ItemSpriteSheet.SEAL), Messages.get(BrokenSeal.class, "choose_title", new Object[0]), Messages.get(BrokenSeal.class, "choose_desc", armor.glyph.name(), this.getGlyph().name()), new String[]{armor.glyph.name(), this.getGlyph().name()}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == -1) {
                            return;
                        }
                        if (outgoing == BrokenSeal.this) {
                            BrokenSeal.this.detach(Dungeon.hero.belongings.backpack);
                        } else if (outgoing instanceof Armor) {
                            ((Armor)outgoing).detachSeal();
                        }
                        if (index == 0) {
                            BrokenSeal.this.setGlyph(null);
                        }
                        GLog.p(Messages.get(BrokenSeal.class, "affix", new Object[0]), new Object[0]);
                        Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                        Sample.INSTANCE.play("sounds/unlock.mp3");
                        armor.affixSeal(BrokenSeal.this);
                    }

                    @Override
                    public void hide() {
                        super.hide();
                        Dungeon.hero.next();
                    }
                });
            } else {
                if (outgoing == this) {
                    this.detach(Dungeon.hero.belongings.backpack);
                } else if (outgoing instanceof Armor) {
                    ((Armor)outgoing).detachSeal();
                }
                GLog.p(Messages.get(BrokenSeal.class, "affix", new Object[0]), new Object[0]);
                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                Sample.INSTANCE.play("sounds/unlock.mp3");
                armor.affixSeal(this);
                Dungeon.hero.next();
            }
        }
    }

    @Override
    public String name() {
        return this.glyph != null ? this.glyph.name(super.name()) : super.name();
    }

    @Override
    public String info() {
        Object info = super.info();
        if (this.glyph != null) {
            info = (String)info + "\n\n" + Messages.get(this, "inscribed", this.glyph.name());
            info = (String)info + " " + this.glyph.desc();
        }
        return info;
    }

    @Override
    public boolean isUpgradable() {
        return this.level() == 0;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(GLYPH, this.glyph);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.glyph = (Armor.Glyph)bundle.get(GLYPH);
    }

    public static class WarriorShield
    extends ShieldBuff {
        private Armor armor;
        private int cooldown;
        private float turnsSinceEnemies;
        private int initialShield;
        private static int COOLDOWN_START = 150;
        public static final String COOLDOWN = "cooldown";
        public static final String TURNS_SINCE_ENEMIES = "turns_since_enemies";
        public static final String INITIAL_SHIELD = "initial_shield";

        public WarriorShield() {
            this.type = Buff.buffType.POSITIVE;
            this.detachesAtZero = false;
            this.shieldUsePriority = 2;
            this.cooldown = 0;
            this.turnsSinceEnemies = 0.0f;
            this.initialShield = 0;
        }

        @Override
        public int icon() {
            if (this.coolingDown() || this.shielding() > 0 || this.cooldown < 0) {
                return 84;
            }
            return 127;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.resetColor();
            if (this.coolingDown() && this.shielding() == 0) {
                icon.brightness(0.3f);
            } else if (this.cooldown < 0) {
                icon.invert();
            }
        }

        @Override
        public float iconFadePercent() {
            if (this.shielding() > 0) {
                return GameMath.gate(0.0f, 1.0f - (float)this.shielding() / (float)this.initialShield, 1.0f);
            }
            if (this.coolingDown()) {
                return GameMath.gate(0.0f, (float)this.cooldown / (float)COOLDOWN_START, 1.0f);
            }
            if (this.cooldown < 0) {
                return GameMath.gate(0.0f, (float)(COOLDOWN_START + this.cooldown) / (float)COOLDOWN_START, 1.0f);
            }
            return 0.0f;
        }

        @Override
        public String iconTextDisplay() {
            if (this.shielding() > 0) {
                return Integer.toString(this.shielding());
            }
            if (this.coolingDown() || this.cooldown < 0) {
                return Integer.toString(this.cooldown);
            }
            return "";
        }

        @Override
        public String desc() {
            if (this.shielding() > 0) {
                return Messages.get(this, "desc_active", this.shielding(), this.cooldown);
            }
            if (this.cooldown < 0) {
                return Messages.get(this, "desc_negative_cooldown", this.cooldown);
            }
            return Messages.get(this, "desc_cooldown", this.cooldown);
        }

        @Override
        public synchronized boolean act() {
            if (this.cooldown > 0 && Regeneration.regenOn()) {
                --this.cooldown;
            }
            if (this.shielding() > 0) {
                if (Dungeon.hero.visibleEnemies() == 0 && Dungeon.hero.buff(Combo.class) == null) {
                    this.turnsSinceEnemies += HoldFast.buffDecayFactor(this.target);
                    if (this.turnsSinceEnemies >= 5.0f) {
                        if (this.cooldown > 0) {
                            float percentLeft = (float)this.shielding() / (float)this.initialShield;
                            this.cooldown = Math.max(0, (int)((float)this.cooldown - (float)COOLDOWN_START * (percentLeft / 2.0f)));
                        }
                        this.decShield(this.shielding());
                    }
                } else {
                    this.turnsSinceEnemies = 0.0f;
                }
            }
            if (this.shielding() <= 0 && this.maxShield() <= 0 && this.cooldown == 0) {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        public synchronized void activate() {
            this.incShield(this.maxShield());
            this.cooldown = Math.max(0, this.cooldown + COOLDOWN_START);
            this.turnsSinceEnemies = 0.0f;
            this.initialShield = this.maxShield();
        }

        public boolean coolingDown() {
            return this.cooldown > 0;
        }

        public void reduceCooldown(float percentage) {
            this.cooldown -= Math.round((float)COOLDOWN_START * percentage);
            this.cooldown = Math.max(this.cooldown, -COOLDOWN_START);
        }

        public synchronized void setArmor(Armor arm) {
            this.armor = arm;
        }

        public synchronized int maxShield() {
            if (((Hero)this.target).heroClass != HeroClass.WARRIOR && ((Hero)this.target).hasTalent(Talent.IRON_WILL)) {
                return ((Hero)this.target).pointsInTalent(Talent.IRON_WILL);
            }
            if (this.armor != null && this.armor.isEquipped((Hero)this.target) && this.armor.checkSeal() != null) {
                return this.armor.checkSeal().maxShield(this.armor.tier, this.armor.level());
            }
            return 0;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(COOLDOWN, this.cooldown);
            bundle.put(TURNS_SINCE_ENEMIES, this.turnsSinceEnemies);
            bundle.put(INITIAL_SHIELD, this.initialShield);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains(COOLDOWN)) {
                this.cooldown = bundle.getInt(COOLDOWN);
                this.turnsSinceEnemies = bundle.getFloat(TURNS_SINCE_ENEMIES);
                this.initialShield = bundle.getInt(INITIAL_SHIELD);
            } else if (this.shielding() > 0) {
                this.turnsSinceEnemies = -100.0f;
                this.initialShield = this.shielding();
            }
        }
    }
}

