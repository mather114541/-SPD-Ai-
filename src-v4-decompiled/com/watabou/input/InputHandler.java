/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.watabou.input.ControllerHandler;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Game;
import com.watabou.utils.PointF;

public class InputHandler
extends InputAdapter {
    private InputMultiplexer multiplexer = new InputMultiplexer();

    public InputHandler(Input input) {
        input.setInputProcessor(this.multiplexer);
        this.addInputProcessor(this);
        input.setCatchKey(4, true);
        input.setCatchKey(82, true);
    }

    public void addInputProcessor(InputProcessor processor) {
        this.multiplexer.addProcessor(0, processor);
    }

    public void removeInputProcessor(InputProcessor processor) {
        this.multiplexer.removeProcessor(processor);
    }

    public void emulateTouch(int id, int button, boolean down) {
        PointF hoverPos = PointerEvent.currentHoverPos();
        if (down) {
            this.multiplexer.touchDown((int)hoverPos.x, (int)hoverPos.y, id, button);
        } else {
            this.multiplexer.touchUp((int)hoverPos.x, (int)hoverPos.y, id, button);
        }
    }

    public void emulateDrag(int id) {
        PointF hoverPos = PointerEvent.currentHoverPos();
        this.multiplexer.touchDragged((int)hoverPos.x, (int)hoverPos.y, id);
    }

    public void processAllEvents() {
        PointerEvent.processPointerEvents();
        KeyEvent.processKeyEvents();
        ScrollEvent.processScrollEvents();
    }

    @Override
    public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenX < 0 || screenX > Game.width || screenY < 0 || screenY > Game.height) {
            return true;
        }
        if (pointer != 19) {
            ControllerHandler.setControllerPointer(false);
            ControllerHandler.controllerActive = false;
        }
        if (button >= 3 && KeyBindings.isKeyBound(button + 1000)) {
            KeyEvent.addKeyEvent(new KeyEvent(button + 1000, true));
        } else if (button < 3) {
            PointerEvent.addPointerEvent(new PointerEvent(screenX, screenY, pointer, PointerEvent.Type.DOWN, button));
        }
        return true;
    }

    @Override
    public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button >= 3 && KeyBindings.isKeyBound(button + 1000)) {
            KeyEvent.addKeyEvent(new KeyEvent(button + 1000, false));
        } else if (button < 3) {
            PointerEvent.addPointerEvent(new PointerEvent(screenX, screenY, pointer, PointerEvent.Type.UP, button));
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        if (button >= 3 && KeyBindings.isKeyBound(button + 1000)) {
            KeyEvent.addKeyEvent(new KeyEvent(button + 1000, false));
        } else if (button < 3) {
            PointerEvent.addPointerEvent(new PointerEvent(screenX, screenY, pointer, PointerEvent.Type.CANCEL, button));
        }
        return true;
    }

    @Override
    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        PointerEvent.addIfExisting(new PointerEvent(screenX, screenY, pointer, PointerEvent.Type.DOWN));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (ControllerHandler.controllerPointerActive()) {
            ControllerHandler.setControllerPointer(false);
            PointF hover = ControllerHandler.getControllerPointerPos();
            screenX = (int)hover.x;
            screenY = (int)hover.y;
        }
        PointerEvent.addPointerEvent(new PointerEvent(screenX, screenY, -1, PointerEvent.Type.HOVER));
        return true;
    }

    @Override
    public synchronized boolean keyDown(int keyCode) {
        if (KeyBindings.isKeyBound(keyCode)) {
            KeyEvent.addKeyEvent(new KeyEvent(keyCode, true));
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean keyUp(int keyCode) {
        if (KeyBindings.isKeyBound(keyCode)) {
            KeyEvent.addKeyEvent(new KeyEvent(keyCode, false));
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        ScrollEvent.addScrollEvent(new ScrollEvent(PointerEvent.currentHoverPos(), amountY));
        return true;
    }
}

