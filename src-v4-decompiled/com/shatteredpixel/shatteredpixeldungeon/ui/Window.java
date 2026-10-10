/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShadowBox;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.utils.Point;
import com.watabou.utils.RectF;
import com.watabou.utils.Signal;

public class Window
extends Group
implements Signal.Listener<KeyEvent> {
    protected int width;
    protected int height;
    protected int xOffset;
    protected int yOffset;
    protected PointerArea blocker;
    protected ShadowBox shadow;
    protected NinePatch chrome;
    public static final int WHITE = 0xFFFFFF;
    public static final int TITLE_COLOR = 0xFFFF44;
    public static final int SHPX_COLOR = 0x33BB33;

    public Window() {
        this(0, 0, Chrome.get(Chrome.Type.WINDOW));
    }

    public Window(int width, int height) {
        this(width, height, Chrome.get(Chrome.Type.WINDOW));
    }

    public Window(int width, int height, NinePatch chrome) {
        this.blocker = new PointerArea(0.0f, 0.0f, PixelScene.uiCamera.width, PixelScene.uiCamera.height){

            @Override
            protected void onClick(PointerEvent event) {
                if (Window.this.parent != null && !Window.this.chrome.overlapsScreenPoint((int)event.current.x, (int)event.current.y)) {
                    Window.this.onBackPressed();
                }
            }
        };
        this.blocker.camera = PixelScene.uiCamera;
        this.add(this.blocker);
        this.chrome = chrome;
        this.width = width;
        this.height = height;
        this.shadow = new ShadowBox();
        this.shadow.am = 0.5f;
        this.shadow.camera = PixelScene.uiCamera.visible ? PixelScene.uiCamera : Camera.main;
        this.add(this.shadow);
        chrome.x = -chrome.marginLeft();
        chrome.y = -chrome.marginTop();
        chrome.size((float)width - chrome.x + (float)chrome.marginRight(), (float)height - chrome.y + (float)chrome.marginBottom());
        this.add(chrome);
        RectF insets = Game.platform.getSafeInsets(1);
        int screenW = (int)((float)Game.width - insets.left - insets.right);
        int screenH = (int)((float)Game.height - insets.top - insets.bottom);
        this.camera = new Camera(0, 0, (int)chrome.width, (int)chrome.height, PixelScene.defaultZoom);
        this.camera.x = (int)(insets.left + ((float)screenW - (float)this.camera.width * this.camera.zoom) / 2.0f);
        this.camera.y = (int)(insets.top + ((float)screenH - (float)this.camera.height * this.camera.zoom) / 2.0f);
        this.camera.y = (int)((float)this.camera.y - (float)this.yOffset * this.camera.zoom);
        this.camera.scroll.set(chrome.x, chrome.y);
        Camera.add(this.camera);
        this.shadow.boxRect((float)this.camera.x / this.camera.zoom, (float)this.camera.y / this.camera.zoom, chrome.width(), chrome.height);
        KeyEvent.addKeyListener(this);
    }

    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
        this.chrome.size(this.width + this.chrome.marginHor(), this.height + this.chrome.marginVer());
        this.camera.resize((int)this.chrome.width, (int)this.chrome.height);
        RectF insets = Game.platform.getSafeInsets(1);
        int screenW = (int)((float)Game.width - insets.left - insets.right);
        int screenH = (int)((float)Game.height - insets.top - insets.bottom);
        this.camera.x = (int)((float)screenW - this.camera.screenWidth()) / 2;
        this.camera.x = (int)((float)this.camera.x + insets.left);
        this.camera.x = (int)((float)this.camera.x + (float)this.xOffset * this.camera.zoom);
        this.camera.y = (int)((float)screenH - this.camera.screenHeight()) / 2;
        this.camera.y = (int)((float)this.camera.y + insets.top);
        this.camera.y = (int)((float)this.camera.y + (float)this.yOffset * this.camera.zoom);
        this.shadow.boxRect((float)this.camera.x / this.camera.zoom, (float)this.camera.y / this.camera.zoom, this.chrome.width(), this.chrome.height);
    }

    public Point getOffset() {
        return new Point(this.xOffset, this.yOffset);
    }

    public final void offset(Point offset) {
        this.offset(offset.x, offset.y);
    }

    public void offset(int xOffset, int yOffset) {
        this.camera.x = (int)((float)this.camera.x - (float)this.xOffset * this.camera.zoom);
        this.xOffset = xOffset;
        this.camera.x = (int)((float)this.camera.x + (float)xOffset * this.camera.zoom);
        this.camera.y = (int)((float)this.camera.y - (float)this.yOffset * this.camera.zoom);
        this.yOffset = yOffset;
        this.camera.y = (int)((float)this.camera.y + (float)yOffset * this.camera.zoom);
        this.shadow.boxRect((float)this.camera.x / this.camera.zoom, (float)this.camera.y / this.camera.zoom, this.chrome.width(), this.chrome.height);
    }

    public void boundOffsetWithMargin(int margin) {
        int newYOfs;
        float x = (float)this.camera.x / this.camera.zoom;
        float y = (float)this.camera.y / this.camera.zoom;
        Camera sceneCam = PixelScene.uiCamera.visible ? PixelScene.uiCamera : Camera.main;
        int newXOfs = this.xOffset;
        if (newXOfs != 0) {
            if (x < (float)margin) {
                newXOfs = (int)((float)newXOfs + ((float)margin - x));
            } else if (x + (float)this.camera.width > (float)(sceneCam.width - margin)) {
                newXOfs = (int)((float)newXOfs + ((float)(sceneCam.width - margin) - (x + (float)this.camera.width)));
            }
        }
        if ((newYOfs = this.yOffset) != 0) {
            if (y < (float)margin) {
                newYOfs = (int)((float)newYOfs + ((float)margin - y));
            } else if (y + (float)this.camera.height > (float)(sceneCam.height - margin)) {
                newYOfs = (int)((float)newYOfs + ((float)(sceneCam.height - margin) - (y + (float)this.camera.height)));
            }
        }
        this.offset(newXOfs, newYOfs);
    }

    public void hide() {
        if (this.parent != null) {
            this.parent.erase(this);
        }
        this.destroy();
    }

    @Override
    public void destroy() {
        super.destroy();
        Camera.remove(this.camera);
        KeyEvent.removeKeyListener(this);
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed && (KeyBindings.getActionForKey(event) == SPDAction.BACK || KeyBindings.getActionForKey(event) == SPDAction.WAIT)) {
            this.onBackPressed();
        }
        return true;
    }

    public void onBackPressed() {
        this.hide();
    }
}

