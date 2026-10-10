/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics;

public interface Texture3DData {
    public boolean isPrepared();

    public void prepare();

    public int getWidth();

    public int getHeight();

    public int getDepth();

    public int getInternalFormat();

    public int getGLType();

    public boolean useMipMaps();

    public void consume3DData();

    public boolean isManaged();
}

