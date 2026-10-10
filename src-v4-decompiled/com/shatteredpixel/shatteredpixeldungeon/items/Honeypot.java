/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Honeypot
extends Item {
    public static final String AC_SHATTER = "SHATTER";

    public Honeypot() {
        this.image = ItemSpriteSheet.HONEYPOT;
        this.defaultAction = "THROW";
        this.usesTargeting = true;
        this.stackable = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SHATTER);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_SHATTER)) {
            hero.sprite.zap(hero.pos);
            this.detach(hero.belongings.backpack);
            Catalog.countUse(this.getClass());
            Item item = this.shatter(hero, hero.pos);
            if (!item.collect()) {
                Dungeon.level.drop(item, hero.pos);
                if (item instanceof ShatteredPot) {
                    ((ShatteredPot)item).dropPot(hero, hero.pos);
                }
            }
            hero.next();
        }
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.pit[cell]) {
            super.onThrow(cell);
        } else {
            Catalog.countUse(this.getClass());
            Dungeon.level.drop(this.shatter(null, cell), cell);
        }
    }

    public Item shatter(Char owner, int pos) {
        if (Dungeon.level.heroFOV[pos]) {
            Sample.INSTANCE.play("sounds/shatter.mp3");
            Splash.at(pos, 16766208, 5);
        }
        int newPos = pos;
        if (Actor.findChar(pos) != null) {
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int n : PathFinder.NEIGHBOURS4) {
                int c = pos + n;
                if (Dungeon.level.solid[c] || Actor.findChar(c) != null) continue;
                candidates.add(c);
            }
            int n = newPos = candidates.size() > 0 ? (Integer)Random.element(candidates) : -1;
        }
        if (newPos != -1) {
            Bee bee = new Bee();
            bee.spawn(Dungeon.scalingDepth());
            bee.setPotInfo(pos, owner);
            bee.HP = bee.HT;
            bee.pos = newPos;
            GameScene.add(bee);
            if (newPos != pos) {
                Actor.add(new Pushing(bee, pos, newPos));
            }
            bee.sprite.alpha(0.0f);
            bee.sprite.parent.add(new AlphaTweener(bee.sprite, 1.0f, 0.15f));
            Sample.INSTANCE.play("sounds/bee.mp3");
            return new ShatteredPot();
        }
        return this;
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
        return 30 * this.quantity;
    }

    public static class ShatteredPot
    extends Item {
        public ShatteredPot() {
            this.image = ItemSpriteSheet.SHATTPOT;
            this.stackable = true;
        }

        @Override
        public boolean doPickUp(Hero hero, int pos) {
            if (super.doPickUp(hero, pos)) {
                this.pickupPot(hero);
                return true;
            }
            return false;
        }

        @Override
        public void doDrop(Hero hero) {
            super.doDrop(hero);
            this.dropPot(hero, hero.pos);
        }

        @Override
        protected void onThrow(int cell) {
            super.onThrow(cell);
            this.dropPot(curUser, cell);
        }

        public void pickupPot(Char holder) {
            for (Bee bee : this.findBees(holder.pos)) {
                this.updateBee(bee, -1, holder);
            }
        }

        public void dropPot(Char holder, int dropPos) {
            for (Bee bee : this.findBees(holder)) {
                this.updateBee(bee, dropPos, null);
            }
        }

        public void movePot(int oldpos, int movePos) {
            for (Bee bee : this.findBees(oldpos)) {
                this.updateBee(bee, movePos, null);
            }
        }

        public void destroyPot(int potPos) {
            for (Bee bee : this.findBees(potPos)) {
                this.updateBee(bee, -1, null);
            }
        }

        private void updateBee(Bee bee, int cell, Char holder) {
            if (bee != null && bee.alignment == Char.Alignment.ENEMY) {
                bee.setPotInfo(cell, holder);
            }
        }

        private ArrayList<Bee> findBees(int potPos) {
            ArrayList<Bee> bees = new ArrayList<Bee>();
            for (Char c : Actor.chars()) {
                if (!(c instanceof Bee) || ((Bee)c).potPos() != potPos) continue;
                bees.add((Bee)c);
                if (bees.size() < this.quantity) continue;
                break;
            }
            return bees;
        }

        private ArrayList<Bee> findBees(Char potHolder) {
            ArrayList<Bee> bees = new ArrayList<Bee>();
            for (Char c : Actor.chars()) {
                if (!(c instanceof Bee) || ((Bee)c).potHolderID() != potHolder.id()) continue;
                bees.add((Bee)c);
                if (bees.size() < this.quantity) continue;
                break;
            }
            return bees;
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
            return 5 * this.quantity;
        }
    }
}

