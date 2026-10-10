/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.watabou.utils.Bundle;

public class DirectableAlly
extends NPC {
    protected boolean attacksAutomatically;
    protected int defendingPos;
    protected boolean movingToDefendPos;
    private static final String DEFEND_POS = "defend_pos";
    private static final String MOVING_TO_DEFEND = "moving_to_defend";

    public DirectableAlly() {
        this.alignment = Char.Alignment.ALLY;
        this.intelligentAlly = true;
        this.WANDERING = new Wandering();
        this.HUNTING = new Hunting();
        this.state = this.WANDERING;
        this.actPriority = -19;
        this.attacksAutomatically = true;
        this.defendingPos = -1;
        this.movingToDefendPos = false;
    }

    public void defendPos(int cell) {
        this.defendingPos = cell;
        this.movingToDefendPos = true;
        this.aggro(null);
        this.state = this.WANDERING;
    }

    public void clearDefensingPos() {
        this.defendingPos = -1;
        this.movingToDefendPos = false;
    }

    public void followHero() {
        this.defendingPos = -1;
        this.movingToDefendPos = false;
        this.aggro(null);
        this.state = this.WANDERING;
    }

    public void targetChar(Char ch) {
        this.defendingPos = -1;
        this.movingToDefendPos = false;
        this.aggro(ch);
        this.target = ch.pos;
    }

    @Override
    public void aggro(Char ch) {
        this.enemy = ch;
        if (!this.movingToDefendPos && this.state != this.PASSIVE) {
            this.state = this.HUNTING;
        }
    }

    public void directTocell(int cell) {
        if (!Dungeon.level.heroFOV[cell] || Actor.findChar(cell) == null || Actor.findChar(cell) != Dungeon.hero && Actor.findChar((int)cell).alignment != Char.Alignment.ENEMY) {
            this.defendPos(cell);
            return;
        }
        if (Actor.findChar(cell) == Dungeon.hero) {
            this.followHero();
        } else if (Actor.findChar((int)cell).alignment == Char.Alignment.ENEMY) {
            this.targetChar(Actor.findChar(cell));
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEFEND_POS, this.defendingPos);
        bundle.put(MOVING_TO_DEFEND, this.movingToDefendPos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(DEFEND_POS)) {
            this.defendingPos = bundle.getInt(DEFEND_POS);
        }
        this.movingToDefendPos = bundle.getBoolean(MOVING_TO_DEFEND);
    }

    private class Wandering
    extends Mob.Wandering {
        private Wandering() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && DirectableAlly.this.attacksAutomatically && !DirectableAlly.this.movingToDefendPos && (DirectableAlly.this.defendingPos == -1 || !Dungeon.level.heroFOV[DirectableAlly.this.defendingPos] || DirectableAlly.this.canAttack(DirectableAlly.this.enemy))) {
                DirectableAlly.this.enemySeen = true;
                DirectableAlly.this.notice();
                DirectableAlly.this.alerted = true;
                DirectableAlly.this.state = DirectableAlly.this.HUNTING;
                DirectableAlly.this.target = ((DirectableAlly)DirectableAlly.this).enemy.pos;
            } else {
                DirectableAlly.this.enemySeen = false;
                int oldPos = DirectableAlly.this.pos;
                DirectableAlly.this.target = DirectableAlly.this.defendingPos != -1 ? DirectableAlly.this.defendingPos : Dungeon.hero.pos;
                if (DirectableAlly.this.getCloser(DirectableAlly.this.target)) {
                    DirectableAlly.this.spend(1.0f / DirectableAlly.this.speed());
                    if (DirectableAlly.this.pos == DirectableAlly.this.defendingPos) {
                        DirectableAlly.this.movingToDefendPos = false;
                    }
                    return DirectableAlly.this.moveSprite(oldPos, DirectableAlly.this.pos);
                }
                if (DirectableAlly.this.movingToDefendPos) {
                    DirectableAlly.this.defendingPos = DirectableAlly.this.pos;
                    DirectableAlly.this.movingToDefendPos = false;
                }
                DirectableAlly.this.spend(1.0f);
            }
            return true;
        }
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && DirectableAlly.this.defendingPos != -1 && Dungeon.level.heroFOV[DirectableAlly.this.defendingPos] && !DirectableAlly.this.canAttack(DirectableAlly.this.enemy)) {
                DirectableAlly.this.target = DirectableAlly.this.defendingPos;
                DirectableAlly.this.state = DirectableAlly.this.WANDERING;
                return true;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }
}

