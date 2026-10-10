/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Goo
extends Mob {
    private int pumpedUp;
    private int healInc;
    private final String PUMPEDUP = "pumpedup";
    private final String HEALINC = "healinc";

    public Goo() {
        this.HT = Dungeon.isChallenged(256) ? 120 : 100;
        this.HP = this.HT;
        this.EXP = 10;
        this.defenseSkill = 8;
        this.spriteClass = GooSprite.class;
        this.properties.add(Char.Property.BOSS);
        this.properties.add(Char.Property.DEMONIC);
        this.properties.add(Char.Property.ACIDIC);
        this.pumpedUp = 0;
        this.healInc = 1;
        this.PUMPEDUP = "pumpedup";
        this.HEALINC = "healinc";
    }

    @Override
    public int damageRoll() {
        int max;
        int min = 1;
        int n = max = this.HP * 2 <= this.HT ? 12 : 8;
        if (this.pumpedUp > 0) {
            this.pumpedUp = 0;
            if (this.enemy == Dungeon.hero) {
                Statistics.qualifiedForBossChallengeBadge = false;
                Statistics.bossScores[0] = Statistics.bossScores[0] - 100;
            }
            return Random.NormalIntRange(min * 3, max * 3);
        }
        return Random.NormalIntRange(min, max);
    }

    @Override
    public int attackSkill(Char target) {
        int attack = 10;
        if (this.HP * 2 <= this.HT) {
            attack = 15;
        }
        if (this.pumpedUp > 0) {
            attack *= 2;
        }
        return attack;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return (int)((double)super.defenseSkill(enemy) * (this.HP * 2 <= this.HT ? 1.5 : 1.0));
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public boolean act() {
        if (this.state != this.HUNTING && this.pumpedUp > 0) {
            this.pumpedUp = 0;
            this.sprite.idle();
        }
        if (!this.flying && Dungeon.level.water[this.pos] && this.HP < this.HT) {
            this.HP += this.healInc;
            Statistics.qualifiedForBossChallengeBadge = false;
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) {
                if (Dungeon.isChallenged(256)) {
                    lock.removeTime(this.healInc);
                } else {
                    lock.removeTime((float)this.healInc * 1.5f);
                }
            }
            if (Dungeon.level.heroFOV[this.pos]) {
                this.sprite.showStatusWithIcon(65280, Integer.toString(this.healInc), FloatingText.HEALING, new Object[0]);
            }
            if (Dungeon.isChallenged(256) && this.healInc < 3) {
                ++this.healInc;
            }
            if (this.HP * 2 > this.HT) {
                BossHealthBar.bleed(false);
                ((GooSprite)this.sprite).spray(false);
                this.HP = Math.min(this.HP, this.HT);
            }
        } else {
            this.healInc = 1;
        }
        if (this.state != this.SLEEPING) {
            Dungeon.level.seal();
        }
        return super.act();
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (this.pumpedUp > 0) {
            return Dungeon.level.distance(enemy.pos, this.pos) <= 2 && new Ballistica((int)this.pos, (int)enemy.pos, (int)13).collisionPos == enemy.pos && new Ballistica((int)enemy.pos, (int)this.pos, (int)13).collisionPos == this.pos;
        }
        return super.canAttack(enemy);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(3) == 0) {
            Buff.affect(enemy, Ooze.class).set(20.0f);
            enemy.sprite.burst(0, 5);
        }
        if (this.pumpedUp > 0) {
            PixelScene.shake(3.0f, 0.2f);
        }
        return damage;
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();
        if (this.pumpedUp > 0) {
            ((GooSprite)this.sprite).pumpUp(this.pumpedUp);
        }
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (this.pumpedUp == 1) {
            ++this.pumpedUp;
            ((GooSprite)this.sprite).pumpUp(this.pumpedUp);
            this.spend(this.attackDelay());
            return true;
        }
        if (this.pumpedUp >= 2 || Random.Int(this.HP * 2 <= this.HT ? 2 : 5) > 0) {
            boolean visible = Dungeon.level.heroFOV[this.pos];
            if (visible) {
                if (this.pumpedUp >= 2) {
                    ((GooSprite)this.sprite).pumpAttack();
                } else {
                    this.sprite.attack(enemy.pos);
                }
            } else {
                if (this.pumpedUp >= 2) {
                    ((GooSprite)this.sprite).triggerEmitters();
                }
                this.attack(enemy);
                Invisibility.dispel(this);
                this.spend(this.attackDelay());
            }
            return !visible;
        }
        if (Dungeon.isChallenged(256)) {
            this.pumpedUp += 2;
            this.spend(GameMath.gate(this.attackDelay(), (int)Math.ceil(enemy.cooldown()), 3.0f * this.attackDelay()));
        } else {
            ++this.pumpedUp;
            this.spend(this.attackDelay());
        }
        ((GooSprite)this.sprite).pumpUp(this.pumpedUp);
        if (Dungeon.level.heroFOV[this.pos]) {
            this.sprite.showStatus(0xFF8800, Messages.get(this, "!!!", new Object[0]), new Object[0]);
            GLog.n(Messages.get(this, "pumpup", new Object[0]), new Object[0]);
        }
        return true;
    }

    @Override
    public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
        boolean result = super.attack(enemy, dmgMulti, dmgBonus, accMulti);
        if (this.pumpedUp > 0) {
            this.pumpedUp = 0;
            if (enemy == Dungeon.hero) {
                Statistics.qualifiedForBossChallengeBadge = false;
                Statistics.bossScores[0] = Statistics.bossScores[0] - 100;
            }
        }
        return result;
    }

    @Override
    protected boolean getCloser(int target) {
        if (this.pumpedUp != 0) {
            this.pumpedUp = 0;
            this.sprite.idle();
        }
        return super.getCloser(target);
    }

    @Override
    protected boolean getFurther(int target) {
        if (this.pumpedUp != 0) {
            this.pumpedUp = 0;
            this.sprite.idle();
        }
        return super.getFurther(target);
    }

    @Override
    public void damage(int dmg, Object src) {
        LockedFloor lock;
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            Dungeon.level.seal();
        }
        boolean bleeding = this.HP * 2 <= this.HT;
        super.damage(dmg, src);
        if (this.HP * 2 <= this.HT && !bleeding) {
            BossHealthBar.bleed(true);
            this.sprite.showStatus(0xFF8800, Messages.get(this, "enraged", new Object[0]), new Object[0]);
            ((GooSprite)this.sprite).spray(true);
            this.yell(Messages.get(this, "gluuurp", new Object[0]));
        }
        if ((lock = Dungeon.hero.buff(LockedFloor.class)) != null && !this.isImmune(src.getClass()) && !this.isInvulnerable(src.getClass())) {
            if (Dungeon.isChallenged(256)) {
                lock.addTime(dmg);
            } else {
                lock.addTime((float)dmg * 1.5f);
            }
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Dungeon.level.unseal();
        GameScene.bossSlain();
        Dungeon.level.drop((Item)new WornKey((int)Dungeon.depth), (int)this.pos).sprite.drop();
        int blobs = Random.chances(new float[]{0.0f, 0.0f, 6.0f, 3.0f, 1.0f});
        for (int i = 0; i < blobs; ++i) {
            int ofs;
            while (!Dungeon.level.passable[this.pos + (ofs = PathFinder.NEIGHBOURS8[Random.Int(8)])]) {
            }
            Dungeon.level.drop((Item)new GooBlob(), (int)(this.pos + ofs)).sprite.drop(this.pos);
        }
        Badges.validateBossSlain();
        if (Statistics.qualifiedForBossChallengeBadge) {
            Badges.validateBossChallengeCompleted();
        }
        Statistics.bossScores[0] = Statistics.bossScores[0] + 1000;
        this.yell(Messages.get(this, "defeated", new Object[0]));
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            Dungeon.level.seal();
            this.yell(Messages.get(this, "notice", new Object[0]));
            for (Char ch : Actor.chars()) {
                if (!(ch instanceof DriedRose.GhostHero)) continue;
                ((DriedRose.GhostHero)ch).sayBoss();
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("pumpedup", this.pumpedUp);
        bundle.put("healinc", this.healInc);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.pumpedUp = bundle.getInt("pumpedup");
        if (this.state != this.SLEEPING) {
            BossHealthBar.assignBoss(this);
        }
        if (this.HP * 2 <= this.HT) {
            BossHealthBar.bleed(true);
        }
        this.healInc = bundle.getInt("healinc");
    }
}

