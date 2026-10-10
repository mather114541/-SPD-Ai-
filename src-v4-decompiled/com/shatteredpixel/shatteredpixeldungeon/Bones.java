/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Bones {
    private static final String BONES_FILE = "bones.dat";
    private static final String LEVEL = "level";
    private static final String BRANCH = "branch";
    private static final String ITEM = "item";
    private static final String HERO_CLASS = "hero_class";
    private static int depth = -1;
    private static int branch = -1;
    private static Item item;
    private static HeroClass heroClass;

    public static void leave() {
        depth = Math.max(Dungeon.depth, Statistics.deepestFloor - 5);
        branch = Dungeon.branch;
        if (Dungeon.daily) {
            branch = -1;
            depth = -1;
            return;
        }
        item = Bones.pickItem(Dungeon.hero);
        heroClass = Dungeon.hero.heroClass;
        Bundle bundle = new Bundle();
        bundle.put(LEVEL, depth);
        bundle.put(BRANCH, branch);
        bundle.put(ITEM, item);
        bundle.put(HERO_CLASS, heroClass);
        try {
            FileUtils.bundleToFile(BONES_FILE, bundle);
        }
        catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
    }

    private static Item pickItem(Hero hero) {
        Item item = null;
        if (!Dungeon.customSeedText.isEmpty()) {
            return null;
        }
        if (Random.Int(3) != 0) {
            switch (Random.Int(7)) {
                case 0: {
                    item = hero.belongings.weapon;
                    if (hero.belongings.secondWep == null || item != null && hero.belongings.secondWep.trueLevel() <= item.trueLevel()) break;
                    item = hero.belongings.secondWep;
                    break;
                }
                case 1: {
                    item = hero.belongings.armor;
                    break;
                }
                case 2: {
                    item = hero.belongings.artifact;
                    break;
                }
                case 3: {
                    item = hero.belongings.misc;
                    break;
                }
                case 4: {
                    item = hero.belongings.ring;
                    break;
                }
                case 5: 
                case 6: {
                    item = Dungeon.quickslot.randomNonePlaceholder();
                }
            }
            if (item == null || !item.bones) {
                return Bones.pickItem(hero);
            }
        } else {
            Iterator<Item> iterator = hero.belongings.backpack.iterator();
            ArrayList<Item> items = new ArrayList<Item>();
            while (iterator.hasNext()) {
                Item curItem = iterator.next();
                if (!curItem.bones) continue;
                items.add(curItem);
            }
            if (Random.Int(3) < items.size()) {
                item = (Item)Random.element(items);
                if (item.stackable) {
                    item.quantity(Random.NormalIntRange(1, (item.quantity() + 1) / 2));
                    if (item.quantity() > 3) {
                        item.quantity(3);
                    }
                }
            } else {
                item = null;
            }
        }
        return item;
    }

    public static ArrayList<Item> get() {
        if (Dungeon.daily) {
            return null;
        }
        if (depth == -1) {
            try {
                Bundle bundle = FileUtils.bundleFromFile(BONES_FILE);
                depth = bundle.getInt(LEVEL);
                branch = bundle.getInt(BRANCH);
                if (depth > 0) {
                    item = bundle.contains(ITEM) ? (Item)bundle.get(ITEM) : null;
                    heroClass = bundle.contains(HERO_CLASS) ? bundle.getEnum(HERO_CLASS, HeroClass.class) : null;
                }
                return Bones.get();
            }
            catch (IOException e) {
                return null;
            }
        }
        if (Bones.lootAtCurLevel()) {
            Bundle emptyBones = new Bundle();
            emptyBones.put(LEVEL, 0);
            try {
                FileUtils.bundleToFile(BONES_FILE, emptyBones);
            }
            catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
            depth = 0;
            if (Dungeon.challenges != 0 || !Dungeon.customSeedText.isEmpty()) {
                item = null;
            }
            if (item instanceof Artifact) {
                if (Generator.removeArtifact(((Artifact)item).getClass())) {
                    Artifact artifact = (Artifact)Reflection.newInstance(((Artifact)item).getClass());
                    if (artifact != null) {
                        artifact.cursed = true;
                        artifact.cursedKnown = true;
                    }
                    item = artifact;
                } else {
                    item = new Gold(item.value());
                }
            }
            if (item != null) {
                if (item.isUpgradable() && !(item instanceof MissileWeapon)) {
                    Bones.item.cursed = true;
                    Bones.item.cursedKnown = true;
                }
                if (item.isUpgradable()) {
                    if (item.level() > 3) {
                        item.degrade(item.level() - 3);
                    }
                    Bones.item.levelKnown = item instanceof MissileWeapon;
                }
                item.reset();
            }
            ArrayList<Item> result = new ArrayList<Item>();
            if (heroClass != null) {
                result.add(RemainsItem.get(heroClass));
                if (Dungeon.bossLevel()) {
                    Statistics.qualifiedForBossRemainsBadge = true;
                }
            }
            if (item != null) {
                result.add(item);
            }
            return result.isEmpty() ? null : result;
        }
        return null;
    }

    private static boolean lootAtCurLevel() {
        if (branch == Dungeon.branch) {
            if (branch == 0) {
                return depth == Dungeon.depth;
            }
            if (branch == 1) {
                return depth / 5 == Dungeon.depth / 5;
            }
        }
        return false;
    }
}

