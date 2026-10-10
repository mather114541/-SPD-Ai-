/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tooltip;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

public class Button
extends Component {
    public static float longClick = 0.5f;
    protected PointerArea hotArea;
    protected Tooltip hoverTip;
    protected static Button pressedButton;
    protected float pressTime;
    protected boolean clickReady;
    private Signal.Listener<KeyEvent> keyListener;

    @Override
    protected void createChildren() {
        this.hotArea = new PointerArea(0.0f, 0.0f, 0.0f, 0.0f){

            @Override
            protected void onPointerDown(PointerEvent event) {
                pressedButton = Button.this;
                Button.this.pressTime = 0.0f;
                Button.this.clickReady = true;
                Button.this.onPointerDown();
            }

            @Override
            protected void onPointerUp(PointerEvent event) {
                if (pressedButton == Button.this) {
                    pressedButton = null;
                } else {
                    Button.this.clickReady = false;
                }
                Button.this.onPointerUp();
            }

            @Override
            protected void onClick(PointerEvent event) {
                if (Button.this.clickReady) {
                    Button.this.killTooltip();
                    switch (event.button) {
                        default: {
                            Button.this.onClick();
                            break;
                        }
                        case 1: {
                            Button.this.onRightClick();
                            break;
                        }
                        case 2: {
                            Button.this.onMiddleClick();
                        }
                    }
                }
            }

            @Override
            protected void onHoverStart(PointerEvent event) {
                Object text = Button.this.hoverText();
                if (text != null) {
                    int key = 0;
                    if (Button.this.keyAction() != null) {
                        key = KeyBindings.getFirstKeyForAction(Button.this.keyAction(), ControllerHandler.controllerActive);
                    }
                    if (key == 0 && Button.this.secondaryTooltipAction() != null) {
                        key = KeyBindings.getFirstKeyForAction(Button.this.secondaryTooltipAction(), ControllerHandler.controllerActive);
                    }
                    if (key != 0) {
                        text = (String)text + " _(" + KeyBindings.getKeyName(key) + ")_";
                    }
                    Button.this.hoverTip = new Tooltip(Button.this, (String)text, 80);
                    Group parent = Button.this.parent;
                    while (parent.parent != null) {
                        parent = parent.parent;
                    }
                    parent.addToFront(Button.this.hoverTip);
                    Button.this.hoverTip.camera = this.camera();
                    Button.this.alignTooltip(Button.this.hoverTip);
                }
            }

            @Override
            protected void onHoverEnd(PointerEvent event) {
                Button.this.killTooltip();
            }
        };
        this.add(this.hotArea);
        this.keyListener = new Signal.Listener<KeyEvent>(){

            @Override
            public boolean onSignal(KeyEvent event) {
                if (Button.this.active && KeyBindings.getActionForKey(event) == Button.this.keyAction()) {
                    if (event.pressed) {
                        pressedButton = Button.this;
                        Button.this.pressTime = 0.0f;
                        Button.this.clickReady = true;
                        Button.this.onPointerDown();
                    } else {
                        Button.this.onPointerUp();
                        if (pressedButton == Button.this) {
                            pressedButton = null;
                            if (Button.this.clickReady) {
                                Button.this.onClick();
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        KeyEvent.addKeyListener(this.keyListener);
    }

    public GameAction keyAction() {
        return null;
    }

    public GameAction secondaryTooltipAction() {
        return null;
    }

    @Override
    public void update() {
        super.update();
        this.hotArea.active = this.visible;
        if (pressedButton == this) {
            float f;
            this.pressTime += Game.elapsed;
            if (f >= longClick) {
                pressedButton = null;
                if (this.onLongClick()) {
                    this.hotArea.reset();
                    this.clickReady = false;
                    this.onPointerUp();
                    if (SPDSettings.vibration()) {
                        Game.vibrate(50);
                    }
                }
            }
        }
    }

    protected void onPointerDown() {
    }

    protected void onPointerUp() {
    }

    protected void onClick() {
    }

    protected void onRightClick() {
    }

    protected void onMiddleClick() {
    }

    protected boolean onLongClick() {
        return false;
    }

    protected String hoverText() {
        return null;
    }

    private void alignTooltip(Tooltip tip) {
        tip.setPos(this.x, this.y - tip.height() - 1.0f);
        Camera cam = this.camera();
        if (tip.right() > (float)cam.width + cam.scroll.x) {
            tip.setPos(tip.left() - (tip.right() - ((float)cam.width + cam.scroll.x)), tip.top());
        }
        if (tip.top() < 0.0f) {
            tip.setPos(tip.left(), this.bottom() + 1.0f);
        }
    }

    public void killTooltip() {
        if (this.hoverTip != null) {
            this.hoverTip.killAndErase();
            this.hoverTip = null;
        }
    }

    @Override
    protected void layout() {
        this.hotArea.x = this.x;
        this.hotArea.y = this.y;
        this.hotArea.width = this.width;
        this.hotArea.height = this.height;
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        KeyEvent.removeKeyListener(this.keyListener);
        this.killTooltip();
    }

    public void givePointerPriority() {
        this.hotArea.givePointerPriority();
    }
}

