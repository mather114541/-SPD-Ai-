/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class DisarmingTrap
extends Trap {
    public DisarmingTrap() {
        this.color = 0;
        this.shape = 6;
    }

    @Override
    public void activate() {
        Heap heap = (Heap)Dungeon.level.heaps.get(this.pos);
        if (heap != null && heap.type == Heap.Type.HEAP) {
            int cell;
            while ((cell = Dungeon.level.randomRespawnCell(null)) != -1 && Dungeon.level.heaps.get(this.pos) != null && ((Heap)Dungeon.level.heaps.get((int)this.pos)).type != Heap.Type.HEAP) {
            }
            if (cell != -1) {
                Item item = heap.pickUp();
                Heap dropped = Dungeon.level.drop(item, cell);
                dropped.seen = true;
                if (item instanceof Honeypot.ShatteredPot) {
                    ((Honeypot.ShatteredPot)item).movePot(this.pos, cell);
                }
                for (int i : PathFinder.NEIGHBOURS9) {
                    Dungeon.level.visited[cell + i] = true;
                }
                GameScene.updateFog();
                Sample.INSTANCE.play("sounds/teleport.mp3");
                CellEmitter.get(this.pos).burst(Speck.factory(2), 4);
            }
        }
        if (Actor.findChar(this.pos) instanceof Statue) {
            Actor.findChar(this.pos).die(this);
            Sample.INSTANCE.play("sounds/teleport.mp3");
            CellEmitter.get(this.pos).burst(Speck.factory(2), 4);
        }
        if (Dungeon.hero.pos == this.pos && !Dungeon.hero.flying) {
            Hero hero = Dungeon.hero;
            KindOfWeapon weapon = hero.belongings.weapon;
            if (weapon != null && !weapon.cursed) {
                int cell;
                int tries = 50;
                do {
                    cell = Dungeon.level.randomRespawnCell(null);
                    if (tries-- < 0 && cell != -1) break;
                    PathFinder.buildDistanceMap(this.pos, Dungeon.level.passable);
                } while (cell == -1 || PathFinder.distance[cell] < 10 || PathFinder.distance[cell] > 20);
                if (tries < 0) {
                    return;
                }
                hero.belongings.weapon = null;
                Dungeon.quickslot.clearItem(weapon);
                weapon.updateQuickslot();
                Dungeon.level.drop((Item)weapon, (int)cell).seen = true;
                for (int i : PathFinder.NEIGHBOURS9) {
                    Dungeon.level.mapped[cell + i] = true;
                }
                GameScene.updateFog(cell, 1);
                GLog.w(Messages.get(this, "disarm", new Object[0]), new Object[0]);
                Sample.INSTANCE.play("sounds/teleport.mp3");
                CellEmitter.get(this.pos).burst(Speck.factory(2), 4);
            }
        }
    }
}

