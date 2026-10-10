/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;

public class ResumeIndicator
extends Tag {
    private Image icon;

    public ResumeIndicator() {
        super(10725013);
        this.setSize(SIZE, SIZE);
        this.visible = false;
    }

    @Override
    public GameAction keyAction() {
        return SPDAction.TAG_RESUME;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.icon = Icons.get(Icons.ARROW);
        this.add(this.icon);
    }

    @Override
    protected void layout() {
        super.layout();
        this.icon.x = !this.flipped ? this.x + ((float)SIZE - this.icon.width()) / 2.0f + 1.0f : this.x + this.width - ((float)SIZE + this.icon.width()) / 2.0f - 1.0f;
        this.icon.y = this.y + (this.height - this.icon.height) / 2.0f;
        PixelScene.align(this.icon);
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (Dungeon.hero.ready) {
            Dungeon.hero.resume();
        }
    }

    @Override
    protected String hoverText() {
        return Messages.titleCase(Messages.get(WndKeyBindings.class, "tag_resume", new Object[0]));
    }

    @Override
    public void update() {
        if (!Dungeon.hero.isAlive()) {
            this.visible = false;
        } else if (this.visible != (Dungeon.hero.lastAction != null)) {
            boolean bl = this.visible = Dungeon.hero.lastAction != null;
            if (this.visible) {
                this.flash();
            }
        }
        super.update();
    }
}

