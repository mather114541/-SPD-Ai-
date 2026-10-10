/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

public interface ArraySupplier<T> {
    public static final ArraySupplier<?> ANY = Object[]::new;

    public static <T> ArraySupplier<T[]> object() {
        return ANY;
    }

    public T get(int var1);
}

