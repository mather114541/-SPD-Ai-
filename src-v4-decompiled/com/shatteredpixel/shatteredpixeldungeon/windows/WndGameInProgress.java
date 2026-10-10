/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import java.util.Locale;

public class WndGameInProgress
extends Window {
    private static final int WIDTH = 120;
    private int GAP = 6;
    private float pos;

    public WndGameInProgress(final int slot) {
        final GamesInProgress.Info info = GamesInProgress.check(slot);
        String className = null;
        className = info.subClass != HeroSubClass.NONE ? info.subClass.title() : info.heroClass.title();
        IconTitle title = new IconTitle();
        title.icon(HeroSprite.avatar(info.heroClass, info.armorTier));
        title.label(Messages.get(this, "title", info.level, className).toUpperCase(Locale.ENGLISH));
        title.color(0xFFFF44);
        title.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(title);
        if (info.challenges > 0) {
            this.GAP -= 2;
        }
        this.pos = title.bottom() + (float)this.GAP;
        if (info.challenges > 0) {
            RedButton btnChallenges = new RedButton(Messages.get(this, "challenges", new Object[0])){

                @Override
                protected void onClick() {
                    Game.scene().add(new WndChallenges(info.challenges, false));
                }
            };
            btnChallenges.icon(Icons.get(Icons.CHALLENGE_COLOR));
            float btnW = btnChallenges.reqWidth() + 2.0f;
            btnChallenges.setRect((120.0f - btnW) / 2.0f, this.pos, btnW, 18.0f);
            this.add(btnChallenges);
            this.pos = btnChallenges.bottom() + (float)this.GAP;
        }
        this.pos += (float)this.GAP;
        int strBonus = info.strBonus;
        if (strBonus > 0) {
            this.statSlot(Messages.get(this, "str", new Object[0]), info.str + " + " + strBonus);
        } else if (strBonus < 0) {
            this.statSlot(Messages.get(this, "str", new Object[0]), info.str + " - " + -strBonus);
        } else {
            this.statSlot(Messages.get(this, "str", new Object[0]), info.str);
        }
        if (info.shld > 0) {
            this.statSlot(Messages.get(this, "health", new Object[0]), info.hp + "+" + info.shld + "/" + info.ht);
        } else {
            this.statSlot(Messages.get(this, "health", new Object[0]), info.hp + "/" + info.ht);
        }
        this.statSlot(Messages.get(this, "exp", new Object[0]), info.exp + "/" + Hero.maxExp(info.level));
        this.pos += (float)this.GAP;
        this.statSlot(Messages.get(this, "gold", new Object[0]), info.goldCollected);
        this.statSlot(Messages.get(this, "depth", new Object[0]), info.maxDepth);
        if (info.daily) {
            if (info.dailyReplay) {
                this.statSlot(Messages.get(this, "replay_for", new Object[0]), "_" + info.customSeed + "_");
            } else {
                this.statSlot(Messages.get(this, "daily_for", new Object[0]), "_" + info.customSeed + "_");
            }
        } else if (!info.customSeed.isEmpty()) {
            this.statSlot(Messages.get(this, "custom_seed", new Object[0]), "_" + info.customSeed + "_");
        } else {
            this.statSlot(Messages.get(this, "dungeon_seed", new Object[0]), DungeonSeed.convertToCode(info.seed));
        }
        this.pos += (float)this.GAP;
        RedButton cont = new RedButton(Messages.get(this, "continue", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                GamesInProgress.curSlot = slot;
                Dungeon.hero = null;
                Dungeon.dailyReplay = false;
                Dungeon.daily = false;
                ActionIndicator.clearAction();
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                ShatteredPixelDungeon.switchScene(InterlevelScene.class);
            }
        };
        RedButton erase = new RedButton(Messages.get(this, "erase", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                ShatteredPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING), Messages.get(WndGameInProgress.class, "erase_warn_title", new Object[0]), Messages.get(WndGameInProgress.class, "erase_warn_body", new Object[0]), new String[]{Messages.get(WndGameInProgress.class, "erase_warn_yes", new Object[0]), Messages.get(WndGameInProgress.class, "erase_warn_no", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            Dungeon.deleteGame(slot, true);
                            ShatteredPixelDungeon.switchNoFade(StartScene.class);
                        }
                    }
                });
            }
        };
        cont.icon(Icons.get(Icons.ENTER));
        cont.setRect(0.0f, this.pos, 59.0f, 20.0f);
        this.add(cont);
        erase.icon(Icons.get(Icons.CLOSE));
        erase.setRect(61.0f, this.pos, 59.0f, 20.0f);
        this.add(erase);
        this.resize(120, (int)cont.bottom() + 1);
    }

    private void statSlot(String label, String value) {
        RenderedTextBlock txt;
        int size = 8;
        do {
            txt = PixelScene.renderTextBlock(label, size);
            --size;
        } while (txt.width() >= 66.0f);
        txt.setPos(0.0f, this.pos + (6.0f - txt.height()) / 2.0f);
        PixelScene.align(txt);
        this.add(txt);
        size = 8;
        do {
            txt = PixelScene.renderTextBlock(value, size);
            --size;
        } while (txt.width() >= 54.0f);
        txt.setPos(66.0f, this.pos + (6.0f - txt.height()) / 2.0f);
        PixelScene.align(txt);
        this.add(txt);
        this.pos += (float)this.GAP + txt.height();
    }

    private void statSlot(String label, int value) {
        this.statSlot(label, Integer.toString(value));
    }
}

