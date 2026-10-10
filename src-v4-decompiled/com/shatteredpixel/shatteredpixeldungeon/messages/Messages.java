/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;

public class Messages {
    private static ArrayList<I18NBundle> bundles;
    private static Languages lang;
    private static Locale locale;
    public static final String NO_TEXT_FOUND = "!!!NO TEXT FOUND!!!";
    private static String[] prop_files;
    private static HashMap<String, DecimalFormat> formatters;
    private static final HashSet<String> noCaps;

    public static Languages lang() {
        return lang;
    }

    public static Locale locale() {
        return locale;
    }

    public static void setup(Languages lang) {
        Locale bundleLocal;
        I18NBundle.setExceptionOnMissingKey(false);
        Messages.lang = lang;
        if (lang == Languages.ENGLISH) {
            locale = Locale.ENGLISH;
            bundleLocal = Locale.ROOT;
        } else {
            bundleLocal = locale = new Locale(lang.code());
        }
        formatters.clear();
        bundles = new ArrayList();
        for (String file : prop_files) {
            if (bundleLocal.getLanguage().equals("id")) {
                bundles.add(I18NBundle.createBundle(Gdx.files.internal(file + "_in"), bundleLocal));
                continue;
            }
            bundles.add(I18NBundle.createBundle(Gdx.files.internal(file), bundleLocal));
        }
    }

    public static String get(String key, Object ... args) {
        return Messages.get(null, key, args);
    }

    public static String get(Object o, String k, Object ... args) {
        return Messages.get(o.getClass(), k, args);
    }

    public static String get(Class c, String k, Object ... args) {
        Object key;
        if (c != null) {
            key = c.getName().replace("com.shatteredpixel.shatteredpixeldungeon.", "");
            key = (String)key + "." + k;
        } else {
            key = k;
        }
        String value = Messages.getFromBundle(((String)key).toLowerCase(Locale.ENGLISH));
        if (value != null) {
            if (args.length > 0) {
                return Messages.format(value, args);
            }
            return value;
        }
        if (c != null && c.getSuperclass() != null) {
            return Messages.get(c.getSuperclass(), k, args);
        }
        return NO_TEXT_FOUND;
    }

    private static String getFromBundle(String key) {
        for (I18NBundle b : bundles) {
            String result = b.get(key);
            if (result.length() == key.length() + 6 && result.contains(key)) continue;
            return result;
        }
        return null;
    }

    public static String format(String format, Object ... args) {
        try {
            return String.format(Messages.locale(), format, args);
        }
        catch (IllegalFormatException e) {
            ShatteredPixelDungeon.reportException(new Exception("formatting error for the string: " + format, e));
            return format;
        }
    }

    public static String decimalFormat(String format, double number) {
        if (!formatters.containsKey(format)) {
            formatters.put(format, new DecimalFormat(format, DecimalFormatSymbols.getInstance(Messages.locale())));
        }
        return formatters.get(format).format(number);
    }

    public static String capitalize(String str) {
        if (str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase(locale) + str.substring(1);
    }

    public static String titleCase(String str) {
        if (lang == Languages.ENGLISH) {
            Object result = "";
            for (String word : str.split("(?<=\\p{Zs})")) {
                result = noCaps.contains(word.trim().toLowerCase(Locale.ENGLISH).replaceAll(":|[0-9]", "")) ? (String)result + word : (String)result + Messages.capitalize(word);
            }
            return Messages.capitalize((String)result);
        }
        return Messages.capitalize(str);
    }

    public static String upperCase(String str) {
        return str.toUpperCase(locale);
    }

    public static String lowerCase(String str) {
        return str.toLowerCase(locale);
    }

    static {
        prop_files = new String[]{"messages/actors/actors", "messages/items/items", "messages/journal/journal", "messages/levels/levels", "messages/misc/misc", "messages/plants/plants", "messages/scenes/scenes", "messages/ui/ui", "messages/windows/windows"};
        formatters = new HashMap();
        Messages.setup(SPDSettings.language());
        noCaps = new HashSet<String>(Arrays.asList("a", "an", "and", "of", "by", "to", "the", "x", "for"));
    }
}

