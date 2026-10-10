/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.controllers.desktop.support;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.ControllerPowerLevel;
import com.badlogic.gdx.controllers.desktop.support.CompositeControllerListener;
import com.badlogic.gdx.controllers.desktop.support.JamepadMapping;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerUnpluggedException;
import java.util.UUID;

public class JamepadController
implements Controller {
    private static final IntMap<ControllerButton> CODE_TO_BUTTON = new IntMap(ControllerButton.values().length);
    private static final IntMap<ControllerAxis> CODE_TO_AXIS = new IntMap(ControllerAxis.values().length);
    private static final Logger logger = new Logger(JamepadController.class.getSimpleName());
    private static final ControllerButton[] CONTROLLER_BUTTON_VALUES = ControllerButton.values();
    private static final ControllerAxis[] CONTROLLER_AXIS_VALUES = ControllerAxis.values();
    private final CompositeControllerListener compositeControllerListener = new CompositeControllerListener();
    private final IntMap<Boolean> buttonState = new IntMap();
    private final IntFloatMap axisState = new IntFloatMap();
    private final String uuid;
    private final String name;
    private ControllerIndex controllerIndex;
    private boolean connected = true;
    private Boolean canVibrate = null;
    private long vibrationEndMs;
    private int axisCount = -1;
    private int maxButtonIndex = -1;

    public JamepadController(ControllerIndex controllerIndex) {
        this.controllerIndex = controllerIndex;
        this.uuid = UUID.randomUUID().toString();
        this.name = this.getInitialName();
        this.initializeState();
    }

    @Override
    public boolean getButton(int buttonCode) {
        try {
            ControllerButton button = this.toButton(buttonCode);
            return button != null && this.controllerIndex.isButtonPressed(button);
        }
        catch (ControllerUnpluggedException | NullPointerException e) {
            this.setDisconnected();
            return false;
        }
    }

    @Override
    public float getAxis(int axisCode) {
        try {
            ControllerAxis axis = this.toAxis(axisCode);
            if (axis == null) {
                return 0.0f;
            }
            return this.controllerIndex.getAxisState(axis);
        }
        catch (ControllerUnpluggedException | NullPointerException e) {
            this.setDisconnected();
            return 0.0f;
        }
    }

    private String getInitialName() {
        try {
            return this.controllerIndex.getName();
        }
        catch (ControllerUnpluggedException | NullPointerException exception) {
            return "Unknown";
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setControllerIndex(ControllerIndex controllerIndex) {
        this.controllerIndex = controllerIndex;
    }

    public void setDisconnected() {
        if (this.connected) {
            this.connected = false;
            if (this.controllerIndex != null) {
                logger.info("Failed querying controller at index: " + this.controllerIndex.getIndex());
            }
            this.compositeControllerListener.disconnected(this);
        }
    }

    @Override
    public void addListener(ControllerListener listener) {
        this.compositeControllerListener.addListener(listener);
    }

    @Override
    public void removeListener(ControllerListener listener) {
        this.compositeControllerListener.removeListener(listener);
    }

    public boolean update() {
        this.updateButtonsState();
        this.updateAxisState();
        return this.connected;
    }

    private ControllerButton toButton(int buttonCode) {
        return CODE_TO_BUTTON.get(buttonCode);
    }

    private ControllerAxis toAxis(int axisCode) {
        return CODE_TO_AXIS.get(axisCode);
    }

    private void updateAxisState() {
        for (ControllerAxis axis : CONTROLLER_AXIS_VALUES) {
            int id = axis.ordinal();
            float value = this.getAxis(id);
            if (value != this.axisState.get(id, 0.0f)) {
                if (logger.getLevel() == 3) {
                    logger.debug("Axis [" + id + " - " + (Object)((Object)this.toAxis(id)) + "] moved [" + value + "]");
                }
                this.compositeControllerListener.axisMoved(this, id, value);
            }
            this.axisState.put(id, value);
        }
    }

    private void updateButtonsState() {
        for (ControllerButton button : CONTROLLER_BUTTON_VALUES) {
            int id = button.ordinal();
            boolean pressed = this.getButton(id);
            if (pressed != this.buttonState.get(id)) {
                if (pressed) {
                    this.compositeControllerListener.buttonDown(this, id);
                } else {
                    this.compositeControllerListener.buttonUp(this, id);
                }
                if (logger.getLevel() == 3) {
                    logger.debug("Button [" + id + " - " + (Object)((Object)this.toButton(id)) + "] is " + (pressed ? "pressed" : "released"));
                }
            }
            this.buttonState.put(id, pressed);
        }
    }

    private void initializeState() {
        for (ControllerAxis controllerAxis : CONTROLLER_AXIS_VALUES) {
            this.axisState.put(controllerAxis.ordinal(), 0.0f);
        }
        for (Enum enum_ : CONTROLLER_BUTTON_VALUES) {
            this.buttonState.put(enum_.ordinal(), false);
        }
    }

    @Override
    public boolean canVibrate() {
        if (this.canVibrate == null) {
            try {
                this.canVibrate = this.controllerIndex.canVibrate();
            }
            catch (ControllerUnpluggedException | NullPointerException e) {
                this.setDisconnected();
                return false;
            }
        }
        return this.canVibrate;
    }

    @Override
    public boolean isVibrating() {
        return this.canVibrate() && TimeUtils.millis() < this.vibrationEndMs;
    }

    @Override
    public void startVibration(int duration, float strength) {
        try {
            if (this.controllerIndex.doVibration(strength, strength, duration)) {
                this.vibrationEndMs = TimeUtils.millis() + (long)duration;
                this.canVibrate = true;
            }
        }
        catch (ControllerUnpluggedException | NullPointerException e) {
            this.setDisconnected();
        }
    }

    @Override
    public void cancelVibration() {
        if (this.isVibrating()) {
            this.startVibration(0, 0.0f);
        }
    }

    @Override
    public String getUniqueId() {
        return this.uuid;
    }

    @Override
    public boolean supportsPlayerIndex() {
        return true;
    }

    @Override
    public int getPlayerIndex() {
        try {
            return this.controllerIndex.getPlayerIndex();
        }
        catch (ControllerUnpluggedException | NullPointerException e) {
            this.setDisconnected();
            return -1;
        }
    }

    @Override
    public void setPlayerIndex(int index) {
        try {
            this.controllerIndex.setPlayerIndex(index);
        }
        catch (ControllerUnpluggedException | NullPointerException e) {
            this.setDisconnected();
        }
    }

    @Override
    public int getMinButtonIndex() {
        return 0;
    }

    @Override
    public int getMaxButtonIndex() {
        if (this.maxButtonIndex >= 0) {
            return this.maxButtonIndex;
        }
        this.maxButtonIndex = JamepadController.CODE_TO_BUTTON.size - 1;
        try {
            while (this.maxButtonIndex > 0 && !this.controllerIndex.isButtonAvailable(CODE_TO_BUTTON.get(this.maxButtonIndex))) {
                --this.maxButtonIndex;
            }
        }
        catch (ControllerUnpluggedException | NullPointerException e) {
            this.setDisconnected();
        }
        return this.maxButtonIndex;
    }

    @Override
    public int getAxisCount() {
        if (this.axisCount >= 0) {
            return this.axisCount;
        }
        this.axisCount = JamepadController.CODE_TO_AXIS.size;
        try {
            while (this.axisCount > 0 && !this.controllerIndex.isAxisAvailable(CODE_TO_AXIS.get(this.axisCount - 1))) {
                --this.axisCount;
            }
        }
        catch (ControllerUnpluggedException | NullPointerException e) {
            this.setDisconnected();
        }
        return this.axisCount;
    }

    @Override
    public boolean isConnected() {
        return this.connected && this.controllerIndex.isConnected();
    }

    @Override
    public ControllerMapping getMapping() {
        return JamepadMapping.getInstance();
    }

    @Override
    public ControllerPowerLevel getPowerLevel() {
        try {
            switch (this.controllerIndex.getPowerLevel()) {
                case POWER_MAX: 
                case POWER_FULL: {
                    return ControllerPowerLevel.POWER_FULL;
                }
                case POWER_MEDIUM: {
                    return ControllerPowerLevel.POWER_MEDIUM;
                }
                case POWER_LOW: {
                    return ControllerPowerLevel.POWER_LOW;
                }
                case POWER_EMPTY: {
                    return ControllerPowerLevel.POWER_EMPTY;
                }
                case POWER_WIRED: {
                    return ControllerPowerLevel.POWER_WIRED;
                }
            }
            return ControllerPowerLevel.POWER_UNKNOWN;
        }
        catch (Throwable t) {
            return ControllerPowerLevel.POWER_UNKNOWN;
        }
    }

    static {
        for (ControllerButton controllerButton : CONTROLLER_BUTTON_VALUES) {
            CODE_TO_BUTTON.put(controllerButton.ordinal(), controllerButton);
        }
        for (Enum enum_ : CONTROLLER_AXIS_VALUES) {
            CODE_TO_AXIS.put(enum_.ordinal(), (ControllerAxis)enum_);
        }
    }
}

