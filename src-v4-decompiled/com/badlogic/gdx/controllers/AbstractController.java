/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import java.util.Iterator;

abstract class AbstractController
implements Disposable,
Controller {
    private final Array<ControllerListener> listeners = new Array();
    private boolean connected = true;

    AbstractController() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose() {
        Array<ControllerListener> array = this.listeners;
        synchronized (array) {
            this.listeners.clear();
        }
        this.connected = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void notifyListenersButtonUp(int button) {
        ControllerListener listener;
        Iterator iterator;
        Array<ControllerListener> managerListeners;
        Array<ControllerListener> array = managerListeners = Controllers.getListeners();
        synchronized (array) {
            iterator = managerListeners.iterator();
            while (iterator.hasNext() && !(listener = (ControllerListener)iterator.next()).buttonUp(this, button)) {
            }
        }
        array = this.listeners;
        synchronized (array) {
            iterator = this.listeners.iterator();
            while (iterator.hasNext() && !(listener = (ControllerListener)iterator.next()).buttonUp(this, button)) {
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void notifyListenersButtonDown(int button) {
        ControllerListener listener;
        Iterator iterator;
        Array<ControllerListener> managerListeners;
        Array<ControllerListener> array = managerListeners = Controllers.getListeners();
        synchronized (array) {
            iterator = managerListeners.iterator();
            while (iterator.hasNext() && !(listener = (ControllerListener)iterator.next()).buttonDown(this, button)) {
            }
        }
        array = this.listeners;
        synchronized (array) {
            iterator = this.listeners.iterator();
            while (iterator.hasNext() && !(listener = (ControllerListener)iterator.next()).buttonDown(this, button)) {
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void notifyListenersAxisMoved(int axisNum, float value) {
        ControllerListener listener;
        Iterator iterator;
        Array<ControllerListener> managerListeners;
        Array<ControllerListener> array = managerListeners = Controllers.getListeners();
        synchronized (array) {
            iterator = managerListeners.iterator();
            while (iterator.hasNext() && !(listener = (ControllerListener)iterator.next()).axisMoved(this, axisNum, value)) {
            }
        }
        array = this.listeners;
        synchronized (array) {
            iterator = this.listeners.iterator();
            while (iterator.hasNext() && !(listener = (ControllerListener)iterator.next()).axisMoved(this, axisNum, value)) {
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addListener(ControllerListener controllerListener) {
        Array<ControllerListener> array = this.listeners;
        synchronized (array) {
            if (!this.listeners.contains(controllerListener, true)) {
                this.listeners.add(controllerListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeListener(ControllerListener controllerListener) {
        Array<ControllerListener> array = this.listeners;
        synchronized (array) {
            this.listeners.removeValue(controllerListener, true);
        }
    }

    @Override
    public boolean canVibrate() {
        return false;
    }

    @Override
    public boolean isVibrating() {
        return false;
    }

    @Override
    public void startVibration(int duration, float strength) {
    }

    @Override
    public void cancelVibration() {
    }

    @Override
    public boolean supportsPlayerIndex() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public int getPlayerIndex() {
        return -1;
    }

    @Override
    public void setPlayerIndex(int index) {
    }
}

