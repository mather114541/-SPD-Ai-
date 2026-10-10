/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers;

import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.ControllerPowerLevel;

public interface Controller {
    public static final int PLAYER_IDX_UNSET = -1;

    public boolean getButton(int var1);

    public float getAxis(int var1);

    public String getName();

    public String getUniqueId();

    public int getMinButtonIndex();

    public int getMaxButtonIndex();

    public int getAxisCount();

    public boolean isConnected();

    public boolean canVibrate();

    public boolean isVibrating();

    public void startVibration(int var1, float var2);

    public void cancelVibration();

    public boolean supportsPlayerIndex();

    public int getPlayerIndex();

    public void setPlayerIndex(int var1);

    public ControllerMapping getMapping();

    public ControllerPowerLevel getPowerLevel();

    public void addListener(ControllerListener var1);

    public void removeListener(ControllerListener var1);
}

