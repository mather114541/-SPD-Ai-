/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.MindForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Crystal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class Trinity
extends ArmorAbility {
    private Bundlable bodyForm;
    private Bundlable mindForm;
    private Bundlable spiritForm;
    private static final String BODY = "body_form";
    private static final String MIND = "mind_form";
    private static final String SPIRIT = "spirit_form";

    public Trinity() {
        this.baseChargeUse = 25.0f;
        this.bodyForm = null;
        this.mindForm = null;
        this.spiritForm = null;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (this.bodyForm == null && this.mindForm == null && this.spiritForm == null) {
            GLog.w(Messages.get(this, "no_imbue", new Object[0]), new Object[0]);
        } else {
            GameScene.show(new WndUseTrinity(armor));
        }
    }

    @Override
    public int targetedPos(Char user, int dst) {
        if (this.mindForm != null) {
            return ((Item)this.mindForm).targetingPos((Hero)user, dst);
        }
        return super.targetedPos(user, dst);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.bodyForm != null) {
            bundle.put(BODY, this.bodyForm);
        }
        if (this.mindForm != null) {
            bundle.put(MIND, this.mindForm);
        }
        if (this.spiritForm != null) {
            bundle.put(SPIRIT, this.spiritForm);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(BODY)) {
            this.bodyForm = bundle.get(BODY);
        }
        if (bundle.contains(MIND)) {
            this.mindForm = bundle.get(MIND);
        }
        if (bundle.contains(SPIRIT)) {
            this.spiritForm = bundle.get(SPIRIT);
        }
    }

    @Override
    public int icon() {
        return 32;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.BODY_FORM, Talent.MIND_FORM, Talent.SPIRIT_FORM, Talent.HEROIC_ENERGY};
    }

    public static String trinityItemUseText(Class<?> cls) {
        float chargeUse = Trinity.trinityChargeUsePerEffect(cls);
        if (Weapon.Enchantment.class.isAssignableFrom(cls) || Armor.Glyph.class.isAssignableFrom(cls)) {
            for (Class<?> ench : Weapon.Enchantment.rare) {
                if (!ench.equals(cls)) continue;
                return Messages.get(Trinity.class, "rare_ench_glyph_use", BodyForm.duration(), Messages.decimalFormat("#.##", chargeUse));
            }
            for (Class<?> glyph : Armor.Glyph.rare) {
                if (!glyph.equals(cls)) continue;
                return Messages.get(Trinity.class, "rare_ench_glyph_use", BodyForm.duration(), Messages.decimalFormat("#.##", chargeUse));
            }
            return Messages.get(Trinity.class, "ench_glyph_use", BodyForm.duration(), Messages.decimalFormat("#.##", chargeUse));
        }
        if (MissileWeapon.class.isAssignableFrom(cls)) {
            return Messages.get(Trinity.class, "thrown_use", MindForm.itemLevel(), Messages.decimalFormat("#.##", chargeUse));
        }
        if (Wand.class.isAssignableFrom(cls)) {
            if (cls.equals(WandOfFireblast.class) || cls.equals(WandOfRegrowth.class)) {
                return Messages.get(Trinity.class, "wand_multi_use", MindForm.itemLevel(), Messages.decimalFormat("#.##", chargeUse));
            }
            return Messages.get(Trinity.class, "wand_use", MindForm.itemLevel(), Messages.decimalFormat("#.##", chargeUse));
        }
        if (Ring.class.isAssignableFrom(cls)) {
            return Messages.get(Trinity.class, "ring_use", SpiritForm.ringLevel(), Messages.decimalFormat("#.##", chargeUse));
        }
        if (Artifact.class.isAssignableFrom(cls)) {
            return Messages.get(Trinity.class, cls.getSimpleName() + "_use", SpiritForm.artifactLevel(), Messages.decimalFormat("#.##", chargeUse));
        }
        return "error!";
    }

    public static float trinityChargeUsePerEffect(Class<?> cls) {
        float chargeUse = Dungeon.hero.armorAbility.chargeUse(Dungeon.hero);
        if (Weapon.Enchantment.class.isAssignableFrom(cls) || Armor.Glyph.class.isAssignableFrom(cls)) {
            for (Class<?> ench : Weapon.Enchantment.rare) {
                if (!ench.equals(cls)) continue;
                return 2.0f * chargeUse;
            }
            for (Class<?> glyph : Armor.Glyph.rare) {
                if (!glyph.equals(cls)) continue;
                return 2.0f * chargeUse;
            }
        }
        if (cls.equals(WandOfFireblast.class) || cls.equals(WandOfRegrowth.class)) {
            return 2.0f * chargeUse;
        }
        if (Artifact.class.isAssignableFrom(cls)) {
            if (cls.equals(DriedRose.class) || cls.equals(UnstableSpellbook.class) || cls.equals(SkeletonKey.class)) {
                return 2.0f * chargeUse;
            }
            if (cls.equals(EtherealChains.class) || cls.equals(TalismanOfForesight.class) || cls.equals(TimekeepersHourglass.class)) {
                return 1.4f * chargeUse;
            }
        }
        return chargeUse;
    }

    public class WndUseTrinity
    extends WndTitledMessage {
        public WndUseTrinity(final ClassArmor armor) {
            super(new HeroIcon(Trinity.this), Messages.titleCase(Trinity.this.name()), Messages.get(WndUseTrinity.class, "text", new Object[0]));
            int top = this.height;
            if (Trinity.this.bodyForm != null) {
                Component btnBody = null;
                if (Trinity.this.bodyForm instanceof Weapon.Enchantment) {
                    btnBody = new RedButton(Messages.get(WndUseTrinity.class, "body", Messages.titleCase(((Weapon.Enchantment)Trinity.this.bodyForm).name())) + " " + Trinity.trinityItemUseText(Trinity.this.bodyForm.getClass()), 6){

                        @Override
                        protected void onClick() {
                            if (Dungeon.hero.belongings.weapon() != null && ((Weapon)Dungeon.hero.belongings.weapon()).enchantment != null && ((Weapon)Dungeon.hero.belongings.weapon()).enchantment.getClass().equals(Trinity.this.bodyForm.getClass())) {
                                GLog.w(Messages.get(Trinity.class, "no_duplicate", new Object[0]), new Object[0]);
                                WndUseTrinity.this.hide();
                            } else {
                                Buff.prolong(Dungeon.hero, BodyForm.BodyFormBuff.class, BodyForm.duration()).setEffect(Trinity.this.bodyForm);
                                if (Trinity.this.bodyForm instanceof Crystal) {
                                    ((Crystal)Trinity.this.bodyForm).setDurability((float)BodyForm.duration() * 1.5f);
                                }
                                Sample.INSTANCE.play("sounds/teleport.mp3");
                                WornShortsword w = new WornShortsword();
                                if (Dungeon.hero.belongings.weapon() != null) {
                                    w.image = Dungeon.hero.belongings.weapon().image;
                                }
                                w.enchant((Weapon.Enchantment)Trinity.this.bodyForm);
                                Enchanting.show(Dungeon.hero, w);
                                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                                Dungeon.hero.spendAndNext(1.0f);
                                armor.charge -= Trinity.trinityChargeUsePerEffect(Trinity.this.bodyForm.getClass());
                                1 v0 = this;
                                v0.armor.updateQuickslot();
                                Invisibility.dispel();
                                WndUseTrinity.this.hide();
                            }
                        }
                    };
                    if (Dungeon.hero.belongings.weapon() != null) {
                        ((StyledButton)btnBody).icon(new ItemSprite(Dungeon.hero.belongings.weapon().image, ((Weapon.Enchantment)Trinity.this.bodyForm).glowing()));
                    } else {
                        ((StyledButton)btnBody).icon(new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD, ((Weapon.Enchantment)Trinity.this.bodyForm).glowing()));
                    }
                } else if (Trinity.this.bodyForm instanceof Armor.Glyph) {
                    btnBody = new RedButton(Messages.get(WndUseTrinity.class, "body", Messages.titleCase(((Armor.Glyph)Trinity.this.bodyForm).name())) + " " + Trinity.trinityItemUseText(Trinity.this.bodyForm.getClass()), 6){

                        @Override
                        protected void onClick() {
                            if (Dungeon.hero.belongings.armor() != null && Dungeon.hero.belongings.armor().glyph != null && Dungeon.hero.belongings.armor().glyph.getClass().equals(Trinity.this.bodyForm.getClass())) {
                                GLog.w(Messages.get(Trinity.class, "no_duplicate", new Object[0]), new Object[0]);
                                WndUseTrinity.this.hide();
                            } else {
                                Buff.prolong(Dungeon.hero, BodyForm.BodyFormBuff.class, BodyForm.duration()).setEffect(Trinity.this.bodyForm);
                                Sample.INSTANCE.play("sounds/teleport.mp3");
                                ClothArmor a = new ClothArmor();
                                if (Dungeon.hero.belongings.armor() != null) {
                                    a.image = Dungeon.hero.belongings.armor().image;
                                }
                                a.inscribe((Armor.Glyph)Trinity.this.bodyForm);
                                Enchanting.show(Dungeon.hero, a);
                                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                                Dungeon.hero.spendAndNext(1.0f);
                                armor.charge -= Trinity.trinityChargeUsePerEffect(Trinity.this.bodyForm.getClass());
                                2 v0 = this;
                                v0.armor.updateQuickslot();
                                Invisibility.dispel();
                                WndUseTrinity.this.hide();
                            }
                        }
                    };
                    if (Dungeon.hero.belongings.armor() != null) {
                        ((StyledButton)btnBody).icon(new ItemSprite(Dungeon.hero.belongings.armor().image, ((Armor.Glyph)Trinity.this.bodyForm).glowing()));
                    } else {
                        ((StyledButton)btnBody).icon(new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH, ((Armor.Glyph)Trinity.this.bodyForm).glowing()));
                    }
                }
                ((RedButton)btnBody).multiline = true;
                btnBody.setSize(this.width, 100.0f);
                btnBody.setRect(0.0f, top + 2, this.width, ((StyledButton)btnBody).reqHeight());
                this.add(btnBody);
                top = (int)btnBody.bottom();
                ((StyledButton)btnBody).enable(Dungeon.hero.buff(MagicImmune.class) == null && armor.charge >= Trinity.trinityChargeUsePerEffect(Trinity.this.bodyForm.getClass()));
            }
            if (Trinity.this.mindForm != null) {
                RedButton btnMind = new RedButton(Messages.get(WndUseTrinity.class, "mind", Messages.titleCase(((Item)Trinity.this.mindForm).name())) + " " + Trinity.trinityItemUseText(Trinity.this.mindForm.getClass()), 6){

                    @Override
                    protected void onClick() {
                        WndUseTrinity.this.hide();
                        MindForm.targetSelector mindEffect = new MindForm.targetSelector();
                        mindEffect.setEffect(Trinity.this.mindForm);
                        GameScene.selectCell(mindEffect);
                        Sample.INSTANCE.play("sounds/teleport.mp3");
                        Enchanting.show(Dungeon.hero, (Item)Trinity.this.mindForm);
                        Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                        if (((Item)Trinity.this.mindForm).usesTargeting && Dungeon.quickslot.contains(armor)) {
                            QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
                        }
                    }
                };
                btnMind.icon(new ItemSprite((Item)Trinity.this.mindForm));
                btnMind.multiline = true;
                btnMind.setSize(this.width, 100.0f);
                btnMind.setRect(0.0f, top + 2, this.width, btnMind.reqHeight());
                this.add(btnMind);
                top = (int)btnMind.bottom();
                btnMind.enable(armor.charge >= Trinity.trinityChargeUsePerEffect(Trinity.this.mindForm.getClass()));
                if (Trinity.this.mindForm instanceof Wand && Dungeon.hero.buff(MagicImmune.class) != null) {
                    btnMind.enable(false);
                }
            }
            if (Trinity.this.spiritForm != null) {
                RedButton btnSpirit = new RedButton(Messages.get(WndUseTrinity.class, "spirit", Messages.titleCase(((Item)Trinity.this.spiritForm).name())) + " " + Trinity.trinityItemUseText(Trinity.this.spiritForm.getClass()), 6){

                    @Override
                    protected void onClick() {
                        if (Dungeon.hero.belongings.ring() != null && Dungeon.hero.belongings.ring().getClass().equals(Trinity.this.spiritForm.getClass()) || Dungeon.hero.belongings.misc() != null && Dungeon.hero.belongings.misc().getClass().equals(Trinity.this.spiritForm.getClass()) || Dungeon.hero.belongings.artifact() != null && Dungeon.hero.belongings.artifact().getClass().equals(Trinity.this.spiritForm.getClass())) {
                            GLog.w(Messages.get(Trinity.class, "no_duplicate", new Object[0]), new Object[0]);
                            WndUseTrinity.this.hide();
                            return;
                        }
                        Invisibility.dispel();
                        if (Trinity.this.spiritForm instanceof Ring || Trinity.this.spiritForm instanceof ChaliceOfBlood) {
                            Buff.prolong(Dungeon.hero, SpiritForm.SpiritFormBuff.class, 20.0f).setEffect(Trinity.this.spiritForm);
                            Dungeon.hero.spendAndNext(1.0f);
                        } else {
                            SpiritForm.applyActiveArtifactEffect(armor, (Artifact)Trinity.this.spiritForm);
                        }
                        Sample.INSTANCE.play("sounds/teleport.mp3");
                        Enchanting.show(Dungeon.hero, (Item)Trinity.this.spiritForm);
                        Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                        armor.charge -= Trinity.trinityChargeUsePerEffect(Trinity.this.spiritForm.getClass());
                        4 v0 = this;
                        v0.armor.updateQuickslot();
                        WndUseTrinity.this.hide();
                    }
                };
                if (Trinity.this.spiritForm instanceof Artifact) {
                    ((Artifact)Trinity.this.spiritForm).resetForTrinity(SpiritForm.artifactLevel());
                }
                btnSpirit.icon(new ItemSprite((Item)Trinity.this.spiritForm));
                btnSpirit.multiline = true;
                btnSpirit.setSize(this.width, 100.0f);
                btnSpirit.setRect(0.0f, top + 2, this.width, btnSpirit.reqHeight());
                this.add(btnSpirit);
                top = (int)btnSpirit.bottom();
                btnSpirit.enable(Dungeon.hero.buff(MagicImmune.class) == null && armor.charge >= Trinity.trinityChargeUsePerEffect(Trinity.this.spiritForm.getClass()));
            }
            this.resize(this.width, top);
        }
    }

    public static class WndItemConfirm
    extends WndTitledMessage {
        public WndItemConfirm(final Window parentWnd, final Item item, final HolyTome tome, final ClericSpell spell) {
            super(new ItemSprite(item), Messages.titleCase(WndItemConfirm.getName(item)), WndItemConfirm.getText(item));
            String text = spell == BodyForm.INSTANCE ? Messages.get(this, "body", new Object[0]) : (spell == MindForm.INSTANCE ? Messages.get(this, "mind", new Object[0]) : Messages.get(this, "spirit", new Object[0]));
            RedButton btnConfirm = new RedButton(text){

                @Override
                protected void onClick() {
                    parentWnd.hide();
                    this.hide();
                    if (item instanceof MeleeWeapon) {
                        ((Trinity)Dungeon.hero.armorAbility).bodyForm = ((MeleeWeapon)item).enchantment;
                    } else if (item instanceof Armor) {
                        ((Trinity)Dungeon.hero.armorAbility).bodyForm = ((Armor)item).glyph;
                    } else if (item instanceof Wand || item instanceof MissileWeapon) {
                        ((Trinity)Dungeon.hero.armorAbility).mindForm = item;
                    } else {
                        ((Trinity)Dungeon.hero.armorAbility).spiritForm = item;
                    }
                    spell.onSpellCast(tome, Dungeon.hero);
                    Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                    Enchanting.show(Dungeon.hero, item);
                    Sample.INSTANCE.play("sounds/teleport.mp3");
                }
            };
            btnConfirm.setRect(0.0f, this.height + 2, this.width, 16.0f);
            this.add(btnConfirm);
            this.resize(this.width, (int)btnConfirm.bottom());
        }

        private static String getName(Item item) {
            if (item instanceof MeleeWeapon) {
                return ((MeleeWeapon)item).enchantment.name();
            }
            if (item instanceof Armor) {
                return ((Armor)item).glyph.name();
            }
            return item.name();
        }

        private static String getText(Item item) {
            if (item instanceof MeleeWeapon) {
                return ((MeleeWeapon)item).enchantment.desc() + "\n\n" + Trinity.trinityItemUseText(((MeleeWeapon)item).enchantment.getClass());
            }
            if (item instanceof Armor) {
                return ((Armor)item).glyph.desc() + "\n\n" + Trinity.trinityItemUseText(((Armor)item).glyph.getClass());
            }
            return item.desc() + "\n\n" + Trinity.trinityItemUseText(item.getClass());
        }
    }

    public static class WndItemtypeSelect
    extends WndTitledMessage {
        public WndItemtypeSelect(final HolyTome tome, final ClericSpell spell) {
            super(new HeroIcon(spell), Messages.titleCase(spell.name()), Messages.get(WndItemtypeSelect.class, "text", new Object[0]));
            int n;
            ArrayList discoveredClasses = new ArrayList();
            if (spell == BodyForm.INSTANCE) {
                for (Class<?> cls : Catalog.ENCHANTMENTS.items()) {
                    if (!Statistics.itemTypesDiscovered.contains(cls)) continue;
                    discoveredClasses.add(cls);
                }
                for (Class<?> cls : Catalog.GLYPHS.items()) {
                    if (!Statistics.itemTypesDiscovered.contains(cls)) continue;
                    discoveredClasses.add(cls);
                }
            } else if (spell == MindForm.INSTANCE) {
                for (Class<?> cls : Catalog.WANDS.items()) {
                    if (!Statistics.itemTypesDiscovered.contains(cls)) continue;
                    discoveredClasses.add(cls);
                }
                for (Class<?> cls : Catalog.THROWN_WEAPONS.items()) {
                    if (!Statistics.itemTypesDiscovered.contains(cls)) continue;
                    discoveredClasses.add(cls);
                }
                for (Class<?> cls : Catalog.TIPPED_DARTS.items()) {
                    if (!Statistics.itemTypesDiscovered.contains(cls)) continue;
                    discoveredClasses.add(cls);
                }
            } else if (spell == SpiritForm.INSTANCE) {
                for (Class<?> cls : Catalog.RINGS.items()) {
                    if (!Statistics.itemTypesDiscovered.contains(cls)) continue;
                    discoveredClasses.add(cls);
                }
                for (Class<?> cls : Catalog.ARTIFACTS.items()) {
                    if (Statistics.itemTypesDiscovered.contains(cls)) {
                        discoveredClasses.add(cls);
                    }
                    discoveredClasses.remove(HolyTome.class);
                }
            }
            ArrayList<Item> options = new ArrayList<Item>();
            for (Class clazz : discoveredClasses) {
                if (Weapon.Enchantment.class.isAssignableFrom(clazz)) {
                    WornShortsword w = new WornShortsword(){

                        @Override
                        public String name() {
                            return this.enchantment.name();
                        }
                    };
                    if (Dungeon.hero.belongings.weapon() != null) {
                        w.image = Dungeon.hero.belongings.weapon().image;
                    }
                    w.enchant((Weapon.Enchantment)Reflection.newInstance(clazz));
                    w.cursedKnown = true;
                    options.add(w);
                    continue;
                }
                if (Armor.Glyph.class.isAssignableFrom(clazz)) {
                    ClothArmor a = new ClothArmor(){

                        @Override
                        public String name() {
                            return this.glyph.name();
                        }
                    };
                    if (Dungeon.hero.belongings.armor() != null) {
                        a.image = Dungeon.hero.belongings.armor().image;
                    }
                    a.inscribe((Armor.Glyph)Reflection.newInstance(clazz));
                    a.cursedKnown = true;
                    options.add(a);
                    continue;
                }
                options.add((Item)Reflection.newInstance(clazz));
            }
            int top = this.height + 2;
            boolean bl = false;
            for (final Item item : options) {
                ItemButton btn = new ItemButton(){

                    @Override
                    protected void onClick() {
                        GameScene.show(new WndItemConfirm(this, item, tome, spell));
                    }
                };
                btn.item(item);
                btn.slot().textVisible(false);
                btn.setRect(n, top, 19.0f, 19.0f);
                this.add(btn);
                if ((n += 20) < this.width - 19) continue;
                top += 20;
                n = 0;
            }
            if (n > 0) {
                top += 20;
                boolean bl2 = false;
            }
            this.resize(this.width, top);
        }
    }
}

