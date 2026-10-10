/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.input.ScrollEvent;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.Visual;
import com.watabou.utils.Signal;

public class ScrollArea
extends PointerArea {
    private Signal.Listener<ScrollEvent> scrollListener = new Signal.Listener<ScrollEvent>(){

        @Override
        public boolean onSignal(ScrollEvent event) {
            boolean hit;
            boolean bl = hit = event != null && ScrollArea.this.target.overlapsScreenPoint((int)event.pos.x, (int)event.pos.y);
            if (!ScrollArea.this.isActive()) {
                return hit && ScrollArea.this.blockLevel == 0;
            }
            if (hit) {
                ScrollArea.this.onScroll(event);
                return true;
            }
            return false;
        }
    };

    public ScrollArea(Visual target) {
        super(target);
        ScrollEvent.addScrollListener(this.scrollListener);
    }

    public ScrollArea(float x, float y, float width, float height) {
        super(x, y, width, height);
        ScrollEvent.addScrollListener(this.scrollListener);
    }

    protected void onScroll(ScrollEvent event) {
    }

    @Override
    public void destroy() {
        super.destroy();
        ScrollEvent.removeScrollListener(this.scrollListener);
    }
}

