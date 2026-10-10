/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRanking;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WndDailies
extends Window {
    private static final int WIDTH = 115;
    private static final int HEIGHT = 144;

    public WndDailies() {
        this.resize(115, 144);
        ScrollPane pane = new ScrollPane(new Component());
        this.add(pane);
        pane.setRect(0.0f, 0.0f, 115.0f, 144.0f);
        Component content = pane.content();
        IconTitle title = new IconTitle(Icons.CALENDAR.get(), Messages.get(this, "title", new Object[0]));
        title.imIcon.hardlight(0.5f, 1.0f, 2.0f);
        title.setRect(0.0f, 0.0f, 115.0f, 0.0f);
        title.setPos(0.0f, 0.0f);
        content.add(title);
        if (Rankings.INSTANCE.latestDailyReplay != null) {
            IconButton replayInfo = new IconButton(Icons.get(Icons.CALENDAR)){

                @Override
                protected void onClick() {
                    ShatteredPixelDungeon.scene().addToFront(new WndRanking(Rankings.INSTANCE.latestDailyReplay));
                }

                @Override
                protected void onPointerUp() {
                    super.onPointerUp();
                    this.icon.hardlight(1.0f, 0.5f, 2.0f);
                }
            };
            replayInfo.icon().hardlight(1.0f, 0.5f, 2.0f);
            replayInfo.setRect(99.0f, 0.0f, 16.0f, 16.0f);
            this.add(replayInfo);
        }
        int top = (int)title.bottom() + 3;
        RenderedTextBlock day = PixelScene.renderTextBlock(Messages.get(this, "date", new Object[0]), 7);
        day.hardlight(0xFFFF44);
        day.setPos(0.0f, top);
        content.add(day);
        RenderedTextBlock score = PixelScene.renderTextBlock(Messages.get(this, "score", new Object[0]), 7);
        score.hardlight(0xFFFF44);
        score.setPos(115.0f - score.width(), top);
        content.add(score);
        top = (int)score.bottom() + 6;
        NumberFormat num = NumberFormat.getInstance(Messages.locale());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        ArrayList<Long> dates = new ArrayList<Long>(Rankings.INSTANCE.dailyScoreHistory.keySet());
        Collections.reverse(dates);
        boolean first = Rankings.INSTANCE.latestDaily != null;
        for (long l : dates) {
            if (first) {
                top += 2;
            }
            ColorBlock sep = new ColorBlock(115.0f, 1.0f, -16777216);
            sep.y = top - 3 - (first ? 2 : 0);
            content.add(sep);
            date.setTime(l);
            day = PixelScene.renderTextBlock(format.format(date), 7);
            day.setPos(0.0f, top);
            content.add(day);
            if (first) {
                IconButton latestInfo = new IconButton(Icons.INFO.get()){

                    @Override
                    protected void onClick() {
                        ShatteredPixelDungeon.scene().addToFront(new WndRanking(Rankings.INSTANCE.latestDaily));
                    }
                };
                latestInfo.setRect(day.right() + 2.0f, top - 5, 16.0f, 16.0f);
                content.add(latestInfo);
            }
            score = PixelScene.renderTextBlock(num.format(Rankings.INSTANCE.dailyScoreHistory.get(l)), 7);
            score.setPos(115.0f - score.width(), top);
            content.add(score);
            top = (int)day.bottom() + 6;
            if (!first) continue;
            top += 2;
            first = false;
        }
        content.setRect(0.0f, 0.0f, 115.0f, top);
        pane.setRect(0.0f, 0.0f, 115.0f, 144.0f);
    }
}

