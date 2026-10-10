/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

public class AmuletScene
extends PixelScene {
    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final float SMALL_GAP = 2.0f;
    private static final float LARGE_GAP = 8.0f;
    public static boolean noText = false;
    private Image amulet;
    StyledButton btnExit;
    StyledButton btnStay;
    private float timer;

    public AmuletScene() {
        this.inGameScene = true;
        this.btnExit = null;
        this.btnStay = null;
        this.timer = 0.0f;
    }

    @Override
    public void create() {
        super.create();
        RenderedTextBlock text = null;
        if (!noText) {
            text = AmuletScene.renderTextBlock(Messages.get(this, "text", new Object[0]), 8);
            text.maxWidth(PixelScene.landscape() ? 236 : 120);
        }
        this.amulet = new Image("sprites/amulet.png");
        this.add(this.amulet);
        this.btnExit = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "exit", new Object[0])){

            @Override
            protected void onClick() {
                Dungeon.win(Amulet.class);
                Dungeon.deleteGame(GamesInProgress.curSlot, true);
                Badges.saveGlobal();
                AmuletScene.this.btnExit.enable(false);
                AmuletScene.this.btnStay.enable(false);
                AmuletScene.this.add(new Delayer(0.1f){

                    @Override
                    protected void onComplete() {
                        if (BadgeBanner.isShowingBadges()) {
                            AmuletScene.this.add(new Delayer(3.0f){

                                @Override
                                protected void onComplete() {
                                    Game.switchScene(RankingsScene.class);
                                }
                            });
                        } else {
                            Game.switchScene(RankingsScene.class);
                        }
                    }
                });
                Music.INSTANCE.playTracks(new String[]{"music/theme_2.ogg", "music/theme_1.ogg"}, new float[]{1.0f, 1.0f}, false);
            }
        };
        this.btnExit.icon(new ItemSprite(ItemSpriteSheet.AMULET));
        this.btnExit.setSize(120.0f, 20.0f);
        this.add(this.btnExit);
        this.btnStay = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "stay", new Object[0])){

            @Override
            protected void onClick() {
                AmuletScene.this.onBackPressed();
                AmuletScene.this.btnExit.enable(false);
                AmuletScene.this.btnStay.enable(false);
            }
        };
        this.btnStay.icon(Icons.CLOSE.get());
        this.btnStay.setSize(120.0f, 20.0f);
        this.add(this.btnStay);
        RectF insets = this.getCommonInsets();
        int w = (int)((float)Camera.main.width - insets.left + insets.right);
        int h = (int)((float)Camera.main.height - insets.top + insets.bottom);
        if (noText) {
            float height = this.amulet.height + 8.0f + this.btnExit.height() + 2.0f + this.btnStay.height();
            this.amulet.x = insets.left + ((float)w - this.amulet.width) / 2.0f;
            this.amulet.y = insets.top + ((float)h - height) / 2.0f;
            AmuletScene.align(this.amulet);
            this.btnExit.setPos(insets.left + ((float)w - this.btnExit.width()) / 2.0f, this.amulet.y + this.amulet.height + 8.0f);
            this.btnStay.setPos(this.btnExit.left(), this.btnExit.bottom() + 2.0f);
        } else {
            float height = this.amulet.height + 8.0f + text.height() + 8.0f + this.btnExit.height() + 2.0f + this.btnStay.height();
            this.amulet.x = insets.left + ((float)w - this.amulet.width) / 2.0f;
            this.amulet.y = insets.top + ((float)h - height) / 2.0f;
            AmuletScene.align(this.amulet);
            text.setPos(insets.left + ((float)w - text.width()) / 2.0f, this.amulet.y + this.amulet.height + 8.0f);
            AmuletScene.align(text);
            this.add(text);
            this.btnExit.setPos(insets.left + ((float)w - this.btnExit.width()) / 2.0f, text.top() + text.height() + 8.0f);
            this.btnStay.setPos(this.btnExit.left(), this.btnExit.bottom() + 2.0f);
        }
        new Flare((int)8, (float)48.0f).color((int)0xFFDDBB, (boolean)true).show((Visual)this.amulet, (float)0.0f).angularSpeed = 30.0f;
        this.fadeIn();
    }

    @Override
    protected void onBackPressed() {
        if (this.btnExit.isActive()) {
            InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
            Game.switchScene(InterlevelScene.class);
        }
    }

    @Override
    public void update() {
        float f;
        super.update();
        this.timer -= Game.elapsed;
        if (f < 0.0f) {
            this.timer = Random.Float(0.5f, 5.0f);
            Speck star = (Speck)this.recycle(Speck.class);
            star.reset(0, this.amulet.x + 10.5f, this.amulet.y + 5.5f, 101);
            this.add(star);
        }
    }
}

