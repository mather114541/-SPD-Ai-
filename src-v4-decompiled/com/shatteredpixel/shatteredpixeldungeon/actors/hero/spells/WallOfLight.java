/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class WallOfLight
extends TargetedClericSpell {
    public static WallOfLight INSTANCE = new WallOfLight();

    @Override
    public int icon() {
        return 57;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 1 + 2 * Dungeon.hero.pointsInTalent(Talent.WALL_OF_LIGHT)) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public int targetingFlags() {
        return -1;
    }

    @Override
    public float chargeUse(Hero hero) {
        if (Dungeon.level.blobs.get(LightWall.class) != null && Dungeon.level.blobs.get(LightWall.class).volume > 0) {
            return 0.0f;
        }
        return 3.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.WALL_OF_LIGHT);
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        if (Dungeon.level.blobs.get(LightWall.class) != null && Dungeon.level.blobs.get(LightWall.class).volume > 0) {
            Dungeon.level.blobs.get(LightWall.class).fullyClear();
            GLog.i(Messages.get(this, "early_end", new Object[0]), new Object[0]);
            return;
        }
        super.onCast(tome, hero);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        int i;
        if (target == null) {
            return;
        }
        if (target == hero.pos) {
            GLog.w(Messages.get(this, "invalid_target", new Object[0]), new Object[0]);
            return;
        }
        int closest = hero.pos;
        int closestIdx = -1;
        for (int i2 = 0; i2 < PathFinder.CIRCLE8.length; ++i2) {
            int ofs = PathFinder.CIRCLE8[i2];
            if (!(Dungeon.level.trueDistance(target, hero.pos + ofs) < Dungeon.level.trueDistance(target, closest))) continue;
            closest = hero.pos + ofs;
            closestIdx = i2;
        }
        int leftDirX = 0;
        int leftDirY = 0;
        int rightDirX = 0;
        int rightDirY = 0;
        int steps = Dungeon.hero.pointsInTalent(Talent.WALL_OF_LIGHT);
        switch (closestIdx) {
            case 0: {
                leftDirX = -1;
                leftDirY = 1;
                rightDirX = 1;
                rightDirY = -1;
                break;
            }
            case 1: {
                leftDirX = -1;
                rightDirX = 1;
                rightDirY = 0;
                leftDirY = 0;
                break;
            }
            case 2: {
                leftDirX = 1;
                leftDirY = 1;
                rightDirX = -1;
                rightDirY = -1;
                break;
            }
            case 3: {
                leftDirY = -1;
                rightDirY = 1;
                rightDirX = 0;
                leftDirX = 0;
                break;
            }
            case 4: {
                leftDirX = 1;
                leftDirY = -1;
                rightDirX = -1;
                rightDirY = 1;
                break;
            }
            case 5: {
                leftDirX = 1;
                rightDirX = -1;
                rightDirY = 0;
                leftDirY = 0;
                break;
            }
            case 6: {
                leftDirX = -1;
                leftDirY = -1;
                rightDirX = 1;
                rightDirY = 1;
                break;
            }
            case 7: {
                leftDirY = -1;
                rightDirY = 1;
                rightDirX = 0;
                leftDirX = 0;
            }
        }
        if (Dungeon.level.blobs.get(LightWall.class) != null) {
            Dungeon.level.blobs.get(LightWall.class).fullyClear();
        }
        boolean placedWall = false;
        int knockBackDir = PathFinder.CIRCLE8[closestIdx];
        if (Dungeon.level.solid[closest] && Dungeon.level.solid[hero.pos + PathFinder.CIRCLE8[(closestIdx + 1) % 8]] && Dungeon.level.solid[hero.pos + PathFinder.CIRCLE8[(closestIdx + 7) % 8]]) {
            GLog.w(Messages.get(this, "invalid_target", new Object[0]), new Object[0]);
            return;
        }
        this.onSpellCast(tome, hero);
        this.placeWall(closest, knockBackDir);
        int leftPos = closest;
        int rightPos = closest;
        for (i = 0; i < steps; ++i) {
            if (leftDirY != 0) {
                if (!Dungeon.level.insideMap(leftPos += leftDirY * Dungeon.level.width())) break;
                this.placeWall(leftPos, knockBackDir);
            }
            if (leftDirX == 0) continue;
            if (!Dungeon.level.insideMap(leftPos += leftDirX)) break;
            this.placeWall(leftPos, knockBackDir);
        }
        for (i = 0; i < steps; ++i) {
            if (rightDirX != 0) {
                if (!Dungeon.level.insideMap(rightPos += rightDirX)) break;
                this.placeWall(rightPos, knockBackDir);
            }
            if (rightDirY == 0) continue;
            if (!Dungeon.level.insideMap(rightPos += rightDirY * Dungeon.level.width())) break;
            this.placeWall(rightPos, knockBackDir);
        }
        Sample.INSTANCE.play("sounds/chargeup.mp3");
        hero.sprite.zap(closest);
        Dungeon.hero.spendAndNext(1.0f);
    }

    private void placeWall(int pos, int knockbackDIR) {
        if (!Dungeon.level.solid[pos]) {
            GameScene.add(Blob.seed(pos, 20, LightWall.class));
            Char ch = Actor.findChar(pos);
            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                WandOfBlastWave.throwChar(ch, new Ballistica(pos, pos + knockbackDIR, 7), 1, false, false, INSTANCE);
                Buff.affect(ch, Paralysis.class, ch.cooldown());
            }
        }
    }

    public static class LightWall
    extends Blob {
        public LightWall() {
            this.alwaysVisible = true;
        }

        @Override
        protected void evolve() {
            Level l = Dungeon.level;
            for (int i = this.area.left; i < this.area.right; ++i) {
                for (int j = this.area.top; j < this.area.bottom; ++j) {
                    int cell = i + j * l.width();
                    this.off[cell] = this.cur[cell] > 0 ? this.cur[cell] - 1 : 0;
                    this.volume += this.off[cell];
                    if (this.off[cell] != 0 || this.cur[cell] <= 0) continue;
                    this.cellsToFlagUpdate.add(cell);
                }
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
                l.solid[cell] = true;
                l.passable[cell] = false;
                l.avoid[cell] = false;
            }
        }

        @Override
        public void use(BlobEmitter emitter) {
            super.use(emitter);
            emitter.pour(MagicMissile.WhiteParticle.WALL, 0.02f);
        }

        @Override
        public String tileDesc() {
            return Messages.get(this, "desc", new Object[0]);
        }
    }
}

