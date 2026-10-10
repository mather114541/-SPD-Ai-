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

public class FigureEightBuilder
extends RegularBuilder {
    private int curveExponent = 0;
    private float curveIntensity = 1.0f;
    private float curveOffset = 0.0f;
    private Room landmarkRoom;
    ArrayList<Room> firstLoop;
    ArrayList<Room> secondLoop;
    PointF firstLoopCenter;
    PointF secondLoopCenter;

    public FigureEightBuilder setLoopShape(int exponent, float intensity, float offset) {
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

    public FigureEightBuilder setLandmarkRoom(Room room) {
        this.landmarkRoom = room;
        return this;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public ArrayList<Room> build(ArrayList<Room> rooms) {
        void var8_21;
        void var8_16;
        this.setupRooms(rooms);
        if (this.landmarkRoom == null) {
            for (Room r : this.mainPathRooms) {
                if (r.maxConnections(0) < 4 || this.landmarkRoom != null && this.landmarkRoom.minWidth() * this.landmarkRoom.minHeight() >= r.minWidth() * r.minHeight()) continue;
                this.landmarkRoom = r;
            }
            if (!this.multiConnections.isEmpty()) {
                this.mainPathRooms.add((Room)this.multiConnections.remove(0));
            }
        }
        this.mainPathRooms.remove(this.landmarkRoom);
        this.multiConnections.remove(this.landmarkRoom);
        float startAngle = Random.Float(0.0f, 360.0f);
        int roomsOnFirstLoop = this.mainPathRooms.size() / 2;
        if (this.mainPathRooms.size() % 2 == 1) {
            roomsOnFirstLoop += Random.Int(2);
        }
        ArrayList roomsToLoop = (ArrayList)this.mainPathRooms.clone();
        ArrayList<Room> firstLoopTemp = new ArrayList<Room>();
        firstLoopTemp.add(this.landmarkRoom);
        for (int i = 0; i < roomsOnFirstLoop; ++i) {
            firstLoopTemp.add((Room)roomsToLoop.remove(0));
        }
        firstLoopTemp.add((firstLoopTemp.size() + 1) / 2, this.entrance);
        float[] pathTunnels = (float[])this.pathTunnelChances.clone();
        this.firstLoop = new ArrayList();
        for (Room room : firstLoopTemp) {
            this.firstLoop.add(room);
            int tunnels = Random.chances(pathTunnels);
            if (tunnels == -1) {
                pathTunnels = (float[])this.pathTunnelChances.clone();
                tunnels = Random.chances(pathTunnels);
            }
            int n = tunnels;
            pathTunnels[n] = pathTunnels[n] - 1.0f;
            for (int j = 0; j < tunnels; ++j) {
                this.firstLoop.add(ConnectionRoom.createRoom());
            }
        }
        ArrayList<Room> secondLoopTemp = new ArrayList<Room>();
        secondLoopTemp.add(this.landmarkRoom);
        secondLoopTemp.addAll(roomsToLoop);
        if (this.exit != null) {
            secondLoopTemp.add((secondLoopTemp.size() + 1) / 2, this.exit);
        }
        this.secondLoop = new ArrayList();
        for (Room r : secondLoopTemp) {
            this.secondLoop.add(r);
            int tunnels = Random.chances(pathTunnels);
            if (tunnels == -1) {
                pathTunnels = (float[])this.pathTunnelChances.clone();
                tunnels = Random.chances(pathTunnels);
            }
            int n = tunnels;
            pathTunnels[n] = pathTunnels[n] - 1.0f;
            for (int j = 0; j < tunnels; ++j) {
                this.secondLoop.add(ConnectionRoom.createRoom());
            }
        }
        this.landmarkRoom.setSize();
        this.landmarkRoom.setPos(0, 0);
        Room room = this.landmarkRoom;
        for (int i = 1; i < this.firstLoop.size(); ++i) {
            float targetAngle;
            void var8_14;
            Room r = this.firstLoop.get(i);
            if (FigureEightBuilder.placeRoom(rooms, (Room)var8_14, r, targetAngle = startAngle + this.targetAngle((float)i / (float)this.firstLoop.size())) != -1.0f) {
                Room room2 = r;
                if (rooms.contains(room2)) continue;
                rooms.add(room2);
                continue;
            }
            return null;
        }
        while (!var8_16.connect(this.landmarkRoom)) {
            ConnectionRoom c = ConnectionRoom.createRoom();
            if (FigureEightBuilder.placeRoom(rooms, (Room)var8_16, c, FigureEightBuilder.angleBetweenRooms((Room)var8_16, this.landmarkRoom)) == -1.0f) {
                return null;
            }
            this.firstLoop.add(c);
            rooms.add(c);
            ConnectionRoom connectionRoom = c;
        }
        Room room3 = this.landmarkRoom;
        startAngle += 180.0f;
        for (int i = 1; i < this.secondLoop.size(); ++i) {
            float targetAngle;
            void var8_19;
            Room r = this.secondLoop.get(i);
            if (FigureEightBuilder.placeRoom(rooms, (Room)var8_19, r, targetAngle = startAngle + this.targetAngle((float)i / (float)this.secondLoop.size())) != -1.0f) {
                Room room4 = r;
                if (rooms.contains(room4)) continue;
                rooms.add(room4);
                continue;
            }
            return null;
        }
        while (!var8_21.connect(this.landmarkRoom)) {
            ConnectionRoom c = ConnectionRoom.createRoom();
            if (FigureEightBuilder.placeRoom(rooms, (Room)var8_21, c, FigureEightBuilder.angleBetweenRooms((Room)var8_21, this.landmarkRoom)) == -1.0f) {
                return null;
            }
            this.secondLoop.add(c);
            rooms.add(c);
            ConnectionRoom connectionRoom = c;
        }
        if (this.shop != null) {
            float angle;
            int tries = 10;
            while ((angle = FigureEightBuilder.placeRoom(rooms, this.entrance, this.shop, Random.Float(360.0f))) == -1.0f && --tries >= 0) {
            }
            if (angle == -1.0f) {
                return null;
            }
        }
        this.firstLoopCenter = new PointF();
        for (Room r : this.firstLoop) {
            this.firstLoopCenter.x += (float)(r.left + r.right) / 2.0f;
            this.firstLoopCenter.y += (float)(r.top + r.bottom) / 2.0f;
        }
        this.firstLoopCenter.x /= (float)this.firstLoop.size();
        this.firstLoopCenter.y /= (float)this.firstLoop.size();
        this.secondLoopCenter = new PointF();
        for (Room r : this.secondLoop) {
            this.secondLoopCenter.x += (float)(r.left + r.right) / 2.0f;
            this.secondLoopCenter.y += (float)(r.top + r.bottom) / 2.0f;
        }
        this.secondLoopCenter.x /= (float)this.secondLoop.size();
        this.secondLoopCenter.y /= (float)this.secondLoop.size();
        ArrayList<Room> branchable = new ArrayList<Room>(this.firstLoop);
        branchable.addAll(this.secondLoop);
        branchable.remove(this.landmarkRoom);
        ArrayList<Room> roomsToBranch = new ArrayList<Room>();
        roomsToBranch.addAll(this.multiConnections);
        roomsToBranch.addAll(this.singleConnections);
        this.weightRooms(branchable);
        if (!this.createBranches(rooms, branchable, roomsToBranch, this.branchTunnelChances)) {
            return null;
        }
        FigureEightBuilder.findNeighbours(rooms);
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
        PointF center = this.firstLoop.contains(r) ? this.firstLoopCenter : this.secondLoopCenter;
        if (center == null) {
            return super.randomBranchAngle(r);
        }
        float toCenter = FigureEightBuilder.angleBetweenPoints(new PointF((float)(r.left + r.right) / 2.0f, (float)(r.top + r.bottom) / 2.0f), center);
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

