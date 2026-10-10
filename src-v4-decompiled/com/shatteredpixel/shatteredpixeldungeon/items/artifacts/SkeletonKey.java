/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SpectralWallParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class SkeletonKey
extends Artifact {
    public static final String AC_INSERT = "INSERT";
    public CellSelector.Listener targeter;

    public SkeletonKey() {
        this.image = ItemSpriteSheet.ARTIFACT_KEY;
        this.levelCap = 10;
        this.charge = 3 + this.level() / 2;
        this.partialCharge = 0.0f;
        this.chargeCap = 3 + this.level() / 2;
        this.defaultAction = AC_INSERT;
        this.targeter = new CellSelector.Listener(){

            @Override
            public void onSelect(final Integer target) {
                if (target != null && (Dungeon.level.visited[target] || Dungeon.level.mapped[target])) {
                    if (target == curUser.pos) {
                        GLog.w(Messages.get(SkeletonKey.class, "invalid_target", new Object[0]), new Object[0]);
                        return;
                    }
                    if (Dungeon.level.adjacent(target, curUser.pos)) {
                        if (Dungeon.level.map[target] == 21) {
                            GLog.w(Messages.get(SkeletonKey.class, "wont_open", new Object[0]), new Object[0]);
                            return;
                        }
                        if (Dungeon.level.map[target] == 10) {
                            if (Dungeon.level.locked || Dungeon.branch != 0) {
                                GLog.w(Messages.get(SkeletonKey.class, "wont_open", new Object[0]), new Object[0]);
                                return;
                            }
                            if (SkeletonKey.this.charge < 1) {
                                GLog.i(Messages.get(SkeletonKey.class, "iron_charges", new Object[0]), new Object[0]);
                                return;
                            }
                            Sample.INSTANCE.play("sounds/unlock.mp3");
                            curUser.sprite.operate(target, new Callback(){

                                @Override
                                public void call() {
                                    Buff.affect(curUser, KeyReplacementTracker.class).processIronLockOpened();
                                    Level.set(target, 5);
                                    GameScene.updateMap(target);
                                    --SkeletonKey.this.charge;
                                    SkeletonKey.this.gainExp(3);
                                    Talent.onArtifactUsed(Dungeon.hero);
                                    curUser.spendAndNext(1.0f);
                                    curUser.sprite.idle();
                                }
                            });
                            curUser.busy();
                            return;
                        }
                        if (Dungeon.level.map[target] == 38) {
                            Sample.INSTANCE.play("sounds/unlock.mp3");
                            curUser.sprite.operate(target, new Callback(){

                                @Override
                                public void call() {
                                    Level.set(target, 5);
                                    GameScene.updateMap(target);
                                    curUser.spendAndNext(1.0f);
                                    curUser.sprite.idle();
                                }
                            });
                            curUser.busy();
                            return;
                        }
                        if (Dungeon.level.map[target] == 31) {
                            if (SkeletonKey.this.charge < 5) {
                                GLog.i(Messages.get(SkeletonKey.class, "crystal_charges", new Object[0]), new Object[0]);
                                return;
                            }
                            Sample.INSTANCE.play("sounds/unlock.mp3");
                            curUser.sprite.operate(target, new Callback(){

                                @Override
                                public void call() {
                                    Buff.affect(curUser, KeyReplacementTracker.class).processCrystalLockOpened();
                                    Level.set(target, 1);
                                    GameScene.updateMap(target);
                                    SkeletonKey.this.charge -= 5;
                                    SkeletonKey.this.gainExp(7);
                                    Talent.onArtifactUsed(Dungeon.hero);
                                    Sample.INSTANCE.play("sounds/teleport.mp3");
                                    CellEmitter.get(target).start(Speck.factory(101), 0.025f, 20);
                                    curUser.spendAndNext(1.0f);
                                    curUser.sprite.idle();
                                    Notes.remove(Notes.Landmark.DISTANT_WELL, Dungeon.depth - 1);
                                }
                            });
                            curUser.busy();
                            return;
                        }
                        if (Dungeon.level.map[target] == 5 || Dungeon.level.map[target] == 6) {
                            if (SkeletonKey.this.charge < 2) {
                                GLog.i(Messages.get(SkeletonKey.class, "lock_charges", new Object[0]), new Object[0]);
                                return;
                            }
                            if (Actor.findChar(target) != null) {
                                Char toMove = Actor.findChar(target);
                                int pushCell = -1;
                                int oppositeCell = target + (target - curUser.pos);
                                if (!(Dungeon.level.solid[oppositeCell] || Actor.findChar(oppositeCell) != null || !Dungeon.level.openSpace[oppositeCell] && Char.hasProp(toMove, Char.Property.LARGE))) {
                                    pushCell = oppositeCell;
                                } else {
                                    for (int i : PathFinder.NEIGHBOURS8) {
                                        if (Dungeon.level.solid[target + i] || Actor.findChar(target + i) != null || !Dungeon.level.openSpace[target + i] && Char.hasProp(toMove, Char.Property.LARGE) || pushCell != -1 && !(Dungeon.level.trueDistance(curUser.pos, pushCell) < Dungeon.level.trueDistance(curUser.pos, target + i))) continue;
                                        pushCell = target + i;
                                    }
                                }
                                if (pushCell != -1 && !Char.hasProp(toMove, Char.Property.IMMOVABLE)) {
                                    Ballistica push = new Ballistica(target, pushCell, 7);
                                    WandOfBlastWave.throwChar(toMove, push, 1, false, false, this);
                                    Artifact.artifactProc(toMove, SkeletonKey.this.visiblyUpgraded(), 2);
                                } else {
                                    GLog.w(Messages.get(SkeletonKey.class, "lock_no_space", new Object[0]), new Object[0]);
                                    return;
                                }
                            }
                            Sample.INSTANCE.play("sounds/unlock.mp3");
                            curUser.sprite.operate(target, new Callback(){

                                @Override
                                public void call() {
                                    Level.set(target, 38);
                                    GameScene.updateMap(target);
                                    SkeletonKey.this.charge -= 2;
                                    SkeletonKey.this.gainExp(2);
                                    Talent.onArtifactUsed(Dungeon.hero);
                                    curUser.spendAndNext(1.0f);
                                    curUser.sprite.idle();
                                    if (Dungeon.level.heaps.get(target) != null) {
                                        ArrayList<Integer> candidates = new ArrayList<Integer>();
                                        for (int n : PathFinder.NEIGHBOURS8) {
                                            if (!Dungeon.level.passable[target + n]) continue;
                                            candidates.add(target + n);
                                        }
                                        if (!candidates.isEmpty()) {
                                            Heap heap = (Heap)Dungeon.level.heaps.get(target);
                                            while (!heap.isEmpty()) {
                                                Dungeon.level.drop((Item)heap.pickUp(), (int)((Integer)Random.element(candidates)).intValue()).sprite.drop(target);
                                            }
                                        }
                                    }
                                }
                            });
                            curUser.busy();
                            return;
                        }
                        if (Dungeon.level.heaps.get(target) != null && ((Heap)Dungeon.level.heaps.get((int)target.intValue())).type == Heap.Type.LOCKED_CHEST) {
                            if (SkeletonKey.this.charge < 2) {
                                GLog.i(Messages.get(SkeletonKey.class, "gold_charges", new Object[0]), new Object[0]);
                                return;
                            }
                            Sample.INSTANCE.play("sounds/unlock.mp3");
                            curUser.sprite.operate(target, new Callback(){

                                @Override
                                public void call() {
                                    Buff.affect(curUser, KeyReplacementTracker.class).processGoldLockOpened();
                                    ((Heap)Dungeon.level.heaps.get(target)).open(curUser);
                                    SkeletonKey.this.charge -= 2;
                                    SkeletonKey.this.gainExp(4);
                                    Talent.onArtifactUsed(Dungeon.hero);
                                    curUser.spendAndNext(1.0f);
                                    curUser.sprite.idle();
                                }
                            });
                            curUser.busy();
                            return;
                        }
                        if (Dungeon.level.heaps.get(target) != null && ((Heap)Dungeon.level.heaps.get((int)target.intValue())).type == Heap.Type.CRYSTAL_CHEST) {
                            if (SkeletonKey.this.charge < 5) {
                                GLog.i(Messages.get(SkeletonKey.class, "crystal_charges", new Object[0]), new Object[0]);
                                return;
                            }
                            Sample.INSTANCE.play("sounds/unlock.mp3");
                            curUser.sprite.operate(target, new Callback(){

                                @Override
                                public void call() {
                                    Buff.affect(curUser, KeyReplacementTracker.class).processCrystalLockOpened();
                                    ((Heap)Dungeon.level.heaps.get(target)).open(curUser);
                                    SkeletonKey.this.charge -= 5;
                                    SkeletonKey.this.gainExp(7);
                                    Talent.onArtifactUsed(Dungeon.hero);
                                    curUser.spendAndNext(1.0f);
                                    curUser.sprite.idle();
                                }
                            });
                            curUser.busy();
                            return;
                        }
                    }
                    if (SkeletonKey.this.charge < 2) {
                        GLog.i(Messages.get(SkeletonKey.class, "wall_charges", new Object[0]), new Object[0]);
                        return;
                    }
                    int closest = curUser.pos;
                    int closestIdx = -1;
                    for (int i = 0; i < PathFinder.CIRCLE8.length; ++i) {
                        int ofs = PathFinder.CIRCLE8[i];
                        if (!(Dungeon.level.trueDistance(target, curUser.pos + ofs) < Dungeon.level.trueDistance(target, closest))) continue;
                        closest = curUser.pos + ofs;
                        closestIdx = i;
                    }
                    final int knockBackDir = PathFinder.CIRCLE8[closestIdx];
                    if (Dungeon.level.solid[closest]) {
                        GLog.w(Messages.get(SkeletonKey.class, "invalid_target", new Object[0]), new Object[0]);
                        return;
                    }
                    final int finalClosestIdx = closestIdx;
                    Sample.INSTANCE.play("sounds/unlock.mp3");
                    curUser.sprite.operate(target, new Callback(){

                        @Override
                        public void call() {
                            SkeletonKey.this.placeWall(curUser.pos + PathFinder.CIRCLE8[finalClosestIdx], knockBackDir);
                            SkeletonKey.this.placeWall(curUser.pos + PathFinder.CIRCLE8[(finalClosestIdx + 7) % 8], knockBackDir);
                            SkeletonKey.this.placeWall(curUser.pos + PathFinder.CIRCLE8[(finalClosestIdx + 1) % 8], knockBackDir);
                            if (finalClosestIdx % 2 == 0) {
                                SkeletonKey.this.placeWall(curUser.pos + 2 * PathFinder.CIRCLE8[(finalClosestIdx + 7) % 8], knockBackDir);
                                SkeletonKey.this.placeWall(curUser.pos + 2 * PathFinder.CIRCLE8[(finalClosestIdx + 1) % 8], knockBackDir);
                            }
                            SkeletonKey.this.charge -= 2;
                            SkeletonKey.this.gainExp(2);
                            Dungeon.observe();
                            GameScene.updateFog();
                            Sample.INSTANCE.play("sounds/teleport.mp3");
                            Talent.onArtifactUsed(Dungeon.hero);
                            curUser.spendAndNext(1.0f);
                            curUser.sprite.idle();
                        }
                    });
                    curUser.busy();
                }
            }

            @Override
            public String prompt() {
                return Messages.get(SkeletonKey.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && hero.buff(MagicImmune.class) == null && !this.cursed) {
            actions.add(AC_INSERT);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_INSERT)) {
            curUser = hero;
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.cursed) {
                GLog.w(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else {
                GameScene.selectCell(this.targeter);
            }
        }
    }

    public void gainExp(int xpGain) {
        if (this.level() == this.levelCap) {
            return;
        }
        this.exp += xpGain;
        if (this.exp > 4 + this.level()) {
            this.exp -= 4 + this.level();
            this.upgrade();
            GLog.p(Messages.get(this, "levelup", new Object[0]), new Object[0]);
            Catalog.countUse(SkeletonKey.class);
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new keyRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.charge < this.chargeCap && !this.cursed && target.buff(MagicImmune.class) == null) {
            this.partialCharge += 0.133f * amount;
            while (this.partialCharge >= 1.0f) {
                this.partialCharge -= 1.0f;
                ++this.charge;
            }
            if (this.charge >= this.chargeCap) {
                this.partialCharge = 0.0f;
            }
            SkeletonKey.updateQuickslot();
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            desc = this.cursed ? (String)desc + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]) : (String)desc + "\n\n" + Messages.get(this, "desc_worn", new Object[0]);
        }
        return desc;
    }

    @Override
    public Item upgrade() {
        this.chargeCap = 3 + (this.level() + 1) / 2;
        return super.upgrade();
    }

    private void placeWall(int pos, int knockbackDIR) {
        Blob wall = Dungeon.level.blobs.get(KeyWall.class);
        if (!Dungeon.level.solid[pos] || wall != null && wall.cur[pos] > 0) {
            GameScene.add(Blob.seed(pos, 10, KeyWall.class));
            Char ch = Actor.findChar(pos);
            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                WandOfBlastWave.throwChar(ch, new Ballistica(pos, pos + knockbackDIR, 7), 1, false, false, this);
                SkeletonKey.artifactProc(ch, this.visiblyUpgraded(), 2);
            }
        }
    }

    public class keyRecharge
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            if (SkeletonKey.this.charge < SkeletonKey.this.chargeCap && !SkeletonKey.this.cursed && this.target.buff(MagicImmune.class) == null && Regeneration.regenOn()) {
                float chargeGain = 1.0f / (120.0f - (float)(SkeletonKey.this.chargeCap - SkeletonKey.this.charge) * 7.5f);
                SkeletonKey.this.partialCharge += (chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
                while (SkeletonKey.this.partialCharge >= 1.0f) {
                    SkeletonKey.this.partialCharge -= 1.0f;
                    ++SkeletonKey.this.charge;
                    if (SkeletonKey.this.charge != SkeletonKey.this.chargeCap) continue;
                    SkeletonKey.this.partialCharge = 0.0f;
                }
            }
            Item.updateQuickslot();
            this.spend(1.0f);
            return true;
        }
    }

    public static class KeyWall
    extends Blob {
        public KeyWall() {
            this.alwaysVisible = true;
        }

        @Override
        protected void evolve() {
            boolean cellEnded = false;
            Level l = Dungeon.level;
            for (int i = this.area.left; i < this.area.right; ++i) {
                for (int j = this.area.top; j < this.area.bottom; ++j) {
                    int cell = i + j * l.width();
                    int n = this.off[cell] = this.cur[cell] > 0 ? this.cur[cell] - 1 : 0;
                    if (this.cur[cell] > 0 && this.off[cell] == 0) {
                        cellEnded = true;
                    }
                    this.off[cell] = Math.min(this.off[cell], 9);
                    this.volume += this.off[cell];
                    if (this.off[cell] != 0 || this.cur[cell] <= 0) continue;
                    this.cellsToFlagUpdate.add(cell);
                }
            }
            if (cellEnded) {
                Dungeon.observe();
            }
        }

        @Override
        public void seed(Level level, int cell, int amount) {
            super.seed(level, cell, amount);
            level.updateCellFlags(cell);
        }

        @Override
        public void clear(int cell) {
            super.clear(cell);
            if (this.cur == null) {
                return;
            }
            Dungeon.level.updateCellFlags(cell);
        }

        @Override
        public void fullyClear() {
            super.fullyClear();
            Dungeon.level.buildFlagMaps();
        }

        @Override
        public void onBuildFlagMaps(Level l) {
            if (this.volume > 0) {
                for (int i = 0; i < l.length(); ++i) {
                    this.onUpdateCellFlags(l, i);
                }
            }
        }

        @Override
        public void onUpdateCellFlags(Level l, int cell) {
            if (this.volume > 0 && this.cur[cell] > 0) {
                l.losBlocking[cell] = true;
                l.solid[cell] = true;
                l.passable[cell] = false;
                l.avoid[cell] = false;
            }
        }

        @Override
        public void use(BlobEmitter emitter) {
            super.use(emitter);
            emitter.pour(SpectralWallParticle.FACTORY, 0.05f);
        }

        @Override
        public String tileDesc() {
            return Messages.get(this, "desc", new Object[0]);
        }
    }

    public static class KeyReplacementTracker
    extends Buff {
        public int[] ironKeysNeeded;
        public int[] goldenKeysNeeded;
        public int[] crystalKeysNeeded;
        public static String IRON_NEEDED = "iron_needed";
        public static String GOLDEN_NEEDED = "golden_needed";
        public static String CRYSTAL_NEEDED = "crystal_needed";

        public KeyReplacementTracker() {
            this.revivePersists = true;
            this.ironKeysNeeded = new int[26];
            Arrays.fill(this.ironKeysNeeded, -1);
            this.goldenKeysNeeded = new int[26];
            Arrays.fill(this.goldenKeysNeeded, -1);
            this.crystalKeysNeeded = new int[26];
            Arrays.fill(this.crystalKeysNeeded, -1);
        }

        public void setupKeysForDepth() {
            this.ironKeysNeeded[Dungeon.depth] = 0;
            this.goldenKeysNeeded[Dungeon.depth] = 0;
            this.crystalKeysNeeded[Dungeon.depth] = 0;
            for (Heap h : Dungeon.level.heaps.valueList()) {
                if (h.type == Heap.Type.LOCKED_CHEST) {
                    int n = Dungeon.depth;
                    this.goldenKeysNeeded[n] = this.goldenKeysNeeded[n] + 1;
                    continue;
                }
                if (h.type != Heap.Type.CRYSTAL_CHEST) continue;
                int n = Dungeon.depth;
                this.crystalKeysNeeded[n] = this.crystalKeysNeeded[n] + 1;
            }
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                if (Dungeon.level.map[i] == 10) {
                    int n = Dungeon.depth;
                    this.ironKeysNeeded[n] = this.ironKeysNeeded[n] + 1;
                    continue;
                }
                if (Dungeon.level.map[i] != 31) continue;
                int n = Dungeon.depth;
                this.crystalKeysNeeded[n] = this.crystalKeysNeeded[n] + 1;
            }
        }

        public void clearDepth() {
            this.ironKeysNeeded[Dungeon.depth] = -1;
            this.goldenKeysNeeded[Dungeon.depth] = -1;
            this.crystalKeysNeeded[Dungeon.depth] = -1;
        }

        public void processIronLockOpened() {
            if (this.ironKeysNeeded[Dungeon.depth] == -1) {
                this.setupKeysForDepth();
            }
            int n = Dungeon.depth;
            this.ironKeysNeeded[n] = this.ironKeysNeeded[n] - 1;
            this.processExcessKeys();
        }

        public void processGoldLockOpened() {
            if (this.goldenKeysNeeded[Dungeon.depth] == -1) {
                this.setupKeysForDepth();
            }
            int n = Dungeon.depth;
            this.goldenKeysNeeded[n] = this.goldenKeysNeeded[n] - 1;
            this.processExcessKeys();
        }

        public void processCrystalLockOpened() {
            if (this.crystalKeysNeeded[Dungeon.depth] == -1) {
                this.setupKeysForDepth();
            }
            int n = Dungeon.depth;
            this.crystalKeysNeeded[n] = this.crystalKeysNeeded[n] - 1;
            this.processExcessKeys();
        }

        public void processExcessKeys() {
            int keysNeeded = this.ironKeysNeeded[Dungeon.depth];
            boolean removed = false;
            if (keysNeeded >= 0) {
                while (Notes.keyCount(new IronKey(Dungeon.depth)) > keysNeeded) {
                    Notes.remove(new IronKey(Dungeon.depth));
                    removed = true;
                }
            }
            if ((keysNeeded = this.goldenKeysNeeded[Dungeon.depth]) >= 0) {
                while (Notes.keyCount(new GoldenKey(Dungeon.depth)) > keysNeeded) {
                    Notes.remove(new GoldenKey(Dungeon.depth));
                    removed = true;
                }
            }
            if ((keysNeeded = this.crystalKeysNeeded[Dungeon.depth]) >= 0) {
                while (Notes.keyCount(new CrystalKey(Dungeon.depth)) > keysNeeded) {
                    Notes.remove(new CrystalKey(Dungeon.depth));
                    removed = true;
                }
            }
            if (removed) {
                GameScene.updateKeyDisplay();
                GLog.i(Messages.get(SkeletonKey.class, "discard", new Object[0]), new Object[0]);
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(IRON_NEEDED, this.ironKeysNeeded);
            bundle.put(GOLDEN_NEEDED, this.goldenKeysNeeded);
            bundle.put(CRYSTAL_NEEDED, this.crystalKeysNeeded);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.ironKeysNeeded = bundle.getIntArray(IRON_NEEDED);
            this.goldenKeysNeeded = bundle.getIntArray(GOLDEN_NEEDED);
            this.crystalKeysNeeded = bundle.getIntArray(CRYSTAL_NEEDED);
        }
    }
}

