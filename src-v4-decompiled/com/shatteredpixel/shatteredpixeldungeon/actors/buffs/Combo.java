/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndCombo;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class Combo
extends Buff
implements ActionIndicator.Action {
    private int count;
    private float comboTime;
    private float initialComboTime;
    private static final String COUNT = "count";
    private static final String TIME = "combotime";
    private static final String INITIAL_TIME = "initialComboTime";
    private static final String CLOBBER_USED = "clobber_used";
    private static final String PARRY_USED = "parry_used";
    private boolean clobberUsed;
    private boolean parryUsed;
    private static ComboMove moveBeingUsed;
    private static int furyHitsLeft;
    private CellSelector.Listener listener;

    public Combo() {
        this.type = Buff.buffType.POSITIVE;
        this.count = 0;
        this.comboTime = 0.0f;
        this.initialComboTime = 5.0f;
        this.clobberUsed = false;
        this.parryUsed = false;
        this.listener = new CellSelector.Listener(){

            @Override
            public void onSelect(final Integer cell) {
                if (cell == null) {
                    return;
                }
                final Char enemy = Actor.findChar(cell);
                if (enemy == null || enemy == Combo.this.target || !Dungeon.level.heroFOV[cell] || Combo.this.target.isCharmedBy(enemy)) {
                    GLog.w(Messages.get(Combo.class, "bad_target", new Object[0]), new Object[0]);
                } else if (!((Hero)Combo.this.target).canAttack(enemy)) {
                    if (((Hero)Combo.this.target).pointsInTalent(Talent.ENHANCED_COMBO) < 3 || Dungeon.level.distance(Combo.this.target.pos, enemy.pos) > 1 + Combo.this.target.buff(Combo.class).count / 3) {
                        GLog.w(Messages.get(Combo.class, "bad_target", new Object[0]), new Object[0]);
                    } else {
                        Ballistica c = new Ballistica(Combo.this.target.pos, enemy.pos, 7);
                        if (c.collisionPos == enemy.pos) {
                            final int leapPos = c.path.get(c.dist - 1);
                            if (!(Dungeon.level.passable[leapPos] || Combo.this.target.flying && Dungeon.level.avoid[leapPos])) {
                                GLog.w(Messages.get(Combo.class, "bad_target", new Object[0]), new Object[0]);
                            } else if (Dungeon.hero.rooted) {
                                PixelScene.shake(1.0f, 1.0f);
                                GLog.w(Messages.get(Combo.class, "bad_target", new Object[0]), new Object[0]);
                            } else {
                                Dungeon.hero.busy();
                                Combo.this.target.sprite.jump(Combo.this.target.pos, leapPos, new Callback(){

                                    @Override
                                    public void call() {
                                        Combo.this.target.move(leapPos);
                                        Dungeon.level.occupyCell(Combo.this.target);
                                        Dungeon.observe();
                                        GameScene.updateFog();
                                        Combo.this.target.sprite.attack(cell, new Callback(){

                                            @Override
                                            public void call() {
                                                Combo.this.doAttack(enemy);
                                            }
                                        });
                                    }
                                });
                            }
                        } else {
                            GLog.w(Messages.get(Combo.class, "bad_target", new Object[0]), new Object[0]);
                        }
                    }
                } else {
                    Dungeon.hero.busy();
                    Combo.this.target.sprite.attack(cell, new Callback(){

                        @Override
                        public void call() {
                            Combo.this.doAttack(enemy);
                        }
                    });
                }
            }

            @Override
            public String prompt() {
                return Messages.get(Combo.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    public int icon() {
        return 17;
    }

    @Override
    public void tintIcon(Image icon) {
        ComboMove move = this.getHighestMove();
        if (move != null) {
            icon.hardlight(move.tintColor);
        } else {
            icon.resetColor();
        }
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (this.initialComboTime - this.comboTime) / this.initialComboTime);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.comboTime);
    }

    public void hit(Char enemy) {
        ++this.count;
        if (this.comboTime <= 5.0f) {
            this.comboTime = Math.max(this.comboTime, 5.0f);
            this.initialComboTime = 5.0f;
        }
        if (!enemy.isAlive() || enemy.buff(Corruption.class) != null && enemy.HP == enemy.HT) {
            this.initialComboTime = this.comboTime = 15.0f + 15.0f * (float)((Hero)this.target).pointsInTalent(Talent.CLEAVE);
        }
        if (this.getHighestMove() != null) {
            ActionIndicator.setAction(this);
            Badges.validateMasteryCombo(this.count);
            GLog.p(Messages.get(this, "combo", this.count), new Object[0]);
        }
        BuffIndicator.refreshHero();
    }

    public void addTime(float time) {
        this.comboTime += time;
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    @Override
    public boolean act() {
        this.comboTime -= 1.0f * HoldFast.buffDecayFactor(this.target);
        this.spend(1.0f);
        if (this.comboTime <= 0.0f) {
            this.detach();
        }
        return true;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.count, this.dispTurns(this.comboTime));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COUNT, this.count);
        bundle.put(TIME, this.comboTime);
        bundle.put(INITIAL_TIME, this.initialComboTime);
        bundle.put(CLOBBER_USED, this.clobberUsed);
        bundle.put(PARRY_USED, this.parryUsed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.count = bundle.getInt(COUNT);
        this.comboTime = bundle.getFloat(TIME);
        this.initialComboTime = bundle.getFloat(INITIAL_TIME);
        this.clobberUsed = bundle.getBoolean(CLOBBER_USED);
        this.parryUsed = bundle.getBoolean(PARRY_USED);
        if (this.getHighestMove() != null) {
            ActionIndicator.setAction(this);
        }
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name", new Object[0]);
    }

    @Override
    public int actionIcon() {
        return 105;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(Integer.toString(this.count));
        txt.hardlight(65280);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        ComboMove best = this.getHighestMove();
        if (best == null) {
            return 0xDFDFDF;
        }
        int r = (int)((float)(best.tintColor >> 16) * 0.875f);
        int g = (int)((float)(best.tintColor >> 8 & 0xFF) * 0.875f);
        int b = (int)((float)(best.tintColor & 0xFF) * 0.875f);
        return (r << 16) + (g << 8) + b;
    }

    @Override
    public void doAction() {
        GameScene.show(new WndCombo(this));
    }

    public ComboMove getHighestMove() {
        ComboMove best = null;
        for (ComboMove move : ComboMove.values()) {
            if (this.count < move.comboReq) continue;
            best = move;
        }
        return best;
    }

    public int getComboCount() {
        return this.count;
    }

    public boolean canUseMove(ComboMove move) {
        if (move == ComboMove.CLOBBER && this.clobberUsed) {
            return false;
        }
        if (move == ComboMove.PARRY && this.parryUsed) {
            return false;
        }
        return move.comboReq <= this.count;
    }

    public void useMove(ComboMove move) {
        if (move == ComboMove.PARRY) {
            this.parryUsed = true;
            this.comboTime = Math.max(this.comboTime, 5.0f);
            Invisibility.dispel();
            Buff.affect(this.target, ParryTracker.class, 1.0f);
            ((Hero)this.target).spendAndNext(1.0f);
            Dungeon.hero.busy();
        } else {
            moveBeingUsed = move;
            GameScene.selectCell(this.listener);
        }
    }

    private void doAttack(final Char enemy) {
        AttackIndicator.target(enemy);
        boolean wasAlly = enemy.alignment == this.target.alignment;
        Hero hero = (Hero)this.target;
        float dmgMulti = 1.0f;
        int dmgBonus = 0;
        switch (moveBeingUsed.ordinal()) {
            case 0: {
                dmgMulti = 0.0f;
                break;
            }
            case 1: {
                dmgBonus = Math.round((float)(this.target.drRoll() * this.count) / 5.0f);
                break;
            }
            case 3: {
                dmgMulti = 0.25f * (float)this.count;
                break;
            }
            case 4: {
                dmgMulti = 0.6f;
            }
        }
        int oldPos = enemy.pos;
        if (hero.attack(enemy, dmgMulti, dmgBonus, Char.INFINITE_ACCURACY)) {
            switch (moveBeingUsed.ordinal()) {
                case 0: {
                    int dist;
                    Ballistica trajectory = new Ballistica(this.target.pos, enemy.pos, 1);
                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), 7);
                    if (enemy.isAlive() && this.count >= 7 && hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 1) {
                        ++dist;
                        Buff.prolong(enemy, Vertigo.class, 3.0f);
                    } else if (!enemy.flying) {
                        for (dist = 2; dist > trajectory.dist || dist > 0 && Dungeon.level.pit[trajectory.path.get(dist)]; --dist) {
                        }
                    }
                    if (!wasAlly) {
                        this.hit(enemy);
                    }
                    if (enemy.pos != oldPos) break;
                    WandOfBlastWave.throwChar(enemy, trajectory, dist, true, false, hero);
                    break;
                }
                case 2: {
                    this.hit(enemy);
                    break;
                }
                case 3: {
                    WandOfBlastWave.BlastWave.blast(enemy.pos);
                    PathFinder.buildDistanceMap(this.target.pos, BArray.not(Dungeon.level.solid, null), 3);
                    for (Char ch : Actor.chars()) {
                        if (ch == enemy || ch.alignment != Char.Alignment.ENEMY || PathFinder.distance[ch.pos] >= Integer.MAX_VALUE) continue;
                        int aoeHit = Math.round((float)this.target.damageRoll() * 0.25f * (float)this.count);
                        aoeHit /= 2;
                        aoeHit -= ch.drRoll();
                        if (ch.buff(Vulnerable.class) != null) {
                            aoeHit = (int)((float)aoeHit * 1.33f);
                        }
                        if (ch instanceof DwarfKing) {
                            ch.damage(aoeHit, this);
                        } else {
                            ch.damage(aoeHit, this.target);
                        }
                        ch.sprite.bloodBurstA(this.target.sprite.center(), aoeHit);
                        ch.sprite.flash();
                        if (ch.isAlive() || !hero.hasTalent(Talent.LETHAL_DEFENSE)) continue;
                        Buff.affect(hero, BrokenSeal.WarriorShield.class).reduceCooldown((float)hero.pointsInTalent(Talent.LETHAL_DEFENSE) / 3.0f);
                    }
                    break;
                }
            }
        }
        Invisibility.dispel();
        switch (moveBeingUsed.ordinal()) {
            case 0: {
                this.clobberUsed = true;
                if (this.getHighestMove() == null) {
                    ActionIndicator.clearAction(this);
                }
                hero.spendAndNext(hero.attackDelay());
                break;
            }
            case 2: {
                break;
            }
            case 4: {
                if (this.count > 0) {
                    furyHitsLeft = this.count;
                    this.count = 0;
                    this.detach();
                    hero.spend(hero.attackDelay());
                }
                if (--furyHitsLeft > 0 && enemy.isAlive() && hero.canAttack(enemy) && hero.paralysed == 0 && (wasAlly || enemy.alignment != this.target.alignment)) {
                    this.target.sprite.attack(enemy.pos, new Callback(){

                        @Override
                        public void call() {
                            Combo.this.doAttack(enemy);
                        }
                    });
                    break;
                }
                furyHitsLeft = 0;
                Sample.INSTANCE.play("sounds/hit_strong.mp3");
                ActionIndicator.clearAction(this);
                hero.next();
                break;
            }
            default: {
                this.detach();
                ActionIndicator.clearAction(this);
                hero.spendAndNext(hero.attackDelay());
            }
        }
        if ((!enemy.isAlive() || !wasAlly && enemy.alignment == this.target.alignment) && hero.hasTalent(Talent.LETHAL_DEFENSE)) {
            Buff.affect(hero, BrokenSeal.WarriorShield.class).reduceCooldown((float)hero.pointsInTalent(Talent.LETHAL_DEFENSE) / 3.0f);
        }
    }

    static {
        furyHitsLeft = 0;
    }

    public static enum ComboMove {
        CLOBBER(2, 65280),
        SLAM(4, 0xCCFF00),
        PARRY(6, 0xFFFF00),
        CRUSH(8, 0xFFCC00),
        FURY(10, 0xFF0000);

        public int comboReq;
        public int tintColor;

        private ComboMove(int comboReq, int tintColor) {
            this.comboReq = comboReq;
            this.tintColor = tintColor;
        }

        public String title() {
            return Messages.get((Object)this, this.name() + ".name", new Object[0]);
        }

        public String desc(int count) {
            switch (this.ordinal()) {
                default: {
                    if (count >= 7 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 1) {
                        return Messages.get((Object)this, this.name() + ".empower_desc", new Object[0]);
                    }
                    return Messages.get((Object)this, this.name() + ".desc", new Object[0]);
                }
                case 1: {
                    if (count >= 3 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 3) {
                        return Messages.get((Object)this, this.name() + ".empower_desc", count / 3, count * 20);
                    }
                    return Messages.get((Object)this, this.name() + ".desc", count * 20);
                }
                case 2: {
                    if (count >= 9 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 2) {
                        return Messages.get((Object)this, this.name() + ".empower_desc", new Object[0]);
                    }
                    return Messages.get((Object)this, this.name() + ".desc", new Object[0]);
                }
                case 3: {
                    if (count >= 3 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 3) {
                        return Messages.get((Object)this, this.name() + ".empower_desc", count / 3, count * 25);
                    }
                    return Messages.get((Object)this, this.name() + ".desc", count * 25);
                }
                case 4: 
            }
            if (count >= 3 && Dungeon.hero.pointsInTalent(Talent.ENHANCED_COMBO) >= 3) {
                return Messages.get((Object)this, this.name() + ".empower_desc", count / 3);
            }
            return Messages.get((Object)this, this.name() + ".desc", new Object[0]);
        }
    }

    public static class ParryTracker
    extends FlavourBuff {
        public boolean parried;

        public ParryTracker() {
            this.actPriority = 1;
        }

        @Override
        public void detach() {
            if (!this.parried && this.target.buff(Combo.class) != null) {
                this.target.buff(Combo.class).detach();
            }
            super.detach();
        }
    }

    public static class RiposteTracker
    extends Buff {
        public Char enemy;

        public RiposteTracker() {
            this.actPriority = 100;
        }

        @Override
        public boolean act() {
            if (this.target.buff(Combo.class) != null) {
                moveBeingUsed = ComboMove.PARRY;
                this.target.sprite.attack(this.enemy.pos, new Callback(){

                    @Override
                    public void call() {
                        target.buff(Combo.class).doAttack(enemy);
                        this.next();
                    }
                });
                this.detach();
                return false;
            }
            this.detach();
            return true;
        }
    }
}

