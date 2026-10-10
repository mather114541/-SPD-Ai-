/*
 * Decompiled with CFR 0.152.
 */
package com.studiohartman.jamepad;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerUnpluggedException;
import java.io.Serializable;

public final class ControllerState
implements Serializable {
    private static final ControllerState DISCONNECTED_CONTROLLER = new ControllerState();
    private static final long serialVersionUID = -3068755590868647792L;
    public final boolean isConnected;
    public final String controllerType;
    public final float leftStickX;
    public final float leftStickY;
    public final float rightStickX;
    public final float rightStickY;
    public final float leftStickAngle;
    public final float leftStickMagnitude;
    public final float rightStickAngle;
    public final float rightStickMagnitude;
    public final boolean leftStickClick;
    public final boolean rightStickClick;
    public final float leftTrigger;
    public final float rightTrigger;
    public final boolean leftStickJustClicked;
    public final boolean rightStickJustClicked;
    public final boolean a;
    public final boolean b;
    public final boolean x;
    public final boolean y;
    public final boolean lb;
    public final boolean rb;
    public final boolean start;
    public final boolean back;
    public final boolean guide;
    public final boolean dpadUp;
    public final boolean dpadDown;
    public final boolean dpadLeft;
    public final boolean dpadRight;
    public final boolean aJustPressed;
    public final boolean bJustPressed;
    public final boolean xJustPressed;
    public final boolean yJustPressed;
    public final boolean lbJustPressed;
    public final boolean rbJustPressed;
    public final boolean startJustPressed;
    public final boolean backJustPressed;
    public final boolean guideJustPressed;
    public final boolean dpadUpJustPressed;
    public final boolean dpadDownJustPressed;
    public final boolean dpadLeftJustPressed;
    public final boolean dpadRightJustPressed;
    public final boolean misc1;
    public final boolean misc1JustPressed;
    public final boolean paddle1;
    public final boolean paddle1JustPressed;
    public final boolean paddle2;
    public final boolean paddle2JustPressed;
    public final boolean paddle3;
    public final boolean paddle3JustPressed;
    public final boolean paddle4;
    public final boolean paddle4JustPressed;
    public final boolean touchpadButton;
    public final boolean touchpadButtonJustPressed;

    static ControllerState getInstanceFromController(ControllerIndex c) {
        try {
            return new ControllerState(c);
        }
        catch (ControllerUnpluggedException e) {
            return DISCONNECTED_CONTROLLER;
        }
    }

    static ControllerState getDisconnectedControllerInstance() {
        return DISCONNECTED_CONTROLLER;
    }

    private ControllerState(ControllerIndex c) throws ControllerUnpluggedException {
        this.isConnected = true;
        this.controllerType = c.getName();
        this.leftStickX = c.getAxisState(ControllerAxis.LEFTX);
        this.leftStickY = c.getAxisState(ControllerAxis.LEFTY);
        this.rightStickX = c.getAxisState(ControllerAxis.RIGHTX);
        this.rightStickY = c.getAxisState(ControllerAxis.RIGHTY);
        this.leftStickAngle = (float)Math.toDegrees(Math.atan2(this.leftStickY, this.leftStickX));
        this.leftStickMagnitude = (float)Math.sqrt(this.leftStickX * this.leftStickX + this.leftStickY * this.leftStickY);
        this.rightStickAngle = (float)Math.toDegrees(Math.atan2(this.rightStickY, this.rightStickX));
        this.rightStickMagnitude = (float)Math.sqrt(this.rightStickX * this.rightStickX + this.rightStickY * this.rightStickY);
        this.leftTrigger = c.getAxisState(ControllerAxis.TRIGGERLEFT);
        this.rightTrigger = c.getAxisState(ControllerAxis.TRIGGERRIGHT);
        this.leftStickJustClicked = c.isButtonJustPressed(ControllerButton.LEFTSTICK);
        this.rightStickJustClicked = c.isButtonJustPressed(ControllerButton.RIGHTSTICK);
        this.leftStickClick = c.isButtonPressed(ControllerButton.LEFTSTICK);
        this.rightStickClick = c.isButtonPressed(ControllerButton.RIGHTSTICK);
        this.aJustPressed = c.isButtonJustPressed(ControllerButton.A);
        this.bJustPressed = c.isButtonJustPressed(ControllerButton.B);
        this.xJustPressed = c.isButtonJustPressed(ControllerButton.X);
        this.yJustPressed = c.isButtonJustPressed(ControllerButton.Y);
        this.lbJustPressed = c.isButtonJustPressed(ControllerButton.LEFTBUMPER);
        this.rbJustPressed = c.isButtonJustPressed(ControllerButton.RIGHTBUMPER);
        this.startJustPressed = c.isButtonJustPressed(ControllerButton.START);
        this.backJustPressed = c.isButtonJustPressed(ControllerButton.BACK);
        this.guideJustPressed = c.isButtonJustPressed(ControllerButton.GUIDE);
        this.dpadUpJustPressed = c.isButtonJustPressed(ControllerButton.DPAD_UP);
        this.dpadDownJustPressed = c.isButtonJustPressed(ControllerButton.DPAD_DOWN);
        this.dpadLeftJustPressed = c.isButtonJustPressed(ControllerButton.DPAD_LEFT);
        this.dpadRightJustPressed = c.isButtonJustPressed(ControllerButton.DPAD_RIGHT);
        this.misc1JustPressed = c.isButtonJustPressed(ControllerButton.BUTTON_MISC1);
        this.paddle1JustPressed = c.isButtonJustPressed(ControllerButton.BUTTON_PADDLE1);
        this.paddle2JustPressed = c.isButtonJustPressed(ControllerButton.BUTTON_PADDLE2);
        this.paddle3JustPressed = c.isButtonJustPressed(ControllerButton.BUTTON_PADDLE3);
        this.paddle4JustPressed = c.isButtonJustPressed(ControllerButton.BUTTON_PADDLE4);
        this.touchpadButtonJustPressed = c.isButtonJustPressed(ControllerButton.BUTTON_TOUCHPAD);
        this.a = c.isButtonPressed(ControllerButton.A);
        this.b = c.isButtonPressed(ControllerButton.B);
        this.x = c.isButtonPressed(ControllerButton.X);
        this.y = c.isButtonPressed(ControllerButton.Y);
        this.lb = c.isButtonPressed(ControllerButton.LEFTBUMPER);
        this.rb = c.isButtonPressed(ControllerButton.RIGHTBUMPER);
        this.start = c.isButtonPressed(ControllerButton.START);
        this.back = c.isButtonPressed(ControllerButton.BACK);
        this.guide = c.isButtonPressed(ControllerButton.GUIDE);
        this.dpadUp = c.isButtonPressed(ControllerButton.DPAD_UP);
        this.dpadDown = c.isButtonPressed(ControllerButton.DPAD_DOWN);
        this.dpadLeft = c.isButtonPressed(ControllerButton.DPAD_LEFT);
        this.dpadRight = c.isButtonPressed(ControllerButton.DPAD_RIGHT);
        this.misc1 = c.isButtonPressed(ControllerButton.BUTTON_MISC1);
        this.paddle1 = c.isButtonPressed(ControllerButton.BUTTON_PADDLE1);
        this.paddle2 = c.isButtonPressed(ControllerButton.BUTTON_PADDLE2);
        this.paddle3 = c.isButtonPressed(ControllerButton.BUTTON_PADDLE3);
        this.paddle4 = c.isButtonPressed(ControllerButton.BUTTON_PADDLE4);
        this.touchpadButton = c.isButtonPressed(ControllerButton.BUTTON_TOUCHPAD);
    }

    private ControllerState() {
        this.isConnected = false;
        this.controllerType = "Not Connected";
        this.leftStickX = 0.0f;
        this.leftStickY = 0.0f;
        this.rightStickX = 0.0f;
        this.rightStickY = 0.0f;
        this.leftStickAngle = 0.0f;
        this.leftStickMagnitude = 0.0f;
        this.rightStickAngle = 0.0f;
        this.rightStickMagnitude = 0.0f;
        this.leftTrigger = 0.0f;
        this.rightTrigger = 0.0f;
        this.leftStickJustClicked = false;
        this.rightStickJustClicked = false;
        this.leftStickClick = false;
        this.rightStickClick = false;
        this.aJustPressed = false;
        this.bJustPressed = false;
        this.xJustPressed = false;
        this.yJustPressed = false;
        this.lbJustPressed = false;
        this.rbJustPressed = false;
        this.startJustPressed = false;
        this.backJustPressed = false;
        this.guideJustPressed = false;
        this.dpadUpJustPressed = false;
        this.dpadDownJustPressed = false;
        this.dpadLeftJustPressed = false;
        this.dpadRightJustPressed = false;
        this.misc1JustPressed = false;
        this.paddle1JustPressed = false;
        this.paddle2JustPressed = false;
        this.paddle3JustPressed = false;
        this.paddle4JustPressed = false;
        this.touchpadButtonJustPressed = false;
        this.a = false;
        this.b = false;
        this.x = false;
        this.y = false;
        this.lb = false;
        this.rb = false;
        this.start = false;
        this.back = false;
        this.guide = false;
        this.dpadUp = false;
        this.dpadDown = false;
        this.dpadLeft = false;
        this.dpadRight = false;
        this.misc1 = false;
        this.paddle1 = false;
        this.paddle2 = false;
        this.paddle3 = false;
        this.paddle4 = false;
        this.touchpadButton = false;
    }
}

