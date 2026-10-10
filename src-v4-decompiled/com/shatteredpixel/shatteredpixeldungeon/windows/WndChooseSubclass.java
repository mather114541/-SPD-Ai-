/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoSubclass;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

public class WndChooseSubclass
extends Window {
    private static final int WIDTH = 130;
    private static final float GAP = 2.0f;

    public WndChooseSubclass(final TengusMask tome, final Hero hero) {
        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(tome.image(), null));
        titlebar.label(tome.name());
        titlebar.setRect(0.0f, 0.0f, 114.0f, 0.0f);
        this.add(titlebar);
        IconButton random = new IconButton(Icons.SHUFFLE.get()){

            @Override
            protected void onClick() {
                super.onClick();
                GameScene.show(new WndOptions(Icons.SHUFFLE.get(), Messages.get(WndChooseSubclass.class, "random_title", new Object[0]), Messages.get(WndChooseSubclass.class, "random_sure", new Object[0]), new String[]{Messages.get(WndChooseSubclass.class, "yes", new Object[0]), Messages.get(WndChooseSubclass.class, "no", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            WndChooseSubclass.this.hide();
                            HeroSubClass cls = Random.oneOf(hero.heroClass.subClasses());
                            tome.choose(cls);
                            GameScene.show(new WndInfoSubclass(hero.heroClass, cls));
                        }
                    }
                });
            }

            @Override
            public void update() {
                if (Statistics.qualifiedForRandomVictoryBadge) {
                    this.icon.tint(1.0f, 1.0f, 1.0f, (float)Math.abs(Math.cos(4.71238898038469 * (double)Game.timeTotal) / 2.0));
                }
                super.update();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndChooseSubclass.class, "random_title", new Object[0]);
            }
        };
        random.setRect(114.0f, 0.0f, 16.0f, 16.0f);
        this.add(random);
        RenderedTextBlock message = PixelScene.renderTextBlock(6);
        message.text(Messages.get(this, "message", new Object[0]), 130);
        message.setPos(titlebar.left(), titlebar.bottom() + 2.0f);
        this.add(message);
        float pos = message.bottom() + 6.0f;
        for (final HeroSubClass subCls : hero.heroClass.subClasses()) {
            RedButton btnCls = new RedButton(subCls.shortDesc(), 6){

                @Override
                protected void onClick() {
                    GameScene.show(new WndOptions(new HeroIcon(subCls), Messages.titleCase(subCls.title()), Messages.get(WndChooseSubclass.this, "are_you_sure", new Object[0]), new String[]{Messages.get(WndChooseSubclass.this, "yes", new Object[0]), Messages.get(WndChooseSubclass.this, "no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            this.hide();
                            if (index == 0 && WndChooseSubclass.this.parent != null) {
                                WndChooseSubclass.this.hide();
                                tome.choose(subCls);
                                Statistics.qualifiedForRandomVictoryBadge = false;
                            }
                        }
                    });
                }
            };
            btnCls.leftJustify = true;
            btnCls.multiline = true;
            btnCls.setSize(110.0f, btnCls.reqHeight() + 2.0f);
            btnCls.setRect(0.0f, pos, 110.0f, btnCls.reqHeight() + 2.0f);
            this.add(btnCls);
            IconButton clsInfo = new IconButton(Icons.get(Icons.INFO)){

                @Override
                protected void onClick() {
                    GameScene.show(new WndInfoSubclass(Dungeon.hero.heroClass, subCls));
                }
            };
            clsInfo.setRect(110.0f, btnCls.top() + (btnCls.height() - 20.0f) / 2.0f, 20.0f, 20.0f);
            this.add(clsInfo);
            pos = btnCls.bottom() + 2.0f;
        }
        RedButton btnCancel = new RedButton(Messages.get(this, "cancel", new Object[0])){

            @Override
            protected void onClick() {
                WndChooseSubclass.this.hide();
            }
        };
        btnCancel.setRect(0.0f, pos, 130.0f, 18.0f);
        this.add(btnCancel);
        this.resize(130, (int)btnCancel.bottom());
    }
}

