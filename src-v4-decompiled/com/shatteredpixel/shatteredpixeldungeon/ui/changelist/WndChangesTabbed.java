/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class WndChangesTabbed
extends WndTabbed {
    protected static final int WIDTH_MIN = 120;
    protected static final int WIDTH_MAX = 220;
    protected static final int GAP = 2;
    private ArrayList<RenderedTextBlock> texts = new ArrayList();

    public WndChangesTabbed(Image icon, String title, String ... messages) {
        int width = 120;
        PointerArea blocker = new PointerArea(0.0f, 0.0f, PixelScene.uiCamera.width, PixelScene.uiCamera.height){

            @Override
            protected void onClick(PointerEvent event) {
                WndChangesTabbed.this.onBackPressed();
            }
        };
        blocker.camera = PixelScene.uiCamera;
        this.add(blocker);
        IconTitle titlebar = new IconTitle(icon, title);
        titlebar.setRect(0.0f, 0.0f, width, 0.0f);
        this.add(titlebar);
        Component largest = null;
        int i = 0;
        while (i < messages.length) {
            RenderedTextBlock text = PixelScene.renderTextBlock(6);
            text.text(messages[i], width);
            text.setPos(titlebar.left(), titlebar.bottom() + 4.0f);
            this.add(text);
            this.texts.add(text);
            if (largest == null || text.height() > largest.height()) {
                largest = text;
            }
            final int finalI = i++;
            this.add(new WndTabbed.LabeledTab(this.numToNumeral(finalI + 1)){

                @Override
                protected void select(boolean value) {
                    super.select(value);
                    WndChangesTabbed.this.texts.get((int)finalI).visible = value;
                }
            });
        }
        while (PixelScene.landscape() && largest.bottom() > 140.0f && width < 220) {
            titlebar.setRect(0.0f, 0.0f, width += 20, 0.0f);
            largest = null;
            for (RenderedTextBlock text : this.texts) {
                text.setPos(titlebar.left(), titlebar.bottom() + 4.0f);
                text.maxWidth(width);
                if (largest != null && !(text.height() > largest.height())) continue;
                largest = text;
            }
        }
        this.bringToFront(titlebar);
        this.resize(width, (int)largest.bottom() + 2);
        this.layoutTabs();
        this.select(0);
    }

    private String numToNumeral(int num) {
        switch (num) {
            case 1: {
                return "I";
            }
            case 2: {
                return "II";
            }
            case 3: {
                return "III";
            }
            case 4: {
                return "IV";
            }
            case 5: {
                return "V";
            }
            case 6: {
                return "VI";
            }
            case 7: {
                return "VII";
            }
            case 8: {
                return "VIII";
            }
            case 9: {
                return "IX";
            }
            case 10: {
                return "X";
            }
        }
        return Integer.toString(num);
    }
}

