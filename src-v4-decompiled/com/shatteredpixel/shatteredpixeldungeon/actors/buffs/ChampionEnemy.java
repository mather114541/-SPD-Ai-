/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Image;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class ChampionEnemy
extends Buff {
    protected int color;
    protected int rays;

    public ChampionEnemy() {
        this.type = Buff.buffType.POSITIVE;
        this.revivePersists = true;
        this.immunities.add(AllyBuff.class);
    }

    @Override
    public int icon() {
        return 36;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(this.color);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.aura(this.color, this.rays);
        } else {
            this.target.sprite.clearAura();
        }
    }

    public void onAttackProc(Char enemy) {
    }

    public boolean canAttackWithExtraReach(Char enemy) {
        return false;
    }

    public float meleeDamageFactor() {
        return 1.0f;
    }

    public float damageTakenFactor() {
        return 1.0f;
    }

    public float evasionAndAccuracyFactor() {
        return 1.0f;
    }

    public static void rollForChampion(Mob m) {
        Class buffCls;
        Dungeon.mobsToChampion -= 1.0f;
        switch (Random.Int(6)) {
            default: {
                buffCls = Blazing.class;
                break;
            }
            case 1: {
                buffCls = Projecting.class;
                break;
            }
            case 2: {
                buffCls = AntiMagic.class;
                break;
            }
            case 3: {
                buffCls = Giant.class;
                break;
            }
            case 4: {
                buffCls = Blessed.class;
                break;
            }
            case 5: {
                buffCls = Growing.class;
            }
        }
        if (Dungeon.mobsToChampion <= 0.0f && Dungeon.isChallenged(128)) {
            if (m instanceof Crab && Dungeon.scalingDepth() <= 3) {
                return;
            }
            if (m instanceof Thief && Dungeon.scalingDepth() <= 4) {
                return;
            }
            if (m instanceof Guard && Dungeon.scalingDepth() <= 7) {
                return;
            }
            if (m instanceof Bat && Dungeon.scalingDepth() <= 9) {
                return;
            }
            Buff.affect(m, buffCls);
            Dungeon.mobsToChampion += 8.0f - (float)Math.min(20, Dungeon.scalingDepth() - 1) / 10.0f;
            if (m.state != m.PASSIVE) {
                m.state = m.WANDERING;
            }
        }
    }

    public static class Blazing
    extends ChampionEnemy {
        public Blazing() {
            this.color = 0xFF8800;
            this.rays = 4;
            this.immunities.add(Burning.class);
        }

        @Override
        public void onAttackProc(Char enemy) {
            if (!Dungeon.level.water[enemy.pos]) {
                Buff.affect(enemy, Burning.class).reignite(enemy);
            }
        }

        @Override
        public void detach() {
            if (this.target.flying || !Dungeon.level.pit[this.target.pos]) {
                for (int i : PathFinder.NEIGHBOURS9) {
                    if (Dungeon.level.solid[this.target.pos + i] || Dungeon.level.water[this.target.pos + i]) continue;
                    GameScene.add(Blob.seed(this.target.pos + i, 2, Fire.class));
                }
            }
            super.detach();
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }
    }

    public static class Projecting
    extends ChampionEnemy {
        public Projecting() {
            this.color = 0x8800FF;
            this.rays = 4;
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }

        @Override
        public boolean canAttackWithExtraReach(Char enemy) {
            if (Dungeon.level.distance(this.target.pos, enemy.pos) > 4) {
                return false;
            }
            boolean[] passable = BArray.not(Dungeon.level.solid, null);
            for (Char ch : Actor.chars()) {
                passable[ch.pos] = ch == this.target;
            }
            PathFinder.buildDistanceMap(enemy.pos, passable, 4);
            return PathFinder.distance[this.target.pos] <= 4;
        }
    }

    public static class AntiMagic
    extends ChampionEnemy {
        public AntiMagic() {
            this.color = 65280;
            this.rays = 5;
            this.immunities.addAll(com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic.RESISTS);
        }

        @Override
        public float damageTakenFactor() {
            return 0.5f;
        }
    }

    public static class Giant
    extends ChampionEnemy {
        public Giant() {
            this.color = 35071;
            this.rays = 5;
        }

        @Override
        public float damageTakenFactor() {
            return 0.2f;
        }

        @Override
        public boolean canAttackWithExtraReach(Char enemy) {
            if (Dungeon.level.distance(this.target.pos, enemy.pos) > 2) {
                return false;
            }
            boolean[] passable = BArray.not(Dungeon.level.solid, null);
            for (Char ch : Actor.chars()) {
                passable[ch.pos] = ch == this.target;
            }
            PathFinder.buildDistanceMap(enemy.pos, passable, 2);
            return PathFinder.distance[this.target.pos] <= 2;
        }
    }

    public static class Blessed
    extends ChampionEnemy {
        public Blessed() {
            this.color = 0xFFFF00;
            this.rays = 6;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return 4.0f;
        }
    }

    public static class Growing
    extends ChampionEnemy {
        private float multiplier;
        private static final String MULTIPLIER = "multiplier";

        public Growing() {
            this.color = 0xFF2222;
            this.rays = 6;
            this.multiplier = 1.19f;
        }

        @Override
        public boolean act() {
            this.multiplier += 0.01f;
            this.spend(4.0f);
            return true;
        }

        @Override
        public float meleeDamageFactor() {
            return this.multiplier;
        }

        @Override
        public float damageTakenFactor() {
            return 1.0f / this.multiplier;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return this.multiplier;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", (int)(100.0f * (this.multiplier - 1.0f)), (int)(100.0f * (1.0f - 1.0f / this.multiplier)));
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(MULTIPLIER, this.multiplier);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.multiplier = bundle.getFloat(MULTIPLIER);
        }
    }
}

