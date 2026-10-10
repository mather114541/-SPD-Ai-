/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesGrid;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesList;
import com.shatteredpixel.shatteredpixeldungeon.ui.CustomNoteButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickRecipe;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingGridPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournalItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collection;

public class WndJournal
extends WndTabbed {
    public static final int WIDTH_P = 126;
    public static final int HEIGHT_P = 180;
    public static final int WIDTH_L = 216;
    public static final int HEIGHT_L = 130;
    private static final int ITEM_HEIGHT = 18;
    private GuideTab guideTab;
    private AlchemyTab alchemyTab;
    private NotesTab notesTab;
    private CatalogTab catalogTab;
    private BadgesTab badgesTab;
    public static int last_index = 0;
    private static WndJournal INSTANCE = null;

    public WndJournal() {
        WndTabbed.Tab[] tabs;
        if (INSTANCE != null) {
            INSTANCE.hide();
        }
        int width = PixelScene.landscape() ? 216 : 126;
        int height = PixelScene.landscape() ? 130 : 180;
        this.resize(width, height);
        this.guideTab = new GuideTab();
        this.add(this.guideTab);
        this.guideTab.setRect(0.0f, 0.0f, width, height);
        this.guideTab.updateList();
        this.alchemyTab = new AlchemyTab();
        this.add(this.alchemyTab);
        this.alchemyTab.setRect(0.0f, 0.0f, width, height);
        this.notesTab = new NotesTab();
        this.add(this.notesTab);
        this.notesTab.setRect(0.0f, 0.0f, width, height);
        this.notesTab.updateList();
        this.catalogTab = new CatalogTab();
        this.add(this.catalogTab);
        this.catalogTab.setRect(0.0f, 0.0f, width, height);
        this.catalogTab.updateList();
        this.badgesTab = new BadgesTab();
        this.add(this.badgesTab);
        this.badgesTab.setRect(0.0f, 0.0f, width, height);
        this.badgesTab.updateList();
        for (WndTabbed.Tab tab : tabs = new WndTabbed.Tab[]{new WndTabbed.IconTab(Icons.JOURNAL.get()){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndJournal.this.notesTab.active = WndJournal.this.notesTab.visible = value;
                if (value) {
                    last_index = 0;
                }
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.this.notesTab, "title", new Object[0]);
            }
        }, new WndTabbed.IconTab((Image)new ItemSprite(ItemSpriteSheet.MASTERY, null)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndJournal.this.guideTab.active = WndJournal.this.guideTab.visible = value;
                if (value) {
                    last_index = 1;
                }
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.this.guideTab, "title", new Object[0]);
            }
        }, new WndTabbed.IconTab(Icons.ALCHEMY.get()){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndJournal.this.alchemyTab.active = WndJournal.this.alchemyTab.visible = value;
                if (value) {
                    last_index = 2;
                }
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.this.alchemyTab, "title", new Object[0]);
            }
        }, new WndTabbed.IconTab(Icons.CATALOG.get()){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndJournal.this.catalogTab.active = WndJournal.this.catalogTab.visible = value;
                if (value) {
                    last_index = 3;
                }
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.this.catalogTab, "title", new Object[0]);
            }
        }, new WndTabbed.IconTab(Icons.BADGES.get()){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndJournal.this.badgesTab.active = WndJournal.this.badgesTab.visible = value;
                if (value) {
                    last_index = 4;
                }
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndJournal.this.badgesTab, "title", new Object[0]);
            }
        }}) {
            this.add(tab);
        }
        this.layoutTabs();
        this.select(last_index);
        INSTANCE = this;
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed && KeyBindings.getActionForKey(event) == SPDAction.JOURNAL) {
            this.onBackPressed();
            return true;
        }
        return super.onSignal(event);
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        this.guideTab.layout();
        this.alchemyTab.layout();
        this.notesTab.layout();
        this.catalogTab.layout();
    }

    private static void addGridItems(ScrollingGridPane grid, Collection<Class<?>> classes) {
        for (Class<?> itemClass : classes) {
            boolean seen = Catalog.isSeen(itemClass);
            ItemSprite sprite = null;
            Image secondIcon = null;
            String title = "";
            Object desc = "";
            if (Item.class.isAssignableFrom(itemClass)) {
                Item item = (Item)Reflection.newInstance(itemClass);
                if (seen) {
                    if (item instanceof Ring) {
                        ((Ring)item).anonymize();
                    } else if (item instanceof Potion) {
                        ((Potion)item).anonymize();
                    } else if (item instanceof Scroll) {
                        ((Scroll)item).anonymize();
                    }
                }
                sprite = new ItemSprite(item.image, seen ? item.glowing() : null);
                if (!seen) {
                    if (item instanceof ExoticPotion) {
                        sprite.frame(ItemSpriteSheet.POTION_CRIMSON);
                    }
                    sprite.lightness(0.0f);
                    title = "???";
                    desc = Messages.get(CatalogTab.class, "not_seen_item", new Object[0]);
                    desc = (String)desc + "\n\n" + Messages.get(item, "discover_hint", new Object[0]);
                } else {
                    title = Messages.titleCase(item.name());
                    desc = item instanceof ClassArmor || item instanceof SpiritBow ? (String)desc + item.desc() : (String)desc + item.info();
                    if (Catalog.useCount(itemClass) > 1) {
                        desc = item.isUpgradable() || item instanceof Artifact ? (String)desc + "\n\n" + Messages.get(CatalogTab.class, "upgrade_count", Catalog.useCount(itemClass)) : (item instanceof Trinket ? (String)desc + "\n\n" + Messages.get(CatalogTab.class, "trinket_count", Catalog.useCount(itemClass)) : (item instanceof Gold ? (String)desc + "\n\n" + Messages.get(CatalogTab.class, "gold_count", Catalog.useCount(itemClass)) : (item instanceof EnergyCrystal ? (String)desc + "\n\n" + Messages.get(CatalogTab.class, "energy_count", Catalog.useCount(itemClass)) : (String)desc + "\n\n" + Messages.get(CatalogTab.class, "use_count", Catalog.useCount(itemClass)))));
                    }
                    if (item instanceof MagesStaff) {
                        RectF frame = sprite.frame();
                        frame.top += frame.height() / 8.0f;
                        sprite.frame(frame);
                    }
                    if (item.icon != -1) {
                        secondIcon = new Image("sprites/item_icons.png");
                        secondIcon.frame(ItemSpriteSheet.Icons.film.get(item.icon));
                    }
                }
            } else if (Weapon.Enchantment.class.isAssignableFrom(itemClass)) {
                Weapon.Enchantment ench = (Weapon.Enchantment)Reflection.newInstance(itemClass);
                if (seen) {
                    sprite = new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD, ench.glowing());
                    title = Messages.titleCase(ench.name());
                    desc = ench.desc();
                } else {
                    sprite = new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD);
                    sprite.lightness(0.0f);
                    title = "???";
                    desc = Messages.get(CatalogTab.class, "not_seen_enchantment", new Object[0]);
                    desc = (String)desc + "\n\n" + Messages.get(ench, "discover_hint", new Object[0]);
                }
            } else if (Armor.Glyph.class.isAssignableFrom(itemClass)) {
                Armor.Glyph glyph = (Armor.Glyph)Reflection.newInstance(itemClass);
                if (seen) {
                    sprite = new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH, glyph.glowing());
                    title = Messages.titleCase(glyph.name());
                    desc = glyph.desc();
                } else {
                    sprite = new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH);
                    sprite.lightness(0.0f);
                    title = "???";
                    desc = Messages.get(CatalogTab.class, "not_seen_glyph", new Object[0]);
                    desc = (String)desc + "\n\n" + Messages.get(glyph, "discover_hint", new Object[0]);
                }
            }
            final String finalTitle = title;
            final String finalDesc = desc;
            ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite){

                @Override
                public boolean onClick(float x, float y) {
                    if (this.inside(x, y)) {
                        ItemSprite sprite = new ItemSprite();
                        ((Image)sprite).copy(this.icon);
                        if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                            GameScene.show(new WndJournalItem(sprite, finalTitle, finalDesc));
                        } else {
                            ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(sprite, finalTitle, finalDesc));
                        }
                        return true;
                    }
                    return false;
                }
            };
            if (secondIcon != null) {
                gridItem.addSecondIcon(secondIcon);
            }
            if (!seen) {
                gridItem.hardLightBG(2.0f, 1.0f, 2.0f);
            }
            grid.addItem(gridItem);
        }
    }

    private static void addGridEntities(ScrollingGridPane grid, Collection<Class<?>> classes) {
        for (Class<?> entityCls : classes) {
            final boolean seen = Bestiary.isSeen(entityCls);
            Mob mob = null;
            Image icon = null;
            String title = null;
            Object desc = null;
            if (Mob.class.isAssignableFrom(entityCls)) {
                mob = (Mob)Reflection.newInstance(entityCls);
                if (mob instanceof Mimic || mob instanceof Pylon || mob instanceof CrystalSpire) {
                    mob.alignment = Char.Alignment.ENEMY;
                }
                if (mob instanceof WandOfWarding.Ward) {
                    if (mob instanceof WandOfWarding.Ward.WardSentry) {
                        ((WandOfWarding.Ward)mob).upgrade(3);
                        ((WandOfWarding.Ward)mob).upgrade(3);
                        ((WandOfWarding.Ward)mob).upgrade(3);
                        ((WandOfWarding.Ward)mob).upgrade(3);
                    } else {
                        ((WandOfWarding.Ward)mob).upgrade(0);
                    }
                }
                CharSprite sprite = mob.sprite();
                sprite.idle();
                icon = new Image(sprite);
                if (seen) {
                    title = Messages.titleCase(mob.name());
                    desc = mob.description();
                    if (Bestiary.encounterCount(entityCls) > 1) {
                        desc = (String)desc + "\n\n" + Messages.get(CatalogTab.class, "enemy_count", Bestiary.encounterCount(entityCls));
                    }
                } else {
                    icon.lightness(0.0f);
                    title = "???";
                    desc = mob instanceof WandOfRegrowth.Lotus ? Messages.get(CatalogTab.class, "not_seen_plant", new Object[0]) : (mob.alignment == Char.Alignment.ENEMY ? Messages.get(CatalogTab.class, "not_seen_enemy", new Object[0]) : Messages.get(CatalogTab.class, "not_seen_ally", new Object[0]));
                    desc = (String)desc + "\n\n" + Messages.get(mob, "discover_hint", new Object[0]);
                }
                if (icon.width() >= 17.0f || icon.height() >= 17.0f) {
                    float hShrink;
                    RectF frame = icon.frame();
                    float wShrink = frame.width() * (1.0f - 17.0f / icon.width());
                    if (wShrink > 0.0f) {
                        frame.left += wShrink / 2.0f;
                        frame.right -= wShrink / 2.0f;
                    }
                    if ((hShrink = frame.height() * (1.0f - 17.0f / icon.height())) > 0.0f) {
                        frame.top += hShrink / 2.0f;
                        frame.bottom -= hShrink / 2.0f;
                    }
                    icon.frame(frame);
                }
            } else if (Trap.class.isAssignableFrom(entityCls)) {
                Trap trap = (Trap)Reflection.newInstance(entityCls);
                icon = TerrainFeaturesTilemap.getTrapVisual(trap);
                if (seen) {
                    title = Messages.titleCase(trap.name());
                    desc = trap.desc();
                    if (Bestiary.encounterCount(entityCls) > 1) {
                        desc = (String)desc + "\n\n" + Messages.get(CatalogTab.class, "trap_count", Bestiary.encounterCount(entityCls));
                    }
                } else {
                    icon.lightness(0.0f);
                    title = "???";
                    desc = Messages.get(CatalogTab.class, "not_seen_trap", new Object[0]);
                    desc = (String)desc + "\n\n" + Messages.get(trap, "discover_hint", new Object[0]);
                }
            } else if (Plant.class.isAssignableFrom(entityCls)) {
                Plant plant = (Plant)Reflection.newInstance(entityCls);
                icon = TerrainFeaturesTilemap.getPlantVisual(plant);
                if (seen) {
                    title = Messages.titleCase(plant.name());
                    desc = plant.desc();
                    if (Bestiary.encounterCount(entityCls) > 1) {
                        desc = (String)desc + "\n\n" + Messages.get(CatalogTab.class, "plant_count", Bestiary.encounterCount(entityCls));
                    }
                } else {
                    icon.lightness(0.0f);
                    title = "???";
                    desc = Messages.get(CatalogTab.class, "not_seen_plant", new Object[0]);
                    desc = (String)desc + "\n\n" + Messages.get(plant, "discover_hint", new Object[0]);
                }
            }
            final Mob finalMob = mob;
            final String finalTitle = title;
            final String finalDesc = desc;
            ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(icon){

                @Override
                public boolean onClick(float x, float y) {
                    if (this.inside(x, y)) {
                        Image image = seen && finalMob != null ? finalMob.sprite() : new Image(this.icon);
                        if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                            GameScene.show(new WndJournalItem(image, finalTitle, finalDesc));
                        } else {
                            ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(image, finalTitle, finalDesc));
                        }
                        return true;
                    }
                    return false;
                }
            };
            if (!seen) {
                gridItem.hardLightBG(2.0f, 1.0f, 2.0f);
            }
            grid.addItem(gridItem);
        }
    }

    private static void addGridDocuments(ScrollingGridPane grid, final Document doc) {
        for (final String page : doc.pageNames()) {
            Image sprite = doc.pageSprite(page);
            final boolean seen = doc.isPageFound(page);
            boolean read = doc.isPageRead(page);
            if (!seen) {
                sprite.lightness(0.0f);
            }
            ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite){

                @Override
                public boolean onClick(float x, float y) {
                    if (this.inside(x, y)) {
                        Image sprite = new Image(this.icon);
                        if (seen) {
                            if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                                GameScene.show(new WndStory(sprite, doc.pageTitle(page), doc.pageBody(page)));
                            } else {
                                ShatteredPixelDungeon.scene().addToFront(new WndStory(sprite, doc.pageTitle(page), doc.pageBody(page)));
                            }
                            doc.readPage(page);
                            this.hardLightBG(1.0f, 1.0f, 1.0f);
                        } else if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                            GameScene.show(new WndJournalItem(sprite, "???", Messages.get(CatalogTab.class, "not_seen_lore", new Object[0]) + "\n\n" + doc.discoverHint()));
                        } else {
                            ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(sprite, "???", Messages.get(CatalogTab.class, "not_seen_lore", new Object[0]) + "\n\n" + doc.discoverHint()));
                        }
                        return true;
                    }
                    return false;
                }
            };
            if (seen) {
                BitmapText text = new BitmapText(Integer.toString(doc.pageIdx(page) + 1), PixelScene.pixelFont);
                text.measure();
                gridItem.addSecondIcon(text);
                if (!read) {
                    gridItem.hardLightBG(0.6f, 1.0f, 2.0f);
                }
            } else {
                gridItem.hardLightBG(2.2f, 1.0f, 2.2f);
            }
            grid.addItem(gridItem);
        }
    }

    public static class GuideTab
    extends Component {
        private ScrollingListPane list;

        @Override
        protected void createChildren() {
            this.list = new ScrollingListPane();
            this.add(this.list);
        }

        @Override
        protected void layout() {
            super.layout();
            this.list.setRect(this.x, this.y, this.width, this.height);
        }

        public void updateList() {
            this.list.addTitle(Document.ADVENTURERS_GUIDE.title());
            for (final String page : Document.ADVENTURERS_GUIDE.pageNames()) {
                final boolean found = Document.ADVENTURERS_GUIDE.isPageFound(page);
                ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(Document.ADVENTURERS_GUIDE.pageSprite(page), null, found ? Messages.titleCase(Document.ADVENTURERS_GUIDE.pageTitle(page)) : Messages.titleCase(Messages.get(this, "missing", new Object[0]))){

                    @Override
                    public boolean onClick(float x, float y) {
                        if (this.inside(x, y) && found) {
                            ShatteredPixelDungeon.scene().addToFront(new WndStory(Document.ADVENTURERS_GUIDE.pageSprite(page), Document.ADVENTURERS_GUIDE.pageTitle(page), Document.ADVENTURERS_GUIDE.pageBody(page)));
                            Document.ADVENTURERS_GUIDE.readPage(page);
                            return true;
                        }
                        return false;
                    }
                };
                if (!found) {
                    item.hardlight(0x999999);
                    item.hardlightIcon(0x999999);
                }
                this.list.addItem(item);
            }
            this.list.setRect(this.x, this.y, this.width, this.height);
        }
    }

    public static class AlchemyTab
    extends Component {
        private RedButton[] pageButtons;
        private static final int NUM_BUTTONS = 9;
        private static final int[] sprites = new int[]{ItemSpriteSheet.SEED_HOLDER, ItemSpriteSheet.STONE_HOLDER, ItemSpriteSheet.FOOD_HOLDER, ItemSpriteSheet.POTION_HOLDER, ItemSpriteSheet.SCROLL_HOLDER, ItemSpriteSheet.BOMB_HOLDER, ItemSpriteSheet.MISSILE_HOLDER, ItemSpriteSheet.ELIXIR_HOLDER, ItemSpriteSheet.SPELL_HOLDER};
        public static int currentPageIdx = 0;
        private IconTitle title;
        private RenderedTextBlock body;
        private ScrollPane list;
        private ArrayList<QuickRecipe> recipes = new ArrayList();

        @Override
        protected void createChildren() {
            this.pageButtons = new RedButton[9];
            for (int i = 0; i < 9; ++i) {
                final int idx = i;
                this.pageButtons[i] = new RedButton(""){

                    @Override
                    protected void onClick() {
                        currentPageIdx = idx;
                        this.updateList();
                    }
                };
                if (Document.ALCHEMY_GUIDE.isPageFound(i)) {
                    this.pageButtons[i].icon(new ItemSprite(sprites[i], null));
                } else {
                    this.pageButtons[i].icon(new ItemSprite(ItemSpriteSheet.SOMETHING, null));
                    this.pageButtons[i].enable(false);
                }
                this.add(this.pageButtons[i]);
            }
            this.title = new IconTitle();
            this.title.icon(new ItemSprite(ItemSpriteSheet.ALCH_PAGE));
            this.title.visible = false;
            this.body = PixelScene.renderTextBlock(6);
            this.list = new ScrollPane(new Component());
            this.add(this.list);
        }

        @Override
        protected void layout() {
            super.layout();
            if (this.width() >= 180.0f) {
                float buttonWidth = this.width() / (float)this.pageButtons.length;
                for (int i = 0; i < 9; ++i) {
                    this.pageButtons[i].setRect(this.x + (float)i * buttonWidth, this.y, buttonWidth, 18.0f);
                    PixelScene.align(this.pageButtons[i]);
                }
            } else {
                float buttonWidth = this.width() / 5.0f;
                float y = 0.0f;
                float x = 0.0f;
                for (int i = 0; i < 9; ++i) {
                    this.pageButtons[i].setRect(this.x + x, this.y + y, buttonWidth, 18.0f);
                    PixelScene.align(this.pageButtons[i]);
                    x += buttonWidth;
                    if (i != 4) continue;
                    y += 18.0f;
                    x = 0.0f;
                    buttonWidth = this.width() / 4.0f;
                }
            }
            this.list.setRect(this.x, this.pageButtons[8].bottom() + 1.0f, this.width, this.height - this.pageButtons[8].bottom() + this.y - 1.0f);
            this.updateList();
        }

        public void updateList() {
            if (currentPageIdx != -1 && !Document.ALCHEMY_GUIDE.isPageFound(currentPageIdx)) {
                currentPageIdx = -1;
            }
            for (int i = 0; i < 9; ++i) {
                if (i == currentPageIdx) {
                    this.pageButtons[i].icon().color(0xFFFF44);
                    continue;
                }
                this.pageButtons[i].icon().resetColor();
            }
            if (currentPageIdx == -1) {
                return;
            }
            for (QuickRecipe r : this.recipes) {
                if (r == null) continue;
                r.killAndErase();
                r.destroy();
            }
            this.recipes.clear();
            Component content = this.list.content();
            content.clear();
            this.title.visible = true;
            this.title.label(Document.ALCHEMY_GUIDE.pageTitle(currentPageIdx));
            this.title.setRect(0.0f, 0.0f, this.width(), 10.0f);
            content.add(this.title);
            this.body.maxWidth((int)this.width());
            this.body.text(Document.ALCHEMY_GUIDE.pageBody(currentPageIdx));
            this.body.setPos(0.0f, this.title.bottom());
            content.add(this.body);
            Document.ALCHEMY_GUIDE.readPage(currentPageIdx);
            ArrayList<QuickRecipe> toAdd = QuickRecipe.getRecipes(currentPageIdx);
            float top = this.body.bottom() + 2.0f;
            ArrayList<QuickRecipe> toAddThisRow = new ArrayList<QuickRecipe>();
            while (!toAdd.isEmpty()) {
                float spacing;
                if (toAdd.get(0) == null) {
                    toAdd.remove(0);
                    top += 6.0f;
                }
                int w = 0;
                while (!toAdd.isEmpty() && toAdd.get(0) != null && (float)w + toAdd.get(0).width() <= this.width()) {
                    toAddThisRow.add(toAdd.remove(0));
                    w = (int)((float)w + ((QuickRecipe)toAddThisRow.get(0)).width());
                }
                float left = spacing = (this.width() - (float)w) / (float)(toAddThisRow.size() + 1);
                while (!toAddThisRow.isEmpty()) {
                    QuickRecipe r = (QuickRecipe)toAddThisRow.remove(0);
                    r.setPos(left, top);
                    left += r.width() + spacing;
                    if (!toAddThisRow.isEmpty()) {
                        ColorBlock spacer = new ColorBlock(1.0f, 16.0f, -14540254);
                        spacer.y = top;
                        spacer.x = left - spacing / 2.0f - 0.5f;
                        PixelScene.align(spacer);
                        content.add(spacer);
                    }
                    this.recipes.add(r);
                    content.add(r);
                }
                if (!toAdd.isEmpty() && toAdd.get(0) == null) {
                    toAdd.remove(0);
                }
                if (!toAdd.isEmpty() && toAdd.get(0) != null) {
                    ColorBlock spacer = new ColorBlock(this.width(), 1.0f, -14540254);
                    spacer.y = top + 16.0f;
                    spacer.x = 0.0f;
                    content.add(spacer);
                }
                top += 17.0f;
                toAddThisRow.clear();
            }
            content.setSize(this.width(), top -= 1.0f);
            this.list.setSize(this.list.width(), this.list.height());
            this.list.scrollTo(0.0f, 0.0f);
        }
    }

    private static class NotesTab
    extends Component {
        private ScrollingGridPane grid;
        private CustomNoteButton custom;

        private NotesTab() {
        }

        @Override
        protected void createChildren() {
            this.grid = new ScrollingGridPane();
            this.add(this.grid);
        }

        @Override
        protected void layout() {
            super.layout();
            this.grid.setRect(this.x, this.y, this.width, this.height);
        }

        private void updateList() {
            this.grid.addHeader("_" + Messages.get(this, "title", new Object[0]) + "_", 9, true);
            this.grid.addHeader(Messages.get(this, "desc", new Object[0]), 6, true);
            ArrayList<Notes.CustomRecord> customRecs = Notes.getRecords(Notes.CustomRecord.class);
            if (!customRecs.isEmpty()) {
                this.grid.addHeader("_" + Messages.get(this, "custom_notes", new Object[0]) + "_ (" + customRecs.size() + "/" + Notes.customRecordLimit() + ")");
                for (final Notes.CustomRecord rec : customRecs) {
                    ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){

                        @Override
                        public boolean onClick(float x, float y) {
                            if (this.inside(x, y)) {
                                GameScene.show(new CustomNoteButton.CustomNoteWindow(rec, INSTANCE));
                                return true;
                            }
                            return false;
                        }
                    };
                    Visual secondIcon = rec.secondIcon();
                    if (secondIcon != null) {
                        gridItem.addSecondIcon(secondIcon);
                    }
                    this.grid.addItem(gridItem);
                }
            }
            for (int i = Statistics.deepestFloor; i > 0; --i) {
                ArrayList<Notes.Record> recs = Notes.getRecords(i);
                if (i == Dungeon.depth) {
                    this.grid.addHeader("_" + Messages.get(this, "floor_header", i) + "_");
                } else {
                    this.grid.addHeader(Messages.get(this, "floor_header", i));
                }
                for (final Notes.Record rec : recs) {
                    ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){

                        @Override
                        public boolean onClick(float x, float y) {
                            if (this.inside(x, y)) {
                                GameScene.show(new WndJournalItem(rec.icon(), Messages.titleCase(rec.title()), rec.desc()));
                                return true;
                            }
                            return false;
                        }
                    };
                    Visual secondIcon = rec.secondIcon();
                    if (secondIcon != null) {
                        gridItem.addSecondIcon(secondIcon);
                    }
                    this.grid.addItem(gridItem);
                }
            }
            this.custom = new CustomNoteButton();
            this.grid.content().add(this.custom);
            this.custom.setPos(this.width - this.custom.width() - 1.0f, 0.0f);
            this.grid.setRect(this.x, this.y, this.width, this.height);
        }
    }

    public static class CatalogTab
    extends Component {
        private RedButton[] itemButtons;
        private static final int NUM_BUTTONS = 4;
        public static int currentItemIdx = 0;
        private static float[] scrollPositions = new float[4];
        private static final int EQUIP_IDX = 0;
        private static final int CONSUM_IDX = 1;
        private static final int BESTIARY_IDX = 2;
        private static final int LORE_IDX = 3;
        private ScrollingGridPane grid;

        @Override
        protected void createChildren() {
            this.itemButtons = new RedButton[4];
            for (int i = 0; i < 4; ++i) {
                final int idx = i;
                this.itemButtons[i] = new RedButton(""){

                    @Override
                    protected void onClick() {
                        currentItemIdx = idx;
                        this.updateList();
                    }
                };
                this.add(this.itemButtons[i]);
            }
            this.itemButtons[0].icon(new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER));
            this.itemButtons[1].icon(new ItemSprite(ItemSpriteSheet.POTION_HOLDER));
            this.itemButtons[2].icon(new ItemSprite(ItemSpriteSheet.MOB_HOLDER));
            this.itemButtons[3].icon(new ItemSprite(ItemSpriteSheet.DOCUMENT_HOLDER));
            this.grid = new ScrollingGridPane(){

                @Override
                public synchronized void update() {
                    super.update();
                    CatalogTab.scrollPositions[CatalogTab.currentItemIdx] = this.content.camera.scroll.y;
                }
            };
            this.add(this.grid);
        }

        @Override
        protected void layout() {
            super.layout();
            int perRow = 4;
            float buttonWidth = this.width() / (float)perRow;
            for (int i = 0; i < 4; ++i) {
                this.itemButtons[i].setRect(this.x + (float)(i % perRow) * buttonWidth, this.y + (float)(i / perRow * 18), buttonWidth, 18.0f);
                PixelScene.align(this.itemButtons[i]);
            }
            this.grid.setRect(this.x, this.itemButtons[3].bottom() + 1.0f, this.width, this.height - this.itemButtons[3].height() - 1.0f);
        }

        public void updateList() {
            int totalItems;
            this.grid.clear();
            for (int i = 0; i < 4; ++i) {
                if (i == currentItemIdx) {
                    this.itemButtons[i].icon().color(0xFFFF44);
                    continue;
                }
                this.itemButtons[i].icon().resetColor();
            }
            this.grid.scrollTo(0.0f, 0.0f);
            if (currentItemIdx == 0) {
                totalItems = 0;
                int totalSeen = 0;
                for (Catalog catalog : Catalog.equipmentCatalogs) {
                    totalItems += catalog.totalItems();
                    totalSeen += catalog.totalSeen();
                }
                this.grid.addHeader("_" + Messages.get(this, "title_equipment", new Object[0]) + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);
                for (Catalog catalog : Catalog.equipmentCatalogs) {
                    this.grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
                    WndJournal.addGridItems(this.grid, catalog.items());
                }
            } else if (currentItemIdx == 1) {
                totalItems = 0;
                int totalSeen = 0;
                for (Catalog catalog : Catalog.consumableCatalogs) {
                    totalItems += catalog.totalItems();
                    totalSeen += catalog.totalSeen();
                }
                this.grid.addHeader("_" + Messages.get(this, "title_consumables", new Object[0]) + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);
                for (Catalog catalog : Catalog.consumableCatalogs) {
                    this.grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
                    WndJournal.addGridItems(this.grid, catalog.items());
                }
            } else if (currentItemIdx == 2) {
                totalItems = 0;
                int totalSeen = 0;
                for (Bestiary bestiary : Bestiary.values()) {
                    totalItems += bestiary.totalEntities();
                    totalSeen += bestiary.totalSeen();
                }
                this.grid.addHeader("_" + Messages.get(this, "title_bestiary", new Object[0]) + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);
                for (Bestiary bestiary : Bestiary.values()) {
                    this.grid.addHeader("_" + Messages.titleCase(bestiary.title()) + "_ (" + bestiary.totalSeen() + "/" + bestiary.totalEntities() + "):");
                    WndJournal.addGridEntities(this.grid, bestiary.entities());
                }
            } else {
                totalItems = 0;
                int totalSeen = 0;
                for (Document doc : Document.values()) {
                    if (!doc.isLoreDoc()) continue;
                    for (String page : doc.pageNames()) {
                        ++totalItems;
                        if (!doc.isPageFound(page)) continue;
                        ++totalSeen;
                    }
                }
                this.grid.addHeader("_" + Messages.get(this, "title_lore", new Object[0]) + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);
                for (Document doc : Document.values()) {
                    if (!doc.isLoreDoc()) continue;
                    for (String page : doc.pageNames()) {
                        ++totalItems;
                        if (!doc.isPageFound(page)) continue;
                        ++totalSeen;
                    }
                }
                for (Document doc : Document.values()) {
                    if (!doc.isLoreDoc()) continue;
                    totalSeen = 0;
                    totalItems = 0;
                    for (String page : doc.pageNames()) {
                        ++totalItems;
                        if (!doc.isPageFound(page)) continue;
                        ++totalSeen;
                    }
                    if (!doc.anyPagesFound()) {
                        this.grid.addHeader("_???_ (" + totalSeen + "/" + totalItems + "):");
                    } else {
                        this.grid.addHeader("_" + Messages.titleCase(doc.title()) + "_ (" + totalSeen + "/" + totalItems + "):");
                    }
                    WndJournal.addGridDocuments(this.grid, doc);
                }
            }
            this.grid.setRect(this.x, this.itemButtons[3].bottom() + 1.0f, this.width, this.height - this.itemButtons[3].height() - 1.0f);
            this.grid.scrollTo(0.0f, scrollPositions[currentItemIdx]);
        }
    }

    public static class BadgesTab
    extends Component {
        private RedButton btnLocal;
        private RedButton btnGlobal;
        private RenderedTextBlock title;
        private Component badgesLocal;
        private Component badgesGlobal;
        public static boolean global = false;

        @Override
        protected void createChildren() {
            if (Dungeon.hero != null) {
                this.btnLocal = new RedButton(Messages.get(this, "this_run", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        global = false;
                        this.updateList();
                    }
                };
                this.btnLocal.icon(Icons.BADGES.get());
                this.add(this.btnLocal);
                this.btnGlobal = new RedButton(Messages.get(this, "overall", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        global = true;
                        this.updateList();
                    }
                };
                this.btnGlobal.icon(Icons.BADGES.get());
                this.add(this.btnGlobal);
                this.badgesLocal = Badges.filterReplacedBadges(false).size() <= 8 ? new BadgesList(false) : new BadgesGrid(false);
                this.add(this.badgesLocal);
            } else {
                this.title = PixelScene.renderTextBlock(Messages.get(this, "title_main_menu", new Object[0]), 9);
                this.title.hardlight(0xFFFF44);
                this.add(this.title);
            }
            this.badgesGlobal = new BadgesGrid(true);
            this.add(this.badgesGlobal);
        }

        @Override
        protected void layout() {
            super.layout();
            if (this.btnLocal != null) {
                this.btnLocal.setRect(this.x, this.y, this.width / 2.0f, 18.0f);
                this.btnGlobal.setRect(this.x + this.width / 2.0f, this.y, this.width / 2.0f, 18.0f);
                this.badgesLocal.setRect(this.x, this.y + 20.0f, this.width, this.height - 20.0f);
                this.badgesGlobal.setRect(this.x, this.y + 20.0f, this.width, this.height - 20.0f);
            } else {
                this.title.setPos(this.x + (this.width - this.title.width()) / 2.0f, this.y + (12.0f - this.title.height()) / 2.0f);
                this.badgesGlobal.setRect(this.x, this.y + 14.0f, this.width, this.height - 14.0f);
            }
        }

        private void updateList() {
            if (this.btnLocal != null) {
                this.badgesLocal.active = !global;
                this.badgesLocal.visible = this.badgesLocal.active;
                this.badgesGlobal.visible = this.badgesGlobal.active = global;
                this.btnLocal.textColor(global ? 0xFFFFFF : 0xFFFF44);
                this.btnGlobal.textColor(global ? 0xFFFF44 : 0xFFFFFF);
            } else {
                this.badgesGlobal.active = true;
                this.badgesGlobal.visible = true;
            }
        }
    }
}

