/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultBossElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ImpStatue;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class VaultFinalRoom
extends SpecialRoom {
    private Point entryDoor;
    private Point lockedDoor;
    private int warnState = 0;
    private boolean lockTriggered = false;
    private static final String ENTRY_DOOR_X = "entry_door_x";
    private static final String ENTRY_DOOR_Y = "entry_door_y";
    private static final String LOCKED_DOOR_X = "locked_door_x";
    private static final String LOCKED_DOOR_Y = "locked_door_y";
    private static final String WARN_STATE = "warn_state";
    private static final String LOCK_TRIGGERED = "lock_triggered";

    @Override
    public int minWidth() {
        return 21;
    }

    @Override
    public int maxWidth() {
        return 21;
    }

    @Override
    public int minHeight() {
        return 21;
    }

    @Override
    public int maxHeight() {
        return 21;
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public boolean canConnect(Point p) {
        return Math.abs(p.x - this.center().x) <= 5 || Math.abs(p.y - this.center().y) <= 5;
    }

    @Override
    public boolean canConnect(Room r) {
        if (r.isEntrance()) {
            return false;
        }
        for (Room r1 : r.connected.keySet()) {
            if (r1.isEntrance()) {
                return false;
            }
            for (Room r2 : r1.connected.keySet()) {
                if (r2.isEntrance()) {
                    return false;
                }
                for (Room r3 : r2.connected.keySet()) {
                    if (!r3.isEntrance()) continue;
                    return false;
                }
            }
        }
        return super.canConnect(r);
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return false;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fillEllipse(level, this, 5, 14);
        Point c = this.center();
        MarkerTiles marker = new MarkerTiles();
        marker.pos(c.x - 4, c.y - 4);
        level.customTiles.add(marker);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.REGULAR);
        EmptyRoom entry = new EmptyRoom();
        EmptyRoom treasure = new EmptyRoom();
        if (entrance.x == this.left) {
            entry.set(this.left + 1, this.top + 5, this.left + 3, this.bottom - 5);
            this.entryDoor = new Point(this.left + 4, c.y);
            treasure.set(this.right - 3, this.top + 3, this.right - 1, this.bottom - 3);
            this.lockedDoor = new Point(this.right - 4, c.y);
        } else if (entrance.x == this.right) {
            treasure.set(this.left + 1, this.top + 3, this.left + 3, this.bottom - 3);
            this.lockedDoor = new Point(this.left + 4, c.y);
            entry.set(this.right - 3, this.top + 5, this.right - 1, this.bottom - 5);
            this.entryDoor = new Point(this.right - 4, c.y);
        } else if (entrance.y == this.top) {
            entry.set(this.left + 5, this.top + 1, this.right - 5, this.top + 3);
            this.entryDoor = new Point(c.x, this.top + 4);
            treasure.set(this.left + 3, this.bottom - 3, this.right - 3, this.bottom - 1);
            this.lockedDoor = new Point(c.x, this.bottom - 4);
        } else {
            treasure.set(this.left + 3, this.top + 1, this.right - 3, this.top + 3);
            this.lockedDoor = new Point(c.x, this.top + 4);
            entry.set(this.left + 5, this.bottom - 3, this.right - 5, this.bottom - 1);
            this.entryDoor = new Point(c.x, this.bottom - 4);
        }
        Painter.set(level, this.entryDoor, 5);
        Painter.set(level, this.lockedDoor, 10);
        Painter.fill(level, entry, 32);
        Carpet carpet = new Carpet();
        carpet.setRect(entry.left, entry.top, entry.width(), entry.height());
        level.customTiles.add(carpet);
        ArrayList<Integer> treasureSpots = new ArrayList<Integer>();
        if (entry.width() > entry.height()) {
            Painter.set(level, entry.left + 1, entry.top + 1, 33);
            carpet.overrideTile(level, entry.left + 1, entry.top + 1, 81);
            Painter.set(level, entry.left + 3, entry.top + 1, 33);
            carpet.overrideTile(level, entry.left + 3, entry.top + 1, 81);
            Painter.set(level, entry.left + 7, entry.top + 1, 33);
            carpet.overrideTile(level, entry.left + 7, entry.top + 1, 81);
            Painter.set(level, entry.left + 9, entry.top + 1, 33);
            carpet.overrideTile(level, entry.left + 9, entry.top + 1, 81);
            treasureSpots.add(treasure.left + 1 + (treasure.top + 1) * level.width());
            treasureSpots.add(treasure.left + 3 + (treasure.top + 1) * level.width());
            treasureSpots.add(treasure.left + 5 + (treasure.top + 1) * level.width());
            treasureSpots.add(treasure.left + 7 + (treasure.top + 1) * level.width());
            treasureSpots.add(treasure.left + 9 + (treasure.top + 1) * level.width());
            treasureSpots.add(treasure.left + 11 + (treasure.top + 1) * level.width());
            treasureSpots.add(treasure.left + 13 + (treasure.top + 1) * level.width());
        } else {
            Painter.set(level, entry.left + 1, entry.top + 1, 33);
            carpet.overrideTile(level, entry.left + 1, entry.top + 1, 81);
            Painter.set(level, entry.left + 1, entry.top + 3, 33);
            carpet.overrideTile(level, entry.left + 1, entry.top + 3, 81);
            Painter.set(level, entry.left + 1, entry.top + 7, 33);
            carpet.overrideTile(level, entry.left + 1, entry.top + 7, 81);
            Painter.set(level, entry.left + 1, entry.top + 9, 33);
            carpet.overrideTile(level, entry.left + 1, entry.top + 9, 81);
            treasureSpots.add(treasure.left + 1 + (treasure.top + 1) * level.width());
            treasureSpots.add(treasure.left + 1 + (treasure.top + 3) * level.width());
            treasureSpots.add(treasure.left + 1 + (treasure.top + 5) * level.width());
            treasureSpots.add(treasure.left + 1 + (treasure.top + 7) * level.width());
            treasureSpots.add(treasure.left + 1 + (treasure.top + 9) * level.width());
            treasureSpots.add(treasure.left + 1 + (treasure.top + 11) * level.width());
            treasureSpots.add(treasure.left + 1 + (treasure.top + 13) * level.width());
        }
        Painter.fill(level, treasure, 14);
        Iterator<Object> iterator = treasureSpots.iterator();
        while (iterator.hasNext()) {
            int cell = (Integer)iterator.next();
            Painter.set(level, cell, 11);
        }
        level.drop(new ImpStatue(), (Integer)treasureSpots.remove(3));
        Random.shuffle(treasureSpots);
        for (Item i : Imp.Quest.rewardOptions) {
            level.drop(i, (Integer)treasureSpots.remove(0));
        }
        Imp.Quest.rewardOptions.clear();
        VaultTreasure vis = new VaultTreasure();
        vis.setRect(treasure.left, treasure.top - 1, treasure.width(), treasure.height() + 1);
        level.customTiles.add(vis);
    }

    public void processHeroStep(Hero hero) {
        if (!this.lockTriggered) {
            Point heroPos = Dungeon.level.cellToPoint(hero.pos);
            int distance = Math.max(Math.abs(heroPos.x - this.lockedDoor.x), Math.abs(heroPos.y - this.lockedDoor.y));
            if (distance <= 3) {
                Level.set(Dungeon.level.pointToCell(this.entryDoor), 10);
                GameScene.updateMap(Dungeon.level.pointToCell(this.entryDoor));
                VaultBossElemental boss = new VaultBossElemental();
                boss.pos = Dungeon.level.pointToCell(this.center());
                GameScene.add(boss, 1.0f);
                boss.fieldOfView = new boolean[Dungeon.level.length()];
                Dungeon.level.updateFieldOfView(boss, boss.fieldOfView);
                boss.aggro(Dungeon.hero);
                boss.sprite.turnTo(boss.pos, Dungeon.hero.pos);
                boss.setElementalForm(boss.curForm());
                Dungeon.level.seal();
                this.lockTriggered = true;
            } else if (distance == 4 && this.warnState < 2) {
                GLog.n(Messages.get(VaultFinalRoom.class, "final_warning", new Object[0]), new Object[0]);
                Sample.INSTANCE.play("sounds/chargeup.mp3");
                hero.interrupt();
                this.warnState = 2;
            } else if (distance >= 5 && this.warnState == 2) {
                this.warnState = 1;
            } else if (distance <= 10 && this.warnState < 1) {
                hero.interrupt();
                ShatteredPixelDungeon.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        boolean[] prepCriteria = new boolean[4];
                        for (Item i : Dungeon.hero.belongings) {
                            if (i.level() < 2) continue;
                            if (i instanceof MagesStaff) {
                                if (((MagesStaff)i).wandClass() == null) continue;
                                prepCriteria[2] = true;
                                continue;
                            }
                            if (i instanceof MeleeWeapon) {
                                prepCriteria[0] = true;
                                continue;
                            }
                            if (i instanceof MissileWeapon) {
                                prepCriteria[1] = true;
                                continue;
                            }
                            if (i instanceof Armor) {
                                prepCriteria[2] = true;
                                continue;
                            }
                            if (!(i instanceof Wand)) continue;
                            prepCriteria[3] = true;
                        }
                        boolean prepared = true;
                        boolean[] blArray = prepCriteria;
                        int n = blArray.length;
                        for (int i = 0; i < n; ++i) {
                            Boolean b = blArray[i];
                            prepared = prepared && b != false;
                        }
                        if (!Imp.Quest.mirrorUsed) {
                            prepared = false;
                        }
                        if (!prepared) {
                            GameScene.show(new WndTitledMessage(new ImpSprite(), Messages.titleCase(Messages.get(Imp.class, "name", new Object[0])), Messages.get(VaultFinalRoom.class, "imp_warning_unprepared", new Object[0])));
                        } else {
                            GameScene.show(new WndTitledMessage(new ImpSprite(), Messages.titleCase(Messages.get(Imp.class, "name", new Object[0])), Messages.get(VaultFinalRoom.class, "imp_warning_prepared", new Object[0])));
                        }
                    }
                });
                this.warnState = 1;
            } else if (distance > 12) {
                this.warnState = 0;
            }
        }
    }

    public boolean elementalWasSummoned() {
        return this.lockTriggered;
    }

    public void unlock() {
        Level.set(Dungeon.level.pointToCell(this.entryDoor), 5);
        GameScene.updateMap(Dungeon.level.pointToCell(this.entryDoor));
        Level.set(Dungeon.level.pointToCell(this.lockedDoor), 5);
        GameScene.updateMap(Dungeon.level.pointToCell(this.lockedDoor));
        for (Heap h : Dungeon.level.heaps.valueList()) {
            Item i = h.peek();
            if (!(i instanceof Ring) || i.levelKnown) continue;
            i.identify(false);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ENTRY_DOOR_X, this.entryDoor.x);
        bundle.put(ENTRY_DOOR_Y, this.entryDoor.y);
        bundle.put(LOCKED_DOOR_X, this.lockedDoor.x);
        bundle.put(LOCKED_DOOR_Y, this.lockedDoor.y);
        bundle.put(WARN_STATE, this.warnState);
        bundle.put(LOCK_TRIGGERED, this.lockTriggered);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.entryDoor = new Point();
        this.entryDoor.x = bundle.getInt(ENTRY_DOOR_X);
        this.entryDoor.y = bundle.getInt(ENTRY_DOOR_Y);
        this.lockedDoor = new Point();
        this.lockedDoor.x = bundle.getInt(LOCKED_DOOR_X);
        this.lockedDoor.y = bundle.getInt(LOCKED_DOOR_Y);
        this.warnState = bundle.getInt(WARN_STATE);
        this.lockTriggered = bundle.getBoolean(LOCK_TRIGGERED);
    }

    public static class MarkerTiles
    extends CustomTilemap {
        final int TEX_WIDTH = 256;

        public MarkerTiles() {
            this.texture = "environment/custom_tiles/city_quest.png";
            this.tileH = 9;
            this.tileW = 9;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(this.mapSimpleImage(5, 4, 256), 9);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            if (tileX == 0 || tileY == 0 || tileX == this.tileW - 1 || tileY == this.tileH - 1) {
                return null;
            }
            if (!(tileX != 1 && tileX != this.tileW - 2 || tileY != 1 && tileY != this.tileH - 2)) {
                return null;
            }
            return super.image(tileX, tileY);
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(this, "name", new Object[0]);
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(this, "desc", new Object[0]);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (this.tileW == 7) {
                this.tileH = 9;
                this.tileW = 9;
                --this.tileX;
                --this.tileY;
            }
        }
    }

    public static class VaultTreasure
    extends CustomTilemap {
        public VaultTreasure() {
            this.texture = "environment/custom_tiles/city_quest.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            for (int i = 0; i < data.length; ++i) {
                data[i] = -1;
                if (i < this.tileW) {
                    if (i == 0) {
                        data[i] = 84;
                    }
                    if (i != this.tileW - 1) continue;
                    data[i] = 83;
                    continue;
                }
                int cell = this.tileX + Dungeon.level.width() * this.tileY;
                if (Dungeon.level.map[cell += i % this.tileW + i / this.tileW * Dungeon.level.width()] == 11) {
                    data[i] = 115;
                    continue;
                }
                if (Dungeon.level.map[cell - Dungeon.level.width()] == 4 || Dungeon.level.map[cell - Dungeon.level.width()] == 12) {
                    data[i] = 98;
                    if (Dungeon.level.map[cell + 1] == 4) {
                        int n = i;
                        data[n] = data[n] + 1;
                        continue;
                    }
                    if (Dungeon.level.map[cell - 1] != 4) continue;
                    int n = i;
                    data[n] = data[n] + 2;
                    continue;
                }
                data[i] = Dungeon.level.map[cell + 1] == 4 ? 96 : (Dungeon.level.map[cell - 1] == 4 ? 97 : 112 + DungeonTileSheet.tileVariance[cell] / 34);
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            if (tileY < 1) {
                return null;
            }
            return super.image(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            int cell = tileX + this.tileX + (tileY + this.tileY) * Dungeon.level.width();
            if (Dungeon.level.map[cell] != 11) {
                return Messages.get(this, "desc", new Object[0]);
            }
            return super.desc(tileX, tileY);
        }
    }
}

