/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Point;

public class SecretHoneypotRoom
extends SecretRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Point brokenPotPos = this.center();
        brokenPotPos.x = (brokenPotPos.x + this.entrance().x) / 2;
        brokenPotPos.y = (brokenPotPos.y + this.entrance().y) / 2;
        Honeypot.ShatteredPot pot = new Honeypot.ShatteredPot();
        level.drop(pot, level.pointToCell(brokenPotPos));
        Bee bee = new Bee();
        bee.spawn(Dungeon.depth);
        bee.HP = bee.HT;
        bee.pos = level.pointToCell(brokenPotPos);
        level.mobs.add(bee);
        bee.setPotInfo(level.pointToCell(brokenPotPos), null);
        this.placeItem(new Honeypot(), level);
        this.placeItem(new Bomb().random(), level);
        this.entrance().set(Room.Door.Type.HIDDEN);
    }

    private void placeItem(Item item, Level level) {
        int itemPos;
        while (level.heaps.get(itemPos = level.pointToCell(this.random())) != null) {
        }
        level.drop(item, itemPos);
    }
}

