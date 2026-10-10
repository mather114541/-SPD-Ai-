/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class PrismaticGuard
extends Buff {
    private float HP;
    private float powerOfManyTurns;
    private static final String HEALTH = "hp";
    private static final String POWER_TURNS = "power_turns";

    public PrismaticGuard() {
        this.type = Buff.buffType.POSITIVE;
        this.powerOfManyTurns = 0.0f;
    }

    @Override
    public boolean act() {
        Hero hero = (Hero)this.target;
        Mob closest = null;
        int v = hero.visibleEnemies();
        for (int i = 0; i < v; ++i) {
            Mob mob = hero.visibleEnemy(i);
            if (!mob.isAlive() || mob.isInvulnerable(PrismaticImage.class) || mob.state == mob.PASSIVE || mob.state == mob.WANDERING || mob.state == mob.SLEEPING || hero.mindVisionEnemies.contains(mob) || closest != null && Dungeon.level.distance(hero.pos, mob.pos) >= Dungeon.level.distance(hero.pos, closest.pos)) continue;
            closest = mob;
        }
        if (closest != null && Dungeon.level.distance(hero.pos, closest.pos) < 5) {
            int bestPos = -1;
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) != null || !Dungeon.level.passable[p] || bestPos != -1 && !(Dungeon.level.trueDistance(p, closest.pos) < Dungeon.level.trueDistance(bestPos, closest.pos))) continue;
                bestPos = p;
            }
            if (bestPos != -1) {
                PrismaticImage pris = new PrismaticImage();
                pris.duplicate(hero, (int)Math.floor(this.HP));
                if (this.powerOfManyTurns > 0.0f) {
                    Buff.affect(pris, PowerOfMany.PowerBuff.class, this.powerOfManyTurns);
                }
                pris.state = pris.HUNTING;
                GameScene.add(pris, 1.0f);
                ScrollOfTeleportation.appear(pris, bestPos);
                this.detach();
            } else {
                this.spend(1.0f);
            }
        } else {
            this.spend(1.0f);
        }
        if (this.HP < (float)this.maxHP() && Regeneration.regenOn()) {
            this.HP += 0.1f;
        }
        if (this.powerOfManyTurns > 0.0f) {
            this.powerOfManyTurns -= 1.0f;
            if (this.powerOfManyTurns <= 0.0f) {
                this.powerOfManyTurns = 0.0f;
                BuffIndicator.refreshHero();
            }
        }
        return true;
    }

    public void set(int HP) {
        this.HP = HP;
        this.powerOfManyTurns = 0.0f;
    }

    public void set(PrismaticImage img) {
        this.HP = img.HP;
        this.powerOfManyTurns = img.buff(PowerOfMany.PowerBuff.class) != null ? img.buff(PowerOfMany.PowerBuff.class).cooldown() + 1.0f : 0.0f;
    }

    public int maxHP() {
        return PrismaticGuard.maxHP((Hero)this.target);
    }

    public static int maxHP(Hero hero) {
        return 10 + (int)Math.floor((float)hero.lvl * 2.5f);
    }

    public boolean isEmpowered() {
        return this.powerOfManyTurns > 0.0f;
    }

    @Override
    public int icon() {
        return 20;
    }

    @Override
    public void tintIcon(Image icon) {
        if (this.isEmpowered()) {
            icon.hardlight(3.0f, 3.0f, 2.0f);
        } else {
            icon.hardlight(1.0f, 1.0f, 2.0f);
        }
    }

    @Override
    public float iconFadePercent() {
        return 1.0f - this.HP / (float)this.maxHP();
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.HP);
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", (int)this.HP, this.maxHP());
        if (this.isEmpowered()) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_many", (int)this.powerOfManyTurns);
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEALTH, this.HP);
        bundle.put(POWER_TURNS, this.powerOfManyTurns);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.HP = bundle.getFloat(HEALTH);
        this.powerOfManyTurns = bundle.getFloat(POWER_TURNS);
    }
}

