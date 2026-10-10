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

public class BranchesBuilder
extends RegularBuilder {
    @Override
    public ArrayList<Room> build(ArrayList<Room> rooms) {
        this.setupRooms(rooms);
        if (this.entrance == null) {
            return null;
        }
        ArrayList<Room> branchable = new ArrayList<Room>();
        this.entrance.setSize();
        this.entrance.setPos(0, 0);
        branchable.add(this.entrance);
        if (this.shop != null) {
            BranchesBuilder.placeRoom(branchable, this.entrance, this.shop, Random.Float(360.0f));
        }
        int mainBranchRooms = Math.max(Random.Int(2, 3), this.mainPathRooms.size());
        float[] pathTunnels = (float[])this.pathTunnelChances.clone();
        for (int i = 1; i < mainBranchRooms; ++i) {
            float result;
            Room prev = this.entrance;
            Room r = (Room)this.mainPathRooms.get(0);
            int tunnels = Random.chances(pathTunnels);
            if (tunnels == -1) {
                pathTunnels = (float[])this.pathTunnelChances.clone();
                tunnels = Random.chances(pathTunnels);
            }
            int n = tunnels;
            pathTunnels[n] = pathTunnels[n] - 1.0f;
            for (int j = 0; j < tunnels; ++j) {
                float result2;
                ConnectionRoom t = ConnectionRoom.createRoom();
                while ((result2 = BranchesBuilder.placeRoom(rooms, prev, t, Random.Float(360.0f))) == -1.0f) {
                }
                branchable.add(t);
                rooms.add(t);
                prev = t;
            }
            while ((result = BranchesBuilder.placeRoom(rooms, prev, r, Random.Float(360.0f))) == -1.0f) {
            }
            branchable.add(r);
            this.mainPathRooms.remove(r);
        }
        ArrayList<Room> roomsToBranch = new ArrayList<Room>();
        roomsToBranch.addAll(this.mainPathRooms);
        roomsToBranch.addAll(this.multiConnections);
        if (this.exit != null) {
            roomsToBranch.add(this.exit);
        }
        roomsToBranch.addAll(this.singleConnections);
        if (!this.createBranches(rooms, branchable, roomsToBranch, this.branchTunnelChances)) {
            return null;
        }
        BranchesBuilder.findNeighbours(rooms);
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
        PointF entranceCenter = new PointF(this.entrance.center());
        float toCenter = BranchesBuilder.angleBetweenPoints(new PointF((float)(r.left + r.right) / 2.0f, (float)(r.top + r.bottom) / 2.0f), entranceCenter);
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

