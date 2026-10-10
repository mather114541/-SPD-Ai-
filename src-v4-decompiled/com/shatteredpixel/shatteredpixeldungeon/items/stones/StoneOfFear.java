/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;

public class StoneOfFear
extends Runestone {
    public StoneOfFear() {
        this.image = ItemSpriteSheet.STONE_FEAR;
    }

    @Override
    protected void activate(int cell) {
        Char ch = Actor.findChar(cell);
        if (ch != null && ch.alignment != Char.Alignment.ALLY) {
            Buff.affect((Char)ch, Terror.class, (float)20.0f).object = curUser.id();
        }
        new Flare(5, 16.0f).color(0xFF0000, true).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2.0f);
        Sample.INSTANCE.play("sounds/read.mp3");
    }
}

