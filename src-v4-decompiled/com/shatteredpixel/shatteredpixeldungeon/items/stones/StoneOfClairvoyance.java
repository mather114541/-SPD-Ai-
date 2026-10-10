/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;

public class StoneOfClairvoyance
extends Runestone {
    private static final int DIST = 20;

    public StoneOfClairvoyance() {
        this.image = ItemSpriteSheet.STONE_CLAIRVOYANCE;
    }

    @Override
    protected void activate(int cell) {
        Point c = Dungeon.level.cellToPoint(cell);
        int[] rounding = ShadowCaster.rounding[20];
        boolean noticed = false;
        for (int y = Math.max(0, c.y - 20); y <= Math.min(Dungeon.level.height() - 1, c.y + 20); ++y) {
            int left;
            if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
                left = c.x - rounding[Math.abs(c.y - y)];
            } else {
                left = 20;
                while (rounding[left] < rounding[Math.abs(c.y - y)]) {
                    --left;
                }
                left = c.x - left;
            }
            int right = Math.min(Dungeon.level.width() - 1, c.x + c.x - left);
            left = Math.max(0, left);
            for (int curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); ++curr) {
                GameScene.checkedCell(curr, cell);
                Dungeon.level.mapped[curr] = true;
                if (!Dungeon.level.secret[curr]) continue;
                Dungeon.level.discover(curr);
                if (!Dungeon.level.heroFOV[curr]) continue;
                GameScene.discoverTile(curr, Dungeon.level.map[curr]);
                ScrollOfMagicMapping.discover(curr);
                noticed = true;
            }
        }
        if (noticed) {
            Sample.INSTANCE.play("sounds/secret.mp3");
        }
        Sample.INSTANCE.play("sounds/teleport.mp3");
        GameScene.updateFog();
    }
}

