/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeIcons;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class ChangeButton
extends Component {
    protected Image icon;
    protected String title;
    protected String[] messages;

    public ChangeButton(ChangeIcons icon, String title, String ... message) {
        this(icon.get(), title, message);
    }

    public ChangeButton(Image icon, String title, String ... messages) {
        this.icon = icon;
        this.add(this.icon);
        this.title = Messages.titleCase(title);
        this.messages = messages;
        this.layout();
    }

    protected void onClick() {
        ChangesScene.showChangeInfo(new Image(this.icon), this.title, this.messages);
    }

    @Override
    protected void layout() {
        super.layout();
        this.icon.x = this.x + (this.width - this.icon.width()) / 2.0f;
        this.icon.y = this.y + (this.height - this.icon.height()) / 2.0f;
        PixelScene.align(this.icon);
    }
}

