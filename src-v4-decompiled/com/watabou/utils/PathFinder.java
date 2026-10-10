/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.utils.BArray;
import java.util.Arrays;
import java.util.LinkedList;

public class PathFinder {
    public static int[] distance;
    private static int[] maxVal;
    private static boolean[] goals;
    private static int[] queue;
    private static boolean[] queued;
    private static int size;
    private static int width;
    private static int[] dir;
    private static int[] dirLR;
    public static int[] NEIGHBOURS4;
    public static int[] NEIGHBOURS8;
    public static int[] NEIGHBOURS9;
    public static int[] CIRCLE4;
    public static int[] CIRCLE8;

    public static void setMapSize(int width, int height) {
        PathFinder.width = width;
        size = width * height;
        distance = new int[size];
        goals = new boolean[size];
        queue = new int[size];
        queued = new boolean[size];
        maxVal = new int[size];
        Arrays.fill(maxVal, Integer.MAX_VALUE);
        dir = new int[]{-1, 1, -width, width, -width - 1, -width + 1, width - 1, width + 1};
        dirLR = new int[]{-1 - width, -1, -1 + width, -width, width, 1 - width, 1, 1 + width};
        NEIGHBOURS4 = new int[]{-width, -1, 1, width};
        NEIGHBOURS8 = new int[]{-width - 1, -width, -width + 1, -1, 1, width - 1, width, width + 1};
        NEIGHBOURS9 = new int[]{-width - 1, -width, -width + 1, -1, 0, 1, width - 1, width, width + 1};
        CIRCLE4 = new int[]{-width, 1, width, -1};
        CIRCLE8 = new int[]{-width - 1, -width, -width + 1, 1, width + 1, width, width - 1, -1};
    }

    public static Path find(int from, int to, boolean[] passable) {
        if (!PathFinder.buildDistanceMap(from, to, passable)) {
            return null;
        }
        Path result = new Path();
        int s = from;
        do {
            int minD = distance[s];
            int mins = s;
            for (int i = 0; i < dir.length; ++i) {
                int n = s + dir[i];
                int thisD = distance[n];
                if (thisD >= minD) continue;
                minD = thisD;
                mins = n;
            }
            s = mins;
            result.add(s);
        } while (s != to);
        return result;
    }

    public static int getStep(int from, int to, boolean[] passable) {
        if (!PathFinder.buildDistanceMap(from, to, passable)) {
            return -1;
        }
        int minD = distance[from];
        int best = from;
        for (int i = 0; i < dir.length; ++i) {
            int step = from + dir[i];
            int stepD = distance[step];
            if (stepD >= minD) continue;
            minD = stepD;
            best = step;
        }
        return best;
    }

    public static int getStepBack(int cur, int from, int lookahead, boolean[] passable, boolean canApproachFromPos) {
        int d = PathFinder.buildEscapeDistanceMap(cur, from, lookahead, passable);
        if (d == 0) {
            return -1;
        }
        if (!canApproachFromPos) {
            int head = 0;
            int tail = 0;
            int newD = distance[cur];
            BArray.setFalse(queued);
            PathFinder.queue[tail++] = cur;
            PathFinder.queued[cur] = true;
            while (head < tail) {
                int step;
                if (distance[step = queue[head++]] > newD) {
                    newD = distance[step];
                }
                int start = step % width == 0 ? 3 : 0;
                int end = (step + 1) % width == 0 ? 3 : 0;
                for (int i = start; i < dirLR.length - end; ++i) {
                    int n = step + dirLR[i];
                    if (n < 0 || n >= size || !passable[n]) continue;
                    if (distance[n] < distance[cur]) {
                        passable[n] = false;
                        continue;
                    }
                    if (distance[n] < distance[step] || queued[n]) continue;
                    PathFinder.queue[tail++] = n;
                    PathFinder.queued[n] = true;
                }
            }
            d = Math.min(newD, d);
        }
        for (int i = 0; i < size; ++i) {
            PathFinder.goals[i] = distance[i] == d;
        }
        if (!PathFinder.buildDistanceMap(cur, goals, passable)) {
            return -1;
        }
        int s = cur;
        int minD = distance[s];
        int mins = s;
        for (int i = 0; i < dir.length; ++i) {
            int n = s + dir[i];
            int thisD = distance[n];
            if (thisD >= minD) continue;
            minD = thisD;
            mins = n;
        }
        return mins;
    }

