/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;

public class ExitButton
extends IconButton {
    public ExitButton() {
        super(Icons.EXIT.get());
        this.width = PixelScene.landscape() && !DeviceCompat.isDesktop() ? 40.0f : 20.0f;
        this.height = 20.0f;
    }

    @Override
    protected void onClick() {
        if (Game.scene() instanceof TitleScene) {
            Game.instance.finish();
        } else {
            ShatteredPixelDungeon.switchNoFade(TitleScene.class);
        }
    }

    @Override
    public GameAction keyAction() {
        return GameAction.BACK;
    }

    @Override
    protected String hoverText() {
        return Messages.titleCase(Messages.get(WndKeyBindings.class, "back", new Object[0]));
    }
}

