/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class SecretHoardRoom
extends SecretRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Class trapClass = Random.Int(2) == 0 ? RockfallTrap.class : (Dungeon.depth >= 10 ? DisintegrationTrap.class : PoisonDartTrap.class);
        int totalGold = (this.width() - 2) * (this.height() - 2) / 2;
        float goldRatio = 8.0f / (float)totalGold;
        for (int i = 0; i < totalGold; ++i) {
            int goldPos;
            while (level.heaps.get(goldPos = level.pointToCell(this.random())) != null) {
            }
            Item gold = new Gold().random();
            gold.quantity(Math.round((float)gold.quantity() * goldRatio));
            level.drop(gold, goldPos);
        }
        for (Point p : this.getPoints()) {
            if (Random.Int(2) != 0 || level.map[level.pointToCell(p)] != 1) continue;
            level.setTrap(((Trap)Reflection.newInstance(trapClass)).reveal(), level.pointToCell(p));
            Painter.set(level, p, 18);
        }
        this.entrance().set(Room.Door.Type.HIDDEN);
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return false;
    }
}

