/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;

public class WndSadGhost
extends Window {
    private static final int WIDTH = 120;
    private static final int BTN_SIZE = 32;
    private static final int BTN_GAP = 5;
    private static final int GAP = 2;
    Ghost ghost;

    public WndSadGhost(Ghost ghost, int type) {
        RenderedTextBlock message;
        this.ghost = ghost;
        IconTitle titlebar = new IconTitle();
        switch (type) {
            default: {
                titlebar.icon(new FetidRatSprite());
                titlebar.label(Messages.get(this, "rat_title", new Object[0]));
                message = PixelScene.renderTextBlock(Messages.get(this, "rat", new Object[0]) + "\n\n" + Messages.get(this, "give_item", new Object[0]), 6);
                break;
            }
            case 2: {
                titlebar.icon(new GnollTricksterSprite());
                titlebar.label(Messages.get(this, "gnoll_title", new Object[0]));
                message = PixelScene.renderTextBlock(Messages.get(this, "gnoll", new Object[0]) + "\n\n" + Messages.get(this, "give_item", new Object[0]), 6);
                break;
            }
            case 3: {
                titlebar.icon(new GreatCrabSprite());
                titlebar.label(Messages.get(this, "crab_title", new Object[0]));
                message = PixelScene.renderTextBlock(Messages.get(this, "crab", new Object[0]) + "\n\n" + Messages.get(this, "give_item", new Object[0]), 6);
            }
        }
        titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(titlebar);
        message.maxWidth(120);
        message.setPos(0.0f, titlebar.bottom() + 2.0f);
        this.add(message);
        ItemButton btnWeapon = new ItemButton(){

            @Override
            protected void onClick() {
                GameScene.show(new RewardWindow(this.item()));
            }
        };
        btnWeapon.item(Ghost.Quest.weapon);
        btnWeapon.setRect(25.0f, message.top() + message.height() + 5.0f, 32.0f, 32.0f);
        this.add(btnWeapon);
        ItemButton btnArmor = new ItemButton(){

            @Override
            protected void onClick() {
                GameScene.show(new RewardWindow(this.item()));
            }
        };
        btnArmor.item(Ghost.Quest.armor);
        btnArmor.setRect(btnWeapon.right() + 5.0f, btnWeapon.top(), 32.0f, 32.0f);
        this.add(btnArmor);
        this.resize(120, (int)btnArmor.bottom() + 5);
    }

    private void selectReward(Item reward) {
        this.hide();
        if (reward == null) {
            return;
        }
        if (reward instanceof Weapon && Ghost.Quest.enchant != null) {
            ((Weapon)reward).enchant(Ghost.Quest.enchant);
        } else if (reward instanceof Armor && Ghost.Quest.glyph != null) {
            ((Armor)reward).inscribe(Ghost.Quest.glyph);
        }
        reward.identify(false);
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())), new Object[0]);
        } else {
            Dungeon.level.drop((Item)reward, (int)this.ghost.pos).sprite.drop();
        }
        this.ghost.yell(Messages.get(this, "farewell", new Object[0]));
        this.ghost.die(null);
        Ghost.Quest.complete();
    }

    private class RewardWindow
    extends WndInfoItem {
        public RewardWindow(final Item item) {
            super(item);
            RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm", new Object[0])){

                @Override
                protected void onClick() {
                    RewardWindow.this.hide();
                    WndSadGhost.this.selectReward(item);
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

