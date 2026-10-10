/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.input;

import com.badlogic.gdx.Input;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class KeyBindings {
    private static LinkedHashMap<Integer, GameAction> bindings = new LinkedHashMap();
    private static LinkedHashMap<Integer, GameAction> controllerBindings = new LinkedHashMap();
    private static LinkedHashMap<Integer, GameAction> hardBindings = new LinkedHashMap();
    public static boolean bindingKey = false;

    public static LinkedHashMap<Integer, GameAction> getAllBindings() {
        return new LinkedHashMap<Integer, GameAction>(bindings);
    }

    public static void setAllBindings(LinkedHashMap<Integer, GameAction> newBindings) {
        bindings = new LinkedHashMap<Integer, GameAction>(newBindings);
    }

    public static LinkedHashMap<Integer, GameAction> getAllControllerBindings() {
        return new LinkedHashMap<Integer, GameAction>(controllerBindings);
    }

    public static void setAllControllerBindings(LinkedHashMap<Integer, GameAction> newBindings) {
        controllerBindings = new LinkedHashMap<Integer, GameAction>(newBindings);
    }

    public static void addHardBinding(int keyCode, GameAction action) {
        hardBindings.put(keyCode, action);
    }

    public static boolean isKeyBound(int keyCode) {
        if (keyCode < 0 || keyCode > 255 && keyCode < 1000) {
            return false;
        }
        return bindingKey || bindings.containsKey(keyCode) || controllerBindings.containsKey(keyCode) || hardBindings.containsKey(keyCode);
    }

    public static GameAction getActionForKey(KeyEvent event) {
        if (bindings.containsKey(event.code)) {
            return bindings.get(event.code);
        }
        if (controllerBindings.containsKey(event.code)) {
            return controllerBindings.get(event.code);
        }
        if (hardBindings.containsKey(event.code)) {
            return hardBindings.get(event.code);
        }
        return GameAction.NONE;
    }

    public static int getFirstKeyForAction(GameAction action, boolean preferController) {
        ArrayList<Integer> keys = KeyBindings.getKeyboardKeysForAction(action);
        ArrayList<Integer> buttons = KeyBindings.getControllerKeysForAction(action);
        if (preferController) {
            if (!buttons.isEmpty()) {
                return buttons.get(0);
            }
        } else if (!keys.isEmpty()) {
            return keys.get(0);
        }
        return 0;
    }

    public static ArrayList<Integer> getKeyboardKeysForAction(GameAction action) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i : bindings.keySet()) {
            if (bindings.get(i) != action) continue;
            result.add(i);
        }
        return result;
    }

    public static ArrayList<Integer> getControllerKeysForAction(GameAction action) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i : controllerBindings.keySet()) {
            if (controllerBindings.get(i) != action) continue;
            result.add(i);
        }
        return result;
    }

    public static String getKeyName(int keyCode) {
        if (ControllerHandler.customButtonName(keyCode) != null) {
            return ControllerHandler.customButtonName(keyCode);
        }
        if (keyCode == 1003) {
            return "Mouse 4";
        }
        if (keyCode == 1004) {
            return "Mouse 5";
        }
        if (keyCode == 0) {
            return "None";
        }
        if (keyCode == 81) {
            return "+";
        }
        if (keyCode == 67) {
            return "Backspc";
        }
        if (keyCode == 112) {
            return "Delete";
        }
        return Input.Keys.toString(keyCode);
    }
}

