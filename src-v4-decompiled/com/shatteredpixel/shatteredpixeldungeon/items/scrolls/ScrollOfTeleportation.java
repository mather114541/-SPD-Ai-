/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ScrollOfTeleportation
extends Scroll {
    public ScrollOfTeleportation() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_TELEPORT;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfTeleportation.curUser.belongings.backpack);
        Sample.INSTANCE.play("sounds/read.mp3");
        if (ScrollOfTeleportation.teleportPreferringUnseen(curUser)) {
            this.readAnimation();
        }
        this.identify();
    }

    public static boolean teleportToLocation(Char ch, int pos) {
        PathFinder.buildDistanceMap(pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
        if (PathFinder.distance[ch.pos] == Integer.MAX_VALUE || !Dungeon.level.passable[pos] && !Dungeon.level.avoid[pos] || Actor.findChar(pos) != null && Actor.findChar(pos) != ch) {
            if (ch == Dungeon.hero) {
                GLog.w(Messages.get(ScrollOfTeleportation.class, "cant_reach", new Object[0]), new Object[0]);
            }
            return false;
        }
        ScrollOfTeleportation.appear(ch, pos);
        Dungeon.level.occupyCell(ch);
        Buff.detach(ch, Roots.class);
        if (ch == Dungeon.hero) {
            Dungeon.observe();
            GameScene.updateFog();
        }
        return true;
    }

    public static boolean teleportChar(Char ch) {
        return ScrollOfTeleportation.teleportChar(ch, ScrollOfTeleportation.class);
    }

    public static boolean teleportChar(Char ch, Class source) {
        int pos;
        if (!(Dungeon.level instanceof RegularLevel) || Dungeon.level.locked) {
            return ScrollOfTeleportation.teleportInNonRegularLevel(ch, false);
        }
        if (Char.hasProp(ch, Char.Property.IMMOVABLE) || ch.isImmune(source)) {
            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele", new Object[0]), new Object[0]);
            return false;
        }
        int count = 20;
        do {
            pos = Dungeon.level.randomRespawnCell(ch);
        } while (count-- > 0 && (pos == -1 || Dungeon.level.secret[pos]));
        if (pos == -1) {
            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele", new Object[0]), new Object[0]);
            return false;
        }
        ScrollOfTeleportation.appear(ch, pos);
        Dungeon.level.occupyCell(ch);
        Buff.detach(ch, Roots.class);
        if (ch == Dungeon.hero) {
            GLog.i(Messages.get(ScrollOfTeleportation.class, "tele", new Object[0]), new Object[0]);
            Dungeon.observe();
            GameScene.updateFog();
            Dungeon.hero.interrupt();
        }
        return true;
    }

    public static boolean teleportPreferringUnseen(Hero hero) {
        if (!(Dungeon.level instanceof RegularLevel) || Dungeon.level.locked) {
            return ScrollOfTeleportation.teleportInNonRegularLevel(hero, true);
        }
        RegularLevel level = (RegularLevel)Dungeon.level;
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (Room r : level.rooms()) {
            if (r instanceof SpecialRoom || r instanceof VaultTreasureRoom) {
                boolean locked = false;
                for (Point p : r.getPoints()) {
                    int terr = level.map[level.pointToCell(p)];
                    if (terr != 10 && terr != 31 && terr != 13) continue;
                    locked = true;
                    break;
                }
                if (locked) continue;
            }
            for (Point p : r.charPlaceablePoints(level)) {
                int cell = level.pointToCell(p);
                if (!level.passable[cell] || level.visited[cell] || level.secret[cell] || Actor.findChar(cell) != null) continue;
                candidates.add(cell);
            }
        }
        if (candidates.isEmpty()) {
            return ScrollOfTeleportation.teleportChar(hero);
        }
        int pos = (Integer)Random.element(candidates);
        boolean secretDoor = false;
        int doorPos = -1;
        Room room = level.room(pos);
        if (room instanceof SpecialRoom || room instanceof VaultTreasureRoom) {
            Room.Door entrance = null;
            if (room instanceof SpecialRoom) {
                entrance = ((SpecialRoom)room).entrance();
            }
            if (room instanceof VaultTreasureRoom) {
                entrance = ((VaultTreasureRoom)room).entrance();
            }
            if (entrance != null) {
                doorPos = level.pointToCell(entrance);
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (room.inside(level.cellToPoint(doorPos + i)) || !level.passable[doorPos + i] || Actor.findChar(doorPos + i) != null) continue;
                    secretDoor = room instanceof SecretRoom;
                    pos = doorPos + i;
                    break;
                }
            }
        }
        GLog.i(Messages.get(ScrollOfTeleportation.class, "tele", new Object[0]), new Object[0]);
        ScrollOfTeleportation.appear(hero, pos);
        Dungeon.level.occupyCell(hero);
        Buff.detach(hero, Roots.class);
        if (secretDoor && level.map[doorPos] == 16) {
            Sample.INSTANCE.play("sounds/secret.mp3");
            int oldValue = Dungeon.level.map[doorPos];
            GameScene.discoverTile(doorPos, oldValue);
            Dungeon.level.discover(doorPos);
            ScrollOfMagicMapping.discover(doorPos);
        }
        Dungeon.observe();
        GameScene.updateFog();
        return true;
    }

    private static boolean teleportInNonRegularLevel(Char ch, boolean preferNotSeen) {
        int pos;
        if (Char.hasProp(ch, Char.Property.IMMOVABLE)) {
            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele", new Object[0]), new Object[0]);
            return false;
        }
        ArrayList<Integer> visibleValid = new ArrayList<Integer>();
        ArrayList<Integer> notVisibleValid = new ArrayList<Integer>();
        ArrayList<Integer> notSeenValid = new ArrayList<Integer>();
        boolean[] passable = Dungeon.level.passable;
        if (Char.hasProp(ch, Char.Property.LARGE)) {
            passable = BArray.and(passable, Dungeon.level.openSpace, null);
        }
        PathFinder.buildDistanceMap(ch.pos, passable);
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE || Dungeon.level.secret[i] || Actor.findChar(i) != null) continue;
            if (preferNotSeen && !Dungeon.level.visited[i]) {
                notSeenValid.add(i);
                continue;
            }
            if (Dungeon.level.heroFOV[i]) {
                visibleValid.add(i);
                continue;
            }
            notVisibleValid.add(i);
        }
        if (!notSeenValid.isEmpty()) {
            pos = (Integer)Random.element(notSeenValid);
        } else if (!notVisibleValid.isEmpty()) {
            pos = (Integer)Random.element(notVisibleValid);
        } else if (!visibleValid.isEmpty()) {
            pos = (Integer)Random.element(visibleValid);
        } else {
            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele", new Object[0]), new Object[0]);
            return false;
        }
        ScrollOfTeleportation.appear(ch, pos);
        Dungeon.level.occupyCell(ch);
        Buff.detach(ch, Roots.class);
        if (ch == Dungeon.hero) {
            GLog.i(Messages.get(ScrollOfTeleportation.class, "tele", new Object[0]), new Object[0]);
            Dungeon.observe();
            GameScene.updateFog();
            Dungeon.hero.interrupt();
        }
        return true;
    }

    public static void appear(Char ch, int pos) {
        if (rl.RLMode.headless) return;   // headless：整段均为 sprite/粒子/相机 UI，无渲染时跳过（否则 ch.sprite 为 null NPE）
        ch.sprite.interruptMotion();
        if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[ch.pos]) {
            Sample.INSTANCE.play("sounds/teleport.mp3");
        }
        if (Dungeon.level.heroFOV[ch.pos] && ch != Dungeon.hero) {
            CellEmitter.get(ch.pos).start(Speck.factory(2), 0.2f, 3);
        }
        ch.move(pos, false);
        if (ch.pos == pos) {
            ch.sprite.interruptMotion();
            ch.sprite.place(pos);
        }
        if (ch.invisible == 0) {
            ch.sprite.alpha(0.0f);
            ch.sprite.parent.add(new AlphaTweener(ch.sprite, 1.0f, 0.4f));
        }
        if (Dungeon.level.heroFOV[pos] || ch == Dungeon.hero) {
            ch.sprite.emitter().start(Speck.factory(2), 0.2f, 3);
        } else if (Camera.main.followTarget() == ch.sprite) {
            Camera.main.panFollow(null, 5.0f);
        }
    }

    public static void appearVFX(Char ch) {
        if (Dungeon.level.heroFOV[ch.pos]) {
            Sample.INSTANCE.play("sounds/teleport.mp3");
        }
        Dungeon.level.occupyCell(ch);
        if (ch.invisible == 0) {
            ch.sprite.alpha(0.0f);
            ch.sprite.parent.add(new AlphaTweener(ch.sprite, 1.0f, 0.4f));
        }
        if (Dungeon.level.heroFOV[ch.pos]) {
            ch.sprite.emitter().start(Speck.factory(2), 0.2f, 3);
        }
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

