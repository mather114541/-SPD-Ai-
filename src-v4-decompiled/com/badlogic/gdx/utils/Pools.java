/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;
import com.badlogic.gdx.scenes.scene2d.actions.AddListenerAction;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.badlogic.gdx.scenes.scene2d.actions.LayoutAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveListenerAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeToAction;
import com.badlogic.gdx.scenes.scene2d.actions.TimeScaleAction;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DefaultPool;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

@Deprecated
public class Pools {
    private static final ObjectMap<Class<?>, Pool<?>> typePools = new ObjectMap();
    public static boolean WARN_ON_REFLECTION_POOL_CREATION = true;
    public static boolean THROW_ON_REFLECTION_POOL_CREATION = false;

    public static <T> Pool<T> get(Class<T> type, int max) {
        Pool<?> pool = typePools.get(type);
        if (pool == null) {
            if (THROW_ON_REFLECTION_POOL_CREATION) {
                throw new RuntimeException("Please manually define a Pool for " + type + " by calling Pools#set before calling Pools#get");
            }
            if (WARN_ON_REFLECTION_POOL_CREATION && Gdx.app != null) {
                Gdx.app.error("Pools", "Please manually define a Pool for " + type + " by calling Pools#set before calling Pools#get");
            }
            pool = new ReflectionPool(type, 4, max);
            typePools.put(type, pool);
        }
        return pool;
    }

    public static <T> Pool<T> get(Class<T> type) {
        return Pools.get(type, 100);
    }

    public static <T> void set(Class<T> type, Pool<T> pool) {
        typePools.put(type, pool);
    }

    public static <T> void set(DefaultPool.PoolSupplier<T> poolTypeSupplier, int max) {
        Pools.set(poolTypeSupplier.get().getClass(), new DefaultPool<T>(poolTypeSupplier, 4, max));
    }

    public static <T> void set(DefaultPool.PoolSupplier<T> poolTypeSupplier) {
        Pools.set(poolTypeSupplier, 100);
    }

    public static <T> T obtain(Class<T> type) {
        return Pools.get(type).obtain();
    }

    public static void free(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        Pool<?> pool = typePools.get(object.getClass());
        if (pool == null) {
            return;
        }
        pool.free(object);
    }

    public static void freeAll(Array objects) {
        Pools.freeAll(objects, false);
    }

    public static void freeAll(Array objects, boolean samePool) {
        if (objects == null) {
            throw new IllegalArgumentException("objects cannot be null.");
        }
        Pool<?> pool = null;
        int n = objects.size;
        for (int i = 0; i < n; ++i) {
            Object object = objects.get(i);
            if (object == null || pool == null && (pool = typePools.get(object.getClass())) == null) continue;
            pool.free(object);
            if (samePool) continue;
            pool = null;
        }
    }

    private Pools() {
    }

    static {
        Pools.set(Array::new);
        Pools.set(ChangeListener.ChangeEvent::new);
        Pools.set(Table.DebugRect::new);
        Pools.set(FocusListener.FocusEvent::new);
        Pools.set(GlyphLayout.GlyphRun::new);
        Pools.set(GlyphLayout::new);
        Pools.set(Net.HttpRequest::new);
        Pools.set(InputEvent::new);
        Pools.set(Rectangle::new);
        Pools.set(Stage.TouchFocus::new);
        Pools.set(AddAction::new);
        Pools.set(AddListenerAction::new);
        Pools.set(AfterAction::new);
        Pools.set(AlphaAction::new);
        Pools.set(ColorAction::new);
        Pools.set(DelayAction::new);
        Pools.set(FloatAction::new);
        Pools.set(IntAction::new);
        Pools.set(LayoutAction::new);
        Pools.set(MoveByAction::new);
        Pools.set(MoveToAction::new);
        Pools.set(ParallelAction::new);
        Pools.set(RemoveAction::new);
        Pools.set(RemoveActorAction::new);
        Pools.set(RemoveListenerAction::new);
        Pools.set(RepeatAction::new);
        Pools.set(RotateByAction::new);
        Pools.set(RotateToAction::new);
        Pools.set(RunnableAction::new);
        Pools.set(ScaleByAction::new);
        Pools.set(ScaleToAction::new);
        Pools.set(SequenceAction::new);
        Pools.set(SizeByAction::new);
        Pools.set(SizeToAction::new);
        Pools.set(TimeScaleAction::new);
        Pools.set(TouchableAction::new);
        Pools.set(VisibleAction::new);
    }
}

