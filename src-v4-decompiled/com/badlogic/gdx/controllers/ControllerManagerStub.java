/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers;

import com.badlogic.gdx.controllers.AbstractControllerManager;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.utils.Array;

public class ControllerManagerStub
extends AbstractControllerManager {
    @Override
    public void addListener(ControllerListener listener) {
    }

    @Override
    public void removeListener(ControllerListener listener) {
    }

    @Override
    public void clearListeners() {
    }

    @Override
    public Array<ControllerListener> getListeners() {
        return new Array<ControllerListener>();
    }
}

