/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Group;
import java.text.NumberFormat;

public class WndScoreBreakdown
extends Window {
    private static final int WIDTH = 115;
    private int GAP = 4;

    public WndScoreBreakdown() {
        IconTitle title = new IconTitle(Icons.get(Icons.INFO), Messages.get(this, "title", new Object[0]));
        title.setRect(0.0f, 0.0f, 115.0f, 16.0f);
        this.add(title);
        float pos = title.bottom() + 2.0f;
        NumberFormat num = NumberFormat.getInstance(Messages.locale());
        if (Dungeon.initialVersion > 628) {
            pos = this.statSlot(this, Messages.get(this, "progress_title", new Object[0]), num.format(Statistics.progressScore), pos, Statistics.progressScore >= 50000);
            pos = this.addInfo(this, Messages.get(this, "progress_desc", new Object[0]), pos);
            pos = this.statSlot(this, Messages.get(this, "treasure_title", new Object[0]), num.format(Statistics.treasureScore), pos, Statistics.treasureScore >= 20000);
            pos = this.addInfo(this, Messages.get(this, "treasure_desc", new Object[0]), pos);
            pos = this.statSlot(this, Messages.get(this, "explore_title", new Object[0]), num.format(Statistics.exploreScore), pos, Statistics.exploreScore >= 20000);
            pos = this.addInfo(this, Messages.get(this, "explore_desc", new Object[0]), pos);
            pos = this.statSlot(this, Messages.get(this, "bosses_title", new Object[0]), num.format(Statistics.totalBossScore), pos, Statistics.totalBossScore >= 15000);
            pos = this.addInfo(this, Messages.get(this, "bosses_desc", new Object[0]), pos);
            pos = this.statSlot(this, Messages.get(this, "quests_title", new Object[0]), num.format(Statistics.totalQuestScore), pos, Statistics.totalQuestScore >= 10000);
            pos = this.addInfo(this, Messages.get(this, "quests_desc", new Object[0]), pos);
        } else {
            pos = this.statSlot(this, Messages.get(this, "progress_title", new Object[0]), num.format(Statistics.progressScore), pos, Statistics.progressScore >= 78000);
            pos = this.addInfo(this, Messages.get(this, "progress_desc", new Object[0]), pos);
            pos = this.statSlot(this, Messages.get(this, "treasure_title", new Object[0]), num.format(Statistics.treasureScore), pos, Statistics.treasureScore >= 30000);
            pos = this.addInfo(this, Messages.get(this, "treasure_desc_old", new Object[0]), pos);
        }
        if (Statistics.winMultiplier > 1.0f) {
            pos = this.statSlot(this, Messages.get(this, "win_multiplier", new Object[0]), Messages.decimalFormat("#.##", Statistics.winMultiplier) + "x", pos, false);
        }
        if (Statistics.chalMultiplier > 1.0f) {
            pos = this.statSlot(this, Messages.get(this, "challenge_multiplier", new Object[0]), Messages.decimalFormat("#.##", Statistics.chalMultiplier) + "x", pos, false);
        }
        pos = this.statSlot(this, Messages.get(this, "total", new Object[0]), num.format(Statistics.totalScore), pos, false);
        if (Dungeon.initialVersion <= 628) {
            pos = this.addInfo(this, Messages.get(this, "old_score_desc", new Object[0]), pos);
        }
        this.resize(115, (int)pos);
    }

    private float statSlot(Group parent, String label, String value, float pos, boolean highlight) {
        RenderedTextBlock txt = PixelScene.renderTextBlock(label, 7);
        if (highlight) {
            txt.hardlight(0xFFFF44);
        }
        txt.setPos(0.0f, pos);
        parent.add(txt);
        txt = PixelScene.renderTextBlock(value, 7);
        if (highlight) {
            txt.hardlight(0xFFFF44);
        }
        txt.setPos(80.5f, pos);
        PixelScene.align(txt);
        parent.add(txt);
        return pos + (float)this.GAP + txt.height();
    }

    private float addInfo(Group parent, String info, float pos) {
        RenderedTextBlock txt = PixelScene.renderTextBlock(info, 5);
        txt.maxWidth(115);
        txt.hardlight(0x999999);
        txt.setPos(0.0f, pos - 2.0f);
        parent.add(txt);
        return pos - 2.0f + (float)this.GAP + txt.height();
    }
}

