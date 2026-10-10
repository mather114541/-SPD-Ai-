/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.lwjgl.system.windows;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class POINTL
extends Struct<POINTL>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X;
    public static final int Y;

    protected POINTL(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected POINTL create(long address, @Nullable ByteBuffer container) {
        return new POINTL(address, container);
    }

    public POINTL(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), POINTL.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="LONG")
    public int x() {
        return POINTL.nx(this.address());
    }

    @NativeType(value="LONG")
    public int y() {
        return POINTL.ny(this.address());
    }

    public POINTL x(@NativeType(value="LONG") int value) {
        POINTL.nx(this.address(), value);
        return this;
    }

    public POINTL y(@NativeType(value="LONG") int value) {
        POINTL.ny(this.address(), value);
        return this;
    }

    public POINTL set(int x, int y) {
        this.x(x);
        this.y(y);
        return this;
    }

    public POINTL set(POINTL src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static POINTL malloc() {
        return new POINTL(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static POINTL calloc() {
        return new POINTL(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static POINTL create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new POINTL(MemoryUtil.memAddress(container), container);
    }

    public static POINTL create(long address) {
        return new POINTL(address, null);
    }

    @Nullable
    public static POINTL createSafe(long address) {
        return address == 0L ? null : new POINTL(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(POINTL.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = POINTL.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Deprecated
    public static POINTL mallocStack() {
        return POINTL.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static POINTL callocStack() {
        return POINTL.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static POINTL mallocStack(MemoryStack stack) {
        return POINTL.malloc(stack);
    }

    @Deprecated
    public static POINTL callocStack(MemoryStack stack) {
        return POINTL.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return POINTL.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return POINTL.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return POINTL.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return POINTL.calloc(capacity, stack);
    }

    public static POINTL malloc(MemoryStack stack) {
        return new POINTL(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static POINTL calloc(MemoryStack stack) {
        return new POINTL(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nx(long struct) {
        return UNSAFE.getInt(null, struct + (long)X);
    }

    public static int ny(long struct) {
        return UNSAFE.getInt(null, struct + (long)Y);
    }

    public static void nx(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)X, value);
    }

    public static void ny(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)Y, value);
    }

    static {
        Struct.Layout layout = POINTL.__struct(POINTL.__member(4), POINTL.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X = layout.offsetof(0);
        Y = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<POINTL, Buffer>
    implements NativeResource {
        private static final POINTL ELEMENT_FACTORY = POINTL.create(-1L);

        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected POINTL getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="LONG")
        public int x() {
            return POINTL.nx(this.address());
        }

        @NativeType(value="LONG")
        public int y() {
            return POINTL.ny(this.address());
        }

        public Buffer x(@NativeType(value="LONG") int value) {
            POINTL.nx(this.address(), value);
            return this;
        }

        public Buffer y(@NativeType(value="LONG") int value) {
            POINTL.ny(this.address(), value);
            return this;
        }
    }
}

