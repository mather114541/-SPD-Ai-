/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.ui;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.PointF;

public class Cursor {
    private static com.badlogic.gdx.graphics.Cursor currentCursor;
    private static Type lastType;
    private static int lastZoom;
    private static boolean cursorCaptured;

    public static void setCustomCursor(Type type, int zoom) {
        int height2;
        int width2;
        if (!DeviceCompat.isDesktop()) {
            return;
        }
        if (currentCursor != null) {
            if (lastType == type && lastZoom == zoom) {
                return;
            }
            currentCursor.dispose();
            currentCursor = null;
        }
        Pixmap cursorImg = new Pixmap(FileUtils.getFileHandle(Files.FileType.Internal, type.file));
        int scaledWidth = cursorImg.getWidth() * zoom;
        for (width2 = 2; width2 < scaledWidth; width2 <<= 1) {
        }
        int scaledHeight = cursorImg.getHeight() * zoom;
        for (height2 = 2; height2 < scaledHeight; height2 <<= 1) {
        }
        Pixmap scaledImg = new Pixmap(width2, height2, cursorImg.getFormat());
        scaledImg.setFilter(Pixmap.Filter.NearestNeighbour);
        scaledImg.drawPixmap(cursorImg, 0, 0, cursorImg.getWidth(), cursorImg.getHeight(), 0, 0, scaledWidth, scaledHeight);
        currentCursor = Gdx.graphics.newCursor(scaledImg, 0, 0);
        Gdx.graphics.setCursor(currentCursor);
        scaledImg.dispose();
        cursorImg.dispose();
        lastType = type;
        lastZoom = zoom;
    }

    public static void captureCursor(boolean captured) {
        cursorCaptured = captured;
        if (captured) {
            Gdx.input.setCursorCatched(true);
        } else if (ControllerHandler.controllerPointerActive()) {
            ControllerHandler.setControllerPointer(true);
            ControllerHandler.updateControllerPointer(new PointF((float)Game.width / 2.0f, (float)Game.height / 2.0f), false);
        } else {
            Gdx.input.setCursorCatched(false);
        }
    }

    public static PointF getCursorDelta() {
        return new PointF(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
    }

    public static boolean isCursorCaptured() {
        return cursorCaptured;
    }

    static {
        cursorCaptured = false;
    }

    public static enum Type {
        DEFAULT("gdx/cursor_mouse.png"),
        CONTROLLER("gdx/cursor_controller.png");

        public final String file;

        private Type(String file) {
            this.file = file;
        }
    }
}

