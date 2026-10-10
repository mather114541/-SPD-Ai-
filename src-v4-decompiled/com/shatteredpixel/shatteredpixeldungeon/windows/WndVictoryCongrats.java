/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SupporterScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;

public class WndVictoryCongrats
extends Window {
    public WndVictoryCongrats() {
        int width = PixelScene.landscape() ? 180 : 120;
        int height = 0;
        IconTitle title = new IconTitle(new ItemSprite(ItemSpriteSheet.AMULET), Messages.get(this, "title", new Object[0]));
        title.setRect(0.0f, 0.0f, width, 0.0f);
        this.add(title);
        RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(this, "start_text", new Object[0]), 6);
        text.maxWidth(width);
        text.setPos(0.0f, title.bottom() + 4.0f);
        this.add(text);
        height = (int)text.bottom() + 6;
        Image chalImg = Icons.CHALLENGE_COLOR.get();
        chalImg.y = height;
        chalImg.x = (16.0f - chalImg.width()) / 2.0f;
        PixelScene.align(chalImg);
        this.add(chalImg);
        RenderedTextBlock chalTxt = PixelScene.renderTextBlock(Messages.get(this, "challenges", new Object[0]), 6);
        chalTxt.maxWidth(width - 16);
        chalTxt.setPos(16.0f, height);
        this.add(chalTxt);
        if (chalTxt.height() > chalImg.height()) {
            chalImg.y += (chalTxt.height() - chalImg.height()) / 2.0f;
            PixelScene.align(chalImg);
        }
        height = (int)((float)height + (Math.max(chalImg.height(), chalTxt.height()) + 6.0f));
        ItemSprite seedImg = new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS);
        seedImg.y = height;
        seedImg.x = (16.0f - seedImg.width()) / 2.0f;
        PixelScene.align(seedImg);
        this.add(seedImg);
        RenderedTextBlock seedTxt = PixelScene.renderTextBlock(Messages.get(this, "custom_seeds", new Object[0]), 6);
        seedTxt.maxWidth(width - 16);
        seedTxt.setPos(16.0f, height);
        this.add(seedTxt);
        if (seedTxt.height() > seedImg.height()) {
            seedImg.y += (seedTxt.height() - seedImg.height()) / 2.0f;
            PixelScene.align(seedImg);
        }
        height = (int)((float)height + (Math.max(seedImg.height(), seedTxt.height()) + 6.0f));
        Image dailyImg = Icons.CALENDAR.get();
        dailyImg.hardlight(0.5f, 1.0f, 2.0f);
        dailyImg.y = height;
        dailyImg.x = (16.0f - dailyImg.width()) / 2.0f;
        PixelScene.align(dailyImg);
        this.add(dailyImg);
        RenderedTextBlock dailyTxt = PixelScene.renderTextBlock(Messages.get(this, "dailies", new Object[0]), 6);
        dailyTxt.maxWidth(width - 16);
        dailyTxt.setPos(16.0f, height);
        this.add(dailyTxt);
        if (dailyTxt.height() > dailyImg.height()) {
            dailyImg.y += (dailyTxt.height() - dailyImg.height()) / 2.0f;
            PixelScene.align(dailyImg);
        }
        height = (int)((float)height + (Math.max(dailyImg.height(), dailyTxt.height()) + 6.0f));
        RenderedTextBlock finalTxt = PixelScene.renderTextBlock(Messages.get(this, "thank_you", new Object[0]) + " " + Messages.get(this, "support_prompt", new Object[0]), 6);
        finalTxt.maxWidth(width);
        finalTxt.setPos(0.0f, height);
        this.add(finalTxt);
        height = (int)finalTxt.bottom() + 4;
        RedButton btnSupport = new RedButton(Messages.get(this, "support", new Object[0])){

            @Override
            protected void onClick() {
                ShatteredPixelDungeon.switchScene(SupporterScene.class);
            }
        };
        btnSupport.icon(Icons.GOLD.get());
        btnSupport.setRect(0.0f, height, width / 2, 18.0f);
        this.add(btnSupport);
        RedButton btnClose = new RedButton(Messages.get(this, "close", new Object[0])){

            @Override
            protected void onClick() {
                WndVictoryCongrats.this.hide();
            }
        };
        btnClose.icon(Icons.EXIT.get());
        btnClose.setRect(btnSupport.right() + 1.0f, height, width / 2 - 1, 18.0f);
        this.add(btnClose);
        this.resize(width, (int)btnClose.bottom());
    }

    @Override
    public void onBackPressed() {
    }
}

