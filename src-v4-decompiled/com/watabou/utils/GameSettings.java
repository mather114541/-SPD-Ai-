/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.watabou.noosa.Game;
import com.watabou.utils.GameMath;

public class GameSettings {
    public static final String DEFAULT_PREFS_FILE = "settings.xml";
    private static Preferences prefs;

    private static Preferences get() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences(DEFAULT_PREFS_FILE);
        }
        return prefs;
    }

    public static void set(Preferences prefs) {
        GameSettings.prefs = prefs;
    }

    public static boolean contains(String key) {
        return GameSettings.get().contains(key);
    }

    public static int getInt(String key, int defValue) {
        return GameSettings.getInt(key, defValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static int getInt(String key, int defValue, int min, int max) {
        try {
            int i = GameSettings.get().getInteger(key, defValue);
            if (i < min || i > max) {
                int val = (int)GameMath.gate(min, i, max);
                GameSettings.put(key, val);
                return val;
            }
            return i;
        }
        catch (Exception e) {
            Game.reportException(e);
            GameSettings.put(key, defValue);
            return defValue;
        }
    }

    public static long getLong(String key, long defValue) {
        return GameSettings.getLong(key, defValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public static long getLong(String key, long defValue, long min, long max) {
        try {
            long i = GameSettings.get().getLong(key, defValue);
            if (i < min || i > max) {
                long val = (long)GameMath.gate(min, i, max);
                GameSettings.put(key, val);
                return val;
            }
            return i;
        }
        catch (Exception e) {
            Game.reportException(e);
            GameSettings.put(key, defValue);
            return defValue;
        }
    }

    public static boolean getBoolean(String key, boolean defValue) {
        try {
            return GameSettings.get().getBoolean(key, defValue);
        }
        catch (Exception e) {
            Game.reportException(e);
            return defValue;
        }
    }

    public static String getString(String key, String defValue) {
        return GameSettings.getString(key, defValue, Integer.MAX_VALUE);
    }

    public static String getString(String key, String defValue, int maxLength) {
        try {
            String s = GameSettings.get().getString(key, defValue);
            if (s != null && s.length() > maxLength) {
                GameSettings.put(key, defValue);
                return defValue;
            }
            return s;
        }
        catch (Exception e) {
            Game.reportException(e);
            GameSettings.put(key, defValue);
            return defValue;
        }
    }

    public static void put(String key, int value) {
        GameSettings.get().putInteger(key, value);
        GameSettings.get().flush();
    }

    public static void put(String key, long value) {
        GameSettings.get().putLong(key, value);
        GameSettings.get().flush();
    }

    public static void put(String key, boolean value) {
        GameSettings.get().putBoolean(key, value);
        GameSettings.get().flush();
    }

    public static void put(String key, String value) {
        GameSettings.get().putString(key, value);
        GameSettings.get().flush();
    }
}

