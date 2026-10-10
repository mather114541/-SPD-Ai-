/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElementalSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public abstract class Elemental
extends Mob {
    protected boolean summonedALly;
    protected int rangedCooldown;
    protected ArrayList<Class<? extends Buff>> harmfulBuffs;
    private static final String COOLDOWN = "cooldown";
    private static final String SUMMONED_ALLY = "summoned_ally";

    public Elemental() {
        this.HT = 60;
        this.HP = 60;
        this.defenseSkill = 20;
        this.EXP = 10;
        this.maxLvl = 20;
        this.flying = true;
        this.rangedCooldown = Random.NormalIntRange(3, 5);
        this.harmfulBuffs = new ArrayList();
    }

    @Override
    public int damageRoll() {
        if (!this.summonedALly) {
            return Random.NormalIntRange(20, 25);
        }
        int regionScale = Math.max(2, 1 + Dungeon.scalingDepth() / 5);
        return Random.NormalIntRange(5 * regionScale, 5 + 5 * regionScale);
    }

    @Override
    public int attackSkill(Char target) {
        if (!this.summonedALly) {
            return 25;
        }
        int regionScale = Math.max(2, 1 + Dungeon.scalingDepth() / 5);
        return 5 + 5 * regionScale;
    }

    public void setSummonedALly() {
        this.summonedALly = true;
        int regionScale = Math.max(2, 1 + Dungeon.scalingDepth() / 5);
        this.defenseSkill = 5 * regionScale;
        this.HT = 15 * regionScale;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 5);
    }

    @Override
    protected boolean act() {
        if (this.state == this.HUNTING) {
            --this.rangedCooldown;
        }
        return super.act();
    }

    @Override
    public void die(Object cause) {
        this.flying = false;
        super.die(cause);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (super.canAttack(enemy)) {
            return true;
        }
        return this.rangedCooldown < 0 && new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent(this.pos, enemy.pos) || this.rangedCooldown > 0 || new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos != enemy.pos) {
            return super.doAttack(enemy);
        }
        if (this.sprite != null && (this.sprite.visible || enemy.sprite.visible)) {
            this.sprite.zap(enemy.pos);
            return false;
        }
        this.zap();
        return true;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        this.meleeProc(enemy, damage);
        return damage;
    }

    protected void zap() {
        this.spend(1.0f);
        Invisibility.dispel(this);
        Char enemy = this.enemy;
        if (Elemental.hit(this, enemy, true)) {
            this.rangedProc(enemy);
        } else {
            enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
        }
        this.rangedCooldown = Random.NormalIntRange(3, 5);
    }

    public void onZapComplete() {
        this.zap();
        this.next();
    }

    @Override
    public boolean add(Buff buff) {
        if (this.harmfulBuffs.contains(buff.getClass())) {
            this.damage(Random.NormalIntRange(this.HT / 2, this.HT * 3 / 5), buff);
            return false;
        }
        return super.add(buff);
    }

    protected abstract void meleeProc(Char var1, int var2);

    protected abstract void rangedProc(Char var1);

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COOLDOWN, this.rangedCooldown);
        bundle.put(SUMMONED_ALLY, this.summonedALly);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(COOLDOWN)) {
            this.rangedCooldown = bundle.getInt(COOLDOWN);
        }
        this.summonedALly = bundle.getBoolean(SUMMONED_ALLY);
        if (this.summonedALly) {
            this.setSummonedALly();
        }
    }

    public static Class<? extends Elemental> random() {
        float altChance = 0.02f * RatSkull.exoticChanceMultiplier();
        if (Random.Float() < altChance) {
            return ChaosElemental.class;
        }
        float roll = Random.Float();
        if (roll < 0.4f) {
            return FireElemental.class;
        }
        if (roll < 0.8f) {
            return FrostElemental.class;
        }
        return ShockElemental.class;
    }

    public static class ChaosElemental
    extends Elemental {
        public ChaosElemental() {
            this.spriteClass = ElementalSprite.Chaos.class;
            this.loot = ScrollOfTransmutation.class;
            this.lootChance = 1.0f;
        }

        @Override
        protected void meleeProc(Char enemy, int damage) {
            Ballistica aim = new Ballistica(this.pos, enemy.pos, 1);
            CursedWand.randomValidEffect(null, this, aim, false).effect(null, this, aim, false);
        }

        @Override
        protected void zap() {
            this.spend(1.0f);
            Invisibility.dispel(this);
            Char enemy = this.enemy;
            this.rangedProc(enemy);
            this.rangedCooldown = Random.NormalIntRange(3, 5);
        }

        @Override
        public void onZapComplete() {
            this.zap();
        }

        @Override
        protected void rangedProc(Char enemy) {
            CursedWand.cursedZap(null, this, new Ballistica(this.pos, enemy.pos, 1), new Callback(){

                @Override
                public void call() {
                    this.next();
                }
            });
        }
    }

    public static class FireElemental
    extends Elemental {
        public FireElemental() {
            this.spriteClass = ElementalSprite.Fire.class;
            this.loot = PotionOfLiquidFlame.class;
            this.lootChance = 0.125f;
            this.properties.add(Char.Property.FIERY);
            this.harmfulBuffs.add(Frost.class);
            this.harmfulBuffs.add(Chill.class);
        }

        @Override
        protected void meleeProc(Char enemy, int damage) {
            if (Random.Int(2) == 0 && !Dungeon.level.water[enemy.pos]) {
                Buff.affect(enemy, Burning.class).reignite(enemy);
                if (enemy.sprite.visible) {
                    Splash.at(enemy.sprite.center(), this.sprite.blood(), 5);
                }
            }
        }

        @Override
        protected void rangedProc(Char enemy) {
            if (!Dungeon.level.water[enemy.pos]) {
                Buff.affect(enemy, Burning.class).reignite(enemy, 4.0f);
            }
            if (enemy.sprite.visible) {
                Splash.at(enemy.sprite.center(), this.sprite.blood(), 5);
            }
        }
    }

    public static class FrostElemental
    extends Elemental {
        public FrostElemental() {
            this.spriteClass = ElementalSprite.Frost.class;
            this.loot = PotionOfFrost.class;
            this.lootChance = 0.125f;
            this.properties.add(Char.Property.ICY);
            this.harmfulBuffs.add(Burning.class);
        }

        @Override
        protected void meleeProc(Char enemy, int damage) {
            if (Random.Int(3) == 0 || Dungeon.level.water[enemy.pos]) {
                Freezing.freeze(enemy.pos);
                if (enemy.sprite.visible) {
                    Splash.at(enemy.sprite.center(), this.sprite.blood(), 5);
                }
            }
        }

        @Override
        protected void rangedProc(Char enemy) {
            Freezing.freeze(enemy.pos);
            if (enemy.sprite.visible) {
                Splash.at(enemy.sprite.center(), this.sprite.blood(), 5);
            }
        }
    }

    public static class ShockElemental
    extends Elemental {
        public ShockElemental() {
            this.spriteClass = ElementalSprite.Shock.class;
            this.loot = ScrollOfRecharging.class;
            this.lootChance = 0.25f;
            this.properties.add(Char.Property.ELECTRIC);
        }

        @Override
        protected void meleeProc(Char enemy, int damage) {
            ArrayList<Char> affected = new ArrayList<Char>();
            ArrayList<Lightning.Arc> arcs = new ArrayList<Lightning.Arc>();
            Shocking.arc(this, enemy, 2, affected, arcs);
            if (!Dungeon.level.water[enemy.pos]) {
                affected.remove(enemy);
            }
            for (Char ch : affected) {
                ch.damage(Math.round((float)damage * 0.4f), new Shocking());
                if (ch != Dungeon.hero || ch.isAlive()) continue;
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", this.name())), new Object[0]);
            }
            boolean visible = this.sprite.visible || enemy.sprite.visible;
            for (Char ch : affected) {
                if (!ch.sprite.visible) continue;
                visible = true;
            }
            if (visible) {
                this.sprite.parent.addToFront(new Lightning(arcs, null));
                Sample.INSTANCE.play("sounds/lightning.mp3");
            }
        }

        @Override
        protected void rangedProc(Char enemy) {
            Buff.affect(enemy, Blindness.class, 5.0f);
            if (enemy == Dungeon.hero) {
                GameScene.flash(-2130706433);
            }
        }
    }

    public static class AllyNewBornElemental
    extends NewbornFireElemental {
        public AllyNewBornElemental() {
            this.rangedCooldown = Integer.MAX_VALUE;
            this.properties.remove((Object)Char.Property.MINIBOSS);
        }
    }

    public static class NewbornFireElemental
    extends FireElemental {
        private int targetingPos;
        private static final String TARGETING_POS = "targeting_pos";

        public NewbornFireElemental() {
            this.spriteClass = ElementalSprite.NewbornFire.class;
            this.defenseSkill = 12;
            this.properties.add(Char.Property.MINIBOSS);
            this.targetingPos = -1;
        }

        @Override
        protected boolean act() {
            if (this.targetingPos != -1 && this.state == this.HUNTING) {
                this.targetingPos = new Ballistica((int)this.pos, (int)this.targetingPos, (int)5).collisionPos;
                if (this.sprite != null && (this.sprite.visible || Dungeon.level.heroFOV[this.targetingPos])) {
                    this.sprite.zap(this.targetingPos);
                    return false;
                }
                this.zap();
                return true;
            }
            if (this.state != this.HUNTING) {
                this.targetingPos = -1;
            }
            return super.act();
        }

        @Override
        protected boolean canAttack(Char enemy) {
            if (super.canAttack(enemy)) {
                return true;
            }
            return this.rangedCooldown < 0 && new Ballistica((int)this.pos, (int)enemy.pos, (int)5).collisionPos == enemy.pos;
        }

        @Override
        protected boolean doAttack(Char enemy) {
            if (this.rangedCooldown > 0) {
                return super.doAttack(enemy);
            }
            if (new Ballistica((int)this.pos, (int)enemy.pos, (int)5).collisionPos == enemy.pos) {
                ArrayList<Integer> candidates = new ArrayList<Integer>();
                for (int i : PathFinder.NEIGHBOURS8) {
                    int target = enemy.pos + i;
                    if (target == this.pos || new Ballistica((int)this.pos, (int)target, (int)5).collisionPos != target) continue;
                    candidates.add(target);
                }
                if (!candidates.isEmpty()) {
                    this.targetingPos = (Integer)Random.element(candidates);
                    float delay = GameMath.gate(this.attackDelay(), (int)Math.ceil(Dungeon.hero.cooldown()), 3.0f * this.attackDelay());
                    for (int i : PathFinder.NEIGHBOURS9) {
                        if (Dungeon.level.solid[this.targetingPos + i]) continue;
                        GameScene.targetedCell(this.targetingPos + i, delay);
                    }
                    GLog.n(Messages.get(this, "charging", new Object[0]), new Object[0]);
                    this.spend(delay);
                    Dungeon.hero.interrupt();
                    return true;
                }
                this.rangedCooldown = 1;
                return super.doAttack(enemy);
            }
            if (this.sprite != null && (this.sprite.visible || Dungeon.level.heroFOV[this.targetingPos])) {
                this.sprite.zap(this.targetingPos);
                return false;
            }
            this.zap();
            return true;
        }

        @Override
        protected void zap() {
            if (this.targetingPos != -1) {
                this.spend(1.0f);
                Invisibility.dispel(this);
                for (int i : PathFinder.NEIGHBOURS9) {
                    if (Dungeon.level.solid[this.targetingPos + i]) continue;
                    CellEmitter.get(this.targetingPos + i).burst(ElmoParticle.FACTORY, 5);
                    if (Dungeon.level.water[this.targetingPos + i]) {
                        GameScene.add(Blob.seed(this.targetingPos + i, 2, Fire.class));
                    } else {
                        GameScene.add(Blob.seed(this.targetingPos + i, 8, Fire.class));
                    }
                    Char target = Actor.findChar(this.targetingPos + i);
                    if (target == null || target == this) continue;
                    Buff.affect(target, Burning.class).reignite(target);
                    if (target != Dungeon.hero) continue;
                    Statistics.questScores[1] = Statistics.questScores[1] - 200;
                }
                Sample.INSTANCE.play("sounds/burning.mp3");
            }
            this.targetingPos = -1;
            this.rangedCooldown = Random.NormalIntRange(3, 5);
        }

        @Override
        public int attackSkill(Char target) {
            if (!this.summonedALly) {
                return 15;
            }
            return super.attackSkill(target);
        }

        @Override
        public int damageRoll() {
            if (!this.summonedALly) {
                return Random.NormalIntRange(10, 12);
            }
            return super.damageRoll();
        }

        @Override
        protected void meleeProc(Char enemy, int damage) {
            if (this.summonedALly) {
                super.meleeProc(enemy, damage);
            }
        }

        @Override
        public void die(Object cause) {
            super.die(cause);
            if (this.alignment == Char.Alignment.ENEMY) {
                Dungeon.level.drop((Item)new Embers(), (int)this.pos).sprite.drop();
                Statistics.questScores[1] = Statistics.questScores[1] + 2000;
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        Music.INSTANCE.fadeOut(1.0f, new Callback(){

                            @Override
                            public void call() {
                                if (Dungeon.level != null) {
                                    Dungeon.level.playLevelMusic();
                                }
                            }
                        });
                    }
                });
            }
        }

        @Override
        public boolean reset() {
            return !this.summonedALly;
        }

        @Override
        public String description() {
            Object desc = super.description();
            desc = this.summonedALly ? (String)desc + " " + Messages.get(this, "desc_ally", new Object[0]) : (String)desc + " " + Messages.get(this, "desc_boss", new Object[0]);
            return desc;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TARGETING_POS, this.targetingPos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.targetingPos = bundle.getInt(TARGETING_POS);
        }
    }
}

