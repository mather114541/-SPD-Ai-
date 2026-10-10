/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;

public class WndOptions
extends Window {
    protected static final int WIDTH_P = 120;
    protected static final int WIDTH_L = 144;
    protected static final int MARGIN = 2;
    protected static final int BUTTON_HEIGHT = 18;

    public WndOptions(Image icon, String title, String message, String ... options) {
        int width = PixelScene.landscape() ? 144 : 120;
        float pos = 0.0f;
        if (title != null) {
            IconTitle tfTitle = new IconTitle(icon, title);
            tfTitle.setRect(0.0f, pos, width, 0.0f);
            this.add(tfTitle);
            pos = tfTitle.bottom() + 4.0f;
        }
        this.layoutBody(pos, message, options);
    }

    public WndOptions(String title, String message, String ... options) {
        int width = PixelScene.landscape() ? 144 : 120;
        float pos = 2.0f;
        if (title != null) {
            RenderedTextBlock tfTitle = PixelScene.renderTextBlock(title, 9);
            tfTitle.hardlight(0xFFFF44);
            tfTitle.setPos(2.0f, pos);
            tfTitle.maxWidth(width - 4);
            this.add(tfTitle);
            pos = tfTitle.bottom() + 4.0f;
        }
        this.layoutBody(pos, message, options);
    }

    protected void layoutBody(float pos, String message, String ... options) {
        int width = PixelScene.landscape() ? 144 : 120;
        RenderedTextBlock tfMesage = PixelScene.renderTextBlock(6);
        tfMesage.text(message, width);
        tfMesage.setPos(0.0f, pos);
        this.add(tfMesage);
        pos = tfMesage.bottom() + 4.0f;
        for (int i = 0; i < options.length; ++i) {
            final int index = i;
            RedButton btn = new RedButton(options[i]){

                @Override
                protected void onClick() {
                    WndOptions.this.hide();
                    WndOptions.this.onSelect(index);
                }
            };
            if (this.hasIcon(i)) {
                btn.icon(this.getIcon(i));
            }
            btn.multiline = true;
            this.add(btn);
            if (!this.hasInfo(i)) {
                btn.setRect(0.0f, pos, width, 18.0f);
            } else {
                btn.setRect(0.0f, pos, width - 18, 18.0f);
                IconButton info = new IconButton(Icons.get(Icons.INFO)){

                    @Override
                    protected void onClick() {
                        WndOptions.this.onInfo(index);
                    }
                };
                info.setRect(width - 18, pos, 18.0f, 18.0f);
                this.add(info);
            }
            btn.enable(this.enabled(i));
            pos += 20.0f;
        }
        this.resize(width, (int)(pos - 2.0f));
    }

    protected boolean enabled(int index) {
        return true;
    }

    protected void onSelect(int index) {
    }

    protected boolean hasInfo(int index) {
        return false;
    }

    protected void onInfo(int index) {
    }

    protected boolean hasIcon(int index) {
        return false;
    }

    protected Image getIcon(int index) {
        return null;
    }
}

