/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBlacksmith;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Blacksmith
extends NPC {
    public Blacksmith() {
        this.spriteClass = BlacksmithSprite.class;
        this.properties.add(Char.Property.IMMOVABLE);
    }

    @Override
    public Notes.Landmark landmark() {
        return !Quest.completed() || Quest.rewardsAvailable() ? Notes.Landmark.TROLL : null;
    }

    @Override
    protected boolean act() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            this.die(null);
            Notes.remove(this.landmark());
            return true;
        }
        if (!Quest.rewardsAvailable() && Quest.completed()) {
            Notes.remove(this.landmark());
        }
        return super.act();
    }

    @Override
    public boolean interact(Char c) {
        this.sprite.turnTo(this.pos, c.pos);
        if (c != Dungeon.hero) {
            return true;
        }
        if (!Quest.given) {
            Object msg1 = "";
            Object msg2 = "";
            switch (Dungeon.hero.heroClass) {
                case WARRIOR: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_quest_warrior", new Object[0]);
                    break;
                }
                case MAGE: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_quest_mage", new Object[0]);
                    break;
                }
                case ROGUE: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_quest_rogue", new Object[0]);
                    break;
                }
                case HUNTRESS: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_quest_huntress", new Object[0]);
                    break;
                }
                case DUELIST: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_quest_duelist", new Object[0]);
                    break;
                }
                case CLERIC: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_quest_cleric", new Object[0]);
                }
            }
            msg1 = (String)msg1 + "\n\n" + Messages.get(this, "intro_quest_start", new Object[0]);
            switch (Quest.type) {
                case 1: {
                    msg2 = (String)msg2 + Messages.get(this, "intro_quest_crystal", new Object[0]);
                    break;
                }
                case 2: {
                    msg2 = (String)msg2 + Messages.get(this, "intro_quest_gnoll", new Object[0]);
                    break;
                }
                case 3: {
                    msg2 = (String)msg2 + Messages.get(this, "intro_quest_fungi", new Object[0]);
                }
            }
            Object msg1Final = msg1;
            String msg2Final = msg2;
            Game.runOnRenderThread(new Callback(){
                final /* synthetic */ String val$msg1Final;
                final /* synthetic */ String val$msg2Final;
                {
                    this.val$msg1Final = string;
                    this.val$msg2Final = string2;
                }

                @Override
                public void call() {
                    GameScene.show(new WndQuest(Blacksmith.this, this.val$msg1Final){

                        @Override
                        public void hide() {
                            Item pick;
                            super.hide();
                            Quest.given = true;
                            Quest.completed = false;
                            Item item = pick = Quest.pickaxe != null ? Quest.pickaxe : new Pickaxe();
                            if (pick.doPickUp(Dungeon.hero)) {
                                GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", pick.name())), new Object[0]);
                            } else {
                                Dungeon.level.drop((Item)pick, (int)Dungeon.hero.pos).sprite.drop();
                            }
                            Quest.pickaxe = null;
                            if (val$msg2Final != "") {
                                GameScene.show(new WndQuest(Blacksmith.this, val$msg2Final));
                            }
                        }
                    });
                }
            });
        } else if (!Quest.completed) {
            String msg = Messages.get(this, "reminder", new Object[0]) + "\n\n";
            switch (Quest.type) {
                case 1: {
                    msg = msg + Messages.get(this, "reminder_crystal", new Object[0]);
                    break;
                }
                case 2: {
                    msg = msg + Messages.get(this, "reminder_gnoll", new Object[0]);
                    break;
                }
                case 3: {
                    msg = msg + Messages.get(this, "reminder_fungi", new Object[0]);
                }
            }
            this.tell(msg);
        } else if (Quest.rewardsAvailable()) {
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    if (Quest.smithRewards != null && Quest.smiths > 0) {
                        GameScene.show(new WndBlacksmith.WndSmith(Blacksmith.this, Dungeon.hero));
                    } else {
                        GameScene.show(new WndBlacksmith(Blacksmith.this, Dungeon.hero));
                    }
                }
            });
        } else {
            this.tell(Messages.get(this, "get_lost", new Object[0]));
        }
        return true;
    }

    private void tell(final String text) {
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                GameScene.show(new WndQuest(Blacksmith.this, text));
            }
        });
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }

    public static class Quest {
        private static int type = 0;
        public static final int CRYSTAL = 1;
        public static final int GNOLL = 2;
        public static final int FUNGI = 3;
        private static boolean spawned;
        private static boolean given;
        private static boolean started;
        private static boolean bossBeaten;
        private static boolean completed;
        public static int favor;
        public static Item pickaxe;
        public static boolean freePickaxe;
        public static int reforges;
        public static int hardens;
        public static int upgrades;
        public static int smiths;
        public static ArrayList<Item> smithRewards;
        public static Weapon.Enchantment smithEnchant;
        public static Armor.Glyph smithGlyph;
        private static final String NODE = "blacksmith";
        private static final String TYPE = "type";
        private static final String ALTERNATIVE = "alternative";
        private static final String SPAWNED = "spawned";
        private static final String GIVEN = "given";
        private static final String STARTED = "started";
        private static final String BOSS_BEATEN = "boss_beaten";
        private static final String COMPLETED = "completed";
        private static final String FAVOR = "favor";
        private static final String PICKAXE = "pickaxe";
        private static final String FREE_PICKAXE = "free_pickaxe";
        private static final String REFORGES = "reforges";
        private static final String HARDENS = "hardens";
        private static final String UPGRADES = "upgrades";
        private static final String SMITHS = "smiths";
        private static final String SMITH_REWARDS = "smith_rewards";
        private static final String ENCHANT = "enchant";
        private static final String GLYPH = "glyph";

        public static void reset() {
            type = 0;
            spawned = false;
            given = false;
            started = false;
            bossBeaten = false;
            completed = false;
            favor = 0;
            pickaxe = new Pickaxe().identify(false);
            freePickaxe = false;
            reforges = 0;
            hardens = 0;
            upgrades = 0;
            smiths = 0;
            smithRewards = null;
            smithEnchant = null;
            smithGlyph = null;
        }

        public static void storeInBundle(Bundle bundle) {
            Bundle node = new Bundle();
            node.put(SPAWNED, spawned);
            if (spawned) {
                node.put(TYPE, type);
                node.put(GIVEN, given);
                node.put(STARTED, started);
                node.put(BOSS_BEATEN, bossBeaten);
                node.put(COMPLETED, completed);
                node.put(FAVOR, favor);
                if (pickaxe != null) {
                    node.put(PICKAXE, pickaxe);
                }
                node.put(FREE_PICKAXE, freePickaxe);
                node.put(REFORGES, reforges);
                node.put(HARDENS, hardens);
                node.put(UPGRADES, upgrades);
                node.put(SMITHS, smiths);
                if (smithRewards != null) {
                    node.put(SMITH_REWARDS, smithRewards);
                    if (smithEnchant != null) {
                        node.put(ENCHANT, smithEnchant);
                        node.put(GLYPH, smithGlyph);
                    }
                }
            }
            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {
            Bundle node = bundle.getBundle(NODE);
            if (!node.isNull() && (spawned = node.getBoolean(SPAWNED))) {
                type = node.getInt(TYPE);
                given = node.getBoolean(GIVEN);
                started = node.getBoolean(STARTED);
                bossBeaten = node.getBoolean(BOSS_BEATEN);
                completed = node.getBoolean(COMPLETED);
                favor = node.getInt(FAVOR);
                pickaxe = node.contains(PICKAXE) ? (Item)node.get(PICKAXE) : null;
                if (node.contains(FREE_PICKAXE)) {
                    freePickaxe = node.getBoolean(FREE_PICKAXE);
                }
                reforges = node.getInt(REFORGES);
                hardens = node.getInt(HARDENS);
                upgrades = node.getInt(UPGRADES);
                smiths = node.getInt(SMITHS);
                if (node.contains(SMITH_REWARDS)) {
                    smithRewards = new ArrayList<Bundlable>(node.getCollection(SMITH_REWARDS));
                    if (node.contains(ENCHANT)) {
                        smithEnchant = (Weapon.Enchantment)node.get(ENCHANT);
                        smithGlyph = (Armor.Glyph)node.get(GLYPH);
                    }
                } else {
                    smithRewards = null;
                }
            } else {
                Quest.reset();
            }
        }

        public static ArrayList<Room> spawn(ArrayList<Room> rooms) {
            if (!spawned && Dungeon.depth > 11 && Random.Int(15 - Dungeon.depth) == 0) {
                rooms.add(new BlacksmithRoom());
                spawned = true;
                type = Random.IntRange(1, 2);
                given = false;
                Quest.generateRewards(true);
            }
            return rooms;
        }

        public static void generateRewards(boolean useDecks) {
            smithRewards = new ArrayList();
            smithRewards.add(Generator.randomWeapon(3, useDecks));
            smithRewards.add(Generator.randomWeapon(3, useDecks));
            ArrayList<Item> toUndo = new ArrayList<Item>();
            while (smithRewards.get(0).getClass() == smithRewards.get(1).getClass()) {
                if (useDecks) {
                    toUndo.add(smithRewards.get(1));
                }
                smithRewards.remove(1);
                smithRewards.add(Generator.randomWeapon(3, useDecks));
            }
            for (Item i : toUndo) {
                Generator.undoDrop(i);
            }
            smithRewards.add(Generator.randomMissile(3, useDecks));
            smithRewards.add(Generator.randomArmor(3));
            float itemLevelRoll = Random.Float();
            int rewardLevel = itemLevelRoll < 0.3f ? 0 : (itemLevelRoll < 0.75f ? 1 : (itemLevelRoll < 0.95f ? 2 : 3));
            for (Item i : smithRewards) {
                i.level(rewardLevel);
                if (i instanceof Weapon) {
                    ((Weapon)i).enchant(null);
                } else if (i instanceof Armor) {
                    ((Armor)i).inscribe(null);
                }
                i.cursed = false;
            }
            smithEnchant = Weapon.Enchantment.random(new Class[0]);
            smithGlyph = Armor.Glyph.random(new Class[0]);
            float enchantRoll = Random.Float();
            if (enchantRoll > 0.3f * ParchmentScrap.enchantChanceMultiplier()) {
                smithEnchant = null;
                smithGlyph = null;
            }
        }

        public static int Type() {
            return type;
        }

        public static boolean given() {
            return given;
        }

        public static boolean started() {
            return started;
        }

        public static void start() {
            started = true;
        }

        public static boolean beatBoss() {
            bossBeaten = true;
            return true;
        }

        public static boolean bossBeaten() {
            return bossBeaten;
        }

        public static boolean completed() {
            return given && completed;
        }

        public static void complete() {
            Pickaxe pick;
            completed = true;
            favor = 0;
            DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
            if (gold != null) {
                favor += Math.min(2000, gold.quantity() * 50);
                gold.detachAll(Dungeon.hero.belongings.backpack);
            }
            if ((pick = Dungeon.hero.belongings.getItem(Pickaxe.class)).isEquipped(Dungeon.hero)) {
                boolean wasCursed = pick.cursed;
                pick.cursed = false;
                pick.doUnequip(Dungeon.hero, false);
                pick.cursed = wasCursed;
            }
            pick.detach(Dungeon.hero.belongings.backpack);
            pickaxe = pick;
            if (bossBeaten) {
                favor += 1000;
            }
            Statistics.questScores[2] = Statistics.questScores[2] + favor;
            if (favor >= 2500) {
                freePickaxe = true;
            }
        }

        public static boolean rewardsAvailable() {
            return favor > 0 || smithRewards != null && smiths > 0 || pickaxe != null && freePickaxe;
        }
    }
}

