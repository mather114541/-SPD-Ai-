/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.Pixel_Dungeon_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChanges;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChangesTabbed;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_4_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_5_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_6_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_7_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_8_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_9_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v4_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;
import java.util.ArrayList;

public class ChangesScene
extends PixelScene {
    public static int changesSelected = 0;
    private NinePatch rightPanel;
    private ScrollPane rightScroll;
    private IconTitle changeTitle;
    private RenderedTextBlock changeBody;

    @Override
    public void create() {
        super.create();
        Music.INSTANCE.playTracks(new String[]{"music/theme_1.ogg", "music/theme_2.ogg"}, new float[]{1.0f, 1.0f}, false);
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        IconTitle title = new IconTitle(Icons.CHANGES.get(), Messages.get(this, "title", new Object[0]));
        title.setSize(200.0f, 0.0f);
        title.setPos(insets.left + ((float)w - title.reqWidth()) / 2.0f, insets.top + (20.0f - title.height()) / 2.0f);
        ChangesScene.align(title);
        this.add(title);
        ExitButton btnExit = new ExitButton();
        btnExit.setPos(insets.left + (float)w - btnExit.width(), insets.top);
        this.add(btnExit);
        NinePatch panel = Chrome.get(Chrome.Type.TOAST);
        int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
        int ph = h - 36;
        if ((float)h >= 200.0f && w >= 300) {
            panel.size(pw, ph);
            panel.x = insets.left + (float)(w - pw) / 2.0f - (float)(pw / 2) - 1.0f;
            panel.y = insets.top + 20.0f;
            this.rightPanel = Chrome.get(Chrome.Type.TOAST);
            this.rightPanel.size(pw, ph);
            this.rightPanel.x = (float)(w - pw) / 2.0f + (float)(pw / 2) + 1.0f;
            this.rightPanel.y = 20.0f;
            this.add(this.rightPanel);
            this.rightScroll = new ScrollPane(new Component());
            this.add(this.rightScroll);
            this.rightScroll.setRect(this.rightPanel.x + (float)this.rightPanel.marginLeft(), this.rightPanel.y + (float)this.rightPanel.marginTop() - 1.0f, this.rightPanel.innerWidth() + 2.0f, this.rightPanel.innerHeight() + 2.0f);
            this.rightScroll.scrollTo(0.0f, 0.0f);
            this.changeTitle = new IconTitle(Icons.get(Icons.CHANGES), Messages.get(this, "right_title", new Object[0]));
            this.changeTitle.setPos(0.0f, 1.0f);
            this.changeTitle.setSize(pw, 20.0f);
            this.rightScroll.content().add(this.changeTitle);
            String body = Messages.get(this, "right_body", new Object[0]);
            this.changeBody = PixelScene.renderTextBlock(body, 6);
            this.changeBody.maxWidth(pw - panel.marginHor());
            this.changeBody.setPos(0.0f, this.changeTitle.bottom() + 2.0f);
            this.rightScroll.content().add(this.changeBody);
        } else {
            panel.size(pw, ph);
            panel.x = insets.left + (float)(w - pw) / 2.0f;
            panel.y = insets.top + 20.0f;
        }
        ChangesScene.align(panel);
        this.add(panel);
        final ArrayList<ChangeInfo> changeInfos = new ArrayList<ChangeInfo>();
        if (Messages.lang() != Languages.ENGLISH) {
            ChangeInfo langWarn = new ChangeInfo("", true, Messages.get(this, "lang_warn", new Object[0]));
            langWarn.hardlight(0xFF8800);
            changeInfos.add(langWarn);
        }
        switch (changesSelected) {
            default: {
                v4_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 1: {
                v3_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 2: {
                v2_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 3: {
                v1_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 4: {
                v0_9_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 5: {
                v0_8_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 6: {
                v0_7_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 7: {
                v0_6_X_Changes.addAllChanges(changeInfos);
                break;
            }
            case 8: {
                v0_5_X_Changes.addAllChanges(changeInfos);
                v0_4_X_Changes.addAllChanges(changeInfos);
                v0_3_X_Changes.addAllChanges(changeInfos);
                v0_2_X_Changes.addAllChanges(changeInfos);
                v0_1_X_Changes.addAllChanges(changeInfos);
                Pixel_Dungeon_Changes.addAllChanges(changeInfos);
            }
        }
        ScrollPane list = new ScrollPane(new Component()){

            @Override
            public void onClick(float x, float y) {
                for (ChangeInfo info : changeInfos) {
                    if (!info.onClick(x, y)) continue;
                    return;
                }
            }
        };
        this.add(list);
        Component content = list.content();
        content.clear();
        float posY = 0.0f;
        float nextPosY = 0.0f;
        boolean second = false;
        for (ChangeInfo info : changeInfos) {
            if (info.major) {
                posY = nextPosY;
                second = false;
                info.setRect(0.0f, posY, panel.innerWidth(), 0.0f);
                content.add(info);
                posY = nextPosY = info.bottom();
                continue;
            }
            if (!second) {
                second = true;
                info.setRect(0.0f, posY, panel.innerWidth() / 2.0f, 0.0f);
                content.add(info);
                nextPosY = info.bottom();
                continue;
            }
            second = false;
            info.setRect(panel.innerWidth() / 2.0f, posY, panel.innerWidth() / 2.0f, 0.0f);
            content.add(info);
            posY = nextPosY = Math.max(info.bottom(), nextPosY);
        }
        content.setSize(panel.innerWidth(), (int)Math.ceil(posY));
        list.setRect(panel.x + (float)panel.marginLeft(), panel.y + (float)panel.marginTop() - 1.0f, panel.innerWidth() + 2.0f, panel.innerHeight() + 2.0f);
        list.scrollTo(0.0f, 0.0f);
        float left = list.left() - 4.0f;
        if (changesSelected <= 3) {
            left = this.setupChangesSelectionButton(0, "v4.X", left, list.bottom(), 24.0f);
            left = this.setupChangesSelectionButton(1, "v3.X", left, list.bottom(), 24.0f);
            left = this.setupChangesSelectionButton(2, "v2.X", left, list.bottom(), 24.0f);
            left = this.setupChangesSelectionButton(3, "v1.X", left, list.bottom(), 24.0f);
            left = this.setupChangesSelectionButton(4, "PreRelease->", left, list.bottom(), 53.0f);
        } else {
            left = this.setupChangesSelectionButton(3, "<-Release", left, list.bottom(), 40.0f);
            left = this.setupChangesSelectionButton(4, "v0.9", left, list.bottom(), 22.0f);
            left = this.setupChangesSelectionButton(5, "v0.8", left, list.bottom(), 22.0f);
            left = this.setupChangesSelectionButton(6, "v0.7", left, list.bottom(), 22.0f);
            left = this.setupChangesSelectionButton(7, "v0.6", left, list.bottom(), 22.0f);
            left = this.setupChangesSelectionButton(8, "v0.5-", left, list.bottom(), 23.0f);
        }
        this.addToBack(BG);
        this.fadeIn();
    }

    private float setupChangesSelectionButton(final int idx, String text, float left, float top, float width) {
        StyledButton button = new StyledButton(Chrome.Type.GREY_BUTTON_TR, text, 8){

            @Override
            protected void onClick() {
                super.onClick();
                if (changesSelected != idx) {
                    changesSelected = idx;
                    ShatteredPixelDungeon.seamlessResetScene();
                }
            }
        };
        if (changesSelected != idx) {
            button.textColor(0xBBBBBB);
        }
        button.setRect(left, top, width, changesSelected == idx ? 19.0f : 15.0f);
        this.addToBack(button);
        return button.right() - 2.0f;
    }

    private void updateChangesText(Image icon, String title, String ... messages) {
        if (this.changeTitle != null) {
            this.changeTitle.icon(icon);
            this.changeTitle.label(title);
            this.changeTitle.setPos(this.changeTitle.left(), this.changeTitle.top());
            Object message = "";
            for (int i = 0; i < messages.length; ++i) {
                message = (String)message + messages[i];
                if (i == messages.length - 1) continue;
                message = (String)message + "\n\n";
            }
            this.changeBody.text((String)message);
            this.rightScroll.content().setSize(this.rightScroll.width(), this.changeBody.bottom() + 2.0f);
            this.rightScroll.setSize(this.rightScroll.width(), this.rightScroll.height());
            this.rightScroll.scrollTo(0.0f, 0.0f);
        } else if (messages.length == 1) {
            this.addToFront(new WndChanges(icon, title, messages[0]));
        } else {
            this.addToFront(new WndChangesTabbed(icon, title, messages));
        }
    }

    public static void showChangeInfo(Image icon, String title, String ... messages) {
        Scene s = ShatteredPixelDungeon.scene();
        if (s instanceof ChangesScene) {
            ((ChangesScene)s).updateChangesText(icon, title, messages);
            return;
        }
        if (messages.length == 1) {
            s.addToFront(new WndChanges(icon, title, messages[0]));
        } else {
            s.addToFront(new WndChangesTabbed(icon, title, messages));
        }
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }
}

