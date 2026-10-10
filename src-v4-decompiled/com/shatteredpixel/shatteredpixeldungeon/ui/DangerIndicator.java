/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;

public class DangerIndicator
extends Tag {
    public static final int COLOR = 12597304;
    private BitmapText number;
    private Image icon;
    private int enemyIndex = 0;
    private int lastNumber = -1;
    public static int HEIGHT = 16;

    public DangerIndicator() {
        super(12597304);
        this.setSize(SIZE, HEIGHT);
        this.visible = false;
    }

    @Override
    public GameAction keyAction() {
        return SPDAction.CYCLE;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.number = new BitmapText(PixelScene.pixelFont);
        this.add(this.number);
        this.icon = Icons.SKULL.get();
        this.add(this.icon);
    }

    @Override
    protected void layout() {
        super.layout();
        this.icon.x = this.left() + 14.0f;
        this.icon.y = this.y + (this.height - this.icon.height) / 2.0f;
        this.placeNumber();
    }

    private void placeNumber() {
        this.number.x = this.left() + 13.0f - this.number.width();
        this.number.y = this.y + (this.height - this.number.baseLine()) / 2.0f;
        PixelScene.align(this.number);
    }

    @Override
    public void update() {
        if (Dungeon.hero.isAlive()) {
            int v = Dungeon.hero.visibleEnemies();
            if (v != this.lastNumber) {
                this.lastNumber = v;
                this.visible = this.lastNumber > 0;
                if (this.visible) {
                    this.number.text(Integer.toString(this.lastNumber));
                    this.number.measure();
                    this.placeNumber();
                    this.flash();
                }
            }
        } else {
            this.visible = false;
        }
        super.update();
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (Dungeon.hero.visibleEnemies() > 0) {
            Mob target = Dungeon.hero.visibleEnemy(++this.enemyIndex);
            QuickSlotButton.target(target);
            if (Dungeon.hero.canAttack(target)) {
                AttackIndicator.target(target);
            }
            if (Dungeon.hero.curAction == null && target.sprite != null) {
                Camera.main.panFollow(target.sprite, 5.0f);
            }
        }
    }

    @Override
    protected String hoverText() {
        return Messages.titleCase(Messages.get(WndKeyBindings.class, "tag_danger", new Object[0]));
    }
}

