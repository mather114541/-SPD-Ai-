/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ArcaneBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FlashBangBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Noisemaker;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ShrapnelBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.WoollyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Bomb
extends Item {
    public Fuse fuse;
    private static boolean lightingFuse = false;
    private static final String AC_LIGHTTHROW = "LIGHTTHROW";
    private static final String FUSE = "fuse";

    public Bomb() {
        this.image = ItemSpriteSheet.BOMB;
        this.defaultAction = AC_LIGHTTHROW;
        this.usesTargeting = true;
        this.stackable = true;
    }

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && this.fuse == ((Bomb)item).fuse;
    }

    public boolean explodesDestructively() {
        return true;
    }

    protected int explosionRange() {
        return 1;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_LIGHTTHROW);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_LIGHTTHROW)) {
            lightingFuse = true;
            action = "THROW";
        } else {
            lightingFuse = false;
        }
        super.execute(hero, action);
    }

    protected Fuse createFuse() {
        return new Fuse();
    }

    @Override
    protected void onThrow(int cell) {
        if (!Dungeon.level.pit[cell] && lightingFuse) {
            this.fuse = this.createFuse().ignite(this);
            Actor.addDelayed(this.fuse, 2.0f);
        }
        super.onThrow(cell);
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (this.fuse != null) {
            GLog.w(Messages.get(this, "snuff_fuse", new Object[0]), new Object[0]);
            this.fuse.snuff();
            this.fuse = null;
        }
        return super.doPickUp(hero, pos);
    }

    public void explode(int cell) {
        if (this.fuse != null) {
            this.fuse.snuff();
            this.fuse = null;
        }
        Sample.INSTANCE.play("sounds/blast.mp3");
        if (this.explodesDestructively()) {
            ArrayList<Integer> affectedCells = new ArrayList<Integer>();
            ArrayList<Char> affectedChars = new ArrayList<Char>();
            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
            }
            boolean terrainAffected = false;
            boolean[] explodable = new boolean[Dungeon.level.length()];
            BArray.not(Dungeon.level.solid, explodable);
            BArray.or(Dungeon.level.flamable, explodable, explodable);
            PathFinder.buildDistanceMap(cell, explodable, this.explosionRange());
            for (int i = 0; i < PathFinder.distance.length; ++i) {
                if (PathFinder.distance[i] == Integer.MAX_VALUE) continue;
                affectedCells.add(i);
                Char ch = Actor.findChar(i);
                if (ch == null) continue;
                affectedChars.add(ch);
            }
            Iterator iterator = affectedCells.iterator();
            while (iterator.hasNext()) {
                Heap heap;
                int i = (Integer)iterator.next();
                if (Dungeon.level.heroFOV[i]) {
                    CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
                }
                if (Dungeon.level.flamable[i]) {
                    Dungeon.level.destroy(i);
                    GameScene.updateMap(i);
                    terrainAffected = true;
                }
                if ((heap = (Heap)Dungeon.level.heaps.get(i)) == null) continue;
                heap.explode();
            }
            for (Char ch : affectedChars) {
                if (!ch.isAlive()) continue;
                int dmg = Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3 * Dungeon.scalingDepth());
                if ((dmg -= ch.drRoll()) > 0) {
                    ch.damage(dmg, this);
                }
                if (ch != Dungeon.hero || ch.isAlive()) continue;
                if (this instanceof ConjuredBomb) {
                    Badges.validateDeathFromFriendlyMagic();
                }
                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                Dungeon.fail(this);
            }
            if (terrainAffected) {
                Dungeon.observe();
            }
        }
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
    public Item random() {
        switch (Random.Int(4)) {
            case 0: {
                return new DoubleBomb();
            }
        }
        return this;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return this.fuse != null ? new ItemSprite.Glowing(0xFF0000, 0.6f) : null;
    }

    @Override
    public int value() {
        return 15 * this.quantity;
    }

    @Override
    public String desc() {
        int depth = Dungeon.hero == null ? 1 : Dungeon.scalingDepth();
        String desc = Messages.get(this, "desc", 4 + depth, 12 + 3 * depth);
        if (this.fuse == null) {
            return desc + "\n\n" + Messages.get(this, "desc_fuse", new Object[0]);
        }
        return desc + "\n\n" + Messages.get(this, "desc_burning", new Object[0]);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FUSE, this.fuse);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(FUSE)) {
            this.fuse = ((Fuse)bundle.get(FUSE)).ignite(this);
            Actor.add(this.fuse);
        }
    }

    public static class Fuse
    extends Actor {
        protected Bomb bomb;

        public Fuse() {
            this.actPriority = -9;
        }

        public Fuse ignite(Bomb bomb) {
            this.bomb = bomb;
            return this;
        }

        @Override
        protected boolean act() {
            if (this.bomb.fuse != this) {
                this.snuff();
                return true;
            }
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (!heap.items.contains(this.bomb)) continue;
                this.trigger(heap);
                return true;
            }
            this.bomb.fuse = null;
            this.snuff();
            return true;
        }

        protected void trigger(Heap heap) {
            heap.remove(this.bomb);
            Catalog.countUse(this.bomb.getClass());
            this.bomb.explode(heap.pos);
            this.snuff();
        }

        public boolean freeze() {
            this.bomb.fuse = null;
            this.snuff();
            return true;
        }

        public void snuff() {
            Actor.remove(this);
        }
    }

    public static class ConjuredBomb
    extends Bomb {
    }

    public static class DoubleBomb
    extends Bomb {
        public DoubleBomb() {
            this.image = ItemSpriteSheet.DBL_BOMB;
            this.stackable = false;
        }

        @Override
        public boolean doPickUp(Hero hero, int pos) {
            Bomb bomb = new Bomb();
            bomb.quantity(2);
            if (bomb.doPickUp(hero, pos)) {
                if (SPDSettings.language() == Languages.ENGLISH) {
                    hero.sprite.showStatus(0xFFFF00, "1+1 free!", new Object[0]);
                }
                return true;
            }
            return false;
        }
    }

    public static class EnhanceBomb
    extends Recipe {
        public static final LinkedHashMap<Class<? extends Item>, Class<? extends Bomb>> validIngredients = new LinkedHashMap();
        private static final HashMap<Class<? extends Bomb>, Integer> bombCosts;

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean bomb = false;
            boolean ingredient = false;
            for (Item i : ingredients) {
                if (!i.isIdentified()) {
                    return false;
                }
                if (i.getClass().equals(Bomb.class)) {
                    bomb = true;
                    continue;
                }
                if (!validIngredients.containsKey(i.getClass())) continue;
                ingredient = true;
            }
            return bomb && ingredient;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                if (!validIngredients.containsKey(i.getClass())) continue;
                return bombCosts.get(validIngredients.get(i.getClass()));
            }
            return 0;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = null;
            for (Item i : ingredients) {
                i.quantity(i.quantity() - 1);
                if (!validIngredients.containsKey(i.getClass())) continue;
                result = Reflection.newInstance(validIngredients.get(i.getClass()));
            }
            if (result instanceof ArcaneBomb) {
                Catalog.countUse(GooBlob.class);
            } else if (result instanceof ShrapnelBomb) {
                Catalog.countUse(MetalShard.class);
            }
            return result;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                if (!validIngredients.containsKey(i.getClass())) continue;
                return Reflection.newInstance(validIngredients.get(i.getClass()));
            }
            return null;
        }

        static {
            validIngredients.put(PotionOfFrost.class, FrostBomb.class);
            validIngredients.put(ScrollOfMirrorImage.class, WoollyBomb.class);
            validIngredients.put(PotionOfLiquidFlame.class, Firebomb.class);
            validIngredients.put(ScrollOfRage.class, Noisemaker.class);
            validIngredients.put(PotionOfInvisibility.class, SmokeBomb.class);
            validIngredients.put(ScrollOfRecharging.class, FlashBangBomb.class);
            validIngredients.put(PotionOfHealing.class, RegrowthBomb.class);
            validIngredients.put(ScrollOfRemoveCurse.class, HolyBomb.class);
            validIngredients.put(GooBlob.class, ArcaneBomb.class);
            validIngredients.put(MetalShard.class, ShrapnelBomb.class);
            bombCosts = new HashMap();
            bombCosts.put(FrostBomb.class, 0);
            bombCosts.put(WoollyBomb.class, 0);
            bombCosts.put(Firebomb.class, 1);
            bombCosts.put(Noisemaker.class, 1);
            bombCosts.put(SmokeBomb.class, 2);
            bombCosts.put(FlashBangBomb.class, 2);
            bombCosts.put(RegrowthBomb.class, 3);
            bombCosts.put(HolyBomb.class, 3);
            bombCosts.put(ArcaneBomb.class, 6);
            bombCosts.put(ShrapnelBomb.class, 6);
        }
    }
}

