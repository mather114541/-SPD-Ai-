/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.builders;

import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.MazeConnectionRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public abstract class RegularBuilder
extends Builder {
    protected float pathVariance = 45.0f;
    protected float pathLength = 0.25f;
    protected float[] pathLenJitterChances = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    protected float[] pathTunnelChances = new float[]{2.0f, 2.0f, 1.0f};
    protected float[] branchTunnelChances = new float[]{1.0f, 1.0f, 0.0f};
    protected float extraConnectionChance = 0.3f;
    protected Room entrance = null;
    protected Room exit = null;
    protected Room shop = null;
    protected ArrayList<Room> mainPathRooms = new ArrayList();
    protected ArrayList<Room> multiConnections = new ArrayList();
    protected ArrayList<Room> singleConnections = new ArrayList();

    public RegularBuilder setPathVariance(float var) {
        this.pathVariance = var;
        return this;
    }

    public RegularBuilder setPathLength(float len, float[] jitter) {
        this.pathLength = len;
        this.pathLenJitterChances = jitter;
        return this;
    }

    public RegularBuilder setTunnelLength(float[] path, float[] branch) {
        this.pathTunnelChances = path;
        this.branchTunnelChances = branch;
        return this;
    }

    public RegularBuilder setExtraConnectionChance(float chance) {
        this.extraConnectionChance = chance;
        return this;
    }

    protected void setupRooms(ArrayList<Room> rooms) {
        for (Room r : rooms) {
            r.setEmpty();
        }
        this.shop = null;
        this.exit = null;
        this.entrance = null;
        this.mainPathRooms.clear();
        this.singleConnections.clear();
        this.multiConnections.clear();
        for (Room r : rooms) {
            if (r.isEntrance()) {
                this.entrance = r;
                continue;
            }
            if (r.isExit()) {
                this.exit = r;
                continue;
            }
            if (r instanceof ShopRoom && r.maxConnections(0) == 1) {
                this.shop = r;
                continue;
            }
            if (r.maxConnections(0) > 1) {
                this.multiConnections.add(r);
                continue;
            }
            if (r.maxConnections(0) != 1) continue;
            this.singleConnections.add(r);
        }
        this.weightRooms(this.multiConnections);
        Random.shuffle(this.multiConnections);
        this.multiConnections = new ArrayList<Room>(new LinkedHashSet<Room>(this.multiConnections));
        Random.shuffle(this.multiConnections);
        int roomsOnMainPath = (int)((float)this.multiConnections.size() * this.pathLength) + Random.chances(this.pathLenJitterChances);
        while (roomsOnMainPath > 0 && !this.multiConnections.isEmpty()) {
            Room r;
            r = this.multiConnections.remove(0);
            roomsOnMainPath = r instanceof StandardRoom ? (roomsOnMainPath -= ((StandardRoom)r).sizeFactor()) : --roomsOnMainPath;
            this.mainPathRooms.add(r);
        }
    }

    protected void weightRooms(ArrayList<Room> rooms) {
        for (Room r : rooms.toArray(new Room[0])) {
            if (!(r instanceof StandardRoom)) continue;
            for (int i = 1; i < ((StandardRoom)r).connectionWeight(); ++i) {
                rooms.add(r);
            }
        }
    }

    protected boolean createBranches(ArrayList<Room> rooms, ArrayList<Room> branchable, ArrayList<Room> roomsToBranch, float[] connChances) {
        int i = 0;
        ArrayList<ConnectionRoom> connectingRoomsThisBranch = new ArrayList<ConnectionRoom>();
        int failedBranchAttempts = 0;
        float[] connectionChances = (float[])connChances.clone();
        while (i < roomsToBranch.size()) {
            float angle;
            int tries;
            int j;
            Room curr;
            if (failedBranchAttempts > 100) {
                return false;
            }
            Room r = roomsToBranch.get(i);
            connectingRoomsThisBranch.clear();
            do {
                curr = Random.element(branchable);
            } while (r instanceof SecretRoom && curr instanceof ConnectionRoom);
            int connectingRooms = Random.chances(connectionChances);
            if (connectingRooms == -1) {
                connectionChances = (float[])connChances.clone();
                connectingRooms = Random.chances(connectionChances);
            }
            int n = connectingRooms;
            connectionChances[n] = connectionChances[n] - 1.0f;
            for (j = 0; j < connectingRooms; ++j) {
                ConnectionRoom connectionRoom = r instanceof SecretRoom ? new MazeConnectionRoom() : ConnectionRoom.createRoom();
                tries = 3;
                while ((angle = RegularBuilder.placeRoom(rooms, curr, connectionRoom, this.randomBranchAngle(curr))) == -1.0f && --tries > 0) {
                }
                if (angle == -1.0f) {
                    connectionRoom.clearConnections();
                    for (Room room : connectingRoomsThisBranch) {
                        room.clearConnections();
                        rooms.remove(room);
                    }
                    connectingRoomsThisBranch.clear();
                    break;
                }
                connectingRoomsThisBranch.add(connectionRoom);
                rooms.add(connectionRoom);
                curr = connectionRoom;
            }
            if (connectingRoomsThisBranch.size() != connectingRooms) {
                ++failedBranchAttempts;
                continue;
            }
            tries = 10;
            while ((angle = RegularBuilder.placeRoom(rooms, curr, r, this.randomBranchAngle(curr))) == -1.0f && --tries > 0) {
            }
            if (angle == -1.0f) {
                r.clearConnections();
                for (Room room : connectingRoomsThisBranch) {
                    room.clearConnections();
                    rooms.remove(room);
                }
                connectingRoomsThisBranch.clear();
                ++failedBranchAttempts;
                continue;
            }
            for (j = 0; j < connectingRoomsThisBranch.size(); ++j) {
                if (Random.Int(3) > 1) continue;
                branchable.add((Room)connectingRoomsThisBranch.get(j));
            }
            if (r.maxConnections(0) > 1 && Random.Int(3) == 0) {
                if (r instanceof StandardRoom) {
                    for (j = 0; j < ((StandardRoom)r).connectionWeight(); ++j) {
                        branchable.add(r);
                    }
                } else {
                    branchable.add(r);
                }
            }
            ++i;
        }
        return true;
    }

    protected float randomBranchAngle(Room r) {
        return Random.Float(360.0f);
    }
}

