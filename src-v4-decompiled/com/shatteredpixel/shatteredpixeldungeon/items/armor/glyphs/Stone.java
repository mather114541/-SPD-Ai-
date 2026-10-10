/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.FerretTuft;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.GameMath;

public class Stone
extends Armor.Glyph {
    private static ItemSprite.Glowing GREY = new ItemSprite.Glowing(0x222222);
    private static boolean testing = false;

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        testing = true;
        float accuracy = attacker.attackSkill(defender);
        float evasion = defender.defenseSkill(attacker);
        testing = false;
        if (attacker.buff(Bless.class) != null) {
            accuracy *= 1.25f;
        }
        if (attacker.buff(Hex.class) != null) {
            accuracy *= 0.8f;
        }
        if (attacker.buff(Daze.class) != null) {
            accuracy *= 0.5f;
        }
        for (ChampionEnemy buff : attacker.buffs(ChampionEnemy.class)) {
            accuracy *= buff.evasionAndAccuracyFactor();
        }
        accuracy *= AscensionChallenge.statModifier(attacker);
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.BLESS) && attacker.alignment == Char.Alignment.ALLY) {
            accuracy *= 1.01f + 0.02f * (float)Dungeon.hero.pointsInTalent(Talent.BLESS);
        }
        if (defender.buff(Bless.class) != null) {
            evasion *= 1.25f;
        }
        if (defender.buff(Hex.class) != null) {
            evasion *= 0.8f;
        }
        if (defender.buff(Daze.class) != null) {
            evasion *= 0.5f;
        }
        for (ChampionEnemy buff : defender.buffs(ChampionEnemy.class)) {
            evasion *= buff.evasionAndAccuracyFactor();
        }
        evasion *= AscensionChallenge.statModifier(defender);
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.BLESS) && defender.alignment == Char.Alignment.ALLY) {
            evasion *= 1.01f + 0.02f * (float)Dungeon.hero.pointsInTalent(Talent.BLESS);
        }
        evasion *= FerretTuft.evasionMultiplier();
        float hitChance = (evasion *= Stone.genericProcChanceMultiplier(defender)) >= accuracy ? accuracy / evasion / 2.0f : 1.0f - evasion / accuracy / 2.0f;
        hitChance = GameMath.gate(0.25f, (1.0f + 3.0f * hitChance) / 4.0f, 1.0f);
        damage = (int)Math.ceil((float)damage * hitChance);
        return damage;
    }

    public static boolean testingEvasion() {
        return testing;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREY;
    }
}

