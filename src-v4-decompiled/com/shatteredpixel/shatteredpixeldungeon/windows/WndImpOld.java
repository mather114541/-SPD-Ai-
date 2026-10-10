/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;

public class WndImpOld
extends Window {
    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final int GAP = 2;

    public WndImpOld(final Imp imp, final DwarfToken tokens) {
        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(tokens.image(), null));
        titlebar.label(Messages.titleCase(tokens.name()));
        titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(titlebar);
        RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message", new Object[0]), 6);
        message.maxWidth(120);
        message.setPos(0.0f, titlebar.bottom() + 2.0f);
        this.add(message);
        RedButton btnReward = new RedButton(Messages.get(this, "reward", new Object[0])){

            @Override
            protected void onClick() {
                WndImpOld.this.takeReward(imp, tokens, Imp.Quest.reward);
            }
        };
        btnReward.setRect(0.0f, message.top() + message.height() + 2.0f, 120.0f, 20.0f);
        this.add(btnReward);
        this.resize(120, (int)btnReward.bottom());
    }

    private void takeReward(Imp imp, DwarfToken tokens, Item reward) {
        this.hide();
        tokens.detachAll(Dungeon.hero.belongings.backpack);
        if (reward == null) {
            return;
        }
        reward.identify(false);
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())), new Object[0]);
        } else {
            Dungeon.level.drop((Item)reward, (int)imp.pos).sprite.drop();
        }
        imp.flee();
        Imp.Quest.oldComplete();
    }
}

