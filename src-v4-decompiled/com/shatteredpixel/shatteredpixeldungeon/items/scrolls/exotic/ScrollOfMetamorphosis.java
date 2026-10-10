/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class ScrollOfMetamorphosis
extends ExoticScroll {
    protected static boolean identifiedByUse = false;

    public ScrollOfMetamorphosis() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_METAMORPH;
        this.talentFactor = 2.0f;
    }

    @Override
    public void doRead() {
        if (!this.isKnown()) {
            this.identify();
            curItem = this.detach(ScrollOfMetamorphosis.curUser.belongings.backpack);
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }
        GameScene.show(new WndMetamorphChoose());
    }

    public static void onMetamorph(Talent oldTalent, Talent newTalent) {
        if (curItem instanceof ScrollOfMetamorphosis) {
            ((ScrollOfMetamorphosis)curItem).readAnimation();
            Sample.INSTANCE.play("sounds/read.mp3");
        }
        ScrollOfMetamorphosis.curUser.sprite.emitter().start(Speck.factory(10), 0.2f, 10);
        Transmuting.show((Char)curUser, oldTalent, newTalent);
        if (Dungeon.hero.hasTalent(newTalent)) {
            Talent.onTalentUpgraded(Dungeon.hero, newTalent);
        }
    }

    private void confirmCancelation(final Window chooseWindow, boolean byID) {
        GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.name()), byID ? Messages.get(InventoryScroll.class, "warning", new Object[0]) : Messages.get(ScrollOfMetamorphosis.class, "cancel_warn", new Object[0]), new String[]{Messages.get(InventoryScroll.class, "yes", new Object[0]), Messages.get(InventoryScroll.class, "no", new Object[0])}){

            @Override
            protected void onSelect(int index) {
                switch (index) {
                    case 0: {
                        curUser.spendAndNext(1.0f);
                        identifiedByUse = false;
                        chooseWindow.hide();
                        break;
                    }
                }
            }

            @Override
            public void onBackPressed() {
            }
        });
    }

    public static class WndMetamorphChoose
    extends Window {
        public static WndMetamorphChoose INSTANCE;
        TalentsPane pane;

        public WndMetamorphChoose() {
            INSTANCE = this;
            float top = 0.0f;
            IconTitle title = new IconTitle(curItem);
            title.color(0xFFFF44);
            title.setRect(0.0f, 0.0f, 120.0f, 0.0f);
            this.add(title);
            top = title.bottom() + 2.0f;
            RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(ScrollOfMetamorphosis.class, "choose_desc", new Object[0]), 6);
            text.maxWidth(120);
            text.setPos(0.0f, top);
            this.add(text);
            top = text.bottom() + 2.0f;
            ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<LinkedHashMap<Talent, Integer>>();
            Talent.initClassTalents(Dungeon.hero.heroClass, talents, Dungeon.hero.metamorphedTalents);
            for (LinkedHashMap<Talent, Integer> tier : talents) {
                for (Talent talent : tier.keySet()) {
                    tier.put(talent, Dungeon.hero.pointsInTalent(talent));
                }
            }
            this.pane = new TalentsPane(TalentButton.Mode.METAMORPH_CHOOSE, talents);
            this.add(this.pane);
            this.pane.setPos(0.0f, top);
            this.pane.setSize(120.0f, this.pane.content().height());
            this.resize((int)this.pane.width(), (int)this.pane.bottom());
            this.pane.setPos(0.0f, top);
        }

        @Override
        public void hide() {
            super.hide();
            INSTANCE = null;
        }

        @Override
        public void onBackPressed() {
            if (identifiedByUse) {
                ((ScrollOfMetamorphosis)curItem).confirmCancelation(this, true);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public void offset(int xOffset, int yOffset) {
            super.offset(xOffset, yOffset);
            this.pane.setPos(this.pane.left(), this.pane.top());
        }
    }

    public static class WndMetamorphReplace
    extends Window {
        public static WndMetamorphReplace INSTANCE;
        public Talent replacing;
        public int tier;
        LinkedHashMap<Talent, Integer> replaceOptions;

        public WndMetamorphReplace() {
            if (INSTANCE != null) {
                this.replacing = WndMetamorphReplace.INSTANCE.replacing;
                this.tier = WndMetamorphReplace.INSTANCE.tier;
                this.replaceOptions = WndMetamorphReplace.INSTANCE.replaceOptions;
                INSTANCE = this;
                this.setup(this.replacing, this.tier, this.replaceOptions);
            } else {
                this.hide();
            }
        }

        public WndMetamorphReplace(Talent replacing, int tier) {
            if (!identifiedByUse && curItem instanceof ScrollOfMetamorphosis) {
                curItem.detach(curUser.belongings.backpack);
            }
            identifiedByUse = false;
            INSTANCE = this;
            this.replacing = replacing;
            this.tier = tier;
            LinkedHashMap<Talent, Integer> options = new LinkedHashMap<Talent, Integer>();
            Set<Talent> curTalentsAtTier = Dungeon.hero.talents.get(tier - 1).keySet();
            for (HeroClass cls : HeroClass.values()) {
                ArrayList<LinkedHashMap<Talent, Integer>> clsTalents = new ArrayList<LinkedHashMap<Talent, Integer>>();
                Talent.initClassTalents(cls, clsTalents);
                Set<Talent> clsTalentsAtTier = clsTalents.get(tier - 1).keySet();
                boolean replacingIsInSet = false;
                for (Talent talent : clsTalentsAtTier.toArray(new Talent[0])) {
                    if (talent == replacing) {
                        replacingIsInSet = true;
                        break;
                    }
                    if (!curTalentsAtTier.contains((Object)talent)) continue;
                    clsTalentsAtTier.remove((Object)talent);
                }
                if (replacingIsInSet || clsTalentsAtTier.isEmpty()) continue;
                options.put(Random.element(clsTalentsAtTier), Dungeon.hero.pointsInTalent(replacing));
            }
            this.replaceOptions = options;
            this.setup(replacing, tier, options);
        }

        private void setup(Talent replacing, int tier, LinkedHashMap<Talent, Integer> replaceOptions) {
            float top = 0.0f;
            IconTitle title = new IconTitle(curItem);
            title.color(0xFFFF44);
            title.setRect(0.0f, 0.0f, 120.0f, 0.0f);
            this.add(title);
            top = title.bottom() + 2.0f;
            RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(ScrollOfMetamorphosis.class, "replace_desc", new Object[0]), 6);
            text.maxWidth(120);
            text.setPos(0.0f, top);
            this.add(text);
            top = text.bottom() + 2.0f;
            TalentsPane.TalentTierPane optionsPane = new TalentsPane.TalentTierPane(replaceOptions, tier, TalentButton.Mode.METAMORPH_REPLACE);
            this.add(optionsPane);
            optionsPane.title.text(" ");
            optionsPane.setPos(0.0f, top);
            optionsPane.setSize(120.0f, optionsPane.height());
            this.resize((int)optionsPane.width(), (int)optionsPane.bottom());
            this.resize(120, (int)optionsPane.bottom());
        }

        @Override
        public void hide() {
            super.hide();
            if (INSTANCE == this) {
                INSTANCE = null;
            }
        }

        @Override
        public void onBackPressed() {
            if (curItem instanceof ScrollOfMetamorphosis) {
                ((ScrollOfMetamorphosis)curItem).confirmCancelation(this, false);
            } else {
                super.onBackPressed();
            }
        }
    }
}

