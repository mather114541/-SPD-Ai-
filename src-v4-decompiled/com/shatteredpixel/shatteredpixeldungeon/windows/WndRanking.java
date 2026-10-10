/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.EscapeCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesGrid;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesList;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndScoreBreakdown;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import java.text.NumberFormat;
import java.util.Locale;

public class WndRanking
extends WndTabbed {
    private static final int WIDTH = 115;
    private static final int HEIGHT = 144;
    private static WndRanking INSTANCE;
    private String gameID;
    private Rankings.Record record;

    public WndRanking(Rankings.Record rec) {
        this.resize(115, 144);
        if (INSTANCE != null) {
            INSTANCE.hide();
        }
        INSTANCE = this;
        this.gameID = rec.gameID;
        this.record = rec;
        try {
            Badges.loadGlobal();
            Rankings.INSTANCE.loadGameData(rec);
            this.createControls();
        }
        catch (Exception e) {
            Game.reportException(new RuntimeException("Rankings Display Failed!", e));
            Dungeon.hero = null;
            this.createControls();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (INSTANCE == this) {
            INSTANCE = null;
        }
    }

    private void createControls() {
        if (Dungeon.hero != null) {
            Icons[] icons = new Icons[]{Icons.RANKINGS, Icons.TALENT, Icons.BACKPACK_LRG, Icons.BADGES, Icons.CHALLENGE_COLOR};
            Group[] pages = new Group[]{new StatsTab(), new TalentsTab(), new ItemsTab(), new BadgesTab(), null};
            if (Dungeon.challenges != 0) {
                pages[4] = new ChallengesTab();
            }
            for (int i = 0; i < pages.length && pages[i] != null; ++i) {
                this.add(pages[i]);
                RankingTab tab = new RankingTab(icons[i], pages[i]);
                this.add(tab);
            }
            this.layoutTabs();
            this.select(0);
        } else {
            StatsTab tab = new StatsTab();
            this.add(tab);
        }
    }

    private class StatsTab
    extends Group {
        private int GAP = 4;

        public StatsTab() {
            String heroClass = WndRanking.this.record.heroClass.name();
            if (Dungeon.hero != null) {
                heroClass = Dungeon.hero.className();
            }
            IconTitle title = new IconTitle();
            title.icon(HeroSprite.avatar(WndRanking.this.record.heroClass, WndRanking.this.record.armorTier));
            title.label(Messages.get(this, "title", WndRanking.this.record.herolevel, heroClass).toUpperCase(Locale.ENGLISH));
            title.color(0xFFFF44);
            title.setRect(0.0f, 0.0f, 115.0f, 0.0f);
            this.add(title);
            if (Dungeon.hero != null && Dungeon.seed != -1L) {
                --this.GAP;
            }
            float pos = title.bottom() + 1.0f;
            RenderedTextBlock date = PixelScene.renderTextBlock(WndRanking.this.record.date, 7);
            date.hardlight(0xCCCCCC);
            date.setPos(0.0f, pos);
            this.add(date);
            RenderedTextBlock version = PixelScene.renderTextBlock(WndRanking.this.record.version, 7);
            version.hardlight(0xCCCCCC);
            version.setPos(115.0f - version.width(), pos);
            this.add(version);
            pos = date.bottom() + 5.0f;
            NumberFormat num = NumberFormat.getInstance(Messages.locale());
            if (Dungeon.hero == null) {
                pos = this.statSlot(this, Messages.get(this, "score", new Object[0]), num.format(WndRanking.this.record.score), pos);
                Image errorIcon = Icons.WARNING.get();
                errorIcon.y = pos += (float)this.GAP;
                this.add(errorIcon);
                RenderedTextBlock errorText = PixelScene.renderTextBlock(Messages.get(WndRanking.class, "error", new Object[0]), 6);
                errorText.maxWidth((int)(115.0f - errorIcon.width() - (float)this.GAP));
                errorText.setPos(errorIcon.width() + (float)this.GAP, pos + (errorIcon.height() - errorText.height()) / 2.0f);
                this.add(errorText);
            } else {
                pos = this.statSlot(this, Messages.get(this, "score", new Object[0]), num.format(Statistics.totalScore), pos);
                IconButton scoreInfo = new IconButton(Icons.get(Icons.INFO)){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        ShatteredPixelDungeon.scene().addToFront(new WndScoreBreakdown());
                    }
                };
                scoreInfo.setSize(16.0f, 16.0f);
                scoreInfo.setPos(115.0f - scoreInfo.width(), pos - 10.0f - (float)this.GAP);
                this.add(scoreInfo);
                pos += (float)this.GAP;
                int strBonus = Dungeon.hero.STR() - Dungeon.hero.STR;
                pos = strBonus > 0 ? this.statSlot(this, Messages.get(this, "str", new Object[0]), Dungeon.hero.STR + " + " + strBonus, pos) : (strBonus < 0 ? this.statSlot(this, Messages.get(this, "str", new Object[0]), Dungeon.hero.STR + " - " + -strBonus, pos) : this.statSlot(this, Messages.get(this, "str", new Object[0]), Integer.toString(Dungeon.hero.STR), pos));
                pos = this.statSlot(this, Messages.get(this, "duration", new Object[0]), num.format((int)Statistics.duration), pos);
                pos = Statistics.highestAscent == 0 ? this.statSlot(this, Messages.get(this, "depth", new Object[0]), num.format(Statistics.deepestFloor), pos) : this.statSlot(this, Messages.get(this, "ascent", new Object[0]), num.format(Statistics.highestAscent), pos);
                pos = Dungeon.seed != -1L ? (Dungeon.daily ? (Dungeon.dailyReplay ? this.statSlot(this, Messages.get(this, "replay_for", new Object[0]), "_" + Dungeon.customSeedText + "_", pos) : this.statSlot(this, Messages.get(this, "daily_for", new Object[0]), "_" + Dungeon.customSeedText + "_", pos)) : (!Dungeon.customSeedText.isEmpty() ? this.statSlot(this, Messages.get(this, "custom_seed", new Object[0]), "_" + Dungeon.customSeedText + "_", pos) : this.statSlot(this, Messages.get(this, "seed", new Object[0]), DungeonSeed.convertToCode(Dungeon.seed), pos))) : (pos += (float)(this.GAP + 5));
                pos += (float)this.GAP;
                pos = this.statSlot(this, Messages.get(this, "enemies", new Object[0]), num.format(Statistics.enemiesSlain), pos);
                pos = this.statSlot(this, Messages.get(this, "gold", new Object[0]), num.format(Statistics.goldCollected), pos);
                pos = this.statSlot(this, Messages.get(this, "food", new Object[0]), num.format(Statistics.foodEaten), pos);
                pos = this.statSlot(this, Messages.get(this, "alchemy", new Object[0]), num.format(Statistics.itemsCrafted), pos);
            }
            int buttontop = 128;
            if (Dungeon.hero != null && Dungeon.seed != -1L && !Dungeon.daily && (DeviceCompat.isDebug() || Badges.isUnlocked(Badges.Badge.VICTORY))) {
                Image icon = Icons.get(Icons.SEED);
                RedButton btnSeed = new RedButton(Messages.get(this, "copy_seed", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        ShatteredPixelDungeon.scene().addToFront(new WndOptions(new Image(this.icon), Messages.get(StatsTab.this, "copy_seed", new Object[0]), Messages.get(StatsTab.this, "copy_seed_desc", new Object[0]), new String[]{Messages.get(StatsTab.this, "copy_seed_copy", new Object[0]), Messages.get(StatsTab.this, "copy_seed_cancel", new Object[0])}){

                            @Override
                            protected void onSelect(int index) {
                                super.onSelect(index);
                                if (index == 0) {
                                    SPDSettings.customSeed(DungeonSeed.convertToCode(Dungeon.seed));
                                    icon.hardlight(1.0f, 1.5f, 0.67f);
                                }
                            }
                        });
                    }
                };
                if (DungeonSeed.convertFromText(SPDSettings.customSeed()) == Dungeon.seed) {
                    icon.hardlight(1.0f, 1.5f, 0.67f);
                }
                btnSeed.icon(icon);
                btnSeed.setRect(0.0f, buttontop, 115.0f, 16.0f);
                this.add(btnSeed);
            }
        }

        private float statSlot(Group parent, String label, String value, float pos) {
            RenderedTextBlock txt;
            int size = 7;
            do {
                txt = PixelScene.renderTextBlock(label, size);
                --size;
            } while (txt.width() >= 63.25f);
            txt.setPos(0.0f, pos + (6.0f - txt.height()) / 2.0f);
            PixelScene.align(txt);
            parent.add(txt);
            size = 7;
            do {
                txt = PixelScene.renderTextBlock(value, size);
                --size;
            } while (txt.width() >= 51.75f);
            txt.setPos(63.25f, pos + (6.0f - txt.height()) / 2.0f);
            PixelScene.align(txt);
            parent.add(txt);
            return pos + (float)this.GAP + txt.height();
        }
    }

    private class TalentsTab
    extends Group {
        public TalentsTab() {
            this.camera = WndRanking.this.camera;
            int tiers = 1;
            if (Dungeon.hero.lvl >= 6) {
                ++tiers;
            }
            if (Dungeon.hero.lvl >= 12 && Dungeon.hero.subClass != HeroSubClass.NONE) {
                ++tiers;
            }
            if (Dungeon.hero.lvl >= 20 && Dungeon.hero.armorAbility != null) {
                ++tiers;
            }
            while (Dungeon.hero.talents.size() > tiers) {
                Dungeon.hero.talents.remove(Dungeon.hero.talents.size() - 1);
            }
            TalentsPane p = new TalentsPane(TalentButton.Mode.INFO);
            this.add(p);
            p.setPos(0.0f, 0.0f);
            p.setSize(115.0f, 144.0f);
            p.setPos(0.0f, 0.0f);
        }
    }

    private class ItemsTab
    extends Group {
        private float pos;

        public ItemsTab() {
            Belongings stuff = Dungeon.hero.belongings;
            if (stuff.weapon != null) {
                this.addItem(stuff.weapon);
            }
            if (stuff.armor != null) {
                this.addItem(stuff.armor);
            }
            if (stuff.artifact != null) {
                this.addItem(stuff.artifact);
            }
            if (stuff.misc != null) {
                this.addItem(stuff.misc);
            }
            if (stuff.ring != null) {
                this.addItem(stuff.ring);
            }
            this.pos = 0.0f;
            int slotsActive = 0;
            for (int i = 0; i < QuickSlot.SIZE; ++i) {
                if (!Dungeon.quickslot.isNonePlaceholder(i).booleanValue()) continue;
                ++slotsActive;
            }
            Trinket trinket = stuff.getItem(Trinket.class);
            if (trinket != null) {
                ++slotsActive;
            }
            float slotWidth = Math.min(28.0f, (float)(115 - slotsActive + 1) / (float)slotsActive);
            for (int i = -1; i < QuickSlot.SIZE; ++i) {
                Item item = null;
                if (i == -1) {
                    item = trinket;
                } else if (Dungeon.quickslot.isNonePlaceholder(i).booleanValue()) {
                    item = Dungeon.quickslot.getItem(i);
                }
                if (item == null) continue;
                QuickSlotButton slot = new QuickSlotButton(item);
                slot.setRect(this.pos, 120.0f, slotWidth, 23.0f);
                PixelScene.align(slot);
                this.add(slot);
                this.pos += slotWidth + 1.0f;
            }
            final EscapeCrystal crystal = Dungeon.hero.belongings.getItem(EscapeCrystal.class);
            if (crystal != null) {
                IconButton vaultInv = new IconButton(new ItemSprite(ItemSpriteSheet.ESCAPE)){

                    @Override
                    protected void onClick() {
                        Bundle items = crystal.storedItems;
                        crystal.restoreHeroBelongings(Dungeon.hero, null);
                        crystal.storedItems = items;
                        ShatteredPixelDungeon.scene().addToFront(new WndVaultItems());
                    }
                };
                vaultInv.setRect(WndRanking.this.width - 16, 2.0f, 16.0f, 16.0f);
                this.add(vaultInv);
            }
        }

        private void addItem(Item item) {
            ItemButton slot = new ItemButton(item);
            slot.setRect(0.0f, this.pos, WndRanking.this.width, 23.0f);
            this.add(slot);
            this.pos += slot.height() + 1.0f;
        }
    }

    private class BadgesTab
    extends Group {
        public BadgesTab() {
            this.camera = WndRanking.this.camera;
            Component badges = Badges.filterReplacedBadges(false).size() <= 8 ? new BadgesList(false) : new BadgesGrid(false);
            this.add(badges);
            badges.setSize(115.0f, 144.0f);
        }
    }

    private class ChallengesTab
    extends Group {
        public ChallengesTab() {
            this.camera = WndRanking.this.camera;
            float pos = 0.0f;
            for (int i = 0; i < Challenges.NAME_IDS.length; ++i) {
                final String challenge = Challenges.NAME_IDS[i];
                CheckBox cb = new CheckBox(Messages.titleCase(Messages.get(Challenges.class, challenge, new Object[0])));
                cb.checked((Dungeon.challenges & Challenges.MASKS[i]) != 0);
                cb.active = false;
                if (i > 0) {
                    pos += 1.0f;
                }
                cb.setRect(0.0f, pos, 99.0f, 15.0f);
                this.add(cb);
                IconButton info = new IconButton(Icons.get(Icons.INFO)){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        ShatteredPixelDungeon.scene().add(new WndMessage(Messages.get(Challenges.class, challenge + "_desc", new Object[0])));
                    }
                };
                info.setRect(cb.right(), pos, 16.0f, 15.0f);
                this.add(info);
                pos = cb.bottom();
            }
        }
    }

    private class RankingTab
    extends WndTabbed.IconTab {
        private Group page;

        public RankingTab(Icons icon, Group page) {
            super(WndRanking.this, Icons.get(icon));
            this.page = page;
        }

        @Override
        protected void select(boolean value) {
            super.select(value);
            if (this.page != null) {
                this.page.visible = this.page.active = this.selected;
            }
        }
    }

    private class QuickSlotButton
    extends ItemSlot {
        private Item item;
        private ColorBlock bg;

        QuickSlotButton(Item item) {
            super(item);
            this.item = item;
            if (item.cursed && item.cursedKnown) {
                this.bg.ra = 0.2f;
                this.bg.ga = -0.1f;
            } else if (!item.isIdentified()) {
                this.bg.ra = 0.1f;
                this.bg.ba = 0.1f;
            }
        }

        @Override
        protected void createChildren() {
            this.bg = new ColorBlock(1.0f, 1.0f, -1722591667);
            this.add(this.bg);
            super.createChildren();
        }

        @Override
        protected void layout() {
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.bg.size(this.width(), this.height());
            super.layout();
        }

        @Override
        protected void onPointerDown() {
            this.bg.brightness(1.5f);
            Sample.INSTANCE.play("sounds/click.mp3", 0.7f, 0.7f, 1.2f);
        }

        @Override
        protected void onPointerUp() {
            this.bg.brightness(1.0f);
        }

        @Override
        protected void onClick() {
            Game.scene().add(new WndInfoItem(this.item));
        }
    }

    private class ItemButton
    extends Button {
        public static final int HEIGHT = 23;
        private Item item;
        private ItemSlot slot;
        private ColorBlock bg;
        private RenderedTextBlock name;

        public ItemButton(Item item) {
            this.item = item;
            this.slot.item(item);
            if (item.cursed && item.cursedKnown) {
                this.bg.ra = 0.3f;
                this.bg.ga = -0.15f;
                this.bg.ba = -0.15f;
            } else if (!item.isIdentified()) {
                if ((item instanceof EquipableItem || item instanceof Wand) && item.cursedKnown) {
                    this.bg.ba = 0.3f;
                    this.bg.ra = -0.1f;
                } else {
                    this.bg.ra = 0.35f;
                    this.bg.ba = 0.35f;
                }
            }
        }

        @Override
        protected void createChildren() {
            this.bg = new ColorBlock(28.0f, 23.0f, -1722591667);
            this.add(this.bg);
            this.slot = new ItemSlot();
            this.add(this.slot);
            this.name = PixelScene.renderTextBlock(7);
            this.add(this.name);
            super.createChildren();
        }

        @Override
        protected void layout() {
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.slot.setRect(this.x, this.y, 28.0f, 23.0f);
            PixelScene.align(this.slot);
            this.name.maxWidth((int)(this.width - this.slot.width() - 2.0f));
            this.name.text(Messages.titleCase(this.item.name()));
            this.name.setPos(this.slot.right() + 2.0f, this.y + (this.height - this.name.height()) / 2.0f);
            PixelScene.align(this.name);
            super.layout();
        }

        @Override
        protected void onPointerDown() {
            this.bg.brightness(1.5f);
            Sample.INSTANCE.play("sounds/click.mp3", 0.7f, 0.7f, 1.2f);
        }

        @Override
        protected void onPointerUp() {
            this.bg.brightness(1.0f);
        }

        @Override
        protected void onClick() {
            Game.scene().add(new WndInfoItem(this.item));
        }
    }

    private class WndVaultItems
    extends Window {
        public WndVaultItems() {
            this.resize(115, 144);
            this.add(new ItemsTab());
        }
    }
}

