/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;
import java.util.Iterator;

public class DisplacingDart
extends TippedDart {
    public DisplacingDart() {
        this.image = ItemSpriteSheet.DISPLACING_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (this.processingChargedShot && attacker.alignment == defender.alignment) {
            return super.proc(attacker, defender, damage);
        }
        if (!defender.properties().contains((Object)Char.Property.IMMOVABLE)) {
            ArrayList<Integer> visiblePositions = new ArrayList<Integer>();
            ArrayList<Integer> nonVisiblePositions = new ArrayList<Integer>();
            PathFinder.buildDistanceMap(attacker.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            for (int pos = 0; pos < Dungeon.level.length(); ++pos) {
                if (!Dungeon.level.passable[pos] || PathFinder.distance[pos] < 8 || PathFinder.distance[pos] > 10 || Char.hasProp(defender, Char.Property.LARGE) && !Dungeon.level.openSpace[pos] || Actor.findChar(pos) != null) continue;
                if (Dungeon.level.heroFOV[pos]) {
                    visiblePositions.add(pos);
                    continue;
                }
                nonVisiblePositions.add(pos);
            }
            int chosenPos = -1;
            if (!visiblePositions.isEmpty()) {
                Iterator iterator = visiblePositions.iterator();
                while (iterator.hasNext()) {
                    int pos = (Integer)iterator.next();
                    if (chosenPos != -1 && !(Dungeon.level.trueDistance(defender.pos, chosenPos) > Dungeon.level.trueDistance(defender.pos, pos))) continue;
                    chosenPos = pos;
                }
            } else {
                Iterator iterator = nonVisiblePositions.iterator();
                while (iterator.hasNext()) {
                    int pos = (Integer)iterator.next();
                    if (chosenPos != -1 && !(Dungeon.level.trueDistance(defender.pos, chosenPos) > Dungeon.level.trueDistance(defender.pos, pos))) continue;
                    chosenPos = pos;
                }
            }
            if (chosenPos != -1) {
                ScrollOfTeleportation.appear(defender, chosenPos);
                Dungeon.level.occupyCell(defender);
                if (defender == Dungeon.hero) {
                    Dungeon.observe();
                    GameScene.updateFog();
                } else if (!Dungeon.level.heroFOV[chosenPos]) {
                    Buff.append((Char)attacker, TalismanOfForesight.CharAwareness.class, (float)5.0f).charID = defender.id();
                }
            }
        }
        return super.proc(attacker, defender, damage);
    }
}

