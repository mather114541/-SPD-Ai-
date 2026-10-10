/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.effects.CircleArc;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Cursor;
import com.watabou.utils.PointF;

public class RadialMenu
extends Window {
    int slots;
    float targetAngle;
    PointF center;
    CircleArc selectionArc;
    RenderedTextBlock titleTxt;
    RenderedTextBlock descTxt;
    String[] texts;
    Image[] icons;
    int selectedIdx = -1;
    private PointF mousePos = new PointF();
    private boolean first = true;

    public RadialMenu(String title, String desc, String[] optionTexts, Image[] optionIcons) {
        super(0, 0, Chrome.get(Chrome.Type.BLANK));
        this.remove(this.shadow);
        int size = SPDSettings.interfaceSize() == 0 ? 140 : 200;
        this.resize(Game.width, Game.height);
        this.slots = optionTexts.length;
        this.center = new PointF(this.width / 2, this.height / 2);
        int length = SPDSettings.interfaceSize() == 0 ? 57 : 80;
        this.selectionArc = new CircleArc(120 / this.slots, size / 2 - 1);
        this.selectionArc.color(0xFFFFFF, false);
        this.selectionArc.alpha(0.6f);
        this.selectionArc.setSweep(1.0f / (float)this.slots);
        this.selectionArc.point(this.center);
        this.selectionArc.visible = false;
        this.add(this.selectionArc);
        Image outerBG = RadialMenu.getBGTexture(size, false);
        outerBG.x = ((float)this.width - outerBG.width) / 2.0f;
        outerBG.y = ((float)this.height - outerBG.height) / 2.0f;
        PixelScene.align(outerBG);
        this.add(outerBG);
        this.texts = optionTexts;
        this.icons = optionIcons;
        for (int i = 0; i < this.slots; ++i) {
            PointF iconCenter = new PointF().polar(6.283185f / (float)this.slots * (float)i - 1.5707963f, length);
            iconCenter.offset(this.center);
            optionIcons[i].x = iconCenter.x - optionIcons[i].width() / 2.0f;
            optionIcons[i].y = iconCenter.y - optionIcons[i].height() / 2.0f;
            PixelScene.align(optionIcons[i]);
            optionIcons[i].alpha(0.4f);
            this.add(optionIcons[i]);
            ColorBlock sep = new ColorBlock(size / 2 - 2, 1.0f, -16777216);
            sep.x = this.center.x;
            sep.y = this.center.y;
            sep.angle = 360.0f / (float)this.slots * (float)i + this.selectionArc.getSweep() * 180.0f - 90.0f;
            this.addToFront(sep);
        }
        Image innerBG = RadialMenu.getBGTexture(size, true);
        innerBG.x = ((float)this.width - innerBG.width) / 2.0f;
        innerBG.y = ((float)this.height - innerBG.height) / 2.0f;
        PixelScene.align(innerBG);
        this.add(innerBG);
        this.descTxt = PixelScene.renderTextBlock(desc, 6);
        this.descTxt.align(2);
        this.descTxt.maxWidth(SPDSettings.interfaceSize() == 0 ? 80 : 100);
        this.descTxt.setPos(this.center.x - this.descTxt.width() / 2.0f, this.center.y - this.descTxt.height() / 4.0f);
        this.add(this.descTxt);
        this.titleTxt = PixelScene.renderTextBlock(title, 9);
        this.titleTxt.setPos(this.center.x - this.titleTxt.width() / 2.0f, this.descTxt.top() - this.titleTxt.height() - 6.0f);
        PixelScene.align(this.titleTxt);
        this.titleTxt.hardlight(0xFFFF44);
        this.add(this.titleTxt);
        Cursor.captureCursor(true);
        Button selector = new Button(){

            @Override
            protected void onClick() {
                super.onClick();
                if (RadialMenu.this.selectedIdx != -1) {
                    RadialMenu.this.hide();
                    RadialMenu.this.onSelect(RadialMenu.this.selectedIdx, false);
                }
            }

            @Override
            protected boolean onLongClick() {
                if (RadialMenu.this.selectedIdx != -1) {
                    RadialMenu.this.hide();
                    RadialMenu.this.onSelect(RadialMenu.this.selectedIdx, true);
                }
                return true;
            }

            @Override
            protected void onRightClick() {
                super.onRightClick();
                if (RadialMenu.this.selectedIdx != -1) {
                    RadialMenu.this.hide();
                    RadialMenu.this.onSelect(RadialMenu.this.selectedIdx, true);
                }
            }
        };
        selector.setRect(0.0f, 0.0f, this.width, this.height);
        this.add(selector);
    }

    public void onSelect(int idx, boolean alt) {
    }

    @Override
    public void destroy() {
        super.destroy();
        Cursor.captureCursor(false);
    }

    @Override
    public synchronized void update() {
        super.update();
        PointF movement = Cursor.getCursorDelta();
        if (Math.abs(ControllerHandler.rightStickPosition.x) >= 0.4f || Math.abs(ControllerHandler.rightStickPosition.y) >= 0.4f) {
            movement.x = ControllerHandler.rightStickPosition.x;
            movement.y = ControllerHandler.rightStickPosition.y;
            this.first = false;
        } else if (movement.length() != 0.0f && !this.first) {
            this.mousePos.offset(movement);
            if (this.mousePos.length() > (float)(PixelScene.defaultZoom * 20)) {
                this.mousePos.invScale(this.mousePos.length() / (float)(PixelScene.defaultZoom * 20));
            }
            movement = this.mousePos;
        }
        if (movement.length() != 0.0f) {
            if (this.first) {
                this.first = false;
                return;
            }
            this.targetAngle = PointF.angle(movement.x, movement.y) / ((float)Math.PI / 180) + 90.0f;
            if (this.targetAngle < 0.0f) {
                this.targetAngle += 360.0f;
            }
            this.selectionArc.visible = true;
            this.selectionArc.angle = this.targetAngle + this.selectionArc.getSweep() * 180.0f;
            int newSelect = Math.round(this.targetAngle / (360.0f / (float)this.slots));
            if (newSelect >= this.slots) {
                newSelect = 0;
            }
            if (newSelect != this.selectedIdx) {
                this.selectedIdx = newSelect;
                for (int i = 0; i < this.slots; ++i) {
                    if (i == this.selectedIdx) {
                        this.icons[i].alpha(1.0f);
                        this.titleTxt.text(this.texts[i]);
                        this.titleTxt.setPos(this.center.x - this.titleTxt.width() / 2.0f, this.descTxt.top() - this.titleTxt.height() - 6.0f);
                        PixelScene.align(this.titleTxt);
                        continue;
                    }
                    this.icons[i].alpha(0.4f);
                }
            }
        }
    }

    private static Image getBGTexture(int size, boolean inner) {
        if (size >= 200) {
            if (!inner) {
                return new Image("interfaces/radial_menu.png", 0, 0, 200, 200);
            }
            return new Image("interfaces/radial_menu.png", 340, 0, 120, 120);
        }
        if (!inner) {
            return new Image("interfaces/radial_menu.png", 200, 0, 140, 140);
        }
        return new Image("interfaces/radial_menu.png", 340, 120, 90, 90);
    }
}

