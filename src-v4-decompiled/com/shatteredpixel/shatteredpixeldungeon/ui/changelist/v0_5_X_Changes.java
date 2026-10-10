/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeIcons;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;
import java.util.ArrayList;

public class v0_5_X_Changes {
    public static void addAllChanges(ArrayList<ChangeInfo> changeInfos) {
        v0_5_X_Changes.add_v0_5_0_Changes(changeInfos);
    }

    public static void add_v0_5_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.5.0", true, "");
        changes.hardlight(0xFFFF44);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(ChangeIcons.V021_SHPX, "Developer Commentary", "_-_ Released February 8th, 2017\n_-_ 115 days after Shattered v0.4.3\n_-_ 233 days after Shattered v0.4.0\n\nv0.5.0 was the first major update that didn't focus on a category of items, instead it was all about the game's visuals! This visual overhaul was a very important update for Shattered, as it created an immediate visual difference between it and the original Pixel Dungeon. At this stage Shattered was starting to become less of an addition to Pixel Dungeon and more of its own game in the same spirit. Having an updated set of graphics really helped sell people on this idea.\n\nv0.5.0 was also Shattered's longest-developed update yet. While I don't think there was much I could do about this for v0.5.0 and v0.6.0, it started an unfortunate trend of major updates taking up to half a year! I eventually broke this trend in v0.9.0 by splitting these larger updates into smaller parts."));
        changes.addButton(new ChangeButton(ChangeIcons.V050_STAIRS, "New Dungeon Visual Style!", "_-_ Walls and some terrain now have depth\n_-_ Characters & items are raised & cast shadows\n_-_ Added a visible tile grid in the settings menu"));
        changes.addButton(new ChangeButton(ChangeIcons.V050_QUARTERSTAFF, "Equipment Balance Changes", "_-_ Quarterstaff armor bonus increased from 2 to 3\n\n_-_ Wand of Frost damage against chilled enemies reduced from -7.5% per turn of chill to -10%\n\n_-_ Wand of Transfusion self-damage reduced from 15% max hp to 10% max hp per zap\n\n_-_ Dried Rose charges 20% faster and the ghost hero is stronger, especially at low levels"));
        changes.addButton(new ChangeButton(ChangeIcons.V040_STYLUS, "Glyph Balance Changes", "_-_ Glyph of Entanglement activates less often but grants significantly more herbal armor\n\n_-_ Glyph of Stone armor bonus reduced from 2+level to 0+level\n\n_-_ Glyph of Antimagic magical damage resist reduced from 50% of armor to 33% of armor\n\n_-_ Glyph of Viscosity damage rate increased from 10% of deferred damage to 15%"));
        changes.addButton(new ChangeButton(ChangeIcons.V034_TRANSLATIONS, Messages.get(ChangesScene.class, "language", new Object[0]), "_-_ Added new Language: Esperanto\n_-_ Added new Language: Indonesian\n"));
    }
}

