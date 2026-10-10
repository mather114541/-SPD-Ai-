/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public abstract class Plant
implements Bundlable {
    public int image;
    public int pos;
    protected Class<? extends Seed> seedClass;
    private static final String POS = "pos";

    public void trigger() {
        if (rl.RLMode.headless) return;   // headless 短路植物触发效果，避免踩植物崩溃（植物效果对换层/探索非必需）
        Char ch = Actor.findChar(this.pos);
        if (ch instanceof Hero) {
            ((Hero)ch).interrupt();
            if (((Hero)ch).hasTalent(Talent.BARKSKIN)) {
                Barkskin.conditionallyAppend(ch, ((Hero)ch).lvl * ((Hero)ch).pointsInTalent(Talent.BARKSKIN) / 3, 1);
            }
        }
        if (Dungeon.level.heroFOV[this.pos] && Dungeon.hero.hasTalent(Talent.NATURES_AID)) {
            Barkskin.conditionallyAppend(Dungeon.hero, 2, 1 + 2 * Dungeon.hero.pointsInTalent(Talent.NATURES_AID));
        }
        this.wither();
        this.activate(ch);
        Bestiary.setSeen(this.getClass());
        Bestiary.countEncounter(this.getClass());
    }

    public abstract void activate(Char var1);

    public void wither() {
        Dungeon.level.uproot(this.pos);
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.get(this.pos).burst(LeafParticle.GENERAL, 6);
        }
        float seedChance = 0.0f;
        for (Char c : Actor.chars()) {
            WandOfRegrowth.Lotus l;
            if (!(c instanceof WandOfRegrowth.Lotus) || !(l = (WandOfRegrowth.Lotus)c).inRange(this.pos)) continue;
            seedChance = Math.max(seedChance, l.seedPreservation());
        }
        if (Random.Float() < seedChance && this.seedClass != null && this.seedClass != Rotberry.Seed.class) {
            Dungeon.level.drop((Item)((Item)Reflection.newInstance(this.seedClass)), (int)this.pos).sprite.drop();
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.pos = bundle.getInt(POS);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, this.pos);
    }

    public String name() {
        return Messages.get(this, "name", new Object[0]);
    }

    public String desc() {
        String desc = Messages.get(this, "desc", new Object[0]);
        if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN) {
            desc = desc + "\n\n" + Messages.get(this, "warden_desc", new Object[0]);
        }
        return desc;
    }

    public static class Seed
    extends Item {
        public static final String AC_PLANT = "PLANT";
        private static final float TIME_TO_PLANT = 1.0f;
        protected Class<? extends Plant> plantClass;

        public Seed() {
            this.stackable = true;
            this.defaultAction = "THROW";
        }

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> actions = super.actions(hero);
            actions.add(AC_PLANT);
            return actions;
        }

        @Override
        protected void onThrow(int cell) {
            if (Dungeon.level.map[cell] == 28 || Dungeon.level.pit[cell] || Dungeon.level.traps.get(cell) != null || Dungeon.isChallenged(8)) {
                super.onThrow(cell);
            } else {
                Catalog.countUse(this.getClass());
                Dungeon.level.plant(this, cell);
                if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
                    for (int i : PathFinder.NEIGHBOURS8) {
                        int c = Dungeon.level.map[cell + i];
                        if (c != 1 && c != 20 && c != 9 && c != 2) continue;
                        Level.set(cell + i, 30);
                        GameScene.updateMap(cell + i);
                        CellEmitter.get(cell + i).burst(LeafParticle.LEVEL_SPECIFIC, 4);
                    }
                }
            }
        }

        @Override
        public void execute(Hero hero, String action) {
            super.execute(hero, action);
            if (action.equals(AC_PLANT)) {
                hero.busy();
                ((Seed)this.detach(hero.belongings.backpack)).onThrow(hero.pos);
                hero.spend(1.0f);
                hero.sprite.operate(hero.pos);
            }
        }

        public Plant couch(int pos, Level level) {
            if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
                Sample.INSTANCE.play("sounds/plant.mp3");
            }
            Plant plant = Reflection.newInstance(this.plantClass);
            plant.pos = pos;
            return plant;
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
        public int value() {
            return 10 * this.quantity;
        }

        @Override
        public int energyVal() {
            return 2 * this.quantity;
        }

        @Override
        public String desc() {
            String desc = Messages.get(this.plantClass, "desc", new Object[0]);
            if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN) {
                desc = desc + "\n\n" + Messages.get(this.plantClass, "warden_desc", new Object[0]);
            }
            return desc;
        }

        @Override
        public String info() {
            return Messages.get(Seed.class, "info", super.info());
        }

        public static class PlaceHolder
        extends Seed {
            public PlaceHolder() {
                this.image = ItemSpriteSheet.SEED_HOLDER;
            }

            @Override
            public boolean isSimilar(Item item) {
                return item instanceof Seed;
            }

            @Override
            public String info() {
                return "";
            }
        }
    }
}

