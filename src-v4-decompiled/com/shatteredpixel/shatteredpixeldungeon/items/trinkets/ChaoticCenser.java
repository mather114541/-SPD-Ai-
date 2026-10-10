/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.HashMap;
import java.util.Iterator;

public class ChaoticCenser
extends Trinket {
    private static final float[][] GAS_CAT_CHANCES = new float[4][3];
    private static final HashMap<Class<? extends Blob>, Float> COMMON_GASSES;
    private static final HashMap<Class<? extends Blob>, Float> UNCOMMON_GASSES;
    private static final HashMap<Class<? extends Blob>, Float> RARE_GASSES;
    private static final HashMap<Class<? extends Blob>, Integer> MISSILE_VFX;

    public ChaoticCenser() {
        this.image = ItemSpriteSheet.CHAOTIC_CENSER;
    }

    @Override
    protected int upgradeEnergyCost() {
        return 6 + 2 * this.level();
    }

    @Override
    public String statsDesc() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", ChaoticCenser.averageTurnsUntilGas(this.buffedLvl()));
        }
        return Messages.get(this, "stats_desc", ChaoticCenser.averageTurnsUntilGas(0));
    }

    public static int averageTurnsUntilGas() {
        return ChaoticCenser.averageTurnsUntilGas(ChaoticCenser.trinketLevel(ChaoticCenser.class));
    }

    public static int averageTurnsUntilGas(int level) {
        if (level <= -1) {
            return -1;
        }
        return 300 / (level + 1);
    }

    private static boolean produceGas(Char target) {
        Integer targetCell;
        int cell;
        float gasQuantity;
        Class<? extends Blob> gasToSpawn;
        int level = ChaoticCenser.trinketLevel(ChaoticCenser.class);
        if (level < 0 || level > 3) {
            return false;
        }
        switch (Random.chances(GAS_CAT_CHANCES[level])) {
            default: {
                do {
                    gasToSpawn = Random.element(COMMON_GASSES.keySet());
                } while (!Regeneration.regenOn() && gasToSpawn == Regrowth.class);
                gasQuantity = COMMON_GASSES.get(gasToSpawn).floatValue();
                break;
            }
            case 1: {
                gasToSpawn = Random.element(UNCOMMON_GASSES.keySet());
                gasQuantity = UNCOMMON_GASSES.get(gasToSpawn).floatValue();
                break;
            }
            case 2: {
                gasToSpawn = Random.element(RARE_GASSES.keySet());
                gasQuantity = RARE_GASSES.get(gasToSpawn).floatValue();
            }
        }
        HashMap<Integer, Float> candidateCells = new HashMap<Integer, Float>();
        PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.not(Dungeon.level.solid, null), 6);
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            if (!Dungeon.level.heroFOV[i] || PathFinder.distance[i] >= Integer.MAX_VALUE || PathFinder.distance[i] < 2 || PathFinder.distance[i] > 6) continue;
            candidateCells.put(i, Float.valueOf(0.0f));
        }
        int targetpos = target.pos;
        if (Dungeon.level.trueDistance(target.pos, Dungeon.hero.pos) >= 4.0f) {
            for (int i : PathFinder.NEIGHBOURS8) {
                while (!Dungeon.level.solid[targetpos + i] && Dungeon.level.trueDistance(target.pos + i, Dungeon.hero.pos) < Dungeon.level.trueDistance(targetpos, Dungeon.hero.pos)) {
                    targetpos = target.pos + i;
                }
            }
        }
        float closest = 100.0f;
        Iterator iterator = candidateCells.keySet().iterator();
        while (iterator.hasNext()) {
            cell = (Integer)iterator.next();
            float dist = Dungeon.level.distance(cell, targetpos);
            if (!(dist < closest)) continue;
            closest = dist;
        }
        iterator = candidateCells.keySet().iterator();
        while (iterator.hasNext()) {
            cell = (Integer)iterator.next();
            float dist = Dungeon.level.distance(cell, targetpos);
            if (dist - closest == 0.0f) {
                candidateCells.put(cell, Float.valueOf(8.0f));
                continue;
            }
            if (dist - closest <= 1.0f) {
                candidateCells.put(cell, Float.valueOf(1.0f));
                continue;
            }
            candidateCells.put(cell, Float.valueOf(0.0f));
        }
        if (!candidateCells.isEmpty() && (targetCell = (Integer)Random.chances(candidateCells)) != null) {
            Buff.affect(Dungeon.hero, GasSpewer.class, Dungeon.hero.cooldown()).set(targetCell, gasToSpawn, (int)gasQuantity);
            GLog.w(Messages.get(ChaoticCenser.class, "spew", Messages.titleCase(Messages.get(gasToSpawn, "name", new Object[0]))), new Object[0]);
            if (target.sprite != null && target.sprite.parent != null) {
                GameScene.targetedCell(targetCell, Dungeon.hero.cooldown());
            }
            return true;
        }
        return false;
    }

    static {
        ChaoticCenser.GAS_CAT_CHANCES[0] = new float[]{80.0f, 10.0f, 10.0f};
        ChaoticCenser.GAS_CAT_CHANCES[1] = new float[]{67.0f, 13.0f, 20.0f};
        ChaoticCenser.GAS_CAT_CHANCES[2] = new float[]{54.0f, 16.0f, 30.0f};
        ChaoticCenser.GAS_CAT_CHANCES[3] = new float[]{40.0f, 20.0f, 40.0f};
        COMMON_GASSES = new HashMap();
        COMMON_GASSES.put(ToxicGas.class, Float.valueOf(300.0f));
        COMMON_GASSES.put(ConfusionGas.class, Float.valueOf(300.0f));
        COMMON_GASSES.put(StenchGas.class, Float.valueOf(200.0f));
        UNCOMMON_GASSES = new HashMap();
        UNCOMMON_GASSES.put(StormCloud.class, Float.valueOf(300.0f));
        UNCOMMON_GASSES.put(SmokeScreen.class, Float.valueOf(300.0f));
        UNCOMMON_GASSES.put(Regrowth.class, Float.valueOf(200.0f));
        RARE_GASSES = new HashMap();
        RARE_GASSES.put(Inferno.class, Float.valueOf(300.0f));
        RARE_GASSES.put(Blizzard.class, Float.valueOf(300.0f));
        RARE_GASSES.put(CorrosiveGas.class, Float.valueOf(200.0f));
        MISSILE_VFX = new HashMap();
        MISSILE_VFX.put(ToxicGas.class, 1107);
        MISSILE_VFX.put(ConfusionGas.class, 1113);
        MISSILE_VFX.put(Regrowth.class, 4);
        MISSILE_VFX.put(StormCloud.class, 1117);
        MISSILE_VFX.put(SmokeScreen.class, 1116);
        MISSILE_VFX.put(StenchGas.class, 1111);
        MISSILE_VFX.put(Inferno.class, 1118);
        MISSILE_VFX.put(Blizzard.class, 1119);
        MISSILE_VFX.put(CorrosiveGas.class, 1108);
    }

    public static class GasSpewer
    extends FlavourBuff {
        private int targetCell;
        private int depth;
        private int branch;
        private Class<? extends Blob> gasType;
        private int gasQuantity;
        private static final String CELL = "cell";
        private static final String DEPTH = "depth";
        private static final String BRANCH = "branch";
        private static final String GAS_TYPE = "gas_type";
        private static final String GAS_QUANTITY = "gas_quantity";

        public void set(int targetCell, Class<? extends Blob> gasType, int gasQuantity) {
            this.targetCell = targetCell;
            this.depth = Dungeon.depth;
            this.branch = Dungeon.branch;
            this.gasType = gasType;
            this.gasQuantity = gasQuantity;
        }

        @Override
        public boolean act() {
            if (this.depth == Dungeon.depth && this.branch == Dungeon.branch) {
                GameScene.add(Blob.seed(this.targetCell, this.gasQuantity, this.gasType));
                if (this.gasType == CorrosiveGas.class) {
                    ((CorrosiveGas)Dungeon.level.blobs.get(CorrosiveGas.class)).setStrength(2 + Dungeon.scalingDepth() / 5, ChaoticCenser.class);
                }
                MagicMissile.boltFromChar(Dungeon.hero.sprite.parent, MISSILE_VFX.get(this.gasType), Dungeon.hero.sprite, this.targetCell, null);
                Sample.INSTANCE.play("sounds/gas.mp3");
            }
            this.detach();
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CELL, this.targetCell);
            bundle.put(DEPTH, this.depth);
            bundle.put(BRANCH, this.branch);
            bundle.put(GAS_TYPE, this.gasType);
            bundle.put(GAS_QUANTITY, this.gasQuantity);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.targetCell = bundle.getInt(CELL);
            this.depth = bundle.getInt(DEPTH);
            this.branch = bundle.getInt(BRANCH);
            this.gasType = bundle.getClass(GAS_TYPE);
            this.gasQuantity = bundle.getInt(GAS_QUANTITY);
        }
    }

    public static class CenserGasTracker
    extends Buff {
        private int left = Integer.MAX_VALUE;
        private int safeAreaDelay = 100;
        private static String LEFT = "left";

        @Override
        public boolean act() {
            Char target;
            int avgTurns = ChaoticCenser.averageTurnsUntilGas();
            if (avgTurns == -1) {
                this.spend(Random.NormalIntRange(1, 5));
                return true;
            }
            if ((float)this.left > (float)avgTurns * 1.2f) {
                this.left = Random.IntRange((int)((float)avgTurns * 0.833f), (int)((float)avgTurns * 1.2f));
            }
            if (this.left <= 0 && TargetHealthIndicator.instance != null && TargetHealthIndicator.instance.isVisible() && (target = TargetHealthIndicator.instance.target()) != null && target.isActive() && target.alignment == Char.Alignment.ENEMY && (!(target instanceof Mob) || ((Mob)target).state != ((Mob)target).PASSIVE) && ChaoticCenser.produceGas(target)) {
                Sample.INSTANCE.play("sounds/gas.mp3", 0.5f);
                Dungeon.hero.interrupt();
                this.left += Random.IntRange((int)((float)avgTurns * 0.9f), (int)((float)avgTurns * 1.1f));
            }
            int delay = Random.NormalIntRange(1, 3);
            this.spend(delay);
            this.left = (int)Math.max((float)(this.left - delay), (float)(-avgTurns) / 3.0f);
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, this.left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains(LEFT)) {
                this.left = bundle.getInt(LEFT);
            }
        }
    }
}

