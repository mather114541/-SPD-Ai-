/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import java.util.ArrayList;

public class WndChallenges
extends Window {
    private static final int WIDTH = 120;
    private static final int TTL_HEIGHT = 16;
    private static final int BTN_HEIGHT = 16;
    private static final int GAP = 1;
    private boolean editable;
    private ArrayList<CheckBox> boxes;

    public WndChallenges(int checked, boolean editable) {
        this.editable = editable;
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 12);
        title.hardlight(0xFFFF44);
        title.setPos((120.0f - title.width()) / 2.0f, (16.0f - title.height()) / 2.0f);
        PixelScene.align(title);
        this.add(title);
        this.boxes = new ArrayList();
        float pos = 16.0f;
        for (int i = 0; i < Challenges.NAME_IDS.length; ++i) {
            final String challenge = Challenges.NAME_IDS[i];
            CheckBox cb = new CheckBox(Messages.titleCase(Messages.get(Challenges.class, challenge, new Object[0])));
            cb.checked((checked & Challenges.MASKS[i]) != 0);
            cb.active = editable;
            if (i > 0) {
                pos += 1.0f;
            }
            cb.setRect(0.0f, pos, 104.0f, 16.0f);
            this.add(cb);
            this.boxes.add(cb);
            IconButton info = new IconButton(Icons.get(Icons.INFO)){

                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.scene().add(new WndMessage(Messages.get(Challenges.class, challenge + "_desc", new Object[0])));
                }
            };
            info.setRect(cb.right(), pos, 16.0f, 16.0f);
            this.add(info);
            pos = cb.bottom();
        }
        this.resize(120, (int)pos);
    }

    @Override
    public void onBackPressed() {
        if (this.editable) {
            int value = 0;
            for (int i = 0; i < this.boxes.size(); ++i) {
                if (!this.boxes.get(i).checked()) continue;
                value |= Challenges.MASKS[i];
            }
            SPDSettings.challenges(value);
        }
        super.onBackPressed();
    }
}

