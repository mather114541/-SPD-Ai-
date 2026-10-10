/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.shatteredpixel.shatteredpixeldungeon.desktop.DesktopLauncher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class DesktopLaunchValidator {
    public static boolean verifyValidJVMState(String[] args) {
        if (SharedLibraryLoader.os == Os.MacOsX) {
            if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + ManagementFactory.getRuntimeMXBean().getName().split("@")[0]))) {
                return true;
            }
            if ("true".equals(System.getProperty("shpdRelaunched"))) {
                System.err.println("Error: Could not verify new process is running on the first thread. Trying to run the game anyway...");
                return true;
            }
            String sep = System.getProperty("file.separator");
            ArrayList<String> jvmArgs = new ArrayList<String>();
            jvmArgs.add(System.getProperty("java.home") + sep + "bin" + sep + "java");
            jvmArgs.add("-XstartOnFirstThread");
            jvmArgs.add("-DshpdRelaunched=true");
            jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
            jvmArgs.add("-cp");
            jvmArgs.add(System.getProperty("java.class.path"));
            jvmArgs.add(DesktopLauncher.class.getName());
            System.err.println("Error: ShatteredPD must start on the first thread in order to work on macOS.");
            System.err.println("  To avoid this error, run the game with the \"-XstartOnFirstThread\" argument");
            System.err.println("  Now attempting to relaunch the game on the first thread automatically:\n");
            try {
                String line;
                Process process = new ProcessBuilder(jvmArgs).redirectErrorStream(true).start();
                BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = out.readLine()) != null) {
                    if (line.toLowerCase().startsWith("error")) {
                        System.err.println(line);
                        continue;
                    }
                    System.out.println(line);
                }
                process.waitFor();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
}

