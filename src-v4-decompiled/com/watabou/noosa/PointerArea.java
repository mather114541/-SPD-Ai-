/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.input.PointerEvent;
import com.watabou.noosa.Visual;
import com.watabou.utils.Signal;

public class PointerArea
extends Visual
implements Signal.Listener<PointerEvent> {
    public Visual target;
    protected PointerEvent curEvent = null;
    protected boolean hovered = false;
    public int blockLevel = 1;
    public static final int ALWAYS_BLOCK = 0;
    public static final int BLOCK_WHEN_ACTIVE = 1;
    public static final int NEVER_BLOCK = 2;

    public PointerArea(Visual target) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.target = target;
        PointerEvent.addPointerListener(this);
    }

    public PointerArea(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.target = this;
        this.visible = false;
        PointerEvent.addPointerListener(this);
    }

    @Override
    public boolean onSignal(PointerEvent event) {
        boolean hit;
        boolean bl = hit = event != null && this.target.overlapsScreenPoint((int)event.current.x, (int)event.current.y);
        if (!this.isActive()) {
            return hit && this.blockLevel == 0;
        }
        if (hit) {
            boolean returnValue;
            boolean bl2 = returnValue = event.type == PointerEvent.Type.DOWN || event == this.curEvent;
            if (event.type == PointerEvent.Type.DOWN) {
                if (this.curEvent == null) {
                    this.curEvent = event;
                }
                this.onPointerDown(event);
            } else if (event.type == PointerEvent.Type.UP) {
                this.onPointerUp(event);
                if (this.curEvent == event) {
                    this.curEvent = null;
                    this.onClick(event);
                }
            } else if (event.type == PointerEvent.Type.CANCEL) {
                this.onPointerUp(event);
                if (this.curEvent == event) {
                    this.curEvent = null;
                }
            } else if (event.type == PointerEvent.Type.HOVER) {
                if (event.handled && this.hovered) {
                    this.hovered = false;
                    this.onHoverEnd(event);
                } else if (!event.handled && !this.hovered) {
                    this.hovered = true;
                    this.onHoverStart(event);
                }
                event.handle();
            }
            return returnValue && this.blockLevel != 2;
        }
        if (event == null && this.curEvent != null) {
            this.onDrag(this.curEvent);
        } else if (this.curEvent != null && (event.type == PointerEvent.Type.UP || event.type == PointerEvent.Type.CANCEL)) {
            this.onPointerUp(event);
            this.curEvent = null;
        } else if (event != null && event.type == PointerEvent.Type.HOVER && this.hovered) {
            this.hovered = false;
            this.onHoverEnd(event);
        }
        return false;
    }

    protected void onPointerDown(PointerEvent event) {
    }

    protected void onPointerUp(PointerEvent event) {
    }

    protected void onClick(PointerEvent event) {
    }

    protected void onDrag(PointerEvent event) {
    }

    protected void onHoverStart(PointerEvent event) {
    }

    protected void onHoverEnd(PointerEvent event) {
    }

    public void reset() {
        this.curEvent = null;
    }

    public void givePointerPriority() {
        PointerEvent.removePointerListener(this);
        PointerEvent.addPointerListener(this);
    }

    @Override
    public void destroy() {
        PointerEvent.removePointerListener(this);
        super.destroy();
    }
}

