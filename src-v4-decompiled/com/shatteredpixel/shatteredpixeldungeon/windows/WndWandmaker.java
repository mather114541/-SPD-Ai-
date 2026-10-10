/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;

public class WndWandmaker
extends Window {
    private static final int WIDTH = 120;
    private static final int BTN_SIZE = 32;
    private static final int BTN_GAP = 5;
    private static final int GAP = 2;
    Wandmaker wandmaker;
    Item questItem;

    public WndWandmaker(Wandmaker wandmaker, Item item) {
        this.wandmaker = wandmaker;
        this.questItem = item;
        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(item.image(), null));
        titlebar.label(Messages.titleCase(item.name()));
        titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(titlebar);
        String msg = "";
        if (item instanceof CorpseDust) {
            msg = Messages.get(this, "dust", new Object[0]);
        } else if (item instanceof Embers) {
            msg = Messages.get(this, "ember", new Object[0]);
        } else if (item instanceof Rotberry.Seed) {
            msg = Messages.get(this, "berry", new Object[0]);
        }
        RenderedTextBlock message = PixelScene.renderTextBlock(msg, 6);
        message.maxWidth(120);
        message.setPos(0.0f, titlebar.bottom() + 2.0f);
        this.add(message);
        ItemButton btnWand1 = new ItemButton(){

            @Override
            protected void onClick() {
                if (Dungeon.hero.belongings.contains(WndWandmaker.this.questItem) && this.item() != null) {
                    GameScene.show(new RewardWindow(this.item()));
                } else {
                    WndWandmaker.this.hide();
                }
            }
        };
        btnWand1.item(Wandmaker.Quest.wand1);
        btnWand1.setRect(25.0f, message.top() + message.height() + 5.0f, 32.0f, 32.0f);
        this.add(btnWand1);
        ItemButton btnWand2 = new ItemButton(){

            @Override
            protected void onClick() {
                if (Dungeon.hero.belongings.contains(WndWandmaker.this.questItem) && this.item() != null) {
                    GameScene.show(new RewardWindow(this.item()));
                } else {
                    WndWandmaker.this.hide();
                }
            }
        };
        btnWand2.item(Wandmaker.Quest.wand2);
        btnWand2.setRect(btnWand1.right() + 5.0f, btnWand1.top(), 32.0f, 32.0f);
        this.add(btnWand2);
        this.resize(120, (int)btnWand2.bottom());
    }

    private void selectReward(Item reward) {
        if (reward == null) {
            return;
        }
        this.hide();
        this.questItem.detach(Dungeon.hero.belongings.backpack);
        reward.identify(false);
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())), new Object[0]);
        } else {
            Dungeon.level.drop((Item)reward, (int)this.wandmaker.pos).sprite.drop();
        }
        this.wandmaker.yell(Messages.get(this, "farewell", Messages.titleCase(Dungeon.hero.name())));
        this.wandmaker.destroy();
        this.wandmaker.sprite.die();
        Wandmaker.Quest.complete();
    }

    private class RewardWindow
    extends WndInfoItem {
        public RewardWindow(final Item item) {
            super(item);
            RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm", new Object[0])){

                @Override
                protected void onClick() {
                    RewardWindow.this.hide();
                    WndWandmaker.this.selectReward(item);
                }
            };
            btnConfirm.setRect(0.0f, this.height + 2, this.width / 2 - 1, 16.0f);
            this.add(btnConfirm);
            RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel", new Object[0])){

                @Override
                protected void onClick() {
                    RewardWindow.this.hide();
                }
            };
            btnCancel.setRect(btnConfirm.right() + 2.0f, this.height + 2, btnConfirm.width(), 16.0f);
            this.add(btnCancel);
            this.resize(this.width, (int)btnCancel.bottom());
        }
    }
}

