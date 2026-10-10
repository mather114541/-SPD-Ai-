/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Cursor;
import com.watabou.utils.PointF;

public class ControllerHandler
implements ControllerListener {
    public static ControllerType lastUsedType = ControllerType.OTHER;
    public static boolean controllerActive = false;
    public static final int CONTROLLER_POINTER_ID = 19;
    private static boolean initialized = false;
    private static boolean failedInit = false;
    public static PointF leftStickPosition = new PointF();
    public static PointF rightStickPosition = new PointF();
    private float L2Trigger = 0.0f;
    private float R2Trigger = 0.0f;
    private static boolean controllerPointerActive = false;
    private static PointF controllerPointerPos;
    public static int DPAD_KEY_OFFSET;

    private static void setControllerType(Controller controller) {
        lastUsedType = controller.getName().contains("Xbox") ? ControllerType.XBOX : (controller.getName().contains("PS") ? ControllerType.PLAYSTATION : (controller.getName().contains("Nintendo") ? ControllerType.NINTENDO : ControllerType.OTHER));
    }

    public static boolean controllersSupported() {
        if (failedInit) {
            return false;
        }
        if (initialized) {
            return true;
        }
        try {
            Controllers.getCurrent();
            initialized = true;
            return true;
        }
        catch (Exception e) {
            Game.reportException(e);
            failedInit = true;
            return false;
        }
    }

    public static boolean vibrationSupported() {
        try {
            return ControllerHandler.isControllerConnected() && Controllers.getCurrent().canVibrate();
        }
        catch (Exception e) {
            return false;
        }
    }

    public static void vibrate(int millis) {
        if (ControllerHandler.vibrationSupported()) {
            Controllers.getCurrent().startVibration(millis, 1.0f);
        }
    }

    public static boolean isControllerConnected() {
        return ControllerHandler.controllersSupported() && Controllers.getCurrent() != null;
    }

    @Override
    public void connected(Controller controller) {
        controllerActive = true;
        ControllerHandler.setControllerType(controller);
    }

    @Override
    public void disconnected(Controller controller) {
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        ControllerHandler.setControllerType(controller);
        controllerActive = true;
        int keyCode = ControllerHandler.buttonToKey(controller, buttonCode);
        if (keyCode != 0) {
            KeyEvent.addKeyEvent(new KeyEvent(keyCode, true));
            return true;
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        ControllerHandler.setControllerType(controller);
        controllerActive = true;
        int keyCode = ControllerHandler.buttonToKey(controller, buttonCode);
        if (keyCode != 0) {
            KeyEvent.addKeyEvent(new KeyEvent(keyCode, false));
            return true;
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        ControllerHandler.setControllerType(controller);
        ControllerMapping mapping = controller.getMapping();
        if (mapping.axisRightX == axisCode) {
            ControllerHandler.rightStickPosition.x = value;
        } else if (mapping.axisRightY == axisCode) {
            ControllerHandler.rightStickPosition.y = value;
        } else if (mapping.axisLeftX == axisCode) {
            ControllerHandler.leftStickPosition.x = value;
        } else if (mapping.axisLeftY == axisCode) {
            ControllerHandler.leftStickPosition.y = value;
        } else if (axisCode == 4 && Gdx.app.getType() == Application.ApplicationType.Desktop && this.L2Trigger != value) {
            if (value == 1.0f) {
                KeyEvent.addKeyEvent(new KeyEvent(104, true));
                controllerActive = true;
            } else if (value == 0.0f) {
                KeyEvent.addKeyEvent(new KeyEvent(104, false));
                controllerActive = true;
            }
            this.L2Trigger = value;
        } else if (axisCode == 5 && Gdx.app.getType() == Application.ApplicationType.Desktop && this.R2Trigger != value) {
            if (value == 1.0f) {
                KeyEvent.addKeyEvent(new KeyEvent(105, true));
                controllerActive = true;
            } else if (value == 0.0f) {
                KeyEvent.addKeyEvent(new KeyEvent(105, false));
                controllerActive = true;
            }
            this.R2Trigger = value;
        }
        return true;
    }

    public static void setControllerPointer(boolean active) {
        if (active) {
            controllerActive = true;
        }
        if (controllerPointerActive == active) {
            return;
        }
        controllerPointerActive = active;
        if (active) {
            Gdx.input.setCursorCatched(true);
            controllerPointerPos = new PointF(PointerEvent.currentHoverPos());
        } else if (!Cursor.isCursorCaptured()) {
            Gdx.input.setCursorCatched(false);
            Gdx.input.setCursorPosition((int)ControllerHandler.controllerPointerPos.x, (int)ControllerHandler.controllerPointerPos.y);
        }
    }

    public static boolean controllerPointerActive() {
        return controllerPointerActive && !Cursor.isCursorCaptured();
    }

    public static PointF getControllerPointerPos() {
        return controllerPointerPos.clone();
    }

    public static void updateControllerPointer(PointF pos, boolean sendEvent) {
        controllerPointerPos.set(pos);
        if (sendEvent) {
            controllerActive = true;
            PointerEvent.addPointerEvent(new PointerEvent((int)ControllerHandler.controllerPointerPos.x, (int)ControllerHandler.controllerPointerPos.y, 10000, PointerEvent.Type.HOVER, -1));
        } else {
            PointerEvent.setHoverPos(pos);
        }
    }

    public static int buttonToKey(Controller controller, int btnCode) {
        ControllerMapping mapping = controller.getMapping();
        if (btnCode == mapping.buttonA) {
            return 96;
        }
        if (btnCode == mapping.buttonB) {
            return 97;
        }
        if (btnCode == mapping.buttonX) {
            return 99;
        }
        if (btnCode == mapping.buttonY) {
            return 100;
        }
        if (btnCode == mapping.buttonBack) {
            return 109;
        }
        if (btnCode == mapping.buttonStart) {
            return 108;
        }
        if (btnCode == mapping.buttonL1) {
            return 102;
        }
        if (btnCode == mapping.buttonL2) {
            return 104;
        }
        if (btnCode == mapping.buttonR1) {
            return 103;
        }
        if (btnCode == mapping.buttonR2) {
            return 105;
        }
        if (btnCode == mapping.buttonDpadUp) {
            return 19 + DPAD_KEY_OFFSET;
        }
        if (btnCode == mapping.buttonDpadDown) {
            return 20 + DPAD_KEY_OFFSET;
        }
        if (btnCode == mapping.buttonDpadLeft) {
            return 21 + DPAD_KEY_OFFSET;
        }
        if (btnCode == mapping.buttonDpadRight) {
            return 22 + DPAD_KEY_OFFSET;
        }
        if (btnCode == mapping.buttonLeftStick) {
            return 106;
        }
        if (btnCode == mapping.buttonRightStick) {
            return 107;
        }
        return 0;
    }

    public static boolean icControllerKey(int keyCode) {
        if (keyCode == 0) {
            return true;
        }
        if (keyCode >= 96 && keyCode <= 110) {
            return true;
        }
        return keyCode >= 19 + DPAD_KEY_OFFSET && keyCode <= 22 + DPAD_KEY_OFFSET;
    }

    public static String customButtonName(int keyCode) {
        if (lastUsedType == ControllerType.PLAYSTATION) {
            if (keyCode == 96) {
                return "Cross Button";
            }
            if (keyCode == 97) {
                return "Circle Button";
            }
            if (keyCode == 99) {
                return "Square Button";
            }
            if (keyCode == 100) {
                return "Triangle Button";
            }
        } else if (lastUsedType == ControllerType.XBOX) {
            if (keyCode == 102) {
                return "Left Bumper";
            }
            if (keyCode == 104) {
                return "Left Trigger";
            }
            if (keyCode == 103) {
                return "Right Bumper";
            }
            if (keyCode == 105) {
                return "Right Trigger";
            }
        }
        if (keyCode == 19 + DPAD_KEY_OFFSET) {
            return Input.Keys.toString(19);
        }
        if (keyCode == 20 + DPAD_KEY_OFFSET) {
            return Input.Keys.toString(20);
        }
        if (keyCode == 21 + DPAD_KEY_OFFSET) {
            return Input.Keys.toString(21);
        }
        if (keyCode == 22 + DPAD_KEY_OFFSET) {
            return Input.Keys.toString(22);
        }
        return null;
    }

    static {
        DPAD_KEY_OFFSET = 1000;
    }

    public static enum ControllerType {
        XBOX,
        PLAYSTATION,
        NINTENDO,
        OTHER;

    }
}

