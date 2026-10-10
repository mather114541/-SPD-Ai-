/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.Architecture;
import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoadRuntimeException;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SharedLibraryLoader {
    public static Os os;
    public static Architecture.Bitness bitness;
    public static Architecture architecture;
    @Deprecated
    public static boolean isWindows;
    @Deprecated
    public static boolean isLinux;
    @Deprecated
    public static boolean isMac;
    @Deprecated
    public static boolean isIos;
    @Deprecated
    public static boolean isAndroid;
    @Deprecated
    public static boolean isARM;
    @Deprecated
    public static boolean is64Bit;
    private static final HashSet<String> loadedLibraries;
    private static final Random random;
    private String nativesJar;

    public SharedLibraryLoader() {
    }

    static String randomUUID() {
        return new UUID(random.nextLong(), random.nextLong()).toString();
    }

    public SharedLibraryLoader(String nativesJar) {
        this.nativesJar = nativesJar;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String crc(InputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
        }
        CRC32 crc = new CRC32();
        byte[] buffer = new byte[4096];
        try {
            int length;
            while ((length = input.read(buffer)) != -1) {
                crc.update(buffer, 0, length);
            }
        }
        catch (Exception exception) {
        }
        finally {
            SharedLibraryLoader.closeQuietly(input);
        }
        return Long.toString(crc.getValue(), 16);
    }

    public String mapLibraryName(String libraryName) {
        if (os == Os.Android) {
            return libraryName;
        }
        return os.getLibPrefix() + libraryName + architecture.toSuffix() + bitness.toSuffix() + "." + os.getLibExtension();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load(String libraryName) {
        if (os == Os.IOS) {
            return;
        }
        Class<SharedLibraryLoader> clazz = SharedLibraryLoader.class;
        synchronized (SharedLibraryLoader.class) {
            if (SharedLibraryLoader.isLoaded(libraryName)) {
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return;
            }
            String platformName = this.mapLibraryName(libraryName);
            try {
                if (os == Os.Android) {
                    System.loadLibrary(platformName);
                } else {
                    this.loadFile(platformName);
                }
                SharedLibraryLoader.setLoaded(libraryName);
            }
            catch (Throwable ex) {
                throw new SharedLibraryLoadRuntimeException("Couldn't load shared library '" + platformName + "' for target: " + (os == Os.Android ? "Android" : System.getProperty("os.name") + ", " + architecture.name() + ", " + bitness.name().substring(1) + "-bit"), ex);
            }
            return;
        }
    }

    private InputStream readFile(String path) {
        if (this.nativesJar == null) {
            InputStream input = SharedLibraryLoader.class.getResourceAsStream("/" + path);
            if (input == null) {
                throw new SharedLibraryLoadRuntimeException("Unable to read file for extraction: " + path);
            }
            return input;
        }
        try {
            ZipFile file = new ZipFile(this.nativesJar);
            ZipEntry entry = file.getEntry(path);
            if (entry == null) {
                throw new SharedLibraryLoadRuntimeException("Couldn't find '" + path + "' in JAR: " + this.nativesJar);
            }
            return file.getInputStream(entry);
        }
        catch (IOException ex) {
            throw new SharedLibraryLoadRuntimeException("Error reading '" + path + "' in JAR: " + this.nativesJar, ex);
        }
    }

    public File extractFile(String sourcePath, String dirName) throws IOException {
        try {
            File extractedFile;
            String sourceCrc = this.crc(this.readFile(sourcePath));
            if (dirName == null) {
                dirName = sourceCrc;
            }
            if ((extractedFile = this.getExtractedFile(dirName, new File(sourcePath).getName())) == null && (extractedFile = this.getExtractedFile(SharedLibraryLoader.randomUUID(), new File(sourcePath).getName())) == null) {
                throw new SharedLibraryLoadRuntimeException("Unable to find writable path to extract file. Is the user home directory writable?");
            }
            return this.extractFile(sourcePath, sourceCrc, extractedFile);
        }
        catch (RuntimeException ex) {
            File file = new File(System.getProperty("java.library.path"), sourcePath);
            if (file.exists()) {
                return file;
            }
            throw ex;
        }
    }

    public void extractFileTo(String sourcePath, File dir) throws IOException {
        this.extractFile(sourcePath, this.crc(this.readFile(sourcePath)), new File(dir, new File(sourcePath).getName()));
    }

    private File getExtractedFile(String dirName, String fileName) {
        File file2;
        File idealFile = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + dirName, fileName);
        if (this.canWrite(idealFile)) {
            return idealFile;
        }
        try {
            file2 = File.createTempFile(dirName, null);
            if (file2.delete() && this.canWrite(file2 = new File(file2, fileName))) {
                return file2;
            }
        }
        catch (IOException file2) {
            // empty catch block
        }
        file2 = new File(System.getProperty("user.home") + "/.libgdx/" + dirName, fileName);
        if (this.canWrite(file2)) {
            return file2;
        }
        file2 = new File(".temp/" + dirName, fileName);
        if (this.canWrite(file2)) {
            return file2;
        }
        if (System.getenv("APP_SANDBOX_CONTAINER_ID") != null) {
            return idealFile;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean canWrite(File file) {
        File testFile;
        File parent = file.getParentFile();
        if (file.exists()) {
            if (!file.canWrite() || !this.canExecute(file)) {
                return false;
            }
            testFile = new File(parent, SharedLibraryLoader.randomUUID().toString());
        } else {
            parent.mkdirs();
            if (!parent.isDirectory()) {
                return false;
            }
            testFile = file;
        }
        try {
            new FileOutputStream(testFile).close();
            if (!this.canExecute(testFile)) {
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        catch (Throwable ex) {
            boolean bl = false;
            return bl;
        }
        finally {
            testFile.delete();
        }
    }

    private boolean canExecute(File file) {
        try {
            Method canExecute = File.class.getMethod("canExecute", new Class[0]);
            if (((Boolean)canExecute.invoke(file, new Object[0])).booleanValue()) {
                return true;
            }
            Method setExecutable = File.class.getMethod("setExecutable", Boolean.TYPE, Boolean.TYPE);
            setExecutable.invoke(file, true, false);
            return (Boolean)canExecute.invoke(file, new Object[0]);
        }
        catch (Exception exception) {
            return false;
        }
    }

    private File extractFile(String sourcePath, String sourceCrc, File extractedFile) throws IOException {
        String extractedCrc = null;
        if (extractedFile.exists()) {
            try {
                extractedCrc = this.crc(new FileInputStream(extractedFile));
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
            InputStream input = null;
            FileOutputStream output = null;
            try {
                int length;
                input = this.readFile(sourcePath);
                extractedFile.getParentFile().mkdirs();
                output = new FileOutputStream(extractedFile);
                byte[] buffer = new byte[4096];
                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
            }
            catch (IOException ex) {
                try {
                    throw new SharedLibraryLoadRuntimeException("Error extracting file: " + sourcePath + "\nTo: " + extractedFile.getAbsolutePath(), ex);
                }
                catch (Throwable throwable) {
                    SharedLibraryLoader.closeQuietly(input);
                    SharedLibraryLoader.closeQuietly(output);
                    throw throwable;
                }
            }
            SharedLibraryLoader.closeQuietly(input);
            SharedLibraryLoader.closeQuietly(output);
        }
        return extractedFile;
    }

    private void loadFile(String sourcePath) {
        String sourceCrc = this.crc(this.readFile(sourcePath));
        String fileName = new File(sourcePath).getName();
        File file = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + sourceCrc, fileName);
        Throwable ex = this.loadFile(sourcePath, sourceCrc, file);
        if (ex == null) {
            return;
        }
        try {
            file = File.createTempFile(sourceCrc, null);
            if (file.delete() && this.loadFile(sourcePath, sourceCrc, file) == null) {
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        file = new File(System.getProperty("user.home") + "/.libgdx/" + sourceCrc, fileName);
        if (this.loadFile(sourcePath, sourceCrc, file) == null) {
            return;
        }
        file = new File(".temp/" + sourceCrc, fileName);
        if (this.loadFile(sourcePath, sourceCrc, file) == null) {
            return;
        }
        file = new File(System.getProperty("java.library.path"), sourcePath);
        if (file.exists()) {
            System.load(file.getAbsolutePath());
            return;
        }
        throw new SharedLibraryLoadRuntimeException(ex);
    }

    private Throwable loadFile(String sourcePath, String sourceCrc, File extractedFile) {
        try {
            System.load(this.extractFile(sourcePath, sourceCrc, extractedFile).getAbsolutePath());
            return null;
        }
        catch (Throwable ex) {
            return ex;
        }
    }

    public static synchronized void setLoaded(String libraryName) {
        loadedLibraries.add(libraryName);
    }

    public static synchronized boolean isLoaded(String libraryName) {
        return loadedLibraries.contains(libraryName);
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    static {
        bitness = Architecture.Bitness._32;
        architecture = Architecture.x86;
        if (System.getProperty("os.name").contains("Windows")) {
            os = Os.Windows;
        } else if (System.getProperty("os.name").contains("Linux")) {
            os = Os.Linux;
        } else if (System.getProperty("os.name").contains("Mac")) {
            os = Os.MacOsX;
        }
        if (System.getProperty("os.arch").startsWith("arm") || System.getProperty("os.arch").startsWith("aarch64")) {
            architecture = Architecture.ARM;
        } else if (System.getProperty("os.arch").startsWith("riscv")) {
            architecture = Architecture.RISCV;
        } else if (System.getProperty("os.arch").startsWith("loongarch")) {
            architecture = Architecture.LOONGARCH;
        }
        if (System.getProperty("os.arch").contains("64") || System.getProperty("os.arch").startsWith("armv8")) {
            bitness = Architecture.Bitness._64;
        } else if (System.getProperty("os.arch").contains("128")) {
            bitness = Architecture.Bitness._128;
        }
        boolean isMOEiOS = System.getProperty("moe.platform.name") != null;
        String vm = System.getProperty("java.runtime.name");
        if (vm != null && vm.contains("Android Runtime")) {
            os = Os.Android;
            bitness = Architecture.Bitness._32;
            architecture = Architecture.x86;
        }
        if (isMOEiOS || os != Os.Android && os != Os.Windows && os != Os.Linux && os != Os.MacOsX) {
            os = Os.IOS;
            bitness = Architecture.Bitness._32;
            architecture = Architecture.x86;
        }
        isWindows = os == Os.Windows;
        isLinux = os == Os.Linux;
        isMac = os == Os.MacOsX;
        isIos = os == Os.IOS;
        isAndroid = os == Os.Android;
        isARM = architecture == Architecture.ARM;
        is64Bit = bitness == Architecture.Bitness._64;
        loadedLibraries = new HashSet();
        random = new Random();
    }
}

