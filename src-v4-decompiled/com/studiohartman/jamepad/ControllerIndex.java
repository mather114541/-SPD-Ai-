/*
 * Decompiled with CFR 0.152.
 */
package com.studiohartman.jamepad;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerPowerLevel;
import com.studiohartman.jamepad.ControllerUnpluggedException;

public final class ControllerIndex {
    private static final float AXIS_MAX_VAL = 32767.0f;
    private int index;
    private long controllerPtr;
    private boolean[] heldDownButtons;
    private boolean[] justPressedButtons;

    ControllerIndex(int index) {
        this.index = index;
        this.heldDownButtons = new boolean[ControllerButton.values().length];
        this.justPressedButtons = new boolean[ControllerButton.values().length];
        for (int i = 0; i < this.heldDownButtons.length; ++i) {
            this.heldDownButtons[i] = false;
            this.justPressedButtons[i] = false;
        }
        this.connectController();
    }

    private void connectController() {
        this.controllerPtr = this.nativeConnectController(this.index);
    }

    private native long nativeConnectController(int var1);

    public void close() {
        if (this.controllerPtr != 0L) {
            this.nativeClose(this.controllerPtr);
            this.controllerPtr = 0L;
        }
    }

    private native void nativeClose(long var1);

    public boolean reconnectController() {
        this.close();
        this.connectController();
        return this.isConnected();
    }

    public boolean isConnected() {
        return this.controllerPtr != 0L && this.nativeIsConnected(this.controllerPtr);
    }

    private native boolean nativeIsConnected(long var1);

    public int getIndex() {
        return this.index;
    }

    public boolean canVibrate() throws ControllerUnpluggedException {
        this.ensureConnected();
        return this.nativeCanVibrate(this.controllerPtr);
    }

    private native boolean nativeCanVibrate(long var1);

    private native boolean nativeDoVibration(long var1, int var3, int var4, int var5);

    public boolean doVibration(float leftMagnitude, float rightMagnitude, int duration_ms) throws ControllerUnpluggedException {
        boolean rightInRange;
        this.ensureConnected();
        boolean leftInRange = leftMagnitude >= 0.0f && leftMagnitude <= 1.0f;
        boolean bl = rightInRange = rightMagnitude >= 0.0f && rightMagnitude <= 1.0f;
        if (!leftInRange || !rightInRange) {
            throw new IllegalArgumentException("The passed values are not in the range 0 to 1!");
        }
        return this.nativeDoVibration(this.controllerPtr, (int)(65535.0f * leftMagnitude), (int)(65535.0f * rightMagnitude), duration_ms);
    }

    public boolean isButtonPressed(ControllerButton toCheck) throws ControllerUnpluggedException {
        this.updateButton(toCheck.ordinal());
        return this.heldDownButtons[toCheck.ordinal()];
    }

    public boolean isButtonJustPressed(ControllerButton toCheck) throws ControllerUnpluggedException {
        this.updateButton(toCheck.ordinal());
        return this.justPressedButtons[toCheck.ordinal()];
    }

    private void updateButton(int buttonIndex) throws ControllerUnpluggedException {
        this.ensureConnected();
        boolean currButtonIsPressed = this.nativeCheckButton(this.controllerPtr, buttonIndex);
        this.justPressedButtons[buttonIndex] = currButtonIsPressed && !this.heldDownButtons[buttonIndex];
        this.heldDownButtons[buttonIndex] = currButtonIsPressed;
    }

    private native boolean nativeCheckButton(long var1, int var3);

    public boolean isButtonAvailable(ControllerButton toCheck) throws ControllerUnpluggedException {
        this.ensureConnected();
        return this.nativeButtonAvailable(this.controllerPtr, toCheck.ordinal());
    }

    private native boolean nativeButtonAvailable(long var1, int var3);

    public float getAxisState(ControllerAxis toCheck) throws ControllerUnpluggedException {
        this.ensureConnected();
        return (float)this.nativeCheckAxis(this.controllerPtr, toCheck.ordinal()) / 32767.0f;
    }

    private native int nativeCheckAxis(long var1, int var3);

    public boolean isAxisAvailable(ControllerAxis toCheck) throws ControllerUnpluggedException {
        this.ensureConnected();
        return this.nativeAxisAvailable(this.controllerPtr, toCheck.ordinal());
    }

    private native boolean nativeAxisAvailable(long var1, int var3);

    public String getName() throws ControllerUnpluggedException {
        this.ensureConnected();
        String controllerName = this.nativeGetName(this.controllerPtr);
        if (controllerName == null) {
            return "Unnamed Controller";
        }
        return controllerName;
    }

    private native String nativeGetName(long var1);

    public int getDeviceInstanceID() throws ControllerUnpluggedException {
        this.ensureConnected();
        return this.nativeGetDeviceInstanceID(this.controllerPtr);
    }

    private native int nativeGetDeviceInstanceID(long var1);

    public int getPlayerIndex() throws ControllerUnpluggedException {
        this.ensureConnected();
        return this.nativeGetPlayerIndex(this.controllerPtr);
    }

    private native int nativeGetPlayerIndex(long var1);

    public void setPlayerIndex(int index) throws ControllerUnpluggedException {
        this.ensureConnected();
        this.nativeSetPlayerIndex(this.controllerPtr, index);
    }

    private native void nativeSetPlayerIndex(long var1, int var3);

    public ControllerPowerLevel getPowerLevel() throws ControllerUnpluggedException {
        this.ensureConnected();
        return ControllerPowerLevel.valueOf(this.nativeGetPowerLevel(this.controllerPtr));
    }

    private native int nativeGetPowerLevel(long var1);

    private void ensureConnected() throws ControllerUnpluggedException {
        if (!this.isConnected()) {
            throw new ControllerUnpluggedException("Controller at index " + this.index + " is not connected!");
        }
    }
}

