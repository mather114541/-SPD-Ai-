/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextInput;
import com.watabou.utils.DeviceCompat;

public class WndTextInput
extends Window {
    private static final int WIDTH = 135;
    private static final int W_LAND_EXTRA = 220;
    private static final int MARGIN = 1;
    private static final int BUTTON_HEIGHT = 16;
    protected TextInput textBox;
    protected RedButton btnCopy;
    protected RedButton btnPaste;

    public WndTextInput(String title, String body, String initialValue, int maxLength, boolean multiLine, String posTxt, String negTxt) {
        float btnWidth;
        int width = PixelScene.landscape() && (multiLine || body != null) ? 220 : 135;
        float pos = 2.0f;
        if (title != null) {
            RenderedTextBlock txtTitle = PixelScene.renderTextBlock(title, 9);
            txtTitle.maxWidth(width);
            txtTitle.hardlight(0xFFFF44);
            txtTitle.setPos(((float)width - txtTitle.width()) / 2.0f, 2.0f);
            this.add(txtTitle);
            pos = txtTitle.bottom() + 4.0f;
        }
        if (body != null) {
            RenderedTextBlock txtBody = PixelScene.renderTextBlock(body, 6);
            txtBody.maxWidth(width);
            txtBody.setPos(0.0f, pos);
            this.add(txtBody);
            pos = txtBody.bottom() + 2.0f;
        }
        int textSize = (int)PixelScene.uiCamera.zoom * (multiLine ? 6 : 9);
        this.textBox = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), multiLine, textSize){

            @Override
            public void enterPressed() {
                WndTextInput.this.onSelect(true, this.getText());
                WndTextInput.this.hide();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                if (WndTextInput.this.btnCopy != null) {
                    WndTextInput.this.btnCopy.enable(!this.getText().isEmpty());
                }
            }

            @Override
            public void onClipBoardUpdate() {
                super.onClipBoardUpdate();
                WndTextInput.this.btnPaste.enable(Gdx.app.getClipboard().hasContents());
            }
        };
        if (initialValue != null) {
            this.textBox.setText(initialValue);
        }
        this.textBox.setMaxLength(maxLength);
        float inputHeight = multiLine ? 64.0f : 16.0f;
        float textBoxWidth = width - 3 - 16;
        this.add(this.textBox);
        this.textBox.setRect(1.0f, pos, textBoxWidth, inputHeight);
        this.btnCopy = new RedButton(""){

            @Override
            protected void onPointerDown() {
                super.onPointerDown();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onClick() {
                super.onClick();
                WndTextInput.this.textBox.copyToClipboard();
            }
        };
        this.btnCopy.icon(Icons.COPY.get());
        this.btnCopy.enable(!this.textBox.getText().isEmpty());
        this.add(this.btnCopy);
        this.btnPaste = new RedButton(""){

            @Override
            protected void onPointerDown() {
                super.onPointerDown();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onClick() {
                super.onClick();
                if (Gdx.app.getClipboard().hasContents()) {
                    WndTextInput.this.textBox.pasteFromClipboard();
                } else {
                    this.enable(false);
                }
            }
        };
        this.btnPaste.icon(Icons.PASTE.get());
        this.btnPaste.enable(Gdx.app.getClipboard().hasContents());
        this.add(this.btnPaste);
        this.btnCopy.setRect(textBoxWidth + 2.0f, pos, 16.0f, 16.0f);
        this.btnPaste.setRect(textBoxWidth + 2.0f, this.btnCopy.bottom() + 1.0f, 16.0f, 16.0f);
        pos += inputHeight + 1.0f;
        RedButton positiveBtn = new RedButton(posTxt){

            @Override
            protected void onClick() {
                WndTextInput.this.onSelect(true, WndTextInput.this.textBox.getText());
                WndTextInput.this.hide();
            }
        };
        RedButton negativeBtn = negTxt != null ? new RedButton(negTxt){

            @Override
            protected void onClick() {
                WndTextInput.this.onSelect(false, WndTextInput.this.textBox.getText());
                WndTextInput.this.hide();
            }
        } : null;
        float f = btnWidth = multiLine ? (float)(width - 2) : textBoxWidth;
        if (negTxt != null) {
            positiveBtn.setRect(1.0f, pos, (btnWidth - 1.0f) / 2.0f, 16.0f);
            this.add(positiveBtn);
            negativeBtn.setRect(positiveBtn.right() + 1.0f, pos, (btnWidth - 1.0f) / 2.0f, 16.0f);
            this.add(negativeBtn);
        } else {
            positiveBtn.setRect(1.0f, pos, btnWidth, 16.0f);
            this.add(positiveBtn);
        }
        this.resize(width, (int)(pos += 16.0f));
        if (!DeviceCompat.hasHardKeyboard()) {
            this.offset(0, -((int)((float)Game.height / (4.0f * this.camera.zoom))));
            this.boundOffsetWithMargin(0);
        }
        this.textBox.setRect(1.0f, this.textBox.top(), textBoxWidth, inputHeight);
        PointerEvent.clearKeyboardThisPress = false;
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        if (this.textBox != null) {
            this.textBox.setRect(this.textBox.left(), this.textBox.top(), this.textBox.width(), this.textBox.height());
        }
    }

    public void onSelect(boolean positive, String text) {
    }

    @Override
    public void onBackPressed() {
    }
}

