/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndHardNotification
extends WndTitledMessage {
    RedButton btnOkay;
    private double timeLeft;
    private String btnMessage;
    float incTime = 0.0f;

    public WndHardNotification(Image icon, String title, String message, String btnMessage, int time) {
        this(new IconTitle(icon, title), message, btnMessage, time);
    }

    public WndHardNotification(Component titlebar, String message, String btnMessage, int time) {
        super(titlebar, message);
        this.timeLeft = time;
        this.btnMessage = btnMessage;
        this.btnOkay = new RedButton(btnMessage + " (" + time + ")"){

            @Override
            protected void onClick() {
                WndHardNotification.this.hide();
            }
        };
        this.btnOkay.setRect(0.0f, this.height + 2, this.width, 16.0f);
        this.btnOkay.enable(false);
        this.add(this.btnOkay);
        this.resize(this.width, (int)this.btnOkay.bottom());
    }

    @Override
    public void update() {
        super.update();
        this.incTime += Game.elapsed;
        if (this.timeLeft <= 0.0 && !this.btnOkay.active) {
            this.btnOkay.enable(true);
            this.btnOkay.text(this.btnMessage);
        } else if (this.timeLeft > 0.0 && this.incTime >= 1.0f) {
            this.timeLeft -= (double)this.incTime;
            this.incTime = 0.0f;
            this.btnOkay.text(this.btnMessage + " (" + (int)Math.ceil(this.timeLeft) + ")");
            this.btnOkay.enable(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.timeLeft <= 0.0) {
            super.onBackPressed();
        }
    }
}

