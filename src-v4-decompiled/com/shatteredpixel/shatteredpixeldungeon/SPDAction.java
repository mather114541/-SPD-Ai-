/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import java.io.IOException;
import java.util.LinkedHashMap;

public class SPDAction
extends GameAction {
    public static final GameAction NONE = GameAction.NONE;
    public static final GameAction BACK = GameAction.BACK;
    public static final GameAction LEFT_CLICK = GameAction.LEFT_CLICK;
    public static final GameAction RIGHT_CLICK = GameAction.RIGHT_CLICK;
    public static final GameAction MIDDLE_CLICK = GameAction.MIDDLE_CLICK;
    public static final GameAction N = new SPDAction("n");
    public static final GameAction W = new SPDAction("w");
    public static final GameAction S = new SPDAction("s");
    public static final GameAction E = new SPDAction("e");
    public static final GameAction NW = new SPDAction("nw");
    public static final GameAction NE = new SPDAction("ne");
    public static final GameAction SW = new SPDAction("sw");
    public static final GameAction SE = new SPDAction("se");
    public static final GameAction WAIT_OR_PICKUP = new SPDAction("wait_or_pickup");
    public static final GameAction INVENTORY = new SPDAction("inventory");
    public static final GameAction INVENTORY_SELECTOR = new SPDAction("inventory_selector");
    public static final GameAction QUICKSLOT_SELECTOR = new SPDAction("quickslot_selector");
    public static final GameAction QUICKSLOT_1 = new SPDAction("quickslot_1");
    public static final GameAction QUICKSLOT_2 = new SPDAction("quickslot_2");
    public static final GameAction QUICKSLOT_3 = new SPDAction("quickslot_3");
    public static final GameAction QUICKSLOT_4 = new SPDAction("quickslot_4");
    public static final GameAction QUICKSLOT_5 = new SPDAction("quickslot_5");
    public static final GameAction QUICKSLOT_6 = new SPDAction("quickslot_6");
    public static final GameAction BAG_1 = new SPDAction("bag_1");
    public static final GameAction BAG_2 = new SPDAction("bag_2");
    public static final GameAction BAG_3 = new SPDAction("bag_3");
    public static final GameAction BAG_4 = new SPDAction("bag_4");
    public static final GameAction BAG_5 = new SPDAction("bag_5");
    public static final GameAction EXAMINE = new SPDAction("examine");
    public static final GameAction WAIT = new SPDAction("wait");
    public static final GameAction REST = new SPDAction("rest");
    public static final GameAction TAG_ATTACK = new SPDAction("tag_attack");
    public static final GameAction TAG_ACTION = new SPDAction("tag_action");
    public static final GameAction TAG_LOOT = new SPDAction("tag_loot");
    public static final GameAction TAG_RESUME = new SPDAction("tag_resume");
    public static final GameAction CYCLE = new SPDAction("cycle");
    public static final GameAction HERO_INFO = new SPDAction("hero_info");
    public static final GameAction JOURNAL = new SPDAction("journal");
    public static final GameAction ZOOM_IN = new SPDAction("zoom_in");
    public static final GameAction ZOOM_OUT = new SPDAction("zoom_out");
    private static final LinkedHashMap<Integer, GameAction> defaultBindings = new LinkedHashMap();
    private static final LinkedHashMap<Integer, GameAction> defaultControllerBindings;
    private static final String BINDINGS_FILE = "keybinds.dat";

    protected SPDAction(String name) {
        super(name);
    }

    public static LinkedHashMap<Integer, GameAction> getDefaults() {
        return new LinkedHashMap<Integer, GameAction>(defaultBindings);
    }

    public static LinkedHashMap<Integer, GameAction> getControllerDefaults() {
        return new LinkedHashMap<Integer, GameAction>(defaultControllerBindings);
    }

    public static void loadBindings() {
        if (!KeyBindings.getAllBindings().isEmpty()) {
            return;
        }
        try {
            Bundle b = FileUtils.bundleFromFile(BINDINGS_FILE);
            Bundle firstKeys = b.getBundle("first_keys");
            Bundle secondKeys = b.getBundle("second_keys");
            Bundle thirdKeys = b.getBundle("third_keys");
            LinkedHashMap<Integer, GameAction> defaults = SPDAction.getDefaults();
            LinkedHashMap<Integer, GameAction> merged = new LinkedHashMap<Integer, GameAction>();
            block2: for (GameAction a : SPDAction.allActions()) {
                if (firstKeys.contains(a.name()) && KeyEvent.isKeyboardKey(firstKeys.getInt(a.name()))) {
                    if (firstKeys.getInt(a.name()) == 0) continue;
                    merged.put(firstKeys.getInt(a.name()), a);
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        defaults.remove(i);
                        break;
                    }
                } else {
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        merged.put(i, (GameAction)defaults.remove(i));
                        break;
                    }
                }
                if (secondKeys.contains(a.name()) && KeyEvent.isKeyboardKey(secondKeys.getInt(a.name()))) {
                    if (secondKeys.getInt(a.name()) == 0) continue;
                    merged.put(secondKeys.getInt(a.name()), a);
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        defaults.remove(i);
                        break;
                    }
                } else {
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        merged.put(i, (GameAction)defaults.remove(i));
                        break;
                    }
                }
                if (thirdKeys.contains(a.name()) && KeyEvent.isKeyboardKey(thirdKeys.getInt(a.name()))) {
                    if (thirdKeys.getInt(a.name()) == 0) continue;
                    merged.put(thirdKeys.getInt(a.name()), a);
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        defaults.remove(i);
                        continue block2;
                    }
                    continue;
                }
                for (int i : defaults.keySet()) {
                    if (defaults.get(i) != a) continue;
                    merged.put(i, (GameAction)defaults.remove(i));
                    continue block2;
                }
            }
            KeyBindings.setAllBindings(merged);
            defaults = SPDAction.getControllerDefaults();
            merged.clear();
            Bundle firstButtons = b.getBundle("first_keys_controller");
            Bundle secondButtons = b.getBundle("second_keys_controller");
            Bundle thirdButtons = b.getBundle("third_keys_controller");
            block9: for (GameAction a : SPDAction.allActions()) {
                if (firstButtons.contains(a.name()) && ControllerHandler.icControllerKey(firstButtons.getInt(a.name()))) {
                    if (firstButtons.getInt(a.name()) == 0) continue;
                    merged.put(firstButtons.getInt(a.name()), a);
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        defaults.remove(i);
                        break;
                    }
                } else {
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        merged.put(i, (GameAction)defaults.remove(i));
                        break;
                    }
                }
                if (secondButtons.contains(a.name()) && ControllerHandler.icControllerKey(secondButtons.getInt(a.name()))) {
                    if (secondButtons.getInt(a.name()) == 0) continue;
                    merged.put(secondButtons.getInt(a.name()), a);
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        defaults.remove(i);
                        break;
                    }
                } else {
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        merged.put(i, (GameAction)defaults.remove(i));
                        break;
                    }
                }
                if (thirdButtons.contains(a.name()) && ControllerHandler.icControllerKey(thirdButtons.getInt(a.name()))) {
                    if (thirdButtons.getInt(a.name()) == 0) continue;
                    merged.put(thirdButtons.getInt(a.name()), a);
                    for (int i : defaults.keySet()) {
                        if (defaults.get(i) != a) continue;
                        defaults.remove(i);
                        continue block9;
                    }
                    continue;
                }
                for (int i : defaults.keySet()) {
                    if (defaults.get(i) != a) continue;
                    merged.put(i, (GameAction)defaults.remove(i));
                    continue block9;
                }
            }
            KeyBindings.setAllControllerBindings(merged);
        }
        catch (Exception e) {
            KeyBindings.setAllBindings(SPDAction.getDefaults());
            KeyBindings.setAllControllerBindings(SPDAction.getControllerDefaults());
        }
    }

    public static void saveBindings() {
        Bundle b = new Bundle();
        Bundle firstKeys = new Bundle();
        Bundle secondKeys = new Bundle();
        Bundle thirdKeys = new Bundle();
        for (GameAction a : SPDAction.allActions()) {
            int firstCur = 0;
            int secondCur = 0;
            int thirdCur = 0;
            int firstDef = 0;
            int secondDef = 0;
            int thirdDef = 0;
            for (int i : defaultBindings.keySet()) {
                if (defaultBindings.get(i) != a) continue;
                if (firstDef == 0) {
                    firstDef = i;
                    continue;
                }
                if (secondDef == 0) {
                    secondDef = i;
                    continue;
                }
                thirdDef = i;
            }
            LinkedHashMap<Integer, GameAction> curBindings = KeyBindings.getAllBindings();
            for (int i : curBindings.keySet()) {
                if (curBindings.get(i) != a) continue;
                if (firstCur == 0) {
                    firstCur = i;
                    continue;
                }
                if (secondCur == 0) {
                    secondCur = i;
                    continue;
                }
                thirdCur = i;
            }
            if (firstCur != firstDef) {
                firstKeys.put(a.name(), firstCur);
            }
            if (secondCur != secondDef) {
                secondKeys.put(a.name(), secondCur);
            }
            if (thirdCur == thirdDef) continue;
            thirdKeys.put(a.name(), thirdCur);
        }
        b.put("first_keys", firstKeys);
        b.put("second_keys", secondKeys);
        b.put("third_keys", thirdKeys);
        Bundle firstButtons = new Bundle();
        Bundle secondButtons = new Bundle();
        Bundle thirdButtons = new Bundle();
        for (GameAction a : SPDAction.allActions()) {
            int firstCur = 0;
            int secondCur = 0;
            int thirdCur = 0;
            int firstDef = 0;
            int secondDef = 0;
            int thirdDef = 0;
            for (int i : defaultControllerBindings.keySet()) {
                if (defaultControllerBindings.get(i) != a) continue;
                if (firstDef == 0) {
                    firstDef = i;
                    continue;
                }
                if (secondDef == 0) {
                    secondDef = i;
                    continue;
                }
                thirdDef = i;
            }
            LinkedHashMap<Integer, GameAction> curBindings = KeyBindings.getAllControllerBindings();
            for (int i : curBindings.keySet()) {
                if (curBindings.get(i) != a) continue;
                if (firstCur == 0) {
                    firstCur = i;
                    continue;
                }
                if (secondCur == 0) {
                    secondCur = i;
                    continue;
                }
                thirdCur = i;
            }
            if (firstCur != firstDef) {
                firstButtons.put(a.name(), firstCur);
            }
            if (secondCur != secondDef) {
                secondButtons.put(a.name(), secondCur);
            }
            if (thirdCur == thirdDef) continue;
            thirdButtons.put(a.name(), thirdCur);
        }
        b.put("first_keys_controller", firstButtons);
        b.put("second_keys_controller", secondButtons);
        b.put("third_keys_controller", thirdButtons);
        try {
            FileUtils.bundleToFile(BINDINGS_FILE, b);
        }
        catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
    }

    static {
        defaultBindings.put(111, BACK);
        defaultBindings.put(67, BACK);
        defaultBindings.put(51, N);
        defaultBindings.put(29, W);
        defaultBindings.put(47, S);
        defaultBindings.put(32, E);
        defaultBindings.put(62, WAIT_OR_PICKUP);
        defaultBindings.put(19, N);
        defaultBindings.put(21, W);
        defaultBindings.put(20, S);
        defaultBindings.put(22, E);
        defaultBindings.put(152, N);
        defaultBindings.put(148, W);
        defaultBindings.put(146, S);
        defaultBindings.put(150, E);
        defaultBindings.put(151, NW);
        defaultBindings.put(153, NE);
        defaultBindings.put(145, SW);
        defaultBindings.put(147, SE);
        defaultBindings.put(149, WAIT_OR_PICKUP);
        defaultBindings.put(34, INVENTORY);
        defaultBindings.put(37, INVENTORY);
        defaultBindings.put(8, QUICKSLOT_1);
        defaultBindings.put(9, QUICKSLOT_2);
        defaultBindings.put(10, QUICKSLOT_3);
        defaultBindings.put(11, QUICKSLOT_4);
        defaultBindings.put(12, QUICKSLOT_5);
        defaultBindings.put(13, QUICKSLOT_6);
        defaultBindings.put(131, BAG_1);
        defaultBindings.put(132, BAG_2);
        defaultBindings.put(133, BAG_3);
        defaultBindings.put(134, BAG_4);
        defaultBindings.put(135, BAG_5);
        defaultBindings.put(33, EXAMINE);
        defaultBindings.put(54, REST);
        defaultBindings.put(45, TAG_ATTACK);
        defaultBindings.put(61, CYCLE);
        defaultBindings.put(52, TAG_ACTION);
        defaultBindings.put(31, TAG_LOOT);
        defaultBindings.put(66, TAG_LOOT);
        defaultBindings.put(46, TAG_RESUME);
        defaultBindings.put(36, HERO_INFO);
        defaultBindings.put(38, JOURNAL);
        defaultBindings.put(81, ZOOM_IN);
        defaultBindings.put(70, ZOOM_IN);
        defaultBindings.put(69, ZOOM_OUT);
        defaultControllerBindings = new LinkedHashMap();
        defaultControllerBindings.put(108, BACK);
        defaultControllerBindings.put(109, JOURNAL);
        defaultControllerBindings.put(105, LEFT_CLICK);
        defaultControllerBindings.put(107, LEFT_CLICK);
        defaultControllerBindings.put(104, RIGHT_CLICK);
        defaultControllerBindings.put(1019, TAG_ACTION);
        defaultControllerBindings.put(1021, TAG_LOOT);
        defaultControllerBindings.put(1020, TAG_RESUME);
        defaultControllerBindings.put(1022, CYCLE);
        defaultControllerBindings.put(106, WAIT_OR_PICKUP);
        defaultControllerBindings.put(103, ZOOM_IN);
        defaultControllerBindings.put(102, ZOOM_OUT);
        defaultControllerBindings.put(96, TAG_ATTACK);
        defaultControllerBindings.put(97, EXAMINE);
        defaultControllerBindings.put(99, QUICKSLOT_SELECTOR);
        defaultControllerBindings.put(100, INVENTORY_SELECTOR);
        KeyBindings.addHardBinding(4, BACK);
        KeyBindings.addHardBinding(82, INVENTORY);
        KeyBindings.addHardBinding(58, NONE);
        KeyBindings.addHardBinding(66, NONE);
    }
}

