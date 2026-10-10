/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.RectF;
import com.watabou.utils.SparseArray;

public class JournalScene
extends PixelScene {
    public static final int WIDTH_P = 126;
    public static final int WIDTH_L = 216;
    private static int lastIDX = 0;

    @Override
    public void create() {
        super.create();
        Dungeon.hero = null;
        Badges.loadGlobal();
        Journal.loadGlobal();
        Potion.clearColors();
        Scroll.clearLabels();
        Ring.clearGems();
        new TerrainFeaturesTilemap(new SparseArray<Plant>(), new SparseArray<Trap>());
        Music.INSTANCE.playTracks(new String[]{"music/theme_1.ogg", "music/theme_2.ogg"}, new float[]{1.0f, 1.0f}, false);
        JournalScene.uiCamera.visible = false;
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        float top = 20.0f;
        IconTitle title = new IconTitle(Icons.JOURNAL.get(), Messages.get(this, "title", new Object[0]));
        title.setSize(200.0f, 0.0f);
        title.setPos(insets.left + ((float)w - title.reqWidth()) / 2.0f, insets.top + (top - title.height()) / 2.0f);
        JournalScene.align(title);
        this.add(title);
        NinePatch panel = Chrome.get(Chrome.Type.TOAST);
        int pw = (JournalScene.landscape() ? 216 : 126) + panel.marginHor();
        int ph = h - 50 + panel.marginVer();
        panel.size(pw, ph);
        panel.x = insets.left + (float)(w - pw) / 2.0f;
        panel.y = insets.top + top;
        this.add(panel);
        switch (lastIDX) {
            default: {
                WndJournal.BadgesTab badges = new WndJournal.BadgesTab();
                this.add(badges);
                badges.setRect(panel.x + (float)panel.marginLeft(), panel.y + (float)panel.marginTop(), panel.width() - (float)panel.marginHor(), panel.height() - (float)panel.marginVer());
                break;
            }
            case 1: {
                WndJournal.CatalogTab catalog = new WndJournal.CatalogTab();
                this.add(catalog);
                catalog.setRect(panel.x + (float)panel.marginLeft(), panel.y + (float)panel.marginTop(), panel.width() - (float)panel.marginHor(), panel.height() - (float)panel.marginVer());
                catalog.updateList();
                break;
            }
            case 2: {
                WndJournal.GuideTab guidebook = new WndJournal.GuideTab();
                this.add(guidebook);
                guidebook.setRect(panel.x + (float)panel.marginLeft(), panel.y + (float)panel.marginTop(), panel.width() - (float)panel.marginHor(), panel.height() - (float)panel.marginVer());
                guidebook.updateList();
                break;
            }
            case 3: {
                WndJournal.AlchemyTab alchemy = new WndJournal.AlchemyTab();
                this.add(alchemy);
                alchemy.setRect(panel.x + (float)panel.marginLeft(), panel.y + (float)panel.marginTop(), panel.width() - (float)panel.marginHor(), panel.height() - (float)panel.marginVer());
            }
        }
        StyledButton btnBadges = new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){

            @Override
            protected void onClick() {
                if (lastIDX != 0) {
                    lastIDX = 0;
                }
                ShatteredPixelDungeon.seamlessResetScene();
                super.onClick();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.BadgesTab.class, "title", new Object[0]);
            }
        };
        btnBadges.icon(Icons.BADGES.get());
        btnBadges.setRect(panel.x, panel.y + (float)ph - 3.0f, (float)pw / 4.0f + 1.5f, lastIDX == 0 ? 25.0f : 20.0f);
        JournalScene.align(btnBadges);
        if (lastIDX != 0) {
            btnBadges.icon().brightness(0.6f);
        }
        this.addToBack(btnBadges);
        StyledButton btnCatalog = new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){

            @Override
            protected void onClick() {
                if (lastIDX != 1) {
                    lastIDX = 1;
                }
                ShatteredPixelDungeon.seamlessResetScene();
                super.onClick();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.CatalogTab.class, "title", new Object[0]);
            }
        };
        btnCatalog.icon(Icons.CATALOG.get());
        btnCatalog.setRect(btnBadges.right() - 2.0f, btnBadges.top(), (float)pw / 4.0f + 1.5f, lastIDX == 1 ? 25.0f : 20.0f);
        JournalScene.align(btnCatalog);
        if (lastIDX != 1) {
            btnCatalog.icon().brightness(0.6f);
        }
        this.addToBack(btnCatalog);
        StyledButton btnGuide = new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){

            @Override
            protected void onClick() {
                if (lastIDX != 2) {
                    lastIDX = 2;
                }
                ShatteredPixelDungeon.seamlessResetScene();
                super.onClick();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.GuideTab.class, "title", new Object[0]);
            }
        };
        btnGuide.icon(new ItemSprite(ItemSpriteSheet.MASTERY));
        btnGuide.setRect(btnCatalog.right() - 2.0f, btnBadges.top(), (float)pw / 4.0f + 1.5f, lastIDX == 2 ? 25.0f : 20.0f);
        JournalScene.align(btnGuide);
        if (lastIDX != 2) {
            btnGuide.icon().brightness(0.6f);
        }
        this.addToBack(btnGuide);
        StyledButton btnAlchemy = new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){

            @Override
            protected void onClick() {
                if (lastIDX != 3) {
                    lastIDX = 3;
                }
                ShatteredPixelDungeon.seamlessResetScene();
                super.onClick();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.AlchemyTab.class, "title", new Object[0]);
            }
        };
        btnAlchemy.icon(Icons.ALCHEMY.get());
        btnAlchemy.setRect(btnGuide.right() - 2.0f, btnBadges.top(), (float)pw / 4.0f + 1.5f, lastIDX == 3 ? 25.0f : 20.0f);
        JournalScene.align(btnAlchemy);
        if (lastIDX != 3) {
            btnAlchemy.icon().brightness(0.6f);
        }
        this.addToBack(btnAlchemy);
        this.addToBack(BG);
        ExitButton btnExit = new ExitButton();
        btnExit.setPos(insets.left + (float)w - btnExit.width(), insets.top);
        this.add(btnExit);
        this.fadeIn();
    }

    @Override
    public void destroy() {
        Badges.saveGlobal();
        super.destroy();
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }
}

