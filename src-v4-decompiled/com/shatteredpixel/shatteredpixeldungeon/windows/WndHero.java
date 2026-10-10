/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;
import java.util.Locale;

public class WndHero
extends WndTabbed {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 120;
    private StatsTab stats;
    private TalentsTab talents;
    private BuffsTab buffs;
    public static int lastIdx = 0;

    public WndHero() {
        this.resize(120, 120);
        this.stats = new StatsTab();
        this.add(this.stats);
        this.talents = new TalentsTab();
        this.add(this.talents);
        this.talents.setRect(0.0f, 0.0f, 120.0f, 120.0f);
        this.buffs = new BuffsTab();
        this.add(this.buffs);
        this.buffs.setRect(0.0f, 0.0f, 120.0f, 120.0f);
        this.buffs.setupList();
        this.add(new WndTabbed.IconTab(Icons.get(Icons.RANKINGS)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                if (this.selected) {
                    lastIdx = 0;
                    if (!WndHero.this.stats.visible) {
                        WndHero.this.stats.initialize();
                    }
                }
                WndHero.this.stats.visible = WndHero.this.stats.active = this.selected;
            }
        });
        this.add(new WndTabbed.IconTab(Icons.get(Icons.TALENT)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                if (this.selected) {
                    lastIdx = 1;
                }
                if (this.selected) {
                    StatusPane.talentBlink = 0.0f;
                }
                WndHero.this.talents.visible = WndHero.this.talents.active = this.selected;
            }
        });
        this.add(new WndTabbed.IconTab(Icons.get(Icons.BUFFS)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                if (this.selected) {
                    lastIdx = 2;
                }
                WndHero.this.buffs.visible = WndHero.this.buffs.active = this.selected;
            }
        });
        this.layoutTabs();
        this.talents.setRect(0.0f, 0.0f, 120.0f, 120.0f);
        this.talents.pane.scrollTo(0.0f, this.talents.pane.content().height() - this.talents.pane.height());
        this.talents.layout();
        this.select(lastIdx);
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed && KeyBindings.getActionForKey(event) == SPDAction.HERO_INFO) {
            this.onBackPressed();
            return true;
        }
        return super.onSignal(event);
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        this.talents.layout();
        this.buffs.layout();
    }

    private class StatsTab
    extends Group {
        private static final int GAP = 6;
        private float pos;

        public StatsTab() {
            this.initialize();
        }

        public void initialize() {
            for (Gizmo g : this.members) {
                if (g == null) continue;
                g.destroy();
            }
            this.clear();
            final Hero hero = Dungeon.hero;
            IconTitle title = new IconTitle();
            title.icon(HeroSprite.avatar(hero));
            if (hero.name().equals(hero.className())) {
                title.label(Messages.get(this, "title", hero.lvl, hero.className()).toUpperCase(Locale.ENGLISH));
            } else {
                title.label((hero.name() + "\n" + Messages.get(this, "title", hero.lvl, hero.className())).toUpperCase(Locale.ENGLISH));
            }
            title.color(0xFFFF44);
            title.setRect(0.0f, 0.0f, 104.0f, 0.0f);
            this.add(title);
            IconButton infoButton = new IconButton(Icons.get(Icons.INFO)){

                @Override
                protected void onClick() {
                    super.onClick();
                    if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                        GameScene.show(new WndHeroInfo(hero.heroClass));
                    } else {
                        ShatteredPixelDungeon.scene().addToFront(new WndHeroInfo(hero.heroClass));
                    }
                }

                @Override
                protected String hoverText() {
                    return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info", new Object[0]));
                }
            };
            infoButton.setRect(title.right(), 0.0f, 16.0f, 16.0f);
            this.add(infoButton);
            this.pos = title.bottom() + 12.0f;
            int strBonus = hero.STR() - hero.STR;
            if (strBonus > 0) {
                this.statSlot(Messages.get(this, "str", new Object[0]), hero.STR + " + " + strBonus);
            } else if (strBonus < 0) {
                this.statSlot(Messages.get(this, "str", new Object[0]), hero.STR + " - " + -strBonus);
            } else {
                this.statSlot(Messages.get(this, "str", new Object[0]), hero.STR());
            }
            if (hero.shielding() > 0) {
                this.statSlot(Messages.get(this, "health", new Object[0]), hero.HP + "+" + hero.shielding() + "/" + hero.HT);
            } else {
                this.statSlot(Messages.get(this, "health", new Object[0]), hero.HP + "/" + hero.HT);
            }
            this.statSlot(Messages.get(this, "exp", new Object[0]), hero.exp + "/" + hero.maxExp());
            this.pos += 6.0f;
            this.statSlot(Messages.get(this, "gold", new Object[0]), Statistics.goldCollected);
            this.statSlot(Messages.get(this, "depth", new Object[0]), Statistics.deepestFloor);
            if (Dungeon.daily) {
                if (!Dungeon.dailyReplay) {
                    this.statSlot(Messages.get(this, "daily_for", new Object[0]), "_" + Dungeon.customSeedText + "_");
                } else {
                    this.statSlot(Messages.get(this, "replay_for", new Object[0]), "_" + Dungeon.customSeedText + "_");
                }
            } else if (!Dungeon.customSeedText.isEmpty()) {
                this.statSlot(Messages.get(this, "custom_seed", new Object[0]), "_" + Dungeon.customSeedText + "_");
            } else {
                this.statSlot(Messages.get(this, "dungeon_seed", new Object[0]), DungeonSeed.convertToCode(Dungeon.seed));
            }
            this.pos += 6.0f;
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
            this.pos += 6.0f + txt.height();
        }

        private void statSlot(String label, int value) {
            this.statSlot(label, Integer.toString(value));
        }

        public float height() {
            return this.pos;
        }
    }

    public class TalentsTab
    extends Component {
        TalentsPane pane;

        @Override
        protected void createChildren() {
            super.createChildren();
            this.pane = new TalentsPane(TalentButton.Mode.UPGRADE);
            this.add(this.pane);
        }

        @Override
        protected void layout() {
            super.layout();
            this.pane.setRect(this.x, this.y, this.width, this.height);
        }
    }

    private class BuffsTab
    extends Component {
        private static final int GAP = 2;
        private float pos;
        private ScrollPane buffList;
        private ArrayList<BuffSlot> slots = new ArrayList();

        private BuffsTab() {
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.buffList = new ScrollPane(new Component()){

                @Override
                public void onClick(float x, float y) {
                    int size = BuffsTab.this.slots.size();
                    for (int i = 0; i < size && !BuffsTab.this.slots.get(i).onClick(x, y); ++i) {
                    }
                }
            };
            this.add(this.buffList);
        }

        @Override
        protected void layout() {
            super.layout();
            this.buffList.setRect(0.0f, 0.0f, this.width, this.height);
        }

        private void setupList() {
            Component content = this.buffList.content();
            for (Buff buff : Dungeon.hero.buffs()) {
                if (buff.icon() == 127) continue;
                BuffSlot slot = new BuffSlot(buff);
                slot.setRect(0.0f, this.pos, 120.0f, slot.icon.height());
                content.add(slot);
                this.slots.add(slot);
                this.pos += 2.0f + slot.height();
            }
            content.setSize(this.buffList.width(), this.pos);
            this.buffList.setSize(this.buffList.width(), this.buffList.height());
        }

        private class BuffSlot
        extends Component {
            private Buff buff;
            Image icon;
            RenderedTextBlock txt;

            public BuffSlot(Buff buff) {
                this.buff = buff;
                this.icon = new BuffIcon(buff, true);
                this.icon.y = this.y;
                this.add(this.icon);
                this.txt = PixelScene.renderTextBlock(Messages.titleCase(buff.name()), 8);
                this.txt.setPos(this.icon.width + 2.0f, this.y + (this.icon.height - this.txt.height()) / 2.0f);
                PixelScene.align(this.txt);
                this.add(this.txt);
            }

            @Override
            protected void layout() {
                super.layout();
                this.icon.y = this.y;
                this.txt.maxWidth((int)(this.width - this.icon.width()));
                this.txt.setPos(this.icon.width + 2.0f, this.y + (this.icon.height - this.txt.height()) / 2.0f);
                PixelScene.align(this.txt);
            }

            protected boolean onClick(float x, float y) {
                if (this.inside(x, y)) {
                    GameScene.show(new WndInfoBuff(this.buff));
                    return true;
                }
                return false;
            }
        }
    }
}

