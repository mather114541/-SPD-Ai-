/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfTransfusion
extends DamageWand {
    private boolean freeCharge;
    private static final String FREECHARGE = "freecharge";

    public WandOfTransfusion() {
        this.image = ItemSpriteSheet.WAND_TRANSFUSION;
        this.collisionProperties = 7;
        this.freeCharge = false;
    }

    @Override
    public int min(int level) {
        return 3 + level;
    }

    @Override
    public int max(int level) {
        return 6 + 2 * level;
    }

    @Override
    public void onZap(Ballistica beam) {
        for (int c : beam.subPath(0, beam.dist)) {
            CellEmitter.center(c).burst(BloodParticle.BURST, 1);
        }
        int cell = beam.collisionPos;
        Char ch = Actor.findChar(cell);
        if (ch instanceof Mob) {
            this.wandProc(ch, this.chargesPerCast());
            if (ch.alignment == Char.Alignment.ALLY || ch.buff(Charm.class) != null) {
                int selfDmg = Math.round((float)WandOfTransfusion.curUser.HT * 0.05f);
                int healing = selfDmg + 3 * this.buffedLvl();
                int shielding = ch.HP + healing - ch.HT;
                if (shielding > 0) {
                    healing -= shielding;
                    Buff.affect(ch, Barrier.class).setShield(shielding);
                } else {
                    shielding = 0;
                }
                ch.HP += healing;
                ch.sprite.emitter().burst(Speck.factory(0), 2 + this.buffedLvl() / 2);
                if (healing > 0) {
                    ch.sprite.showStatusWithIcon(65280, Integer.toString(healing), FloatingText.HEALING, new Object[0]);
                }
                if (shielding > 0) {
                    ch.sprite.showStatusWithIcon(65280, Integer.toString(shielding), FloatingText.SHIELDING, new Object[0]);
                }
                if (!this.freeCharge) {
                    this.damageHero(selfDmg);
                } else {
                    this.freeCharge = false;
                }
            } else if (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic) {
                Buff.affect(curUser, Barrier.class).setShield(5 + this.buffedLvl());
                WandOfTransfusion.curUser.sprite.showStatusWithIcon(65280, Integer.toString(5 + this.buffedLvl()), FloatingText.SHIELDING, new Object[0]);
                if (!ch.properties().contains((Object)Char.Property.UNDEAD)) {
                    Charm charm = Buff.affect(ch, Charm.class, 5.0f);
                    charm.object = curUser.id();
                    charm.ignoreHeroAllies = true;
                    ch.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 3);
                } else {
                    ch.damage(this.damageRoll(), this);
                    ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + this.buffedLvl());
                    Sample.INSTANCE.play("sounds/burning.mp3");
                }
            }
        }
    }

    private void damageHero(int damage) {
        curUser.damage(damage, this);
        if (!curUser.isAlive()) {
            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        if (defender.buff(Charm.class) != null && defender.buff(Charm.class).object == attacker.id()) {
            this.freeCharge = true;
            int shieldToGive = Math.round((float)(2 * (5 + this.buffedLvl())) * WandOfTransfusion.procChanceMultiplier(attacker));
            Buff.affect(attacker, Barrier.class).setShield(shieldToGive);
            attacker.sprite.showStatusWithIcon(65280, Integer.toString(shieldToGive), FloatingText.SHIELDING, new Object[0]);
            GLog.p(Messages.get(this, "charged", new Object[0]), new Object[0]);
            attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
        }
    }

    @Override
    public void fx(Ballistica beam, Callback callback) {
        WandOfTransfusion.curUser.sprite.parent.add(new Beam.HealthRay(WandOfTransfusion.curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        Sample.INSTANCE.play("sounds/ray.mp3");
        callback.call();
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xCC0000);
        particle.am = 0.6f;
        particle.setLifespan(1.0f);
        particle.speed.polar(Random.Float(6.283185f), 2.0f);
        particle.setSize(1.0f, 2.0f);
        particle.radiateXY(0.5f);
    }

    @Override
    public String statsDesc() {
        int selfDMG;
        int n = selfDMG = Dungeon.hero != null ? Math.round((float)Dungeon.hero.HT * 0.05f) : 1;
        if (this.levelKnown) {
            return Messages.get(this, "stats_desc", selfDMG, selfDMG + 3 * this.buffedLvl(), 5 + this.buffedLvl(), this.min(), this.max());
        }
        return Messages.get(this, "stats_desc", selfDMG, selfDMG, 5, this.min(0), this.max(0));
    }

    @Override
    public String upgradeStat1(int level) {
        int selfDMG = Dungeon.hero != null ? Math.round((float)Dungeon.hero.HT * 0.05f) : 1;
        return Integer.toString(selfDMG + 3 * level);
    }

    @Override
    public String upgradeStat2(int level) {
        return Integer.toString(5 + level);
    }

    @Override
    public String upgradeStat3(int level) {
        return super.upgradeStat1(level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.freeCharge = bundle.getBoolean(FREECHARGE);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FREECHARGE, this.freeCharge);
    }
}

