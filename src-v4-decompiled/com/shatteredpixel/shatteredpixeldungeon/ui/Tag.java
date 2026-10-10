/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;

public class Tag
extends Button {
    private float r;
    private float g;
    private float b;
    protected NinePatch bg;
    protected float lightness = 0.0f;
    public static int SIZE = 24;
    protected boolean flipped = false;

    public Tag(int color) {
        this.r = (float)(color >> 16) / 255.0f;
        this.g = (float)(color >> 8 & 0xFF) / 255.0f;
        this.b = (float)(color & 0xFF) / 255.0f;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.bg = Chrome.get(Chrome.Type.TAG);
        this.bg.hardlight(this.r, this.g, this.b);
        this.add(this.bg);
    }

    @Override
    protected void onClick() {
        GameScene.tagDisappeared = false;
    }

    @Override
    protected void layout() {
        super.layout();
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.bg.size(this.width, this.height);
    }

    public void flash() {
        this.lightness = 1.0f;
    }

    public void flip(boolean value) {
        this.flipped = value;
        this.bg.flipHorizontal(value);
        this.layout();
    }

    public void setColor(int color) {
        this.r = (float)(color >> 16) / 255.0f;
        this.g = (float)(color >> 8 & 0xFF) / 255.0f;
        this.b = (float)(color & 0xFF) / 255.0f;
        this.bg.hardlight(this.r, this.g, this.b);
    }

    @Override
    public void update() {
        super.update();
        if (this.visible && (double)this.lightness > 0.5) {
            float f;
            this.lightness -= Game.elapsed;
            if ((double)f > 0.5) {
                this.bg.ga = this.bg.ba = 2.0f * this.lightness - 1.0f;
                this.bg.ra = this.bg.ba;
                this.bg.rm = 2.0f * this.r * (1.0f - this.lightness);
                this.bg.gm = 2.0f * this.g * (1.0f - this.lightness);
                this.bg.bm = 2.0f * this.b * (1.0f - this.lightness);
            } else {
                this.bg.hardlight(this.r, this.g, this.b);
            }
        }
    }
}

