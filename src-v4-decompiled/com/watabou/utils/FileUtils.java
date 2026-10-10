/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.watabou.utils.Bundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtils {
    private static Files.FileType defaultFileType = null;
    private static String defaultPath = "";

    public static void setDefaultFileProperties(Files.FileType type, String path) {
        defaultFileType = type;
        defaultPath = path;
    }

    public static FileHandle getFileHandle(String name) {
        return FileUtils.getFileHandle(defaultFileType, defaultPath, name);
    }

    public static FileHandle getFileHandle(Files.FileType type, String name) {
        return FileUtils.getFileHandle(type, "", name);
    }

    public static FileHandle getFileHandle(Files.FileType type, String basePath, String name) {
        switch (type) {
            case Classpath: {
                return Gdx.files.classpath(basePath + name);
            }
            case Internal: {
                return Gdx.files.internal(basePath + name);
            }
            case External: {
                return Gdx.files.external(basePath + name);
            }
            case Absolute: {
                return Gdx.files.absolute(basePath + name);
            }
            case Local: {
                return Gdx.files.local(basePath + name);
            }
        }
        return null;
    }

    public static boolean cleanTempFiles() {
        return FileUtils.cleanTempFiles("");
    }

    public static boolean cleanTempFiles(String dirName) {
        FileHandle dir = FileUtils.getFileHandle(dirName);
        boolean foundTemp = false;
        for (FileHandle file : dir.list()) {
            block8: {
                if (file.isDirectory()) {
                    foundTemp = FileUtils.cleanTempFiles(dirName + file.name()) || foundTemp;
                    continue;
                }
                if (file.length() == 0L) {
                    file.delete();
                    continue;
                }
                if (!file.name().endsWith(".spdtmp")) continue;
                FileHandle temp = file;
                FileHandle original = FileUtils.getFileHandle(defaultFileType, "", temp.path().replace(".spdtmp", ""));
                try {
                    FileUtils.bundleFromStream(temp.read());
                    try {
                        FileUtils.bundleFromStream(original.read());
                        if (temp.lastModified() > original.lastModified()) {
                            temp.moveTo(original);
                            break block8;
                        }
                        temp.delete();
                    }
                    catch (Exception e) {
                        temp.moveTo(original);
                    }
                }
                catch (Exception e) {
                    temp.delete();
                }
            }
            foundTemp = true;
        }
        return foundTemp;
    }

    public static boolean fileExists(String name) {
        FileHandle file = FileUtils.getFileHandle(name);
        return file.exists() && !file.isDirectory() && file.length() > 0L;
    }

    public static long fileLength(String name) {
        FileHandle file = FileUtils.getFileHandle(name);
        if (!file.exists() || file.isDirectory()) {
            return 0L;
        }
        return file.length();
    }

    public static boolean deleteFile(String name) {
        return FileUtils.getFileHandle(name).delete();
    }

    public static void overwriteFile(String name, int bytes) {
        byte[] data = new byte[bytes];
        Arrays.fill(data, (byte)1);
        FileUtils.getFileHandle(name).writeBytes(data, false);
    }

    public static boolean dirExists(String name) {
        FileHandle dir = FileUtils.getFileHandle(name);
        return dir.exists() && dir.isDirectory();
    }

    public static boolean deleteDir(String name) {
        FileHandle dir = FileUtils.getFileHandle(name);
        if (dir == null || !dir.isDirectory()) {
            return false;
        }
        return dir.deleteDirectory();
    }

    public static ArrayList<String> filesInDir(String name) {
        FileHandle dir = FileUtils.getFileHandle(name);
        ArrayList<String> result = new ArrayList<String>();
        if (dir != null && dir.isDirectory()) {
            for (FileHandle file : dir.list()) {
                result.add(file.name());
            }
        }
        return result;
    }

    public static Bundle bundleFromFile(String fileName) throws IOException {
        try {
            FileHandle file = FileUtils.getFileHandle(fileName);
            if (!file.exists() || file.isDirectory() || file.length() == 0L) {
                throw new IOException("file does not exist!");
            }
            return FileUtils.bundleFromStream(file.read());
        }
        catch (GdxRuntimeException e) {
            throw new IOException(e);
        }
    }

    private static Bundle bundleFromStream(InputStream input) throws IOException {
        Bundle bundle = Bundle.read(input);
        input.close();
        return bundle;
    }

    public static void bundleToFile(String fileName, Bundle bundle) throws IOException {
        try {
            FileHandle file = FileUtils.getFileHandle(fileName);
            if (file.exists()) {
                FileHandle temp = FileUtils.getFileHandle(fileName + ".spdtmp");
                FileUtils.bundleToStream(temp.write(false), bundle);
                file.delete();
                temp.moveTo(file);
            } else {
                FileUtils.bundleToStream(file.write(false), bundle);
            }
        }
        catch (GdxRuntimeException e) {
            throw new IOException(e);
        }
    }

    private static void bundleToStream(OutputStream output, Bundle bundle) throws IOException {
        Bundle.write(bundle, output);
        output.close();
    }
}

