/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class Radiance
extends ClericSpell {
    public static final Radiance INSTANCE = new Radiance();

    @Override
    public int icon() {
        return 50;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.subClass == HeroSubClass.PRIEST;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        GameScene.flash(-2130706433);
        Sample.INSTANCE.play("sounds/blast.mp3");
        if (Dungeon.level.viewDistance < 6) {
            Buff.prolong(hero, Light.class, Dungeon.isChallenged(32) ? 20.0f : 100.0f);
        }
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment == Char.Alignment.ALLY || !Dungeon.level.heroFOV[mob.pos]) continue;
            if (mob.buff(GuidingLight.Illuminated.class) != null) {
                mob.damage(hero.lvl + 5, GuidingLight.INSTANCE);
            } else {
                Buff.affect(mob, GuidingLight.Illuminated.class);
                Buff.affect(mob, GuidingLight.WasIlluminatedTracker.class);
            }
            if (!mob.isActive()) continue;
            Buff.affect(mob, Paralysis.class, 3.0f);
        }
        hero.spend(1.0f);
        hero.busy();
        hero.sprite.operate(hero.pos);
        this.onSpellCast(tome, hero);
    }
}

