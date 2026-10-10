/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ScrollEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.RecallInscription;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

public enum Talent {
    HEARTY_MEAL(0),
    VETERANS_INTUITION(1),
    PROVOKED_ANGER(2),
    IRON_WILL(3),
    IRON_STOMACH(4),
    LIQUID_WILLPOWER(5),
    RUNIC_TRANSFERENCE(6),
    LETHAL_MOMENTUM(7),
    IMPROVISED_PROJECTILES(8),
    HOLD_FAST(9, 3),
    STRONGMAN(10, 3),
    ENDLESS_RAGE(11, 3),
    DEATHLESS_FURY(12, 3),
    ENRAGED_CATALYST(13, 3),
    CLEAVE(14, 3),
    LETHAL_DEFENSE(15, 3),
    ENHANCED_COMBO(16, 3),
    BODY_SLAM(17, 4),
    IMPACT_WAVE(18, 4),
    DOUBLE_JUMP(19, 4),
    EXPANDING_WAVE(20, 4),
    STRIKING_WAVE(21, 4),
    SHOCK_FORCE(22, 4),
    SUSTAINED_RETRIBUTION(23, 4),
    SHRUG_IT_OFF(24, 4),
    EVEN_THE_ODDS(25, 4),
    EMPOWERING_MEAL(32),
    SCHOLARS_INTUITION(33),
    LINGERING_MAGIC(34),
    BACKUP_BARRIER(35),
    ENERGIZING_MEAL(36),
    INSCRIBED_POWER(37),
    WAND_PRESERVATION(38),
    ARCANE_VISION(39),
    SHIELD_BATTERY(40),
    DESPERATE_POWER(41, 3),
    ALLY_WARP(42, 3),
    EMPOWERED_STRIKE(43, 3),
    MYSTICAL_CHARGE(44, 3),
    EXCESS_CHARGE(45, 3),
    SOUL_EATER(46, 3),
    SOUL_SIPHON(47, 3),
    NECROMANCERS_MINIONS(48, 3),
    BLAST_RADIUS(49, 4),
    ELEMENTAL_POWER(50, 4),
    REACTIVE_BARRIER(51, 4),
    WILD_POWER(52, 4),
    FIRE_EVERYTHING(53, 4),
    CONSERVED_MAGIC(54, 4),
    TELEFRAG(55, 4),
    REMOTE_BEACON(56, 4),
    LONGRANGE_WARP(57, 4),
    CACHED_RATIONS(64),
    THIEFS_INTUITION(65),
    SUCKER_PUNCH(66),
    PROTECTIVE_SHADOWS(67),
    MYSTICAL_MEAL(68),
    INSCRIBED_STEALTH(69),
    WIDE_SEARCH(70),
    SILENT_STEPS(71),
    ROGUES_FORESIGHT(72),
    ENHANCED_RINGS(73, 3),
    LIGHT_CLOAK(74, 3),
    ENHANCED_LETHALITY(75, 3),
    ASSASSINS_REACH(76, 3),
    BOUNTY_HUNTER(77, 3),
    EVASIVE_ARMOR(78, 3),
    PROJECTILE_MOMENTUM(79, 3),
    SPEEDY_STEALTH(80, 3),
    HASTY_RETREAT(81, 4),
    BODY_REPLACEMENT(82, 4),
    SHADOW_STEP(83, 4),
    FEAR_THE_REAPER(84, 4),
    DEATHLY_DURABILITY(85, 4),
    DOUBLE_MARK(86, 4),
    SHADOW_BLADE(87, 4),
    CLONED_ARMOR(88, 4),
    PERFECT_COPY(89, 4),
    NATURES_BOUNTY(96),
    SURVIVALISTS_INTUITION(97),
    FOLLOWUP_STRIKE(98),
    NATURES_AID(99),
    INVIGORATING_MEAL(100),
    LIQUID_NATURE(101),
    REJUVENATING_STEPS(102),
    HEIGHTENED_SENSES(103),
    DURABLE_PROJECTILES(104),
    POINT_BLANK(105, 3),
    SEER_SHOT(106, 3),
    FARSIGHT(107, 3),
    SHARED_ENCHANTMENT(108, 3),
    SHARED_UPGRADES(109, 3),
    DURABLE_TIPS(110, 3),
    BARKSKIN(111, 3),
    SHIELDING_DEW(112, 3),
    FAN_OF_BLADES(113, 4),
    PROJECTING_BLADES(114, 4),
    SPIRIT_BLADES(115, 4),
    GROWING_POWER(116, 4),
    NATURES_WRATH(117, 4),
    WILD_MOMENTUM(118, 4),
    EAGLE_EYE(119, 4),
    GO_FOR_THE_EYES(120, 4),
    SWIFT_SPIRIT(121, 4),
    STRENGTHENING_MEAL(128),
    ADVENTURERS_INTUITION(129),
    PATIENT_STRIKE(130),
    AGGRESSIVE_BARRIER(131),
    FOCUSED_MEAL(132),
    LIQUID_AGILITY(133),
    WEAPON_RECHARGING(134),
    LETHAL_HASTE(135),
    SWIFT_EQUIP(136),
    PRECISE_ASSAULT(137, 3),
    DEADLY_FOLLOWUP(138, 3),
    VARIED_CHARGE(139, 3),
    TWIN_UPGRADES(140, 3),
    COMBINED_LETHALITY(141, 3),
    UNENCUMBERED_SPIRIT(142, 3),
    MONASTIC_VIGOR(143, 3),
    COMBINED_ENERGY(144, 3),
    CLOSE_THE_GAP(145, 4),
    INVIGORATING_VICTORY(146, 4),
    ELIMINATION_MATCH(147, 4),
    ELEMENTAL_REACH(148, 4),
    STRIKING_FORCE(149, 4),
    DIRECTED_POWER(150, 4),
    FEIGNED_RETREAT(151, 4),
    EXPOSE_WEAKNESS(152, 4),
    COUNTER_ABILITY(153, 4),
    SATIATED_SPELLS(160),
    HOLY_INTUITION(161),
    SEARING_LIGHT(162),
    SHIELD_OF_LIGHT(163),
    ENLIGHTENING_MEAL(164),
    RECALL_INSCRIPTION(165),
    SUNRAY(166),
    DIVINE_SENSE(167),
    BLESS(168),
    CLEANSE(169, 3),
    LIGHT_READING(170, 3),
    HOLY_LANCE(171, 3),
    HALLOWED_GROUND(172, 3),
    MNEMONIC_PRAYER(173, 3),
    LAY_ON_HANDS(174, 3),
    AURA_OF_PROTECTION(175, 3),
    WALL_OF_LIGHT(176, 3),
    DIVINE_INTERVENTION(177, 4),
    JUDGEMENT(178, 4),
    FLASH(179, 4),
    BODY_FORM(180, 4),
    MIND_FORM(181, 4),
    SPIRIT_FORM(182, 4),
    BEAMING_RAY(183, 4),
    LIFE_LINK(184, 4),
    STASIS(185, 4),
    HEROIC_ENERGY(26, 4),
    RATSISTANCE(215, 4),
    RATLOMACY(216, 4),
    RATFORCEMENTS(217, 4);

