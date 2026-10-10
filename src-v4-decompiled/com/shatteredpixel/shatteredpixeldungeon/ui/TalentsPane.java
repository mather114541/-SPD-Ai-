/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TalentsPane
extends ScrollPane {
    ArrayList<TalentTierPane> panes = new ArrayList();
    ArrayList<ColorBlock> separators = new ArrayList();
    ColorBlock sep;
    ColorBlock blocker;
    RenderedTextBlock blockText;

    public TalentsPane(TalentButton.Mode mode) {
        this(mode, Dungeon.hero.talents);
    }

    public TalentsPane(TalentButton.Mode mode, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        super(new Component());
        int i;
        int tiersAvailable;
        Ratmogrify.useRatroicEnergy = Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify;
        if (mode == TalentButton.Mode.INFO) {
            tiersAvailable = !Badges.isUnlocked(Badges.Badge.LEVEL_REACHED_1) ? 1 : (!Badges.isUnlocked(Badges.Badge.LEVEL_REACHED_2) || !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_2) ? 2 : (!Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_4) ? 3 : 4));
        } else {
            for (tiersAvailable = 1; tiersAvailable < 4 && Dungeon.hero.lvl + 1 >= Talent.tierLevelThresholds[tiersAvailable + 1]; ++tiersAvailable) {
            }
            if (tiersAvailable > 2 && Dungeon.hero.subClass == HeroSubClass.NONE) {
                tiersAvailable = 2;
            } else if (tiersAvailable > 3 && Dungeon.hero.armorAbility == null) {
                tiersAvailable = 3;
            }
        }
        tiersAvailable = Math.min(tiersAvailable, talents.size());
        for (i = 0; i < Math.min(tiersAvailable, talents.size()); ++i) {
            if (talents.get(i).isEmpty()) continue;
            TalentTierPane pane = new TalentTierPane(talents.get(i), i + 1, mode);
            this.panes.add(pane);
            this.content.add(pane);
            ColorBlock sep = new ColorBlock(0.0f, 1.0f, -16777216);
            this.separators.add(sep);
            this.content.add(sep);
        }
        this.sep = new ColorBlock(0.0f, 1.0f, -16777216);
        this.content.add(this.sep);
        this.blocker = new ColorBlock(0.0f, 0.0f, -14540254);
        this.content.add(this.blocker);
        if (tiersAvailable == 1) {
            this.blockText = PixelScene.renderTextBlock(Messages.get(this, "unlock_tier2", new Object[0]), 6);
            this.content.add(this.blockText);
        } else if (tiersAvailable == 2) {
            this.blockText = PixelScene.renderTextBlock(Messages.get(this, "unlock_tier3", new Object[0]), 6);
            this.content.add(this.blockText);
        } else if (tiersAvailable == 3) {
            this.blockText = PixelScene.renderTextBlock(Messages.get(this, "unlock_tier4", new Object[0]), 6);
            this.content.add(this.blockText);
        } else {
            this.blockText = null;
        }
        for (i = this.panes.size() - 1; i >= 0; --i) {
            this.content.bringToFront(this.panes.get(i));
        }
    }

    @Override
    protected void layout() {
        float bottom;
        super.layout();
        float top = 0.0f;
        for (int i = 0; i < this.panes.size(); ++i) {
            this.panes.get(i).setRect(this.x, top += 2.0f, this.width, 0.0f);
            top = this.panes.get(i).bottom();
            this.separators.get((int)i).x = 0.0f;
            this.separators.get((int)i).y = top + 2.0f;
            this.separators.get(i).size(this.width, 1.0f);
            top += 3.0f;
        }
        if (this.blockText != null) {
            bottom = Math.max(this.height, top + 20.0f);
            this.blocker.x = 0.0f;
            this.blocker.y = top;
            this.blocker.size(this.width, bottom - top);
            this.blockText.maxWidth((int)this.width);
            this.blockText.align(2);
            this.blockText.setPos((this.width - this.blockText.width()) / 2.0f, this.blocker.y + (bottom - this.blocker.y - this.blockText.height()) / 2.0f);
        } else {
            bottom = Math.max(this.height, top);
            this.blocker.visible = false;
        }
        this.content.setSize(this.width, bottom);
    }

    public static class TalentTierPane
    extends Component {
        private int tier;
        public RenderedTextBlock title;
        ArrayList<TalentButton> buttons;
        ArrayList<Image> stars = new ArrayList();
        IconButton random;

        public TalentTierPane(LinkedHashMap<Talent, Integer> talents, final int tier, TalentButton.Mode mode) {
            this.tier = tier;
            this.title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(TalentsPane.class, "tier", tier)), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            if (mode == TalentButton.Mode.UPGRADE) {
                this.setupStars();
                if (Dungeon.hero.talentPointsAvailable(tier) > 0) {
                    this.random = new IconButton(Icons.SHUFFLE.get()){

                        @Override
                        protected void onClick() {
                            super.onClick();
                            GameScene.show(new WndOptions(Icons.SHUFFLE.get(), Messages.get(TalentsPane.class, "random_title", new Object[0]), Messages.get(TalentsPane.class, "random_sure", new Object[0]), new String[]{Messages.get(TalentsPane.class, "random_yes", new Object[0]), Messages.get(TalentsPane.class, "random_one", new Object[0]), Messages.get(TalentsPane.class, "random_no", new Object[0])}){

                                @Override
                                protected void onSelect(int index) {
                                    super.onSelect(index);
                                    if (parent == null) {
                                        return;
                                    }
                                    if (index == 0 || index == 1) {
                                        while (Dungeon.hero.talentPointsAvailable(tier) > 0) {
                                            TalentButton button = Random.element(buttons);
                                            if (Dungeon.hero.pointsInTalent(button.talent) >= button.talent.maxPoints()) continue;
                                            button.upgradeTalent();
                                            if (index != 1) continue;
                                            break;
                                        }
                                        this.setupStars();
                                        this.layout();
                                    }
                                }
                            });
                        }

                        @Override
                        public void update() {
                            if (Dungeon.hero.lvl >= 3 && Statistics.qualifiedForRandomVictoryBadge) {
                                this.icon.tint(1.0f, 1.0f, 1.0f, (float)Math.abs(Math.cos(4.71238898038469 * (double)Game.timeTotal) / 2.0));
                            }
                            super.update();
                        }
                    };
                    this.add(this.random);
                }
            }
            this.buttons = new ArrayList();
            for (Talent talent : talents.keySet()) {
                TalentButton btn = new TalentButton(tier, talent, talents.get((Object)talent), mode){

                    @Override
                    public void upgradeTalent() {
                        super.upgradeTalent();
                        if (this.parent != null) {
                            this.setupStars();
                            this.layout();
                        }
                    }
                };
                this.buttons.add(btn);
                this.add(btn);
            }
        }

        private void setupStars() {
            if (!this.stars.isEmpty()) {
                for (Image im : this.stars) {
                    im.killAndErase();
                }
                this.stars.clear();
            }
            int totStars = Talent.tierLevelThresholds[this.tier + 1] - Talent.tierLevelThresholds[this.tier] + Dungeon.hero.bonusTalentPoints(this.tier);
            int openStars = Dungeon.hero.talentPointsAvailable(this.tier);
            int usedStars = Dungeon.hero.talentPointsSpent(this.tier);
            for (int i = 0; i < totStars; ++i) {
                Speck im = new Speck().image(1);
                this.stars.add(im);
                this.add(im);
                if (i >= openStars && i < openStars + usedStars) {
                    im.tint(0.75f, 0.75f, 0.75f, 0.9f);
                    continue;
                }
                if (i < openStars + usedStars) continue;
                im.tint(0.0f, 0.0f, 0.0f, 0.9f);
            }
            if (this.random != null && openStars == 0) {
                this.random.killAndErase();
                this.random.destroy();
                this.random = null;
            }
        }

        @Override
        protected void layout() {
            super.layout();
            int regStars = Talent.tierLevelThresholds[this.tier + 1] - Talent.tierLevelThresholds[this.tier];
            float titleWidth = this.title.width();
            this.title.setPos(this.x + (this.width - (titleWidth += (float)(2 + Math.min(this.stars.size(), regStars) * 6))) / 2.0f, this.y);
            float left = this.title.right() + 2.0f;
            float starTop = this.title.top();
            if (regStars < this.stars.size()) {
                starTop -= 2.0f;
            }
            for (Image star : this.stars) {
                star.x = left;
                star.y = starTop;
                PixelScene.align(star);
                left += 6.0f;
                if (--regStars != 0) continue;
                starTop += 6.0f;
                left = this.title.right() + 2.0f;
            }
            if (this.random != null) {
                this.random.setRect(this.width - 16.0f, this.y - 2.0f, 16.0f, 14.0f);
            }
            float gap = (this.width - (float)(this.buttons.size() * 20)) / (float)(this.buttons.size() + 1);
            left = this.x + gap;
            for (TalentButton btn : this.buttons) {
                btn.setPos(left, this.title.bottom() + 4.0f);
                PixelScene.align(btn);
                left += btn.width() + gap;
            }
            this.height = this.buttons.get(0).bottom() - this.y;
        }
    }
}

