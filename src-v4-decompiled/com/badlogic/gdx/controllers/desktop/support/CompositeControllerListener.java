/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers.desktop.support;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import java.util.LinkedList;

public class CompositeControllerListener
implements ControllerListener {
    private final LinkedList<ControllerListener> listeners = new LinkedList();

    @Override
    public void connected(Controller controller) {
        for (ControllerListener listener : this.listeners) {
            listener.connected(controller);
        }
    }

    @Override
    public void disconnected(Controller controller) {
        for (ControllerListener listener : this.listeners) {
            listener.disconnected(controller);
        }
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        for (ControllerListener listener : this.listeners) {
            if (!listener.buttonDown(controller, buttonCode)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        for (ControllerListener listener : this.listeners) {
            if (!listener.buttonUp(controller, buttonCode)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        for (ControllerListener listener : this.listeners) {
            if (!listener.axisMoved(controller, axisCode, value)) continue;
            return true;
        }
        return false;
    }

    public void addListener(ControllerListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ControllerListener listener) {
        this.listeners.remove(listener);
    }

    public void clear() {
        this.listeners.clear();
    }
}