    int icon;
    int maxPoints;
    public static int[] tierLevelThresholds;
    public static final int MAX_TALENT_TIERS = 4;
    private static final String TALENT_TIER = "talents_tier_";
    private static final HashSet<String> removedTalents;
    private static final HashMap<String, String> renamedTalents;

    private Talent(int icon) {
        this(icon, 2);
    }

    private Talent(int icon, int maxPoints) {
        this.icon = icon;
        this.maxPoints = maxPoints;
    }

    public int icon() {
        if (this == HEROIC_ENERGY) {
            if (Ratmogrify.useRatroicEnergy) {
                return 218;
            }
            HeroClass cls = Dungeon.hero != null ? Dungeon.hero.heroClass : GamesInProgress.selectedClass;
            switch (cls) {
                default: {
                    return 26;
                }
                case MAGE: {
                    return 58;
                }
                case ROGUE: {
                    return 90;
                }
                case HUNTRESS: {
                    return 122;
                }
                case DUELIST: {
                    return 154;
                }
                case CLERIC: 
            }
            return 186;
        }
        return this.icon;
    }

    public int maxPoints() {
        return this.maxPoints;
    }

    public String title() {
        if (this == HEROIC_ENERGY && Ratmogrify.useRatroicEnergy) {
            return Messages.get((Object)this, this.name() + ".rat_title", new Object[0]);
        }
        return Messages.get((Object)this, this.name() + ".title", new Object[0]);
    }

    public final String desc() {
        return this.desc(false);
    }

    public String desc(boolean metamorphed) {
        String metaDesc;
        if (metamorphed && !(metaDesc = Messages.get((Object)this, this.name() + ".meta_desc", new Object[0])).equals("!!!NO TEXT FOUND!!!")) {
            return Messages.get((Object)this, this.name() + ".desc", new Object[0]) + "\n\n" + metaDesc;
        }
        return Messages.get((Object)this, this.name() + ".desc", new Object[0]);
    }

