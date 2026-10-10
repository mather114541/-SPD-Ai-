/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndCombo
extends Window {
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 180;
    private static final int MARGIN = 2;

    public WndCombo(final Combo combo) {
        int width = PixelScene.landscape() ? 180 : 120;
        float pos = 2.0f;
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title", new Object[0])), 9);
        title.hardlight(0xFFFF44);
        title.setPos(((float)width - title.width()) / 2.0f, pos);
        title.maxWidth(width - 4);
        this.add(title);
        pos = title.bottom() + 6.0f;
        ItemSprite icon = Dungeon.hero.belongings.weapon() != null ? new ItemSprite(Dungeon.hero.belongings.weapon().image, null) : new ItemSprite(new Item(){
            {
                this.image = ItemSpriteSheet.WEAPON_HOLDER;
            }
        });
        for (final Combo.ComboMove move : Combo.ComboMove.values()) {
            String text = "_" + Messages.titleCase(move.title()) + " " + Messages.get(this, "combo_req", move.comboReq) + ":_ " + move.desc(combo.getComboCount());
            RedButton moveBtn = new RedButton(text, 6){

                @Override
                protected void onClick() {
                    super.onClick();
                    WndCombo.this.hide();
                    combo.useMove(move);
                }
            };
            moveBtn.leftJustify = true;
            moveBtn.multiline = true;
            moveBtn.setSize(width, moveBtn.reqHeight());
            moveBtn.setRect(0.0f, pos, width, moveBtn.reqHeight());
            moveBtn.enable(combo.canUseMove(move));
            this.add(moveBtn);
            pos = moveBtn.bottom() + 2.0f;
        }
        this.resize(width, (int)pos);
    }
}

