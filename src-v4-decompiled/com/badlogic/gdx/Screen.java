/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx;

import com.badlogic.gdx.utils.Disposable;

public interface Screen
extends Disposable {
    public void show();

    public void render(float var1);

    public void resize(int var1, int var2);

    public void pause();

    public void resume();

    public void hide();

    @Override
    public void dispose();
}

