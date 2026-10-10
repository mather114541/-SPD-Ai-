/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndWandmaker;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Wandmaker
extends NPC {
    public Wandmaker() {
        this.spriteClass = WandmakerSprite.class;
        this.properties.add(Char.Property.IMMOVABLE);
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.WANDMAKER;
    }

    @Override
    protected boolean act() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            this.die(null);
            return true;
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
        if (Quest.given) {
            Item item;
            switch (Quest.type) {
                default: {
                    item = Dungeon.hero.belongings.getItem(CorpseDust.class);
                    break;
                }
                case 2: {
                    item = Dungeon.hero.belongings.getItem(Embers.class);
                    break;
                }
                case 3: {
                    item = Dungeon.hero.belongings.getItem(Rotberry.Seed.class);
                }
            }
            if (item != null) {
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        GameScene.show(new WndWandmaker(Wandmaker.this, item));
                    }
                });
            } else {
                String msg;
                switch (Quest.type) {
                    default: {
                        msg = Messages.get(this, "reminder_dust", Messages.titleCase(Dungeon.hero.name()));
                        break;
                    }
                    case 2: {
                        msg = Messages.get(this, "reminder_ember", Messages.titleCase(Dungeon.hero.name()));
                        break;
                    }
                    case 3: {
                        msg = Messages.get(this, "reminder_berry", Messages.titleCase(Dungeon.hero.name()));
                    }
                }
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        GameScene.show(new WndQuest(Wandmaker.this, msg));
                    }
                });
            }
        } else {
            Object msg1 = "";
            Object msg2 = "";
            switch (Dungeon.hero.heroClass) {
                case WARRIOR: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_warrior", new Object[0]);
                    break;
                }
                case ROGUE: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_rogue", new Object[0]);
                    break;
                }
                case MAGE: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_mage", Messages.titleCase(Dungeon.hero.name()));
                    break;
                }
                case HUNTRESS: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_huntress", new Object[0]);
                    break;
                }
                case DUELIST: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_duelist", new Object[0]);
                    break;
                }
                case CLERIC: {
                    msg1 = (String)msg1 + Messages.get(this, "intro_cleric", new Object[0]);
                }
            }
            msg1 = (String)msg1 + Messages.get(this, "intro_1", new Object[0]);
            switch (Quest.type) {
                case 1: {
                    msg2 = (String)msg2 + Messages.get(this, "intro_dust", new Object[0]);
                    break;
                }
                case 2: {
                    msg2 = (String)msg2 + Messages.get(this, "intro_ember", new Object[0]);
                    break;
                }
                case 3: {
                    msg2 = (String)msg2 + Messages.get(this, "intro_berry", new Object[0]);
                }
            }
            msg2 = (String)msg2 + Messages.get(this, "intro_2", new Object[0]);
            Object msg1Final = msg1;
            Object msg2Final = msg2;
            Game.runOnRenderThread(new Callback(){
                final /* synthetic */ String val$msg1Final;
                final /* synthetic */ String val$msg2Final;
                {
                    this.val$msg1Final = string;
                    this.val$msg2Final = string2;
                }

                @Override
                public void call() {
                    GameScene.show(new WndQuest(Wandmaker.this, this.val$msg1Final){

                        @Override
                        public void hide() {
                            super.hide();
                            GameScene.show(new WndQuest(Wandmaker.this, val$msg2Final));
                        }
                    });
                }
            });
            Quest.given = true;
        }
        return true;
    }

    public static class Quest {
        private static int type;
        private static boolean spawned;
        private static boolean given;
        public static Wand wand1;
        public static Wand wand2;
        private static final String NODE = "wandmaker";
        private static final String SPAWNED = "spawned";
        private static final String TYPE = "type";
        private static final String GIVEN = "given";
        private static final String WAND1 = "wand1";
        private static final String WAND2 = "wand2";
        private static final String RITUALPOS = "ritualpos";
        private static boolean questRoomSpawned;

        public static void reset() {
            spawned = false;
            type = 0;
            wand1 = null;
            wand2 = null;
        }

        public static void storeInBundle(Bundle bundle) {
            Bundle node = new Bundle();
            node.put(SPAWNED, spawned);
            if (spawned) {
                node.put(TYPE, type);
                node.put(GIVEN, given);
                node.put(WAND1, wand1);
                node.put(WAND2, wand2);
                if (type == 2) {
                    node.put(RITUALPOS, CeremonialCandle.ritualPos);
                }
            }
            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {
            Bundle node = bundle.getBundle(NODE);
            if (!node.isNull() && (spawned = node.getBoolean(SPAWNED))) {
                type = node.getInt(TYPE);
                given = node.getBoolean(GIVEN);
                wand1 = (Wand)node.get(WAND1);
                wand2 = (Wand)node.get(WAND2);
                if (type == 2) {
                    CeremonialCandle.ritualPos = node.getInt(RITUALPOS);
                }
            } else {
                Quest.reset();
            }
        }

        public static void spawnWandmaker(Level level, Room room) {
            if (questRoomSpawned) {
                boolean validPos;
                questRoomSpawned = false;
                Wandmaker npc = new Wandmaker();
                int tries = 0;
                int dist = 2;
                do {
                    validPos = true;
                    if (tries > 30 && dist > 0) {
                        tries = 0;
                        --dist;
                    }
                    npc.pos = level.pointToCell(room.random(dist));
                    if (npc.pos == level.entrance() || level.solid[npc.pos]) {
                        validPos = false;
                    }
                    for (int i : PathFinder.NEIGHBOURS4) {
                        if (level.map[npc.pos + i] != 5) continue;
                        validPos = false;
                    }
                    if (level.traps.get(npc.pos) != null || !level.passable[npc.pos] || level.map[npc.pos] == 14) {
                        validPos = false;
                    }
                    ++tries;
                } while (!validPos);
                level.mobs.add(npc);
                spawned = true;
                given = false;
                wand1 = (Wand)Generator.random(Generator.Category.WAND);
                Quest.wand1.cursed = false;
                wand1.upgrade();
                wand2 = (Wand)Generator.random(Generator.Category.WAND);
                ArrayList<Wand> toUndo = new ArrayList<Wand>();
                while (wand2.getClass() == wand1.getClass()) {
                    toUndo.add(wand2);
                    wand2 = (Wand)Generator.random(Generator.Category.WAND);
                }
                for (Item item : toUndo) {
                    Generator.undoDrop(item);
                }
                Quest.wand2.cursed = false;
                wand2.upgrade();
            }
        }

        public static ArrayList<Room> spawnRoom(ArrayList<Room> rooms) {
            questRoomSpawned = false;
            if (!spawned && (type != 0 || Dungeon.depth > 6 && Random.Int(10 - Dungeon.depth) == 0)) {
                if (type == 0) {
                    type = Random.Int(3) + 1;
                }
                switch (type) {
                    default: {
                        rooms.add(new MassGraveRoom());
                        break;
                    }
                    case 2: {
                        rooms.add(new RitualSiteRoom());
                        break;
                    }
                    case 3: {
                        rooms.add(new RotGardenRoom());
                    }
                }
                questRoomSpawned = true;
            }
            return rooms;
        }

        public static boolean active() {
            if (wand1 == null || wand2 == null || !(Dungeon.level instanceof RegularLevel) || Dungeon.hero == null) {
                return false;
            }
            if (type == 1) {
                if (((RegularLevel)Dungeon.level).room(Dungeon.hero.pos) instanceof MassGraveRoom) {
                    return true;
                }
                for (Buff buff : Dungeon.hero.buffs()) {
                    if (!(buff instanceof CorpseDust.DustGhostSpawner)) continue;
                    return true;
                }
                return false;
            }
            if (type == 2) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (!(mob instanceof Elemental.NewbornFireElemental)) continue;
                    return true;
                }
                if (((RegularLevel)Dungeon.level).room(Dungeon.hero.pos) instanceof RitualSiteRoom) {
                    int candles = 0;
                    if (Dungeon.hero.belongings.getItem(CeremonialCandle.class) != null) {
                        candles += Dungeon.hero.belongings.getItem(CeremonialCandle.class).quantity();
                    }
                    if (candles >= 4) {
                        return true;
                    }
                    for (Heap h : Dungeon.level.heaps.valueList()) {
                        if (!(((RegularLevel)Dungeon.level).room(h.pos) instanceof RitualSiteRoom)) continue;
                        for (Item i : h.items) {
                            if (!(i instanceof CeremonialCandle)) continue;
                            candles += i.quantity();
                        }
                    }
                    if (candles >= 4) {
                        return true;
                    }
                }
                return false;
            }
            if (((RegularLevel)Dungeon.level).room(Dungeon.hero.pos) instanceof RotGardenRoom) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (!(mob instanceof RotHeart)) continue;
                    return true;
                }
            }
            return false;
        }

        public static void complete() {
            wand1 = null;
            wand2 = null;
            Notes.remove(Notes.Landmark.WANDMAKER);
            if (type == 1) {
                Statistics.questScores[1] = Statistics.questScores[1] + 2000;
            }
        }
    }
}

