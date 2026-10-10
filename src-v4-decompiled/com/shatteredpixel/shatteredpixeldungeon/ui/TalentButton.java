/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoTalent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import java.util.LinkedHashMap;

public class TalentButton
extends Button {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 26;
    int tier;
    Talent talent;
    int pointsInTalent;
    Mode mode;
    TalentIcon icon;
    Image bg;
    ColorBlock fill;

    public TalentButton(int tier, Talent talent, int points, Mode mode) {
        this.hotArea.blockLevel = 2;
        this.tier = tier;
        this.talent = talent;
        this.pointsInTalent = points;
        this.mode = mode;
        this.bg.frame(20 * (talent.maxPoints() - 1), 0, 20, 26);
        this.icon = new TalentIcon(talent);
        this.add(this.icon);
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.fill = new ColorBlock(0.0f, 4.0f, -188);
        this.add(this.fill);
        this.bg = new Image("interfaces/talent_button.png");
        this.add(this.bg);
    }

    @Override
    protected void layout() {
        this.width = 20.0f;
        this.height = 26.0f;
        super.layout();
        this.fill.x = this.x + 2.0f;
        this.fill.y = this.y + 20.0f - 1.0f;
        this.fill.size((float)this.pointsInTalent / (float)this.talent.maxPoints() * 16.0f, 5.0f);
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.icon.x = this.x + 2.0f;
        this.icon.y = this.y + 2.0f;
        PixelScene.align(this.icon);
    }

    @Override
    protected void onClick() {
        super.onClick();
        WndInfoTalent toAdd = this.mode == Mode.UPGRADE && Dungeon.hero != null && Dungeon.hero.isAlive() && Dungeon.hero.talentPointsAvailable(this.tier) > 0 && Dungeon.hero.pointsInTalent(this.talent) < this.talent.maxPoints() ? new WndInfoTalent(this.talent, this.pointsInTalent, new WndInfoTalent.TalentButtonCallback(){

            @Override
            public String prompt() {
                return Messages.titleCase(Messages.get(WndInfoTalent.class, "upgrade", new Object[0]));
            }

            @Override
            public void call() {
                TalentButton.this.upgradeTalent();
                Statistics.qualifiedForRandomVictoryBadge = false;
            }
        }) : (this.mode == Mode.METAMORPH_CHOOSE && Dungeon.hero != null && Dungeon.hero.isAlive() ? new WndInfoTalent(this.talent, this.pointsInTalent, new WndInfoTalent.TalentButtonCallback(){

            @Override
            public String prompt() {
                return Messages.titleCase(Messages.get(ScrollOfMetamorphosis.class, "metamorphose_talent", new Object[0]));
            }

            @Override
            public boolean metamorphDesc() {
                return true;
            }

            @Override
            public void call() {
                if (ScrollOfMetamorphosis.WndMetamorphChoose.INSTANCE != null) {
                    ScrollOfMetamorphosis.WndMetamorphChoose.INSTANCE.hide();
                }
                GameScene.show(new ScrollOfMetamorphosis.WndMetamorphReplace(TalentButton.this.talent, TalentButton.this.tier));
            }
        }) : (this.mode == Mode.METAMORPH_REPLACE && Dungeon.hero != null && Dungeon.hero.isAlive() ? new WndInfoTalent(this.talent, this.pointsInTalent, new WndInfoTalent.TalentButtonCallback(){

            @Override
            public String prompt() {
                return Messages.titleCase(Messages.get(ScrollOfMetamorphosis.class, "metamorphose_talent", new Object[0]));
            }

            @Override
            public boolean metamorphDesc() {
                return true;
            }

            @Override
            public void call() {
                Talent replacing = ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE.replacing;
                for (LinkedHashMap<Talent, Integer> tier : Dungeon.hero.talents) {
                    if (!tier.containsKey((Object)replacing)) continue;
                    LinkedHashMap<Talent, Integer> newTier = new LinkedHashMap<Talent, Integer>();
                    for (Talent t : tier.keySet()) {
                        if (t == replacing) {
                            newTier.put(TalentButton.this.talent, tier.get((Object)replacing));
                            if (!Dungeon.hero.metamorphedTalents.containsValue((Object)replacing)) {
                                Dungeon.hero.metamorphedTalents.put(replacing, TalentButton.this.talent);
                                continue;
                            }
                            if (Dungeon.hero.metamorphedTalents.get((Object)TalentButton.this.talent) == replacing) {
                                Dungeon.hero.metamorphedTalents.remove((Object)TalentButton.this.talent);
                                continue;
                            }
                            for (Talent t2 : Dungeon.hero.metamorphedTalents.keySet()) {
                                if (Dungeon.hero.metamorphedTalents.get((Object)t2) != replacing) continue;
                                Dungeon.hero.metamorphedTalents.put(t2, TalentButton.this.talent);
                            }
                            continue;
                        }
                        newTier.put(t, tier.get((Object)t));
                    }
                    Dungeon.hero.talents.set(ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE.tier - 1, newTier);
                    break;
                }
                ScrollOfMetamorphosis.onMetamorph(replacing, TalentButton.this.talent);
                if (ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE != null) {
                    ScrollOfMetamorphosis.WndMetamorphReplace.INSTANCE.hide();
                }
            }
        }) : new WndInfoTalent(this.talent, this.pointsInTalent, null)));
        if (ShatteredPixelDungeon.scene() instanceof GameScene) {
            GameScene.show(toAdd);
        } else {
            ShatteredPixelDungeon.scene().addToFront(toAdd);
        }
    }

    @Override
    protected void onPointerDown() {
        this.icon.brightness(1.5f);
        this.bg.brightness(1.5f);
        Sample.INSTANCE.play("sounds/click.mp3");
    }

    @Override
    protected void onPointerUp() {
        this.icon.resetColor();
        this.bg.resetColor();
    }

    @Override
    protected String hoverText() {
        return Messages.titleCase(this.talent.title());
    }

    public void enable(boolean value) {
        this.active = value;
        this.icon.alpha(value ? 1.0f : 0.3f);
        this.bg.alpha(value ? 1.0f : 0.3f);
    }

    public void upgradeTalent() {
        if (Dungeon.hero.talentPointsAvailable(this.tier) > 0 && this.parent != null) {
            Dungeon.hero.upgradeTalent(this.talent);
            float oldWidth = this.fill.width();
            ++this.pointsInTalent;
            this.layout();
            Sample.INSTANCE.play("sounds/levelup.mp3", 0.7f, 1.2f);
            Emitter emitter = (Emitter)this.parent.recycle(Emitter.class);
            emitter.revive();
            emitter.pos(this.fill.x + (this.fill.width() + oldWidth) / 2.0f, this.fill.y + this.fill.height() / 2.0f);
            emitter.burst(Speck.factory(1), 12);
        }
    }

    public static enum Mode {
        INFO,
        UPGRADE,
        METAMORPH_CHOOSE,
        METAMORPH_REPLACE;

    }
}

