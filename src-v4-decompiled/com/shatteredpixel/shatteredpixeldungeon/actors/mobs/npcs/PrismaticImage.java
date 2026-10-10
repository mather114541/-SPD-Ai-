/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PrismaticGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PrismaticSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class PrismaticImage
extends NPC {
    private Hero hero;
    private int heroID;
    public int armTier;
    private int deathTimer;
    private static final String HEROID = "hero_id";
    private static final String TIMER = "timer";

    public PrismaticImage() {
        this.spriteClass = PrismaticSprite.class;
        this.HT = 10;
        this.HP = 10;
        this.defenseSkill = 1;
        this.alignment = Char.Alignment.ALLY;
        this.intelligentAlly = true;
        this.state = this.HUNTING;
        this.WANDERING = new Wandering();
        this.actPriority = -19;
        this.deathTimer = -1;
        this.immunities.add(ToxicGas.class);
        this.immunities.add(CorrosiveGas.class);
        this.immunities.add(Burning.class);
        this.immunities.add(AllyBuff.class);
    }

    @Override
    protected boolean act() {
        if (!this.isAlive()) {
            --this.deathTimer;
            if (this.deathTimer > 0) {
                this.sprite.alpha((float)(this.deathTimer + 3) / 8.0f);
                this.spend(1.0f);
            } else {
                this.destroy();
                this.sprite.die();
            }
            return true;
        }
        if (this.deathTimer != -1) {
            if (this.paralysed == 0) {
                this.sprite.remove(CharSprite.State.PARALYSED);
            }
            this.deathTimer = -1;
            this.sprite.resetColor();
        }
        if (this.hero == null) {
            this.hero = (Hero)Actor.findById(this.heroID);
            if (this.hero == null) {
                this.destroy();
                this.sprite.die();
                return true;
            }
        }
        if (this.hero.tier() != this.armTier) {
            this.armTier = this.hero.tier();
            ((PrismaticSprite)this.sprite).updateArmor(this.armTier);
        }
        return super.act();
    }

    @Override
    public void die(Object cause) {
        if (this.deathTimer == -1) {
            if (cause == Chasm.class) {
                super.die(cause);
            } else {
                this.deathTimer = 5;
                this.sprite.add(CharSprite.State.PARALYSED);
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.isAlive() || this.deathTimer > 0;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEROID, this.heroID);
        bundle.put(TIMER, this.deathTimer);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.heroID = bundle.getInt(HEROID);
        this.deathTimer = bundle.getInt(TIMER);
    }

    public void duplicate(Hero hero, int HP) {
        this.hero = hero;
        this.heroID = this.hero.id();
        this.HP = HP;
        this.HT = PrismaticGuard.maxHP(hero);
    }

    @Override
    public int damageRoll() {
        if (this.hero != null) {
            return Random.NormalIntRange(2 + this.hero.lvl / 4, 4 + this.hero.lvl / 2);
        }
        return Random.NormalIntRange(2, 4);
    }

    @Override
    public int attackSkill(Char target) {
        if (this.hero != null) {
            return (int)((float)(9 + this.hero.lvl) * RingOfAccuracy.accuracyMultiplier(this.hero));
        }
        return 0;
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (this.hero != null) {
            int baseEvasion = 4 + this.hero.lvl;
            int heroEvasion = (int)((float)(4 + this.hero.lvl) * RingOfEvasion.evasionMultiplier(this.hero));
            if (this.hero.belongings.armor() != null) {
                heroEvasion = (int)this.hero.belongings.armor().evasionFactor(this, heroEvasion);
            }
            return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
        }
        return 0;
    }

    @Override
    public int drRoll() {
        int dr = super.drRoll();
        if (this.hero != null) {
            return dr + this.hero.drRoll();
        }
        return dr;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (this.hero != null && this.hero.belongings.armor() != null) {
            damage = this.hero.belongings.armor().proc(enemy, this, damage);
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public int glyphLevel(Class<? extends Armor.Glyph> cls) {
        if (this.hero != null) {
            return Math.max(super.glyphLevel(cls), this.hero.glyphLevel(cls));
        }
        return super.glyphLevel(cls);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (enemy instanceof Mob) {
            ((Mob)enemy).aggro(this);
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public CharSprite sprite() {
        CharSprite s = super.sprite();
        this.hero = (Hero)Actor.findById(this.heroID);
        this.armTier = this.hero != null ? this.hero.tier() : 1;
        ((PrismaticSprite)s).updateArmor(this.armTier);
        return s;
    }

    private class Wandering
    extends Mob.Wandering {
        private Wandering() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV) {
                Buff.affect(PrismaticImage.this.hero, PrismaticGuard.class).set(PrismaticImage.this);
                PrismaticImage.this.destroy();
                CellEmitter.get(PrismaticImage.this.pos).start(Speck.factory(2), 0.2f, 3);
                PrismaticImage.this.sprite.die();
                Sample.INSTANCE.play("sounds/teleport.mp3");
                return true;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }
}

