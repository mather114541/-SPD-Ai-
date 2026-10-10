/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Styleable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Null;

public class ScrollPane
extends WidgetGroup
implements Styleable<ScrollPaneStyle> {
    private ScrollPaneStyle style;
    private Actor actor;
    final Rectangle actorArea = new Rectangle();
    final Rectangle hScrollBounds = new Rectangle();
    final Rectangle hKnobBounds = new Rectangle();
    final Rectangle vScrollBounds = new Rectangle();
    final Rectangle vKnobBounds = new Rectangle();
    private final Rectangle actorCullingArea = new Rectangle();
    private ActorGestureListener flickScrollListener;
    boolean scrollX;
    boolean scrollY;
    boolean vScrollOnRight = true;
    boolean hScrollOnBottom = true;
    float amountX;
    float amountY;
    float visualAmountX;
    float visualAmountY;
    float maxX;
    float maxY;
    boolean touchScrollH;
    boolean touchScrollV;
    final Vector2 lastPoint = new Vector2();
    boolean fadeScrollBars = true;
    boolean smoothScrolling = true;
    boolean scrollBarTouch = true;
    float fadeAlpha;
    float fadeAlphaSeconds = 1.0f;
    float fadeDelay;
    float fadeDelaySeconds = 1.0f;
    boolean cancelTouchFocus = true;
    boolean flickScroll = true;
    float flingTime = 1.0f;
    float flingTimer;
    float velocityX;
    float velocityY;
    private boolean overscrollX = true;
    private boolean overscrollY = true;
    private float overscrollDistance = 50.0f;
    private float overscrollSpeedMin = 30.0f;
    private float overscrollSpeedMax = 200.0f;
    private boolean forceScrollX;
    private boolean forceScrollY;
    boolean disableX;
    boolean disableY;
    private boolean clamp = true;
    private boolean scrollbarsOnTop;
    private boolean variableSizeKnobs = true;
    int draggingPointer = -1;

    public ScrollPane(@Null Actor actor) {
        this(actor, new ScrollPaneStyle());
    }

    public ScrollPane(@Null Actor actor, Skin skin) {
        this(actor, skin.get(ScrollPaneStyle.class));
    }

    public ScrollPane(@Null Actor actor, Skin skin, String styleName) {
        this(actor, skin.get(styleName, ScrollPaneStyle.class));
    }

    public ScrollPane(@Null Actor actor, ScrollPaneStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.setActor(actor);
        this.setSize(150.0f, 150.0f);
        this.addCaptureListener();
        this.flickScrollListener = this.getFlickScrollListener();
        this.addListener(this.flickScrollListener);
        this.addScrollListener();
    }

    protected void addCaptureListener() {
        this.addCaptureListener(new InputListener(){
            private float handlePosition;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (ScrollPane.this.draggingPointer != -1) {
                    return false;
                }
                if (pointer == 0 && button != 0) {
                    return false;
                }
                if (ScrollPane.this.getStage() != null) {
                    ScrollPane.this.getStage().setScrollFocus(ScrollPane.this);
                }
                if (!ScrollPane.this.flickScroll) {
                    ScrollPane.this.setScrollbarsVisible(true);
                }
                if (ScrollPane.this.fadeAlpha == 0.0f) {
                    return false;
                }
                if (ScrollPane.this.scrollBarTouch && ScrollPane.this.scrollX && ScrollPane.this.hScrollBounds.contains(x, y)) {
                    event.stop();
                    ScrollPane.this.setScrollbarsVisible(true);
                    if (ScrollPane.this.hKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.hKnobBounds.x;
                        ScrollPane.this.touchScrollH = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                    }
                    ScrollPane.this.setScrollX(ScrollPane.this.amountX + ScrollPane.this.actorArea.width * (float)(x < ScrollPane.this.hKnobBounds.x ? -1 : 1));
                    return true;
                }
                if (ScrollPane.this.scrollBarTouch && ScrollPane.this.scrollY && ScrollPane.this.vScrollBounds.contains(x, y)) {
                    event.stop();
                    ScrollPane.this.setScrollbarsVisible(true);
                    if (ScrollPane.this.vKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.vKnobBounds.y;
                        ScrollPane.this.touchScrollV = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                    }
                    ScrollPane.this.setScrollY(ScrollPane.this.amountY + ScrollPane.this.actorArea.height * (float)(y < ScrollPane.this.vKnobBounds.y ? 1 : -1));
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer != ScrollPane.this.draggingPointer) {
                    return;
                }
                ScrollPane.this.cancel();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (pointer != ScrollPane.this.draggingPointer) {
                    return;
                }
                if (ScrollPane.this.touchScrollH) {
                    float scrollH;
                    float delta = x - ScrollPane.this.lastPoint.x;
                    this.handlePosition = scrollH = this.handlePosition + delta;
                    scrollH = Math.max(ScrollPane.this.hScrollBounds.x, scrollH);
                    scrollH = Math.min(ScrollPane.this.hScrollBounds.x + ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width, scrollH);
                    float total = ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width;
                    if (total != 0.0f) {
                        ScrollPane.this.setScrollPercentX((scrollH - ScrollPane.this.hScrollBounds.x) / total);
                    }
                    ScrollPane.this.lastPoint.set(x, y);
                } else if (ScrollPane.this.touchScrollV) {
                    float scrollV;
                    float delta = y - ScrollPane.this.lastPoint.y;
                    this.handlePosition = scrollV = this.handlePosition + delta;
                    scrollV = Math.max(ScrollPane.this.vScrollBounds.y, scrollV);
                    scrollV = Math.min(ScrollPane.this.vScrollBounds.y + ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height, scrollV);
                    float total = ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height;
                    if (total != 0.0f) {
                        ScrollPane.this.setScrollPercentY(1.0f - (scrollV - ScrollPane.this.vScrollBounds.y) / total);
                    }
                    ScrollPane.this.lastPoint.set(x, y);
                }
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (!ScrollPane.this.flickScroll) {
                    ScrollPane.this.setScrollbarsVisible(true);
                }
                return false;
            }
        });
    }

    protected ActorGestureListener getFlickScrollListener() {
        return new ActorGestureListener(){

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                ScrollPane.this.setScrollbarsVisible(true);
                if (!ScrollPane.this.scrollX) {
                    deltaX = 0.0f;
                }
                if (!ScrollPane.this.scrollY) {
                    deltaY = 0.0f;
                }
                ScrollPane.this.amountX -= deltaX;
                ScrollPane.this.amountY += deltaY;
                ScrollPane.this.clamp();
                if (ScrollPane.this.cancelTouchFocus && (deltaX != 0.0f || deltaY != 0.0f)) {
                    ScrollPane.this.cancelTouchFocus();
                }
            }

            @Override
            public void fling(InputEvent event, float x, float y, int button) {
                float velocityY;
                float velocityX = Math.abs(x) > 150.0f && ScrollPane.this.scrollX ? x : 0.0f;
                float f = velocityY = Math.abs(y) > 150.0f && ScrollPane.this.scrollY ? -y : 0.0f;
                if (velocityX != 0.0f || velocityY != 0.0f) {
                    if (ScrollPane.this.cancelTouchFocus) {
                        ScrollPane.this.cancelTouchFocus();
                    }
                    ScrollPane.this.fling(ScrollPane.this.flingTime, velocityX, velocityY);
                }
            }

            @Override
            public boolean handle(Event event) {
                if (super.handle(event)) {
                    if (((InputEvent)event).getType() == InputEvent.Type.touchDown) {
                        ScrollPane.this.flingTimer = 0.0f;
                    }
                    return true;
                }
                if (event instanceof InputEvent && ((InputEvent)event).isTouchFocusCancel()) {
                    ScrollPane.this.cancel();
                }
                return false;
            }
        };
    }

    protected void addScrollListener() {
        this.addListener(new InputListener(){

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float scrollAmountX, float scrollAmountY) {
                event.cancel();
                ScrollPane.this.setScrollbarsVisible(true);
                if (ScrollPane.this.scrollY || ScrollPane.this.scrollX) {
                    if (ScrollPane.this.scrollY) {
                        if (!ScrollPane.this.scrollX && scrollAmountY == 0.0f) {
                            scrollAmountY = scrollAmountX;
                        }
                    } else if (ScrollPane.this.scrollX && scrollAmountX == 0.0f) {
                        scrollAmountX = scrollAmountY;
                    }
                } else {
                    return false;
                }
                ScrollPane.this.setScrollY(ScrollPane.this.amountY + ScrollPane.this.getMouseWheelY() * scrollAmountY);
                ScrollPane.this.setScrollX(ScrollPane.this.amountX + ScrollPane.this.getMouseWheelX() * scrollAmountX);
                return true;
            }
        });
    }

    public void setScrollbarsVisible(boolean visible) {
        if (visible) {
            this.fadeAlpha = this.fadeAlphaSeconds;
            this.fadeDelay = this.fadeDelaySeconds;
        } else {
            this.fadeAlpha = 0.0f;
            this.fadeDelay = 0.0f;
        }
    }

    public void cancelTouchFocus() {
        Stage stage = this.getStage();
        if (stage != null) {
            stage.cancelTouchFocusExcept(this.flickScrollListener, this);
        }
    }

    public void cancel() {
        this.draggingPointer = -1;
        this.touchScrollH = false;
        this.touchScrollV = false;
        this.flickScrollListener.getGestureDetector().cancel();
    }

    void clamp() {
        if (!this.clamp) {
            return;
        }
        this.scrollX(this.overscrollX ? MathUtils.clamp(this.amountX, -this.overscrollDistance, this.maxX + this.overscrollDistance) : MathUtils.clamp(this.amountX, 0.0f, this.maxX));
        this.scrollY(this.overscrollY ? MathUtils.clamp(this.amountY, -this.overscrollDistance, this.maxY + this.overscrollDistance) : MathUtils.clamp(this.amountY, 0.0f, this.maxY));
    }

    @Override
    public void setStyle(ScrollPaneStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.invalidateHierarchy();
    }

    @Override
    public ScrollPaneStyle getStyle() {
        return this.style;
    }

    @Override
    public void act(float delta) {
        Stage stage;
        super.act(delta);
        boolean panning = this.flickScrollListener.getGestureDetector().isPanning();
        boolean animating = false;
        if (this.fadeAlpha > 0.0f && this.fadeScrollBars && !panning && !this.touchScrollH && !this.touchScrollV) {
            this.fadeDelay -= delta;
            if (this.fadeDelay <= 0.0f) {
                this.fadeAlpha = Math.max(0.0f, this.fadeAlpha - delta);
            }
            animating = true;
        }
        if (this.flingTimer > 0.0f) {
            this.setScrollbarsVisible(true);
            float alpha = this.flingTimer / this.flingTime;
            this.amountX -= this.velocityX * alpha * delta;
            this.amountY -= this.velocityY * alpha * delta;
            this.clamp();
            if (this.amountX == -this.overscrollDistance) {
                this.velocityX = 0.0f;
            }
            if (this.amountX >= this.maxX + this.overscrollDistance) {
                this.velocityX = 0.0f;
            }
            if (this.amountY == -this.overscrollDistance) {
                this.velocityY = 0.0f;
            }
            if (this.amountY >= this.maxY + this.overscrollDistance) {
                this.velocityY = 0.0f;
            }
            this.flingTimer -= delta;
            if (this.flingTimer <= 0.0f) {
                this.velocityX = 0.0f;
                this.velocityY = 0.0f;
            }
            animating = true;
        }
        if (this.smoothScrolling && this.flingTimer <= 0.0f && !panning && (!this.touchScrollH || this.scrollX && this.maxX / (this.hScrollBounds.width - this.hKnobBounds.width) > this.actorArea.width * 0.1f) && (!this.touchScrollV || this.scrollY && this.maxY / (this.vScrollBounds.height - this.vKnobBounds.height) > this.actorArea.height * 0.1f)) {
            if (this.visualAmountX != this.amountX) {
                if (this.visualAmountX < this.amountX) {
                    this.visualScrollX(Math.min(this.amountX, this.visualAmountX + Math.max(200.0f * delta, (this.amountX - this.visualAmountX) * 7.0f * delta)));
                } else {
                    this.visualScrollX(Math.max(this.amountX, this.visualAmountX - Math.max(200.0f * delta, (this.visualAmountX - this.amountX) * 7.0f * delta)));
                }
                animating = true;
            }
            if (this.visualAmountY != this.amountY) {
                if (this.visualAmountY < this.amountY) {
                    this.visualScrollY(Math.min(this.amountY, this.visualAmountY + Math.max(200.0f * delta, (this.amountY - this.visualAmountY) * 7.0f * delta)));
                } else {
                    this.visualScrollY(Math.max(this.amountY, this.visualAmountY - Math.max(200.0f * delta, (this.visualAmountY - this.amountY) * 7.0f * delta)));
                }
                animating = true;
            }
        } else {
            if (this.visualAmountX != this.amountX) {
                this.visualScrollX(this.amountX);
            }
            if (this.visualAmountY != this.amountY) {
                this.visualScrollY(this.amountY);
            }
        }
        if (!panning) {
            if (this.overscrollX && this.scrollX) {
                if (this.amountX < 0.0f) {
                    this.setScrollbarsVisible(true);
                    this.amountX += (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -this.amountX / this.overscrollDistance) * delta;
                    if (this.amountX > 0.0f) {
                        this.scrollX(0.0f);
                    }
                    animating = true;
                } else if (this.amountX > this.maxX) {
                    this.setScrollbarsVisible(true);
                    this.amountX -= (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -(this.maxX - this.amountX) / this.overscrollDistance) * delta;
                    if (this.amountX < this.maxX) {
                        this.scrollX(this.maxX);
                    }
                    animating = true;
                }
            }
            if (this.overscrollY && this.scrollY) {
                if (this.amountY < 0.0f) {
                    this.setScrollbarsVisible(true);
                    this.amountY += (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -this.amountY / this.overscrollDistance) * delta;
                    if (this.amountY > 0.0f) {
                        this.scrollY(0.0f);
                    }
                    animating = true;
                } else if (this.amountY > this.maxY) {
                    this.setScrollbarsVisible(true);
                    this.amountY -= (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -(this.maxY - this.amountY) / this.overscrollDistance) * delta;
                    if (this.amountY < this.maxY) {
                        this.scrollY(this.maxY);
                    }
                    animating = true;
                }
            }
        }
        if (animating && (stage = this.getStage()) != null && stage.getActionsRequestRendering()) {
            Gdx.graphics.requestRendering();
        }
    }

    @Override
    public void layout() {
        float y;
        float actorHeight;
        float actorWidth;
        Drawable bg = this.style.background;
        Drawable hScrollKnob = this.style.hScrollKnob;
        Drawable vScrollKnob = this.style.vScrollKnob;
        float bgLeftWidth = 0.0f;
        float bgRightWidth = 0.0f;
        float bgTopHeight = 0.0f;
        float bgBottomHeight = 0.0f;
        if (bg != null) {
            bgLeftWidth = bg.getLeftWidth();
            bgRightWidth = bg.getRightWidth();
            bgTopHeight = bg.getTopHeight();
            bgBottomHeight = bg.getBottomHeight();
        }
        float width = this.getWidth();
        float height = this.getHeight();
        this.actorArea.set(bgLeftWidth, bgBottomHeight, width - bgLeftWidth - bgRightWidth, height - bgTopHeight - bgBottomHeight);
        if (this.actor == null) {
            return;
        }
        float scrollbarHeight = 0.0f;
        float scrollbarWidth = 0.0f;
        if (hScrollKnob != null) {
            scrollbarHeight = hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
        }
        if (vScrollKnob != null) {
            scrollbarWidth = vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
        }
        if (this.actor instanceof Layout) {
            Layout layout = (Layout)((Object)this.actor);
            actorWidth = layout.getPrefWidth();
            actorHeight = layout.getPrefHeight();
        } else {
            actorWidth = this.actor.getWidth();
            actorHeight = this.actor.getHeight();
        }
        this.scrollX = this.forceScrollX || actorWidth > this.actorArea.width && !this.disableX;
        boolean bl = this.scrollY = this.forceScrollY || actorHeight > this.actorArea.height && !this.disableY;
        if (!this.scrollbarsOnTop) {
            if (this.scrollY) {
                this.actorArea.width -= scrollbarWidth;
                if (!this.vScrollOnRight) {
                    this.actorArea.x += scrollbarWidth;
                }
                if (!this.scrollX && actorWidth > this.actorArea.width && !this.disableX) {
                    this.scrollX = true;
                }
            }
            if (this.scrollX) {
                this.actorArea.height -= scrollbarHeight;
                if (this.hScrollOnBottom) {
                    this.actorArea.y += scrollbarHeight;
                }
                if (!this.scrollY && actorHeight > this.actorArea.height && !this.disableY) {
                    this.scrollY = true;
                    this.actorArea.width -= scrollbarWidth;
                    if (!this.vScrollOnRight) {
                        this.actorArea.x += scrollbarWidth;
                    }
                }
            }
        }
        actorWidth = this.disableX ? this.actorArea.width : Math.max(this.actorArea.width, actorWidth);
        actorHeight = this.disableY ? this.actorArea.height : Math.max(this.actorArea.height, actorHeight);
        this.maxX = actorWidth - this.actorArea.width;
        this.maxY = actorHeight - this.actorArea.height;
        this.scrollX(MathUtils.clamp(this.amountX, 0.0f, this.maxX));
        this.scrollY(MathUtils.clamp(this.amountY, 0.0f, this.maxY));
        if (this.scrollX) {
            if (hScrollKnob != null) {
                float x = this.scrollbarsOnTop ? bgLeftWidth : this.actorArea.x;
                y = this.hScrollOnBottom ? bgBottomHeight : height - bgTopHeight - scrollbarHeight;
                this.hScrollBounds.set(x, y, this.actorArea.width, scrollbarHeight);
                if (this.scrollY && this.scrollbarsOnTop) {
                    this.hScrollBounds.width -= scrollbarWidth;
                    if (!this.vScrollOnRight) {
                        this.hScrollBounds.x += scrollbarWidth;
                    }
                }
                this.hKnobBounds.width = this.variableSizeKnobs ? Math.max(hScrollKnob.getMinWidth(), (float)((int)(this.hScrollBounds.width * this.actorArea.width / actorWidth))) : hScrollKnob.getMinWidth();
                if (this.hKnobBounds.width > actorWidth) {
                    this.hKnobBounds.width = 0.0f;
                }
                this.hKnobBounds.height = hScrollKnob.getMinHeight();
                this.hKnobBounds.x = this.hScrollBounds.x + (float)((int)((this.hScrollBounds.width - this.hKnobBounds.width) * this.getScrollPercentX()));
                this.hKnobBounds.y = this.hScrollBounds.y;
            } else {
                this.hScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                this.hKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
            }
        }
        if (this.scrollY) {
            if (vScrollKnob != null) {
                float x = this.vScrollOnRight ? width - bgRightWidth - scrollbarWidth : bgLeftWidth;
                y = this.scrollbarsOnTop ? bgBottomHeight : this.actorArea.y;
                this.vScrollBounds.set(x, y, scrollbarWidth, this.actorArea.height);
                if (this.scrollX && this.scrollbarsOnTop) {
                    this.vScrollBounds.height -= scrollbarHeight;
                    if (this.hScrollOnBottom) {
                        this.vScrollBounds.y += scrollbarHeight;
                    }
                }
                this.vKnobBounds.width = vScrollKnob.getMinWidth();
                this.vKnobBounds.height = this.variableSizeKnobs ? Math.max(vScrollKnob.getMinHeight(), (float)((int)(this.vScrollBounds.height * this.actorArea.height / actorHeight))) : vScrollKnob.getMinHeight();
                if (this.vKnobBounds.height > actorHeight) {
                    this.vKnobBounds.height = 0.0f;
                }
                this.vKnobBounds.x = this.vScrollOnRight ? width - bgRightWidth - vScrollKnob.getMinWidth() : bgLeftWidth;
                this.vKnobBounds.y = this.vScrollBounds.y + (float)((int)((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - this.getScrollPercentY())));
            } else {
                this.vScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                this.vKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
            }
        }
        this.updateActorPosition();
        if (this.actor instanceof Layout) {
            this.actor.setSize(actorWidth, actorHeight);
            ((Layout)((Object)this.actor)).validate();
        }
    }

    private void updateActorPosition() {
        float x = this.actorArea.x - (float)(this.scrollX ? (int)this.visualAmountX : 0);
        float y = this.actorArea.y - (float)((int)(this.scrollY ? this.maxY - this.visualAmountY : this.maxY));
        this.actor.setPosition(x, y);
        if (this.actor instanceof Cullable) {
            this.actorCullingArea.x = this.actorArea.x - x;
            this.actorCullingArea.y = this.actorArea.y - y;
            this.actorCullingArea.width = this.actorArea.width;
            this.actorCullingArea.height = this.actorArea.height;
            ((Cullable)((Object)this.actor)).setCullingArea(this.actorCullingArea);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.actor == null) {
            return;
        }
        this.validate();
        this.applyTransform(batch, this.computeTransform());
        if (this.scrollX) {
            this.hKnobBounds.x = this.hScrollBounds.x + (float)((int)((this.hScrollBounds.width - this.hKnobBounds.width) * this.getVisualScrollPercentX()));
        }
        if (this.scrollY) {
            this.vKnobBounds.y = this.vScrollBounds.y + (float)((int)((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - this.getVisualScrollPercentY())));
        }
        this.updateActorPosition();
        Color color = this.getColor();
        float alpha = color.a * parentAlpha;
        if (this.style.background != null) {
            batch.setColor(color.r, color.g, color.b, alpha);
            this.style.background.draw(batch, 0.0f, 0.0f, this.getWidth(), this.getHeight());
        }
        batch.flush();
        if (this.clipBegin(this.actorArea.x, this.actorArea.y, this.actorArea.width, this.actorArea.height)) {
            this.drawChildren(batch, parentAlpha);
            batch.flush();
            this.clipEnd();
        }
        batch.setColor(color.r, color.g, color.b, alpha);
        if (this.fadeScrollBars) {
            alpha *= Interpolation.fade.apply(this.fadeAlpha / this.fadeAlphaSeconds);
        }
        this.drawScrollBars(batch, color.r, color.g, color.b, alpha);
        this.resetTransform(batch);
    }

    protected void drawScrollBars(Batch batch, float r, float g, float b, float a) {
        boolean y;
        if (a <= 0.0f) {
            return;
        }
        batch.setColor(r, g, b, a);
        boolean x = this.scrollX && this.hKnobBounds.width > 0.0f;
        boolean bl = y = this.scrollY && this.vKnobBounds.height > 0.0f;
        if (x) {
            if (y && this.style.corner != null) {
                this.style.corner.draw(batch, this.hScrollBounds.x + this.hScrollBounds.width, this.hScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.y);
            }
            if (this.style.hScroll != null) {
                this.style.hScroll.draw(batch, this.hScrollBounds.x, this.hScrollBounds.y, this.hScrollBounds.width, this.hScrollBounds.height);
            }
            if (this.style.hScrollKnob != null) {
                this.style.hScrollKnob.draw(batch, this.hKnobBounds.x, this.hKnobBounds.y, this.hKnobBounds.width, this.hKnobBounds.height);
            }
        }
        if (y) {
            if (this.style.vScroll != null) {
                this.style.vScroll.draw(batch, this.vScrollBounds.x, this.vScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.height);
            }
            if (this.style.vScrollKnob != null) {
                this.style.vScrollKnob.draw(batch, this.vKnobBounds.x, this.vKnobBounds.y, this.vKnobBounds.width, this.vKnobBounds.height);
            }
        }
    }

    public void fling(float flingTime, float velocityX, float velocityY) {
        this.flingTimer = flingTime;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override
    public float getPrefWidth() {
        float width = 0.0f;
        if (this.actor instanceof Layout) {
            width = ((Layout)((Object)this.actor)).getPrefWidth();
        } else if (this.actor != null) {
            width = this.actor.getWidth();
        }
        Drawable background = this.style.background;
        if (background != null) {
            width = Math.max(width + background.getLeftWidth() + background.getRightWidth(), background.getMinWidth());
        }
        if (this.scrollY) {
            float scrollbarWidth = 0.0f;
            if (this.style.vScrollKnob != null) {
                scrollbarWidth = this.style.vScrollKnob.getMinWidth();
            }
            if (this.style.vScroll != null) {
                scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
            }
            width += scrollbarWidth;
        }
        return width;
    }

    @Override
    public float getPrefHeight() {
        float height = 0.0f;
        if (this.actor instanceof Layout) {
            height = ((Layout)((Object)this.actor)).getPrefHeight();
        } else if (this.actor != null) {
            height = this.actor.getHeight();
        }
        Drawable background = this.style.background;
        if (background != null) {
            height = Math.max(height + background.getTopHeight() + background.getBottomHeight(), background.getMinHeight());
        }
        if (this.scrollX) {
            float scrollbarHeight = 0.0f;
            if (this.style.hScrollKnob != null) {
                scrollbarHeight = this.style.hScrollKnob.getMinHeight();
            }
            if (this.style.hScroll != null) {
                scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
            }
            height += scrollbarHeight;
        }
        return height;
    }

    @Override
    public float getMinWidth() {
        return 0.0f;
    }

    @Override
    public float getMinHeight() {
        return 0.0f;
    }

    public void setActor(@Null Actor actor) {
        if (this.actor == this) {
            throw new IllegalArgumentException("actor cannot be the ScrollPane.");
        }
        if (this.actor != null) {
            super.removeActor(this.actor);
        }
        this.actor = actor;
        if (actor != null) {
            super.addActor(actor);
        }
    }

    @Null
    public Actor getActor() {
        return this.actor;
    }

    @Deprecated
    public void setWidget(@Null Actor actor) {
        this.setActor(actor);
    }

    @Deprecated
    @Null
    public Actor getWidget() {
        return this.actor;
    }

    @Override
    @Deprecated
    public void addActor(Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setActor.");
    }

    @Override
    @Deprecated
    public void addActorAt(int index, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setActor.");
    }

    @Override
    @Deprecated
    public void addActorBefore(Actor actorBefore, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setActor.");
    }

    @Override
    @Deprecated
    public void addActorAfter(Actor actorAfter, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setActor.");
    }

    @Override
    public boolean removeActor(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor != this.actor) {
            return false;
        }
        this.setActor(null);
        return true;
    }

    @Override
    public boolean removeActor(Actor actor, boolean unfocus) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor != this.actor) {
            return false;
        }
        this.actor = null;
        return super.removeActor(actor, unfocus);
    }

    @Override
    public Actor removeActorAt(int index, boolean unfocus) {
        Actor actor = super.removeActorAt(index, unfocus);
        if (actor == this.actor) {
            this.actor = null;
        }
        return actor;
    }

    @Override
    @Null
    public Actor hit(float x, float y, boolean touchable) {
        if (x < 0.0f || x >= this.getWidth() || y < 0.0f || y >= this.getHeight()) {
            return null;
        }
        if (touchable && this.getTouchable() == Touchable.enabled && this.isVisible()) {
            if (this.scrollX && this.touchScrollH && this.hScrollBounds.contains(x, y)) {
                return this;
            }
            if (this.scrollY && this.touchScrollV && this.vScrollBounds.contains(x, y)) {
                return this;
            }
        }
        return super.hit(x, y, touchable);
    }

    protected void scrollX(float pixelsX) {
        this.amountX = pixelsX;
    }

    protected void scrollY(float pixelsY) {
        this.amountY = pixelsY;
    }

    protected void visualScrollX(float pixelsX) {
        this.visualAmountX = pixelsX;
    }

    protected void visualScrollY(float pixelsY) {
        this.visualAmountY = pixelsY;
    }

    protected float getMouseWheelX() {
        return Math.min(this.actorArea.width, Math.max(this.actorArea.width * 0.9f, this.maxX * 0.1f) / 4.0f);
    }

    protected float getMouseWheelY() {
        return Math.min(this.actorArea.height, Math.max(this.actorArea.height * 0.9f, this.maxY * 0.1f) / 4.0f);
    }

    public void setScrollX(float pixels) {
        this.scrollX(MathUtils.clamp(pixels, 0.0f, this.maxX));
    }

    public float getScrollX() {
        return this.amountX;
    }

    public void setScrollY(float pixels) {
        this.scrollY(MathUtils.clamp(pixels, 0.0f, this.maxY));
    }

    public float getScrollY() {
        return this.amountY;
    }

    public void updateVisualScroll() {
        this.visualAmountX = this.amountX;
        this.visualAmountY = this.amountY;
    }

    public float getVisualScrollX() {
        return !this.scrollX ? 0.0f : this.visualAmountX;
    }

    public float getVisualScrollY() {
        return !this.scrollY ? 0.0f : this.visualAmountY;
    }

    public float getVisualScrollPercentX() {
        if (this.maxX == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.visualAmountX / this.maxX, 0.0f, 1.0f);
    }

    public float getVisualScrollPercentY() {
        if (this.maxY == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.visualAmountY / this.maxY, 0.0f, 1.0f);
    }

    public float getScrollPercentX() {
        if (this.maxX == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.amountX / this.maxX, 0.0f, 1.0f);
    }

    public void setScrollPercentX(float percentX) {
        this.scrollX(this.maxX * MathUtils.clamp(percentX, 0.0f, 1.0f));
    }

    public float getScrollPercentY() {
        if (this.maxY == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.amountY / this.maxY, 0.0f, 1.0f);
    }

    public void setScrollPercentY(float percentY) {
        this.scrollY(this.maxY * MathUtils.clamp(percentY, 0.0f, 1.0f));
    }

    public void setFlickScroll(boolean flickScroll) {
        if (this.flickScroll == flickScroll) {
            return;
        }
        this.flickScroll = flickScroll;
        if (flickScroll) {
            this.addListener(this.flickScrollListener);
        } else {
            this.removeListener(this.flickScrollListener);
        }
        this.invalidate();
    }

    public void setFlickScrollTapSquareSize(float halfTapSquareSize) {
        this.flickScrollListener.getGestureDetector().setTapSquareSize(halfTapSquareSize);
    }

    public void scrollTo(float x, float y, float width, float height) {
        this.scrollTo(x, y, width, height, false, false);
    }

    public void scrollTo(float x, float y, float width, float height, boolean centerHorizontal, boolean centerVertical) {
        this.validate();
        float amountX = this.amountX;
        amountX = centerHorizontal ? x + (width - this.actorArea.width) / 2.0f : MathUtils.clamp(amountX, x, x + width - this.actorArea.width);
        this.scrollX(MathUtils.clamp(amountX, 0.0f, this.maxX));
        float amountY = this.amountY;
        y = this.maxY - y;
        amountY = centerVertical ? y + (this.actorArea.height + height) / 2.0f : MathUtils.clamp(amountY, y + height, y + this.actorArea.height);
        this.scrollY(MathUtils.clamp(amountY, 0.0f, this.maxY));
    }

    public float getMaxX() {
        return this.maxX;
    }

    public float getMaxY() {
        return this.maxY;
    }

    public float getScrollBarHeight() {
        if (!this.scrollX) {
            return 0.0f;
        }
        float height = 0.0f;
        if (this.style.hScrollKnob != null) {
            height = this.style.hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            height = Math.max(height, this.style.hScroll.getMinHeight());
        }
        return height;
    }

    public float getScrollBarWidth() {
        if (!this.scrollY) {
            return 0.0f;
        }
        float width = 0.0f;
        if (this.style.vScrollKnob != null) {
            width = this.style.vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            width = Math.max(width, this.style.vScroll.getMinWidth());
        }
        return width;
    }

    public float getScrollWidth() {
        return this.actorArea.width;
    }

    public float getScrollHeight() {
        return this.actorArea.height;
    }

    public boolean isScrollX() {
        return this.scrollX;
    }

    public boolean isScrollY() {
        return this.scrollY;
    }

    public void setScrollingDisabled(boolean x, boolean y) {
        if (x == this.disableX && y == this.disableY) {
            return;
        }
        this.disableX = x;
        this.disableY = y;
        this.invalidate();
    }

    public boolean isScrollingDisabledX() {
        return this.disableX;
    }

    public boolean isScrollingDisabledY() {
        return this.disableY;
    }

    public boolean isLeftEdge() {
        return !this.scrollX || this.amountX <= 0.0f;
    }

    public boolean isRightEdge() {
        return !this.scrollX || this.amountX >= this.maxX;
    }

    public boolean isTopEdge() {
        return !this.scrollY || this.amountY <= 0.0f;
    }

    public boolean isBottomEdge() {
        return !this.scrollY || this.amountY >= this.maxY;
    }

    public boolean isDragging() {
        return this.draggingPointer != -1;
    }

    public boolean isPanning() {
        return this.flickScrollListener.getGestureDetector().isPanning();
    }

    public boolean isFlinging() {
        return this.flingTimer > 0.0f;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityX() {
        return this.velocityX;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getVelocityY() {
        return this.velocityY;
    }

    public void setOverscroll(boolean overscrollX, boolean overscrollY) {
        this.overscrollX = overscrollX;
        this.overscrollY = overscrollY;
    }

    public void setupOverscroll(float distance, float speedMin, float speedMax) {
        this.overscrollDistance = distance;
        this.overscrollSpeedMin = speedMin;
        this.overscrollSpeedMax = speedMax;
    }

    public float getOverscrollDistance() {
        return this.overscrollDistance;
    }

    public void setForceScroll(boolean x, boolean y) {
        this.forceScrollX = x;
        this.forceScrollY = y;
    }

    public boolean isForceScrollX() {
        return this.forceScrollX;
    }

    public boolean isForceScrollY() {
        return this.forceScrollY;
    }

    public void setFlingTime(float flingTime) {
        this.flingTime = flingTime;
    }

    public void setClamp(boolean clamp) {
        this.clamp = clamp;
    }

    public void setScrollBarPositions(boolean bottom, boolean right) {
        this.hScrollOnBottom = bottom;
        this.vScrollOnRight = right;
    }

    public void setFadeScrollBars(boolean fadeScrollBars) {
        if (this.fadeScrollBars == fadeScrollBars) {
            return;
        }
        this.fadeScrollBars = fadeScrollBars;
        if (!fadeScrollBars) {
            this.fadeAlpha = this.fadeAlphaSeconds;
        }
        this.invalidate();
    }

    public void setupFadeScrollBars(float fadeAlphaSeconds, float fadeDelaySeconds) {
        this.fadeAlphaSeconds = fadeAlphaSeconds;
        this.fadeDelaySeconds = fadeDelaySeconds;
    }

    public boolean getFadeScrollBars() {
        return this.fadeScrollBars;
    }

    public void setScrollBarTouch(boolean scrollBarTouch) {
        this.scrollBarTouch = scrollBarTouch;
    }

    public void setSmoothScrolling(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }

    public void setScrollbarsOnTop(boolean scrollbarsOnTop) {
        this.scrollbarsOnTop = scrollbarsOnTop;
        this.invalidate();
    }

    public boolean getVariableSizeKnobs() {
        return this.variableSizeKnobs;
    }

    public void setVariableSizeKnobs(boolean variableSizeKnobs) {
        this.variableSizeKnobs = variableSizeKnobs;
    }

    public void setCancelTouchFocus(boolean cancelTouchFocus) {
        this.cancelTouchFocus = cancelTouchFocus;
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        this.drawDebugBounds(shapes);
        this.applyTransform(shapes, this.computeTransform());
        if (this.clipBegin(this.actorArea.x, this.actorArea.y, this.actorArea.width, this.actorArea.height)) {
            this.drawDebugChildren(shapes);
            shapes.flush();
            this.clipEnd();
        }
        this.resetTransform(shapes);
    }

    public static class ScrollPaneStyle {
        @Null
        public Drawable background;
        @Null
        public Drawable corner;
        @Null
        public Drawable hScroll;
        @Null
        public Drawable hScrollKnob;
        @Null
        public Drawable vScroll;
        @Null
        public Drawable vScrollKnob;

        public ScrollPaneStyle() {
        }

        public ScrollPaneStyle(@Null Drawable background, @Null Drawable hScroll, @Null Drawable hScrollKnob, @Null Drawable vScroll, @Null Drawable vScrollKnob) {
            this.background = background;
            this.hScroll = hScroll;
            this.hScrollKnob = hScrollKnob;
            this.vScroll = vScroll;
            this.vScrollKnob = vScrollKnob;
        }

        public ScrollPaneStyle(ScrollPaneStyle style) {
            this.background = style.background;
            this.corner = style.corner;
            this.hScroll = style.hScroll;
            this.hScrollKnob = style.hScrollKnob;
            this.vScroll = style.vScroll;
            this.vScrollKnob = style.vScrollKnob;
        }
    }
}

