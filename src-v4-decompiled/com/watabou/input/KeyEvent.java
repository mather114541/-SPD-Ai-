/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.input;

import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.noosa.Game;
import com.watabou.utils.Signal;
import java.util.ArrayList;

public class KeyEvent {
    public int code;
    public boolean pressed;
    private static Signal<KeyEvent> keySignal = new Signal(true);
    private static ArrayList<KeyEvent> keyEvents = new ArrayList();

    public KeyEvent(int code, boolean pressed) {
        this.code = code;
        this.pressed = pressed;
    }

    public static void addKeyListener(Signal.Listener<KeyEvent> listener) {
        keySignal.add(listener);
    }

    public static void removeKeyListener(Signal.Listener<KeyEvent> listener) {
        keySignal.remove(listener);
    }

    public static void clearListeners() {
        keySignal.removeAll();
    }

    public static synchronized void addKeyEvent(KeyEvent event) {
        keyEvents.add(event);
    }

    public static synchronized void processKeyEvents() {
        if (keyEvents.isEmpty()) {
            return;
        }
        for (KeyEvent k : keyEvents) {
            if (KeyBindings.getActionForKey(k) == GameAction.LEFT_CLICK) {
                Game.inputHandler.emulateTouch(19, 0, k.pressed);
                if (!KeyBindings.bindingKey) continue;
                keySignal.dispatch(k);
                continue;
            }
            if (KeyBindings.getActionForKey(k) == GameAction.RIGHT_CLICK) {
                Game.inputHandler.emulateTouch(19, 1, k.pressed);
                if (!KeyBindings.bindingKey) continue;
                keySignal.dispatch(k);
                continue;
            }
            if (KeyBindings.getActionForKey(k) == GameAction.MIDDLE_CLICK) {
                Game.inputHandler.emulateTouch(19, 2, k.pressed);
                if (!KeyBindings.bindingKey) continue;
                keySignal.dispatch(k);
                continue;
            }
            keySignal.dispatch(k);
        }
        keyEvents.clear();
    }

    public static boolean isKeyboardKey(int keyCode) {
        return keyCode == 0 || !ControllerHandler.icControllerKey(keyCode);
    }
}

