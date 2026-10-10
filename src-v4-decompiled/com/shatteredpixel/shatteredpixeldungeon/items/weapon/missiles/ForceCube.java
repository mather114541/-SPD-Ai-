/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ForceCube
extends MissileWeapon {
    public ForceCube() {
        this.image = ItemSpriteSheet.FORCE_CUBE;
        this.tier = 5;
        this.baseUses = 5.0f;
        this.sticky = false;
    }

    @Override
    public int max(int lvl) {
        return 6 * this.tier + this.tier * lvl;
    }

    @Override
    public void hitSound(float pitch) {
    }

    @Override
    public float castDelay(Char user, int cell) {
        if (!Dungeon.level.pit[cell] && Actor.findChar(cell) == null) {
            return this.delayFactor(user);
        }
        return super.castDelay(user, cell);
    }

    @Override
    protected void onThrow(int cell) {
        Char primaryTarget;
        if (Dungeon.level.pit[cell] && Actor.findChar(cell) == null) {
            super.onThrow(cell);
            return;
        }
        MissileWeapon parentTemp = this.parent;
        this.rangedHit(null, cell);
        this.parent = parentTemp;
        Dungeon.level.pressCell(cell);
        ArrayList<Char> targets = new ArrayList<Char>();
        if (Actor.findChar(cell) != null) {
            primaryTarget = Actor.findChar(cell);
            targets.add(primaryTarget);
        } else {
            primaryTarget = null;
        }
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!(Dungeon.level.traps.get(cell + i) instanceof TenguDartTrap)) {
                Dungeon.level.pressCell(cell + i);
            }
            if (Actor.findChar(cell + i) == null) continue;
            targets.add(Actor.findChar(cell + i));
        }
        Collections.sort(targets, new Comparator<Char>(){

            @Override
            public int compare(Char a, Char b) {
                return Float.compare(Dungeon.level.trueDistance(b.pos, curUser.pos), Dungeon.level.trueDistance(a.pos, curUser.pos));
            }
        });
        Object object = targets.iterator();
        while (object.hasNext()) {
            Char target = (Char)object.next();
            curUser.shoot(target, this);
            if (target != Dungeon.hero || target.isAlive()) continue;
            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
        }
        if (ForceCube.curUser.subClass == HeroSubClass.SNIPER && primaryTarget != null && primaryTarget.isActive()) {
            Actor.add(new Actor(){
                {
                    this.actPriority = 99;
                }

                @Override
                protected boolean act() {
                    SnipersMark mark = Dungeon.hero.buff(SnipersMark.class);
                    if (mark != null && primaryTarget.isActive()) {
                        mark.object = primaryTarget.id();
                    }
                    Actor.remove(this);
                    return true;
                }
            });
        }
        WandOfBlastWave.BlastWave.blast(cell);
        Sample.INSTANCE.play("sounds/blast.mp3");
    }
}

