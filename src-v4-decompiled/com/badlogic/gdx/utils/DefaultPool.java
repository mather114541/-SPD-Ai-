/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.Pool;

public class DefaultPool<T>
extends Pool<T> {
    private final PoolSupplier<T> poolTypeSupplier;

    public DefaultPool(PoolSupplier<T> supplier) {
        this(supplier, 16, Integer.MAX_VALUE);
    }

    public DefaultPool(PoolSupplier<T> supplier, int initialCapacity) {
        this(supplier, initialCapacity, Integer.MAX_VALUE);
    }

    public DefaultPool(PoolSupplier<T> supplier, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.poolTypeSupplier = supplier;
    }

    @Override
    protected T newObject() {
        return this.poolTypeSupplier.get();
    }

    public static interface PoolSupplier<T> {
        public T get();
    }
}

