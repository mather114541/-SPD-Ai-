/*
 * Decompiled with CFR 0.152.
 */
package com.studiohartman.jamepad;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.studiohartman.jamepad.Configuration;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerState;
import com.studiohartman.jamepad.ControllerUnpluggedException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

public class ControllerManager {
    private final Configuration configuration;
    private String mappingsPath;
    private boolean isInitialized;
    private ControllerIndex[] controllers;

    public ControllerManager() {
        this(new Configuration(), "/gamecontrollerdb.txt");
    }

    public ControllerManager(Configuration configuration) {
        this(configuration, "/gamecontrollerdb.txt");
    }

    public ControllerManager(Configuration configuration, String mappingsPath) {
        this.configuration = configuration;
        this.mappingsPath = mappingsPath;
        this.isInitialized = false;
        this.controllers = new ControllerIndex[configuration.maxNumControllers];
        if (configuration.loadNativeLibrary) {
            new SharedLibraryLoader().load("jamepad");
        }
    }

    public void initSDLGamepad() throws IllegalStateException {
        if (this.isInitialized) {
            throw new IllegalStateException("SDL is already initialized!");
        }
        if (!this.nativeInitSDLGamepad(!this.configuration.useRawInput)) {
            throw new IllegalStateException("Failed to initialize SDL in native method!");
        }
        this.isInitialized = true;
        try {
            this.addMappingsFromFile(this.mappingsPath);
        }
        catch (IOException | IllegalStateException e) {
            System.err.println("Failed to load mapping with original location \"" + this.mappingsPath + "\", Falling back of SDL's built in mappings");
            e.printStackTrace();
        }
        for (int i = 0; i < this.controllers.length; ++i) {
            this.controllers[i] = new ControllerIndex(i);
        }
    }

    private native boolean nativeInitSDLGamepad(boolean var1);

    public void quitSDLGamepad() {
        for (ControllerIndex c : this.controllers) {
            c.close();
        }
        this.nativeCloseSDLGamepad();
        this.controllers = new ControllerIndex[0];
        this.isInitialized = false;
    }

    private native void nativeCloseSDLGamepad();

    public ControllerState getState(int index) throws IllegalStateException {
        this.verifyInitialized();
        if (index < this.controllers.length && index >= 0) {
            this.update();
            return ControllerState.getInstanceFromController(this.controllers[index]);
        }
        return ControllerState.getDisconnectedControllerInstance();
    }

    public boolean doVibration(int index, float leftMagnitude, float rightMagnitude, int duration_ms) throws IllegalStateException {
        this.verifyInitialized();
        if (index < this.controllers.length && index >= 0) {
            try {
                return this.controllers[index].doVibration(leftMagnitude, rightMagnitude, duration_ms);
            }
            catch (ControllerUnpluggedException e) {
                return false;
            }
        }
        return false;
    }

    public ControllerIndex getControllerIndex(int index) {
        this.verifyInitialized();
        return this.controllers[index];
    }

    public int getNumControllers() {
        this.verifyInitialized();
        return this.nativeGetNumRollers();
    }

    private native int nativeGetNumRollers();

    public boolean update() {
        this.verifyInitialized();
        if (this.nativeControllerConnectedOrDisconnected()) {
            for (int i = 0; i < this.controllers.length; ++i) {
                this.controllers[i].reconnectController();
            }
            return true;
        }
        return false;
    }

    private native boolean nativeControllerConnectedOrDisconnected();

    public void addMappingsFromFile(String path) throws IOException, IllegalStateException {
        InputStream source = this.getClass().getResourceAsStream(path);
        if (source == null) {
            source = ClassLoader.getSystemResourceAsStream(path);
        }
        if (source == null) {
            throw new IOException("Cannot open resource from classpath " + path);
        }
        if (this.configuration.loadDatabaseInMemory) {
            int read;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            while ((read = source.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
            byte[] b = buffer.toByteArray();
            if (!this.nativeAddMappingsFromBuffer(b, b.length)) {
                throw new IllegalStateException("Failed to set SDL controller mappings! Falling back to build in SDL mappings.");
            }
        } else {
            Path extractedLoc = Files.createTempFile(null, null, new FileAttribute[0]).toAbsolutePath();
            Files.copy(source, extractedLoc, StandardCopyOption.REPLACE_EXISTING);
            if (!this.nativeAddMappingsFromFile(extractedLoc.toString())) {
                throw new IllegalStateException("Failed to set SDL controller mappings! Falling back to build in SDL mappings.");
            }
            Files.delete(extractedLoc);
        }
    }

    private native boolean nativeAddMappingsFromFile(String var1);

    private native boolean nativeAddMappingsFromBuffer(byte[] var1, int var2);

    public native String getLastNativeError();

    private boolean verifyInitialized() throws IllegalStateException {
        if (!this.isInitialized) {
            throw new IllegalStateException("SDL_GameController is not initialized!");
        }
        return true;
    }
}

