/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ScrollOfMirrorImage
extends Scroll {
    private static final int NIMAGES = 2;

    public ScrollOfMirrorImage() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_MIRRORIMG;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfMirrorImage.curUser.belongings.backpack);
        if (ScrollOfMirrorImage.spawnImages(curUser, 2) > 0) {
            GLog.i(Messages.get(this, "copies", new Object[0]), new Object[0]);
        } else {
            GLog.i(Messages.get(this, "no_copies", new Object[0]), new Object[0]);
        }
        this.identify();
        Sample.INSTANCE.play("sounds/read.mp3");
        this.readAnimation();
    }

    public static int spawnImages(Hero hero, int nImages) {
        return ScrollOfMirrorImage.spawnImages(hero, hero.pos, nImages);
    }

    public static int spawnImages(Hero hero, int pos, int nImages) {
        ArrayList<Integer> respawnPoints = new ArrayList<Integer>();
        for (int i = 0; i < PathFinder.NEIGHBOURS9.length; ++i) {
            int p = pos + PathFinder.NEIGHBOURS9[i];
            if (Actor.findChar(p) != null || !Dungeon.level.passable[p]) continue;
            respawnPoints.add(p);
        }
        int spawned = 0;
        while (nImages > 0 && respawnPoints.size() > 0) {
            int index = Random.index(respawnPoints);
            MirrorImage mob = new MirrorImage();
            mob.duplicate(hero);
            GameScene.add(mob);
            ScrollOfTeleportation.appear(mob, (Integer)respawnPoints.get(index));
            respawnPoints.remove(index);
            --nImages;
            ++spawned;
        }
        return spawned;
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

