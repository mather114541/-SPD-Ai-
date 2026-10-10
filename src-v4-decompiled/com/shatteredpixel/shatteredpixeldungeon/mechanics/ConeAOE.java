/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.mechanics;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class ConeAOE {
    public Ballistica coreRay;
    public ArrayList<Ballistica> outerRays = new ArrayList();
    public ArrayList<Ballistica> rays = new ArrayList();
    public HashSet<Integer> cells = new HashSet();

    public ConeAOE(Ballistica core, float degrees) {
        this(core, Float.POSITIVE_INFINITY, degrees, core.collisionProperties);
    }

    public ConeAOE(Ballistica core, float maxDist, float degrees, int ballisticaParams) {
        this.coreRay = core;
        PointF fromP = new PointF(Dungeon.level.cellToPoint(core.sourcePos));
        fromP.x += 0.5f;
        fromP.y += 0.5f;
        PointF toP = new PointF(Dungeon.level.cellToPoint(core.collisionPos));
        toP.x += 0.5f;
        toP.y += 0.5f;
        if (PointF.distance(fromP, toP) > maxDist) {
            toP = PointF.inter(fromP, toP, maxDist / PointF.distance(fromP, toP));
        }
        float circleRadius = PointF.distance(fromP, toP);
        circleRadius += 0.5f;
        PointF scan = new PointF();
        Point scanInt = new Point();
        float initalAngle = PointF.angle(fromP, toP) / ((float)Math.PI / 180);
        LinkedHashSet<Integer> targetCells = new LinkedHashSet<Integer>();
        LinkedHashSet<Integer> outerCells = new LinkedHashSet<Integer>();
        for (float a = initalAngle + degrees / 2.0f; a >= initalAngle - degrees / 2.0f; a -= 0.5f) {
            scan.polar(a * ((float)Math.PI / 180), circleRadius);
            scan.offset(fromP);
            scan.x = scan.x + (fromP.x > scan.x ? 0.5f : -0.5f);
            scan.y = scan.y + (fromP.y > scan.y ? 0.5f : -0.5f);
            scanInt.set((int)GameMath.gate(0.0f, (int)Math.floor(scan.x), Dungeon.level.width() - 1), (int)GameMath.gate(0.0f, (int)Math.floor(scan.y), Dungeon.level.height() - 1));
            targetCells.add(Dungeon.level.pointToCell(scanInt));
            outerCells.add(Dungeon.level.pointToCell(scanInt));
            if (!(circleRadius >= 4.0f)) continue;
            scan.polar(a * ((float)Math.PI / 180), circleRadius - 1.0f);
            scan.offset(fromP);
            scan.x = scan.x + (fromP.x > scan.x ? 0.5f : -0.5f);
            scan.y = scan.y + (fromP.y > scan.y ? 0.5f : -0.5f);
            scanInt.set((int)GameMath.gate(0.0f, (int)Math.floor(scan.x), Dungeon.level.width() - 1), (int)GameMath.gate(0.0f, (int)Math.floor(scan.y), Dungeon.level.height() - 1));
            targetCells.add(Dungeon.level.pointToCell(scanInt));
        }
        Iterator iterator = targetCells.iterator();
        while (iterator.hasNext()) {
            int c = (Integer)iterator.next();
            Ballistica ray = new Ballistica(core.sourcePos, c, ballisticaParams);
            this.cells.addAll(ray.subPath(1, ray.dist));
            this.rays.add(ray);
            if (!outerCells.contains(c)) continue;
            this.outerRays.add(ray);
        }
    }
}

