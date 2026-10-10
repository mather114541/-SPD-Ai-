/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Judgement
extends ClericSpell {
    public static Judgement INSTANCE = new Judgement();

    @Override
    public int icon() {
        return 59;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.JUDGEMENT) && hero.buff(AscendedForm.AscendBuff.class) != null;
    }

    @Override
    public void onCast(final HolyTome tome, final Hero hero) {
        hero.sprite.attack(hero.pos, new Callback(){

            @Override
            public void call() {
                GameScene.flash(-2130706433);
                Sample.INSTANCE.play("sounds/blast.mp3");
                int damageBase = 5 + 5 * hero.pointsInTalent(Talent.JUDGEMENT);
                damageBase += Math.round((float)(damageBase * hero.buff(AscendedForm.AscendBuff.class).spellCasts) / 3.0f);
                for (Char ch : Actor.chars()) {
                    if (ch.alignment == hero.alignment || !Dungeon.level.heroFOV[ch.pos]) continue;
                    ch.damage(Hero.heroDamageIntRange(damageBase, 2 * damageBase), Judgement.this);
                    if (hero.subClass != HeroSubClass.PRIEST) continue;
                    Buff.affect(ch, GuidingLight.Illuminated.class);
                }
                hero.spendAndNext(1.0f);
                Judgement.this.onSpellCast(tome, hero);
                hero.buff(AscendedForm.AscendBuff.class).spellCasts = 0;
            }
        });
        hero.busy();
    }

    @Override
    public String desc() {
        int baseDmg;
        int totalBaseDmg = baseDmg = 5 + 5 * Dungeon.hero.pointsInTalent(Talent.JUDGEMENT);
        if (Dungeon.hero.buff(AscendedForm.AscendBuff.class) != null) {
            totalBaseDmg += Math.round((float)(baseDmg * Dungeon.hero.buff(AscendedForm.AscendBuff.class).spellCasts) / 3.0f);
        }
        return Messages.get(this, "desc", baseDmg, 2 * baseDmg, totalBaseDmg, 2 * totalBaseDmg) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }
}

