/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Preparation
extends Buff
implements ActionIndicator.Action {
    private int turnsInvis;
    private static final String TURNS = "turnsInvis";
    private CellSelector.Listener attack;

    public Preparation() {
        this.actPriority = -31;
        this.type = Buff.buffType.POSITIVE;
        this.turnsInvis = 0;
        this.attack = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer cell) {
                if (cell == null) {
                    return;
                }
                Char enemy = Actor.findChar(cell);
                if (enemy == null || Dungeon.hero.isCharmedBy(enemy) || enemy instanceof NPC || !Dungeon.level.heroFOV[cell] || enemy == Dungeon.hero) {
                    GLog.w(Messages.get(Preparation.class, "no_target", new Object[0]), new Object[0]);
                } else {
                    if (Dungeon.hero.canAttack(enemy)) {
                        Dungeon.hero.curAction = new HeroAction.Attack(enemy);
                        Dungeon.hero.next();
                        return;
                    }
                    AttackLevel lvl = AttackLevel.getLvl(Preparation.this.turnsInvis);
                    PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), lvl.blinkDistance());
                    int dest = -1;
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (Actor.findChar(cell + i) != null || !Dungeon.level.passable[cell + i] && (!Preparation.this.target.flying || !Dungeon.level.avoid[cell + i])) continue;
                        if (dest == -1 || PathFinder.distance[dest] > PathFinder.distance[cell + i]) {
                            dest = cell + i;
                            continue;
                        }
                        if (PathFinder.distance[dest] != PathFinder.distance[cell + i] || !(Dungeon.level.trueDistance(Dungeon.hero.pos, dest) > Dungeon.level.trueDistance(Dungeon.hero.pos, cell + i))) continue;
                        dest = cell + i;
                    }
                    if (dest == -1 || PathFinder.distance[dest] == Integer.MAX_VALUE || Dungeon.hero.rooted) {
                        GLog.w(Messages.get(Preparation.class, "out_of_reach", new Object[0]), new Object[0]);
                        if (Dungeon.hero.rooted) {
                            PixelScene.shake(1.0f, 1.0f);
                        }
                        return;
                    }
                    Dungeon.hero.pos = dest;
                    Dungeon.level.occupyCell(Dungeon.hero);
                    Dungeon.observe();
                    GameScene.updateFog();
                    Dungeon.hero.checkVisibleMobs();
                    Dungeon.hero.sprite.place(Dungeon.hero.pos);
                    Dungeon.hero.sprite.turnTo(Dungeon.hero.pos, cell);
                    CellEmitter.get(Dungeon.hero.pos).burst(Speck.factory(7), 6);
                    Sample.INSTANCE.play("sounds/puff.mp3");
                    Dungeon.hero.curAction = new HeroAction.Attack(enemy);
                    Dungeon.hero.next();
                }
            }

            @Override
            public String prompt() {
                return Messages.get(Preparation.class, "prompt", AttackLevel.getLvl(Preparation.this.turnsInvis).blinkDistance());
            }
        };
    }

    @Override
    public boolean act() {
        if (this.target.invisible > 0) {
            ++this.turnsInvis;
            if (AttackLevel.getLvl(this.turnsInvis).blinkDistance() > 0 && this.target == Dungeon.hero) {
                ActionIndicator.setAction(this);
            }
            this.spend(1.0f);
        } else {
            this.detach();
        }
        return true;
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    public int attackLevel() {
        return AttackLevel.getLvl(this.turnsInvis).ordinal() + 1;
    }

    public int damageRoll(Char attacker) {
        return AttackLevel.getLvl(this.turnsInvis).damageRoll(attacker);
    }

    public boolean canKO(Char defender) {
        return !defender.isInvulnerable(this.target.getClass()) && AttackLevel.getLvl(this.turnsInvis).canKO(defender);
    }

    @Override
    public int icon() {
        return 42;
    }

    @Override
    public void tintIcon(Image icon) {
        switch (AttackLevel.getLvl(this.turnsInvis).ordinal()) {
            case 0: {
                icon.hardlight(0.0f, 1.0f, 0.0f);
                break;
            }
            case 1: {
                icon.hardlight(1.0f, 1.0f, 0.0f);
                break;
            }
            case 2: {
                icon.hardlight(1.0f, 0.6f, 0.0f);
                break;
            }
            case 3: {
                icon.hardlight(1.0f, 0.0f, 0.0f);
            }
        }
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        AttackLevel lvl = AttackLevel.getLvl(this.turnsInvis);
        desc = (String)desc + "\n\n" + Messages.get(this, "desc_dmg", (int)(lvl.baseDmgBonus * 100.0f), (int)(lvl.KOThreshold() * 100.0f), (int)(lvl.KOThreshold() * 20.0f));
        if (lvl.damageRolls > 1) {
            desc = (String)desc + " " + Messages.get(this, "desc_dmg_likely", new Object[0]);
        }
        if (lvl.blinkDistance() > 0) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_blink", lvl.blinkDistance());
        }
        desc = (String)desc + "\n\n" + Messages.get(this, "desc_invis_time", this.turnsInvis);
        if (lvl.ordinal() != AttackLevel.values().length - 1) {
            AttackLevel next = AttackLevel.values()[lvl.ordinal() + 1];
            desc = (String)desc + "\n" + Messages.get(this, "desc_invis_next", next.turnsReq);
        }
        return desc;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.turnsInvis = bundle.getInt(TURNS);
        ActionIndicator.setAction(this);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TURNS, this.turnsInvis);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name", new Object[0]);
    }

    @Override
    public int actionIcon() {
        return 106;
    }

    @Override
    public Visual primaryVisual() {
        HeroIcon actionIco = new HeroIcon(this);
        this.tintIcon(actionIco);
        return actionIco;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(Integer.toString(Math.min(9, this.turnsInvis)));
        txt.hardlight(65280);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        return 0x444444;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(this.attack);
    }

    public static enum AttackLevel {
        LVL_1(1, 0.1f, 1),
        LVL_2(3, 0.2f, 1),
        LVL_3(5, 0.35f, 2),
        LVL_4(9, 0.5f, 3);

        final int turnsReq;
        final float baseDmgBonus;
        final int damageRolls;
        private static final float[][] KOThresholds;
        private static final int[][] blinkRanges;

        private AttackLevel(int turns, float base, int rolls) {
            this.turnsReq = turns;
            this.baseDmgBonus = base;
            this.damageRolls = rolls;
        }

        public float KOThreshold() {
            return KOThresholds[this.ordinal()][Dungeon.hero.pointsInTalent(Talent.ENHANCED_LETHALITY)];
        }

        public int blinkDistance() {
            return blinkRanges[this.ordinal()][Dungeon.hero.pointsInTalent(Talent.ASSASSINS_REACH)];
        }

        public boolean canKO(Char defender) {
            if (defender.properties().contains((Object)Char.Property.MINIBOSS) || defender.properties().contains((Object)Char.Property.BOSS)) {
                return (float)defender.HP / (float)defender.HT < this.KOThreshold() / 5.0f;
            }
            return (float)defender.HP / (float)defender.HT < this.KOThreshold();
        }

        public int damageRoll(Char attacker) {
            int dmg = attacker.damageRoll();
            for (int i = 1; i < this.damageRolls; ++i) {
                int newDmg = attacker.damageRoll();
                if (newDmg <= dmg) continue;
                dmg = newDmg;
            }
            return Math.round((float)dmg * (1.0f + this.baseDmgBonus));
        }

        public static AttackLevel getLvl(int turnsInvis) {
            List<AttackLevel> values = Arrays.asList(AttackLevel.values());
            Collections.reverse(values);
            for (AttackLevel lvl : values) {
                if (turnsInvis < lvl.turnsReq) continue;
                return lvl;
            }
            return LVL_1;
        }

        static {
            KOThresholds = new float[][]{{0.03f, 0.04f, 0.05f, 0.06f}, {0.1f, 0.13f, 0.17f, 0.2f}, {0.2f, 0.27f, 0.33f, 0.4f}, {0.5f, 0.67f, 0.83f, 1.0f}};
            blinkRanges = new int[][]{{1, 1, 2, 2}, {2, 3, 4, 5}, {3, 4, 6, 7}, {4, 6, 8, 10}};
        }
    }
}

