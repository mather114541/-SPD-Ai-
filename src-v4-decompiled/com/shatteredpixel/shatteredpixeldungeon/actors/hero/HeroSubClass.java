/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;

public enum HeroSubClass {
    NONE(127),
    BERSERKER(0),
    GLADIATOR(1),
    BATTLEMAGE(2),
    WARLOCK(3),
    ASSASSIN(4),
    FREERUNNER(5),
    SNIPER(6),
    WARDEN(7),
    CHAMPION(8),
    MONK(9),
    PRIEST(10),
    PALADIN(11);

    int icon;

    private HeroSubClass(int icon) {
        this.icon = icon;
    }

    public String title() {
        return Messages.get((Object)this, this.name(), new Object[0]);
    }

    public String shortDesc() {
        return Messages.get((Object)this, this.name() + "_short_desc", new Object[0]);
    }

    public String desc() {
        if (this == BATTLEMAGE) {
            MagesStaff staff;
            Object desc = Messages.get((Object)this, this.name() + "_desc", new Object[0]);
            if (Game.scene() instanceof GameScene && (staff = Dungeon.hero.belongings.getItem(MagesStaff.class)) != null && staff.wandClass() != null) {
                desc = (String)desc + "\n\n" + Messages.get(staff.wandClass(), "bmage_desc", new Object[0]);
                desc = ((String)desc).replaceAll("_", "");
            }
            return desc;
        }
        return Messages.get((Object)this, this.name() + "_desc", new Object[0]);
    }

    public int icon() {
        return this.icon;
    }
}

