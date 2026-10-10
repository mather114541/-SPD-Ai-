/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class MagicalFireRoom
extends SpecialRoom {
    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Room.Door door = this.entrance();
        door.set(Room.Door.Type.REGULAR);
        Point firePos = this.center();
        EmptyRoom behindFire = new EmptyRoom();
        if (door.x == this.left || door.x == this.right) {
            firePos.y = this.top + 1;
            while (firePos.y != this.bottom) {
                Blob.seed(level.pointToCell(firePos), 1, EternalFire.class, level);
                Painter.set(level, firePos, 14);
                ++firePos.y;
            }
            if (door.x == this.left) {
                behindFire.set(firePos.x + 1, this.top + 1, this.right - 1, this.bottom - 1);
            } else {
                behindFire.set(this.left + 1, this.top + 1, firePos.x - 1, this.bottom - 1);
            }
        } else {
            firePos.x = this.left + 1;
            while (firePos.x != this.right) {
                Blob.seed(level.pointToCell(firePos), 1, EternalFire.class, level);
                Painter.set(level, firePos, 14);
                ++firePos.x;
            }
            if (door.y == this.top) {
                behindFire.set(this.left + 1, firePos.y + 1, this.right - 1, this.bottom - 1);
            } else {
                behindFire.set(this.left + 1, this.top + 1, this.right - 1, firePos.y - 1);
            }
        }
        Painter.fill(level, behindFire, 14);
        boolean honeyPot = Random.Int(2) == 0;
        int n = Random.IntRange(3, 4);
        for (int i = 0; i < n; ++i) {
            int pos;
            while (level.heaps.get(pos = level.pointToCell(behindFire.random(0))) != null) {
            }
            if (honeyPot) {
                level.drop(new Honeypot(), pos);
                honeyPot = false;
                continue;
            }
            level.drop(MagicalFireRoom.prize(level), pos);
        }
        level.addItemToSpawn(new PotionOfFrost());
    }

    private static Item prize(Level level) {
        Item prize;
        if (Random.Int(3) != 0 && (prize = level.findPrizeItem()) != null) {
            return prize;
        }
        return Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.SCROLL, Generator.Category.FOOD, Generator.Category.GOLD));
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        Blob fire = l.blobs.get(EternalFire.class);
        if (fire != null && fire.volume > 0) {
            int cell = l.pointToCell(p);
            if (l.map[cell] == 14) {
                return false;
            }
            if (fire.cur[cell] > 0) {
                return false;
            }
            for (int i : PathFinder.NEIGHBOURS4) {
                if (fire.cur[cell + i] <= 0) continue;
                return false;
            }
        }
        return super.canPlaceCharacter(p, l);
    }

    public static class EternalFire
    extends Blob {
        @Override
        protected void evolve() {
            Freezing freeze = (Freezing)Dungeon.level.blobs.get(Freezing.class);
            Blizzard bliz = (Blizzard)Dungeon.level.blobs.get(Blizzard.class);
            Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
            boolean clearAll = false;
            Level l = Dungeon.level;
            for (int i = this.area.left - 1; i <= this.area.right; ++i) {
                for (int j = this.area.top - 1; j <= this.area.bottom; ++j) {
                    int cell = i + j * l.width();
                    if (this.cur[cell] > 0) {
                        if (l.water[cell]) {
                            this.cur[cell] = 0;
                            clearAll = true;
                        }
                        if (fire != null && fire.volume > 0 && fire.cur[cell] > 0) {
                            fire.clear(cell);
                        }
                        for (int k : PathFinder.NEIGHBOURS9) {
                            if (freeze != null && freeze.volume > 0 && freeze.cur[cell + k] > 0) {
                                freeze.clear(cell);
                                this.cur[cell] = 0;
                                clearAll = true;
                            }
                            if (bliz == null || bliz.volume <= 0 || bliz.cur[cell + k] <= 0) continue;
                            bliz.clear(cell);
                            this.cur[cell] = 0;
                            clearAll = true;
                        }
                    }
                    if (this.cur[cell] > 0 || this.cur[cell - 1] > 0 || this.cur[cell + 1] > 0 || this.cur[cell - Dungeon.level.width()] > 0 || this.cur[cell + Dungeon.level.width()] > 0) {
                        Char ch;
                        if (Dungeon.level.flamable[cell] && (fire == null || fire.volume == 0 || fire.cur[cell] == 0)) {
                            GameScene.add(Blob.seed(cell, 4, Fire.class));
                        }
                        if ((ch = Actor.findChar(cell)) != null && !ch.isImmune(this.getClass())) {
                            Buff.affect(ch, Burning.class).reignite(ch, 4.0f);
                        }
                        if (Dungeon.level.heaps.get(cell) != null && Dungeon.level.map[cell] != 14 && Dungeon.level.map[cell] != 29) {
                            ((Heap)Dungeon.level.heaps.get(cell)).burn();
                        }
                    }
                    this.off[cell] = this.cur[cell];
                    this.volume += this.off[cell];
                }
            }
            if (clearAll) {
                this.fullyClear();
            }
        }

        @Override
        public void seed(Level level, int cell, int amount) {
            super.seed(level, cell, amount);
            level.updateCellFlags(cell);
        }

        @Override
        public void clear(int cell) {
            if (this.volume > 0 && this.cur[cell] > 0) {
                this.fullyClear();
            }
        }

        @Override
        public void fullyClear() {
            super.fullyClear();
            Dungeon.level.buildFlagMaps();
        }

        @Override
        public void use(BlobEmitter emitter) {
            super.use(emitter);
            emitter.pour(ElmoParticle.FACTORY, 0.02f);
        }

        @Override
        public String tileDesc() {
            return Messages.get(this, "desc", new Object[0]);
        }

        @Override
        public void onBuildFlagMaps(Level l) {
            if (this.volume > 0) {
                for (int i = 0; i < l.length(); ++i) {
                    this.onUpdateCellFlags(l, i);
                }
            }
        }

        @Override
        public void onUpdateCellFlags(Level l, int cell) {
            if (this.volume > 0 && this.cur[cell] > 0) {
                l.passable[cell] = false;
                l.avoid[cell] = false;
            }
        }
    }
}

