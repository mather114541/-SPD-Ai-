/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.input;

import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Cursor;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;
import java.util.ArrayList;
import java.util.HashMap;

public class PointerEvent {
    public static final int NONE = -1;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int MIDDLE = 2;
    public static final int BACK = 3;
    public static final int FORWARD = 4;
    public PointF start;
    public PointF current;
    public int id;
    public Type type;
    public int button;
    public boolean handled;
    private static Signal<PointerEvent> pointerSignal = new Signal(true);
    private static ArrayList<PointerEvent> pointerEvents = new ArrayList();
    private static HashMap<Integer, PointerEvent> activePointers = new HashMap();
    private static PointF lastHoverPos = new PointF();
    public static boolean clearKeyboardThisPress = true;

    public PointerEvent(int x, int y, int id, Type type) {
        this(x, y, id, type, -1);
    }

    public PointerEvent(int x, int y, int id, Type type, int button) {
        if (Cursor.isCursorCaptured()) {
            x = Game.width / 2;
            y = Game.width / 2;
        }
        this.start = this.current = new PointF(x, y);
        this.id = id;
        this.type = type;
        this.handled = false;
        this.button = button;
    }

    public void update(PointerEvent other) {
        this.current = other.current;
    }

    public void update(int x, int y) {
        this.current.set(x, y);
    }

    public PointerEvent up() {
        if (this.type == Type.DOWN) {
            this.type = Type.UP;
        }
        return this;
    }

    public PointerEvent cancel() {
        if (this.type == Type.DOWN) {
            this.type = Type.CANCEL;
        }
        return this;
    }

    public PointerEvent handle() {
        this.handled = true;
        return this;
    }

    public static void addPointerListener(Signal.Listener<PointerEvent> listener) {
        pointerSignal.add(listener);
    }

    public static void removePointerListener(Signal.Listener<PointerEvent> listener) {
        pointerSignal.remove(listener);
    }

    public static void clearListeners() {
        pointerSignal.removeAll();
    }

    public static PointF currentHoverPos() {
        if (PointerEvent.lastHoverPos.x == 0.0f && PointerEvent.lastHoverPos.y == 0.0f) {
            PointerEvent.lastHoverPos.x = Game.width / 2;
            PointerEvent.lastHoverPos.y = Game.height / 2;
        }
        return lastHoverPos.clone();
    }

    public static void setHoverPos(PointF pos) {
        lastHoverPos.set(pos);
    }

    public static synchronized void addPointerEvent(PointerEvent event) {
        pointerEvents.add(event);
    }

    public static synchronized void addIfExisting(PointerEvent event) {
        if (activePointers.containsKey(event.id)) {
            pointerEvents.add(event);
        }
    }

    public static synchronized void processPointerEvents() {
        if (pointerEvents.isEmpty()) {
            return;
        }
        boolean hovered = false;
        for (PointerEvent p : pointerEvents) {
            if (p.type != Type.HOVER) continue;
            lastHoverPos.set(p.current);
            pointerSignal.dispatch(p);
            hovered = true;
        }
        for (PointerEvent p : pointerEvents) {
            if (p.type == Type.HOVER) continue;
            clearKeyboardThisPress = true;
            if (activePointers.containsKey(p.id)) {
                PointerEvent existing = activePointers.get(p.id);
                existing.current = p.current;
                if (existing.type == p.type) {
                    pointerSignal.dispatch(null);
                } else if (p.type == Type.DOWN) {
                    pointerSignal.dispatch(existing);
                } else if (p.type == Type.UP) {
                    activePointers.remove(existing.id);
                    pointerSignal.dispatch(existing.up());
                } else if (p.type == Type.CANCEL) {
                    activePointers.remove(existing.id);
                    pointerSignal.dispatch(existing.cancel());
                }
            } else {
                if (p.type == Type.DOWN) {
                    activePointers.put(p.id, p);
                }
                pointerSignal.dispatch(p);
            }
            if (!clearKeyboardThisPress) continue;
            Game.platform.setOnscreenKeyboardVisible(false, false);
        }
        pointerEvents.clear();
        if (hovered && activePointers.containsKey(19)) {
            Game.inputHandler.emulateDrag(19);
        }
    }

    public static enum Type {
        DOWN,
        UP,
        CANCEL,
        HOVER;

    }
}

