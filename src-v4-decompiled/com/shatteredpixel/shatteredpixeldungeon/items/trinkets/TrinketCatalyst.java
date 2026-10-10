/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.io.IOException;
import java.util.ArrayList;

public class TrinketCatalyst
extends Item {
    private ArrayList<Trinket> rolledTrinkets;
    private static final String ROLLED_TRINKETS = "rolled_trinkets";

    public TrinketCatalyst() {
        this.image = ItemSpriteSheet.TRINKET_CATA;
        this.unique = true;
        this.rolledTrinkets = new ArrayList();
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (super.doPickUp(hero, pos)) {
            if (!Document.ADVENTURERS_GUIDE.isPageRead("Alchemy")) {
                GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, "Alchemy");
            }
            return true;
        }
        return false;
    }

    public boolean hasRolledTrinkets() {
        return !this.rolledTrinkets.isEmpty();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (!this.rolledTrinkets.isEmpty()) {
            bundle.put(ROLLED_TRINKETS, this.rolledTrinkets);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.rolledTrinkets.clear();
        if (bundle.contains(ROLLED_TRINKETS)) {
            this.rolledTrinkets.addAll(bundle.getCollection(ROLLED_TRINKETS));
        }
    }

    public static class WndTrinket
    extends Window {
        private static final int WIDTH = 120;
        private static final int BTN_SIZE = 24;
        private static final int BTN_GAP = 4;
        private static final int GAP = 2;
        private static final int NUM_TRINKETS = 4;

        public WndTrinket(TrinketCatalyst cata) {
            IconTitle titlebar = new IconTitle();
            titlebar.icon(new ItemSprite(cata));
            titlebar.label(Messages.titleCase(Messages.get(TrinketCatalyst.class, "window_title", new Object[0])));
            titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
            this.add(titlebar);
            RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(TrinketCatalyst.class, "window_text", new Object[0]), 6);
            message.maxWidth(120);
            message.setPos(0.0f, titlebar.bottom() + 2.0f);
            this.add(message);
            while (cata.rolledTrinkets.size() < 4) {
                cata.rolledTrinkets.add((Trinket)Generator.random(Generator.Category.TRINKET));
            }
            for (int i = 0; i < 4; ++i) {
                ItemButton btnReward = new ItemButton(){

                    @Override
                    protected void onClick() {
                        ShatteredPixelDungeon.scene().addToFront(new RewardWindow(this.item()));
                    }
                };
                btnReward.item(cata.rolledTrinkets.get(i));
                btnReward.setRect((i + 1) * 116 / 4 - 24, message.top() + message.height() + 4.0f, 24.0f, 24.0f);
                this.add(btnReward);
            }
            this.resize(120, (int)(message.top() + message.height() + 8.0f + 24.0f));
        }

        @Override
        public void onBackPressed() {
        }

        private class RewardWindow
        extends WndInfoItem {
            public RewardWindow(final Item item) {
                super(item);
                RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm", new Object[0])){

                    @Override
                    protected void onClick() {
                        TrinketCatalyst cata;
                        RewardWindow.this.hide();
                        WndTrinket.this.hide();
                        Item result = item;
                        if (result instanceof RandomTrinket) {
                            result = Generator.random(Generator.Category.TRINKET);
                        }
                        if ((cata = Dungeon.hero.belongings.getItem(TrinketCatalyst.class)) != null) {
                            cata.detach(Dungeon.hero.belongings.backpack);
                            Catalog.countUse(cata.getClass());
                            result.identify();
                            if (ShatteredPixelDungeon.scene() instanceof AlchemyScene) {
                                ((AlchemyScene)ShatteredPixelDungeon.scene()).craftItem(null, result);
                            } else {
                                Sample.INSTANCE.play("sounds/puff.mp3");
                                if (result.doPickUp(Dungeon.hero)) {
                                    GLog.p(Messages.capitalize(Messages.get(Hero.class, "you_now_have", item.name())), new Object[0]);
                                } else {
                                    Dungeon.level.drop(result, Dungeon.hero.pos);
                                }
                                ++Statistics.itemsCrafted;
                                Badges.validateItemsCrafted();
                                try {
                                    Dungeon.saveAll();
                                }
                                catch (IOException e) {
                                    ShatteredPixelDungeon.reportException(e);
                                }
                            }
                        }
                    }
                };
                btnConfirm.setRect(0.0f, this.height + 2, this.width / 2 - 1, 16.0f);
                this.add(btnConfirm);
                RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel", new Object[0])){

                    @Override
                    protected void onClick() {
                        RewardWindow.this.hide();
                    }
                };
                btnCancel.setRect(btnConfirm.right() + 2.0f, this.height + 2, btnConfirm.width(), 16.0f);
                this.add(btnCancel);
                this.resize(this.width, (int)btnCancel.bottom());
            }
        }
    }

    public static class RandomTrinket
    extends Item {
        public RandomTrinket() {
            this.image = ItemSpriteSheet.SOMETHING;
        }
    }

    public static class Recipe
    extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.size() == 1 && ingredients.get(0) instanceof TrinketCatalyst;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 6;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            TrinketCatalyst newCata = (TrinketCatalyst)ingredients.get(0).duplicate();
            newCata.collect();
            ingredients.get(0).quantity(0);
            ShatteredPixelDungeon.scene().addToFront(new WndTrinket(newCata));
            try {
                Dungeon.saveAll();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return new Trinket.PlaceHolder();
        }
    }
}

