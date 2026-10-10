/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.RightClickMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;
import java.util.ArrayList;

public class WndClericSpells
extends Window {
    protected static final int WIDTH = 120;
    public static int BTN_SIZE = 20;

    public WndClericSpells(final HolyTome tome, final Hero cleric, final boolean info) {
        IconTitle title = !info ? new IconTitle(new ItemSprite(tome), Messages.titleCase(Messages.get(this, "cast_title", new Object[0]))) : new IconTitle(Icons.INFO.get(), Messages.titleCase(Messages.get(this, "info_title", new Object[0])));
        title.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(title);
        IconButton btnInfo = new IconButton(info ? new ItemSprite(tome) : Icons.INFO.get()){

            @Override
            protected void onClick() {
                GameScene.show(new WndClericSpells(tome, cleric, !info));
                WndClericSpells.this.hide();
            }
        };
        btnInfo.setRect(104.0f, 0.0f, 16.0f, 16.0f);
        this.add(btnInfo);
        RenderedTextBlock msg = info ? PixelScene.renderTextBlock(Messages.get(this, "info_desc", new Object[0]), 6) : (DeviceCompat.isDesktop() ? PixelScene.renderTextBlock(Messages.get(this, "cast_desc_desktop", new Object[0]), 6) : PixelScene.renderTextBlock(Messages.get(this, "cast_desc_mobile", new Object[0]), 6));
        msg.maxWidth(120);
        msg.setPos(0.0f, title.bottom() + 4.0f);
        this.add(msg);
        int top = (int)msg.bottom() + 4;
        for (int i = 1; i <= 4; ++i) {
            ArrayList<ClericSpell> spells = ClericSpell.getSpellList(cleric, i);
            if (!spells.isEmpty() && i != 1) {
                ColorBlock sep = new ColorBlock(120.0f, 1.0f, -16777216);
                sep.y = top += BTN_SIZE + 2;
                this.add(sep);
                top += 3;
            }
            ArrayList<SpellButton> spellBtns = new ArrayList<SpellButton>();
            for (ClericSpell spell : spells) {
                SpellButton spellButton = new SpellButton(spell, tome, info);
                this.add(spellButton);
                spellBtns.add(spellButton);
            }
            int left = 2 + (120 - spellBtns.size() * (BTN_SIZE + 4)) / 2;
            for (IconButton iconButton : spellBtns) {
                iconButton.setRect(left, top, BTN_SIZE, BTN_SIZE);
                left = (int)((float)left + (iconButton.width() + 4.0f));
            }
        }
        this.resize(120, top + BTN_SIZE);
        if (SPDSettings.interfaceSize() != 2) {
            this.offset(0, GameScene.uiCamera.height / 2 - 30 - this.height / 2);
        }
    }

    public class SpellButton
    extends IconButton {
        ClericSpell spell;
        HolyTome tome;
        boolean info;
        NinePatch bg;

        public SpellButton(ClericSpell spell, HolyTome tome, boolean info) {
            super(new HeroIcon(spell));
            this.spell = spell;
            this.tome = tome;
            this.info = info;
            if (!tome.canCast(Dungeon.hero, spell)) {
                this.icon.alpha(0.3f);
            } else if (spell == GuidingLight.INSTANCE && spell.chargeUse(Dungeon.hero) == 0.0f) {
                this.icon.brightness(3.0f);
            }
            this.bg = Chrome.get(Chrome.Type.TOAST);
            this.addToBack(this.bg);
        }

        @Override
        protected void onPointerDown() {
            super.onPointerDown();
            if (this.spell == GuidingLight.INSTANCE && this.spell.chargeUse(Dungeon.hero) == 0.0f) {
                this.icon.brightness(4.0f);
            }
        }

        @Override
        protected void onPointerUp() {
            super.onPointerUp();
            if (!this.tome.canCast(Dungeon.hero, this.spell)) {
                this.icon.alpha(0.3f);
            } else if (this.spell == GuidingLight.INSTANCE && this.spell.chargeUse(Dungeon.hero) == 0.0f) {
                this.icon.brightness(3.0f);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            if (this.bg != null) {
                this.bg.size(this.width, this.height);
                this.bg.x = this.x;
                this.bg.y = this.y;
            }
        }

        @Override
        protected void onClick() {
            if (this.info) {
                GameScene.show(new WndTitledMessage(new HeroIcon(this.spell), Messages.titleCase(this.spell.name()), this.spell.desc()));
            } else {
                WndClericSpells.this.hide();
                if (!this.tome.canCast(Dungeon.hero, this.spell)) {
                    GLog.w(Messages.get(HolyTome.class, "no_spell", new Object[0]), new Object[0]);
                } else {
                    this.spell.onCast(this.tome, Dungeon.hero);
                    if (this.spell.targetingFlags() != -1 && Dungeon.quickslot.contains(this.tome)) {
                        this.tome.targetingSpell = this.spell;
                        QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(this.tome));
                    }
                }
            }
        }

        @Override
        protected boolean onLongClick() {
            WndClericSpells.this.hide();
            this.tome.setQuickSpell(this.spell);
            return true;
        }

        @Override
        protected void onRightClick() {
            super.onRightClick();
            RightClickMenu r = new RightClickMenu(new Image(this.icon), Messages.titleCase(this.spell.name()), new String[]{Messages.get(WndClericSpells.class, "cast", new Object[0]), Messages.get(WndClericSpells.class, "info", new Object[0]), Messages.get(WndClericSpells.class, "quick_cast", new Object[0])}){

                @Override
                public void onSelect(int index) {
                    switch (index) {
                        default: {
                            break;
                        }
                        case 0: {
                            WndClericSpells.this.hide();
                            if (!SpellButton.this.tome.canCast(Dungeon.hero, SpellButton.this.spell)) {
                                GLog.w(Messages.get(HolyTome.class, "no_spell", new Object[0]), new Object[0]);
                                break;
                            }
                            SpellButton.this.spell.onCast(SpellButton.this.tome, Dungeon.hero);
                            if (SpellButton.this.spell.targetingFlags() == -1 || !Dungeon.quickslot.contains(SpellButton.this.tome)) break;
                            SpellButton.this.tome.targetingSpell = SpellButton.this.spell;
                            QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(SpellButton.this.tome));
                            break;
                        }
                        case 1: {
                            GameScene.show(new WndTitledMessage(new HeroIcon(SpellButton.this.spell), Messages.titleCase(SpellButton.this.spell.name()), SpellButton.this.spell.desc()));
                            break;
                        }
                        case 2: {
                            WndClericSpells.this.hide();
                            SpellButton.this.tome.setQuickSpell(SpellButton.this.spell);
                        }
                    }
                }
            };
            this.parent.addToFront(r);
            r.camera = this.camera();
            PointF mousePos = PointerEvent.currentHoverPos();
            mousePos = this.camera.screenToCamera((int)mousePos.x, (int)mousePos.y);
            r.setPos(mousePos.x - 3.0f, mousePos.y - 3.0f);
        }

        @Override
        protected String hoverText() {
            return "_" + Messages.titleCase(this.spell.name()) + "_\n" + this.spell.shortDesc();
        }
    }
}

