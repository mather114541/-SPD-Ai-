/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

public class WndChooseAbility
extends Window {
    private static final int WIDTH = 130;
    private static final float GAP = 2.0f;

    public WndChooseAbility(final KingsCrown crown, final Armor armor, final Hero hero) {
        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(crown == null ? armor.image() : crown.image(), null));
        titlebar.label(Messages.titleCase(crown == null ? armor.name() : crown.name()));
        titlebar.setRect(0.0f, 0.0f, 130.0f, 0.0f);
        this.add(titlebar);
        IconButton random = new IconButton(Icons.SHUFFLE.get()){

            @Override
            protected void onClick() {
                super.onClick();
                GameScene.show(new WndOptions(Icons.SHUFFLE.get(), Messages.get(WndChooseAbility.class, "random_title", new Object[0]), Messages.get(WndChooseAbility.class, "random_sure", new Object[0]), new String[]{Messages.get(WndChooseAbility.class, "yes", new Object[0]), Messages.get(WndChooseAbility.class, "no", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            WndChooseAbility.this.hide();
                            ArmorAbility abil = Random.oneOf(hero.heroClass.armorAbilities());
                            crown.upgradeArmor(hero, armor, abil);
                            GameScene.show(new WndInfoArmorAbility(hero.heroClass, abil));
                        }
                    }
                });
            }

            @Override
            public void update() {
                if (Statistics.qualifiedForRandomVictoryBadge) {
                    this.icon.tint(1.0f, 1.0f, 1.0f, (float)Math.abs(Math.cos(4.71238898038469 * (double)Game.timeTotal) / 2.0));
                }
                super.update();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndChooseAbility.class, "random_title", new Object[0]);
            }
        };
        random.setRect(114.0f, 0.0f, 16.0f, 16.0f);
        if (crown != null) {
            this.add(random);
        }
        RenderedTextBlock body = PixelScene.renderTextBlock(6);
        if (crown != null) {
            body.text(Messages.get(this, "message", new Object[0]), 130);
        } else {
            body.text(Messages.get(this, "message_no_crown", new Object[0]), 130);
        }
        body.setPos(titlebar.left(), titlebar.bottom() + 2.0f);
        this.add(body);
        float pos = body.bottom() + 6.0f;
        for (final ArmorAbility ability : hero.heroClass.armorAbilities()) {
            RedButton abilityButton = new RedButton(ability.shortDesc(), 6){

                @Override
                protected void onClick() {
                    GameScene.show(new WndOptions(new HeroIcon(ability), Messages.titleCase(ability.name()), Messages.get(WndChooseAbility.this, "are_you_sure", new Object[0]), new String[]{Messages.get(WndChooseAbility.this, "yes", new Object[0]), Messages.get(WndChooseAbility.this, "no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            this.hide();
                            if (index == 0 && WndChooseAbility.this.parent != null) {
                                WndChooseAbility.this.hide();
                                if (crown != null) {
                                    crown.upgradeArmor(hero, armor, ability);
                                } else {
                                    new KingsCrown().upgradeArmor(hero, null, ability);
                                }
                                Statistics.qualifiedForRandomVictoryBadge = false;
                            }
                        }
                    });
                }
            };
            abilityButton.leftJustify = true;
            abilityButton.multiline = true;
            abilityButton.setSize(110.0f, abilityButton.reqHeight() + 2.0f);
            abilityButton.setRect(0.0f, pos, 110.0f, abilityButton.reqHeight() + 2.0f);
            this.add(abilityButton);
            IconButton abilityInfo = new IconButton(Icons.get(Icons.INFO)){

                @Override
                protected void onClick() {
                    GameScene.show(new WndInfoArmorAbility(Dungeon.hero.heroClass, ability));
                }
            };
            abilityInfo.setRect(110.0f, abilityButton.top() + (abilityButton.height() - 20.0f) / 2.0f, 20.0f, 20.0f);
            this.add(abilityInfo);
            pos = abilityButton.bottom() + 2.0f;
        }
        RedButton cancelButton = new RedButton(Messages.get(this, "cancel", new Object[0])){

            @Override
            protected void onClick() {
                WndChooseAbility.this.hide();
            }
        };
        cancelButton.setRect(0.0f, pos, 130.0f, 18.0f);
        this.add(cancelButton);
        pos = cancelButton.bottom() + 2.0f;
        this.resize(130, (int)pos);
    }
}

