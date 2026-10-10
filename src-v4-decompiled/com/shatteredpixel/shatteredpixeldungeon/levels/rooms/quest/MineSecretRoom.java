/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class MineSecretRoom
extends SecretRoom {
    @Override
    public int maxWidth() {
        return 7;
    }

    @Override
    public int maxHeight() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        this.entrance().set(Room.Door.Type.HIDDEN);
        if (Blacksmith.Quest.Type() == 1) {
            Painter.fill(level, this, 1, 35);
        } else {
            if (Blacksmith.Quest.Type() == 2) {
                Painter.fill(level, this, 1, 14);
                level.drop((Item)new DarkGold().quantity((int)Random.NormalIntRange((int)4, (int)5)), (int)level.pointToCell((Point)this.center())).type = Heap.Type.CHEST;
                return;
            }
            if (Blacksmith.Quest.Type() == 3) {
                Painter.fill(level, this, 1, 15);
            } else {
                Painter.fill(level, this, 1, 1);
            }
        }
        int goldAmount = Random.NormalIntRange(4, 5);
        for (int i = 0; i < goldAmount; ++i) {
            int cell;
            while (level.map[cell = level.pointToCell(this.random(1))] == 12) {
            }
            Painter.set(level, this.random(1), 12);
        }
    }
}

