/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class EnergyCrystal
extends Item {
    public EnergyCrystal() {
        this(1);
    }

    public EnergyCrystal(int value) {
        this.image = ItemSpriteSheet.ENERGY;
        this.stackable = true;
        this.quantity = value;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return new ArrayList<String>();
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        Catalog.setSeen(this.getClass());
        Statistics.itemTypesDiscovered.add(this.getClass());
        Dungeon.energy += this.quantity;
        GameScene.pickUp(this, pos);
        hero.sprite.showStatusWithIcon(0x44CCFF, Integer.toString(this.quantity), FloatingText.ENERGY, new Object[0]);
        hero.spendAndNext(this.pickupDelay());
        Sample.INSTANCE.play("sounds/item.mp3");
        EnergyCrystal.updateQuickslot();
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
}

