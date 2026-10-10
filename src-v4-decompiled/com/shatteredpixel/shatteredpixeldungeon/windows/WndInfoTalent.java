/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.utils.Callback;

public class WndInfoTalent
extends Window {
    private static final float GAP = 2.0f;
    private static final int WIDTH_MIN = 120;
    private static final int WIDTH_MAX = 220;

    public WndInfoTalent(Talent talent, int points, final TalentButtonCallback buttonCallback) {
        int width = 120;
        IconTitle titlebar = new IconTitle();
        titlebar.icon(new TalentIcon(talent));
        Object title = Messages.titleCase(talent.title());
        if (points > 0) {
            title = (String)title + " +" + points;
        }
        titlebar.label((String)title, 0xFFFF44);
        titlebar.setRect(0.0f, 0.0f, width, 0.0f);
        this.add(titlebar);
        boolean metaDesc = buttonCallback != null && buttonCallback.metamorphDesc() || Dungeon.hero != null && Dungeon.hero.metamorphedTalents.containsValue((Object)talent);
        RenderedTextBlock txtInfo = PixelScene.renderTextBlock(talent.desc(metaDesc), 6);
        txtInfo.maxWidth(width);
        txtInfo.setPos(titlebar.left(), titlebar.bottom() + 4.0f);
        this.add(txtInfo);
        while (PixelScene.landscape() && txtInfo.height() > 120.0f && width < 220) {
            txtInfo.maxWidth(width += 20);
        }
        titlebar.setRect(0.0f, 0.0f, width, 0.0f);
        this.resize(width, (int)(txtInfo.bottom() + 2.0f));
        if (buttonCallback != null) {
            RedButton button = new RedButton(buttonCallback.prompt()){

                @Override
                protected void onClick() {
                    super.onClick();
                    WndInfoTalent.this.hide();
                    buttonCallback.call();
                }
            };
            button.icon(Icons.get(Icons.TALENT));
            button.setRect(0.0f, txtInfo.bottom() + 4.0f, width, 18.0f);
            this.add(button);
            this.resize(width, (int)button.bottom() + 1);
        }
    }

    public static abstract class TalentButtonCallback
    implements Callback {
        public abstract String prompt();

        public boolean metamorphDesc() {
            return false;
        }
    }
}

