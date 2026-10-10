/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.ScrollArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;

public class ScrollPane
extends Component {
    protected static final int THUMB_COLOR = -8683405;
    protected static final float THUMB_ALPHA = 0.5f;
    protected PointerController controller;
    protected Signal.Listener<KeyEvent> keyListener;
    protected Component content;
    protected ColorBlock thumb;
    private float keyScroll = 0.0f;

    public ScrollPane(Component content) {
        this.content = content;
        this.addToBack(content);
        this.width = content.width();
        this.height = content.height();
        content.camera = new Camera(0, 0, 1, 1, PixelScene.defaultZoom);
        Camera.add(content.camera);
        this.keyListener = new Signal.Listener<KeyEvent>(){

            @Override
            public boolean onSignal(KeyEvent keyEvent) {
                GameAction action = KeyBindings.getActionForKey(keyEvent);
                if (action == SPDAction.ZOOM_IN) {
                    ScrollPane.this.keyScroll = keyEvent.pressed ? (ScrollPane.this.keyScroll += 1.0f) : (ScrollPane.this.keyScroll -= 1.0f);
                    ScrollPane.this.keyScroll = GameMath.gate(-1.0f, ScrollPane.this.keyScroll, 1.0f);
                    return true;
                }
                if (action == SPDAction.ZOOM_OUT) {
                    ScrollPane.this.keyScroll = keyEvent.pressed ? (ScrollPane.this.keyScroll -= 1.0f) : (ScrollPane.this.keyScroll += 1.0f);
                    ScrollPane.this.keyScroll = GameMath.gate(-1.0f, ScrollPane.this.keyScroll, 1.0f);
                    return true;
                }
                return false;
            }
        };
        KeyEvent.addKeyListener(this.keyListener);
    }

    @Override
    public void destroy() {
        super.destroy();
        Camera.remove(this.content.camera);
        KeyEvent.removeKeyListener(this.keyListener);
    }

    public void scrollTo(float x, float y) {
        Camera c = this.content.camera;
        c.scroll.set(x, y);
        if (c.scroll.x + this.width > this.content.width()) {
            c.scroll.x = this.content.width() - this.width;
        }
        if (c.scroll.x < 0.0f) {
            c.scroll.x = 0.0f;
        }
        if (c.scroll.y + this.height > this.content.height()) {
            c.scroll.y = this.content.height() - this.height;
        }
        if (c.scroll.y < 0.0f) {
            c.scroll.y = 0.0f;
        }
        this.thumb.y = this.y + this.height * c.scroll.y / this.content.height();
    }

    @Override
    public synchronized void update() {
        super.update();
        if (this.keyScroll != 0.0f) {
            this.scrollTo(this.content.camera.scroll.x, this.content.camera.scroll.y + this.keyScroll * 150.0f * Game.elapsed);
        }
    }

    @Override
    protected void createChildren() {
        this.controller = new PointerController();
        this.add(this.controller);
        this.thumb = new ColorBlock(1.0f, 1.0f, -8683405);
        this.thumb.am = 0.5f;
        this.add(this.thumb);
    }

    @Override
    protected void layout() {
        this.content.setPos(0.0f, 0.0f);
        this.controller.x = this.x;
        this.controller.y = this.y;
        this.controller.width = this.width;
        this.controller.height = this.height;
        Point p = this.camera().cameraToScreen(this.x, this.y);
        Camera cs = this.content.camera;
        cs.x = p.x;
        cs.y = p.y;
        cs.resize((int)this.width, (int)this.height);
        boolean bl = this.thumb.visible = this.height < this.content.height();
        if (this.thumb.visible) {
            this.thumb.scale.set(2.0f, this.height * this.height / this.content.height());
            this.thumb.x = this.right() - this.thumb.width();
            this.thumb.y = this.y + this.height * this.content.camera.scroll.y / this.content.height();
        }
    }

    public Component content() {
        return this.content;
    }

    public void onClick(float x, float y) {
    }

    public class PointerController
    extends ScrollArea {
        private float dragThreshold;
        private boolean dragging;
        private PointF lastPos;

        public PointerController() {
            super(0.0f, 0.0f, 0.0f, 0.0f);
            this.dragging = false;
            this.lastPos = new PointF();
            this.dragThreshold = PixelScene.defaultZoom * 8;
        }

        @Override
        protected void onScroll(ScrollEvent event) {
            PointF newPt = new PointF(this.lastPos);
            newPt.y -= event.amount * ScrollPane.this.content.camera.zoom * 10.0f;
            this.scroll(newPt);
            this.dragging = false;
        }

        @Override
        protected void onPointerUp(PointerEvent event) {
            if (event == this.curEvent && this.dragging) {
                this.dragging = false;
                ScrollPane.this.thumb.am = 0.5f;
                this.curEvent = null;
            }
        }

        @Override
        protected void onClick(PointerEvent event) {
            PointF p = ScrollPane.this.content.camera.screenToCamera((int)event.current.x, (int)event.current.y);
            ScrollPane.this.onClick(p.x, p.y);
        }

        @Override
        protected void onDrag(PointerEvent event) {
            if (this.dragging) {
                this.scroll(event.current);
            } else if (PointF.distance(event.current, event.start) > this.dragThreshold) {
                this.dragging = true;
                this.lastPos.set(event.current);
                ScrollPane.this.thumb.am = 1.0f;
            }
        }

        private void scroll(PointF current) {
            Camera c = ScrollPane.this.content.camera;
            c.shift(PointF.diff(this.lastPos, current).invScale(c.zoom));
            if (c.scroll.x + this.width > ScrollPane.this.content.width()) {
                c.scroll.x = ScrollPane.this.content.width() - this.width;
            }
            if (c.scroll.x < 0.0f) {
                c.scroll.x = 0.0f;
            }
            if (c.scroll.y + this.height > ScrollPane.this.content.height()) {
                c.scroll.y = ScrollPane.this.content.height() - this.height;
            }
            if (c.scroll.y < 0.0f) {
                c.scroll.y = 0.0f;
            }
            ScrollPane.this.thumb.y = this.y + this.height * c.scroll.y / ScrollPane.this.content.height();
            this.lastPos.set(current);
        }
    }
}

