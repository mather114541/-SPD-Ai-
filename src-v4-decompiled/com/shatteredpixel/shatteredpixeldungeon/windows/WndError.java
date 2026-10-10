/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

public class WndError
extends WndTitledMessage {
    public WndError(String message) {
        super(Icons.WARNING.get(), Messages.get(WndError.class, "title", new Object[0]), message);
    }
}

