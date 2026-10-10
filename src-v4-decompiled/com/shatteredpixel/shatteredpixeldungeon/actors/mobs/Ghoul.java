/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhoulSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Ghoul
extends Mob {
    private int timesDowned;
    protected int partnerID;
    private static final String PARTNER_ID = "partner_id";
    private static final String TIMES_DOWNED = "times_downed";
    private boolean beingLifeLinked;

    public Ghoul() {
        this.spriteClass = GhoulSprite.class;
        this.HT = 45;
        this.HP = 45;
        this.defenseSkill = 20;
        this.EXP = 5;
        this.maxLvl = 20;
        this.SLEEPING = new Sleeping();
        this.WANDERING = new Wandering();
        this.state = this.SLEEPING;
        this.loot = Gold.class;
        this.lootChance = 0.2f;
        this.properties.add(Char.Property.UNDEAD);
        this.timesDowned = 0;
        this.partnerID = -1;
        this.beingLifeLinked = false;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(16, 22);
    }

    @Override
    public int attackSkill(Char target) {
        return 24;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 4);
    }

    @Override
    public float spawningWeight() {
        return 0.5f;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PARTNER_ID, this.partnerID);
        bundle.put(TIMES_DOWNED, this.timesDowned);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.partnerID = bundle.getInt(PARTNER_ID);
        this.timesDowned = bundle.getInt(TIMES_DOWNED);
    }

    @Override
    protected boolean act() {
        if (this.partnerID == -1) {
            int[] neighbours;
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int n : neighbours = new int[]{this.pos + 1, this.pos - 1, this.pos + Dungeon.level.width(), this.pos - Dungeon.level.width()}) {
                if (!Dungeon.level.passable[n] || Actor.findChar(n) != null || Char.hasProp(this, Char.Property.LARGE) && !Dungeon.level.openSpace[n]) continue;
                candidates.add(n);
            }
            if (!candidates.isEmpty()) {
                Ghoul child = new Ghoul();
                child.partnerID = this.id();
                this.partnerID = child.id();
                if (this.state != this.SLEEPING) {
                    child.state = child.WANDERING;
                }
                child.pos = (Integer)Random.element(candidates);
                GameScene.add(child);
                Dungeon.level.occupyCell(child);
                if (this.sprite.visible) {
                    Actor.add(new Pushing(child, this.pos, child.pos));
                }
                for (Buff b : this.buffs()) {
                    if (!b.revivePersists) continue;
                    Buff.affect(child, b.getClass());
                }
            }
        }
        return super.act();
    }

    @Override
    public void die(Object cause) {
        Ghoul nearby;
        if (cause != Chasm.class && cause != GhoulLifeLink.class && !Dungeon.level.pit[this.pos] && (nearby = GhoulLifeLink.searchForHost(this)) != null) {
            this.beingLifeLinked = true;
            ++this.timesDowned;
            Actor.remove(this);
            Dungeon.level.mobs.remove(this);
            Buff.append(nearby, GhoulLifeLink.class).set(this.timesDowned * 5, this);
            ((GhoulSprite)this.sprite).crumple();
            return;
        }
        super.die(cause);
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() || this.beingLifeLinked;
    }

    @Override
    public boolean isActive() {
        return !this.beingLifeLinked && this.isAlive();
    }

    @Override
    protected synchronized void onRemove() {
        if (this.beingLifeLinked) {
            for (Buff buff : this.buffs()) {
                if (buff instanceof SacrificialFire.Marked) {
                    Buff.prolong(this, SacrificialFire.Marked.class, this.timesDowned * 5);
                    continue;
                }
                if (buff.revivePersists) continue;
                buff.detach();
            }
        } else {
            super.onRemove();
        }
    }

    private class Sleeping
    extends Mob.Sleeping {
        private Sleeping() {
            super(Ghoul.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Ghoul partner = (Ghoul)Actor.findById(Ghoul.this.partnerID);
            if (partner != null && partner.state != partner.SLEEPING) {
                Ghoul.this.state = Ghoul.this.WANDERING;
                Ghoul.this.target = partner.pos;
                return true;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }

    private class Wandering
    extends Mob.Wandering {
        private Wandering() {
            super(Ghoul.this);
        }

        @Override
        protected boolean continueWandering() {
            Ghoul.this.enemySeen = false;
            Ghoul partner = (Ghoul)Actor.findById(Ghoul.this.partnerID);
            if (partner != null && (partner.state != partner.WANDERING || Dungeon.level.distance(Ghoul.this.pos, partner.target) > 1)) {
                Ghoul.this.target = partner.pos;
                int oldPos = Ghoul.this.pos;
                if (Ghoul.this.getCloser(Ghoul.this.target)) {
                    Ghoul.this.spend(1.0f / Ghoul.this.speed());
                    return Ghoul.this.moveSprite(oldPos, Ghoul.this.pos);
                }
                Ghoul.this.spend(1.0f);
                return true;
            }
            return super.continueWandering();
        }
    }

    public static class GhoulLifeLink
    extends Buff {
        private Ghoul ghoul;
        private int turnsToRevive;
        private static final String GHOUL = "ghoul";
        private static final String LEFT = "left";

        @Override
        public boolean act() {
            if (this.target.alignment != this.ghoul.alignment) {
                this.detach();
                return true;
            }
            if (this.target.fieldOfView == null) {
                this.target.fieldOfView = new boolean[Dungeon.level.length()];
                Dungeon.level.updateFieldOfView(this.target, this.target.fieldOfView);
            }
            if (!this.target.fieldOfView[this.ghoul.pos] && Dungeon.level.distance(this.ghoul.pos, this.target.pos) >= 4) {
                this.detach();
                return true;
            }
            if (Dungeon.level.pit[this.ghoul.pos]) {
                super.detach();
                this.ghoul.beingLifeLinked = false;
                this.ghoul.die(this);
                return true;
            }
            if (this.target.buff(Challenge.DuelParticipant.class) == null) {
                --this.turnsToRevive;
            }
            if (this.turnsToRevive <= 0) {
                if (Actor.findChar(this.ghoul.pos) != null) {
                    ArrayList<Integer> candidates = new ArrayList<Integer>();
                    for (int n : PathFinder.NEIGHBOURS8) {
                        int cell = this.ghoul.pos + n;
                        if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null || Char.hasProp(this.ghoul, Char.Property.LARGE) && !Dungeon.level.openSpace[cell]) continue;
                        candidates.add(cell);
                    }
                    if (candidates.size() > 0) {
                        int newPos = (Integer)Random.element(candidates);
                        Actor.add(new Pushing(this.ghoul, this.ghoul.pos, newPos));
                        this.ghoul.pos = newPos;
                    } else {
                        this.spend(1.0f);
                        return true;
                    }
                }
                this.ghoul.HP = Math.round((float)this.ghoul.HT / 10.0f);
                this.ghoul.beingLifeLinked = false;
                Actor.add(this.ghoul);
                this.ghoul.timeToNow();
                Dungeon.level.mobs.add(this.ghoul);
                Dungeon.level.occupyCell(this.ghoul);
                this.ghoul.sprite.idle();
                this.ghoul.sprite.showStatusWithIcon(65280, Integer.toString(Math.round((float)this.ghoul.HT / 10.0f)), FloatingText.HEALING, new Object[0]);
                if (this.ghoul.enemy != null && this.ghoul.enemy.alignment == this.ghoul.alignment) {
                    this.ghoul.enemy = null;
                }
                super.detach();
                return true;
            }
            this.spend(1.0f);
            return true;
        }

        public void updateVisibility() {
            if (this.ghoul != null && this.ghoul.sprite != null) {
                this.ghoul.sprite.visible = Dungeon.level.heroFOV[this.ghoul.pos];
            }
        }

        public void set(int turns, Ghoul ghoul) {
            this.ghoul = ghoul;
            this.turnsToRevive = turns;
        }

        @Override
        public void fx(boolean on) {
            if (on && this.ghoul != null && this.ghoul.sprite == null) {
                GameScene.addSprite(this.ghoul);
                ((GhoulSprite)this.ghoul.sprite).crumple();
            }
        }

        @Override
        public void detach() {
            super.detach();
            Ghoul newHost = GhoulLifeLink.searchForHost(this.ghoul);
            if (newHost != null) {
                this.attachTo(newHost);
                this.timeToNow();
            } else {
                this.ghoul.beingLifeLinked = false;
                this.ghoul.die(this);
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(GHOUL, this.ghoul);
            bundle.put(LEFT, this.turnsToRevive);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.ghoul = (Ghoul)bundle.get(GHOUL);
            this.ghoul.beingLifeLinked = true;
            this.turnsToRevive = bundle.getInt(LEFT);
        }

        public static Ghoul searchForHost(Ghoul dieing) {
            for (Char ch : Actor.chars()) {
                if (ch == dieing || !(ch instanceof Ghoul) || ch.alignment != dieing.alignment || ch.buff(Challenge.SpectatorFreeze.class) != null) continue;
                if (ch.fieldOfView == null) {
                    ch.fieldOfView = new boolean[Dungeon.level.length()];
                    Dungeon.level.updateFieldOfView(ch, ch.fieldOfView);
                }
                if (!ch.fieldOfView[dieing.pos] && Dungeon.level.distance(ch.pos, dieing.pos) >= 4) continue;
                return (Ghoul)ch;
            }
            return null;
        }
    }
}

