/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.journal;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public abstract class DocumentPage
extends Item {
    private String page;
    private static final String PAGE = "page";

    public DocumentPage() {
        this.image = ItemSpriteSheet.MASTERY;
    }

    public abstract Document document();

    public void page(String page) {
        this.page = page;
    }

    public String page() {
        return this.page;
    }

    @Override
    public final boolean doPickUp(Hero hero, int pos) {
        GameScene.pickUpJournal(this, pos);
        GameScene.flashForDocument(this.document(), this.page());
        if (this.document() == Document.ADVENTURERS_GUIDE) {
            WndJournal.last_index = 1;
        } else if (this.document() == Document.ALCHEMY_GUIDE) {
            WndJournal.last_index = 2;
            WndJournal.AlchemyTab.currentPageIdx = this.document().pageIdx(this.page());
        } else if (this.document().isLoreDoc()) {
            WndJournal.last_index = 3;
            WndJournal.CatalogTab.currentItemIdx = 3;
        }
        this.document().findPage(this.page);
        Sample.INSTANCE.play("sounds/item.mp3");
        hero.spendAndNext(this.pickupDelay());
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PAGE, this.page());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.page = bundle.getString(PAGE);
    }
}

