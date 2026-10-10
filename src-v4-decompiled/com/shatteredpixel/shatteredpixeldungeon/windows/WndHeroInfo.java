/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoSubclass;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WndHeroInfo
extends WndTabbed {
    private HeroInfoTab heroInfo;
    private TalentInfoTab talentInfo;
    private SubclassInfoTab subclassInfo;
    private ArmorAbilityInfoTab abilityInfo;
    private static int WIDTH = 120;
    private static int MIN_HEIGHT = 125;
    private static int MARGIN = 2;

    public WndHeroInfo(HeroClass cl) {
        ItemSprite tabIcon;
        switch (cl) {
            default: {
                tabIcon = new ItemSprite(ItemSpriteSheet.SEAL, null);
                break;
            }
            case MAGE: {
                tabIcon = new ItemSprite(ItemSpriteSheet.MAGES_STAFF, null);
                break;
            }
            case ROGUE: {
                tabIcon = new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK, null);
                break;
            }
            case HUNTRESS: {
                tabIcon = new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null);
                break;
            }
            case DUELIST: {
                tabIcon = new ItemSprite(ItemSpriteSheet.RAPIER, null);
                break;
            }
            case CLERIC: {
                tabIcon = new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME, null);
            }
        }
        int finalHeight = MIN_HEIGHT;
        this.heroInfo = new HeroInfoTab(cl);
        this.add(this.heroInfo);
        this.heroInfo.setSize(WIDTH, MIN_HEIGHT);
        finalHeight = (int)Math.max((float)finalHeight, this.heroInfo.height());
        this.add(new WndTabbed.IconTab((Image)tabIcon){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndHeroInfo.this.heroInfo.visible = WndHeroInfo.this.heroInfo.active = value;
            }
        });
        this.talentInfo = new TalentInfoTab(cl);
        this.add(this.talentInfo);
        this.talentInfo.setSize(WIDTH, MIN_HEIGHT);
        finalHeight = (int)Math.max((float)finalHeight, this.talentInfo.height());
        this.add(new WndTabbed.IconTab(Icons.get(Icons.TALENT)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndHeroInfo.this.talentInfo.visible = WndHeroInfo.this.talentInfo.active = value;
            }
        });
        if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_2) || DeviceCompat.isDebug()) {
            this.subclassInfo = new SubclassInfoTab(cl);
            this.add(this.subclassInfo);
            this.subclassInfo.setSize(WIDTH, MIN_HEIGHT);
            finalHeight = (int)Math.max((float)finalHeight, this.subclassInfo.height());
            this.add(new WndTabbed.IconTab((Image)new ItemSprite(ItemSpriteSheet.MASK, null)){

                @Override
                protected void select(boolean value) {
                    super.select(value);
                    WndHeroInfo.this.subclassInfo.visible = WndHeroInfo.this.subclassInfo.active = value;
                }
            });
        }
        if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_4) || DeviceCompat.isDebug()) {
            this.abilityInfo = new ArmorAbilityInfoTab(cl);
            this.add(this.abilityInfo);
            this.abilityInfo.setSize(WIDTH, MIN_HEIGHT);
            finalHeight = (int)Math.max((float)finalHeight, this.abilityInfo.height());
            this.add(new WndTabbed.IconTab((Image)new ItemSprite(ItemSpriteSheet.CROWN, null)){

                @Override
                protected void select(boolean value) {
                    super.select(value);
                    WndHeroInfo.this.abilityInfo.visible = WndHeroInfo.this.abilityInfo.active = value;
                }
            });
        }
        this.resize(WIDTH, finalHeight);
        this.layoutTabs();
        this.talentInfo.layout();
        this.select(0);
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        this.talentInfo.layout();
    }

    private static class HeroInfoTab
    extends Component {
        private RenderedTextBlock title;
        private RenderedTextBlock[] info;
        private Image[] icons;

        public HeroInfoTab(HeroClass cls) {
            this.title = PixelScene.renderTextBlock(Messages.titleCase(cls.title()), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            String[] desc_entries = cls.desc().split("\n\n");
            this.info = new RenderedTextBlock[desc_entries.length];
            for (int i = 0; i < desc_entries.length; ++i) {
                this.info[i] = PixelScene.renderTextBlock(desc_entries[i], 6);
                this.add(this.info[i]);
            }
            switch (cls) {
                default: {
                    this.icons = new Image[]{new ItemSprite(ItemSpriteSheet.SEAL), new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD), new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                }
                case MAGE: {
                    this.icons = new Image[]{new ItemSprite(ItemSpriteSheet.MAGES_STAFF), new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE), new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                }
                case ROGUE: {
                    this.icons = new Image[]{new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK), Icons.get(Icons.STAIRS), new ItemSprite(ItemSpriteSheet.DAGGER), new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                }
                case HUNTRESS: {
                    this.icons = new Image[]{new ItemSprite(ItemSpriteSheet.SPIRIT_BOW), Icons.GRASS.get(), new ItemSprite(ItemSpriteSheet.GLOVES), new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                }
                case DUELIST: {
                    this.icons = new Image[]{new ItemSprite(ItemSpriteSheet.RAPIER), new ItemSprite(ItemSpriteSheet.WAR_HAMMER), new ItemSprite(ItemSpriteSheet.THROWING_SPIKE), new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                }
                case CLERIC: {
                    this.icons = new Image[]{new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME), Icons.TALENT.get(), new ItemSprite(ItemSpriteSheet.CUDGEL), new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                }
            }
            for (Image im : this.icons) {
                this.add(im);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            this.title.setPos((this.width - this.title.width()) / 2.0f, MARGIN);
            float pos = this.title.bottom() + (float)(4 * MARGIN);
            for (int i = 0; i < this.info.length; ++i) {
                this.info[i].maxWidth((int)this.width - 20);
                this.info[i].setPos(20.0f, pos);
                this.icons[i].x = (20.0f - this.icons[i].width()) / 2.0f;
                this.icons[i].y = this.info[i].top() + (this.info[i].height() - this.icons[i].height()) / 2.0f;
                PixelScene.align(this.icons[i]);
                pos = this.info[i].bottom() + (float)(4 * MARGIN);
            }
            this.height = Math.max(this.height, pos - (float)(4 * MARGIN));
        }
    }

    private static class TalentInfoTab
    extends Component {
        private RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "talents", new Object[0])), 9);
        private RenderedTextBlock message;
        private TalentsPane talentPane;

        public TalentInfoTab(HeroClass cls) {
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "talents_msg", new Object[0]), 6);
            this.add(this.message);
            ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<LinkedHashMap<Talent, Integer>>();
            Talent.initClassTalents(cls, talents);
            talents.get(2).clear();
            this.talentPane = new TalentsPane(TalentButton.Mode.INFO, talents);
            this.add(this.talentPane);
        }

        @Override
        protected void layout() {
            super.layout();
            this.title.setPos((this.width - this.title.width()) / 2.0f, MARGIN);
            this.message.maxWidth((int)this.width);
            this.message.setPos(0.0f, this.title.bottom() + (float)(4 * MARGIN));
            this.talentPane.setRect(0.0f, this.message.bottom() + (float)(3 * MARGIN), this.width, 85.0f);
            this.height = Math.max(this.height, this.talentPane.bottom());
        }
    }

    private static class SubclassInfoTab
    extends Component {
        private RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses", new Object[0])), 9);
        private RenderedTextBlock message;
        private RenderedTextBlock[] subClsDescs;
        private IconButton[] subClsInfos;

        public SubclassInfoTab(final HeroClass cls) {
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "subclasses_msg", new Object[0]), 6);
            this.add(this.message);
            final HeroSubClass[] subClasses = cls.subClasses();
            this.subClsDescs = new RenderedTextBlock[subClasses.length];
            this.subClsInfos = new IconButton[subClasses.length];
            for (int i = 0; i < subClasses.length; ++i) {
                this.subClsDescs[i] = PixelScene.renderTextBlock(subClasses[i].shortDesc(), 6);
                final int finalI = i;
                this.subClsInfos[i] = new IconButton(Icons.get(Icons.INFO)){

                    @Override
                    protected void onClick() {
                        Game.scene().addToFront(new WndInfoSubclass(cls, subClasses[finalI]));
                    }
                };
                this.add(this.subClsDescs[i]);
                this.add(this.subClsInfos[i]);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            this.title.setPos((this.width - this.title.width()) / 2.0f, MARGIN);
            this.message.maxWidth((int)this.width);
            this.message.setPos(0.0f, this.title.bottom() + (float)(4 * MARGIN));
            float pos = this.message.bottom() + (float)(4 * MARGIN);
            for (int i = 0; i < this.subClsDescs.length; ++i) {
                this.subClsDescs[i].maxWidth((int)this.width - 20);
                this.subClsDescs[i].setPos(0.0f, pos);
                this.subClsInfos[i].setRect(this.width - 20.0f, this.subClsDescs[i].top() + (this.subClsDescs[i].height() - 20.0f) / 2.0f, 20.0f, 20.0f);
                pos = this.subClsDescs[i].bottom() + (float)(4 * MARGIN);
            }
            this.height = Math.max(this.height, pos - (float)(4 * MARGIN));
        }
    }

    private static class ArmorAbilityInfoTab
    extends Component {
        private RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "abilities", new Object[0])), 9);
        private RenderedTextBlock message;
        private RenderedTextBlock[] abilityDescs;
        private IconButton[] abilityInfos;

        public ArmorAbilityInfoTab(final HeroClass cls) {
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "abilities_msg", new Object[0]), 6);
            this.add(this.message);
            final ArmorAbility[] abilities = cls.armorAbilities();
            this.abilityDescs = new RenderedTextBlock[abilities.length];
            this.abilityInfos = new IconButton[abilities.length];
            for (int i = 0; i < abilities.length; ++i) {
                this.abilityDescs[i] = PixelScene.renderTextBlock(abilities[i].shortDesc(), 6);
                final int finalI = i;
                this.abilityInfos[i] = new IconButton(Icons.get(Icons.INFO)){

                    @Override
                    protected void onClick() {
                        Game.scene().addToFront(new WndInfoArmorAbility(cls, abilities[finalI]));
                    }
                };
                this.add(this.abilityDescs[i]);
                this.add(this.abilityInfos[i]);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            this.title.setPos((this.width - this.title.width()) / 2.0f, MARGIN);
            this.message.maxWidth((int)this.width);
            this.message.setPos(0.0f, this.title.bottom() + (float)(4 * MARGIN));
            float pos = this.message.bottom() + (float)(4 * MARGIN);
            for (int i = 0; i < this.abilityDescs.length; ++i) {
                this.abilityDescs[i].maxWidth((int)this.width - 20);
                this.abilityDescs[i].setPos(0.0f, pos);
                this.abilityInfos[i].setRect(this.width - 20.0f, this.abilityDescs[i].top() + (this.abilityDescs[i].height() - 20.0f) / 2.0f, 20.0f, 20.0f);
                pos = this.abilityDescs[i].bottom() + (float)(4 * MARGIN);
            }
            this.height = Math.max(this.height, pos - (float)(4 * MARGIN));
        }
    }
}

