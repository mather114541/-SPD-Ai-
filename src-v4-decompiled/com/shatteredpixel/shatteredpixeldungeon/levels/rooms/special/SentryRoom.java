/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SentrySprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class SentryRoom
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
        int y;
        int x;
        Point center;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        Room.Door entrance = this.entrance();
        do {
            center = this.center();
        } while (center.x == entrance.x || center.y == entrance.y);
        Point sentryPos = new Point();
        Point treasurePos = new Point();
        int dangerDist = 0;
        if (entrance.x == this.left) {
            sentryPos.set(this.right - 1, center.y);
            Painter.fill(level, this.left + 1, this.top + 1, 1, this.height() - 2, 1);
            if (entrance.y > center.y) {
                treasurePos.set(this.left + 1, (this.top + 1 + center.y) / 2);
                Painter.fill(level, this.left + 1, this.top + 1, 2, center.y - this.top - 1, 1);
            } else {
                treasurePos.set(this.left + 1, (this.bottom + center.y) / 2);
                Painter.fill(level, this.left + 1, center.y + 1, 2, this.bottom - center.y - 1, 1);
            }
            for (x = this.right - 3; x > this.left; --x) {
                if (level.map[x + center.y * level.width()] == 14) {
                    Painter.set(level, x, center.y, 26);
                    continue;
                }
                Painter.set(level, x, center.y, 25);
            }
            dangerDist = 2 * (this.width() - 5);
        } else if (entrance.x == this.right) {
            sentryPos.set(this.left + 1, center.y);
            Painter.fill(level, this.right - 1, this.top + 1, 1, this.height() - 2, 1);
            if (entrance.y > center.y) {
                treasurePos.set(this.right - 1, (this.top + 1 + center.y) / 2);
                Painter.fill(level, this.right - 2, this.top + 1, 2, center.y - this.top - 1, 1);
            } else {
                treasurePos.set(this.right - 1, (this.bottom + 1 + center.y) / 2);
                Painter.fill(level, this.right - 2, center.y + 1, 2, this.bottom - center.y - 1, 1);
            }
            for (x = this.left + 3; x < this.right; ++x) {
                if (level.map[x + center.y * level.width()] == 14) {
                    Painter.set(level, x, center.y, 26);
                    continue;
                }
                Painter.set(level, x, center.y, 25);
            }
            dangerDist = 2 * (this.width() - 5);
        } else if (entrance.y == this.top) {
            sentryPos.set(center.x, this.bottom - 1);
            Painter.fill(level, this.left + 1, this.top + 1, this.width() - 2, 1, 1);
            if (entrance.x > center.x) {
                treasurePos.set((this.left + 1 + center.x) / 2, this.top + 1);
                Painter.fill(level, this.left + 1, this.top + 1, center.x - this.left - 1, 2, 1);
            } else {
                treasurePos.set((this.right + center.x) / 2, this.top + 1);
                Painter.fill(level, center.x + 1, this.top + 1, this.right - center.x - 1, 2, 1);
            }
            for (y = this.bottom - 3; y > this.top; --y) {
                if (level.map[center.x + y * level.width()] == 14) {
                    Painter.set(level, center.x, y, 26);
                    continue;
                }
                Painter.set(level, center.x, y, 25);
            }
            dangerDist = 2 * (this.height() - 5);
        } else if (entrance.y == this.bottom) {
            sentryPos.set(center.x, this.top + 1);
            Painter.fill(level, this.left + 1, this.bottom - 1, this.width() - 2, 1, 1);
            if (entrance.x > center.x) {
                treasurePos.set((this.left + 1 + center.x) / 2, this.bottom - 1);
                Painter.fill(level, this.left + 1, this.bottom - 2, center.x - this.left - 1, 2, 1);
            } else {
                treasurePos.set((this.right + center.x) / 2, this.bottom - 1);
                Painter.fill(level, center.x + 1, this.bottom - 2, this.right - center.x - 1, 2, 1);
            }
            for (y = this.top + 3; y < this.bottom; ++y) {
                if (level.map[center.x + y * level.width()] == 14) {
                    Painter.set(level, center.x, y, 26);
                    continue;
                }
                Painter.set(level, center.x, y, 25);
            }
            dangerDist = 2 * (this.height() - 5);
        }
        Painter.set(level, sentryPos, 11);
        Sentry sentry = new Sentry();
        sentry.pos = level.pointToCell(sentryPos);
        sentry.room = new EmptyRoom();
        sentry.room.set((Rect)this);
        sentry.initialChargeDelay = sentry.curChargeDelay = (float)dangerDist / 3.0f + 0.1f;
        level.mobs.add(sentry);
        Painter.set(level, treasurePos, 11);
        level.drop((Item)SentryRoom.prize((Level)level), (int)level.pointToCell((Point)treasurePos)).type = Heap.Type.CHEST;
        level.addItemToSpawn(new PotionOfHaste());
        entrance.set(Room.Door.Type.REGULAR);
    }

    private static Item prize(Level level) {
        Item prize;
        if (Random.Int(2) == 0 && (prize = level.findPrizeItem()) != null) {
            return prize;
        }
        switch (Random.Int(5)) {
            default: {
                prize = Generator.randomWeapon(Dungeon.depth / 5 + 1);
                if (!((Weapon)prize).hasCurseEnchant()) break;
                ((Weapon)prize).enchant(null);
                break;
            }
            case 2: {
                prize = Generator.randomMissile(Dungeon.depth / 5 + 1);
                if (!((Weapon)prize).hasCurseEnchant()) break;
                ((Weapon)prize).enchant(null);
                break;
            }
            case 3: 
            case 4: {
                prize = Generator.randomArmor(Dungeon.depth / 5 + 1);
                if (!((Armor)prize).hasCurseGlyph()) break;
                ((Armor)prize).inscribe(null);
            }
        }
        prize.cursed = false;
        prize.cursedKnown = true;
        if (Random.Int(3) == 0) {
            prize.upgrade();
        }
        return prize;
    }

    @Override
    public boolean canConnect(Point p) {
        if (!super.canConnect(p)) {
            return false;
        }
        if (this.width() % 2 == 1 && p.x == this.center().x) {
            return false;
        }
        return this.height() % 2 != 1 || p.y != this.center().y;
    }

    public static class Sentry
    extends NPC {
        private float initialChargeDelay;
        private float curChargeDelay;
        private EmptyRoom room;
        private static final String INITIAL_DELAY = "initial_delay";
        private static final String CUR_DELAY = "cur_delay";
        private static final String ROOM = "room";

        public Sentry() {
            this.spriteClass = SentrySprite.Red.class;
            this.properties.add(Char.Property.IMMOVABLE);
        }

        @Override
        protected boolean act() {
            if (Dungeon.level.heroFOV[this.pos]) {
                Bestiary.setSeen(this.getClass());
            }
            if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
                this.fieldOfView = new boolean[Dungeon.level.length()];
            }
            Dungeon.level.updateFieldOfView(this, this.fieldOfView);
            if (this.properties().contains((Object)Char.Property.IMMOVABLE)) {
                this.throwItems();
            }
            if (Dungeon.hero != null) {
                if (this.fieldOfView[Dungeon.hero.pos] && Dungeon.level.map[Dungeon.hero.pos] == 14 && this.room.inside(Dungeon.level.cellToPoint(Dungeon.hero.pos)) && !Dungeon.hero.belongings.lostInventory()) {
                    if (this.curChargeDelay > 0.001f) {
                        if (this.curChargeDelay == this.initialChargeDelay && !rl.RLMode.headless) {
                            ((SentrySprite)this.sprite).charge();
                        }
                        this.curChargeDelay -= Dungeon.hero.cooldown();
                        if (Dungeon.hero.cooldown() >= 0.34f) {
                            Dungeon.hero.interrupt();
                        }
                    }
                    if (this.curChargeDelay <= 0.001f) {
                        this.curChargeDelay = 1.0f;
                        if (rl.RLMode.headless) {
                            this.onZapComplete(); // headless：跳过动画，直接结算攻击
                        } else {
                            this.sprite.zap(Dungeon.hero.pos);
                            ((SentrySprite)this.sprite).charge();
                        }
                    }
                    this.spend(Dungeon.hero.cooldown());
                    return true;
                }
                this.curChargeDelay = this.initialChargeDelay;
                if (!rl.RLMode.headless) this.sprite.idle();
                this.spend(Dungeon.hero.cooldown());
            } else {
                this.spend(1.0f);
            }
            return true;
        }

        public void onZapComplete() {
            if (Sentry.hit(this, Dungeon.hero, true)) {
                Dungeon.hero.damage(Random.NormalIntRange(2 + Dungeon.depth / 2, 4 + Dungeon.depth), new Eye.DeathGaze());
                if (!Dungeon.hero.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", this.name())), new Object[0]);
                }
            } else if (!rl.RLMode.headless) {
                Dungeon.hero.sprite.showStatus(0xFFFF00, Dungeon.hero.defenseVerb(), new Object[0]);
            }
        }

        @Override
        public CharSprite sprite() {
            SentrySprite sprite = (SentrySprite)super.sprite();
            if (this.curChargeDelay != this.initialChargeDelay) {
                sprite.charge();
            }
            return sprite;
        }

        @Override
        public int attackSkill(Char target) {
            return 20 + Dungeon.depth * 2;
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
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INITIAL_DELAY, this.initialChargeDelay);
            bundle.put(CUR_DELAY, this.curChargeDelay);
            bundle.put(ROOM, this.room);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.initialChargeDelay = bundle.getFloat(INITIAL_DELAY);
            this.curChargeDelay = bundle.getFloat(CUR_DELAY);
            this.room = (EmptyRoom)bundle.get(ROOM);
        }
    }
}

