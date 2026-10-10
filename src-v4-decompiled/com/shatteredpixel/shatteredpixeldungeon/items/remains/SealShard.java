/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class SealShard
extends RemainsItem {
    public SealShard() {
        this.image = ItemSpriteSheet.SEAL_SHARD;
    }

    @Override
    protected void doEffect(Hero hero) {
        Buff.affect(hero, Barrier.class).incShield(Math.round((float)hero.HT / 5.0f));
        hero.sprite.showStatusWithIcon(65280, Integer.toString(Math.round((float)hero.HT / 5.0f)), FloatingText.SHIELDING, new Object[0]);
        Sample.INSTANCE.play("sounds/unlock.mp3");
    }
}

