/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WndInfoArmorAbility
extends WndTitledMessage {
    public WndInfoArmorAbility(HeroClass cls, ArmorAbility ability) {
        super(new HeroIcon(ability), Messages.titleCase(ability.name()), ability.desc());
        ArrayList<LinkedHashMap<Talent, Integer>> talentList = new ArrayList<LinkedHashMap<Talent, Integer>>();
        Talent.initArmorTalents(ability, talentList);
        Ratmogrify.useRatroicEnergy = ability instanceof Ratmogrify;
        TalentsPane.TalentTierPane talentPane = new TalentsPane.TalentTierPane(talentList.get(3), 4, TalentButton.Mode.INFO);
        talentPane.title.text(Messages.titleCase(Messages.get(WndHeroInfo.class, "talents", new Object[0])));
        talentPane.setRect(0.0f, this.height + 5, this.width, talentPane.height());
        this.add(talentPane);
        this.resize(this.width, (int)talentPane.bottom());
    }

    @Override
    protected float targetHeight() {
        return super.targetHeight() - 40.0f;
    }
}

