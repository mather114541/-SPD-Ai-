/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import java.util.HashSet;

public class Buff
extends Actor {
    public Char target;
    public boolean mnemonicExtended = false;
    public buffType type;
    public boolean announced;
    public boolean revivePersists;
    protected HashSet<Class> resistances;
    protected HashSet<Class> immunities;
    private static final String MNEMONIC_EXTENDED = "mnemonic_extended";

    public Buff() {
        this.actPriority = -30;
        this.type = buffType.NEUTRAL;
        this.announced = false;
        this.revivePersists = false;
        this.resistances = new HashSet();
        this.immunities = new HashSet();
    }

    public HashSet<Class> resistances() {
        return new HashSet<Class>(this.resistances);
    }

    public HashSet<Class> immunities() {
        return new HashSet<Class>(this.immunities);
    }

    public boolean attachTo(Char target) {
        if (target.isImmune(this.getClass())) {
            return false;
        }
        this.target = target;
        if (target.add(this)) {
            if (target.sprite != null) {
                this.fx(true);
            }
            return true;
        }
        this.target = null;
        return false;
    }

    public void detach() {
        if (this.target.remove(this) && this.target.sprite != null) {
            this.fx(false);
        }
    }

    @Override
    public boolean act() {
        this.diactivate();
        return true;
    }

    public int icon() {
        return 127;
    }

    public void tintIcon(Image icon) {
    }

    public float iconFadePercent() {
        return 0.0f;
    }

    public String iconTextDisplay() {
        return "";
    }

    public void fx(boolean on) {
    }

    public String heroMessage() {
        String msg = Messages.get(this, "heromsg", new Object[0]);
        if (msg.isEmpty()) {
            return null;
        }
        return msg;
    }

    public String name() {
        return Messages.get(this, "name", new Object[0]);
    }

    public String desc() {
        return Messages.get(this, "desc", new Object[0]);
    }

    protected String dispTurns(float input) {
        return Messages.decimalFormat("#.##", input);
    }

    public float visualcooldown() {
        return this.cooldown() + 1.0f;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.mnemonicExtended) {
            bundle.put(MNEMONIC_EXTENDED, this.mnemonicExtended);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(MNEMONIC_EXTENDED)) {
            this.mnemonicExtended = bundle.getBoolean(MNEMONIC_EXTENDED);
        }
    }

    public static <T extends Buff> T append(Char target, Class<T> buffClass) {
        Buff buff = (Buff)Reflection.newInstance(buffClass);
        buff.attachTo(target);
        return (T)buff;
    }

    public static <T extends FlavourBuff> T append(Char target, Class<T> buffClass, float duration) {
        FlavourBuff buff = (FlavourBuff)Buff.append(target, buffClass);
        buff.spend(duration * target.resist(buffClass));
        return (T)buff;
    }

    public static <T extends Buff> T affect(Char target, Class<T> buffClass) {
        T buff = target.buff(buffClass);
        if (buff != null) {
            return buff;
        }
        return Buff.append(target, buffClass);
    }

    public static <T extends FlavourBuff> T affect(Char target, Class<T> buffClass, float duration) {
        FlavourBuff buff = (FlavourBuff)Buff.affect(target, buffClass);
        buff.spend(duration * target.resist(buffClass));
        return (T)buff;
    }

    public static <T extends FlavourBuff> T prolong(Char target, Class<T> buffClass, float duration) {
        FlavourBuff buff = (FlavourBuff)Buff.affect(target, buffClass);
        buff.postpone(duration * target.resist(buffClass));
        return (T)buff;
    }

    public static <T extends CounterBuff> T count(Char target, Class<T> buffclass, float count) {
        CounterBuff buff = (CounterBuff)Buff.affect(target, buffclass);
        buff.countUp(count);
        return (T)buff;
    }

    public static void detach(Char target, Class<? extends Buff> cl) {
        for (Buff buff : target.buffs(cl)) {
            buff.detach();
        }
    }

    public static enum buffType {
        POSITIVE,
        NEGATIVE,
        NEUTRAL;

    }

    public static interface DOTbuff {
        public int totalIncomingDMG();
    }
}

