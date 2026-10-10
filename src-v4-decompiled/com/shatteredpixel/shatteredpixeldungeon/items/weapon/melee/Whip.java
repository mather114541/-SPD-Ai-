/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import java.util.ArrayList;

public class Whip
extends MeleeWeapon {
    public Whip() {
        this.image = ItemSpriteSheet.WHIP;
        this.hitSound = "sounds/hit.mp3";
        this.hitSoundPitch = 1.1f;
        this.tier = 3;
        this.RCH = 3;
    }

    @Override
    public int max(int lvl) {
        return 5 * this.tier + lvl * this.tier;
    }

    @Override
    protected void duelistAbility(final Hero hero, Integer target) {
        final ArrayList<Char> targets = new ArrayList<Char>();
        Char closest = null;
        hero.belongings.abilityWeapon = this;
        for (Char ch : Actor.chars()) {
            if (ch.alignment != Char.Alignment.ENEMY || hero.isCharmedBy(ch) || !Dungeon.level.heroFOV[ch.pos] || !hero.canAttack(ch)) continue;
            targets.add(ch);
            if (closest != null && !(Dungeon.level.trueDistance(hero.pos, closest.pos) > Dungeon.level.trueDistance(hero.pos, ch.pos))) continue;
            closest = ch;
        }
        hero.belongings.abilityWeapon = null;
        if (targets.isEmpty()) {
            GLog.w(Messages.get(this, "ability_no_target", new Object[0]), new Object[0]);
            return;
        }
        this.throwSound();
        final Char finalClosest = closest;
        hero.sprite.attack(hero.pos, new Callback(){

            @Override
            public void call() {
                Whip.this.beforeAbilityUsed(hero, finalClosest);
                for (Char ch : targets) {
                    hero.attack(ch, 1.0f, 0.0f, Char.INFINITE_ACCURACY);
                    if (ch.isAlive()) continue;
                    MeleeWeapon.onAbilityKill(hero, ch);
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                Whip.this.afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min()), this.augment.damageFactor(this.max()));
        }
        return Messages.get(this, "typical_ability_desc", this.min(0), this.max(0));
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return this.augment.damageFactor(this.min(level)) + "-" + this.augment.damageFactor(this.max(level));
    }
}

