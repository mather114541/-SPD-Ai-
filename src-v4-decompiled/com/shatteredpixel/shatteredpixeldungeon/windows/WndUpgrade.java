/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;

public class WndUpgrade
extends Window {
    private static final int WIDTH = 120;
    private static final float COL_1 = 30.0f;
    private static final float COL_2 = 75.0f;
    private static final float COL_3 = 105.0f;
    private static final int GAP = 2;
    private static final int ITEMSLOT_SIZE = 18;
    private Item upgrader;
    private boolean force;
    private RedButton btnUpgrade;
    private RedButton btnCancel;

    public WndUpgrade(final Item upgrader, final Item toUpgrade, final boolean force) {
        Enum aug;
        boolean curseInfused;
        this.upgrader = upgrader;
        this.force = force;
        IconTitle title = new IconTitle(new ItemSprite(upgrader), Messages.get(this, "title", new Object[0]));
        title.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(title);
        int quantity = upgrader.quantity();
        Object moreUpgradeItem = Dungeon.hero.belongings.getItem(upgrader.getClass());
        if (moreUpgradeItem != null && moreUpgradeItem != upgrader) {
            quantity += ((Item)moreUpgradeItem).quantity();
        }
        Object mainText = Messages.get(this, "desc", new Object[0]);
        if (quantity > 1) {
            mainText = (String)mainText + "\n" + Messages.get(this, "remaining", quantity);
        }
        RenderedTextBlock message = PixelScene.renderTextBlock(6);
        message.text((String)mainText, 120);
        message.setPos(0.0f, title.bottom() + 2.0f);
        this.add(message);
        int levelFrom = toUpgrade.isIdentified() ? toUpgrade.level() : 0;
        int levelTo = levelFrom + 1;
        if (toUpgrade instanceof Wand && ((Wand)toUpgrade).resinBonus > 0) {
            --levelTo;
        }
        boolean bl = curseInfused = toUpgrade instanceof Weapon && ((Weapon)toUpgrade).curseInfusionBonus || toUpgrade instanceof Armor && ((Armor)toUpgrade).curseInfusionBonus || toUpgrade instanceof Wand && ((Wand)toUpgrade).curseInfusionBonus;
        if (curseInfused && toUpgrade.trueLevel() / 6 < (toUpgrade.trueLevel() + 1) / 6) {
            ++levelTo;
        }
        ColorBlock bg1 = new ColorBlock(18.0f, 18.0f, -1722591667);
        bg1.x = 66.0f;
        bg1.y = message.bottom() + 4.0f;
        this.add(bg1);
        ColorBlock bg2 = new ColorBlock(18.0f, 18.0f, -1722591667);
        bg2.x = 96.0f;
        bg2.y = message.bottom() + 4.0f;
        this.add(bg2);
        if (!toUpgrade.isIdentified()) {
            if (!toUpgrade.cursed && toUpgrade.cursedKnown) {
                bg1.hardlight(1.0f, 1.0f, 2.0f);
                bg2.hardlight(1.0f, 1.0f, 2.0f);
            } else {
                bg1.hardlight(2.0f, 1.0f, 2.0f);
                bg2.hardlight(2.0f, 1.0f, 2.0f);
            }
        } else if (toUpgrade.cursed && toUpgrade.cursedKnown) {
            bg1.hardlight(2.0f, 0.5f, 1.0f);
            bg2.hardlight(2.0f, 0.5f, 1.0f);
        }
        ItemSprite i1 = new ItemSprite();
        this.add(i1);
        i1.view(toUpgrade);
        i1.x = 75.0f - i1.width() / 2.0f;
        i1.y = bg1.y + (18.0f - i1.height()) / 2.0f;
        PixelScene.align(i1);
        this.add(i1);
        ItemSprite i2 = new ItemSprite();
        this.add(i2);
        i2.view(toUpgrade);
        i2.x = 105.0f - i2.width() / 2.0f;
        i2.y = i1.y;
        PixelScene.align(i2);
        this.add(i2);
        BitmapText t1 = new BitmapText(PixelScene.pixelFont);
        BitmapText t2 = new BitmapText(PixelScene.pixelFont);
        if (toUpgrade.isIdentified()) {
            if (levelFrom > 0) {
                t1.text("+" + levelFrom);
            } else {
                t1.text("");
            }
            t1.hardlight(0x44FF44);
            t2.text("+" + levelTo);
            t2.hardlight(0x44FF44);
            if (curseInfused) {
                t1.hardlight(0x8800FF);
                t2.hardlight(0x8800FF);
            }
        } else {
            t1.text("?");
            t2.text("+1?");
            t2.hardlight(0x44FF44);
        }
        t1.measure();
        t1.x = 84.0f - t1.width();
        t1.y = bg1.y + 18.0f - t1.baseLine() - 1.0f;
        this.add(t1);
        t2.measure();
        t2.x = 114.0f - t2.width();
        t2.y = bg2.y + 18.0f - t2.baseLine() - 1.0f;
        this.add(t2);
        float bottom = i1.y + 18.0f;
        if (toUpgrade instanceof Weapon) {
            aug = ((Weapon)toUpgrade).augment;
            bottom = this.fillFields(Messages.get(this, "damage", new Object[0]), ((Weapon.Augment)aug).damageFactor(((Weapon)toUpgrade).min(levelFrom)) + "-" + ((Weapon.Augment)aug).damageFactor(((Weapon)toUpgrade).max(levelFrom)), ((Weapon.Augment)aug).damageFactor(((Weapon)toUpgrade).min(levelTo)) + "-" + ((Weapon.Augment)aug).damageFactor(((Weapon)toUpgrade).max(levelTo)), bottom);
        }
        if (toUpgrade instanceof Crossbow) {
            bottom = this.fillFields(Messages.get(this, "dart_damage", new Object[0]), ((Crossbow)toUpgrade).dartMin(levelFrom) + "-" + ((Crossbow)toUpgrade).dartMax(levelFrom), ((Crossbow)toUpgrade).dartMin(levelTo) + "-" + ((Crossbow)toUpgrade).dartMax(levelTo), bottom);
        }
        if (toUpgrade instanceof Tomahawk) {
            bottom = this.fillFields(Messages.get(this, "bleeding", new Object[0]), Math.round(((Tomahawk)toUpgrade).minBleed(levelFrom)) + "-" + Math.round(((Tomahawk)toUpgrade).maxBleed(levelFrom)), Math.round(((Tomahawk)toUpgrade).minBleed(levelTo)) + "-" + Math.round(((Tomahawk)toUpgrade).maxBleed(levelTo)), bottom);
        }
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST && toUpgrade instanceof MeleeWeapon && ((MeleeWeapon)toUpgrade).upgradeAbilityStat(levelFrom) != null) {
            bottom = this.fillFields(Messages.get(toUpgrade, "upgrade_ability_stat_name", new Object[0]), ((MeleeWeapon)toUpgrade).upgradeAbilityStat(levelFrom), ((MeleeWeapon)toUpgrade).upgradeAbilityStat(levelTo), bottom);
        }
        if (toUpgrade instanceof Armor) {
            aug = ((Armor)toUpgrade).augment;
            bottom = this.fillFields(Messages.get(this, "blocking", new Object[0]), ((Armor)toUpgrade).DRMin(levelFrom) + "-" + ((Armor)toUpgrade).DRMax(levelFrom), ((Armor)toUpgrade).DRMin(levelTo) + "-" + ((Armor)toUpgrade).DRMax(levelTo), bottom);
        } else if (toUpgrade instanceof RoundShield) {
            bottom = this.fillFields(Messages.get(this, "blocking", new Object[0]), "0-" + ((RoundShield)toUpgrade).DRMax(levelFrom), "0-" + ((RoundShield)toUpgrade).DRMax(levelTo), bottom);
        } else if (toUpgrade instanceof Greatshield) {
            bottom = this.fillFields(Messages.get(this, "blocking", new Object[0]), "0-" + ((Greatshield)toUpgrade).DRMax(levelFrom), "0-" + ((Greatshield)toUpgrade).DRMax(levelTo), bottom);
        }
        if (toUpgrade instanceof Weapon) {
            bottom = this.fillFields(Messages.get(this, "weight", new Object[0]), Integer.toString(((Weapon)toUpgrade).STRReq(levelFrom)), Integer.toString(((Weapon)toUpgrade).STRReq(levelTo)), bottom);
        } else if (toUpgrade instanceof Armor) {
            bottom = this.fillFields(Messages.get(this, "weight", new Object[0]), Integer.toString(((Armor)toUpgrade).STRReq(levelFrom)), Integer.toString(((Armor)toUpgrade).STRReq(levelTo)), bottom);
        }
        if (toUpgrade instanceof MissileWeapon) {
            int uses1 = (int)Math.ceil(100.0f / ((MissileWeapon)toUpgrade).durabilityPerUse(levelFrom));
            int uses2 = (int)Math.ceil(100.0f / ((MissileWeapon)toUpgrade).durabilityPerUse(levelTo));
            bottom = this.fillFields(Messages.get(this, "durability", new Object[0]), uses1 > 100 ? "\u221e" : Integer.toString(uses1), uses2 > 100 ? "\u221e" : Integer.toString(uses2), bottom);
            bottom = this.fillFields(Messages.get(this, "quantity", new Object[0]), Integer.toString(toUpgrade.quantity()), Integer.toString(((MissileWeapon)toUpgrade).defaultQuantity()), bottom);
        }
        Item wand = toUpgrade;
        if (toUpgrade instanceof MagesStaff && ((MagesStaff)toUpgrade).wandClass() != null) {
            wand = Reflection.newInstance(((MagesStaff)toUpgrade).wandClass());
        }
        if (wand instanceof Wand) {
            if (((Wand)wand).upgradeStat1(levelFrom) != null) {
                bottom = this.fillFields(Messages.get(wand, "upgrade_stat_name_1", new Object[0]), ((Wand)wand).upgradeStat1(levelFrom), ((Wand)wand).upgradeStat1(levelTo), bottom);
            }
            if (((Wand)wand).upgradeStat2(levelFrom) != null) {
                bottom = this.fillFields(Messages.get(wand, "upgrade_stat_name_2", new Object[0]), ((Wand)wand).upgradeStat2(levelFrom), ((Wand)wand).upgradeStat2(levelTo), bottom);
            }
            if (((Wand)wand).upgradeStat3(levelFrom) != null) {
                bottom = this.fillFields(Messages.get(wand, "upgrade_stat_name_3", new Object[0]), ((Wand)wand).upgradeStat3(levelFrom), ((Wand)wand).upgradeStat3(levelTo), bottom);
            }
        }
        if (wand instanceof Wand) {
            int chargeboost = levelFrom + (toUpgrade instanceof MagesStaff ? 1 : 0);
            bottom = this.fillFields(Messages.get(this, "charges", new Object[0]), Integer.toString(Math.min(10, ((Wand)wand).initialCharges() + chargeboost)), Integer.toString(Math.min(10, ((Wand)wand).initialCharges() + chargeboost + 1)), bottom);
        }
        if (toUpgrade instanceof Ring && ((Ring)toUpgrade).isKnown()) {
            if (((Ring)toUpgrade).upgradeStat1(levelFrom) != null) {
                bottom = this.fillFields(Messages.get(toUpgrade, "upgrade_stat_name_1", new Object[0]), ((Ring)toUpgrade).upgradeStat1(levelFrom), ((Ring)toUpgrade).upgradeStat1(levelTo), bottom);
            }
            if (((Ring)toUpgrade).upgradeStat2(levelFrom) != null) {
                bottom = this.fillFields(Messages.get(toUpgrade, "upgrade_stat_name_2", new Object[0]), ((Ring)toUpgrade).upgradeStat2(levelFrom), ((Ring)toUpgrade).upgradeStat2(levelTo), bottom);
            }
            if (((Ring)toUpgrade).upgradeStat3(levelFrom) != null) {
                bottom = this.fillFields(Messages.get(toUpgrade, "upgrade_stat_name_3", new Object[0]), ((Ring)toUpgrade).upgradeStat3(levelFrom), ((Ring)toUpgrade).upgradeStat3(levelTo), bottom);
            }
        }
        ColorBlock sep = new ColorBlock(1.0f, 1.0f, -14540254);
        sep.size(1.0f, bottom - message.bottom());
        sep.x = 60.0f;
        sep.y = message.bottom() + 2.0f;
        this.add(sep);
        sep = new ColorBlock(1.0f, 1.0f, -14540254);
        sep.size(1.0f, bottom - message.bottom());
        sep.x = 90.0f;
        sep.y = message.bottom() + 2.0f;
        this.add(sep);
        if (!toUpgrade.isIdentified()) {
            bottom = toUpgrade instanceof Ring && !((Ring)toUpgrade).isKnown() ? this.addMessage(Messages.get(this, "unknown_ring", new Object[0]), 0xFF8800, bottom) : this.addMessage(Messages.get(this, "unided", new Object[0]), 0xFF8800, bottom);
        }
        if (!(upgrader instanceof MagicalInfusion)) {
            if (toUpgrade instanceof Weapon && ((Weapon)toUpgrade).hasGoodEnchant() || toUpgrade instanceof Armor && ((Armor)toUpgrade).hasGoodGlyph()) {
                int lossChance;
                if (toUpgrade instanceof Weapon && ((Weapon)toUpgrade).enchantHardened || toUpgrade instanceof Armor && ((Armor)toUpgrade).glyphHardened) {
                    lossChance = Math.min(100, 10 * (int)Math.pow(2.0, levelFrom - 6));
                } else {
                    lossChance = Math.min(100, 10 * (int)Math.pow(2.0, levelFrom - 4));
                    if (Dungeon.hero != null && Dungeon.hero.heroClass != HeroClass.WARRIOR && Dungeon.hero.hasTalent(Talent.RUNIC_TRANSFERENCE) && levelFrom < 5 + Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE)) {
                        lossChance = 0;
                    }
                }
                if (lossChance >= 10) {
                    String warn = toUpgrade instanceof Weapon ? (((Weapon)toUpgrade).enchantHardened ? Messages.get(this, "harden", lossChance) : Messages.get(this, "enchant", lossChance)) : (((Armor)toUpgrade).glyphHardened ? Messages.get(this, "harden", lossChance) : Messages.get(this, "glyph", lossChance));
                    bottom = this.addMessage(warn, 0xFF8800, bottom);
                }
            }
            if ((toUpgrade.cursed || toUpgrade instanceof Weapon && ((Weapon)toUpgrade).hasCurseEnchant() || toUpgrade instanceof Armor && ((Armor)toUpgrade).hasCurseGlyph()) && toUpgrade.cursedKnown) {
                bottom = toUpgrade.cursed && toUpgrade instanceof MeleeWeapon && ((Weapon)toUpgrade).hasCurseEnchant() || toUpgrade instanceof Armor && ((Armor)toUpgrade).hasCurseGlyph() ? this.addMessage(Messages.get(this, "cursed_weaken", new Object[0]), 65280, bottom) : this.addMessage(Messages.get(this, "cursed", new Object[0]), 65280, bottom);
                if (curseInfused) {
                    bottom = this.addMessage(Messages.get(this, "curse_infusion", new Object[0]), 0xFF8800, bottom);
                }
            }
        }
        if (toUpgrade instanceof Wand && ((Wand)toUpgrade).resinBonus > 0) {
            bottom = this.addMessage(Messages.get(this, "resin", new Object[0]), 0xFF8800, bottom);
        }
        if (toUpgrade instanceof MissileWeapon && ((MissileWeapon)toUpgrade).extraThrownLeft) {
            bottom = this.addMessage(Messages.get(this, "thrown_dust", new Object[0]), 0xFF8800, bottom);
        }
        this.btnUpgrade = new RedButton(Messages.get(this, "upgrade", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                ScrollOfUpgrade.upgrade(Dungeon.hero);
                Item upgraded = toUpgrade;
                if (upgrader instanceof ScrollOfUpgrade) {
                    ((ScrollOfUpgrade)upgrader).readAnimation();
                    upgraded = ((ScrollOfUpgrade)upgrader).upgradeItem(toUpgrade);
                    Sample.INSTANCE.play("sounds/read.mp3");
                } else if (upgrader instanceof MagicalInfusion) {
                    ((MagicalInfusion)upgrader).useAnimation();
                    upgraded = ((MagicalInfusion)upgrader).upgradeItem(toUpgrade);
                }
                if (!force) {
                    upgrader.detach(Dungeon.hero.belongings.backpack);
                }
                Object moreUpgradeItem = Dungeon.hero.belongings.getItem(upgrader.getClass());
                WndUpgrade.this.hide();
                if (moreUpgradeItem != null && toUpgrade.isUpgradable()) {
                    GameScene.show(new WndUpgrade((Item)moreUpgradeItem, upgraded, false));
                }
            }
        };
        this.btnUpgrade.setRect(0.0f, bottom + 4.0f, 60.0f, 16.0f);
        this.add(this.btnUpgrade);
        this.btnCancel = new RedButton(Messages.get(this, "back", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                WndUpgrade.this.hide();
                if (upgrader instanceof ScrollOfUpgrade) {
                    ((ScrollOfUpgrade)upgrader).reShowSelector(force);
                } else if (upgrader instanceof MagicalInfusion) {
                    ((MagicalInfusion)upgrader).reShowSelector();
                }
            }
        };
        this.btnCancel.setRect(this.btnUpgrade.right() + 1.0f, bottom + 4.0f, 60.0f, 16.0f);
        this.add(this.btnCancel);
        this.btnUpgrade.enable(Dungeon.hero.ready);
        this.btnUpgrade.icon(new ItemSprite(upgrader));
        this.btnCancel.icon(Icons.EXIT.get());
        bottom = (int)this.btnCancel.bottom();
        this.resize(120, (int)bottom);
    }

    @Override
    public synchronized void update() {
        super.update();
        if (!this.btnUpgrade.active && Dungeon.hero.ready) {
            this.btnUpgrade.enable(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.upgrader instanceof ScrollOfUpgrade) {
            ((ScrollOfUpgrade)this.upgrader).reShowSelector(this.force);
        } else if (this.upgrader instanceof MagicalInfusion) {
            ((MagicalInfusion)this.upgrader).reShowSelector();
        }
    }

    public WndBag.ItemSelector getItemSelector() {
        if (this.upgrader instanceof ScrollOfUpgrade) {
            return ((ScrollOfUpgrade)this.upgrader).getSelector(this.force);
        }
        if (this.upgrader instanceof MagicalInfusion) {
            return ((MagicalInfusion)this.upgrader).getSelector();
        }
        return null;
    }

    private float fillFields(String title, String msg1, String msg2, float bottom) {
        if (Messages.lang() == Languages.CHI_SMPL || Messages.lang() == Languages.CHI_TRAD) {
            msg1 = msg1.replace('-', '~');
            msg2 = msg2.replace('-', '~');
        }
        RenderedTextBlock ttl = PixelScene.renderTextBlock(6);
        ttl.align(2);
        ttl.text(title, 60);
        ttl.setPos(30.0f - ttl.width() / 2.0f, bottom + 2.0f);
        PixelScene.align(ttl);
        this.add(ttl);
        RenderedTextBlock m1 = PixelScene.renderTextBlock(msg1, 6);
        m1.setPos(75.0f - m1.width() / 2.0f, ttl.top());
        PixelScene.align(m1);
        this.add(m1);
        RenderedTextBlock m2 = PixelScene.renderTextBlock(msg2, 6);
        m2.setPos(105.0f - m2.width() / 2.0f, ttl.top());
        PixelScene.align(m2);
        this.add(m2);
        return ttl.bottom() + 2.0f;
    }

    private float addMessage(String text, int color, float bottom) {
        RenderedTextBlock message = PixelScene.renderTextBlock(6);
        message.text(text, 120);
        message.setPos(0.0f, bottom + 2.0f);
        message.hardlight(color);
        this.add(message);
        return message.bottom();
    }
}

