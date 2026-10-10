/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.builders;

import com.shatteredpixel.shatteredpixeldungeon.levels.builders.RegularBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class LineBuilder
extends RegularBuilder {
    @Override
    public ArrayList<Room> build(ArrayList<Room> rooms) {
        this.setupRooms(rooms);
        if (this.entrance == null) {
            return null;
        }
        float direction = Random.Float(0.0f, 360.0f);
        ArrayList<Room> branchable = new ArrayList<Room>();
        this.entrance.setSize();
        this.entrance.setPos(0, 0);
        this.mainPathRooms.add(0, this.entrance);
        if (this.exit != null) {
            this.mainPathRooms.add(this.exit);
        }
        if (this.shop != null) {
            LineBuilder.placeRoom(rooms, this.entrance, this.shop, direction + 180.0f);
        }
        Room prev = this.entrance;
        float[] pathTunnels = (float[])this.pathTunnelChances.clone();
        for (int i = 1; i < this.mainPathRooms.size(); ++i) {
            Room r = (Room)this.mainPathRooms.get(i);
            int tunnels = Random.chances(pathTunnels);
            if (tunnels == -1) {
                pathTunnels = (float[])this.pathTunnelChances.clone();
                tunnels = Random.chances(pathTunnels);
            }
            int n = tunnels;
            pathTunnels[n] = pathTunnels[n] - 1.0f;
            for (int j = 0; j < tunnels; ++j) {
                ConnectionRoom t = ConnectionRoom.createRoom();
                LineBuilder.placeRoom(rooms, prev, t, direction + Random.Float(-this.pathVariance, this.pathVariance));
                branchable.add(t);
                rooms.add(t);
                prev = t;
            }
            LineBuilder.placeRoom(rooms, prev, r, direction + Random.Float(-this.pathVariance, this.pathVariance));
            branchable.add(r);
            prev = r;
        }
        ArrayList<Room> roomsToBranch = new ArrayList<Room>();
        roomsToBranch.addAll(this.multiConnections);
        roomsToBranch.addAll(this.singleConnections);
        this.weightRooms(branchable);
        if (!this.createBranches(rooms, branchable, roomsToBranch, this.branchTunnelChances)) {
            return null;
        }
        LineBuilder.findNeighbours(rooms);
        for (Room r : rooms) {
            for (Room n : r.neigbours) {
                if (n.connected.containsKey(r) || !(Random.Float() < this.extraConnectionChance)) continue;
                r.connect(n);
            }
        }
        return rooms;
    }
}

