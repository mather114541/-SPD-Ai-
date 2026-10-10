/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.utils.Signal;

public class Scene
extends Group {
    private Signal.Listener<KeyEvent> keyListener;

    public void create() {
        this.keyListener = new Signal.Listener<KeyEvent>(){

            @Override
            public boolean onSignal(KeyEvent event) {
                if (Game.instance != null && event.pressed && KeyBindings.getActionForKey(event) == GameAction.BACK) {
                    Scene.this.onBackPressed();
                }
                return false;
            }
        };
        KeyEvent.addKeyListener(this.keyListener);
    }

    @Override
    public void destroy() {
        KeyEvent.removeKeyListener(this.keyListener);
        super.destroy();
    }

    public void onPause() {
    }

    public void onResume() {
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public Camera camera() {
        return Camera.main;
    }

    protected void onBackPressed() {
        Game.instance.finish();
    }
}

