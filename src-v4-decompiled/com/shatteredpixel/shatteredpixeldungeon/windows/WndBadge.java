/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

public class WndBadge
extends Window {
    private static final int MAX_WIDTH = 125;
    private static final int MARGIN = 4;

    public WndBadge(Badges.Badge badge, boolean unlocked) {
        Image icon = BadgeBanner.image(badge.image);
        icon.scale.set(2.0f);
        if (!unlocked) {
            icon.brightness(0.4f);
        }
        this.add(icon);
        RenderedTextBlock title = PixelScene.renderTextBlock(badge.title(), 9);
        title.maxWidth(117);
        title.align(2);
        title.hardlight(0xFFFF44);
        if (!unlocked) {
            title.hardlight(0x888822);
        }
        this.add(title);
        Object desc = badge.desc();
        String unlock = Badges.showCompletionProgress(badge);
        if (unlock != null) {
            desc = (String)desc + unlock;
        }
        RenderedTextBlock info = PixelScene.renderTextBlock((String)desc, 6);
        info.maxWidth(117);
        info.align(2);
        if (!unlocked) {
            info.hardlight(0x888888);
            info.setHightlighting(true, 0x888822);
        }
        this.add(info);
        float w = Math.max(icon.width(), Math.max(title.width(), info.width())) + 8.0f;
        icon.x = (w - icon.width()) / 2.0f;
        icon.y = 4.0f;
        PixelScene.align(icon);
        title.setPos((w - title.width()) / 2.0f, icon.y + icon.height() + 4.0f);
        PixelScene.align(title);
        info.setPos((w - info.width()) / 2.0f, title.bottom() + 4.0f);
        PixelScene.align(info);
        this.resize((int)w, (int)(info.bottom() + 4.0f));
        if (unlocked) {
            BadgeBanner.highlight(icon, badge.image);
        }
        PointerArea blocker = new PointerArea(0.0f, 0.0f, PixelScene.uiCamera.width, PixelScene.uiCamera.height){

            @Override
            protected void onClick(PointerEvent event) {
                WndBadge.this.onBackPressed();
            }
        };
        blocker.camera = PixelScene.uiCamera;
        this.add(blocker);
    }
}

