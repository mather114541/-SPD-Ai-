/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDailies;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRanking;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndVictoryCongrats;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.RectF;

public class RankingsScene
extends PixelScene {
    private static final float ROW_HEIGHT_MAX = 20.0f;
    private static final float ROW_HEIGHT_MIN = 12.0f;
    private static final float MAX_ROW_WIDTH = 160.0f;
    private static final float GAP = 4.0f;

    @Override
    public void create() {
        float left;
        super.create();
        Music.INSTANCE.playTracks(new String[]{"music/theme_1.ogg", "music/theme_2.ogg"}, new float[]{1.0f, 1.0f}, false);
        RankingsScene.uiCamera.visible = false;
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        this.add(BG);
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        Rankings.INSTANCE.load();
        IconTitle title = new IconTitle(Icons.RANKINGS.get(), Messages.get(this, "title", new Object[0]));
        title.setSize(200.0f, 0.0f);
        title.setPos(insets.left + ((float)w - title.reqWidth()) / 2.0f, insets.top + (20.0f - title.height()) / 2.0f);
        RankingsScene.align(title);
        this.add(title);
        if (Rankings.INSTANCE.records.size() > 0) {
            float rowHeight = GameMath.gate(12.0f, (h - 26) / Rankings.INSTANCE.records.size(), 20.0f);
            left = ((float)w - Math.min(160.0f, (float)w)) / 2.0f + 4.0f;
            float top = ((float)h - rowHeight * (float)Rankings.INSTANCE.records.size()) / 2.0f;
            int pos = 0;
            for (Rankings.Record rec : Rankings.INSTANCE.records) {
                Record row = new Record(pos, pos == Rankings.INSTANCE.lastRecord, rec);
                float offset = 0.0f;
                if (rowHeight <= 14.0f) {
                    offset = pos % 2 == 1 ? 5.0f : -5.0f;
                }
                row.setRect(insets.left + left + offset, insets.top + top + (float)pos * rowHeight, (float)w - left * 2.0f, rowHeight);
                this.add(row);
                ++pos;
            }
            if (Rankings.INSTANCE.totalNumber >= 11) {
                RenderedTextBlock label = PixelScene.renderTextBlock(8);
                label.hardlight(0xCCCCCC);
                label.setHightlighting(true, 0x33BB33);
                label.text(Messages.get(this, "total", new Object[0]) + " _" + Rankings.INSTANCE.wonNumber + "_/" + Rankings.INSTANCE.totalNumber);
                this.add(label);
                label.setPos(insets.left + ((float)w - label.width()) / 2.0f, insets.top + (float)h - label.height() - 8.0f);
                RankingsScene.align(label);
            }
        } else {
            RenderedTextBlock noRec = PixelScene.renderTextBlock(Messages.get(this, "no_games", new Object[0]), 8);
            noRec.hardlight(0xCCCCCC);
            noRec.setPos(insets.left + ((float)w - noRec.width()) / 2.0f, insets.top + ((float)h - noRec.height()) / 2.0f);
            RankingsScene.align(noRec);
            this.add(noRec);
        }
        ExitButton btnExit = new ExitButton();
        btnExit.setPos((float)Camera.main.width - btnExit.width() - insets.right, insets.top);
        this.add(btnExit);
        left = insets.left + (float)(PixelScene.landscape() && !DeviceCompat.isDesktop() ? 10 : 0);
        if (Rankings.INSTANCE.latestDaily != null) {
            IconButton btnDailies = new IconButton(Icons.CALENDAR.get()){

                @Override
                protected void onClick() {
                    ShatteredPixelDungeon.scene().addToFront(new WndDailies());
                }

                @Override
                protected void onPointerUp() {
                    this.icon.hardlight(0.5f, 1.0f, 2.0f);
                }
            };
            btnDailies.icon().hardlight(0.5f, 1.0f, 2.0f);
            btnDailies.setRect(left, insets.top, 16.0f, 20.0f);
            left += 16.0f;
            this.add(btnDailies);
        }
        if (Dungeon.daily) {
            this.addToFront(new WndDailies());
        } else if (Badges.isUnlocked(Badges.Badge.VICTORY) && !SPDSettings.victoryNagged()) {
            SPDSettings.victoryNagged(true);
            this.add(new WndVictoryCongrats());
        }
        this.fadeIn();
    }

    @Override
    public void destroy() {
        super.destroy();
        Dungeon.dailyReplay = false;
        Dungeon.daily = false;
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }

    public static class Record
    extends Button {
        private static final float GAP = 4.0f;
        private static final int[] TEXT_WIN = new int[]{0xFFFF88, 11711071};
        private static final int[] TEXT_LOSE = new int[]{0xDDDDDD, 0x888888};
        private static final int FLARE_WIN = 0x888866;
        private static final int FLARE_LOSE = 0x666666;
        private Rankings.Record rec;
        protected Image shield;
        private Flare flare;
        private BitmapText position;
        private RenderedTextBlock desc;
        private Image steps;
        private BitmapText depth;
        private Image classIcon;
        private BitmapText level;

        public Record(int pos, boolean latest, Rankings.Record rec) {
            this.rec = rec;
            if (latest) {
                this.flare = new Flare(6, 24.0f);
                this.flare.angularSpeed = 90.0f;
                this.flare.color(rec.win ? 0x888866 : 0x666666);
                this.addToBack(this.flare);
            }
            if (pos != 10) {
                this.position.text(Integer.toString(pos + 1));
            } else {
                this.position.text(" ");
            }
            this.position.measure();
            this.desc.text(Messages.titleCase(rec.desc()));
            int odd = pos % 2;
            if (rec.win) {
                this.shield.copy(new ItemSprite(ItemSpriteSheet.AMULET, null));
                this.position.hardlight(TEXT_WIN[odd]);
                this.desc.hardlight(TEXT_WIN[odd]);
                this.depth.hardlight(TEXT_WIN[odd]);
                this.level.hardlight(TEXT_WIN[odd]);
            } else {
                this.position.hardlight(TEXT_LOSE[odd]);
                this.desc.hardlight(TEXT_LOSE[odd]);
                this.depth.hardlight(TEXT_LOSE[odd]);
                this.level.hardlight(TEXT_LOSE[odd]);
                if (rec.depth != 0) {
                    this.depth.text(Integer.toString(rec.depth));
                    this.depth.measure();
                    this.steps.copy(Icons.STAIRS.get());
                    this.add(this.steps);
                    this.add(this.depth);
                }
                if (rec.ascending) {
                    this.shield.copy(new ItemSprite(ItemSpriteSheet.AMULET, null));
                    this.shield.hardlight(0.4f, 0.4f, 0.7f);
                }
            }
            if (rec.daily) {
                this.shield.copy(Icons.get(Icons.CALENDAR));
                this.shield.hardlight(0.5f, 1.0f, 2.0f);
            } else if (!rec.customSeed.isEmpty()) {
                this.shield.copy(Icons.get(Icons.SEED));
                this.shield.hardlight(1.0f, 1.5f, 0.67f);
            }
            if (rec.herolevel != 0) {
                this.level.text(Integer.toString(rec.herolevel));
                this.level.measure();
                this.add(this.level);
            }
            this.classIcon.copy(Icons.get(rec.heroClass));
            if (rec.heroClass == HeroClass.ROGUE) {
                this.classIcon.brightness(2.0f);
            }
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.shield = new Image(new ItemSprite(ItemSpriteSheet.TOMB, null));
            this.add(this.shield);
            this.position = new BitmapText(PixelScene.pixelFont);
            this.add(this.position);
            this.desc = PixelScene.renderTextBlock(7);
            this.add(this.desc);
            this.depth = new BitmapText(PixelScene.pixelFont);
            this.steps = new Image();
            this.classIcon = new Image();
            this.add(this.classIcon);
            this.level = new BitmapText(PixelScene.pixelFont);
        }

        @Override
        protected void layout() {
            super.layout();
            this.shield.x = this.x + (16.0f - this.shield.width) / 2.0f;
            this.shield.y = this.y + (this.height - this.shield.height) / 2.0f;
            PixelScene.align(this.shield);
            this.position.x = this.shield.x + (this.shield.width - this.position.width()) / 2.0f;
            this.position.y = this.shield.y + (this.shield.height - this.position.height()) / 2.0f + 1.0f;
            PixelScene.align(this.position);
            if (this.flare != null) {
                this.flare.point(this.shield.center());
            }
            this.classIcon.x = this.x + this.width - 16.0f + (16.0f - this.classIcon.width()) / 2.0f;
            this.classIcon.y = this.shield.y + (16.0f - this.classIcon.height()) / 2.0f;
            PixelScene.align(this.classIcon);
            this.level.x = this.classIcon.x + (this.classIcon.width - this.level.width()) / 2.0f;
            this.level.y = this.classIcon.y + (this.classIcon.height - this.level.height()) / 2.0f + 1.0f;
            PixelScene.align(this.level);
            this.steps.x = this.x + this.width - 32.0f + (16.0f - this.steps.width()) / 2.0f;
            this.steps.y = this.shield.y + (16.0f - this.steps.height()) / 2.0f;
            PixelScene.align(this.steps);
            this.depth.x = this.steps.x + (this.steps.width - this.depth.width()) / 2.0f;
            this.depth.y = this.steps.y + (this.steps.height - this.depth.height()) / 2.0f + 1.0f;
            PixelScene.align(this.depth);
            this.desc.maxWidth((int)(this.steps.x - (this.x + 16.0f + 4.0f)));
            this.desc.setPos(this.x + 16.0f + 4.0f, this.shield.y + (this.shield.height - this.desc.height()) / 2.0f + 1.0f);
            PixelScene.align(this.desc);
        }

        @Override
        protected void onClick() {
            this.parent.add(new WndRanking(this.rec));
        }
    }
}

