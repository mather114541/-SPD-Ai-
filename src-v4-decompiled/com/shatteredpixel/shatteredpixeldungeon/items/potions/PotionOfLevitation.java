/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfLevitation
extends Potion {
    public PotionOfLevitation() {
        this.icon = ItemSpriteSheet.Icons.POTION_LEVITATE;
    }

    @Override
    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            this.identify();
            Sample.INSTANCE.play("sounds/shatter.mp3");
            Sample.INSTANCE.play("sounds/gas.mp3");
        }
        GameScene.add(Blob.seed(cell, 1000, ConfusionGas.class));
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        Buff.prolong(hero, Levitation.class, 20.0f);
        GLog.i(Messages.get(this, "float", new Object[0]), new Object[0]);
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

