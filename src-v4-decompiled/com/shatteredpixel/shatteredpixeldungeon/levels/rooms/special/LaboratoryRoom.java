/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.AlchemyPage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Collection;

public class LaboratoryRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        int pos;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        Room.Door entrance = this.entrance();
        Point pot = null;
        if (entrance.x == this.left) {
            pot = new Point(this.right - 1, Random.Int(2) == 0 ? this.top + 1 : this.bottom - 1);
        } else if (entrance.x == this.right) {
            pot = new Point(this.left + 1, Random.Int(2) == 0 ? this.top + 1 : this.bottom - 1);
        } else if (entrance.y == this.top) {
            pot = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.bottom - 1);
        } else if (entrance.y == this.bottom) {
            pot = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.top + 1);
        }
        Painter.set(level, pot, 28);
        int chapter = 1 + Dungeon.depth / 5;
        Blob.seed(pot.x + level.width() * pot.y, 1, Alchemy.class, level);
        while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
        }
        level.drop(new EnergyCrystal().quantity(5), pos);
        int n = Random.NormalIntRange(1, 2);
        for (int i = 0; i < n; ++i) {
            while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
            }
            level.drop(LaboratoryRoom.prize(level), pos);
        }
        Collection<String> allPages = Document.ALCHEMY_GUIDE.pageNames();
        ArrayList<String> missingPages = new ArrayList<String>();
        for (String page : allPages) {
            if (Document.ALCHEMY_GUIDE.isPageFound(page)) continue;
            missingPages.add(page);
        }
        int chapterTarget = missingPages.size() <= 5 ? 2 : 1;
        if (!missingPages.isEmpty() && chapter >= chapterTarget) {
            int pagesToDrop = Math.min(missingPages.size(), chapter - chapterTarget + 1);
            for (int i = 0; i < pagesToDrop; ++i) {
                AlchemyPage p = new AlchemyPage();
                p.page((String)missingPages.remove(0));
                while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
                }
                level.drop(p, pos);
            }
        }
        entrance.set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private static Item prize(Level level) {
        Item prize = level.findPrizeItem(TrinketCatalyst.class);
        if (prize == null && (prize = level.findPrizeItem(PotionOfStrength.class)) == null) {
            prize = Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.STONE));
        }
        return prize;
    }
}

