/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.utils.Architecture;
import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.desktop.DesktopLaunchValidator;
import com.shatteredpixel.shatteredpixeldungeon.desktop.DesktopPlatformSupport;
import com.shatteredpixel.shatteredpixeldungeon.desktop.DesktopWindowListener;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.UpdateImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class DesktopLauncher {
    public static void main(String[] args) {
        if (!DesktopLaunchValidator.verifyValidJVMState(args)) {
            return;
        }
        if (System.getProperty("os.name").contains("FreeBSD")) {
            SharedLibraryLoader.os = Os.Linux;
            if (System.getProperty("os.arch").contains("64") || System.getProperty("os.arch").startsWith("armv8")) {
                SharedLibraryLoader.bitness = Architecture.Bitness._64;
            }
        }
        final String title = DesktopLauncher.class.getPackage().getSpecificationTitle() == null ? System.getProperty("Specification-Title") : DesktopLauncher.class.getPackage().getSpecificationTitle();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Game.reportException(throwable);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                pw.flush();
                Object exceptionMsg = sw.toString();
                exceptionMsg = ((String)exceptionMsg).replaceAll("\\(.*:([0-9]*)\\)", "($1)");
                exceptionMsg = ((String)exceptionMsg).replace("com.shatteredpixel.shatteredpixeldungeon.", "");
                exceptionMsg = ((String)exceptionMsg).replace("com.watabou.", "");
                exceptionMsg = ((String)exceptionMsg).replace("com.badlogic.gdx.", "");
                exceptionMsg = ((String)exceptionMsg).replace("\t", "  ");
                exceptionMsg = ((String)exceptionMsg).replace('\'', '\u2019');
                exceptionMsg = ((String)exceptionMsg).replace('\"', '\u201d');
                if (((String)exceptionMsg).length() > 1000) {
                    exceptionMsg = ((String)exceptionMsg).substring(0, 1000) + "...";
                }
                if (((String)exceptionMsg).contains("Couldn\u2019t create window")) {
                    TinyFileDialogs.tinyfd_messageBox(title + " Has Crashed!", title + " was not able to initialize its graphics display, sorry about that!\n\nThis usually happens when your graphics card has misconfigured drivers or does not support openGL 2.0+.\n\nIf you are certain the game should work on your computer, please message the developer (Evan@ShatteredPixel.com)\n\nversion: " + Game.version + "\n" + (String)exceptionMsg, "ok", "error", false);
                } else {
                    TinyFileDialogs.tinyfd_messageBox(title + " Has Crashed!", title + " has run into an error it cannot recover from and has crashed, sorry about that!\n\nIf you could, please email this error message to the developer (Evan@ShatteredPixel.com):\n\nversion: " + Game.version + "\n" + (String)exceptionMsg, "ok", "error", false);
                }
                System.exit(1);
            }
        });
        Game.version = DesktopLauncher.class.getPackage().getSpecificationVersion();
        if (Game.version == null) {
            Game.version = System.getProperty("Specification-Version");
        }
        try {
            Game.versionCode = Integer.parseInt(DesktopLauncher.class.getPackage().getImplementationVersion());
        }
        catch (NumberFormatException e) {
            Game.versionCode = Integer.parseInt(System.getProperty("Implementation-Version"));
        }
        if (UpdateImpl.supportsUpdates()) {
            Updates.service = UpdateImpl.getUpdateService();
        }
        if (NewsImpl.supportsNews()) {
            News.service = NewsImpl.getNewsService();
        }
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(title);
        String vendor = DesktopLauncher.class.getPackage().getImplementationTitle();
        if (vendor == null) {
            vendor = System.getProperty("Implementation-Title");
        }
        vendor = vendor.split("\\.")[1];
        Object basePath = "";
        Files.FileType baseFileType = null;
        if (SharedLibraryLoader.os == Os.Windows) {
            basePath = System.getProperties().getProperty("os.name").equals("Windows XP") ? "Application Data/." + vendor + "/" + title + "/" : "AppData/Roaming/." + vendor + "/" + title + "/";
            baseFileType = Files.FileType.External;
        } else if (SharedLibraryLoader.os == Os.MacOsX) {
            basePath = "Library/Application Support/" + title + "/";
            baseFileType = Files.FileType.External;
        } else if (SharedLibraryLoader.os == Os.Linux) {
            Object XDGHome = System.getenv("XDG_DATA_HOME");
            if (XDGHome == null) {
                XDGHome = System.getProperty("user.home") + "/.local/share";
            }
            String titleLinux = title.toLowerCase(Locale.ROOT).replace(" ", "-");
            basePath = (String)XDGHome + "/." + vendor + "/" + titleLinux + "/";
            baseFileType = Files.FileType.Absolute;
        }
        config.setPreferencesConfig((String)basePath, baseFileType);
        SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle((String)basePath + "settings.xml", baseFileType)));
        FileUtils.setDefaultFileProperties(baseFileType, (String)basePath);
        config.setWindowSizeLimits(720, 400, -1, -1);
        Point p = SPDSettings.windowResolution();
        config.setWindowedMode(p.x, p.y);
        config.setMaximized(SPDSettings.windowMaximized());
        DesktopWindowListener listener = new DesktopWindowListener();
        config.setWindowListener(listener);
        config.setWindowIcon("icons/icon_16.png", "icons/icon_32.png", "icons/icon_48.png", "icons/icon_64.png", "icons/icon_128.png", "icons/icon_256.png");
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport()), config);
    }
}

