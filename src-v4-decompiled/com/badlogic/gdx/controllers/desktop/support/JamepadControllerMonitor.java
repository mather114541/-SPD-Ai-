/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers.desktop.support;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.desktop.JamepadControllerManager;
import com.badlogic.gdx.controllers.desktop.support.JamepadController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerUnpluggedException;

public class JamepadControllerMonitor
implements Runnable {
    private final ControllerManager controllerManager;
    private final ControllerListener listener;
    private final IntMap<Tuple> indexToController;
    private final Array<JamepadController> connectedControllers;

    public JamepadControllerMonitor(ControllerManager controllerManager, ControllerListener listener) {
        this.indexToController = new IntMap(JamepadControllerManager.jamepadConfiguration.maxNumControllers);
        this.connectedControllers = new Array();
        this.controllerManager = controllerManager;
        this.listener = listener;
        this.reconcileControllers();
    }

    @Override
    public void run() {
        boolean controllersChanged = this.controllerManager.update();
        if (controllersChanged) {
            this.reconcileControllers();
        }
        this.update();
        Gdx.app.postRunnable(this);
    }

    private void reconcileControllers() {
        for (Tuple tuple : this.indexToController.values()) {
            JamepadController controller = tuple.controller;
            tuple.index = null;
            controller.setControllerIndex(null);
        }
        this.connectedControllers.clear();
        int numControllers = JamepadControllerManager.jamepadConfiguration.maxNumControllers;
        for (int i = 0; i < numControllers; ++i) {
            try {
                ControllerIndex controllerIndex = this.controllerManager.getControllerIndex(i);
                try {
                    Tuple tuple1;
                    int instanceID = controllerIndex.getDeviceInstanceID();
                    if (this.indexToController.containsKey(instanceID)) {
                        tuple1 = this.indexToController.get(instanceID);
                        tuple1.index = controllerIndex;
                        tuple1.controller.setControllerIndex(controllerIndex);
                        continue;
                    }
                    tuple1 = new Tuple(controllerIndex);
                    this.indexToController.put(instanceID, tuple1);
                    this.connectedControllers.add(tuple1.controller);
                }
                catch (ControllerUnpluggedException instanceID) {}
                continue;
            }
            catch (ArrayIndexOutOfBoundsException controllerIndex) {
                // empty catch block
            }
        }
        IntMap.Values<Tuple> values = this.indexToController.values();
        while (values.hasNext()) {
            Tuple tuple = values.next();
            if (tuple.index != null) continue;
            tuple.controller.setDisconnected();
            values.remove();
        }
        for (JamepadController controller : this.connectedControllers) {
            controller.addListener(this.listener);
            this.listener.connected(controller);
        }
    }

    private void update() {
        IntMap.Values<Tuple> values = this.indexToController.values();
        while (values.hasNext()) {
            Tuple tuple = values.next();
            JamepadController controller = tuple.controller;
            boolean connected = controller.update();
            if (connected) continue;
            values.remove();
        }
    }

    private class Tuple {
        public ControllerIndex index;
        public final JamepadController controller;

        public Tuple(ControllerIndex index) {
            this.index = index;
            this.controller = new JamepadController(index);
        }
    }
}