    private static boolean buildDistanceMap(int from, int to, boolean[] passable) {
        if (from == to) {
            return false;
        }
        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
        boolean pathFound = false;
        int head = 0;
        int tail = 0;
        PathFinder.queue[tail++] = to;
        PathFinder.distance[to] = 0;
        while (head < tail) {
            int step;
            if ((step = queue[head++]) == from) {
                pathFound = true;
                break;
            }
            int nextDistance = distance[step] + 1;
            int start = step % width == 0 ? 3 : 0;
            int end = (step + 1) % width == 0 ? 3 : 0;
            for (int i = start; i < dirLR.length - end; ++i) {
                int n = step + dirLR[i];
                if (n != from && (n < 0 || n >= size || !passable[n] || distance[n] <= nextDistance)) continue;
                PathFinder.queue[tail++] = n;
                PathFinder.distance[n] = nextDistance;
            }
        }
        return pathFound;
    }

    public static void buildDistanceMap(int to, boolean[] passable, int limit) {
        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
        int head = 0;
        int tail = 0;
        PathFinder.queue[tail++] = to;
        PathFinder.distance[to] = 0;
        while (head < tail) {
            int step;
            int nextDistance;
            if ((nextDistance = distance[step = queue[head++]] + 1) > limit) {
                return;
            }
            int start = step % width == 0 ? 3 : 0;
            int end = (step + 1) % width == 0 ? 3 : 0;
            for (int i = start; i < dirLR.length - end; ++i) {
                int n = step + dirLR[i];
                if (n < 0 || n >= size || !passable[n] || distance[n] <= nextDistance) continue;
                PathFinder.queue[tail++] = n;
                PathFinder.distance[n] = nextDistance;
            }
        }
    }

    private static boolean buildDistanceMap(int from, boolean[] to, boolean[] passable) {
        if (to[from]) {
            return false;
        }
        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
        boolean pathFound = false;
        int head = 0;
        int tail = 0;
        for (int i = 0; i < size; ++i) {
            if (!to[i]) continue;
            PathFinder.queue[tail++] = i;
            PathFinder.distance[i] = 0;
        }
        while (head < tail) {
            int step;
            if ((step = queue[head++]) == from) {
                pathFound = true;
                break;
            }
            int nextDistance = distance[step] + 1;
            int start = step % width == 0 ? 3 : 0;
            int end = (step + 1) % width == 0 ? 3 : 0;
            for (int i = start; i < dirLR.length - end; ++i) {
                int n = step + dirLR[i];
                if (n != from && (n < 0 || n >= size || !passable[n] || distance[n] <= nextDistance)) continue;
                PathFinder.queue[tail++] = n;
                PathFinder.distance[n] = nextDistance;
            }
        }
        return pathFound;
    }

    private static int buildEscapeDistanceMap(int cur, int from, int lookAhead, boolean[] passable) {
        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
        int destDist = Integer.MAX_VALUE;
        int head = 0;
        int tail = 0;
        PathFinder.queue[tail++] = from;
        PathFinder.distance[from] = 0;
        int dist = 0;
        while (head < tail) {
            int step;
            if ((dist = distance[step = queue[head++]]) > destDist) {
                return destDist;
            }
            if (step == cur) {
                destDist = dist + lookAhead;
            }
            int nextDistance = dist + 1;
            int start = step % width == 0 ? 3 : 0;
            int end = (step + 1) % width == 0 ? 3 : 0;
            for (int i = start; i < dirLR.length - end; ++i) {
                int n = step + dirLR[i];
                if (n < 0 || n >= size || !passable[n] || distance[n] <= nextDistance) continue;
                PathFinder.queue[tail++] = n;
                PathFinder.distance[n] = nextDistance;
            }
        }
        return dist;
    }

    public static void buildDistanceMap(int to, boolean[] passable) {
        System.arraycopy(maxVal, 0, distance, 0, maxVal.length);
        int head = 0;
        int tail = 0;
        PathFinder.queue[tail++] = to;
        PathFinder.distance[to] = 0;
        while (head < tail) {
            int step = queue[head++];
            int nextDistance = distance[step] + 1;
            int start = step % width == 0 ? 3 : 0;
            int end = (step + 1) % width == 0 ? 3 : 0;
            for (int i = start; i < dirLR.length - end; ++i) {
                int n = step + dirLR[i];
                if (n < 0 || n >= size || !passable[n] || distance[n] <= nextDistance) continue;
                PathFinder.queue[tail++] = n;
                PathFinder.distance[n] = nextDistance;
            }
        }
    }

    static {
        size = 0;
        width = 0;
    }

    public static class Path
    extends LinkedList<Integer> {
    }
}

