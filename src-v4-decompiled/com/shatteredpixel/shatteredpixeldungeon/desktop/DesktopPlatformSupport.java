/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.Game;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.Point;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesktopPlatformSupport
extends PlatformSupport {
    protected static Point[] previousSizes = null;
    private static boolean first = true;
    private static FreeTypeFontGenerator basicFontGenerator;
    private static FreeTypeFontGenerator asianFontGenerator;
    private static Matcher asianMatcher;
    private Pattern regularsplitter = Pattern.compile("(?<=\n)|(?=\n)|(?<=_)|(?=_)|(?<=\\*\\*)|(?=\\*\\*)|(?<=\\p{InHiragana})|(?=\\p{InHiragana})|(?<=\\p{InKatakana})|(?=\\p{InKatakana})|(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})");
    private Pattern regularsplitterMultiline = Pattern.compile("(?<= )|(?= )|(?<=\n)|(?=\n)|(?<=_)|(?=_)|(?<=\\*\\*)|(?=\\*\\*)|(?<=\\p{InHiragana})|(?=\\p{InHiragana})|(?<=\\p{InKatakana})|(?=\\p{InKatakana})|(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})");

    @Override
    public void updateDisplaySize() {
        if (previousSizes == null) {
            previousSizes = new Point[2];
            DesktopPlatformSupport.previousSizes[1] = SPDSettings.windowResolution();
        } else {
            DesktopPlatformSupport.previousSizes[1] = previousSizes[0];
        }
        DesktopPlatformSupport.previousSizes[0] = new Point(Game.width, Game.height);
        if (!SPDSettings.fullscreen()) {
            SPDSettings.windowResolution(previousSizes[0]);
        }
    }

    @Override
    public void updateSystemUI() {
        Gdx.app.postRunnable(new Runnable(){

            @Override
            public void run() {
                if (SPDSettings.fullscreen()) {
                    Graphics.Monitor[] monitors;
                    int monitorNum = 0;
                    if (!first) {
                        monitors = Gdx.graphics.getMonitors();
                        for (int i = 0; i < monitors.length; ++i) {
                            if (((Lwjgl3Graphics.Lwjgl3Monitor)Gdx.graphics.getMonitor()).getMonitorHandle() != ((Lwjgl3Graphics.Lwjgl3Monitor)monitors[i]).getMonitorHandle()) continue;
                            monitorNum = i;
                        }
                    } else {
                        monitorNum = SPDSettings.fulLScreenMonitor();
                    }
                    if ((monitors = Gdx.graphics.getMonitors()).length <= monitorNum) {
                        monitorNum = 0;
                    }
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode(monitors[monitorNum]));
                    SPDSettings.fulLScreenMonitor(monitorNum);
                } else {
                    Point p = SPDSettings.windowResolution();
                    Gdx.graphics.setWindowedMode(p.x, p.y);
                }
                first = false;
            }
        });
    }

    @Override
    public boolean connectedToUnmeteredNetwork() {
        return true;
    }

    @Override
    public boolean supportsVibration() {
        return ControllerHandler.vibrationSupported();
    }

    @Override
    public void setupFontGenerators(int pageSize, boolean systemfont) {
        if (fonts != null && this.pageSize == pageSize && this.systemfont == systemfont) {
            return;
        }
        this.pageSize = pageSize;
        this.systemfont = systemfont;
        this.resetGenerators(false);
        fonts = new HashMap();
        if (systemfont) {
            basicFontGenerator = asianFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/droid_sans.ttf"));
        } else {
            basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel_font.ttf"));
            asianFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/droid_sans.ttf"));
        }
        fonts.put(basicFontGenerator, new HashMap());
        fonts.put(asianFontGenerator, new HashMap());
        this.packer = new PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 1, false);
    }

    @Override
    protected FreeTypeFontGenerator getGeneratorForString(String input) {
        if (asianMatcher.reset(input).find()) {
            return asianFontGenerator;
        }
        return basicFontGenerator;
    }

    @Override
    public String[] splitforTextBlock(String text, boolean multiline) {
        if (multiline) {
            return this.regularsplitterMultiline.split(text);
        }
        return this.regularsplitter.split(text);
    }

    static {
        asianMatcher = Pattern.compile("\\p{InHangul_Syllables}|\\p{InCJK_Unified_Ideographs}|\\p{InCJK_Symbols_and_Punctuation}|\\p{InHalfwidth_and_Fullwidth_Forms}|\\p{InHiragana}|\\p{InKatakana}").matcher("");
    }
}

