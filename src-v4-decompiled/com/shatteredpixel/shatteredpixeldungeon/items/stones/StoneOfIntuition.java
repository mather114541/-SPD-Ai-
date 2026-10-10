/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class StoneOfIntuition
extends InventoryStone {
    private static Class curGuess = null;

    public StoneOfIntuition() {
        this.image = ItemSpriteSheet.STONE_INTUITION;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        if (item instanceof Ring) {
            return !((Ring)item).isKnown();
        }
        if (item instanceof Potion) {
            return !((Potion)item).isKnown();
        }
        if (item instanceof Scroll) {
            return !((Scroll)item).isKnown();
        }
        return false;
    }

    @Override
    protected void onItemSelected(Item item) {
        GameScene.show(new WndGuess(item));
    }

    @Override
    public String desc() {
        Object text = super.desc();
        if (Dungeon.hero != null) {
            text = Dungeon.hero.buff(IntuitionUseTracker.class) == null ? (String)text + "\n\n" + Messages.get(this, "break_info", new Object[0]) : (String)text + "\n\n" + Messages.get(this, "break_warn", new Object[0]);
        }
        return text;
    }

    public class WndGuess
    extends Window {
        private static final int WIDTH = 120;
        private static final int BTN_SIZE = 20;

        public WndGuess(final Item item) {
            float left;
            int rows;
            IconTitle titlebar = new IconTitle();
            titlebar.icon(new ItemSprite(item));
            titlebar.label(Messages.titleCase(item.name()));
            titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
            this.add(titlebar);
            RenderedTextBlock text = PixelScene.renderTextBlock(6);
            text.text(Messages.get(this, "text", new Object[0]));
            text.setPos(0.0f, titlebar.bottom() + 2.0f);
            text.maxWidth(120);
            this.add(text);
            final RedButton guess = new RedButton(""){

                @Override
                protected void onClick() {
                    super.onClick();
                    StoneOfIntuition.this.useAnimation();
                    if (item.getClass() == curGuess) {
                        if (item instanceof Ring) {
                            ((Ring)item).setKnown();
                            Item.updateQuickslot();
                        } else {
                            item.identify();
                        }
                        GLog.p(Messages.get(WndGuess.class, "correct", new Object[0]), new Object[0]);
                        curUser.sprite.parent.add(new Identification(curUser.sprite.center().offset(0.0f, -16.0f)));
                    } else {
                        GLog.w(Messages.get(WndGuess.class, "incorrect", new Object[0]), new Object[0]);
                    }
                    if (!StoneOfIntuition.this.anonymous) {
                        Catalog.countUse(StoneOfIntuition.class);
                        if (curUser.buff(IntuitionUseTracker.class) == null) {
                            Buff.affect(curUser, IntuitionUseTracker.class);
                        } else {
                            curItem.detach(curUser.belongings.backpack);
                            curUser.buff(IntuitionUseTracker.class).detach();
                        }
                        Talent.onRunestoneUsed(curUser, curUser.pos, StoneOfIntuition.class);
                    }
                    curGuess = null;
                    WndGuess.this.hide();
                }
            };
            guess.visible = false;
            guess.icon(new ItemSprite(item));
            guess.enable(false);
            guess.setRect(0.0f, 80.0f, 120.0f, 20.0f);
            this.add(guess);
            float top = text.bottom() + 5.0f;
            int placed = 0;
            ArrayList<Class<Item>> unIDed = new ArrayList<Class<Item>>();
            if (item.isIdentified()) {
                this.hide();
                return;
            }
            if (item instanceof Potion) {
                if (item instanceof ExoticPotion) {
                    for (Class<Item> clazz : Potion.getUnknown()) {
                        unIDed.add(ExoticPotion.regToExo.get(clazz));
                    }
                } else {
                    unIDed.addAll(Potion.getUnknown());
                }
            } else if (item instanceof Scroll) {
                if (item instanceof ExoticScroll) {
                    for (Class<Item> clazz : Scroll.getUnknown()) {
                        unIDed.add(ExoticScroll.regToExo.get(clazz));
                    }
                } else {
                    unIDed.addAll(Scroll.getUnknown());
                }
            } else if (item instanceof Ring) {
                unIDed.addAll(Ring.getUnknown());
            } else {
                this.hide();
                return;
            }
            if (unIDed.size() <= 5) {
                rows = 1;
                top += 10.0f;
                left = (float)(120 - 20 * unIDed.size()) / 2.0f;
            } else {
                rows = 2;
                left = (float)(120 - 20 * ((unIDed.size() + 1) / 2)) / 2.0f;
            }
            for (final Class<Item> clazz : unIDed) {
                IconButton btn = new IconButton(){

                    @Override
                    protected void onClick() {
                        curGuess = clazz;
                        guess.visible = true;
                        guess.text(Messages.titleCase(Messages.get(curGuess, "name", new Object[0])));
                        guess.enable(true);
                        super.onClick();
                    }
                };
                Image im = new Image("sprites/item_icons.png");
                im.frame(ItemSpriteSheet.Icons.film.get(Reflection.newInstance(clazz).icon));
                im.scale.set(2.0f);
                btn.icon(im);
                btn.setRect(left + (float)(placed * 20), top, 20.0f, 20.0f);
                this.add(btn);
                if (rows != 2 || ++placed != (unIDed.size() + 1) / 2) continue;
                placed = 0;
                if (unIDed.size() % 2 == 1) {
                    left += 10.0f;
                }
                top += 20.0f;
            }
            this.resize(120, 100);
        }
    }

    public static class IntuitionUseTracker
    extends Buff {
        public IntuitionUseTracker() {
            this.revivePersists = true;
        }
    }
}

