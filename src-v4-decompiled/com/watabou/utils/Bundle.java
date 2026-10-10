/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Reflection;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Bundle {
    private static final String CLASS_NAME = "__className";
    public static final String DEFAULT_KEY = "key";
    private static HashMap<String, String> aliases = new HashMap();
    private JSONObject data;
    private static final boolean compressByDefault = true;
    private static final int GZIP_BUFFER = 4096;

    public Bundle() {
        this(new JSONObject());
    }

    public String toString() {
        return this.data.toString();
    }

    private Bundle(JSONObject data) {
        this.data = data;
    }

    public boolean isNull() {
        return this.data == null;
    }

    public boolean contains(String key) {
        return !this.isNull() && !this.data.isNull(key);
    }

    public boolean remove(String key) {
        return this.data.remove(key) != null;
    }

    public ArrayList<String> getKeys() {
        Iterator<String> keys = this.data.keys();
        ArrayList<String> result = new ArrayList<String>();
        while (keys.hasNext()) {
            result.add(keys.next());
        }
        return result;
    }

    public boolean getBoolean(String key) {
        return this.data.optBoolean(key);
    }

    public int getInt(String key) {
        return this.data.optInt(key);
    }

    public long getLong(String key) {
        return this.data.optLong(key);
    }

    public float getFloat(String key) {
        return (float)this.data.optDouble(key, 0.0);
    }

    public String getString(String key) {
        return this.data.optString(key);
    }

    public Class getClass(String key) {
        String clName = this.getString(key).replace("class ", "");
        if (!clName.equals("")) {
            if (aliases.containsKey(clName)) {
                clName = aliases.get(clName);
            }
            return Reflection.forName(clName);
        }
        return null;
    }

    public Bundle getBundle(String key) {
        return new Bundle(this.data.optJSONObject(key));
    }

    private Bundlable get() {
        Bundlable object;
        Class cl;
        if (this.data == null) {
            return null;
        }
        String clName = this.getString(CLASS_NAME);
        if (aliases.containsKey(clName)) {
            clName = aliases.get(clName);
        }
        if ((cl = Reflection.forName(clName)) != null && (!Reflection.isMemberClass(cl) || Reflection.isStatic(cl)) && (object = (Bundlable)Reflection.newInstance(cl)) != null) {
            object.restoreFromBundle(this);
            return object;
        }
        return null;
    }

    public Bundlable get(String key) {
        return this.getBundle(key).get();
    }

    public <E extends Enum<E>> E getEnum(String key, Class<E> enumClass) {
        try {
            return Enum.valueOf(enumClass, this.data.getString(key));
        }
        catch (JSONException e) {
            Game.reportException(e);
            return (E)((Enum[])enumClass.getEnumConstants())[0];
        }
        catch (IllegalArgumentException e) {
            Game.reportException(e);
            return (E)((Enum[])enumClass.getEnumConstants())[0];
        }
    }

    public int[] getIntArray(String key) {
        try {
            JSONArray array = this.data.getJSONArray(key);
            int length = array.length();
            int[] result = new int[length];
            for (int i = 0; i < length; ++i) {
                result[i] = array.getInt(i);
            }
            return result;
        }
        catch (JSONException e) {
            Game.reportException(e);
            return null;
        }
    }

    public long[] getLongArray(String key) {
        try {
            JSONArray array = this.data.getJSONArray(key);
            int length = array.length();
            long[] result = new long[length];
            for (int i = 0; i < length; ++i) {
                result[i] = array.getLong(i);
            }
            return result;
        }
        catch (JSONException e) {
            Game.reportException(e);
            return null;
        }
    }

    public float[] getFloatArray(String key) {
        try {
            JSONArray array = this.data.getJSONArray(key);
            int length = array.length();
            float[] result = new float[length];
            for (int i = 0; i < length; ++i) {
                result[i] = (float)array.optDouble(i, 0.0);
            }
            return result;
        }
        catch (JSONException e) {
            Game.reportException(e);
            return null;
        }
    }

    public boolean[] getBooleanArray(String key) {
        try {
            JSONArray array = this.data.getJSONArray(key);
            int length = array.length();
            boolean[] result = new boolean[length];
            for (int i = 0; i < length; ++i) {
                result[i] = array.getBoolean(i);
            }
            return result;
        }
        catch (JSONException e) {
            Game.reportException(e);
            return null;
        }
    }

    public String[] getStringArray(String key) {
        try {
            JSONArray array = this.data.getJSONArray(key);
            int length = array.length();
            String[] result = new String[length];
            for (int i = 0; i < length; ++i) {
                result[i] = array.getString(i);
            }
            return result;
        }
        catch (JSONException e) {
            Game.reportException(e);
            return null;
        }
    }

    public Class[] getClassArray(String key) {
        try {
            JSONArray array = this.data.getJSONArray(key);
            int length = array.length();
            Class[] result = new Class[length];
            for (int i = 0; i < length; ++i) {
                Class cl;
                String clName = array.getString(i).replace("class ", "");
                if (aliases.containsKey(clName)) {
                    clName = aliases.get(clName);
                }
                result[i] = cl = Reflection.forName(clName);
            }
            return result;
        }
        catch (JSONException e) {
            Game.reportException(e);
            return null;
        }
    }

    public Bundle[] getBundleArray() {
        return this.getBundleArray(DEFAULT_KEY);
    }

    public Bundle[] getBundleArray(String key) {
        try {
            JSONArray array = this.data.getJSONArray(key);
            int length = array.length();
            Bundle[] result = new Bundle[length];
            for (int i = 0; i < length; ++i) {
                result[i] = new Bundle(array.getJSONObject(i));
            }
            return result;
        }
        catch (JSONException e) {
            Game.reportException(e);
            return null;
        }
    }

    public Collection<Bundlable> getCollection(String key) {
        ArrayList<Bundlable> list = new ArrayList<Bundlable>();
        try {
            JSONArray array = this.data.getJSONArray(key);
            for (int i = 0; i < array.length(); ++i) {
                Bundlable O = new Bundle(array.getJSONObject(i)).get();
                if (O == null) continue;
                list.add(O);
            }
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
        return list;
    }

    public void put(String key, boolean value) {
        try {
            this.data.put(key, value);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, int value) {
        try {
            this.data.put(key, value);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, long value) {
        try {
            this.data.put(key, value);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, float value) {
        try {
            this.data.put(key, value);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, String value) {
        try {
            this.data.put(key, value);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, Class value) {
        try {
            this.data.put(key, value);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, Bundle bundle) {
        try {
            this.data.put(key, bundle.data);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, Bundlable object) {
        if (object != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.put(CLASS_NAME, object.getClass().getName());
                object.storeInBundle(bundle);
                this.data.put(key, bundle.data);
            }
            catch (JSONException e) {
                Game.reportException(e);
            }
        }
    }

    public void put(String key, Enum<?> value) {
        if (value != null) {
            try {
                this.data.put(key, value.name());
            }
            catch (JSONException e) {
                Game.reportException(e);
            }
        }
    }

    public void put(String key, int[] array) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(i, array[i]);
            }
            this.data.put(key, jsonArray);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, long[] array) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(i, array[i]);
            }
            this.data.put(key, jsonArray);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, float[] array) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(i, array[i]);
            }
            this.data.put(key, jsonArray);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, boolean[] array) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(i, array[i]);
            }
            this.data.put(key, jsonArray);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, String[] array) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(i, array[i]);
            }
            this.data.put(key, jsonArray);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, Class[] array) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(i, array[i].getName());
            }
            this.data.put(key, jsonArray);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public void put(String key, Collection<? extends Bundlable> collection) {
        JSONArray array = new JSONArray();
        for (Bundlable bundlable : collection) {
            Class<?> cl;
            if (bundlable == null || Reflection.isMemberClass(cl = bundlable.getClass()) && !Reflection.isStatic(cl)) continue;
            Bundle bundle = new Bundle();
            bundle.put(CLASS_NAME, cl.getName());
            bundlable.storeInBundle(bundle);
            array.put(bundle.data);
        }
        try {
            this.data.put(key, array);
        }
        catch (JSONException e) {
            Game.reportException(e);
        }
    }

    public static Bundle read(InputStream stream) throws IOException {
        try {
            String line;
            if (!stream.markSupported()) {
                stream = new BufferedInputStream(stream, 2);
            }
            stream.mark(2);
            byte[] header = new byte[2];
            stream.read(header);
            stream.reset();
            if (header[0] == 31 && header[1] == -117) {
                stream = new GZIPInputStream(stream, 4096);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder jsonBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
                jsonBuilder.append("\n");
            }
            String jsonString = jsonBuilder.toString();
            Object json = new JSONTokener(jsonString).nextValue();
            reader.close();
            if (json instanceof JSONArray) {
                json = new JSONObject().put(DEFAULT_KEY, json);
            }
            if (!(json instanceof JSONObject)) {
                throw new JSONException("Malformed JSON Object: " + jsonString);
            }
            return new Bundle((JSONObject)json);
        }
        catch (Exception e) {
            Game.reportException(e);
            throw new IOException();
        }
    }

    public static boolean write(Bundle bundle, OutputStream stream) {
        return Bundle.write(bundle, stream, true);
    }

    public static boolean write(Bundle bundle, OutputStream stream, boolean compressed) {
        try {
            BufferedWriter writer = compressed ? new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(stream, 4096))) : new BufferedWriter(new OutputStreamWriter(stream));
            writer.write(bundle.data.toString());
            writer.close();
            return true;
        }
        catch (IOException e) {
            Game.reportException(e);
            return false;
        }
    }

    public static void addAlias(Class<?> cl, String alias) {
        aliases.put(alias, cl.getName());
    }
}

