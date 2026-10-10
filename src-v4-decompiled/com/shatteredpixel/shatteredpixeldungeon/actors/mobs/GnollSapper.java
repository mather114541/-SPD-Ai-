/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSapperSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GnollSapper
extends Mob {
    public int spawnPos;
    private int partnerID;
    private int abilityCooldown;
    private boolean lastAbilityWasRockfall;
    public int throwingRockFromPos;
    public int throwingRockToPos;
    private static final String SPAWN_POS = "spawn_pos";
    private static final String PARTNER_ID = "partner_id";
    private static final String ABILITY_COOLDOWN = "ability_cooldown";
    private static final String LAST_ABILITY_WAS_ROCKFALL = "last_ability_was_rockfall";
    private static final String ROCK_FROM_POS = "rock_from_pos";
    private static final String ROCK_TO_POS = "rock_to_pos";

    public GnollSapper() {
        this.actPriority = -21;
        this.spriteClass = GnollSapperSprite.class;
        this.HT = 45;
        this.HP = 45;
        this.defenseSkill = 15;
        this.EXP = 10;
        this.maxLvl = -2;
        this.properties.add(Char.Property.MINIBOSS);
        this.HUNTING = new Hunting();
        this.WANDERING = new Wandering();
        this.state = this.SLEEPING;
        this.partnerID = -1;
        this.abilityCooldown = Random.NormalIntRange(4, 6);
        this.lastAbilityWasRockfall = false;
        this.throwingRockFromPos = -1;
        this.throwingRockToPos = -1;
    }

    public void linkPartner(Char c) {
        this.losePartner();
        this.partnerID = c.id();
        if (c instanceof GnollGuard) {
            ((GnollGuard)c).linkSapper(this);
        } else if (c instanceof GnollGeomancer) {
            ((GnollGeomancer)c).linkSapper(this);
        }
    }

    public void losePartner() {
        if (this.partnerID != -1) {
            if (Actor.findById(this.partnerID) instanceof GnollGuard) {
                ((GnollGuard)Actor.findById(this.partnerID)).loseSapper();
            } else if (Actor.findById(this.partnerID) instanceof GnollGeomancer) {
                ((GnollGeomancer)Actor.findById(this.partnerID)).loseSapper();
            }
            this.partnerID = -1;
        }
    }

    public Actor getPartner() {
        return Actor.findById(this.partnerID);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        this.losePartner();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 6);
    }

    @Override
    public int attackSkill(Char target) {
        return 18;
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);
        this.abilityCooldown = (int)((float)this.abilityCooldown - (float)dmg / 10.0f);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 6);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    protected boolean act() {
        if (this.throwingRockFromPos != -1) {
            boolean attacked;
            boolean bl = attacked = Dungeon.level.map[this.throwingRockFromPos] == 36;
            if (attacked) {
                GnollGeomancer.doRockThrowAttack(this, this.throwingRockFromPos, this.throwingRockToPos);
            }
            this.throwingRockFromPos = -1;
            this.throwingRockToPos = -1;
            this.spend(1.0f);
            return !attacked;
        }
        return super.act();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PARTNER_ID, this.partnerID);
        bundle.put(SPAWN_POS, this.spawnPos);
        bundle.put(ABILITY_COOLDOWN, this.abilityCooldown);
        bundle.put(LAST_ABILITY_WAS_ROCKFALL, this.lastAbilityWasRockfall);
        bundle.put(ROCK_FROM_POS, this.throwingRockFromPos);
        bundle.put(ROCK_TO_POS, this.throwingRockToPos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.partnerID = bundle.getInt(PARTNER_ID);
        this.spawnPos = bundle.getInt(SPAWN_POS);
        this.abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);
        this.lastAbilityWasRockfall = bundle.getBoolean(LAST_ABILITY_WAS_ROCKFALL);
        this.throwingRockFromPos = bundle.getInt(ROCK_FROM_POS);
        this.throwingRockToPos = bundle.getInt(ROCK_TO_POS);
    }

    public class Hunting
    extends Mob.Hunting {
        public Hunting() {
            super(GnollSapper.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV) {
                if (Dungeon.level.distance(GnollSapper.this.spawnPos, GnollSapper.this.target) > 3) {
                    GnollSapper.this.target = GnollSapper.this.pos;
                }
                return super.act(enemyInFOV, justAlerted);
            }
            GnollSapper.this.enemySeen = true;
            if (GnollSapper.this.getPartner() != null && GnollSapper.this.getPartner() instanceof Mob && ((Mob)GnollSapper.this.getPartner()).alignment != GnollSapper.this.alignment) {
                GnollSapper.this.losePartner();
            }
            if (Actor.findById(GnollSapper.this.partnerID) != null && Dungeon.level.distance(GnollSapper.this.pos, GnollSapper.this.enemy.pos) <= 3) {
                Mob partner = (Mob)Actor.findById(GnollSapper.this.partnerID);
                if (partner.state == partner.SLEEPING) {
                    partner.notice();
                }
                if (GnollSapper.this.enemy != partner) {
                    partner.target = GnollSapper.this.enemy.pos;
                    partner.aggro(GnollSapper.this.enemy);
                }
            }
            if (GnollSapper.this.abilityCooldown-- <= 0) {
                Ballistica aim;
                boolean targetNextToBarricade = false;
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (Dungeon.level.map[GnollSapper.this.enemy.pos + i] != 13 && Dungeon.level.map[GnollSapper.this.enemy.pos + i] != 7) continue;
                    targetNextToBarricade = true;
                    break;
                }
                if ((aim = GnollGeomancer.prepRockThrowAttack(GnollSapper.this.enemy, GnollSapper.this)) != null && (targetNextToBarricade || GnollSapper.this.lastAbilityWasRockfall || Random.Int(2) == 0)) {
                    GnollSapper.this.lastAbilityWasRockfall = false;
                    GnollSapper.this.throwingRockFromPos = aim.sourcePos;
                    GnollSapper.this.throwingRockToPos = aim.collisionPos;
                    float delay = GameMath.gate(1.0f, (int)Math.ceil(GnollSapper.this.enemy.cooldown()), 3.0f);
                    Ballistica warnPath = new Ballistica(aim.sourcePos, aim.collisionPos, 4);
                    for (int i : warnPath.subPath(0, warnPath.dist)) {
                        GameScene.targetedCell(i, delay);
                    }
                    Dungeon.hero.interrupt();
                    GnollSapper.this.abilityCooldown = Random.NormalIntRange(4, 6);
                    GnollSapper.this.spend(delay);
                    return true;
                }
                if (GnollGeomancer.prepRockFallAttack(GnollSapper.this.enemy, GnollSapper.this, 2, true)) {
                    GnollSapper.this.lastAbilityWasRockfall = true;
                    Dungeon.hero.interrupt();
                    GnollSapper.this.spend(GameMath.gate(1.0f, (int)Math.ceil(GnollSapper.this.enemy.cooldown()), 3.0f));
                    GnollSapper.this.abilityCooldown = Random.NormalIntRange(4, 6);
                    return true;
                }
            }
            if (GnollSapper.this.canAttack(GnollSapper.this.enemy)) {
                return super.act(enemyInFOV, justAlerted);
            }
            GnollSapper.this.spend(1.0f);
            return true;
        }
    }

    public class Wandering
    extends Mob.Wandering {
        public Wandering() {
            super(GnollSapper.this);
        }

        @Override
        protected int randomDestination() {
            return GnollSapper.this.spawnPos;
        }
    }
}

