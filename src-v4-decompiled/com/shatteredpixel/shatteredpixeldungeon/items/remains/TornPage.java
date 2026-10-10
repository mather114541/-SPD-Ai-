/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class TornPage
extends RemainsItem {
    public TornPage() {
        this.image = ItemSpriteSheet.TORN_PAGE;
    }

    @Override
    protected void doEffect(Hero hero) {
        int toHeal = Math.round((float)hero.HT / 10.0f);
        hero.HP = Math.min(hero.HP + toHeal, hero.HT);
        hero.sprite.showStatusWithIcon(65280, Integer.toString(toHeal), FloatingText.HEALING, new Object[0]);
        Sample.INSTANCE.play("sounds/read.mp3");
    }
}

