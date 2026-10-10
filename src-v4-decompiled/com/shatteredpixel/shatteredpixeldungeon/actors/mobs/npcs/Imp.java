/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.AmbitiousImpRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndImpOld;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Imp
extends NPC {
    private boolean seenBefore;

    public Imp() {
        this.spriteClass = ImpSprite.class;
        this.properties.add(Char.Property.IMMOVABLE);
        this.seenBefore = false;
    }

    @Override
    public Notes.Landmark landmark() {
        return Quest.isCompleted() ? null : Notes.Landmark.IMP;
    }

    @Override
    protected boolean act() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            this.die(null);
            return true;
        }
        if (Quest.isCompleted() && Quest.reward != null) {
            Dungeon.level.drop(Quest.reward, this.pos);
            this.throwItems();
            Quest.reward = null;
        }
        if (Quest.isCompleted() && Quest.score > 2000 && this.fieldOfView != null && !this.fieldOfView[Dungeon.hero.pos]) {
            this.flee();
        } else if (!Quest.given && Dungeon.level.visited[this.pos]) {
            if (!this.seenBefore && Dungeon.level.heroFOV[this.pos]) {
                this.yell(Messages.get(this, "hey", Messages.titleCase(Dungeon.hero.name())));
                this.seenBefore = true;
            }
        } else {
            this.seenBefore = false;
        }
        return super.act();
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

    @Override
    public boolean interact(Char c) {
        this.sprite.turnTo(this.pos, Dungeon.hero.pos);
        if (c != Dungeon.hero) {
            return true;
        }
        if (Quest.oldQuest) {
            if (Quest.given) {
                final DwarfToken tokens = Dungeon.hero.belongings.getItem(DwarfToken.class);
                if (tokens != null && (tokens.quantity() >= 5 || !Quest.alternative && tokens.quantity() >= 4)) {
                    Game.runOnRenderThread(new Callback(){

                        @Override
                        public void call() {
                            GameScene.show(new WndImpOld(Imp.this, tokens));
                        }
                    });
                } else {
                    this.tell(Quest.alternative ? Messages.get(this, "old_monks_2", Messages.titleCase(Dungeon.hero.name())) : Messages.get(this, "old_golems_2", Messages.titleCase(Dungeon.hero.name())));
                }
            } else {
                this.tell(Messages.get(this, "old_intro", new Object[0]) + "\n\n" + (Quest.alternative ? Messages.get(this, "old_monks_1", Messages.titleCase(Dungeon.hero.name())) : Messages.get(this, "old_golems_1", Messages.titleCase(Dungeon.hero.name()))));
                Quest.given = true;
                Quest.completed = false;
            }
        } else if (!Quest.given()) {
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    GameScene.show(new WndQuest(Imp.this, Messages.get(Imp.this, "quest_intro_1", new Object[0])){

                        @Override
                        public void hide() {
                            super.hide();
                            Quest.given = true;
                            Quest.completed = false;
                            Imp.this.tell(Messages.get(Imp.this, "quest_intro_2", new Object[0]));
                        }
                    });
                }
            });
        } else if (!Quest.isCompleted()) {
            this.tell(Messages.get(this, "quest_in_progress", new Object[0]));
        } else if (Quest.score <= 2000) {
            this.tell(Messages.get(this, "quest_completed_bad", new Object[0]));
        } else if (Quest.score < 4000) {
            this.tell(Messages.get(this, "quest_completed_good", new Object[0]));
        } else {
            this.tell(Messages.get(this, "quest_completed_great", new Object[0]));
        }
        return true;
    }

    private void tell(final String text) {
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                GameScene.show(new WndQuest(Imp.this, text));
            }
        });
    }

    public void flee() {
        this.yell(Messages.get(this, "cya", Messages.titleCase(Dungeon.hero.name())));
        this.destroy();
        this.sprite.die();
    }

    public static class Quest {
        private static boolean spawned;
        private static boolean oldQuest;
        private static boolean alternative;
        private static boolean given;
        private static boolean completed;
        public static Item reward;
        public static ArrayList<Item> rewardOptions;
        public static int hazardFreebies;
        public static boolean mirrorUsed;
        private static int score;
        private static final String NODE = "demon";
        private static final String SPAWNED = "spawned";
        private static final String OLD_QUEST = "old_quest";
        private static final String ALTERNATIVE = "alternative";
        private static final String REWARD = "reward";
        private static final String GIVEN = "given";
        private static final String COMPLETED = "completed";
        private static final String HAZRD_FREEBIES = "hazard_freebies";
        private static final String SCORE = "score";
        private static final String REWARD_OPTIONS = "reward_options";
        private static final String MIRROR_USED = "mirror_used";

        public static void reset() {
            spawned = false;
            given = false;
            completed = false;
            reward = null;
            hazardFreebies = 2;
            mirrorUsed = false;
            score = 0;
        }

        public static void storeInBundle(Bundle bundle) {
            Bundle node = new Bundle();
            node.put(SPAWNED, spawned);
            if (spawned) {
                node.put(OLD_QUEST, oldQuest);
                node.put(ALTERNATIVE, alternative);
                node.put(GIVEN, given);
                node.put(COMPLETED, completed);
                node.put(REWARD, reward);
                node.put(HAZRD_FREEBIES, hazardFreebies);
                node.put(SCORE, score);
                node.put(REWARD_OPTIONS, rewardOptions);
                node.put(MIRROR_USED, mirrorUsed);
            }
            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {
            Bundle node = bundle.getBundle(NODE);
            if (!node.isNull() && (spawned = node.getBoolean(SPAWNED))) {
                oldQuest = node.contains(OLD_QUEST) ? node.getBoolean(OLD_QUEST) : true;
                if (oldQuest) {
                    alternative = node.getBoolean(ALTERNATIVE);
                    score = 0;
                    rewardOptions.clear();
                    mirrorUsed = false;
                } else {
                    alternative = false;
                    hazardFreebies = node.getInt(HAZRD_FREEBIES);
                    mirrorUsed = node.getBoolean(MIRROR_USED);
                    score = node.getInt(SCORE);
                    rewardOptions = new ArrayList<Bundlable>(node.getCollection(REWARD_OPTIONS));
                }
                reward = (Item)node.get(REWARD);
                given = node.getBoolean(GIVEN);
                completed = node.getBoolean(COMPLETED);
            }
        }

        public static ArrayList<Room> spawn(ArrayList<Room> rooms) {
            if (!spawned && Dungeon.depth > 16 && Random.Int(20 - Dungeon.depth) == 0) {
                Item ring;
                rooms.add(new AmbitiousImpRoom());
                spawned = true;
                oldQuest = false;
                reward = null;
                score = 0;
                given = false;
                mirrorUsed = false;
                rewardOptions.clear();
                Item artif = Generator.randomArtifact();
                if (artif != null) {
                    ((Artifact)artif.identify(false)).transferUpgrade(5);
                } else {
                    artif = Generator.random(Generator.Category.RING);
                    artif.level(Random.IntRange(2, 4));
                }
                rewardOptions.add(artif);
                while ((ring = Generator.random(Generator.Category.RING)).getClass() == artif.getClass()) {
                }
                ring.level(Random.IntRange(2, 4));
                rewardOptions.add(ring);
                if (Random.Int(2) == 0) {
                    rewardOptions.add(((Weapon)Generator.random(Generator.Category.WEP_T5)).enchant().identify(false).level(Random.IntRange(2, 4)));
                    rewardOptions.add(((Weapon)Generator.random(Generator.Category.MIS_T4)).enchant().identify(false).level(Random.IntRange(3, 5)));
                } else {
                    rewardOptions.add(((Weapon)Generator.random(Generator.Category.MIS_T5)).enchant().identify(false).level(Random.IntRange(2, 4)));
                    rewardOptions.add(((Weapon)Generator.random(Generator.Category.WEP_T4)).enchant().identify(false).level(Random.IntRange(3, 5)));
                }
                rewardOptions.add(new PlateArmor().inscribe().identify(false).level(Random.IntRange(2, 4)));
                Wand w = (Wand)Generator.random(Generator.Category.WAND);
                w.identify(false).level(Random.IntRange(2, 4));
                w.curCharges = w.maxCharges;
                rewardOptions.add(w);
                for (Item i : rewardOptions) {
                    i.cursed = false;
                }
            }
            return rooms;
        }

        public static boolean given() {
            return given;
        }

        public static boolean isOld() {
            return oldQuest;
        }

        public static void oldProcess(Mob mob) {
            if (spawned && oldQuest && given && !completed && Dungeon.depth != 20 && (alternative && mob instanceof Monk || !alternative && mob instanceof Golem)) {
                Dungeon.level.drop((Item)new DwarfToken(), (int)mob.pos).sprite.drop();
            }
        }

        public static void oldComplete() {
            reward = null;
            completed = true;
            Statistics.questScores[3] = 4000;
            Notes.remove(Notes.Landmark.IMP);
        }

        public static void complete(int score) {
            completed = true;
            Quest.score = score;
            Statistics.questScores[3] = Statistics.questScores[3] + score;
            Notes.remove(Notes.Landmark.IMP);
        }

        public static boolean isCompleted() {
            return spawned && completed;
        }

        public static boolean earnedShop() {
            return completed && (oldQuest || score > 2000);
        }

        static {
            oldQuest = false;
            rewardOptions = new ArrayList();
            mirrorUsed = false;
        }
    }
}