    public static void onTalentUpgraded(Hero hero, Talent talent) {
        if (talent == IRON_WILL && hero.heroClass != HeroClass.WARRIOR) {
            Buff.affect(hero, BrokenSeal.WarriorShield.class);
        }
        if (talent == VETERANS_INTUITION && hero.pointsInTalent(VETERANS_INTUITION) == 2 && hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled()) {
            hero.belongings.armor.identify();
        }
        if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 2) {
            if (hero.belongings.ring instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.ring.identify();
            }
            if (hero.belongings.misc instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.misc.identify();
            }
            for (Item item : Dungeon.hero.belongings) {
                if (!(item instanceof Ring)) continue;
                ((Ring)item).setKnown();
            }
        }
        if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 1) {
            if (hero.belongings.ring instanceof Ring) {
                hero.belongings.ring.setKnown();
            }
            if (hero.belongings.misc instanceof Ring) {
                ((Ring)hero.belongings.misc).setKnown();
            }
        }
        if (talent == ADVENTURERS_INTUITION && hero.pointsInTalent(ADVENTURERS_INTUITION) == 2) {
            if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.weapon().identify();
            }
            if (hero.belongings.secondWep() != null && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.secondWep().identify();
            }
        }
        if (talent == PROTECTIVE_SHADOWS && hero.invisible > 0) {
            Buff.affect(hero, ProtectiveShadowsTracker.class);
        }
        if (talent == LIGHT_CLOAK && hero.heroClass == HeroClass.ROGUE) {
            for (Item item : Dungeon.hero.belongings.backpack) {
                if (!(item instanceof CloakOfShadows) || hero.belongings.lostInventory() && !item.keptThroughLostInventory()) continue;
                ((CloakOfShadows)item).activate(Dungeon.hero);
            }
        }
        if (talent == HEIGHTENED_SENSES || talent == FARSIGHT || talent == DIVINE_SENSE) {
            Dungeon.observe();
        }
        if (talent == TWIN_UPGRADES || talent == DESPERATE_POWER || talent == STRONGMAN || talent == DURABLE_PROJECTILES) {
            Item.updateQuickslot();
        }
        if (talent == UNENCUMBERED_SPIRIT && hero.pointsInTalent(talent) == 3) {
            Item toGive = new ClothArmor().identify();
            if (!toGive.collect()) {
                Dungeon.level.drop((Item)toGive, (int)hero.pos).sprite.drop();
            }
            if (!(toGive = new Gloves().identify()).collect()) {
                Dungeon.level.drop((Item)toGive, (int)hero.pos).sprite.drop();
            }
        }
        if (talent == LIGHT_READING && hero.heroClass == HeroClass.CLERIC) {
            for (Item item : Dungeon.hero.belongings.backpack) {
                if (!(item instanceof HolyTome) || hero.belongings.lostInventory() && !item.keptThroughLostInventory()) continue;
                ((HolyTome)item).activate(Dungeon.hero);
            }
        }
        if (talent == SPIRIT_FORM) {
            Dungeon.hero.updateHT(false);
        }
    }

    public static void onFoodEaten(Hero hero, float foodVal, Item foodSource) {
        if (hero.hasTalent(HEARTY_MEAL) && (float)hero.HP / (float)hero.HT < 0.334f) {
            int healing = 2 + 2 * hero.pointsInTalent(HEARTY_MEAL);
            hero.HP = Math.min(hero.HP + healing, hero.HT);
            hero.sprite.showStatusWithIcon(65280, Integer.toString(healing), FloatingText.HEALING, new Object[0]);
        }
        if (hero.hasTalent(IRON_STOMACH) && hero.cooldown() > 0.0f) {
            Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
        }
        if (hero.hasTalent(EMPOWERING_MEAL)) {
            Buff.affect(hero, WandEmpower.class).set(1 + hero.pointsInTalent(EMPOWERING_MEAL), 3);
            ScrollOfRecharging.charge(hero);
        }
        int wandChargeTurns = 0;
        if (hero.hasTalent(ENERGIZING_MEAL)) {
            wandChargeTurns += 2 + 3 * hero.pointsInTalent(ENERGIZING_MEAL);
        }
        int artifactChargeTurns = 0;
        if (hero.hasTalent(MYSTICAL_MEAL)) {
            artifactChargeTurns += 1 + 2 * hero.pointsInTalent(MYSTICAL_MEAL);
        }
        if (hero.hasTalent(INVIGORATING_MEAL)) {
            Buff.prolong(hero, Haste.class, 0.67f + (float)hero.pointsInTalent(INVIGORATING_MEAL));
        }
        if (hero.hasTalent(STRENGTHENING_MEAL)) {
            Buff.affect(hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(STRENGTHENING_MEAL));
        }
        if (hero.hasTalent(FOCUSED_MEAL)) {
            if (hero.heroClass == HeroClass.DUELIST) {
                Buff.affect(hero, MeleeWeapon.Charger.class).gainCharge((float)(hero.pointsInTalent(FOCUSED_MEAL) + 1) / 3.0f);
                ScrollOfRecharging.charge(hero);
            } else {
                Buff.affect(hero, PhysicalEmpower.class).set(Math.round((float)hero.lvl / (4.0f - (float)hero.pointsInTalent(FOCUSED_MEAL))), 1);
            }
        }
        if (hero.hasTalent(SATIATED_SPELLS)) {
            if (hero.heroClass == HeroClass.CLERIC) {
                Buff.affect(hero, SatiatedSpellsTracker.class);
            } else {
                int amount = 1 + 2 * hero.pointsInTalent(SATIATED_SPELLS);
                Barrier b = Buff.affect(hero, Barrier.class);
                if (b.shielding() <= amount) {
                    b.setShield(amount);
                    b.delay(Math.max(10.0f - b.cooldown(), 0.0f));
                }
            }
        }
        if (hero.hasTalent(ENLIGHTENING_MEAL)) {
            if (hero.heroClass == HeroClass.CLERIC) {
                HolyTome tome = hero.belongings.getItem(HolyTome.class);
                if (tome != null) {
                    tome.directCharge((float)(1 + hero.pointsInTalent(ENLIGHTENING_MEAL)) / 3.0f);
                    ScrollOfRecharging.charge(hero);
                }
            } else {
                wandChargeTurns += 1 + hero.pointsInTalent(ENLIGHTENING_MEAL);
                artifactChargeTurns += 1 + hero.pointsInTalent(ENLIGHTENING_MEAL);
            }
        }
        if (wandChargeTurns > 0) {
            Buff.prolong(hero, Recharging.class, wandChargeTurns);
            ScrollOfRecharging.charge(hero);
            SpellSprite.show(hero, 2);
        }
        if (artifactChargeTurns > 0) {
            ArtifactRecharge buff = Buff.affect(hero, ArtifactRecharge.class);
            if (buff.left() < (float)artifactChargeTurns) {
                buff.set((float)((float)artifactChargeTurns)).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
            }
            ScrollOfRecharging.charge(hero);
            SpellSprite.show(hero, 2, 0.0f, 1.0f, 1.0f);
        }
    }

    public static float itemIDSpeedFactor(Hero hero, Item item) {
        float factor = 1.0f;
        if (item instanceof MeleeWeapon) {
            factor *= 1.0f + 1.5f * (float)hero.pointsInTalent(ADVENTURERS_INTUITION);
            factor *= 1.0f + 0.75f * (float)hero.pointsInTalent(VETERANS_INTUITION);
        }
        if (item instanceof Armor) {
            factor *= 1.0f + 0.75f * (float)hero.pointsInTalent(ADVENTURERS_INTUITION);
            factor *= 1.0f + (float)hero.pointsInTalent(VETERANS_INTUITION);
        }
        if (item instanceof Wand) {
            factor *= 1.0f + 2.0f * (float)hero.pointsInTalent(SCHOLARS_INTUITION);
        }
        if (item instanceof MissileWeapon) {
            factor *= 1.0f + 2.0f * (float)hero.pointsInTalent(SURVIVALISTS_INTUITION);
        }
        if (item instanceof Ring) {
            factor *= 1.0f + (float)hero.pointsInTalent(THIEFS_INTUITION);
        }
        return factor;
    }

    public static void onPotionUsed(Hero hero, int cell, float factor) {
        if (hero.hasTalent(LIQUID_WILLPOWER)) {
            int shieldToGive = Math.round(factor * (float)hero.HT * (0.03f + 0.035f * (float)hero.pointsInTalent(LIQUID_WILLPOWER)));
            hero.sprite.showStatusWithIcon(65280, Integer.toString(shieldToGive), FloatingText.SHIELDING, new Object[0]);
            Buff.affect(hero, Barrier.class).setShield(shieldToGive);
        }
        if (hero.hasTalent(LIQUID_NATURE)) {
            ArrayList<Integer> grassCells = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS9) {
                grassCells.add(cell + i);
            }
            Random.shuffle(grassCells);
            Object object = grassCells.iterator();
            while (object.hasNext()) {
                int grassCell = (Integer)object.next();
                Char ch = Actor.findChar(grassCell);
                if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                    Buff.affect(ch, Roots.class, factor * (float)hero.pointsInTalent(LIQUID_NATURE));
                }
                if (Dungeon.level.map[grassCell] == 1 || Dungeon.level.map[grassCell] == 9 || Dungeon.level.map[grassCell] == 20) {
                    Level.set(grassCell, 2);
                    GameScene.updateMap(grassCell);
                }
                CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            }
            int totalGrassCells = (int)(factor * (float)(2 + 2 * hero.pointsInTalent(LIQUID_NATURE)));
            while (grassCells.size() > totalGrassCells) {
                grassCells.remove(0);
            }
            Iterator iterator = grassCells.iterator();
            while (iterator.hasNext()) {
                int grassCell = (Integer)iterator.next();
                int t = Dungeon.level.map[grassCell];
                if (t != 1 && t != 20 && t != 9 && t != 2 && t != 30 || Dungeon.level.plants.get(grassCell) != null) continue;
                Level.set(grassCell, 15);
                GameScene.updateMap(grassCell);
            }
            Dungeon.observe();
        }
        if (hero.hasTalent(LIQUID_AGILITY)) {
            Buff.prolong(hero, LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0.0f, factor - 1.0f));
            if (factor >= 0.5f) {
                Buff.prolong((Char)hero, LiquidAgilACCTracker.class, (float)5.0f).uses = Math.round(factor);
            }
        }
    }

    public static void onScrollUsed(Hero hero, int pos, float factor, Class<? extends Item> cls) {
        if (hero.hasTalent(INSCRIBED_POWER)) {
            Buff.affect(hero, ScrollEmpower.class).reset((int)(factor * (float)(1 + hero.pointsInTalent(INSCRIBED_POWER))));
        }
        if (hero.hasTalent(INSCRIBED_STEALTH)) {
            Buff.affect(hero, Invisibility.class, factor * (float)(1 + 2 * hero.pointsInTalent(INSCRIBED_STEALTH)));
            Sample.INSTANCE.play("sounds/meld.mp3");
        }
        if (hero.hasTalent(RECALL_INSCRIPTION) && Scroll.class.isAssignableFrom(cls) && cls != ScrollOfUpgrade.class) {
            if (hero.heroClass == HeroClass.CLERIC) {
                Buff.prolong((Char)hero, RecallInscription.UsedItemTracker.class, (float)(hero.pointsInTalent((Talent)Talent.RECALL_INSCRIPTION) == 2 ? 300.0f : 10.0f)).item = cls;
            } else if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)) {
                Reflection.newInstance(cls).collect();
                GLog.p(Messages.get(Talent.class, RECALL_INSCRIPTION.name() + ".refunded", new Object[0]), new Object[0]);
            }
        }
    }

    public static void onRunestoneUsed(Hero hero, int pos, Class<? extends Item> cls) {
        if (hero.hasTalent(RECALL_INSCRIPTION) && Runestone.class.isAssignableFrom(cls)) {
            if (hero.heroClass == HeroClass.CLERIC) {
                Buff.prolong((Char)hero, RecallInscription.UsedItemTracker.class, (float)(hero.pointsInTalent((Talent)Talent.RECALL_INSCRIPTION) == 2 ? 300.0f : 10.0f)).item = cls;
            } else {
                if (cls.equals(StoneOfIntuition.class) && hero.buff(StoneOfIntuition.IntuitionUseTracker.class) != null) {
                    return;
                }
                if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)) {
                    Reflection.newInstance(cls).collect();
                    GLog.p(Messages.get(Talent.class, RECALL_INSCRIPTION.name() + ".refunded", new Object[0]), new Object[0]);
                }
            }
        }
    }

    public static void onArtifactUsed(Hero hero) {
        if (hero.hasTalent(ENHANCED_RINGS)) {
            Buff.prolong(hero, EnhancedRings.class, 3.0f * (float)hero.pointsInTalent(ENHANCED_RINGS));
        }
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(DIVINE_SENSE)) {
            Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown() + 1.0f);
        }
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(CLEANSE) && Random.Int(10) < Dungeon.hero.pointsInTalent(CLEANSE)) {
            boolean removed = false;
            for (Buff b : Dungeon.hero.buffs()) {
                if (b.type != Buff.buffType.NEGATIVE || b instanceof LostInventory) continue;
                b.detach();
                removed = true;
            }
            if (removed && Dungeon.hero.sprite != null) {
                new Flare(6, 32.0f).color(16731346, true).show(Dungeon.hero.sprite, 2.0f);
            }
        }
    }

    public static void onItemEquipped(Hero hero, Item item) {
        boolean identify = false;
        if (hero.pointsInTalent(VETERANS_INTUITION) == 2 && item instanceof Armor) {
            identify = true;
        }
        if (hero.hasTalent(THIEFS_INTUITION) && item instanceof Ring) {
            if (hero.pointsInTalent(THIEFS_INTUITION) == 2) {
                identify = true;
            }
            ((Ring)item).setKnown();
        }
        if (hero.pointsInTalent(ADVENTURERS_INTUITION) == 2 && item instanceof Weapon) {
            identify = true;
        }
        if (identify) {
            if (ShardOfOblivion.passiveIDDisabled()) {
                if (item instanceof Weapon) {
                    ((Weapon)item).setIDReady();
                } else if (item instanceof Armor) {
                    ((Armor)item).setIDReady();
                } else if (item instanceof Ring) {
                    ((Ring)item).setIDReady();
                }
            } else {
                item.identify();
            }
        }
    }

    public static void onItemCollected(Hero hero, Item item) {
        if (hero.pointsInTalent(THIEFS_INTUITION) == 2 && item instanceof Ring) {
            ((Ring)item).setKnown();
        }
    }

    public static int onAttackProc(Hero hero, Char enemy, int dmg) {
        if (hero.hasTalent(PROVOKED_ANGER) && hero.buff(ProvokedAngerTracker.class) != null) {
            dmg += 1 + 2 * hero.pointsInTalent(PROVOKED_ANGER);
            hero.buff(ProvokedAngerTracker.class).detach();
        }
        if (hero.hasTalent(LINGERING_MAGIC) && hero.buff(LingeringMagicTracker.class) != null) {
            dmg += Random.IntRange(hero.pointsInTalent(LINGERING_MAGIC), 2);
            hero.buff(LingeringMagicTracker.class).detach();
        }
        if (hero.hasTalent(SUCKER_PUNCH) && enemy instanceof Mob && ((Mob)enemy).surprisedBy(hero) && enemy.buff(SuckerPunchTracker.class) == null) {
            dmg += Random.IntRange(hero.pointsInTalent(SUCKER_PUNCH), 2);
            Buff.affect(enemy, SuckerPunchTracker.class);
        }
        if (hero.hasTalent(FOLLOWUP_STRIKE) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
            if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
                Buff.prolong((Char)hero, FollowupStrikeTracker.class, (float)5.0f).object = enemy.id();
            } else if (hero.buff(FollowupStrikeTracker.class) != null && hero.buff(FollowupStrikeTracker.class).object == enemy.id()) {
                dmg += 1 + hero.pointsInTalent(FOLLOWUP_STRIKE);
                hero.buff(FollowupStrikeTracker.class).detach();
            }
        }
        if (hero.buff(SpiritBladesTracker.class) != null && Random.Int(10) < 3 * hero.pointsInTalent(SPIRIT_BLADES)) {
            SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
            if (bow != null) {
                dmg = bow.proc(hero, enemy, dmg);
            }
            hero.buff(SpiritBladesTracker.class).detach();
        }
        if (hero.hasTalent(PATIENT_STRIKE) && hero.buff(PatientStrikeTracker.class) != null && !(hero.belongings.attackingWeapon() instanceof MissileWeapon)) {
            hero.buff(PatientStrikeTracker.class).detach();
            dmg += Random.IntRange(hero.pointsInTalent(PATIENT_STRIKE), 2);
        }
        if (hero.hasTalent(DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
            if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
                if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
                    Buff.prolong((Char)hero, DeadlyFollowupTracker.class, (float)5.0f).object = enemy.id();
                }
            } else if (hero.buff(DeadlyFollowupTracker.class) != null && hero.buff(DeadlyFollowupTracker.class).object == enemy.id()) {
                dmg = Math.round((float)dmg * (1.0f + 0.1f * (float)hero.pointsInTalent(DEADLY_FOLLOWUP)));
            }
        }
        return dmg;
    }

    public static void initClassTalents(Hero hero) {
        Talent.initClassTalents(hero.heroClass, hero.talents, hero.metamorphedTalents);
    }

    public static void initClassTalents(HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        Talent.initClassTalents(cls, talents, new LinkedHashMap<Talent, Talent>());
    }

    public static void initClassTalents(HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements) {
        while (talents.size() < 4) {
            talents.add(new LinkedHashMap());
        }
        ArrayList tierTalents = new ArrayList();
        switch (cls) {
            default: {
                Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, PROVOKED_ANGER, IRON_WILL);
                break;
            }
            case MAGE: {
                Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, LINGERING_MAGIC, BACKUP_BARRIER);
                break;
            }
            case ROGUE: {
                Collections.addAll(tierTalents, CACHED_RATIONS, THIEFS_INTUITION, SUCKER_PUNCH, PROTECTIVE_SHADOWS);
                break;
            }
            case HUNTRESS: {
                Collections.addAll(tierTalents, NATURES_BOUNTY, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID);
                break;
            }
            case DUELIST: {
                Collections.addAll(tierTalents, STRENGTHENING_MEAL, ADVENTURERS_INTUITION, PATIENT_STRIKE, AGGRESSIVE_BARRIER);
                break;
            }
            case CLERIC: {
                Collections.addAll(tierTalents, SATIATED_SPELLS, HOLY_INTUITION, SEARING_LIGHT, SHIELD_OF_LIGHT);
            }
        }
        for (Talent talent : tierTalents) {
            if (replacements.containsKey((Object)talent)) {
                talent = replacements.get((Object)talent);
            }
            talents.get(0).put(talent, 0);
        }
        tierTalents.clear();
        switch (cls) {
            default: {
                Collections.addAll(tierTalents, IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
                break;
            }
            case MAGE: {
                Collections.addAll(tierTalents, ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
                break;
            }
            case ROGUE: {
                Collections.addAll(tierTalents, MYSTICAL_MEAL, INSCRIBED_STEALTH, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT);
                break;
            }
            case HUNTRESS: {
                Collections.addAll(tierTalents, INVIGORATING_MEAL, LIQUID_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES);
                break;
            }
            case DUELIST: {
                Collections.addAll(tierTalents, FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP);
                break;
            }
            case CLERIC: {
                Collections.addAll(tierTalents, ENLIGHTENING_MEAL, RECALL_INSCRIPTION, SUNRAY, DIVINE_SENSE, BLESS);
            }
        }
        for (Talent talent : tierTalents) {
            if (replacements.containsKey((Object)talent)) {
                talent = replacements.get((Object)talent);
            }
            talents.get(1).put(talent, 0);
        }
        tierTalents.clear();
        switch (cls) {
            default: {
                Collections.addAll(tierTalents, HOLD_FAST, STRONGMAN);
                break;
            }
            case MAGE: {
                Collections.addAll(tierTalents, DESPERATE_POWER, ALLY_WARP);
                break;
            }
            case ROGUE: {
                Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK);
                break;
            }
            case HUNTRESS: {
                Collections.addAll(tierTalents, POINT_BLANK, SEER_SHOT);
                break;
            }
            case DUELIST: {
                Collections.addAll(tierTalents, PRECISE_ASSAULT, DEADLY_FOLLOWUP);
                break;
            }
            case CLERIC: {
                Collections.addAll(tierTalents, CLEANSE, LIGHT_READING);
            }
        }
        for (Talent talent : tierTalents) {
            if (replacements.containsKey((Object)talent)) {
                talent = replacements.get((Object)talent);
            }
            talents.get(2).put(talent, 0);
        }
        tierTalents.clear();
    }

    public static void initSubclassTalents(Hero hero) {
        Talent.initSubclassTalents(hero.subClass, hero.talents);
    }

    public static void initSubclassTalents(HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        if (cls == HeroSubClass.NONE) {
            return;
        }
        while (talents.size() < 4) {
            talents.add(new LinkedHashMap());
        }
        ArrayList tierTalents = new ArrayList();
        switch (cls) {
            default: {
                Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST);
                break;
            }
            case GLADIATOR: {
                Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO);
                break;
            }
            case BATTLEMAGE: {
                Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE);
                break;
            }
            case WARLOCK: {
                Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS);
                break;
            }
            case ASSASSIN: {
                Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER);
                break;
            }
            case FREERUNNER: {
                Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH);
                break;
            }
            case SNIPER: {
                Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES);
                break;
            }
            case WARDEN: {
                Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, SHIELDING_DEW);
                break;
            }
            case CHAMPION: {
                Collections.addAll(tierTalents, VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY);
                break;
            }
            case MONK: {
                Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY);
                break;
            }
            case PRIEST: {
                Collections.addAll(tierTalents, HOLY_LANCE, HALLOWED_GROUND, MNEMONIC_PRAYER);
                break;
            }
            case PALADIN: {
                Collections.addAll(tierTalents, LAY_ON_HANDS, AURA_OF_PROTECTION, WALL_OF_LIGHT);
            }
        }
        for (Talent talent : tierTalents) {
            talents.get(2).put(talent, 0);
        }
        tierTalents.clear();
    }

    public static void initArmorTalents(Hero hero) {
        Talent.initArmorTalents(hero.armorAbility, hero.talents);
    }

    public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        if (abil == null) {
            return;
        }
        while (talents.size() < 4) {
            talents.add(new LinkedHashMap());
        }
        for (Talent t : abil.talents()) {
            talents.get(3).put(t, 0);
        }
    }

    public static void storeTalentsInBundle(Bundle bundle, Hero hero) {
        for (int i = 0; i < 4; ++i) {
            LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
            Bundle tierBundle = new Bundle();
            for (Talent talent : tier.keySet()) {
                if (tier.get((Object)talent) > 0) {
                    tierBundle.put(talent.name(), tier.get((Object)talent));
                }
                if (!tierBundle.contains(talent.name())) continue;
                tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
            }
            bundle.put(TALENT_TIER + (i + 1), tierBundle);
        }
        Bundle replacementsBundle = new Bundle();
        for (Talent t : hero.metamorphedTalents.keySet()) {
            replacementsBundle.put(t.name(), hero.metamorphedTalents.get((Object)t));
        }
        bundle.put("replacements", replacementsBundle);
    }

    public static void restoreTalentsFromBundle(Bundle bundle, Hero hero) {
        if (bundle.contains("replacements")) {
            Bundle replacements = bundle.getBundle("replacements");
            for (String key : replacements.getKeys()) {
                String value = replacements.getString(key);
                if (renamedTalents.containsKey(key)) {
                    key = renamedTalents.get(key);
                }
                if (renamedTalents.containsKey(value)) {
                    value = renamedTalents.get(value);
                }
                if (removedTalents.contains(key) || removedTalents.contains(value)) continue;
                try {
                    hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
                }
                catch (Exception e) {
                    ShatteredPixelDungeon.reportException(e);
                }
            }
        }
        if (hero.heroClass != null) {
            Talent.initClassTalents(hero);
        }
        if (hero.subClass != null) {
            Talent.initSubclassTalents(hero);
        }
        if (hero.armorAbility != null) {
            Talent.initArmorTalents(hero);
        }
        for (int i = 0; i < 4; ++i) {
            Bundle tierBundle;
            LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
            Bundle bundle2 = tierBundle = bundle.contains(TALENT_TIER + (i + 1)) ? bundle.getBundle(TALENT_TIER + (i + 1)) : null;
            if (tierBundle == null) continue;
            for (String tName : tierBundle.getKeys()) {
                int points = tierBundle.getInt(tName);
                if (renamedTalents.containsKey(tName)) {
                    tName = renamedTalents.get(tName);
                }
                if (removedTalents.contains(tName)) continue;
                try {
                    Talent talent = Talent.valueOf(tName);
                    if (!tier.containsKey((Object)talent)) continue;
                    tier.put(talent, Math.min(points, talent.maxPoints()));
                }
                catch (Exception e) {
                    ShatteredPixelDungeon.reportException(e);
                }
            }
        }
    }

    static {
        tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};
        removedTalents = new HashSet();
        renamedTalents = new HashMap();
    }

    public static class ProtectiveShadowsTracker
    extends Buff {
        float barrierInc = 0.5f;
        private static final String BARRIER_INC = "barrier_inc";

        @Override
        public boolean act() {
            if (((Hero)this.target).hasTalent(PROTECTIVE_SHADOWS) && this.target.invisible > 0) {
                Barrier barrier = Buff.affect(this.target, Barrier.class);
                if (barrier.shielding() < 1 + 2 * ((Hero)this.target).pointsInTalent(PROTECTIVE_SHADOWS)) {
                    this.barrierInc += 0.5f * (float)((Hero)this.target).pointsInTalent(PROTECTIVE_SHADOWS);
                }
                if (this.barrierInc >= 1.0f) {
                    this.barrierInc = 0.0f;
                    barrier.incShield(1);
                } else {
                    barrier.incShield(0);
                }
            } else {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BARRIER_INC, this.barrierInc);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.barrierInc = bundle.getFloat(BARRIER_INC);
        }
    }

    public static class WarriorFoodImmunity
    extends FlavourBuff {
        public WarriorFoodImmunity() {
            this.actPriority = 1;
        }
    }

    public static class SatiatedSpellsTracker
    extends Buff {
        @Override
        public int icon() {
            return 75;
        }
    }

    public static class LiquidAgilEVATracker
    extends FlavourBuff {
        public LiquidAgilEVATracker() {
            this.actPriority = 1;
        }
    }

    public static class LiquidAgilACCTracker
    extends FlavourBuff {
        public int uses;
        private static final String USES = "uses";

        public LiquidAgilACCTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 57;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 0.0f, 1.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, 1.0f - this.visualcooldown() / 5.0f);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(USES, this.uses);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.uses = bundle.getInt(USES);
        }
    }

    public static class ProvokedAngerTracker
    extends FlavourBuff {
        public ProvokedAngerTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 45;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.43f, 1.43f, 1.43f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, 1.0f - this.visualcooldown() / 5.0f);
        }
    }

    public static class LingeringMagicTracker
    extends FlavourBuff {
        public LingeringMagicTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 45;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.43f, 1.43f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, 1.0f - this.visualcooldown() / 5.0f);
        }
    }

    public static class SuckerPunchTracker
    extends Buff {
    }

    public static class FollowupStrikeTracker
    extends FlavourBuff {
        public int object;
        private static final String OBJECT = "object";

        public FollowupStrikeTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 57;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.0f, 0.75f, 1.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, 1.0f - this.visualcooldown() / 5.0f);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(OBJECT, this.object);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.object = bundle.getInt(OBJECT);
        }
    }

    public static class SpiritBladesTracker
    extends FlavourBuff {
    }

    public static class PatientStrikeTracker
    extends Buff {
        public int pos;
        private static final String POS = "pos";

        public PatientStrikeTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 0.0f, 1.0f);
        }

        @Override
        public boolean act() {
            if (this.pos != this.target.pos) {
                this.detach();
            } else {
                this.spend(1.0f);
            }
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, this.pos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.pos = bundle.getInt(POS);
        }
    }

    public static class DeadlyFollowupTracker
    extends FlavourBuff {
        public int object;
        private static final String OBJECT = "object";

        public DeadlyFollowupTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 57;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 0.0f, 1.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, 1.0f - this.visualcooldown() / 5.0f);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(OBJECT, this.object);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.object = bundle.getInt(OBJECT);
        }
    }

    public static class NatureBerriesDropped
    extends CounterBuff {
        public NatureBerriesDropped() {
            this.revivePersists = true;
        }
    }

    public static class CachedRationsDropped
    extends CounterBuff {
        public CachedRationsDropped() {
            this.revivePersists = true;
        }
    }

    public static class SearingLightCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.0f, 0.0f, 1.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, this.visualcooldown() / 20.0f);
        }
    }

    public static class CounterAbilityTacker
    extends FlavourBuff {
    }

    public static class CombinedEnergyAbilityTracker
    extends FlavourBuff {
        public boolean monkAbilused = false;
        public boolean wepAbilUsed = false;
        private static final String MONK_ABIL_USED = "monk_abil_used";
        private static final String WEP_ABIL_USED = "wep_abil_used";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(MONK_ABIL_USED, this.monkAbilused);
            bundle.put(WEP_ABIL_USED, this.wepAbilUsed);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
            this.wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
        }
    }

    public static class CombinedLethalityAbilityTracker
    extends FlavourBuff {
        public MeleeWeapon weapon;
    }

    public static class VariedChargeTracker
    extends Buff {
        public Class weapon;
        private static final String WEAPON = "weapon";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(WEAPON, this.weapon);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.weapon = bundle.getClass(WEAPON);
        }
    }

    public static class PreciseAssaultTracker
    extends FlavourBuff {
        public PreciseAssaultTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 57;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 1.0f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, 1.0f - this.visualcooldown() / 5.0f);
        }
    }

    public static class SwiftEquipCooldown
    extends FlavourBuff {
        public boolean secondUse;
        private static final String SECOND_USE = "second_use";

        public boolean hasSecondUse() {
            return this.secondUse;
        }

        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            if (this.hasSecondUse()) {
                icon.hardlight(0.85f, 0.0f, 1.0f);
            } else {
                icon.hardlight(0.35f, 0.0f, 0.7f);
            }
        }

        @Override
        public float iconFadePercent() {
            return GameMath.gate(0.0f, this.visualcooldown() / 20.0f, 1.0f);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SECOND_USE, this.secondUse);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.secondUse = bundle.getBoolean(SECOND_USE);
        }
    }

    public static class LethalHasteCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.35f, 0.0f, 0.7f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, this.visualcooldown() / 100.0f);
        }
    }

    public static class AggressiveBarrierCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.35f, 0.0f, 0.7f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, this.visualcooldown() / 50.0f);
        }
    }

    public static class SeerShotCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return this.target.buff(RevealedArea.class) != null ? 127 : 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.7f, 0.4f, 0.7f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, this.visualcooldown() / 20.0f);
        }
    }

    public static class RejuvenatingStepsFurrow
    extends CounterBuff {
        public RejuvenatingStepsFurrow() {
            this.revivePersists = true;
        }
    }

    public static class RejuvenatingStepsCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.0f, 0.35f, 0.15f);
        }

        @Override
        public float iconFadePercent() {
            return GameMath.gate(0.0f, this.visualcooldown() / (float)(15 - 5 * Dungeon.hero.pointsInTalent(REJUVENATING_STEPS)), 1.0f);
        }
    }

    public static class BountyHunterTracker
    extends FlavourBuff {
    }

    public static class EmpoweredStrikeTracker
    extends FlavourBuff {
        public boolean delayedDetach = false;
    }

    public static class WandPreservationCounter
    extends CounterBuff {
        public WandPreservationCounter() {
            this.revivePersists = true;
        }
    }

    public static class StrikingWaveTracker
    extends FlavourBuff {
    }

    public static class LethalMomentumTracker
    extends FlavourBuff {
    }

    public static class ImprovisedProjectileCooldown
    extends FlavourBuff {
        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.15f, 0.2f, 0.5f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, this.visualcooldown() / 50.0f);
        }
    }
}

