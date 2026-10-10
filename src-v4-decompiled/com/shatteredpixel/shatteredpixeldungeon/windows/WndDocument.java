/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;

public class WndDocument
extends Window {
    private ScrollingListPane list = new ScrollingListPane();

    public WndDocument(final Document doc) {
        this.add(this.list);
        this.list.addTitle(Messages.titleCase(doc.title()));
        for (final String page : doc.pageNames()) {
            final boolean found = doc.isPageFound(page);
            ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(doc.pageSprite(), null, found ? Messages.titleCase(doc.pageTitle(page)) : Messages.titleCase(Messages.get(this, "missing", new Object[0]))){

                @Override
                public boolean onClick(float x, float y) {
                    if (this.inside(x, y) && found) {
                        ShatteredPixelDungeon.scene().addToFront(new WndStory(doc.pageSprite(page), doc.pageTitle(page), doc.pageBody(page)));
                        doc.readPage(page);
                        this.hardlight(0xFFFFFF);
                        return true;
                    }
                    return false;
                }
            };
            if (!found) {
                item.hardlight(0x999999);
                item.hardlightIcon(0x999999);
            } else if (!doc.isPageRead(page)) {
                item.hardlight(0xFFFF44);
            }
            this.list.addItem(item);
        }
        this.resize(120, Math.min(144, (int)this.list.content().height()));
        this.list.setRect(0.0f, 0.0f, this.width, this.height);
    }
}

