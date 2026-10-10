/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.RectF;

public class Tooltip
extends Component {
    private static float tooltipAlpha = -5.0f;
    private static float lastUsedTime = -1.0f;
    private Component parent;
    private RectF parentDims;
    private NinePatch bg;
    private RenderedTextBlock text;

    public static void resetLastUsedTime() {
        lastUsedTime = -1.0f;
        tooltipAlpha = -5.0f;
    }

    public Tooltip(Component parent, String msg, int maxWidth) {
        this.text.text(msg, maxWidth);
        this.layout();
        this.parent = parent;
        this.parentDims = new RectF(parent.left(), parent.top(), parent.right(), parent.bottom());
        if (lastUsedTime == -1.0f || lastUsedTime > Game.timeTotal) {
            tooltipAlpha = -5.0f;
        } else {
            float elapsed = Game.timeTotal - lastUsedTime;
            if (elapsed >= 0.25f || tooltipAlpha < 1.0f) {
                tooltipAlpha = -5.0f;
            }
        }
        lastUsedTime = Game.timeTotal;
        this.bg.alpha(GameMath.gate(0.0f, tooltipAlpha, 1.0f));
        this.text.alpha(GameMath.gate(0.0f, tooltipAlpha, 1.0f));
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.bg = Chrome.get(Chrome.Type.TOAST_TR_HEAVY);
        this.add(this.bg);
        this.text = PixelScene.renderTextBlock(6);
        this.add(this.text);
    }

    @Override
    public synchronized void update() {
        if (!(this.parent.exists && this.parent.isActive() && this.parent.isVisible() && this.parentDims.left == this.parent.left() && this.parentDims.top == this.parent.top() && this.parentDims.right == this.parent.right() && this.parentDims.bottom == this.parent.bottom())) {
            this.killAndErase();
            return;
        }
        super.update();
        tooltipAlpha = Math.min(1.0f, tooltipAlpha + 10.0f * Game.elapsed);
        lastUsedTime = Game.timeTotal;
        this.bg.alpha(GameMath.gate(0.0f, tooltipAlpha, 1.0f));
        this.text.alpha(GameMath.gate(0.0f, tooltipAlpha, 1.0f));
    }

    @Override
    protected void layout() {
        this.text.setPos(this.x + (float)this.bg.marginLeft(), this.y + (float)this.bg.marginTop());
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.bg.size(this.text.width() + (float)this.bg.marginHor(), this.text.height() + (float)this.bg.marginVer());
        this.width = this.bg.width;
        this.height = this.bg.height;
    }
}

