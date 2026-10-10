/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.noosa.Game;
import java.io.IOException;

public class WndGame
extends Window {
    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final int GAP = 2;
    private int pos;

    public WndGame() {
        RedButton curBtn = new RedButton(Messages.get(this, "settings", new Object[0])){

            @Override
            protected void onClick() {
                WndGame.this.hide();
                GameScene.show(new WndSettings());
            }
        };
        this.addButton(curBtn);
        curBtn.icon(Icons.get(Icons.PREFS));
        if (Dungeon.challenges > 0) {
            curBtn = new RedButton(Messages.get(this, "challenges", new Object[0])){

                @Override
                protected void onClick() {
                    WndGame.this.hide();
                    GameScene.show(new WndChallenges(Dungeon.challenges, false));
                }
            };
            this.addButton(curBtn);
            curBtn.icon(Icons.get(Icons.CHALLENGE_COLOR));
        }
        if (Dungeon.hero == null || !Dungeon.hero.isAlive()) {
            curBtn = new RedButton(Messages.get(this, "start", new Object[0])){

                @Override
                protected void onClick() {
                    GamesInProgress.selectedClass = Dungeon.hero.heroClass;
                    GamesInProgress.curSlot = GamesInProgress.firstEmpty();
                    ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
                }
            };
            this.addButton(curBtn);
            curBtn.icon(Icons.get(Icons.ENTER));
            curBtn.textColor(0xFFFF44);
            curBtn = new RedButton(Messages.get(this, "rankings", new Object[0])){

                @Override
                protected void onClick() {
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
                    Game.switchScene(RankingsScene.class);
                }
            };
            this.addButton(curBtn);
            curBtn.icon(Icons.get(Icons.RANKINGS));
        }
        curBtn = new RedButton(Messages.get(this, "menu", new Object[0])){

            @Override
            protected void onClick() {
                try {
                    Dungeon.saveAll();
                }
                catch (IOException e) {
                    ShatteredPixelDungeon.reportException(e);
                }
                Game.switchScene(TitleScene.class);
            }
        };
        this.addButton(curBtn);
        curBtn.icon(Icons.get(Icons.DISPLAY));
        if (SPDSettings.intro()) {
            curBtn.enable(false);
        }
        this.resize(120, this.pos);
    }

    private void addButton(RedButton btn) {
        this.add(btn);
        btn.setRect(0.0f, this.pos > 0 ? (float)(this.pos += 2) : 0.0f, 120.0f, 20.0f);
        this.pos += 20;
    }

    private void addButtons(RedButton btn1, RedButton btn2) {
        this.add(btn1);
        btn1.setRect(0.0f, this.pos > 0 ? (float)(this.pos += 2) : 0.0f, 59.0f, 20.0f);
        this.add(btn2);
        btn2.setRect(btn1.right() + 2.0f, btn1.top(), 120.0f - btn1.right() - 2.0f, 20.0f);
        this.pos += 20;
    }
}

