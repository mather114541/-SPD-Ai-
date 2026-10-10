/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

public abstract class OptionSlider
extends Component {
    private PointerArea pointerArea;
    private RenderedTextBlock title;
    private RenderedTextBlock minTxt;
    private RenderedTextBlock maxTxt;
    private int minVal;
    private int maxVal;
    private int selectedVal;
    private NinePatch sliderNode;
    private NinePatch BG;
    private ColorBlock sliderBG;
    private ColorBlock[] sliderTicks;
    private float tickDist;

    public OptionSlider(String title, String minTxt, String maxTxt, int minVal, int maxVal) {
        if (minVal > maxVal) {
            minVal = maxVal;
            this.active = false;
        }
        this.title.text(title);
        this.minTxt.text(minTxt);
        this.maxTxt.text(maxTxt);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.sliderTicks = new ColorBlock[maxVal - minVal + 1];
        for (int i = 0; i < this.sliderTicks.length; ++i) {
            this.sliderTicks[i] = new ColorBlock(1.0f, 9.0f, -14540254);
            this.add(this.sliderTicks[i]);
        }
        this.add(this.sliderNode);
    }

    protected abstract void onChange();

    public int getSelectedValue() {
        return this.selectedVal;
    }

    public void setSelectedValue(int val) {
        this.selectedVal = val;
        this.sliderNode.x = (float)((int)(this.x + this.tickDist * (float)(this.selectedVal - this.minVal))) + 0.5f;
        this.sliderNode.y = this.sliderBG.y - 4.0f;
        PixelScene.align(this.sliderNode);
    }

    public void enable(boolean value) {
        this.active = value;
        this.title.alpha(value ? 1.0f : 0.3f);
        this.minTxt.alpha(value ? 1.0f : 0.3f);
        this.maxTxt.alpha(value ? 1.0f : 0.3f);
        this.sliderNode.alpha(value ? 1.0f : 0.3f);
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.BG = Chrome.get(Chrome.Type.RED_BUTTON);
        this.add(this.BG);
        this.BG.alpha(0.5f);
        this.title = PixelScene.renderTextBlock(9);
        this.add(this.title);
        this.minTxt = PixelScene.renderTextBlock(6);
        this.add(this.minTxt);
        this.maxTxt = PixelScene.renderTextBlock(6);
        this.add(this.maxTxt);
        this.sliderBG = new ColorBlock(1.0f, 1.0f, -14540254);
        this.add(this.sliderBG);
        this.sliderNode = Chrome.get(Chrome.Type.RED_BUTTON);
        this.sliderNode.size(4.0f, 7.0f);
        this.pointerArea = new PointerArea(0.0f, 0.0f, 0.0f, 0.0f){
            boolean pressed;
            {
                this.pressed = false;
            }

            @Override
            protected void onPointerDown(PointerEvent event) {
                this.pressed = true;
                PointF p = this.camera().screenToCamera((int)event.current.x, (int)event.current.y);
                OptionSlider.this.sliderNode.x = GameMath.gate(OptionSlider.this.sliderBG.x - 2.0f, p.x - OptionSlider.this.sliderNode.width() / 2.0f, OptionSlider.this.sliderBG.x + OptionSlider.this.sliderBG.width() - 2.0f);
                OptionSlider.this.sliderNode.brightness(1.5f);
            }

            @Override
            protected void onPointerUp(PointerEvent event) {
                if (this.pressed) {
                    PointF p = this.camera().screenToCamera((int)event.current.x, (int)event.current.y);
                    OptionSlider.this.sliderNode.x = GameMath.gate(OptionSlider.this.sliderBG.x - 2.0f, p.x - OptionSlider.this.sliderNode.width() / 2.0f, OptionSlider.this.sliderBG.x + OptionSlider.this.sliderBG.width() - 2.0f);
                    OptionSlider.this.sliderNode.resetColor();
                    OptionSlider.this.selectedVal = OptionSlider.this.minVal + Math.round((OptionSlider.this.sliderNode.x - this.x) / OptionSlider.this.tickDist);
                    OptionSlider.this.sliderNode.x = this.x + OptionSlider.this.tickDist * (float)(OptionSlider.this.selectedVal - OptionSlider.this.minVal) + 0.5f;
                    PixelScene.align(OptionSlider.this.sliderNode);
                    OptionSlider.this.onChange();
                    this.pressed = false;
                }
            }

            @Override
            protected void onDrag(PointerEvent event) {
                if (this.pressed) {
                    PointF p = this.camera().screenToCamera((int)event.current.x, (int)event.current.y);
                    OptionSlider.this.sliderNode.x = GameMath.gate(OptionSlider.this.sliderBG.x - 2.0f, p.x - OptionSlider.this.sliderNode.width() / 2.0f, OptionSlider.this.sliderBG.x + OptionSlider.this.sliderBG.width() - 2.0f);
                }
            }
        };
        this.add(this.pointerArea);
    }

    @Override
    protected void layout() {
        if (this.title.width() > 0.6f * this.width) {
            String titleText = this.title.text;
            this.remove(this.title);
            this.title = PixelScene.renderTextBlock(6);
            this.add(this.title);
            this.title.text(titleText);
        }
        this.title.setPos(this.x + (this.width - this.title.width()) / 2.0f, this.y + 2.0f);
        PixelScene.align(this.title);
        this.sliderBG.y = this.y + this.height() - 7.0f;
        this.sliderBG.x = this.x + 2.0f;
        this.sliderBG.size(this.width - 5.0f, 1.0f);
        this.tickDist = this.sliderBG.width() / (float)(this.maxVal - this.minVal);
        for (int i = 0; i < this.sliderTicks.length; ++i) {
            this.sliderTicks[i].y = this.sliderBG.y - 4.0f;
            this.sliderTicks[i].x = this.x + 2.0f + this.tickDist * (float)i;
            PixelScene.align(this.sliderTicks[i]);
        }
        this.minTxt.setPos(this.x + 1.0f, this.sliderBG.y - 5.0f - this.minTxt.height());
        this.maxTxt.setPos(this.x + this.width() - this.maxTxt.width() - 1.0f, this.sliderBG.y - 5.0f - this.minTxt.height());
        this.sliderNode.x = this.x + this.tickDist * (float)(this.selectedVal - this.minVal) + 0.5f;
        this.sliderNode.y = this.sliderBG.y - 3.0f;
        PixelScene.align(this.sliderNode);
        this.pointerArea.x = this.x;
        this.pointerArea.y = this.y;
        this.pointerArea.width = this.width();
        this.pointerArea.height = this.height();
        this.BG.size(this.width(), this.height());
        this.BG.x = this.x;
        this.BG.y = this.y;
    }
}

