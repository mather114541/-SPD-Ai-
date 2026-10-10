/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.DefaultPool;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class PoolManager {
    private final ObjectMap<Class<?>, Pool<?>> typePools = new ObjectMap();

    public PoolManager() {
    }

    public PoolManager(DefaultPool.PoolSupplier<?> ... poolSuppliers) {
        for (DefaultPool.PoolSupplier<?> poolSupplier : poolSuppliers) {
            this.addPool(poolSupplier);
        }
    }

    public PoolManager(Pool<?> ... pools) {
        for (Pool<?> pool : pools) {
            this.addPool(pool);
        }
    }

    public <T> void addPool(DefaultPool.PoolSupplier<T> poolSupplier) {
        this.addPool(new DefaultPool<T>(poolSupplier));
    }

    public <T> void addPool(Pool<T> pool) {
        T object = pool.obtain();
        Class<?> clazz = object.getClass();
        pool.free(object);
        Pool<T> oldPool = this.typePools.put(clazz, pool);
        if (oldPool != null) {
            throw new GdxRuntimeException("Attempt to add pool with already existing class: " + clazz + ", register using PoolManager#addPool(" + clazz.getSimpleName() + "::new)");
        }
    }

    public <T> Pool<T> getPool(Class<T> clazz) {
        Pool<?> pool = this.typePools.get(clazz);
        if (pool == null) {
            throw new GdxRuntimeException("Attempt to get pool with unknown class: " + clazz + ", register using PoolManager#addPool(" + clazz.getSimpleName() + "::new)");
        }
        return pool;
    }

    public <T> Pool<T> getPoolOrNull(Class<T> clazz) {
        return this.typePools.get(clazz);
    }

    public <T> T obtain(Class<T> clazz) {
        Pool<?> pool = this.typePools.get(clazz);
        if (pool == null) {
            throw new GdxRuntimeException("Attempt to get pooled object with unknown class: " + clazz + ", register using PoolManager#addPool(" + clazz.getSimpleName() + "::new)");
        }
        return (T)pool.obtain();
    }

    public <T> T obtainOrNull(Class<T> clazz) {
        Pool<?> pool = this.typePools.get(clazz);
        if (pool == null) {
            return null;
        }
        return (T)pool.obtain();
    }

    public <T> void free(T object) {
        Pool<?> pool = this.typePools.get(object.getClass());
        if (pool == null) {
            throw new GdxRuntimeException("Attempt to free pooled object with unknown class: " + object.getClass() + ", register using PoolManager#addPool(" + object.getClass().getSimpleName() + "::new)");
        }
        pool.free(object);
    }

    public void clear() {
        for (Pool pool : this.typePools.values()) {
            pool.clear();
        }
    }
}

