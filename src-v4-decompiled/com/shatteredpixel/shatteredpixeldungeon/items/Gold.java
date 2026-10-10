/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Gold
extends Item {
    public Gold() {
        this(1);
    }

    public Gold(int value) {
        this.image = ItemSpriteSheet.GOLD;
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
        Dungeon.gold += this.quantity;
        Statistics.goldCollected += this.quantity;
        Badges.validateGoldCollected();
        GameScene.pickUp(this, pos);
        if (hero.sprite != null) hero.sprite.showStatusWithIcon(0xFFFF00, Integer.toString(this.quantity), FloatingText.GOLD, new Object[0]);
        hero.spendAndNext(this.pickupDelay());
        Sample.INSTANCE.play("sounds/gold.mp3", 1.0f, 1.0f, Random.Float(0.9f, 1.1f));
        Gold.updateQuickslot();
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
    public Item random() {
        this.quantity = Random.IntRange(30 + Dungeon.depth * 10, 60 + Dungeon.depth * 20);
        return this;
    }
}

