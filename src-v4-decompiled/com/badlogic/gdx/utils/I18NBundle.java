/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.utils.TextFormatter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class I18NBundle {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static boolean simpleFormatter = false;
    private static boolean exceptionOnMissingKey = true;
    private I18NBundle parent;
    private Locale locale;
    private ObjectMap<String, String> properties;
    private TextFormatter formatter;

    public static boolean getSimpleFormatter() {
        return simpleFormatter;
    }

    public static void setSimpleFormatter(boolean enabled) {
        simpleFormatter = enabled;
    }

    public static boolean getExceptionOnMissingKey() {
        return exceptionOnMissingKey;
    }

    public static void setExceptionOnMissingKey(boolean enabled) {
        exceptionOnMissingKey = enabled;
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle) {
        return I18NBundle.createBundleImpl(baseFileHandle, Locale.getDefault(), DEFAULT_ENCODING);
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle, Locale locale) {
        return I18NBundle.createBundleImpl(baseFileHandle, locale, DEFAULT_ENCODING);
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle, String encoding) {
        return I18NBundle.createBundleImpl(baseFileHandle, Locale.getDefault(), encoding);
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle, Locale locale, String encoding) {
        return I18NBundle.createBundleImpl(baseFileHandle, locale, encoding);
    }

    private static I18NBundle createBundleImpl(FileHandle baseFileHandle, Locale locale, String encoding) {
        if (baseFileHandle == null || locale == null || encoding == null) {
            throw new NullPointerException();
        }
        I18NBundle bundle = null;
        I18NBundle baseBundle = null;
        Locale targetLocale = locale;
        do {
            List<Locale> candidateLocales;
            if ((bundle = I18NBundle.loadBundleChain(baseFileHandle, encoding, candidateLocales = I18NBundle.getCandidateLocales(targetLocale), 0, baseBundle)) == null) continue;
            Locale bundleLocale = bundle.getLocale();
            boolean isBaseBundle = bundleLocale.equals(Locale.ROOT);
            if (!isBaseBundle || bundleLocale.equals(locale) || candidateLocales.size() == 1 && bundleLocale.equals(candidateLocales.get(0))) break;
            if (baseBundle != null) continue;
            baseBundle = bundle;
        } while ((targetLocale = I18NBundle.getFallbackLocale(targetLocale)) != null);
        if (bundle == null) {
            if (baseBundle == null) {
                throw new MissingResourceException("Can't find bundle for base file handle " + baseFileHandle.path() + ", locale " + locale, baseFileHandle + "_" + locale, "");
            }
            bundle = baseBundle;
        }
        return bundle;
    }

    private static List<Locale> getCandidateLocales(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        ArrayList<Locale> locales = new ArrayList<Locale>(4);
        if (!variant.isEmpty()) {
            locales.add(locale);
        }
        if (!country.isEmpty()) {
            locales.add(locales.isEmpty() ? locale : new Locale(language, country));
        }
        if (!language.isEmpty()) {
            locales.add(locales.isEmpty() ? locale : new Locale(language));
        }
        locales.add(Locale.ROOT);
        return locales;
    }

    private static Locale getFallbackLocale(Locale locale) {
        Locale defaultLocale = Locale.getDefault();
        return locale.equals(defaultLocale) ? null : defaultLocale;
    }

    private static I18NBundle loadBundleChain(FileHandle baseFileHandle, String encoding, List<Locale> candidateLocales, int candidateIndex, I18NBundle baseBundle) {
        Locale targetLocale = candidateLocales.get(candidateIndex);
        I18NBundle parent = null;
        if (candidateIndex != candidateLocales.size() - 1) {
            parent = I18NBundle.loadBundleChain(baseFileHandle, encoding, candidateLocales, candidateIndex + 1, baseBundle);
        } else if (baseBundle != null && targetLocale.equals(Locale.ROOT)) {
            return baseBundle;
        }
        I18NBundle bundle = I18NBundle.loadBundle(baseFileHandle, encoding, targetLocale);
        if (bundle != null) {
            bundle.parent = parent;
            return bundle;
        }
        return parent;
    }

    private static I18NBundle loadBundle(FileHandle baseFileHandle, String encoding, Locale targetLocale) {
        I18NBundle bundle = null;
        Reader reader = null;
        try {
            FileHandle fileHandle = I18NBundle.toFileHandle(baseFileHandle, targetLocale);
            if (I18NBundle.checkFileExistence(fileHandle)) {
                bundle = new I18NBundle();
                reader = fileHandle.reader(encoding);
                bundle.load(reader);
            }
        }
        catch (IOException e) {
            throw new GdxRuntimeException(e);
        }
        finally {
            StreamUtils.closeQuietly(reader);
        }
        if (bundle != null) {
            bundle.setLocale(targetLocale);
        }
        return bundle;
    }

    private static boolean checkFileExistence(FileHandle fh) {
        try {
            fh.read().close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    protected void load(Reader reader) throws IOException {
        this.properties = new ObjectMap();
        PropertiesUtils.load(this.properties, reader);
    }

    private static FileHandle toFileHandle(FileHandle baseFileHandle, Locale locale) {
        StringBuilder sb = new StringBuilder(baseFileHandle.name());
        if (!locale.equals(Locale.ROOT)) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            String variant = locale.getVariant();
            if (!(language.isEmpty() && country.isEmpty() && variant.isEmpty())) {
                sb.append('_');
                if (!variant.isEmpty()) {
                    sb.append(language).append('_').append(country).append('_').append(variant);
                } else if (!country.isEmpty()) {
                    sb.append(language).append('_').append(country);
                } else {
                    sb.append(language);
                }
            }
        }
        return baseFileHandle.sibling(sb.append(".properties").toString());
    }

    public Locale getLocale() {
        return this.locale;
    }

    private void setLocale(Locale locale) {
        this.locale = locale;
        this.formatter = new TextFormatter(locale, !simpleFormatter);
    }

    public String get(String key) {
        String result = this.properties.get(key);
        if (result == null) {
            if (this.parent != null) {
                result = this.parent.get(key);
            }
            if (result == null) {
                if (exceptionOnMissingKey) {
                    throw new MissingResourceException("Can't find bundle key " + key, this.getClass().getName(), key);
                }
                return "???" + key + "???";
            }
        }
        return result;
    }

    public Set<String> keys() {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        ObjectMap.Keys<String> keys = this.properties.keys();
        if (keys != null) {
            for (String key : keys) {
                result.add(key);
            }
        }
        return result;
    }

    public String format(String key, Object ... args) {
        return this.formatter.format(this.get(key), args);
    }

    public void debug(String placeholder) {
        ObjectMap.Keys<String> keys = this.properties.keys();
        if (keys == null) {
            return;
        }
        for (String s : keys) {
            this.properties.put(s, placeholder);
        }
    }
}

