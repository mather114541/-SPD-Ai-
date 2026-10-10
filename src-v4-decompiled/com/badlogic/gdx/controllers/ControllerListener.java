/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers;

import com.badlogic.gdx.controllers.Controller;

public interface ControllerListener {
    public void connected(Controller var1);

    public void disconnected(Controller var1);

    public boolean buttonDown(Controller var1, int var2);

    public boolean buttonUp(Controller var1, int var2);

    public boolean axisMoved(Controller var1, int var2, float var3);
}

