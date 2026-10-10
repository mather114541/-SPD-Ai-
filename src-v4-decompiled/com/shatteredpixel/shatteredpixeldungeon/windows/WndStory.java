/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

public class WndStory
extends Window {
    private static final int WIDTH_P = 125;
    private static final int WIDTH_L = 180;
    private static final int MARGIN = 2;
    private IconTitle ttl;
    private RenderedTextBlock tf;
    private float appearDelay;
    private float disappearDelay;

    public WndStory(String text) {
        this(null, null, text);
    }

    public WndStory(Image icon, String title, String text) {
        super(0, 0, Chrome.get(Chrome.Type.SCROLL));
        int width = PixelScene.landscape() ? 176 : 121;
        float y = 2.0f;
        if (icon != null && title != null) {
            this.ttl = new IconTitle(icon, title);
            this.ttl.setRect(2.0f, y, width - 4, 0.0f);
            y = this.ttl.bottom() + 2.0f;
            this.add(this.ttl);
            this.ttl.tfLabel.invert();
        }
        this.tf = PixelScene.renderTextBlock(text, 6);
        this.tf.maxWidth(width);
        this.tf.invert();
        this.tf.setPos(2.0f, y);
        this.add(this.tf);
        PointerArea blocker = new PointerArea(0.0f, 0.0f, PixelScene.uiCamera.width, PixelScene.uiCamera.height){

            @Override
            protected void onClick(PointerEvent event) {
                WndStory.this.onBackPressed();
            }
        };
        blocker.camera = PixelScene.uiCamera;
        this.add(blocker);
        this.resize(width + 4, (int)(this.tf.bottom() + 2.0f));
    }

    public WndStory setDelays(float appearDelay, float disappearDelay) {
        this.appearDelay = appearDelay;
        if (appearDelay > 0.0f) {
            this.tf.visible = false;
            this.chrome.visible = false;
            this.shadow.visible = false;
            if (this.ttl != null) {
                this.ttl.visible = false;
            }
        }
        this.disappearDelay = disappearDelay;
        return this;
    }

    @Override
    public void onBackPressed() {
        if (this.appearDelay <= 0.0f && this.disappearDelay <= 0.0f) {
            super.onBackPressed();
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.appearDelay > 0.0f) {
            this.appearDelay -= Game.elapsed;
            if (this.appearDelay <= 0.0f) {
                this.tf.visible = true;
                this.chrome.visible = true;
                this.shadow.visible = true;
                if (this.ttl != null) {
                    this.ttl.visible = true;
                }
            }
        } else if (this.disappearDelay > 0.0f) {
            this.disappearDelay -= Game.elapsed;
        }
    }
}

