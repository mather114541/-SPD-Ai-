/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public abstract class Key
extends Item {
    public static final float TIME_TO_UNLOCK = 1.0f;
    public int depth;
    private static final String DEPTH = "depth";

    public Key() {
        this.stackable = true;
        this.unique = true;
    }

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && ((Key)item).depth == this.depth;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        Catalog.setSeen(this.getClass());
        Statistics.itemTypesDiscovered.add(this.getClass());
        GameScene.pickUpJournal(this, pos);
        WndJournal.last_index = 0;
        Notes.add(this);
        Sample.INSTANCE.play("sounds/item.mp3");
        hero.spendAndNext(this.pickupDelay());
        GameScene.updateKeyDisplay();
        if (hero.buff(SkeletonKey.KeyReplacementTracker.class) != null) {
            hero.buff(SkeletonKey.KeyReplacementTracker.class).processExcessKeys();
        }
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, this.depth);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.depth = bundle.getInt(DEPTH);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}

