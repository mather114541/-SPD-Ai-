/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class WndOptionsCondensed
extends WndOptions {
    public WndOptionsCondensed(Image icon, String title, String message, String ... options) {
        super(icon, title, message, options);
    }

    public WndOptionsCondensed(String title, String message, String ... options) {
        super(title, message, options);
    }

    @Override
    protected void layoutBody(float pos, String message, String ... options) {
        RedButton btn;
        int width = PixelScene.landscape() ? 144 : 120;
        RenderedTextBlock tfMesage = PixelScene.renderTextBlock(6);
        tfMesage.text(message, width);
        tfMesage.setPos(0.0f, pos);
        this.add(tfMesage);
        pos = tfMesage.bottom() + 4.0f;
        ArrayList<RedButton> buttons = new ArrayList<RedButton>();
        for (int i = 0; i < options.length; ++i) {
            final int index = i;
            btn = new RedButton(options[i]){

                @Override
                protected void onClick() {
                    WndOptionsCondensed.this.hide();
                    WndOptionsCondensed.this.onSelect(index);
                }
            };
            if (this.hasIcon(i)) {
                btn.icon(this.getIcon(i));
            }
            btn.enable(this.enabled(i));
            btn.setSize(btn.reqWidth(), 18.0f);
            this.add(btn);
            buttons.add(btn);
        }
        ArrayList<RedButton> curRow = new ArrayList<RedButton>();
        float widthLeftThisRow = width;
        while (!buttons.isEmpty()) {
            btn = (RedButton)buttons.get(0);
            widthLeftThisRow -= btn.width();
            if (curRow.isEmpty()) {
                curRow.add(btn);
                buttons.remove(btn);
            } else if ((widthLeftThisRow -= 2.0f) >= 0.0f) {
                curRow.add(btn);
                buttons.remove(btn);
            }
            if (!buttons.isEmpty() && !(widthLeftThisRow <= 0.0f) && curRow.size() < 5) continue;
            widthLeftThisRow = width - (curRow.size() - 1);
            for (RedButton b : curRow) {
                widthLeftThisRow -= b.width();
            }
            while (widthLeftThisRow > 0.0f) {
                float widthToGrow;
                ArrayList<RedButton> shortest = new ArrayList<RedButton>();
                Component secondShortest = null;
                for (RedButton b : curRow) {
                    if (shortest.isEmpty()) {
                        shortest.add(b);
                        continue;
                    }
                    if (b.width() < ((RedButton)shortest.get(0)).width()) {
                        secondShortest = (RedButton)shortest.get(0);
                        shortest.clear();
                        shortest.add(b);
                        continue;
                    }
                    if (b.width() == ((RedButton)shortest.get(0)).width()) {
                        shortest.add(b);
                        continue;
                    }
                    if (secondShortest != null && !(secondShortest.width() > b.width())) continue;
                    secondShortest = b;
                }
                if (secondShortest == null) {
                    widthToGrow = widthLeftThisRow / (float)shortest.size();
                    widthLeftThisRow = 0.0f;
                } else {
                    widthToGrow = secondShortest.width() - ((RedButton)shortest.get(0)).width();
                    if (widthToGrow * (float)shortest.size() >= widthLeftThisRow) {
                        widthToGrow = widthLeftThisRow / (float)shortest.size();
                        widthLeftThisRow = 0.0f;
                    } else {
                        widthLeftThisRow -= widthToGrow * (float)shortest.size();
                    }
                }
                for (RedButton toGrow : shortest) {
                    toGrow.setRect(0.0f, 0.0f, toGrow.width() + widthToGrow, toGrow.height());
                }
            }
            float x = 0.0f;
            for (RedButton b : curRow) {
                b.setRect(x, pos, b.width(), b.height());
                x += b.width() + 1.0f;
            }
            pos += 20.0f;
            widthLeftThisRow = width;
            curRow.clear();
        }
        this.resize(width, (int)(pos - 2.0f));
    }

    @Override
    protected boolean hasInfo(int index) {
        return false;
    }
}

