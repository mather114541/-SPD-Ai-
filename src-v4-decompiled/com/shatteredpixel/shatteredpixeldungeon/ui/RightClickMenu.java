/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemJournalButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Point;
import java.util.ArrayList;

public class RightClickMenu
extends Component {
    private NinePatch bg;
    private PointerArea blocker;
    private Image icon;
    private RenderedTextBlock titleText;
    private ColorBlock separator;
    private Button topRightButton;
    private RedButton[] buttons;
    private Item item;
    private Point childOffset;

    public RightClickMenu(Item item) {
        ArrayList<String> actions = item.actions(Dungeon.hero);
        if (actions.remove(item.defaultAction())) {
            actions.add(0, item.defaultAction());
        }
        String[] options = actions.toArray(new String[0]);
        this.item = item;
        this.setup(new ItemSprite(item), Messages.titleCase(item.name()), options);
    }

    public RightClickMenu(Image icon, String title, String ... options) {
        this.setup(icon, title, options);
    }

    private void setup(Image icon, String title, final String ... options) {
        this.bg = Chrome.get(Chrome.Type.TOAST_TR_HEAVY);
        this.add(this.bg);
        this.icon = icon;
        this.add(icon);
        this.titleText = PixelScene.renderTextBlock(title, 6);
        this.titleText.maxWidth(50);
        this.titleText.hardlight(0xFFFF44);
        this.add(this.titleText);
        this.separator = new ColorBlock(1.0f, 1.0f, -16777216);
        this.add(this.separator);
        this.blocker = new PointerArea(0.0f, 0.0f, 0.0f, 0.0f){

            @Override
            public boolean onSignal(PointerEvent event) {
                boolean hit;
                boolean bl = hit = event != null && this.target.overlapsScreenPoint((int)event.current.x, (int)event.current.y);
                if (event != null && event.type == PointerEvent.Type.HOVER && !hit) {
                    RightClickMenu.this.destroy();
                    RightClickMenu.this.killAndErase();
                } else if (hit) {
                    return true;
                }
                return false;
            }
        };
        this.blocker.target = this.bg;
        this.add(this.blocker);
        if (this.item != null && Dungeon.hero.belongings.contains(this.item)) {
            this.topRightButton = new ItemJournalButton(this.item, null){

                @Override
                protected void onClick() {
                    RightClickMenu.this.destroy();
                    RightClickMenu.this.killAndErase();
                    GameScene.centerNextWndOnInvPane();
                    super.onClick();
                }
            };
            this.topRightButton.setSize(16.0f, 16.0f);
            this.add(this.topRightButton);
        }
        this.buttons = new RedButton[options.length];
        for (int i = 0; i < options.length; ++i) {
            final int finalI = i;
            this.buttons[i] = new RedButton(options[finalI], 6){

                @Override
                protected void onClick() {
                    super.onClick();
                    if (RightClickMenu.this.childOffset != null) {
                        GameScene.nextWndOffset(RightClickMenu.this.childOffset);
                    }
                    if (RightClickMenu.this.item != null) {
                        RightClickMenu.this.item.execute(Dungeon.hero, options[finalI]);
                        if (options[finalI].equals(RightClickMenu.this.item.defaultAction()) && RightClickMenu.this.item.usesTargeting) {
                            InventoryPane.useTargeting();
                        }
                    }
                    RightClickMenu.this.onSelect(finalI);
                    RightClickMenu.this.destroy();
                    RightClickMenu.this.killAndErase();
                }
            };
            if (this.item != null) {
                if (options[i].equals(this.item.defaultAction())) {
                    this.buttons[i].textColor(0xFFFF44);
                }
                this.buttons[i].text(this.item.actionName(options[i], Dungeon.hero));
            }
            this.add(this.buttons[i]);
        }
    }

    public void setChildWindowOffset(Point offset) {
        this.childOffset = offset;
    }

    public void onSelect(int index) {
    }

    @Override
    protected void layout() {
        super.layout();
        float topHeight = Math.max(this.icon.height(), this.titleText.height());
        if (this.topRightButton != null) {
            topHeight = Math.max(topHeight, this.topRightButton.height());
        }
        this.height = 0.0f;
        this.height += (float)this.bg.marginVer();
        this.height += topHeight;
        this.height += 2.0f;
        this.height += (float)(13 * this.buttons.length);
        this.width = this.icon.width + 2.0f + this.titleText.width() + (float)this.bg.marginVer();
        if (this.topRightButton != null) {
            this.width += 2.0f + this.topRightButton.width();
        }
        for (RedButton button : this.buttons) {
            if (!(this.width < button.reqWidth() + (float)this.bg.marginHor())) continue;
            this.width = button.reqWidth() + (float)this.bg.marginHor();
        }
        if (this.x + this.width > (float)this.camera.width + this.camera.scroll.x) {
            this.x -= this.x + this.width - ((float)this.camera.width + this.camera.scroll.x);
        }
        if (this.y + this.height > (float)this.camera.height + this.camera.scroll.y) {
            this.y -= this.y + this.height - ((float)this.camera.height + this.camera.scroll.y);
        }
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.icon.x = this.x + (float)this.bg.marginLeft();
        this.icon.y = this.y + (float)this.bg.marginTop() + (topHeight - this.icon.height()) / 2.0f;
        this.titleText.setPos(this.icon.x + this.icon.width() + 2.0f, this.y + (float)this.bg.marginTop() + (topHeight - this.titleText.height()) / 2.0f);
        if (this.topRightButton != null) {
            this.topRightButton.setPos(this.titleText.right() + 2.0f, this.y + (float)this.bg.marginTop() + (topHeight - this.topRightButton.height()) / 2.0f);
        }
        this.separator.x = this.x + (float)this.bg.marginLeft();
        this.separator.y = this.y + (float)this.bg.marginTop() + topHeight + 1.0f;
        this.separator.size(this.width - (float)this.bg.marginHor(), 1.0f);
        float top = this.separator.y + 2.0f;
        for (RedButton button : this.buttons) {
            button.setRect(this.x + (float)this.bg.marginLeft(), top, this.width - (float)this.bg.marginHor(), 12.0f);
            top = button.bottom() + 1.0f;
        }
        this.bg.size(this.width, this.height);
    }
}

