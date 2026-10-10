/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.journal;

import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AlchemyPage
extends DocumentPage {
    public AlchemyPage() {
        this.image = ItemSpriteSheet.ALCH_PAGE;
    }

    @Override
    public Document document() {
        return Document.ALCHEMY_GUIDE;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.document().pageTitle(this.page()));
    }
}

