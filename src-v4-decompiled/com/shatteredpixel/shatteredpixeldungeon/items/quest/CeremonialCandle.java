/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class CeremonialCandle
extends Item {
    public static int ritualPos;
    public boolean aflame;
    public static String AFLAME;

    public CeremonialCandle() {
        this.image = ItemSpriteSheet.CANDLE;
        this.defaultAction = "THROW";
        this.unique = true;
        this.stackable = true;
        this.aflame = false;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public void doDrop(Hero hero) {
        super.doDrop(hero);
        this.aflame = false;
        CeremonialCandle.checkCandles();
    }

    @Override
    protected void onThrow(int cell) {
        super.onThrow(cell);
        this.aflame = false;
        CeremonialCandle.checkCandles();
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (super.doPickUp(hero, pos)) {
            this.aflame = false;
            return true;
        }
        return false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(AFLAME, this.aflame);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.aflame = bundle.getBoolean(AFLAME);
    }

    @Override
    public Emitter emitter() {
        if (this.aflame) {
            Emitter emitter = new Emitter();
            emitter.pos(6.0f, 0.0f);
            emitter.fillTarget = false;
            emitter.pour(ElmoParticle.FACTORY, 0.25f);
            return emitter;
        }
        return super.emitter();
    }

    private static void checkCandles() {
        if (!(Dungeon.level instanceof RegularLevel)) {
            return;
        }
        if (!(((RegularLevel)Dungeon.level).room(ritualPos) instanceof RitualSiteRoom)) {
            return;
        }
        Heap[] candleHeaps = new Heap[]{(Heap)Dungeon.level.heaps.get(ritualPos - Dungeon.level.width()), (Heap)Dungeon.level.heaps.get(ritualPos + 1), (Heap)Dungeon.level.heaps.get(ritualPos + Dungeon.level.width()), (Heap)Dungeon.level.heaps.get(ritualPos - 1)};
        boolean allCandles = true;
        for (Heap h : candleHeaps) {
            if (h != null && h.type == Heap.Type.HEAP) {
                boolean foundCandle = false;
                for (Item i : h.items) {
                    if (!(i instanceof CeremonialCandle)) continue;
                    if (!((CeremonialCandle)i).aflame) {
                        ((CeremonialCandle)i).aflame = true;
                        h.sprite.view(h).place(h.pos);
                    }
                    foundCandle = true;
                }
                if (foundCandle) continue;
                allCandles = false;
                continue;
            }
            allCandles = false;
        }
        if (allCandles) {
            for (Heap h : candleHeaps) {
                for (Item i : h.items.toArray(new Item[0])) {
                    if (!(i instanceof CeremonialCandle)) continue;
                    h.remove(i);
                }
            }
            Elemental.NewbornFireElemental elemental = new Elemental.NewbornFireElemental();
            Char ch = Actor.findChar(ritualPos);
            if (ch != null) {
                ArrayList<Integer> candidates = new ArrayList<Integer>();
                for (int n : PathFinder.NEIGHBOURS8) {
                    int cell = ritualPos + n;
                    if (!Dungeon.level.passable[cell] && !Dungeon.level.avoid[cell] || Actor.findChar(cell) != null) continue;
                    candidates.add(cell);
                }
                elemental.pos = candidates.size() > 0 ? (Integer)Random.element(candidates) : ritualPos;
            } else {
                elemental.pos = ritualPos;
            }
            elemental.state = elemental.HUNTING;
            GameScene.add(elemental, 1.0f);
            if (Dungeon.level instanceof PrisonLevel) {
                ((PrisonLevel)Dungeon.level).updateWandmakerQuestMusic();
            }
            for (int i : PathFinder.NEIGHBOURS9) {
                CellEmitter.get(ritualPos + i).burst(ElmoParticle.FACTORY, 10);
            }
            Sample.INSTANCE.play("sounds/burning.mp3");
        }
    }

    static {
        AFLAME = "aflame";
    }
}

