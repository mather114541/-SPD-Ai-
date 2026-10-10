/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FungalCore;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;

public class MineGiantRoom
extends CaveRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 0.0f, 1.0f};
    }

    @Override
    protected float fill() {
        return 0.7f;
    }

    @Override
    public int mobSpawnWeight() {
        return 1;
    }

    @Override
    public void paint(Level level) {
        super.paint(level);
        if (Blacksmith.Quest.Type() == 1) {
            Painter.fillEllipse(level, this, 3, 1);
            for (int i = 0; i < this.width() * this.height() / 2; ++i) {
                Point r = this.random(1);
                if (level.map[level.pointToCell(r)] == 4) continue;
                Painter.set(level, r, 35);
            }
            Point p = this.center();
            CrystalSpire m = new CrystalSpire();
            m.pos = level.pointToCell(p);
            level.mobs.add(m);
            Painter.set(level, p, 1);
        } else if (Blacksmith.Quest.Type() == 2) {
            Painter.fillEllipse(level, this, 3, 1);
            for (Object n : this.connected.keySet()) {
                if (n instanceof SecretRoom || ((Room.Door)this.connected.get((Object)n)).type != Room.Door.Type.REGULAR) continue;
                if (Random.Int(10) == 0) {
                    ((Room.Door)this.connected.get(n)).set(Room.Door.Type.EMPTY);
                } else {
                    ((Room.Door)this.connected.get(n)).set(Room.Door.Type.WALL);
                }
                ((Room.Door)this.connected.get(n)).lockTypeChanges(true);
            }
            ArrayList<Room.Door> doors = new ArrayList<Room.Door>();
            for (Room.Door d : this.connected.values()) {
                if (d.type != Room.Door.Type.WALL) continue;
                doors.add(d);
            }
            for (Point p : this.getPoints()) {
                int cell = level.pointToCell(p);
                if (level.map[cell] != 1) continue;
                float dist = 1000.0f;
                for (Room.Door d : doors) {
                    dist = Math.min(dist, Point.distance(p, d));
                }
                float val = Random.Float((float)Math.pow(dist = GameMath.gate(1.0f, dist - 0.5f, 3.1f), 2.0));
                if (val <= 0.75f || dist <= 1.0f) {
                    Painter.set(level, cell, 36);
                    continue;
                }
                if (!(val <= 5.0f) || !(dist <= 3.0f)) continue;
                Painter.set(level, cell, 20);
            }
            Point center = this.center();
            Rect centerArea = new Rect(center.x - 2, center.y - 2, center.x + 3, center.y + 3);
            Painter.fillEllipse(level, centerArea, 0, 36);
            Painter.fill(level, centerArea, 2, 20);
            GnollGeomancer g = new GnollGeomancer();
            g.pos = level.pointToCell(center);
            Buff.affect(g, GnollGeomancer.RockArmor.class).setShield(50);
            level.mobs.add(g);
        } else if (Blacksmith.Quest.Type() == 3) {
            Painter.fillEllipse(level, this, 2, 15);
            Painter.fillEllipse(level, this, 3, 2);
            Painter.fillEllipse(level, this, 4, 15);
            Painter.fillEllipse(level, this, 5, 2);
            Point p = this.center();
            FungalCore m = new FungalCore();
            m.pos = level.pointToCell(p);
            level.mobs.add(m);
        } else {
            Painter.fillEllipse(level, this, 3, 1);
        }
    }
}

