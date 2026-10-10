/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class BeaconOfReturning
extends Spell {
    public int returnDepth;
    public int returnBranch;
    public int returnPos;
    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);
    private static final String DEPTH = "depth";
    private static final String BRANCH = "branch";
    private static final String POS = "pos";

    public BeaconOfReturning() {
        this.image = ItemSpriteSheet.RETURN_BEACON;
        this.talentChance = 0.2f;
        this.returnDepth = -1;
        this.returnBranch = 0;
    }

    @Override
    protected void onCast(final Hero hero) {
        BeaconTracker tracker = hero.buff(BeaconTracker.class);
        if (tracker == null && this.returnDepth == -1) {
            this.setBeacon(hero);
        } else {
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.name()), Messages.get(BeaconOfReturning.class, "wnd_body", new Object[0]), new String[]{Messages.get(BeaconOfReturning.class, "wnd_set", new Object[0]), Messages.get(BeaconOfReturning.class, "wnd_return", new Object[0])}){

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        BeaconOfReturning.this.setBeacon(hero);
                    } else if (index == 1) {
                        BeaconOfReturning.this.returnBeacon(hero);
                    }
                }
            });
        }
    }

    @Override
    protected void onThrow(int cell) {
        if (this.returnDepth != -1) {
            if (Dungeon.hero.belongings.getItem(this.getClass()) == null) {
                Notes.remove(Notes.Landmark.BEACON_LOCATION, this.returnDepth);
            }
            this.returnDepth = -1;
        }
        super.onThrow(cell);
    }

    @Override
    public void doDrop(Hero hero) {
        if (this.returnDepth != -1) {
            Notes.remove(Notes.Landmark.BEACON_LOCATION, this.returnDepth);
            this.returnDepth = -1;
        }
        super.doDrop(hero);
    }

    private void setBeacon(Hero hero) {
        if (this.returnDepth != -1) {
            Notes.remove(Notes.Landmark.BEACON_LOCATION, this.returnDepth);
        }
        if (hero.buff(BeaconTracker.class) != null) {
            Notes.remove(Notes.Landmark.BEACON_LOCATION, hero.buff(BeaconTracker.class).returnDepth);
        }
        BeaconTracker tracker = Buff.affect(hero, BeaconTracker.class);
        tracker.returnDepth = Dungeon.depth;
        tracker.returnBranch = Dungeon.branch;
        tracker.returnPos = hero.pos;
        Notes.add(Notes.Landmark.BEACON_LOCATION, tracker.returnDepth);
        hero.spend(1.0f);
        hero.busy();
        GLog.i(Messages.get(this, "set", new Object[0]), new Object[0]);
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play("sounds/beacon.mp3");
        BeaconOfReturning.updateQuickslot();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void returnBeacon(Hero hero) {
        BeaconTracker tracker = hero.buff(BeaconTracker.class);
        if (tracker == null) {
            if (this.returnDepth == -1) {
                return;
            }
            tracker = new BeaconTracker();
            tracker.returnDepth = this.returnDepth;
            tracker.returnBranch = this.returnBranch;
            tracker.returnPos = this.returnPos;
        }
        if (tracker.returnDepth == Dungeon.depth && tracker.returnBranch == Dungeon.branch) {
            Char existing = Actor.findChar(tracker.returnPos);
            if (existing != null && existing != hero) {
                Char char_ = Char.hasProp(existing, Char.Property.IMMOVABLE) ? hero : existing;
                ArrayList<Integer> candidates = new ArrayList<Integer>();
                for (int n : PathFinder.NEIGHBOURS8) {
                    int cell = tracker.returnPos + n;
                    if (Dungeon.level.solid[cell] || Actor.findChar(cell) != null || Char.hasProp(char_, Char.Property.LARGE) && !Dungeon.level.openSpace[cell]) continue;
                    candidates.add(cell);
                }
                Random.shuffle(candidates);
                if (!candidates.isEmpty()) {
                    if (char_ == hero) {
                        tracker.returnPos = (Integer)candidates.get(0);
                    } else {
                        Actor.add(new Pushing(char_, char_.pos, (Integer)candidates.get(0)));
                        char_.pos = (Integer)candidates.get(0);
                        Dungeon.level.occupyCell(char_);
                    }
                } else {
                    GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele", new Object[0]), new Object[0]);
                    return;
                }
            }
            if (!ScrollOfTeleportation.teleportToLocation(hero, tracker.returnPos)) return;
            hero.spendAndNext(1.0f);
        } else {
            if (!Dungeon.interfloorTeleportAllowed()) {
                GLog.w(Messages.get(this, "preventing", new Object[0]), new Object[0]);
                return;
            }
            if (tracker.returnDepth >= 11 && tracker.returnDepth <= 14 && tracker.returnBranch == 1) {
                GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele", new Object[0]), new Object[0]);
                return;
            }
            Level.beforeTransition();
            Invisibility.dispel();
            InterlevelScene.mode = InterlevelScene.Mode.RETURN;
            InterlevelScene.returnDepth = tracker.returnDepth;
            InterlevelScene.returnBranch = tracker.returnBranch;
            InterlevelScene.returnPos = tracker.returnPos;
            Game.switchScene(InterlevelScene.class);
        }
        if (this.quantity == 1) {
            Notes.remove(Notes.Landmark.BEACON_LOCATION, tracker.returnDepth);
            if (tracker.target != null) {
                tracker.detach();
            }
        }
        this.detach(hero.belongings.backpack);
        Catalog.countUse(this.getClass());
        if (!(Random.Float() < this.talentChance)) return;
        Talent.onScrollUsed(curUser, BeaconOfReturning.curUser.pos, this.talentFactor, this.getClass());
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (Dungeon.hero != null) {
            BeaconTracker tracker = Dungeon.hero.buff(BeaconTracker.class);
            if (tracker != null) {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_set", tracker.returnDepth);
            } else if (this.returnDepth != -1) {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_set", this.returnDepth);
            }
        }
        return desc;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return this.returnDepth != -1 ? WHITE : null;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, this.returnDepth);
        bundle.put(BRANCH, this.returnBranch);
        if (this.returnDepth != -1) {
            bundle.put(POS, this.returnPos);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.returnDepth = bundle.getInt(DEPTH);
        this.returnBranch = bundle.getInt(BRANCH);
        this.returnPos = bundle.getInt(POS);
    }

    @Override
    public int value() {
        return (int)(60.0f * ((float)this.quantity / 5.0f));
    }

    @Override
    public int energyVal() {
        return (int)(12.0f * ((float)this.quantity / 5.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 5;

        public Recipe() {
            this.inputs = new Class[]{ScrollOfPassage.class};
            this.inQuantity = new int[]{1};
            this.cost = 12;
            this.output = BeaconOfReturning.class;
            this.outQuantity = 5;
        }
    }

    public static class BeaconTracker
    extends Buff {
        public int returnDepth;
        public int returnBranch;
        public int returnPos;
        private static final String DEPTH = "depth";
        private static final String BRANCH = "branch";
        private static final String POS = "pos";

        public BeaconTracker() {
            this.revivePersists = true;
            this.returnDepth = -1;
            this.returnBranch = 0;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("depth", this.returnDepth);
            bundle.put("branch", this.returnBranch);
            if (this.returnDepth != -1) {
                bundle.put("pos", this.returnPos);
            }
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.returnDepth = bundle.getInt("depth");
            this.returnBranch = bundle.getInt("branch");
            this.returnPos = bundle.getInt("pos");
        }
    }
}

