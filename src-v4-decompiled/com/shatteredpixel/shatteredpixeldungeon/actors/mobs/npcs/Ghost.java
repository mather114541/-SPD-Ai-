/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FetidRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GreatCrab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Ghost
extends NPC {
    public Ghost() {
        this.spriteClass = GhostSprite.class;
        this.flying = true;
        this.state = this.WANDERING = new Wandering();
        this.properties.add(Char.Property.LARGE);
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.GHOST;
    }

    @Override
    protected boolean act() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            this.die(null);
            Notes.remove(this.landmark());
            return true;
        }
        return super.act();
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public float speed() {
        return 0.5f;
    }

    @Override
    protected Char chooseEnemy() {
        return null;
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
        this.sprite.turnTo(this.pos, c.pos);
        Sample.INSTANCE.play("sounds/ghost.mp3");
        if (c != Dungeon.hero) {
            return super.interact(c);
        }
        if (Quest.given) {
            if (Quest.weapon != null) {
                if (Quest.processed) {
                    Game.runOnRenderThread(new Callback(){

                        @Override
                        public void call() {
                            GameScene.show(new WndSadGhost(Ghost.this, Quest.type));
                        }
                    });
                } else {
                    Game.runOnRenderThread(new Callback(){

                        @Override
                        public void call() {
                            switch (Quest.type) {
                                default: {
                                    GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "rat_2", new Object[0])));
                                    break;
                                }
                                case 2: {
                                    GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "gnoll_2", new Object[0])));
                                    break;
                                }
                                case 3: {
                                    GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "crab_2", new Object[0])));
                                }
                            }
                        }
                    });
                }
            }
        } else {
            String txt_quest;
            Mob questBoss;
            switch (Quest.type) {
                default: {
                    questBoss = new FetidRat();
                    txt_quest = Messages.get(this, "rat_1", Messages.titleCase(Dungeon.hero.name()));
                    break;
                }
                case 2: {
                    questBoss = new GnollTrickster();
                    txt_quest = Messages.get(this, "gnoll_1", Messages.titleCase(Dungeon.hero.name()));
                    break;
                }
                case 3: {
                    questBoss = new GreatCrab();
                    txt_quest = Messages.get(this, "crab_1", Messages.titleCase(Dungeon.hero.name()));
                }
            }
            questBoss.pos = Dungeon.level.randomRespawnCell(this);
            if (questBoss.pos != -1) {
                GameScene.add(questBoss);
                Quest.given = true;
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        GameScene.show(new WndQuest(Ghost.this, txt_quest){

                            @Override
                            public void hide() {
                                super.hide();
                                Music.INSTANCE.fadeOut(1.0f, new Callback(){

                                    @Override
                                    public void call() {
                                        if (Dungeon.level != null) {
                                            Dungeon.level.playLevelMusic();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        return true;
    }

    protected class Wandering
    extends Mob.Wandering {
        protected Wandering() {
        }

        @Override
        protected int randomDestination() {
            int pos = super.randomDestination();
            if (Dungeon.level.heaps.get(pos) != null || pos == Dungeon.level.exit()) {
                return -1;
            }
            return pos;
        }
    }

    public static class Quest {
        private static boolean spawned;
        private static int type;
        private static boolean given;
        private static boolean processed;
        private static int depth;
        public static Weapon weapon;
        public static Armor armor;
        public static Weapon.Enchantment enchant;
        public static Armor.Glyph glyph;
        private static final String NODE = "sadGhost";
        private static final String SPAWNED = "spawned";
        private static final String TYPE = "type";
        private static final String GIVEN = "given";
        private static final String PROCESSED = "processed";
        private static final String DEPTH = "depth";
        private static final String WEAPON = "weapon";
        private static final String ARMOR = "armor";
        private static final String ENCHANT = "enchant";
        private static final String GLYPH = "glyph";

        public static void reset() {
            spawned = false;
            weapon = null;
            armor = null;
            enchant = null;
            glyph = null;
        }

        public static void storeInBundle(Bundle bundle) {
            Bundle node = new Bundle();
            node.put(SPAWNED, spawned);
            if (spawned) {
                node.put(TYPE, type);
                node.put(GIVEN, given);
                node.put(DEPTH, depth);
                node.put(PROCESSED, processed);
                node.put(WEAPON, weapon);
                node.put(ARMOR, armor);
                if (enchant != null) {
                    node.put(ENCHANT, enchant);
                    node.put(GLYPH, glyph);
                }
            }
            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {
            Bundle node = bundle.getBundle(NODE);
            if (!node.isNull() && (spawned = node.getBoolean(SPAWNED))) {
                type = node.getInt(TYPE);
                given = node.getBoolean(GIVEN);
                processed = node.getBoolean(PROCESSED);
                depth = node.getInt(DEPTH);
                weapon = (Weapon)node.get(WEAPON);
                armor = (Armor)node.get(ARMOR);
                if (node.contains(ENCHANT)) {
                    enchant = (Weapon.Enchantment)node.get(ENCHANT);
                    glyph = (Armor.Glyph)node.get(GLYPH);
                }
            } else {
                Quest.reset();
            }
        }

        public static void spawn(SewerLevel level, Room room) {
            if (!spawned && Dungeon.depth > 1 && Random.Int(5 - Dungeon.depth) == 0) {
                Ghost ghost = new Ghost();
                do {
                    ghost.pos = level.pointToCell(room.random());
                } while (ghost.pos == -1 || level.solid[ghost.pos] || !level.openSpace[ghost.pos] || ghost.pos == level.exit());
                level.mobs.add(ghost);
                spawned = true;
                type = Dungeon.depth - 1;
                given = false;
                processed = false;
                depth = Dungeon.depth;
                switch (Random.chances(new float[]{0.0f, 0.0f, 10.0f, 6.0f, 3.0f, 1.0f})) {
                    default: {
                        armor = new LeatherArmor();
                        break;
                    }
                    case 3: {
                        armor = new MailArmor();
                        break;
                    }
                    case 4: {
                        armor = new ScaleArmor();
                        break;
                    }
                    case 5: {
                        armor = new PlateArmor();
                    }
                }
                int wepTier = Random.chances(new float[]{0.0f, 0.0f, 10.0f, 6.0f, 3.0f, 1.0f});
                weapon = (Weapon)Generator.random(Generator.wepTiers[wepTier - 1]);
                weapon.level(0);
                weapon.enchant(null);
                Quest.weapon.cursed = false;
                float itemLevelRoll = Random.Float();
                int itemLevel = itemLevelRoll < 0.5f ? 0 : (itemLevelRoll < 0.8f ? 1 : (itemLevelRoll < 0.95f ? 2 : 3));
                weapon.upgrade(itemLevel);
                armor.upgrade(itemLevel);
                enchant = Weapon.Enchantment.random(new Class[0]);
                glyph = Armor.Glyph.random(new Class[0]);
                float enchantRoll = Random.Float();
                if (enchantRoll > 0.2f * ParchmentScrap.enchantChanceMultiplier()) {
                    enchant = null;
                    glyph = null;
                }
            }
        }

        public static void process() {
            if (spawned && given && !processed && depth == Dungeon.depth) {
                GLog.n(Messages.get(Ghost.class, "find_me", new Object[0]), new Object[0]);
                Sample.INSTANCE.play("sounds/ghost.mp3");
                processed = true;
                Statistics.questScores[0] = Statistics.questScores[0] + 1000;
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        Music.INSTANCE.fadeOut(1.0f, new Callback(){

                            @Override
                            public void call() {
                                if (Dungeon.level != null) {
                                    Dungeon.level.playLevelMusic();
                                }
                            }
                        });
                    }
                });
            }
        }

        public static boolean active() {
            return spawned && given && !processed && depth == Dungeon.depth;
        }

        public static void complete() {
            weapon = null;
            armor = null;
            Notes.remove(Notes.Landmark.GHOST);
        }

        public static boolean processed() {
            return spawned && processed;
        }

        public static boolean completed() {
            return Quest.processed() && weapon == null && armor == null;
        }
    }
}

