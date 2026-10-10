/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfMagicMapping
extends Scroll {
    public ScrollOfMagicMapping() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_MAGICMAP;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfMagicMapping.curUser.belongings.backpack);
        int length = Dungeon.level.length();
        int[] map = Dungeon.level.map;
        boolean[] mapped = Dungeon.level.mapped;
        boolean[] discoverable = Dungeon.level.discoverable;
        boolean noticed = false;
        for (int i = 0; i < length; ++i) {
            int terr = map[i];
            if (!discoverable[i]) continue;
            mapped[i] = true;
            if ((Terrain.flags[terr] & 8) == 0) continue;
            Dungeon.level.discover(i);
            if (!Dungeon.level.heroFOV[i]) continue;
            GameScene.discoverTile(i, terr);
            ScrollOfMagicMapping.discover(i);
            noticed = true;
        }
        GameScene.updateFog();
        GLog.i(Messages.get(this, "layout", new Object[0]), new Object[0]);
        if (noticed) {
            Sample.INSTANCE.play("sounds/secret.mp3");
        }
        SpellSprite.show(curUser, 1);
        Sample.INSTANCE.play("sounds/read.mp3");
        this.identify();
        this.readAnimation();
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }

    public static void discover(int cell) {
        CellEmitter.get(cell).start(Speck.factory(101), 0.1f, 4);
    }
}

