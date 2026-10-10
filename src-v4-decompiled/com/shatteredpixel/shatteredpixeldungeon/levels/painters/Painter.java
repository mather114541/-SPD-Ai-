/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Painter {
    public abstract boolean paint(Level var1, ArrayList<Room> var2);

    public static void set(Level level, int cell, int value) {
        level.map[cell] = value;
    }

    public static void set(Level level, int x, int y, int value) {
        Painter.set(level, x + y * level.width(), value);
    }

    public static void set(Level level, Point p, int value) {
        Painter.set(level, p.x, p.y, value);
    }

    public static void fill(Level level, int x, int y, int w, int h, int value) {
        int width = level.width();
        int pos = y * width + x;
        int i = y;
        while (i < y + h) {
            Arrays.fill(level.map, pos, pos + w, value);
            ++i;
            pos += width;
        }
    }

    public static void fill(Level level, Rect rect, int value) {
        Painter.fill(level, rect.left, rect.top, rect.width(), rect.height(), value);
    }

    public static void fill(Level level, Rect rect, int m, int value) {
        Painter.fill(level, rect.left + m, rect.top + m, rect.width() - m * 2, rect.height() - m * 2, value);
    }

    public static void fill(Level level, Rect rect, int l, int t, int r, int b, int value) {
        Painter.fill(level, rect.left + l, rect.top + t, rect.width() - (l + r), rect.height() - (t + b), value);
    }

    public static void drawLine(Level level, Point from, Point to, int value) {
        boolean movingbyX;
        float x = from.x;
        float y = from.y;
        float dx = to.x - from.x;
        float dy = to.y - from.y;
        boolean bl = movingbyX = Math.abs(dx) >= Math.abs(dy);
        if (movingbyX) {
            dy /= Math.abs(dx);
            dx /= Math.abs(dx);
        } else {
            dx /= Math.abs(dy);
            dy /= Math.abs(dy);
        }
        Painter.set(level, Math.round(x), Math.round(y), value);
        while (movingbyX && (float)to.x != x || !movingbyX && (float)to.y != y) {
            Painter.set(level, Math.round(x += dx), Math.round(y += dy), value);
        }
    }

    public static void fillEllipse(Level level, Rect rect, int value) {
        Painter.fillEllipse(level, rect.left, rect.top, rect.width(), rect.height(), value);
    }

    public static void fillEllipse(Level level, Rect rect, int m, int value) {
        Painter.fillEllipse(level, rect.left + m, rect.top + m, rect.width() - m * 2, rect.height() - m * 2, value);
    }

    public static void fillEllipse(Level level, int x, int y, int w, int h, int value) {
        double radH = (float)h / 2.0f;
        double radW = (float)w / 2.0f;
        for (int i = 0; i < h; ++i) {
            double rowY = -radH + 0.5 + (double)i;
            double rowW = 2.0 * Math.sqrt(radW * radW * (1.0 - rowY * rowY / (radH * radH)));
            if (w % 2 == 0) {
                rowW = (double)Math.round(rowW / 2.0) * 2.0;
            } else {
                rowW = Math.floor(rowW / 2.0) * 2.0;
                rowW += 1.0;
            }
            int cell = x + (w - (int)rowW) / 2 + (y + i) * level.width();
            Arrays.fill(level.map, cell, cell + (int)rowW, value);
        }
    }

    public static void fillDiamond(Level level, Rect rect, int value) {
        Painter.fillDiamond(level, rect.left, rect.top, rect.width(), rect.height(), value);
    }

    public static void fillDiamond(Level level, Rect rect, int m, int value) {
        Painter.fillDiamond(level, rect.left + m, rect.top + m, rect.width() - m * 2, rect.height() - m * 2, value);
    }

    public static void fillDiamond(Level level, int x, int y, int w, int h, int value) {
        int diamondWidth = w - (h - 2 - h % 2);
        diamondWidth = Math.max(diamondWidth, w % 2 == 0 ? 2 : 3);
        for (int i = 0; i <= h; ++i) {
            Painter.fill(level, x + (w - diamondWidth) / 2, y + i, diamondWidth, h - 2 * i, value);
            if ((diamondWidth += 2) > w) break;
        }
    }

    public static Point drawInside(Level level, Room room, Point from, int n, int value) {
        Point step = new Point();
        if (from.x == room.left) {
            step.set(1, 0);
        } else if (from.x == room.right) {
            step.set(-1, 0);
        } else if (from.y == room.top) {
            step.set(0, 1);
        } else if (from.y == room.bottom) {
            step.set(0, -1);
        }
        Point p = new Point(from).offset(step);
        for (int i = 0; i < n; ++i) {
            if (value != -1) {
                Painter.set(level, p, value);
            }
            p.offset(step);
        }
        return p;
    }
}

