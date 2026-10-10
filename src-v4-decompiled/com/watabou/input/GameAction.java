/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.input;

import java.util.ArrayList;

public class GameAction {
    private static final ArrayList<GameAction> ALL_ACTIONS = new ArrayList();
    private int code = ALL_ACTIONS.size();
    private String name;
    public static final GameAction NONE = new GameAction("none");
    public static final GameAction BACK = new GameAction("back");
    public static final GameAction LEFT_CLICK = new GameAction("left_click");
    public static final GameAction RIGHT_CLICK = new GameAction("right_click");
    public static final GameAction MIDDLE_CLICK = new GameAction("middle_click");

    protected GameAction(String name) {
        this.name = name;
        ALL_ACTIONS.add(this);
    }

    public int code() {
        return this.code;
    }

    public String name() {
        return this.name;
    }

    public boolean equals(Object o) {
        return o instanceof GameAction && ((GameAction)o).code == this.code;
    }

    public static ArrayList<GameAction> allActions() {
        return new ArrayList<GameAction>(ALL_ACTIONS);
    }

    public static int totalActions() {
        return ALL_ACTIONS.size();
    }
}

