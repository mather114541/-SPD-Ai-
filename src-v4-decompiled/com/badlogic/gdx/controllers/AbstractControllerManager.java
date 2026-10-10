/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerManager;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractControllerManager
implements ControllerManager {
    protected final Array<Controller> controllers = new Array();
    private Controller currentController;

    @Override
    public Array<Controller> getControllers() {
        return this.controllers;
    }

    @Override
    public Controller getCurrentController() {
        return this.currentController;
    }

    public class ManageCurrentControllerListener
    extends ControllerAdapter {
        @Override
        public void connected(Controller controller) {
            if (AbstractControllerManager.this.currentController == null) {
                AbstractControllerManager.this.currentController = controller;
            }
        }

        @Override
        public void disconnected(Controller controller) {
            if (AbstractControllerManager.this.currentController == controller) {
                AbstractControllerManager.this.currentController = null;
            }
        }

        @Override
        public boolean buttonDown(Controller controller, int buttonIndex) {
            AbstractControllerManager.this.currentController = controller;
            return false;
        }

        @Override
        public boolean buttonUp(Controller controller, int buttonIndex) {
            AbstractControllerManager.this.currentController = controller;
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            AbstractControllerManager.this.currentController = controller;
            return false;
        }
    }
}

