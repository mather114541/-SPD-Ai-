/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.AbstractControllerManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.desktop.support.CompositeControllerListener;
import com.badlogic.gdx.controllers.desktop.support.JamepadControllerMonitor;
import com.badlogic.gdx.controllers.desktop.support.JamepadShutdownHook;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.studiohartman.jamepad.Configuration;
import com.studiohartman.jamepad.ControllerManager;
import java.io.IOException;

public class JamepadControllerManager
extends AbstractControllerManager
implements Disposable {
    public static Configuration jamepadConfiguration;
    private static boolean nativeLibInitialized;
    private static ControllerManager controllerManager;
    private final CompositeControllerListener compositeListener = new CompositeControllerListener();

    public JamepadControllerManager() {
        this.compositeListener.addListener(new ManageControllers());
        if (!nativeLibInitialized) {
            if (jamepadConfiguration == null) {
                jamepadConfiguration = new Configuration();
            }
            controllerManager = new ControllerManager(jamepadConfiguration);
            controllerManager.initSDLGamepad();
            JamepadControllerMonitor monitor = new JamepadControllerMonitor(controllerManager, this.compositeListener);
            monitor.run();
            Gdx.app.addLifecycleListener(new JamepadShutdownHook(controllerManager));
            nativeLibInitialized = true;
        }
    }

    @Override
    public void addListener(ControllerListener listener) {
        this.compositeListener.addListener(listener);
    }

    @Override
    public void removeListener(ControllerListener listener) {
        this.compositeListener.removeListener(listener);
    }

    @Override
    public Array<ControllerListener> getListeners() {
        Array<ControllerListener> array = new Array<ControllerListener>();
        array.add(this.compositeListener);
        return array;
    }

    @Override
    public void clearListeners() {
        this.compositeListener.clear();
        this.compositeListener.addListener(new ManageControllers());
    }

    @Override
    public void dispose() {
        controllerManager.quitSDLGamepad();
    }

    public static void addMappingsFromFile(String path) throws IOException, IllegalStateException {
        controllerManager.addMappingsFromFile(path);
    }

    public static void logLastNativeGamepadError() {
        Gdx.app.error("Jamepad", controllerManager.getLastNativeError());
    }

    static {
        nativeLibInitialized = false;
    }

    private class ManageControllers
    extends AbstractControllerManager.ManageCurrentControllerListener {
        private ManageControllers() {
            super(JamepadControllerManager.this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void connected(Controller controller) {
            Array array = JamepadControllerManager.this.controllers;
            synchronized (array) {
                JamepadControllerManager.this.controllers.add(controller);
            }
            super.connected(controller);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void disconnected(Controller controller) {
            Array array = JamepadControllerManager.this.controllers;
            synchronized (array) {
                JamepadControllerManager.this.controllers.removeValue(controller, true);
            }
            super.disconnected(controller);
        }
    }
}

