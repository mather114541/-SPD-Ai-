/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameSettings;
import com.watabou.utils.Point;
import java.util.Locale;

public class SPDSettings
extends GameSettings {
    public static final String KEY_VERSION = "version";
    public static final String KEY_FULLSCREEN = "fullscreen";
    public static final String KEY_LANDSCAPE = "force_landscape";
    public static final String KEY_ZOOM = "zoom";
    public static final String KEY_BRIGHTNESS = "brightness";
    public static final String KEY_GRID = "visual_grid";
    public static final String KEY_CAMERA_FOLLOW = "camera_follow";
    public static final String KEY_SCREEN_SHAKE = "screen_shake";
    public static final String KEY_UI_SIZE = "full_ui";
    public static final String KEY_SCALE = "scale";
    public static final String KEY_QUICK_SWAP = "quickslot_swapper";
    public static final String KEY_FLIPTOOLBAR = "flipped_ui";
    public static final String KEY_FLIPTAGS = "flip_tags";
    public static final String KEY_BARMODE = "toolbar_mode";
    public static final String KEY_SLOTWATERSKIN = "quickslot_waterskin";
    public static final String KEY_SYSTEMFONT = "system_font";
    public static final String KEY_VIBRATION = "vibration";
    public static final String KEY_GAMES_SORT = "games_sort";
    public static final String KEY_LAST_CLASS = "last_class";
    public static final String KEY_CHALLENGES = "challenges";
    public static final String KEY_CUSTOM_SEED = "custom_seed";
    public static final String KEY_LAST_DAILY = "last_daily";
    public static final String KEY_INTRO = "intro";
    public static final String KEY_SUPPORT_NAGGED = "support_nagged";
    public static final String KEY_VICTORY_NAGGED = "victory_nagged";
    public static final String KEY_VAULT_INJURE_WARNS = "vault_injure_warns";
    public static final String KEY_CONTROLLER_SENS = "controller_sens";
    public static final String KEY_MOVE_SENS = "move_sens";
    public static final String KEY_NEWS = "news";
    public static final String KEY_UPDATES = "updates";
    public static final String KEY_BETAS = "betas";
    public static final String KEY_WIFI = "wifi";
    public static final String KEY_NEWS_LAST_READ = "news_last_read";
    public static boolean newsDefault = true;
    public static final String KEY_MUSIC = "music";
    public static final String KEY_MUSIC_VOL = "music_vol";
    public static final String KEY_SOUND_FX = "soundfx";
    public static final String KEY_SFX_VOL = "sfx_vol";
    public static final String KEY_IGNORE_SILENT = "ignore_silent";
    public static final String KEY_MUSIC_BG = "music_bg";
    public static final String KEY_LANG = "language";
    public static final String KEY_WINDOW_WIDTH = "window_width";
    public static final String KEY_WINDOW_HEIGHT = "window_height";
    public static final String KEY_WINDOW_MAXIMIZED = "window_maximized";
    public static final String KEY_FULLSCREEN_MONITOR = "fullscreen_monitor";

    public static void version(int value) {
        SPDSettings.put(KEY_VERSION, value);
    }

    public static int version() {
        return SPDSettings.getInt(KEY_VERSION, 0);
    }

    public static void fullscreen(boolean value) {
        SPDSettings.put(KEY_FULLSCREEN, value);
        ShatteredPixelDungeon.updateSystemUI();
    }

    public static boolean fullscreen() {
        return SPDSettings.getBoolean(KEY_FULLSCREEN, true);
    }

    public static void landscape(boolean value) {
        SPDSettings.put(KEY_LANDSCAPE, value);
        ((ShatteredPixelDungeon)ShatteredPixelDungeon.instance).updateDisplaySize();
    }

    public static boolean landscape() {
        return SPDSettings.getBoolean(KEY_LANDSCAPE, false);
    }

    public static void zoom(int value) {
        SPDSettings.put(KEY_ZOOM, value);
    }

    public static int zoom() {
        return SPDSettings.getInt(KEY_ZOOM, 0);
    }

    public static void brightness(int value) {
        SPDSettings.put(KEY_BRIGHTNESS, value);
        GameScene.updateFog();
    }

    public static int brightness() {
        return SPDSettings.getInt(KEY_BRIGHTNESS, 0, -1, 1);
    }

    public static void visualGrid(int value) {
        SPDSettings.put(KEY_GRID, value);
        GameScene.updateMap();
    }

    public static int visualGrid() {
        return SPDSettings.getInt(KEY_GRID, 0, -1, 2);
    }

    public static void cameraFollow(int value) {
        SPDSettings.put(KEY_CAMERA_FOLLOW, value);
    }

    public static int cameraFollow() {
        return SPDSettings.getInt(KEY_CAMERA_FOLLOW, 4, 1, 4);
    }

    public static void screenShake(int value) {
        SPDSettings.put(KEY_SCREEN_SHAKE, value);
    }

    public static int screenShake() {
        return SPDSettings.getInt(KEY_SCREEN_SHAKE, 2, 0, 4);
    }

    public static void interfaceSize(int value) {
        SPDSettings.put(KEY_UI_SIZE, value);
    }

    public static int interfaceSize() {
        float hMin;
        float wMin;
        int size = SPDSettings.getInt(KEY_UI_SIZE, DeviceCompat.isDesktop() ? 2 : 0);
        if (size > 0 && Math.min(wMin = (float)Game.width / 360.0f, hMin = (float)Game.height / 200.0f) < 2.0f * Game.density) {
            size = 0;
        }
        return size;
    }

    public static void scale(int value) {
        SPDSettings.put(KEY_SCALE, value);
    }

    public static int scale() {
        return SPDSettings.getInt(KEY_SCALE, 0);
    }

    public static void quickSwapper(boolean value) {
        SPDSettings.put(KEY_QUICK_SWAP, value);
    }

    public static boolean quickSwapper() {
        return SPDSettings.getBoolean(KEY_QUICK_SWAP, true);
    }

    public static void flipToolbar(boolean value) {
        SPDSettings.put(KEY_FLIPTOOLBAR, value);
    }

    public static boolean flipToolbar() {
        return SPDSettings.getBoolean(KEY_FLIPTOOLBAR, false);
    }

    public static void flipTags(boolean value) {
        SPDSettings.put(KEY_FLIPTAGS, value);
    }

    public static boolean flipTags() {
        return SPDSettings.getBoolean(KEY_FLIPTAGS, false);
    }

    public static void toolbarMode(String value) {
        SPDSettings.put(KEY_BARMODE, value);
    }

    public static String toolbarMode() {
        return SPDSettings.getString(KEY_BARMODE, PixelScene.landscape() ? "GROUP" : "SPLIT");
    }

    public static void quickslotWaterskin(boolean value) {
        SPDSettings.put(KEY_SLOTWATERSKIN, value);
    }

    public static boolean quickslotWaterskin() {
        return SPDSettings.getBoolean(KEY_SLOTWATERSKIN, true);
    }

    public static void systemFont(boolean value) {
        SPDSettings.put(KEY_SYSTEMFONT, value);
    }

    public static boolean systemFont() {
        return SPDSettings.getBoolean(KEY_SYSTEMFONT, SPDSettings.language() == Languages.CHI_SMPL || SPDSettings.language() == Languages.CHI_TRAD || SPDSettings.language() == Languages.KOREAN || SPDSettings.language() == Languages.JAPANESE);
    }

    public static void vibration(boolean value) {
        SPDSettings.put(KEY_VIBRATION, value);
    }

    public static boolean vibration() {
        return SPDSettings.getBoolean(KEY_VIBRATION, true);
    }

    public static String gamesInProgressSort() {
        return SPDSettings.getString(KEY_GAMES_SORT, "level");
    }

    public static void gamesInProgressSort(String value) {
        SPDSettings.put(KEY_GAMES_SORT, value);
    }

    public static void intro(boolean value) {
        SPDSettings.put(KEY_INTRO, value);
    }

    public static boolean intro() {
        return SPDSettings.getBoolean(KEY_INTRO, true);
    }

    public static void lastClass(int value) {
        SPDSettings.put(KEY_LAST_CLASS, value);
    }

    public static int lastClass() {
        return SPDSettings.getInt(KEY_LAST_CLASS, 0, 0, 3);
    }

    public static void challenges(int value) {
        SPDSettings.put(KEY_CHALLENGES, value);
    }

    public static int challenges() {
        return SPDSettings.getInt(KEY_CHALLENGES, 0, 0, 511);
    }

    public static void customSeed(String value) {
        SPDSettings.put(KEY_CUSTOM_SEED, value);
    }

    public static String customSeed() {
        return SPDSettings.getString(KEY_CUSTOM_SEED, "", 20);
    }

    public static void lastDaily(long value) {
        SPDSettings.put(KEY_LAST_DAILY, value);
    }

    public static long lastDaily() {
        return SPDSettings.getLong(KEY_LAST_DAILY, 0L);
    }

    public static void supportNagged(boolean value) {
        SPDSettings.put(KEY_SUPPORT_NAGGED, value);
    }

    public static boolean supportNagged() {
        return SPDSettings.getBoolean(KEY_SUPPORT_NAGGED, false);
    }

    public static void victoryNagged(boolean value) {
        SPDSettings.put(KEY_VICTORY_NAGGED, value);
    }

    public static boolean victoryNagged() {
        return SPDSettings.getBoolean(KEY_VICTORY_NAGGED, false);
    }

    public static void vaultInjureWarns(int value) {
        SPDSettings.put(KEY_VAULT_INJURE_WARNS, value);
    }

    public static int vaultInjureWarns() {
        return SPDSettings.getInt(KEY_VAULT_INJURE_WARNS, 0);
    }

    public static void controllerPointerSensitivity(int value) {
        SPDSettings.put(KEY_CONTROLLER_SENS, value);
    }

    public static int controllerPointerSensitivity() {
        return SPDSettings.getInt(KEY_CONTROLLER_SENS, 5, 1, 10);
    }

    public static void movementHoldSensitivity(int value) {
        SPDSettings.put(KEY_MOVE_SENS, value);
    }

    public static int movementHoldSensitivity() {
        return SPDSettings.getInt(KEY_MOVE_SENS, 3, 0, 4);
    }

    public static void news(boolean value) {
        SPDSettings.put(KEY_NEWS, value);
    }

    public static boolean news() {
        return SPDSettings.getBoolean(KEY_NEWS, newsDefault);
    }

    public static void updates(boolean value) {
        SPDSettings.put(KEY_UPDATES, value);
    }

    public static boolean updates() {
        return SPDSettings.getBoolean(KEY_UPDATES, true);
    }

    public static void betas(boolean value) {
        SPDSettings.put(KEY_BETAS, value);
    }

    public static boolean betas() {
        return SPDSettings.getBoolean(KEY_BETAS, Game.version.contains("BETA") || Game.version.contains("RC"));
    }

    public static void WiFi(boolean value) {
        SPDSettings.put(KEY_WIFI, value);
    }

    public static boolean WiFi() {
        return SPDSettings.getBoolean(KEY_WIFI, true);
    }

    public static void newsLastRead(long lastRead) {
        SPDSettings.put(KEY_NEWS_LAST_READ, lastRead);
    }

    public static long newsLastRead() {
        return SPDSettings.getLong(KEY_NEWS_LAST_READ, 0L);
    }

    public static void music(boolean value) {
        Music.INSTANCE.enable(value);
        SPDSettings.put(KEY_MUSIC, value);
    }

    public static boolean music() {
        return SPDSettings.getBoolean(KEY_MUSIC, true);
    }

    public static void musicVol(int value) {
        Music.INSTANCE.volume((float)(value * value) / 100.0f);
        SPDSettings.put(KEY_MUSIC_VOL, value);
    }

    public static int musicVol() {
        return SPDSettings.getInt(KEY_MUSIC_VOL, 10, 0, 10);
    }

    public static void soundFx(boolean value) {
        Sample.INSTANCE.enable(value);
        SPDSettings.put(KEY_SOUND_FX, value);
    }

    public static boolean soundFx() {
        return SPDSettings.getBoolean(KEY_SOUND_FX, true);
    }

    public static void SFXVol(int value) {
        Sample.INSTANCE.volume((float)(value * value) / 100.0f);
        SPDSettings.put(KEY_SFX_VOL, value);
    }

    public static int SFXVol() {
        return SPDSettings.getInt(KEY_SFX_VOL, 10, 0, 10);
    }

    public static void ignoreSilentMode(boolean value) {
        SPDSettings.put(KEY_IGNORE_SILENT, value);
        Game.platform.setHonorSilentSwitch(!value);
    }

    public static boolean ignoreSilentMode() {
        return SPDSettings.getBoolean(KEY_IGNORE_SILENT, false);
    }

    public static void playMusicInBackground(boolean value) {
        SPDSettings.put(KEY_MUSIC_BG, value);
    }

    public static boolean playMusicInBackground() {
        return SPDSettings.getBoolean(KEY_MUSIC_BG, true);
    }

    public static void language(Languages lang) {
        SPDSettings.put(KEY_LANG, lang.code());
    }

    public static Languages language() {
        String code = SPDSettings.getString(KEY_LANG, null);
        if (code == null) {
            return Languages.matchLocale(Locale.getDefault());
        }
        return Languages.matchCode(code);
    }

    public static void windowResolution(Point p) {
        SPDSettings.put(KEY_WINDOW_WIDTH, p.x);
        SPDSettings.put(KEY_WINDOW_HEIGHT, p.y);
    }

    public static Point windowResolution() {
        return new Point(SPDSettings.getInt(KEY_WINDOW_WIDTH, 800, 720, Integer.MAX_VALUE), SPDSettings.getInt(KEY_WINDOW_HEIGHT, 600, 400, Integer.MAX_VALUE));
    }

    public static void windowMaximized(boolean value) {
        SPDSettings.put(KEY_WINDOW_MAXIMIZED, value);
    }

    public static boolean windowMaximized() {
        return SPDSettings.getBoolean(KEY_WINDOW_MAXIMIZED, false);
    }

    public static void fulLScreenMonitor(int value) {
        SPDSettings.put(KEY_FULLSCREEN_MONITOR, value);
    }

    public static int fulLScreenMonitor() {
        return SPDSettings.getInt(KEY_FULLSCREEN_MONITOR, 0);
    }
}

