/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class Badges {
    private static HashSet<Badge> global;
    private static HashSet<Badge> local;
    private static boolean saveNeeded;
    public static final String BADGES_FILE = "badges.dat";
    private static final String BADGES = "badges";
    private static final HashSet<String> removedBadges;
    private static final HashMap<String, String> renamedBadges;
    private static LinkedHashMap<HeroClass, Badge> firstBossClassBadges;
    private static LinkedHashMap<HeroClass, Badge> victoryClassBadges;
    private static LinkedHashMap<HeroSubClass, Badge> thirdBossSubclassBadges;
    private static final Badge[][] tierBadgeReplacements;
    private static final Badge[][] prerequisiteBadges;
    private static final Badge[][] summaryBadgeReplacements;

    public static void reset() {
        local.clear();
        Badges.loadGlobal();
    }

    public static HashSet<Badge> restore(Bundle bundle) {
        HashSet<Badge> badges = new HashSet<Badge>();
        if (bundle == null) {
            return badges;
        }
        String[] names = bundle.getStringArray(BADGES);
        if (names == null) {
            return badges;
        }
        for (int i = 0; i < names.length; ++i) {
            try {
                if (renamedBadges.containsKey(names[i])) {
                    names[i] = renamedBadges.get(names[i]);
                }
                if (removedBadges.contains(names[i])) continue;
                badges.add(Badge.valueOf(names[i]));
                continue;
            }
            catch (Exception e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }
        Badges.addReplacedBadges(badges);
        return badges;
    }

    public static void store(Bundle bundle, HashSet<Badge> badges) {
        Badges.addReplacedBadges(badges);
        int count = 0;
        String[] names = new String[badges.size()];
        for (Badge badge : badges) {
            names[count++] = badge.name();
        }
        bundle.put(BADGES, names);
    }

    public static void loadLocal(Bundle bundle) {
        local = Badges.restore(bundle);
    }

    public static void saveLocal(Bundle bundle) {
        Badges.store(bundle, local);
    }

    public static void loadGlobal() {
        if (global == null) {
            try {
                Bundle bundle = FileUtils.bundleFromFile(BADGES_FILE);
                global = Badges.restore(bundle);
            }
            catch (IOException e) {
                global = new HashSet();
            }
        }
    }

    public static void saveGlobal() {
        Badges.saveGlobal(false);
    }

    public static void saveGlobal(boolean force) {
        if (saveNeeded || force) {
            Bundle bundle = new Bundle();
            Badges.store(bundle, global);
            try {
                FileUtils.bundleToFile(BADGES_FILE, bundle);
                saveNeeded = false;
            }
            catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }
    }

    public static int totalUnlocked(boolean global) {
        if (global) {
            return Badges.global.size();
        }
        return local.size();
    }

    public static void validateMonstersSlain() {
        Badge badge = null;
        if (!local.contains((Object)Badge.MONSTERS_SLAIN_1) && Statistics.enemiesSlain >= 10) {
            badge = Badge.MONSTERS_SLAIN_1;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.MONSTERS_SLAIN_2) && Statistics.enemiesSlain >= 50) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.MONSTERS_SLAIN_2;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.MONSTERS_SLAIN_3) && Statistics.enemiesSlain >= 100) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.MONSTERS_SLAIN_3;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.MONSTERS_SLAIN_4) && Statistics.enemiesSlain >= 250) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.MONSTERS_SLAIN_4;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.MONSTERS_SLAIN_5) && Statistics.enemiesSlain >= 500) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.MONSTERS_SLAIN_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateGoldCollected() {
        Badge badge = null;
        if (!local.contains((Object)Badge.GOLD_COLLECTED_1) && Statistics.goldCollected >= 250) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.GOLD_COLLECTED_1;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.GOLD_COLLECTED_2) && Statistics.goldCollected >= 1000) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.GOLD_COLLECTED_2;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.GOLD_COLLECTED_3) && Statistics.goldCollected >= 2500) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.GOLD_COLLECTED_3;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.GOLD_COLLECTED_4) && Statistics.goldCollected >= 7500) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.GOLD_COLLECTED_4;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.GOLD_COLLECTED_5) && Statistics.goldCollected >= 15000) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.GOLD_COLLECTED_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateLevelReached() {
        Badge badge = null;
        if (!local.contains((Object)Badge.LEVEL_REACHED_1) && Dungeon.hero.lvl >= 6) {
            badge = Badge.LEVEL_REACHED_1;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.LEVEL_REACHED_2) && Dungeon.hero.lvl >= 12) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.LEVEL_REACHED_2;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.LEVEL_REACHED_3) && Dungeon.hero.lvl >= 18) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.LEVEL_REACHED_3;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.LEVEL_REACHED_4) && Dungeon.hero.lvl >= 24) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.LEVEL_REACHED_4;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.LEVEL_REACHED_5) && Dungeon.hero.lvl >= 30) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.LEVEL_REACHED_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateStrengthAttained() {
        Badge badge = null;
        if (!local.contains((Object)Badge.STRENGTH_ATTAINED_1) && Dungeon.hero.STR >= 12) {
            badge = Badge.STRENGTH_ATTAINED_1;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.STRENGTH_ATTAINED_2) && Dungeon.hero.STR >= 14) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.STRENGTH_ATTAINED_2;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.STRENGTH_ATTAINED_3) && Dungeon.hero.STR >= 16) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.STRENGTH_ATTAINED_3;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.STRENGTH_ATTAINED_4) && Dungeon.hero.STR >= 18) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.STRENGTH_ATTAINED_4;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.STRENGTH_ATTAINED_5) && Dungeon.hero.STR >= 20) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.STRENGTH_ATTAINED_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateFoodEaten() {
        Badge badge = null;
        if (!local.contains((Object)Badge.FOOD_EATEN_1) && Statistics.foodEaten >= 10) {
            badge = Badge.FOOD_EATEN_1;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.FOOD_EATEN_2) && Statistics.foodEaten >= 20) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.FOOD_EATEN_2;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.FOOD_EATEN_3) && Statistics.foodEaten >= 30) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.FOOD_EATEN_3;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.FOOD_EATEN_4) && Statistics.foodEaten >= 40) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.FOOD_EATEN_4;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.FOOD_EATEN_5) && Statistics.foodEaten >= 50) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.FOOD_EATEN_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateItemsCrafted() {
        Badge badge = null;
        if (!local.contains((Object)Badge.ITEMS_CRAFTED_1) && Statistics.itemsCrafted >= 3) {
            badge = Badge.ITEMS_CRAFTED_1;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEMS_CRAFTED_2) && Statistics.itemsCrafted >= 8) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEMS_CRAFTED_2;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEMS_CRAFTED_3) && Statistics.itemsCrafted >= 15) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEMS_CRAFTED_3;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEMS_CRAFTED_4) && Statistics.itemsCrafted >= 24) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEMS_CRAFTED_4;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEMS_CRAFTED_5) && Statistics.itemsCrafted >= 35) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEMS_CRAFTED_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateHazardAssists() {
        if (!local.contains((Object)Badge.ENEMY_HAZARDS) && Statistics.hazardAssistedKills >= 10) {
            local.add(Badge.ENEMY_HAZARDS);
            Badges.displayBadge(Badge.ENEMY_HAZARDS);
        }
    }

    public static void validatePiranhasKilled() {
        Badge badge = null;
        if (!local.contains((Object)Badge.PIRANHAS) && Statistics.piranhasKilled >= 6) {
            badge = Badge.PIRANHAS;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateItemLevelAquired(Item item) {
        if (!item.levelKnown || item instanceof Artifact) {
            return;
        }
        if (item instanceof MeleeWeapon) {
            Badges.validateDuelistUnlock();
        }
        Badge badge = null;
        if (!local.contains((Object)Badge.ITEM_LEVEL_1) && item.level() >= 3) {
            badge = Badge.ITEM_LEVEL_1;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEM_LEVEL_2) && item.level() >= 6) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEM_LEVEL_2;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEM_LEVEL_3) && item.level() >= 9) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEM_LEVEL_3;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEM_LEVEL_4) && item.level() >= 12) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEM_LEVEL_4;
            local.add(badge);
        }
        if (!local.contains((Object)Badge.ITEM_LEVEL_5) && item.level() >= 15) {
            if (badge != null) {
                Badges.unlock(badge);
            }
            badge = Badge.ITEM_LEVEL_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateAllBagsBought(Item bag) {
        Badge badge = null;
        if (bag instanceof VelvetPouch) {
            badge = Badge.BAG_BOUGHT_VELVET_POUCH;
        } else if (bag instanceof ScrollHolder) {
            badge = Badge.BAG_BOUGHT_SCROLL_HOLDER;
        } else if (bag instanceof PotionBandolier) {
            badge = Badge.BAG_BOUGHT_POTION_BANDOLIER;
        } else if (bag instanceof MagicalHolster) {
            badge = Badge.BAG_BOUGHT_MAGICAL_HOLSTER;
        }
        if (badge != null) {
            local.add(badge);
            if (!local.contains((Object)Badge.ALL_BAGS_BOUGHT) && local.contains((Object)Badge.BAG_BOUGHT_VELVET_POUCH) && local.contains((Object)Badge.BAG_BOUGHT_SCROLL_HOLDER) && local.contains((Object)Badge.BAG_BOUGHT_POTION_BANDOLIER) && local.contains((Object)Badge.BAG_BOUGHT_MAGICAL_HOLSTER)) {
                badge = Badge.ALL_BAGS_BOUGHT;
                local.add(badge);
                Badges.displayBadge(badge);
            }
        }
    }

    public static void validateCatalogBadges() {
        int totalSeen = 0;
        int totalThings = 0;
        for (Catalog catalog : Catalog.values()) {
            totalSeen += catalog.totalSeen();
            totalThings += catalog.totalItems();
        }
        for (Enum enum_ : Bestiary.values()) {
            totalSeen += ((Bestiary)enum_).totalSeen();
            totalThings += ((Bestiary)enum_).totalEntities();
        }
        for (Enum enum_ : Document.values()) {
            if (((Document)enum_).isLoreDoc()) continue;
            for (String page : ((Document)enum_).pageNames()) {
                if (((Document)enum_).isPageFound(page)) {
                    ++totalSeen;
                }
                ++totalThings;
            }
        }
        Badge badge = null;
        if (totalSeen >= 40) {
            badge = Badge.RESEARCHER_1;
        }
        if (totalSeen >= 80) {
            Badges.unlock(badge);
            badge = Badge.RESEARCHER_2;
        }
        if (totalSeen >= 160) {
            Badges.unlock(badge);
            badge = Badge.RESEARCHER_3;
        }
        if (totalSeen >= 320) {
            Badges.unlock(badge);
            badge = Badge.RESEARCHER_4;
        }
        if (totalSeen == totalThings) {
            Badges.unlock(badge);
            badge = Badge.RESEARCHER_5;
        }
        Badges.displayBadge(badge);
        boolean qualified = true;
        for (Catalog catalog : Catalog.equipmentCatalogs) {
            if (catalog == Catalog.ENCHANTMENTS || catalog == Catalog.GLYPHS || catalog.totalSeen() != 0) continue;
            qualified = false;
            break;
        }
        if (qualified) {
            Badges.displayBadge(Badge.CATALOG_ONE_EQUIPMENT);
        }
        if (!local.contains((Object)Badge.CATALOG_POTIONS_SCROLLS) && Potion.allKnown() && Scroll.allKnown() && Dungeon.hero != null && Dungeon.hero.isAlive()) {
            local.add(Badge.CATALOG_POTIONS_SCROLLS);
            Badges.displayBadge(Badge.CATALOG_POTIONS_SCROLLS);
        }
        if (Bestiary.RARE.totalSeen() >= 10) {
            Badges.displayBadge(Badge.ALL_RARE_ENEMIES);
        }
        if (Document.HALLS_KING.isPageRead("attrition")) {
            Badges.displayBadge(Badge.RODNEY);
        }
    }

    public static void validateDeathFromFire() {
        Badge badge = Badge.DEATH_FROM_FIRE;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromPoison() {
        Badge badge = Badge.DEATH_FROM_POISON;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromGas() {
        Badge badge = Badge.DEATH_FROM_GAS;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromHunger() {
        Badge badge = Badge.DEATH_FROM_HUNGER;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromFalling() {
        Badge badge = Badge.DEATH_FROM_FALLING;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromEnemyMagic() {
        Badge badge = Badge.DEATH_FROM_ENEMY_MAGIC;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromFriendlyMagic() {
        Badge badge = Badge.DEATH_FROM_FRIENDLY_MAGIC;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromSacrifice() {
        Badge badge = Badge.DEATH_FROM_SACRIFICE;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    public static void validateDeathFromGrimOrDisintTrap() {
        Badge badge = Badge.DEATH_FROM_GRIM_TRAP;
        local.add(badge);
        Badges.displayBadge(badge);
        Badges.validateDeathFromAll();
    }

    private static void validateDeathFromAll() {
        Badge badge;
        if (Badges.isUnlocked(Badge.DEATH_FROM_FIRE) && Badges.isUnlocked(Badge.DEATH_FROM_POISON) && Badges.isUnlocked(Badge.DEATH_FROM_GAS) && Badges.isUnlocked(Badge.DEATH_FROM_HUNGER) && Badges.isUnlocked(Badge.DEATH_FROM_FALLING) && Badges.isUnlocked(Badge.DEATH_FROM_ENEMY_MAGIC) && Badges.isUnlocked(Badge.DEATH_FROM_FRIENDLY_MAGIC) && Badges.isUnlocked(Badge.DEATH_FROM_SACRIFICE) && Badges.isUnlocked(Badge.DEATH_FROM_GRIM_TRAP) && !Badges.isUnlocked(badge = Badge.DEATH_FROM_ALL)) {
            Badges.displayBadge(badge);
        }
    }

    public static void validateBossSlain() {
        Badge badge = null;
        switch (Dungeon.depth) {
            case 5: {
                badge = Badge.BOSS_SLAIN_1;
                break;
            }
            case 10: {
                badge = Badge.BOSS_SLAIN_2;
                break;
            }
            case 15: {
                badge = Badge.BOSS_SLAIN_3;
                break;
            }
            case 20: {
                badge = Badge.BOSS_SLAIN_4;
            }
        }
        if (badge != null) {
            local.add(badge);
            Badges.displayBadge(badge);
            if (badge == Badge.BOSS_SLAIN_1) {
                badge = firstBossClassBadges.get((Object)Dungeon.hero.heroClass);
                if (badge == null) {
                    return;
                }
                local.add(badge);
                Badges.unlock(badge);
                boolean allUnlocked = true;
                for (Badge b : firstBossClassBadges.values()) {
                    if (Badges.isUnlocked(b)) continue;
                    allUnlocked = false;
                    break;
                }
                if (allUnlocked && !Badges.isUnlocked(badge = Badge.BOSS_SLAIN_1_ALL_CLASSES)) {
                    Badges.displayBadge(badge);
                }
            } else if (badge == Badge.BOSS_SLAIN_3) {
                badge = thirdBossSubclassBadges.get((Object)Dungeon.hero.subClass);
                if (badge == null) {
                    return;
                }
                local.add(badge);
                Badges.unlock(badge);
                boolean allUnlocked = true;
                for (Badge b : thirdBossSubclassBadges.values()) {
                    if (Badges.isUnlocked(b)) continue;
                    allUnlocked = false;
                    break;
                }
                if (allUnlocked && !Badges.isUnlocked(badge = Badge.BOSS_SLAIN_3_ALL_SUBCLASSES)) {
                    Badges.displayBadge(badge);
                }
            }
            if (Statistics.qualifiedForBossRemainsBadge && Dungeon.hero.belongings.getItem(RemainsItem.class) != null) {
                badge = Badge.BOSS_SLAIN_REMAINS;
                local.add(badge);
                Badges.displayBadge(badge);
            }
        }
    }

    public static void validateBossChallengeCompleted() {
        Badge badge = null;
        switch (Dungeon.depth) {
            case 5: {
                badge = Badge.BOSS_CHALLENGE_1;
                break;
            }
            case 10: {
                badge = Badge.BOSS_CHALLENGE_2;
                break;
            }
            case 15: {
                badge = Badge.BOSS_CHALLENGE_3;
                break;
            }
            case 20: {
                badge = Badge.BOSS_CHALLENGE_4;
                break;
            }
            case 25: {
                badge = Badge.BOSS_CHALLENGE_5;
            }
        }
        if (badge != null) {
            local.add(badge);
            Badges.displayBadge(badge);
        }
    }

    public static void validateMastery() {
        Badge badge = null;
        switch (Dungeon.hero.heroClass) {
            case WARRIOR: {
                badge = Badge.MASTERY_WARRIOR;
                break;
            }
            case MAGE: {
                badge = Badge.MASTERY_MAGE;
                break;
            }
            case ROGUE: {
                badge = Badge.MASTERY_ROGUE;
                break;
            }
            case HUNTRESS: {
                badge = Badge.MASTERY_HUNTRESS;
                break;
            }
            case DUELIST: {
                badge = Badge.MASTERY_DUELIST;
                break;
            }
            case CLERIC: {
                badge = Badge.MASTERY_CLERIC;
            }
        }
        Badges.unlock(badge);
    }

    public static void validateRatmogrify() {
        Badges.unlock(Badge.FOUND_RATMOGRIFY);
    }

    public static void validateMageUnlock() {
        if (Statistics.upgradesUsed >= 1 && !Badges.isUnlocked(Badge.UNLOCK_MAGE)) {
            Badges.displayBadge(Badge.UNLOCK_MAGE);
        }
    }

    public static void validateRogueUnlock() {
        if (Statistics.sneakAttacks >= 10 && !Badges.isUnlocked(Badge.UNLOCK_ROGUE)) {
            Badges.displayBadge(Badge.UNLOCK_ROGUE);
        }
    }

    public static void validateHuntressUnlock() {
        if (Statistics.thrownAttacks >= 10 && !Badges.isUnlocked(Badge.UNLOCK_HUNTRESS)) {
            Badges.displayBadge(Badge.UNLOCK_HUNTRESS);
        }
    }

    public static void validateDuelistUnlock() {
        if (!Badges.isUnlocked(Badge.UNLOCK_DUELIST) && Dungeon.hero != null && Dungeon.hero.belongings.weapon instanceof MeleeWeapon && ((MeleeWeapon)Dungeon.hero.belongings.weapon).tier >= 2 && ((MeleeWeapon)Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()) {
            if (Dungeon.hero.belongings.weapon.isIdentified() && ((MeleeWeapon)Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()) {
                Badges.displayBadge(Badge.UNLOCK_DUELIST);
            } else if (!Dungeon.hero.belongings.weapon.isIdentified() && ((MeleeWeapon)Dungeon.hero.belongings.weapon).STRReq(0) <= Dungeon.hero.STR()) {
                Badges.displayBadge(Badge.UNLOCK_DUELIST);
            }
        }
    }

    public static void validateClericUnlock() {
        if (!Badges.isUnlocked(Badge.UNLOCK_CLERIC)) {
            Badges.displayBadge(Badge.UNLOCK_CLERIC);
        }
    }

    public static void validateMasteryCombo(int n) {
        if (!local.contains((Object)Badge.MASTERY_COMBO) && n == 10) {
            Badge badge = Badge.MASTERY_COMBO;
            local.add(badge);
            Badges.displayBadge(badge);
        }
    }

    public static void validateVictory() {
        Badge badge = Badge.VICTORY;
        local.add(badge);
        Badges.displayBadge(badge);
        if (Statistics.qualifiedForRandomVictoryBadge && Dungeon.hero.subClass != null && Dungeon.hero.armorAbility != null) {
            badge = Badge.VICTORY_RANDOM;
            local.add(badge);
            Badges.displayBadge(badge);
        }
        if ((badge = victoryClassBadges.get((Object)Dungeon.hero.heroClass)) == null) {
            return;
        }
        local.add(badge);
        Badges.unlock(badge);
        boolean allUnlocked = true;
        for (Badge b : victoryClassBadges.values()) {
            if (Badges.isUnlocked(b)) continue;
            allUnlocked = false;
            break;
        }
        if (allUnlocked) {
            badge = Badge.VICTORY_ALL_CLASSES;
            Badges.displayBadge(badge);
        }
    }

    public static void validateTakingTheMick(Object cause) {
        if ((cause == Dungeon.hero || cause instanceof Explosive.ExplosiveCurseBomb) && Dungeon.hero.belongings.attackingWeapon() instanceof Pickaxe && Dungeon.hero.belongings.attackingWeapon().level() >= 20) {
            local.add(Badge.TAKING_THE_MICK);
            Badges.displayBadge(Badge.TAKING_THE_MICK);
        }
    }

    public static void validateNoKilling() {
        if (!local.contains((Object)Badge.NO_MONSTERS_SLAIN) && Statistics.completedWithNoKilling) {
            Badge badge = Badge.NO_MONSTERS_SLAIN;
            local.add(badge);
            Badges.displayBadge(badge);
            Statistics.completedWithNoKilling = false;
        }
    }

    public static void validateGrimWeapon() {
        if (!local.contains((Object)Badge.GRIM_WEAPON)) {
            Badge badge = Badge.GRIM_WEAPON;
            local.add(badge);
            Badges.displayBadge(badge);
        }
    }

    public static void validateManyBuffs() {
        if (!local.contains((Object)Badge.MANY_BUFFS)) {
            Badge badge = Badge.MANY_BUFFS;
            local.add(badge);
            Badges.displayBadge(badge);
        }
    }

    public static void validateGamesPlayed() {
        Badge badge = null;
        if (Rankings.INSTANCE.totalNumber >= 10 || Rankings.INSTANCE.wonNumber >= 1) {
            badge = Badge.GAMES_PLAYED_1;
        }
        if (Rankings.INSTANCE.totalNumber >= 25 || Rankings.INSTANCE.wonNumber >= 3) {
            Badges.unlock(badge);
            badge = Badge.GAMES_PLAYED_2;
        }
        if (Rankings.INSTANCE.totalNumber >= 50 || Rankings.INSTANCE.wonNumber >= 5) {
            Badges.unlock(badge);
            badge = Badge.GAMES_PLAYED_3;
        }
        if (Rankings.INSTANCE.totalNumber >= 200 || Rankings.INSTANCE.wonNumber >= 10) {
            Badges.unlock(badge);
            badge = Badge.GAMES_PLAYED_4;
        }
        if (Rankings.INSTANCE.totalNumber >= 1000 || Rankings.INSTANCE.wonNumber >= 25) {
            Badges.unlock(badge);
            badge = Badge.GAMES_PLAYED_5;
        }
        Badges.displayBadge(badge);
    }

    public static void validateHighScore(int score) {
        Badge badge = null;
        if (score >= 5000) {
            badge = Badge.HIGH_SCORE_1;
            local.add(badge);
        }
        if (score >= 25000) {
            Badges.unlock(badge);
            badge = Badge.HIGH_SCORE_2;
            local.add(badge);
        }
        if (score >= 100000) {
            Badges.unlock(badge);
            badge = Badge.HIGH_SCORE_3;
            local.add(badge);
        }
        if (score >= 250000) {
            Badges.unlock(badge);
            badge = Badge.HIGH_SCORE_4;
            local.add(badge);
        }
        if (score >= 1000000) {
            Badges.unlock(badge);
            badge = Badge.HIGH_SCORE_5;
            local.add(badge);
        }
        Badges.displayBadge(badge);
    }

    public static void validateHappyEnd() {
        local.add(Badge.HAPPY_END);
        Badges.displayBadge(Badge.HAPPY_END);
        if (Dungeon.hero.belongings.getItem(RemainsItem.class) != null) {
            local.add(Badge.HAPPY_END_REMAINS);
            Badges.displayBadge(Badge.HAPPY_END_REMAINS);
        }
        if (AscensionChallenge.qualifiedForPacifist()) {
            local.add(Badge.PACIFIST_ASCENT);
            Badges.displayBadge(Badge.PACIFIST_ASCENT);
        }
    }

    public static void validateChampion(int challenges) {
        if (challenges == 0) {
            return;
        }
        Badge badge = null;
        if (challenges >= 1) {
            badge = Badge.CHAMPION_1;
        }
        if (challenges >= 3) {
            Badges.unlock(badge);
            badge = Badge.CHAMPION_2;
        }
        if (challenges >= 6) {
            Badges.unlock(badge);
            badge = Badge.CHAMPION_3;
        }
        local.add(badge);
        Badges.displayBadge(badge);
    }

    private static void displayBadge(Badge badge) {
        if (badge == null || badge.type != BadgeType.JOURNAL && !Dungeon.customSeedText.isEmpty()) {
            return;
        }
        if (Badges.isUnlocked(badge)) {
            if (badge.type == BadgeType.LOCAL) {
                GLog.h(Messages.get(Badges.class, "endorsed", badge.title()), new Object[0]);
                GLog.newLine();
            }
        } else {
            Badges.unlock(badge);
            GLog.h(Messages.get(Badges.class, "new", badge.title() + " (" + badge.desc() + ")"), new Object[0]);
            GLog.newLine();
            PixelScene.showBadge(badge);
        }
    }

    public static boolean isUnlocked(Badge badge) {
        return global.contains((Object)badge);
    }

    public static HashSet<Badge> allUnlocked() {
        Badges.loadGlobal();
        return new HashSet<Badge>(global);
    }

    public static void disown(Badge badge) {
        Badges.loadGlobal();
        global.remove((Object)badge);
        saveNeeded = true;
    }

    public static void unlock(Badge badge) {
        if (!Badges.isUnlocked(badge) && (badge.type == BadgeType.JOURNAL || Dungeon.customSeedText.isEmpty())) {
            global.add(badge);
            saveNeeded = true;
        }
    }

    public static List<Badge> filterReplacedBadges(boolean global) {
        ArrayList<Badge> badges = new ArrayList<Badge>(global ? Badges.global : local);
        Iterator<Badge> iterator = badges.iterator();
        while (iterator.hasNext()) {
            Badge badge = iterator.next();
            if ((global || badge.type == BadgeType.LOCAL) && badge.type != BadgeType.HIDDEN) continue;
            iterator.remove();
        }
        Collections.sort(badges);
        return Badges.filterReplacedBadges(badges);
    }

    public static List<Badge> filterReplacedBadges(List<Badge> badges) {
        for (Badge[] tierReplace : tierBadgeReplacements) {
            Badges.leaveBest(badges, tierReplace);
        }
        for (Badge[] metaReplace : summaryBadgeReplacements) {
            Badges.leaveBest(badges, metaReplace);
        }
        return badges;
    }

    private static void leaveBest(Collection<Badge> list, Badge ... badges) {
        for (int i = badges.length - 1; i > 0; --i) {
            if (!list.contains((Object)badges[i])) continue;
            for (int j = 0; j < i; ++j) {
                list.remove((Object)badges[j]);
            }
            break;
        }
    }

    public static List<Badge> filterBadgesWithoutPrerequisites(List<Badge> badges) {
        for (Badge[] prereqReplace : prerequisiteBadges) {
            Badges.leaveWorst(badges, prereqReplace);
        }
        for (Badge[] tierReplace : tierBadgeReplacements) {
            Badges.leaveWorst(badges, tierReplace);
        }
        Collections.sort(badges);
        return badges;
    }

    private static void leaveWorst(Collection<Badge> list, Badge ... badges) {
        for (int i = 0; i < badges.length; ++i) {
            if (!list.contains((Object)badges[i])) continue;
            for (int j = i + 1; j < badges.length; ++j) {
                list.remove((Object)badges[j]);
            }
            break;
        }
    }

    public static Collection<Badge> addReplacedBadges(Collection<Badge> badges) {
        for (Badge[] tierReplace : tierBadgeReplacements) {
            Badges.addLower(badges, tierReplace);
        }
        for (Badge[] metaReplace : summaryBadgeReplacements) {
            Badges.addLower(badges, metaReplace);
        }
        return badges;
    }

    private static void addLower(Collection<Badge> list, Badge ... badges) {
        for (int i = badges.length - 1; i > 0; --i) {
            if (!list.contains((Object)badges[i])) continue;
            for (int j = 0; j < i; ++j) {
                list.add(badges[j]);
            }
            break;
        }
    }

    public static String showCompletionProgress(Badge badge) {
        if (Badges.isUnlocked(badge)) {
            return null;
        }
        Object result = "\n";
        if (badge == Badge.BOSS_SLAIN_1_ALL_CLASSES) {
            for (HeroClass cls : HeroClass.values()) {
                result = (String)result + "\n";
                result = Badges.isUnlocked(firstBossClassBadges.get((Object)cls)) ? (String)result + "_" + Messages.titleCase(cls.title()) + "_" : (String)result + Messages.titleCase(cls.title());
            }
            return result;
        }
        if (badge == Badge.VICTORY_ALL_CLASSES) {
            for (HeroClass cls : HeroClass.values()) {
                result = (String)result + "\n";
                result = Badges.isUnlocked(victoryClassBadges.get((Object)cls)) ? (String)result + "_" + Messages.titleCase(cls.title()) + "_" : (String)result + Messages.titleCase(cls.title());
            }
            return result;
        }
        if (badge == Badge.BOSS_SLAIN_3_ALL_SUBCLASSES) {
            for (HeroSubClass cls : HeroSubClass.values()) {
                if (cls == HeroSubClass.NONE) continue;
                result = (String)result + "\n";
                result = Badges.isUnlocked(thirdBossSubclassBadges.get((Object)cls)) ? (String)result + "_" + Messages.titleCase(cls.title()) + "_" : (String)result + Messages.titleCase(cls.title());
            }
            return result;
        }
        return null;
    }

    static {
        local = new HashSet();
        saveNeeded = false;
        removedBadges = new HashSet();
        removedBadges.add("ALL_WEAPONS_IDENTIFIED");
        removedBadges.add("ALL_ARMOR_IDENTIFIED");
        removedBadges.add("ALL_WANDS_IDENTIFIED");
        removedBadges.add("ALL_RINGS_IDENTIFIED");
        removedBadges.add("ALL_ARTIFACTS_IDENTIFIED");
        removedBadges.add("ALL_POTIONS_IDENTIFIED");
        removedBadges.add("ALL_SCROLLS_IDENTIFIED");
        removedBadges.add("ALL_ITEMS_IDENTIFIED");
        renamedBadges = new HashMap();
        firstBossClassBadges = new LinkedHashMap();
        firstBossClassBadges.put(HeroClass.WARRIOR, Badge.BOSS_SLAIN_1_WARRIOR);
        firstBossClassBadges.put(HeroClass.MAGE, Badge.BOSS_SLAIN_1_MAGE);
        firstBossClassBadges.put(HeroClass.ROGUE, Badge.BOSS_SLAIN_1_ROGUE);
        firstBossClassBadges.put(HeroClass.HUNTRESS, Badge.BOSS_SLAIN_1_HUNTRESS);
        firstBossClassBadges.put(HeroClass.DUELIST, Badge.BOSS_SLAIN_1_DUELIST);
        firstBossClassBadges.put(HeroClass.CLERIC, Badge.BOSS_SLAIN_1_CLERIC);
        victoryClassBadges = new LinkedHashMap();
        victoryClassBadges.put(HeroClass.WARRIOR, Badge.VICTORY_WARRIOR);
        victoryClassBadges.put(HeroClass.MAGE, Badge.VICTORY_MAGE);
        victoryClassBadges.put(HeroClass.ROGUE, Badge.VICTORY_ROGUE);
        victoryClassBadges.put(HeroClass.HUNTRESS, Badge.VICTORY_HUNTRESS);
        victoryClassBadges.put(HeroClass.DUELIST, Badge.VICTORY_DUELIST);
        victoryClassBadges.put(HeroClass.CLERIC, Badge.VICTORY_CLERIC);
        thirdBossSubclassBadges = new LinkedHashMap();
        thirdBossSubclassBadges.put(HeroSubClass.BERSERKER, Badge.BOSS_SLAIN_3_BERSERKER);
        thirdBossSubclassBadges.put(HeroSubClass.GLADIATOR, Badge.BOSS_SLAIN_3_GLADIATOR);
        thirdBossSubclassBadges.put(HeroSubClass.BATTLEMAGE, Badge.BOSS_SLAIN_3_BATTLEMAGE);
        thirdBossSubclassBadges.put(HeroSubClass.WARLOCK, Badge.BOSS_SLAIN_3_WARLOCK);
        thirdBossSubclassBadges.put(HeroSubClass.ASSASSIN, Badge.BOSS_SLAIN_3_ASSASSIN);
        thirdBossSubclassBadges.put(HeroSubClass.FREERUNNER, Badge.BOSS_SLAIN_3_FREERUNNER);
        thirdBossSubclassBadges.put(HeroSubClass.SNIPER, Badge.BOSS_SLAIN_3_SNIPER);
        thirdBossSubclassBadges.put(HeroSubClass.WARDEN, Badge.BOSS_SLAIN_3_WARDEN);
        thirdBossSubclassBadges.put(HeroSubClass.CHAMPION, Badge.BOSS_SLAIN_3_CHAMPION);
        thirdBossSubclassBadges.put(HeroSubClass.MONK, Badge.BOSS_SLAIN_3_MONK);
        thirdBossSubclassBadges.put(HeroSubClass.PRIEST, Badge.BOSS_SLAIN_3_PRIEST);
        thirdBossSubclassBadges.put(HeroSubClass.PALADIN, Badge.BOSS_SLAIN_3_PALADIN);
        tierBadgeReplacements = new Badge[][]{{Badge.MONSTERS_SLAIN_1, Badge.MONSTERS_SLAIN_2, Badge.MONSTERS_SLAIN_3, Badge.MONSTERS_SLAIN_4, Badge.MONSTERS_SLAIN_5}, {Badge.GOLD_COLLECTED_1, Badge.GOLD_COLLECTED_2, Badge.GOLD_COLLECTED_3, Badge.GOLD_COLLECTED_4, Badge.GOLD_COLLECTED_5}, {Badge.ITEM_LEVEL_1, Badge.ITEM_LEVEL_2, Badge.ITEM_LEVEL_3, Badge.ITEM_LEVEL_4, Badge.ITEM_LEVEL_5}, {Badge.LEVEL_REACHED_1, Badge.LEVEL_REACHED_2, Badge.LEVEL_REACHED_3, Badge.LEVEL_REACHED_4, Badge.LEVEL_REACHED_5}, {Badge.STRENGTH_ATTAINED_1, Badge.STRENGTH_ATTAINED_2, Badge.STRENGTH_ATTAINED_3, Badge.STRENGTH_ATTAINED_4, Badge.STRENGTH_ATTAINED_5}, {Badge.FOOD_EATEN_1, Badge.FOOD_EATEN_2, Badge.FOOD_EATEN_3, Badge.FOOD_EATEN_4, Badge.FOOD_EATEN_5}, {Badge.ITEMS_CRAFTED_1, Badge.ITEMS_CRAFTED_2, Badge.ITEMS_CRAFTED_3, Badge.ITEMS_CRAFTED_4, Badge.ITEMS_CRAFTED_5}, {Badge.BOSS_SLAIN_1, Badge.BOSS_SLAIN_2, Badge.BOSS_SLAIN_3, Badge.BOSS_SLAIN_4}, {Badge.RESEARCHER_1, Badge.RESEARCHER_2, Badge.RESEARCHER_3, Badge.RESEARCHER_4, Badge.RESEARCHER_5}, {Badge.HIGH_SCORE_1, Badge.HIGH_SCORE_2, Badge.HIGH_SCORE_3, Badge.HIGH_SCORE_4, Badge.HIGH_SCORE_5}, {Badge.GAMES_PLAYED_1, Badge.GAMES_PLAYED_2, Badge.GAMES_PLAYED_3, Badge.GAMES_PLAYED_4, Badge.GAMES_PLAYED_5}, {Badge.CHAMPION_1, Badge.CHAMPION_2, Badge.CHAMPION_3}};
        prerequisiteBadges = new Badge[][]{{Badge.BOSS_SLAIN_1, Badge.BOSS_CHALLENGE_1}, {Badge.BOSS_SLAIN_2, Badge.BOSS_CHALLENGE_2}, {Badge.BOSS_SLAIN_3, Badge.BOSS_CHALLENGE_3}, {Badge.BOSS_SLAIN_4, Badge.BOSS_CHALLENGE_4}, {Badge.VICTORY, Badge.BOSS_CHALLENGE_5}, {Badge.HAPPY_END, Badge.PACIFIST_ASCENT}, {Badge.VICTORY, Badge.TAKING_THE_MICK}};
        summaryBadgeReplacements = new Badge[][]{{Badge.DEATH_FROM_FIRE, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_GAS, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_HUNGER, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_POISON, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_FALLING, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_ENEMY_MAGIC, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_FRIENDLY_MAGIC, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_SACRIFICE, Badge.DEATH_FROM_ALL}, {Badge.DEATH_FROM_GRIM_TRAP, Badge.DEATH_FROM_ALL}};
    }

    public static enum Badge {
        MASTERY_WARRIOR,
        MASTERY_MAGE,
        MASTERY_ROGUE,
        MASTERY_HUNTRESS,
        MASTERY_DUELIST,
        MASTERY_CLERIC,
        FOUND_RATMOGRIFY,
        UNLOCK_MAGE(1),
        UNLOCK_ROGUE(2),
        UNLOCK_HUNTRESS(3),
        UNLOCK_DUELIST(4),
        UNLOCK_CLERIC(5),
        MONSTERS_SLAIN_1(6),
        MONSTERS_SLAIN_2(7),
        GOLD_COLLECTED_1(8),
        GOLD_COLLECTED_2(9),
        ITEM_LEVEL_1(10),
        LEVEL_REACHED_1(11),
        STRENGTH_ATTAINED_1(12),
        FOOD_EATEN_1(13),
        ITEMS_CRAFTED_1(14),
        BOSS_SLAIN_1(15),
        CATALOG_ONE_EQUIPMENT(16, BadgeType.JOURNAL),
        DEATH_FROM_FIRE(17),
        DEATH_FROM_POISON(18),
        DEATH_FROM_GAS(19),
        DEATH_FROM_HUNGER(20),
        DEATH_FROM_FALLING(21),
        RESEARCHER_1(22, BadgeType.JOURNAL),
        GAMES_PLAYED_1(23, BadgeType.GLOBAL),
        HIGH_SCORE_1(24),
        NO_MONSTERS_SLAIN(32),
        BOSS_SLAIN_REMAINS(33),
        MONSTERS_SLAIN_3(34),
        MONSTERS_SLAIN_4(35),
        GOLD_COLLECTED_3(36),
        GOLD_COLLECTED_4(37),
        ITEM_LEVEL_2(38),
        ITEM_LEVEL_3(39),
        LEVEL_REACHED_2(40),
        LEVEL_REACHED_3(41),
        STRENGTH_ATTAINED_2(42),
        STRENGTH_ATTAINED_3(43),
        FOOD_EATEN_2(44),
        FOOD_EATEN_3(45),
        ITEMS_CRAFTED_2(46),
        ITEMS_CRAFTED_3(47),
        BOSS_SLAIN_2(48),
        BOSS_SLAIN_3(49),
        CATALOG_POTIONS_SCROLLS(50),
        DEATH_FROM_ENEMY_MAGIC(51),
        DEATH_FROM_FRIENDLY_MAGIC(52),
        DEATH_FROM_SACRIFICE(53),
        BOSS_SLAIN_1_WARRIOR,
        BOSS_SLAIN_1_MAGE,
        BOSS_SLAIN_1_ROGUE,
        BOSS_SLAIN_1_HUNTRESS,
        BOSS_SLAIN_1_DUELIST,
        BOSS_SLAIN_1_CLERIC,
        BOSS_SLAIN_1_ALL_CLASSES(54, BadgeType.GLOBAL),
        RESEARCHER_2(55, BadgeType.JOURNAL),
        GAMES_PLAYED_2(56, BadgeType.GLOBAL),
        HIGH_SCORE_2(57),
        ENEMY_HAZARDS(64),
        PIRANHAS(65),
        GRIM_WEAPON(66),
        BAG_BOUGHT_VELVET_POUCH,
        BAG_BOUGHT_SCROLL_HOLDER,
        BAG_BOUGHT_POTION_BANDOLIER,
        BAG_BOUGHT_MAGICAL_HOLSTER,
        ALL_BAGS_BOUGHT(67),
        MASTERY_COMBO(68),
        MONSTERS_SLAIN_5(69),
        GOLD_COLLECTED_5(70),
        ITEM_LEVEL_4(71),
        LEVEL_REACHED_4(72),
        STRENGTH_ATTAINED_4(73),
        STRENGTH_ATTAINED_5(74),
        FOOD_EATEN_4(75),
        FOOD_EATEN_5(76),
        ITEMS_CRAFTED_4(77),
        ITEMS_CRAFTED_5(78),
        BOSS_SLAIN_4(79),
        ALL_RARE_ENEMIES(80, BadgeType.JOURNAL),
        DEATH_FROM_GRIM_TRAP(81),
        VICTORY(82),
        BOSS_CHALLENGE_1(83),
        BOSS_CHALLENGE_2(84),
        RESEARCHER_3(85, BadgeType.JOURNAL),
        GAMES_PLAYED_3(86, BadgeType.GLOBAL),
        HIGH_SCORE_3(87),
        MANY_BUFFS(96),
        ITEM_LEVEL_5(97),
        LEVEL_REACHED_5(98),
        HAPPY_END(99),
        VICTORY_RANDOM(100),
        HAPPY_END_REMAINS(101),
        RODNEY(102, BadgeType.JOURNAL),
        VICTORY_WARRIOR,
        VICTORY_MAGE,
        VICTORY_ROGUE,
        VICTORY_HUNTRESS,
        VICTORY_DUELIST,
        VICTORY_CLERIC,
        VICTORY_ALL_CLASSES(103, BadgeType.GLOBAL),
        DEATH_FROM_ALL(104, BadgeType.GLOBAL),
        BOSS_SLAIN_3_GLADIATOR,
        BOSS_SLAIN_3_BERSERKER,
        BOSS_SLAIN_3_WARLOCK,
        BOSS_SLAIN_3_BATTLEMAGE,
        BOSS_SLAIN_3_FREERUNNER,
        BOSS_SLAIN_3_ASSASSIN,
        BOSS_SLAIN_3_SNIPER,
        BOSS_SLAIN_3_WARDEN,
        BOSS_SLAIN_3_CHAMPION,
        BOSS_SLAIN_3_MONK,
        BOSS_SLAIN_3_PRIEST,
        BOSS_SLAIN_3_PALADIN,
        BOSS_SLAIN_3_ALL_SUBCLASSES(105, BadgeType.GLOBAL),
        BOSS_CHALLENGE_3(106),
        BOSS_CHALLENGE_4(107),
        RESEARCHER_4(108, BadgeType.JOURNAL),
        GAMES_PLAYED_4(109, BadgeType.GLOBAL),
        HIGH_SCORE_4(110),
        CHAMPION_1(111),
        PACIFIST_ASCENT(120),
        TAKING_THE_MICK(121),
        BOSS_CHALLENGE_5(122),
        RESEARCHER_5(123, BadgeType.JOURNAL),
        GAMES_PLAYED_5(124, BadgeType.GLOBAL),
        HIGH_SCORE_5(125),
        CHAMPION_2(126),
        CHAMPION_3(127);

        public boolean meta;
        public int image;
        public BadgeType type;

        private Badge() {
            this(-1, BadgeType.HIDDEN);
        }

        private Badge(int image) {
            this(image, BadgeType.LOCAL);
        }

        private Badge(int image, BadgeType type) {
            this.image = image;
            this.type = type;
        }

        public String title() {
            return Messages.get((Object)this, this.name() + ".title", new Object[0]);
        }

        public String desc() {
            return Messages.get((Object)this, this.name() + ".desc", new Object[0]);
        }
    }

    public static enum BadgeType {
        HIDDEN,
        LOCAL,
        GLOBAL,
        JOURNAL;

    }
}

