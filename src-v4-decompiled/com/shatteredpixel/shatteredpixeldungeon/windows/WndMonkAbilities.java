/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndMonkAbilities
extends Window {
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 180;
    private static final int MARGIN = 2;
    MonkEnergy.MonkAbility abilityBeingUsed;
    private CellSelector.Listener listener = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {
            WndMonkAbilities.this.abilityBeingUsed.doAbility(Dungeon.hero, cell);
        }

        @Override
        public String prompt() {
            return WndMonkAbilities.this.abilityBeingUsed.targetingPrompt();
        }
    };

    public WndMonkAbilities(MonkEnergy energyBuff) {
        int width = PixelScene.landscape() ? 180 : 120;
        float pos = 2.0f;
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title", new Object[0])), 9);
        title.hardlight(0xFFFF44);
        title.setPos(((float)width - title.width()) / 2.0f, pos);
        title.maxWidth(width - 4);
        this.add(title);
        pos = title.bottom() + 6.0f;
        for (final MonkEnergy.MonkAbility abil : MonkEnergy.MonkAbility.abilities) {
            String text = "_" + Messages.titleCase(abil.name()) + " " + Messages.get(this, "energycost", abil.energyCost()) + ":_ " + abil.desc();
            RedButton moveBtn = new RedButton(text, 6){

                @Override
                protected void onClick() {
                    super.onClick();
                    WndMonkAbilities.this.hide();
                    if (abil.targetingPrompt() != null) {
                        WndMonkAbilities.this.abilityBeingUsed = abil;
                        GameScene.selectCell(WndMonkAbilities.this.listener);
                    } else {
                        abil.doAbility(Dungeon.hero, null);
                    }
                }
            };
            moveBtn.leftJustify = true;
            moveBtn.multiline = true;
            moveBtn.setSize(width, moveBtn.reqHeight());
            moveBtn.setRect(0.0f, pos, width, moveBtn.reqHeight());
            moveBtn.enable(abil.usable(energyBuff));
            this.add(moveBtn);
            pos = moveBtn.bottom() + 2.0f;
        }
        this.resize(width, (int)pos);
    }
}

