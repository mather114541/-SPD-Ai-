/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WaterOfAwareness
extends WellWater {
    @Override
    protected boolean affectHero(Hero hero) {
        Sample.INSTANCE.play("sounds/drink.mp3");
        this.emitter.parent.add(new Identification(hero.sprite.center()));
        hero.belongings.observe();
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            int terr = Dungeon.level.map[i];
            if ((Terrain.flags[terr] & 8) == 0) continue;
            Dungeon.level.discover(i);
            if (!Dungeon.level.heroFOV[i]) continue;
            GameScene.discoverTile(i, terr);
        }
        Buff.affect(hero, Awareness.class, 2.0f);
        Dungeon.observe();
        Dungeon.hero.interrupt();
        GLog.p(Messages.get(this, "procced", new Object[0]), new Object[0]);
        return true;
    }

    @Override
    protected Item affectItem(Item item, int pos) {
        if (item.isIdentified()) {
            return null;
        }
        ScrollOfIdentify.IDItem(item);
        Sample.INSTANCE.play("sounds/drink.mp3");
        this.emitter.parent.add(new Identification(DungeonTilemap.tileCenterToWorld(pos)));
        return item;
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.WELL_OF_AWARENESS;
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(3), 0.3f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

