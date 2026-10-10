/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WndInfoSubclass
extends WndTitledMessage {
    public WndInfoSubclass(HeroClass cls, HeroSubClass subCls) {
        super(new HeroIcon(subCls), Messages.titleCase(subCls.title()), subCls.desc());
        ArrayList<LinkedHashMap<Talent, Integer>> talentList = new ArrayList<LinkedHashMap<Talent, Integer>>();
        Talent.initClassTalents(cls, talentList);
        Talent.initSubclassTalents(subCls, talentList);
        TalentsPane.TalentTierPane talentPane = new TalentsPane.TalentTierPane(talentList.get(2), 3, TalentButton.Mode.INFO);
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

