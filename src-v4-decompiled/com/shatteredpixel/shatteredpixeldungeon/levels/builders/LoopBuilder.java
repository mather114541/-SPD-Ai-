/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.builders;

import com.shatteredpixel.shatteredpixeldungeon.levels.builders.RegularBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class LoopBuilder
extends RegularBuilder {
    private int curveExponent = 0;
    private float curveIntensity = 1.0f;
    private float curveOffset = 0.0f;
    private PointF loopCenter;

    public LoopBuilder setLoopShape(int exponent, float intensity, float offset) {
        this.curveExponent = Math.abs(exponent);
        this.curveIntensity = intensity % 1.0f;
        this.curveOffset = offset % 0.5f;
        return this;
    }

    private float targetAngle(float percentAlong) {
        return 360.0f * (float)((double)this.curveIntensity * this.curveEquation(percentAlong += this.curveOffset) + (double)((1.0f - this.curveIntensity) * percentAlong) - (double)this.curveOffset);
    }

    private double curveEquation(double x) {
        return Math.pow(4.0, 2 * this.curveExponent) * Math.pow(x % 0.5 - 0.25, 2 * this.curveExponent + 1) + 0.25 + 0.5 * Math.floor(2.0 * x);
    }

    @Override
    public ArrayList<Room> build(ArrayList<Room> rooms) {
        this.setupRooms(rooms);
        if (this.entrance == null) {
            return null;
        }
        this.entrance.setSize();
        this.entrance.setPos(0, 0);
        float startAngle = Random.Float(0.0f, 360.0f);
        this.mainPathRooms.add(0, this.entrance);
        if (this.exit != null) {
            this.mainPathRooms.add((this.mainPathRooms.size() + 1) / 2, this.exit);
        }
        ArrayList<Room> loop = new ArrayList<Room>();
        float[] pathTunnels = (float[])this.pathTunnelChances.clone();
        for (Room r : this.mainPathRooms) {
            loop.add(r);
            int tunnels = Random.chances(pathTunnels);
            if (tunnels == -1) {
                pathTunnels = (float[])this.pathTunnelChances.clone();
                tunnels = Random.chances(pathTunnels);
            }
            int n = tunnels;
            pathTunnels[n] = pathTunnels[n] - 1.0f;
            for (int j = 0; j < tunnels; ++j) {
                loop.add(ConnectionRoom.createRoom());
            }
        }
        Room prev = this.entrance;
        for (int i = 1; i < loop.size(); ++i) {
            float targetAngle;
            Room r = (Room)loop.get(i);
            if (LoopBuilder.placeRoom(rooms, prev, r, targetAngle = startAngle + this.targetAngle((float)i / (float)loop.size())) != -1.0f) {
                prev = r;
                if (rooms.contains(prev)) continue;
                rooms.add(prev);
                continue;
            }
            return null;
        }
        while (!prev.connect(this.entrance)) {
            ConnectionRoom c = ConnectionRoom.createRoom();
            if (LoopBuilder.placeRoom(loop, prev, c, LoopBuilder.angleBetweenRooms(prev, this.entrance)) == -1.0f) {
                return null;
            }
            loop.add(c);
            rooms.add(c);
            prev = c;
        }
        if (this.shop != null) {
            float angle;
            int tries = 10;
            while ((angle = LoopBuilder.placeRoom(loop, this.entrance, this.shop, Random.Float(360.0f))) == -1.0f && --tries >= 0) {
            }
            if (angle == -1.0f) {
                return null;
            }
        }
        this.loopCenter = new PointF();
        for (Room r : loop) {
            this.loopCenter.x += (float)(r.left + r.right) / 2.0f;
            this.loopCenter.y += (float)(r.top + r.bottom) / 2.0f;
        }
        this.loopCenter.x /= (float)loop.size();
        this.loopCenter.y /= (float)loop.size();
        ArrayList<Room> branchable = new ArrayList<Room>(loop);
        ArrayList<Room> roomsToBranch = new ArrayList<Room>();
        roomsToBranch.addAll(this.multiConnections);
        roomsToBranch.addAll(this.singleConnections);
        this.weightRooms(branchable);
        if (!this.createBranches(rooms, branchable, roomsToBranch, this.branchTunnelChances)) {
            return null;
        }
        LoopBuilder.findNeighbours(rooms);
        for (Room r : rooms) {
            for (Room n : r.neigbours) {
                if (n.connected.containsKey(r) || !(Random.Float() < this.extraConnectionChance)) continue;
                r.connect(n);
            }
        }
        return rooms;
    }

    @Override
    protected float randomBranchAngle(Room r) {
        if (this.loopCenter == null) {
            return super.randomBranchAngle(r);
        }
        float toCenter = LoopBuilder.angleBetweenPoints(new PointF((float)(r.left + r.right) / 2.0f, (float)(r.top + r.bottom) / 2.0f), this.loopCenter);
        if (toCenter < 0.0f) {
            toCenter += 360.0f;
        }
        float currAngle = Random.Float(360.0f);
        for (int i = 0; i < 4; ++i) {
            float newAngle = Random.Float(360.0f);
            if (!(Math.abs(toCenter - newAngle) < Math.abs(toCenter - currAngle))) continue;
            currAngle = newAngle;
        }
        return currAngle;
    }
}

