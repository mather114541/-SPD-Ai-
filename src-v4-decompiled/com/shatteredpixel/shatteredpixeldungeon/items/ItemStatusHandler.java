/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class ItemStatusHandler<T extends Item> {
    private Class<? extends T>[] items;
    private LinkedHashMap<Class<? extends T>, String> itemLabels;
    private LinkedHashMap<String, Integer> labelImages;
    private LinkedHashSet<Class<? extends T>> known;
    private static final String PFX_LABEL = "_label";
    private static final String PFX_KNOWN = "_known";

    public ItemStatusHandler(Class<? extends T>[] items, HashMap<String, Integer> labelImages) {
        this.items = items;
        this.itemLabels = new LinkedHashMap();
        this.labelImages = new LinkedHashMap<String, Integer>(labelImages);
        this.known = new LinkedHashSet();
        ArrayList<String> labelsLeft = new ArrayList<String>(labelImages.keySet());
        for (int i = 0; i < items.length; ++i) {
            Class<? extends T> item = items[i];
            int index = Random.Int(labelsLeft.size());
            this.itemLabels.put(item, labelsLeft.get(index));
            labelsLeft.remove(index);
        }
    }

    public ItemStatusHandler(Class<? extends T>[] items, HashMap<String, Integer> labelImages, Bundle bundle) {
        this.items = items;
        this.itemLabels = new LinkedHashMap();
        this.labelImages = new LinkedHashMap<String, Integer>(labelImages);
        this.known = new LinkedHashSet();
        ArrayList<String> allLabels = new ArrayList<String>(labelImages.keySet());
        this.restore(bundle, allLabels);
    }

    public void save(Bundle bundle) {
        for (int i = 0; i < this.items.length; ++i) {
            String itemName = this.items[i].getSimpleName();
            bundle.put(itemName + PFX_LABEL, this.itemLabels.get(this.items[i]));
            bundle.put(itemName + PFX_KNOWN, this.known.contains(this.items[i]));
        }
    }

    public void saveSelectively(Bundle bundle, ArrayList<Item> itemsToSave) {
        List<Class<T>> items = Arrays.asList(this.items);
        for (Item item : itemsToSave) {
            if (!items.contains(item.getClass())) continue;
            Class<T> cls = items.get(items.indexOf(item.getClass()));
            String itemName = cls.getSimpleName();
            bundle.put(itemName + PFX_LABEL, this.itemLabels.get(cls));
            bundle.put(itemName + PFX_KNOWN, this.known.contains(cls));
        }
    }

    public void saveClassesSelectively(Bundle bundle, ArrayList<Class<? extends Item>> clsToSave) {
        List<Class<T>> items = Arrays.asList(this.items);
        for (Class<? extends Item> cls : clsToSave) {
            if (!items.contains(cls)) continue;
            Class<T> toSave = items.get(items.indexOf(cls));
            String itemName = toSave.getSimpleName();
            bundle.put(itemName + PFX_LABEL, this.itemLabels.get(toSave));
            bundle.put(itemName + PFX_KNOWN, this.known.contains(toSave));
        }
    }

    private void restore(Bundle bundle, ArrayList<String> labelsLeft) {
        String itemName;
        ArrayList<Class<? extends T>> unlabelled = new ArrayList<Class<? extends T>>();
        for (int i = 0; i < this.items.length; ++i) {
            Class<T> clazz = this.items[i];
            itemName = clazz.getSimpleName();
            if (bundle.contains(itemName + PFX_LABEL)) {
                String label = bundle.getString(itemName + PFX_LABEL);
                this.itemLabels.put(clazz, label);
                labelsLeft.remove(label);
                if (!bundle.getBoolean(itemName + PFX_KNOWN)) continue;
                this.known.add(clazz);
                continue;
            }
            unlabelled.add(this.items[i]);
        }
        for (Class clazz : unlabelled) {
            itemName = clazz.getSimpleName();
            int index = Random.Int(labelsLeft.size());
            this.itemLabels.put(clazz, labelsLeft.get(index));
            labelsLeft.remove(index);
            if (!bundle.contains(itemName + PFX_KNOWN) || !bundle.getBoolean(itemName + PFX_KNOWN)) continue;
            this.known.add(clazz);
        }
    }

    public boolean contains(T item) {
        for (Class<? extends T> i : this.items) {
            if (!item.getClass().equals(i)) continue;
            return true;
        }
        return false;
    }

    public boolean contains(Class<? extends T> itemCls) {
        for (Class<? extends T> i : this.items) {
            if (!itemCls.equals(i)) continue;
            return true;
        }
        return false;
    }

    public int image(T item) {
        return this.labelImages.get(this.label(item));
    }

    public int image(Class<? extends T> itemCls) {
        return this.labelImages.get(this.label(itemCls));
    }

    public String label(T item) {
        return this.itemLabels.get(item.getClass());
    }

    public String label(Class<? extends T> itemCls) {
        return this.itemLabels.get(itemCls);
    }

    public boolean isKnown(T item) {
        return this.known.contains(item.getClass());
    }

    public boolean isKnown(Class<? extends T> itemCls) {
        return this.known.contains(itemCls);
    }

    public void know(T item) {
        this.known.add(item.getClass());
    }

    public void know(Class<? extends T> itemCls) {
        this.known.add(itemCls);
    }

    public HashSet<Class<? extends T>> known() {
        return this.known;
    }

    public HashSet<Class<? extends T>> unknown() {
        LinkedHashSet<Class<T>> result = new LinkedHashSet<Class<T>>();
        for (Class<? extends T> i : this.items) {
            if (this.known.contains(i)) continue;
            result.add(i);
        }
        return result;
    }
}

