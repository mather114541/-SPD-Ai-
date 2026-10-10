/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class GameLog
extends Component
implements Signal.Listener<String> {
    private static final int MAX_LINES = 3;
    private static final Pattern PUNCTUATION = Pattern.compile(".*[.,;?! ]$");
    private RenderedTextBlock lastEntry;
    private int lastColor;
    private static ArrayList<Entry> entries = new ArrayList();
    private static ArrayList<String> textsToAdd = new ArrayList();

    public GameLog() {
        GLog.update.replace(this);
        this.recreateLines();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void update() {
        ArrayList<String> arrayList = textsToAdd;
        synchronized (arrayList) {
            if (!textsToAdd.isEmpty()) {
                int maxLines = SPDSettings.interfaceSize() > 0 ? 5 : 3;
                for (String text : textsToAdd) {
                    int nLines;
                    if (this.length != entries.size()) {
                        this.clear();
                        this.recreateLines();
                    }
                    if (text.equals("\n")) {
                        this.lastEntry = null;
                        continue;
                    }
                    int color = 0xFFFFFF;
                    if (text.startsWith("++ ")) {
                        text = text.substring("++ ".length());
                        color = 65280;
                    } else if (text.startsWith("-- ")) {
                        text = text.substring("-- ".length());
                        color = 0xFF0000;
                    } else if (text.startsWith("** ")) {
                        text = text.substring("** ".length());
                        color = 0xFF8800;
                    } else if (text.startsWith("@@ ")) {
                        text = text.substring("@@ ".length());
                        color = 0xFFFF00;
                    }
                    if (this.lastEntry != null && color == this.lastColor && this.lastEntry.nLines < maxLines) {
                        String lastMessage = this.lastEntry.text();
                        this.lastEntry.text((String)(lastMessage.length() == 0 ? text : lastMessage + " " + text));
                        GameLog.entries.get((int)(GameLog.entries.size() - 1)).text = this.lastEntry.text();
                    } else {
                        this.lastEntry = PixelScene.renderTextBlock(text, 6);
                        this.lastEntry.hardlight(color);
                        this.lastColor = color;
                        this.add(this.lastEntry);
                        entries.add(new Entry(text, color));
                    }
                    if (this.length <= 0) continue;
                    do {
                        nLines = 0;
                        for (int i = 0; i < this.length - 1; ++i) {
                            nLines += ((RenderedTextBlock)this.members.get((int)i)).nLines;
                        }
                        if (nLines <= maxLines) continue;
                        RenderedTextBlock r = (RenderedTextBlock)this.members.get(0);
                        this.remove(r);
                        r.destroy();
                        entries.remove(0);
                    } while (nLines > maxLines);
                    if (!entries.isEmpty()) continue;
                    this.lastEntry = null;
                }
                this.layout();
                textsToAdd.clear();
            }
        }
        super.update();
    }

    private synchronized void recreateLines() {
        for (Entry entry : entries) {
            this.lastEntry = PixelScene.renderTextBlock(entry.text, 6);
            this.lastColor = entry.color;
            this.lastEntry.hardlight(this.lastColor);
            this.add(this.lastEntry);
        }
    }

    public synchronized void newLine() {
        this.lastEntry = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean onSignal(String text) {
        ArrayList<String> arrayList = textsToAdd;
        synchronized (arrayList) {
            textsToAdd.add(text);
        }
        return false;
    }

    @Override
    protected void layout() {
        float pos = this.y;
        for (int i = this.length - 1; i >= 0; --i) {
            RenderedTextBlock entry = (RenderedTextBlock)this.members.get(i);
            entry.setHightlighting(false);
            entry.maxWidth((int)this.width);
            entry.setPos(this.x, pos - entry.height());
            pos -= entry.height() + 2.0f;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void wipe() {
        ArrayList<String> arrayList = textsToAdd;
        synchronized (arrayList) {
            entries.clear();
            textsToAdd.clear();
        }
    }

    private static class Entry {
        public String text;
        public int color;

        public Entry(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }
}

