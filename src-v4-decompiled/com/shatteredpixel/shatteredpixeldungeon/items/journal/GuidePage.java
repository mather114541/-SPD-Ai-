/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.journal;

import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GuidePage
extends DocumentPage {
    public GuidePage() {
        this.image = ItemSpriteSheet.GUIDE_PAGE;
    }

    @Override
    public Document document() {
        return Document.ADVENTURERS_GUIDE;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.document().pageTitle(this.page()));
    }
}

