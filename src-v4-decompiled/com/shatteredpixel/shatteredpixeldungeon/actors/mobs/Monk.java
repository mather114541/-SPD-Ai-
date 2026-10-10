/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Monk
extends Mob {
    protected float focusCooldown;
    private static String FOCUS_COOLDOWN = "focus_cooldown";

    public Monk() {
        this.spriteClass = MonkSprite.class;
        this.HT = 70;
        this.HP = 70;
        this.defenseSkill = 30;
        this.EXP = 11;
        this.maxLvl = 21;
        this.loot = Food.class;
        this.lootChance = 0.083f;
        this.properties.add(Char.Property.UNDEAD);
        this.focusCooldown = 0.0f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(12, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    public float attackDelay() {
        return super.attackDelay() * 0.5f;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public void rollToDropLoot() {
        Imp.Quest.oldProcess(this);
        super.rollToDropLoot();
    }

    @Override
    protected boolean act() {
        boolean result = super.act();
        if (this.buff(Focus.class) == null && this.state == this.HUNTING && this.focusCooldown <= 0.0f) {
            Buff.affect(this, Focus.class);
        }
        return result;
    }

    @Override
    protected void spend(float time) {
        this.focusCooldown -= time;
        super.spend(time);
    }

    @Override
    public void move(int step, boolean travelling) {
        if (travelling) {
            this.focusCooldown -= 0.67f;
        }
        super.move(step, travelling);
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (this.buff(Focus.class) != null && this.paralysed == 0 && this.state != this.SLEEPING) {
            return INFINITE_EVASION;
        }
        return super.defenseSkill(enemy);
    }

    @Override
    public String defenseVerb() {
        Focus f = this.buff(Focus.class);
        if (f == null) {
            return super.defenseVerb();
        }
        f.detach();
        if (this.sprite != null && this.sprite.visible) {
            Sample.INSTANCE.play("sounds/hit_parry.mp3", 1.0f, Random.Float(0.96f, 1.05f));
        }
        this.focusCooldown = Random.NormalFloat(6.0f, 7.0f);
        return Messages.get(this, "parried", new Object[0]);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FOCUS_COOLDOWN, this.focusCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.focusCooldown = bundle.getInt(FOCUS_COOLDOWN);
    }

    public static class Focus
    extends Buff {
        public Focus() {
            this.type = Buff.buffType.POSITIVE;
            this.announced = true;
        }

        @Override
        public int icon() {
            return 0;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.25f, 1.5f, 1.0f);
        }
    }
}

