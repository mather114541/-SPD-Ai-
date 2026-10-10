/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;

public class WndResurrect
extends Window {
    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final float GAP = 2.0f;
    private static final float BTN_GAP = 10.0f;
    private static final int BTN_SIZE = 36;
    public static Object instance;
    private ItemButton btnItem1;
    private ItemButton btnItem2;
    private ItemButton btnPressed;
    RedButton btnContinue;
    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return Messages.get(WndResurrect.class, "prompt", new Object[0]);
        }

        @Override
        public boolean itemSelectable(Item item) {
            return !(item instanceof Ankh) && !(item instanceof Bag);
        }

        @Override
        public void onSelect(Item item) {
            if (item != null && WndResurrect.this.btnPressed.parent != null) {
                WndResurrect.this.btnPressed.item(item);
                if (WndResurrect.this.btnItem1.item() == WndResurrect.this.btnItem2.item()) {
                    if (WndResurrect.this.btnPressed == WndResurrect.this.btnItem1) {
                        WndResurrect.this.btnItem2.clear();
                    } else {
                        WndResurrect.this.btnItem1.clear();
                    }
                }
            }
        }
    };

    public WndResurrect(final Ankh ankh) {
        instance = this;
        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(ankh.image(), null));
        titlebar.label(Messages.titleCase(Messages.get(this, "title", new Object[0])));
        titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(titlebar);
        RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message", new Object[0]), 6);
        message.maxWidth(120);
        message.setPos(0.0f, titlebar.bottom() + 2.0f);
        this.add(message);
        this.btnItem1 = new ItemButton(){

            @Override
            protected void onClick() {
                WndResurrect.this.btnPressed = WndResurrect.this.btnItem1;
                GameScene.selectItem(WndResurrect.this.itemSelector);
            }
        };
        this.btnItem1.item(Dungeon.hero.belongings.weapon());
        this.btnItem1.setRect(19.0f, message.bottom() + 10.0f, 36.0f, 36.0f);
        this.add(this.btnItem1);
        this.btnItem2 = new ItemButton(){

            @Override
            protected void onClick() {
                WndResurrect.this.btnPressed = WndResurrect.this.btnItem2;
                GameScene.selectItem(WndResurrect.this.itemSelector);
            }
        };
        this.btnItem2.item(Dungeon.hero.belongings.armor());
        this.btnItem2.setRect(this.btnItem1.right() + 10.0f, this.btnItem1.top(), 36.0f, 36.0f);
        this.add(this.btnItem2);
        this.btnContinue = new RedButton(Messages.get(this, "confirm", new Object[0])){

            @Override
            protected void onClick() {
                if (WndResurrect.this.btnItem1.item() == null || WndResurrect.this.btnItem2.item() == null) {
                    GameScene.show(new WndOptions(Icons.WARNING.get(), Messages.get(WndResurrect.class, "warn_title", new Object[0]), Messages.get(WndResurrect.class, "warn_body", new Object[0]), new String[]{Messages.get(WndResurrect.class, "warn_yes", new Object[0]), Messages.get(WndResurrect.class, "warn_no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                WndResurrect.this.resurrect(ankh);
                            }
                        }
                    });
                } else {
                    WndResurrect.this.resurrect(ankh);
                }
            }
        };
        this.btnContinue.setRect(0.0f, this.btnItem1.bottom() + 10.0f, 120.0f, 20.0f);
        this.add(this.btnContinue);
        this.resize(120, (int)this.btnContinue.bottom());
    }

    private void resurrect(Ankh ankh) {
        this.hide();
        ++Statistics.ankhsUsed;
        Catalog.countUse(Ankh.class);
        ankh.detach(Dungeon.hero.belongings.backpack);
        if (this.btnItem1.item() != null) {
            this.btnItem1.item().keptThoughLostInvent = true;
        }
        if (this.btnItem2.item() != null) {
            this.btnItem2.item().keptThoughLostInvent = true;
        }
        InterlevelScene.mode = InterlevelScene.Mode.RESURRECT;
        Game.switchScene(InterlevelScene.class);
    }

    @Override
    public void destroy() {
        super.destroy();
        instance = null;
    }

    @Override
    public void onBackPressed() {
    }
}

