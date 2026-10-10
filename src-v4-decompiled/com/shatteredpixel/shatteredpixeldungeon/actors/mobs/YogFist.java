/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FistSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class YogFist
extends Mob {
    private float rangedCooldown;
    protected boolean canRangedInMelee;
    private boolean invulnWarned;
    public static final String RANGED_COOLDOWN = "ranged_cooldown";

    public YogFist() {
        this.HT = 300;
        this.HP = 300;
        this.defenseSkill = 20;
        this.viewDistance = 6;
        this.EXP = 25;
        this.maxLvl = -2;
        this.state = this.HUNTING;
        this.properties.add(Char.Property.BOSS);
        this.properties.add(Char.Property.DEMONIC);
        this.canRangedInMelee = true;
        this.invulnWarned = false;
        this.immunities.add(Sleep.class);
    }

    protected void incrementRangedCooldown() {
        this.rangedCooldown += Random.NormalFloat(8.0f, 12.0f);
    }

    @Override
    protected boolean act() {
        if (this.paralysed <= 0 && this.rangedCooldown > 0.0f) {
            this.rangedCooldown -= 1.0f;
        }
        if (Dungeon.hero.invisible <= 0 && this.state == this.WANDERING) {
            this.beckon(Dungeon.hero.pos);
            this.state = this.HUNTING;
            this.enemy = Dungeon.hero;
        }
        return super.act();
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (this.rangedCooldown <= 0.0f) {
            return new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos == enemy.pos;
        }
        return super.canAttack(enemy);
    }

    protected boolean isNearYog() {
        int yogPos = Dungeon.level.exit() + 3 * Dungeon.level.width();
        return Dungeon.level.distance(this.pos, yogPos) <= 4;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        if (this.isNearYog() && !this.invulnWarned) {
            this.invulnWarned = true;
            GLog.w(Messages.get(this, "invuln_warn", new Object[0]), new Object[0]);
        }
        return this.isNearYog() || super.isInvulnerable(effect);
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent(this.pos, enemy.pos) && (!this.canRangedInMelee || this.rangedCooldown > 0.0f)) {
            return super.doAttack(enemy);
        }
        this.incrementRangedCooldown();
        if (this.sprite != null && (this.sprite.visible || enemy.sprite.visible)) {
            this.sprite.zap(enemy.pos);
            return false;
        }
        this.zap();
        return true;
    }

    @Override
    public void damage(int dmg, Object src) {
        int preHP = this.HP;
        super.damage(dmg, src);
        int dmgTaken = preHP - this.HP;
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (dmgTaken > 0 && lock != null && !this.isImmune(src.getClass()) && !this.isInvulnerable(src.getClass())) {
            if (Dungeon.isChallenged(256)) {
                lock.addTime((float)dmgTaken / 4.0f);
            } else {
                lock.addTime((float)dmgTaken / 2.0f);
            }
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        for (Char c : Actor.chars()) {
            if (!(c instanceof YogDzewa)) continue;
            ((YogDzewa)c).processFistDeath();
        }
    }

    protected abstract void zap();

    public void onZapComplete() {
        this.zap();
        this.next();
    }

    @Override
    public int attackSkill(Char target) {
        return 36;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(18, 36);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 15);
    }

    @Override
    public String description() {
        return Messages.get(YogFist.class, "desc", new Object[0]) + "\n\n" + Messages.get(this, "desc", new Object[0]);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(RANGED_COOLDOWN, this.rangedCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.rangedCooldown = bundle.getFloat(RANGED_COOLDOWN);
    }

    public static class DarkFist
    extends YogFist {
        public DarkFist() {
            this.spriteClass = FistSprite.Dark.class;
            this.canRangedInMelee = false;
        }

        @Override
        protected void incrementRangedCooldown() {
        }

        @Override
        protected void zap() {
            this.spend(1.0f);
            Invisibility.dispel(this);
            Char enemy = this.enemy;
            if (DarkFist.hit(this, enemy, true)) {
                enemy.damage(Random.NormalIntRange(10, 20), new DarkBolt());
                Light l = enemy.buff(Light.class);
                if (l != null) {
                    l.weaken(50);
                }
                if (!enemy.isAlive() && enemy == Dungeon.hero) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(Char.class, "kill", this.name()), new Object[0]);
                }
            } else {
                enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
            }
        }

        @Override
        public void damage(int dmg, Object src) {
            int beforeHP = this.HP;
            super.damage(dmg, src);
            if (this.isAlive() && beforeHP > this.HT / 2 && this.HP <= this.HT / 2) {
                int i;
                this.HP = this.HT / 2;
                Light l = Dungeon.hero.buff(Light.class);
                if (l != null) {
                    l.detach();
                }
                while (Dungeon.level.heroFOV[i = Random.Int(Dungeon.level.length())] || Dungeon.level.solid[i] || Actor.findChar(i) != null || PathFinder.getStep(i, Dungeon.level.exit(), Dungeon.level.passable) == -1) {
                }
                ScrollOfTeleportation.appear(this, i);
                this.state = this.WANDERING;
                GameScene.flash(0, false);
                GLog.w(Messages.get(this, "teleport", new Object[0]), new Object[0]);
            } else if (!this.isAlive()) {
                Light l = Dungeon.hero.buff(Light.class);
                if (l != null) {
                    l.detach();
                }
                GameScene.flash(0, false);
            }
        }

        public static class DarkBolt {
        }
    }

    public static class BrightFist
    extends YogFist {
        public BrightFist() {
            this.spriteClass = FistSprite.Bright.class;
            this.properties.add(Char.Property.ELECTRIC);
            this.canRangedInMelee = false;
        }

        @Override
        protected void incrementRangedCooldown() {
        }

        @Override
        protected void zap() {
            this.spend(1.0f);
            Invisibility.dispel(this);
            Char enemy = this.enemy;
            if (BrightFist.hit(this, enemy, true)) {
                enemy.damage(Random.NormalIntRange(10, 20), new LightBeam());
                Buff.prolong(enemy, Blindness.class, 5.0f);
                if (!enemy.isAlive() && enemy == Dungeon.hero) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(Char.class, "kill", this.name()), new Object[0]);
                }
            } else {
                enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
            }
        }

        @Override
        public void damage(int dmg, Object src) {
            int beforeHP = this.HP;
            super.damage(dmg, src);
            if (this.isAlive() && beforeHP > this.HT / 2 && this.HP <= this.HT / 2) {
                int i;
                this.HP = this.HT / 2;
                Buff.prolong(Dungeon.hero, Blindness.class, 15.0f);
                while (Dungeon.level.heroFOV[i = Random.Int(Dungeon.level.length())] || Dungeon.level.solid[i] || Actor.findChar(i) != null || PathFinder.getStep(i, Dungeon.level.exit(), Dungeon.level.passable) == -1) {
                }
                ScrollOfTeleportation.appear(this, i);
                this.state = this.WANDERING;
                GameScene.flash(-2130706433);
                GLog.w(Messages.get(this, "teleport", new Object[0]), new Object[0]);
            } else if (!this.isAlive()) {
                Buff.prolong(Dungeon.hero, Blindness.class, 30.0f);
                GameScene.flash(-2130706433);
            }
        }

        public static class LightBeam {
        }
    }

    public static class RustedFist
    extends YogFist {
        public RustedFist() {
            this.spriteClass = FistSprite.Rusted.class;
            this.properties.add(Char.Property.LARGE);
            this.properties.add(Char.Property.INORGANIC);
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(22, 44);
        }

        @Override
        public void damage(int dmg, Object src) {
            if (!this.isInvulnerable(src.getClass()) && !(src instanceof Viscosity.DeferedDamage)) {
                if ((dmg = Math.round((float)dmg * this.resist(src.getClass()))) >= 0) {
                    Buff.affect(this, Viscosity.DeferedDamage.class).extend(dmg);
                    this.sprite.showStatus(0xFF8800, Messages.get(Viscosity.class, "deferred", dmg), new Object[0]);
                }
            } else {
                super.damage(dmg, src);
            }
        }

        @Override
        protected void zap() {
            this.spend(1.0f);
            Buff.affect(this.enemy, Cripple.class, 4.0f);
        }
    }

    public static class RottingFist
    extends YogFist {
        public RottingFist() {
            this.spriteClass = FistSprite.Rotting.class;
            this.properties.add(Char.Property.ACIDIC);
            this.immunities.add(ToxicGas.class);
        }

        @Override
        protected boolean act() {
            GameScene.add(Blob.seed(this.pos, 0, ToxicGas.class));
            if (Dungeon.level.water[this.pos] && this.HP < this.HT) {
                this.sprite.showStatusWithIcon(65280, Integer.toString(this.HT / 50), FloatingText.HEALING, new Object[0]);
                this.HP = Math.min(this.HT, this.HP + this.HT / 50);
            }
            return super.act();
        }

        @Override
        public void damage(int dmg, Object src) {
            if (!this.isInvulnerable(src.getClass()) && !(src instanceof Bleeding) && this.buff(Sickle.HarvestBleedTracker.class) == null) {
                if ((dmg = Math.round((float)dmg * this.resist(src.getClass()))) < 0) {
                    return;
                }
                Bleeding b = this.buff(Bleeding.class);
                if (b == null) {
                    b = new Bleeding();
                }
                b.announced = false;
                b.attachTo(this);
                b.set((float)dmg * 0.6f);
                this.sprite.showStatus(0xFF8800, Messages.titleCase(b.name()) + " " + (int)b.level(), new Object[0]);
            } else {
                super.damage(dmg, src);
            }
        }

        @Override
        protected void zap() {
            this.spend(1.0f);
            GameScene.add(Blob.seed(this.enemy.pos, 100, ToxicGas.class));
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage = super.attackProc(enemy, damage);
            if (Random.Int(2) == 0) {
                Buff.affect(enemy, Ooze.class).set(20.0f);
                enemy.sprite.burst(-16777216, 5);
            }
            return damage;
        }
    }

    public static class SoiledFist
    extends YogFist {
        public SoiledFist() {
            this.spriteClass = FistSprite.Soiled.class;
        }

        @Override
        public boolean act() {
            boolean result = super.act();
            int furrowedTiles = Random.chances(new float[]{0.0f, 2.0f, 1.0f});
            for (int i = 0; i < furrowedTiles; ++i) {
                int cell = this.pos + PathFinder.NEIGHBOURS9[Random.Int(9)];
                if (Dungeon.level.map[cell] != 2) continue;
                Level.set(cell, 30);
                GameScene.updateMap(cell);
                CellEmitter.get(cell).burst(LeafParticle.GENERAL, 10);
            }
            Dungeon.observe();
            for (int i : PathFinder.NEIGHBOURS9) {
                int cell = this.pos + i;
                if (!this.canSpreadGrass(cell)) continue;
                Level.set(this.pos + i, 2);
                GameScene.updateMap(this.pos + i);
            }
            return result;
        }

        @Override
        public void damage(int dmg, Object src) {
            int grassCells = 0;
            for (int i : PathFinder.NEIGHBOURS9) {
                if (Dungeon.level.map[this.pos + i] != 30 && Dungeon.level.map[this.pos + i] != 15) continue;
                ++grassCells;
            }
            if (grassCells > 0) {
                dmg = Math.round((float)(dmg * (6 - grassCells)) / 6.0f);
            }
            if (src.getClass() == Burning.class) {
                return;
            }
            super.damage(dmg, src);
        }

        @Override
        public int incomingDOT() {
            if (!this.needsIncomingDOTUpdate) {
                return this.cachedIncomingDOT;
            }
            int total = super.incomingDOT();
            Burning b = this.buff(Burning.class);
            if (b != null) {
                total -= b.totalIncomingDMG();
            }
            this.cachedIncomingDOT = total;
            return total;
        }

        @Override
        protected void zap() {
            this.spend(1.0f);
            Invisibility.dispel(this);
            Char enemy = this.enemy;
            if (SoiledFist.hit(this, enemy, true)) {
                Buff.affect(enemy, Roots.class, 3.0f);
            } else {
                enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
            }
            for (int i : PathFinder.NEIGHBOURS9) {
                int cell = enemy.pos + i;
                if (!this.canSpreadGrass(cell)) continue;
                if (Random.Int(5) == 0) {
                    Level.set(cell, 30);
                    GameScene.updateMap(cell);
                } else {
                    Level.set(cell, 2);
                    GameScene.updateMap(cell);
                }
                CellEmitter.get(cell).burst(LeafParticle.GENERAL, 10);
            }
            Dungeon.observe();
        }

        private boolean canSpreadGrass(int cell) {
            int yogPos = Dungeon.level.exit() + Dungeon.level.width() * 3;
            return Dungeon.level.distance(cell, yogPos) > 4 && !Dungeon.level.solid[cell] && Dungeon.level.map[cell] != 30 && Dungeon.level.map[cell] != 15;
        }
    }

    public static class BurningFist
    extends YogFist {
        public BurningFist() {
            this.spriteClass = FistSprite.Burning.class;
            this.properties.add(Char.Property.FIERY);
            this.immunities.add(Frost.class);
            this.resistances.add(StormCloud.class);
            this.resistances.add(GeyserTrap.class);
        }

        @Override
        public boolean act() {
            boolean result = super.act();
            if (Dungeon.level.map[this.pos] == 29) {
                Level.set(this.pos, 1);
                GameScene.updateMap(this.pos);
                CellEmitter.get(this.pos).burst(Speck.factory(13), 10);
            }
            int evaporatedTiles = Random.chances(new float[]{0.0f, 1.0f, 2.0f});
            for (int i = 0; i < evaporatedTiles; ++i) {
                int cell = this.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                if (Dungeon.level.map[cell] != 29) continue;
                Level.set(cell, 1);
                GameScene.updateMap(cell);
                CellEmitter.get(cell).burst(Speck.factory(13), 10);
            }
            for (int i : PathFinder.NEIGHBOURS9) {
                int vol = Fire.volumeAt(this.pos + i, Fire.class);
                if (vol >= 4 || Dungeon.level.water[this.pos + i] || Dungeon.level.solid[this.pos + i]) continue;
                GameScene.add(Blob.seed(this.pos + i, 4 - vol, Fire.class));
            }
            return result;
        }

        @Override
        protected void zap() {
            this.spend(1.0f);
            if (Dungeon.level.map[this.enemy.pos] == 29) {
                Level.set(this.enemy.pos, 1);
                GameScene.updateMap(this.enemy.pos);
                CellEmitter.get(this.enemy.pos).burst(Speck.factory(13), 10);
            } else {
                Buff.affect(this.enemy, Burning.class).reignite(this.enemy);
            }
            for (int i : PathFinder.NEIGHBOURS9) {
                int vol;
                if (Dungeon.level.water[this.enemy.pos + i] || Dungeon.level.solid[this.enemy.pos + i] || (vol = Fire.volumeAt(this.enemy.pos + i, Fire.class)) >= 4) continue;
                GameScene.add(Blob.seed(this.enemy.pos + i, 4 - vol, Fire.class));
            }
        }
    }
}

