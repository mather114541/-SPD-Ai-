/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.AtlasTmjMapLoader;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.BaseTiledMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmjMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.XmlReader;

public class TiledMapLoader
extends AsynchronousAssetLoader<TiledMap, BaseTiledMapLoader.Parameters> {
    private final TmxMapLoader tmxMapLoader;
    private final TmjMapLoader tmjMapLoader;
    private final AtlasTmxMapLoader atlasTmxMapLoader;
    private final XmlReader xmlReader;
    private final AtlasTmjMapLoader atlasTmjMapLoader;
    private final JsonReader jsonReader;

    public TiledMapLoader() {
        this(new InternalFileHandleResolver());
    }

    public TiledMapLoader(FileHandleResolver resolver) {
        super(resolver);
        this.tmxMapLoader = new TmxMapLoader(resolver);
        this.tmjMapLoader = new TmjMapLoader(resolver);
        this.atlasTmxMapLoader = new AtlasTmxMapLoader(resolver);
        this.xmlReader = new XmlReader();
        this.atlasTmjMapLoader = new AtlasTmjMapLoader(resolver);
        this.jsonReader = new JsonReader();
    }

    public TiledMap load(String fileName) {
        return this.load(fileName, new BaseTiledMapLoader.Parameters());
    }

    public TiledMap load(String fileName, BaseTiledMapLoader.Parameters parameter) {
        FileHandle file;
        String extension;
        if (parameter == null) {
            parameter = new BaseTiledMapLoader.Parameters();
        }
        if ((extension = (file = this.resolve(fileName)).extension().toLowerCase()).equals("tmx")) {
            if (this.usesAtlas(file)) {
                return this.atlasTmxMapLoader.load(fileName, parameter);
            }
            return this.tmxMapLoader.load(fileName, parameter);
        }
        if (extension.equals("tmj")) {
            if (this.usesAtlas(file)) {
                return this.atlasTmjMapLoader.load(fileName, parameter);
            }
            return this.tmjMapLoader.load(fileName, parameter);
        }
        throw new GdxRuntimeException("Unsupported map format: '" + extension + "' in file: " + fileName);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BaseTiledMapLoader.Parameters parameter) {
        String extension;
        if (parameter == null) {
            parameter = new BaseTiledMapLoader.Parameters();
        }
        if ((extension = file.extension().toLowerCase()).equals("tmx")) {
            if (this.usesAtlas(file)) {
                return this.atlasTmxMapLoader.getDependencies(fileName, file, parameter);
            }
            return this.tmxMapLoader.getDependencies(fileName, file, parameter);
        }
        if (extension.equals("tmj")) {
            if (this.usesAtlas(file)) {
                return this.atlasTmjMapLoader.getDependencies(fileName, file, parameter);
            }
            return this.tmjMapLoader.getDependencies(fileName, file, parameter);
        }
        throw new IllegalArgumentException("Unsupported map format: " + extension);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, BaseTiledMapLoader.Parameters parameter) {
        String extension;
        if (parameter == null) {
            parameter = new BaseTiledMapLoader.Parameters();
        }
        if ((extension = file.extension().toLowerCase()).equals("tmx")) {
            if (this.usesAtlas(file)) {
                this.atlasTmxMapLoader.loadAsync(manager, fileName, file, parameter);
            } else {
                this.tmxMapLoader.loadAsync(manager, fileName, file, parameter);
            }
        } else if (extension.equals("tmj")) {
            if (this.usesAtlas(file)) {
                this.atlasTmjMapLoader.loadAsync(manager, fileName, file, parameter);
            } else {
                this.tmjMapLoader.loadAsync(manager, fileName, file, parameter);
            }
        } else {
            throw new IllegalArgumentException("Unsupported map format: " + extension);
        }
    }

    @Override
    public TiledMap loadSync(AssetManager manager, String fileName, FileHandle file, BaseTiledMapLoader.Parameters parameter) {
        TiledMap map;
        String extension;
        if (parameter == null) {
            parameter = new BaseTiledMapLoader.Parameters();
        }
        if ((extension = file.extension().toLowerCase()).equals("tmx")) {
            map = this.usesAtlas(file) ? this.atlasTmxMapLoader.loadSync(manager, fileName, file, parameter) : this.tmxMapLoader.loadSync(manager, fileName, file, parameter);
        } else if (extension.equals("tmj")) {
            map = this.usesAtlas(file) ? this.atlasTmjMapLoader.loadSync(manager, fileName, file, parameter) : this.tmjMapLoader.loadSync(manager, fileName, file, parameter);
        } else {
            throw new IllegalArgumentException("Unsupported map format: " + extension);
        }
        return map;
    }

    private boolean usesAtlas(FileHandle file) {
        block4: {
            JsonValue root;
            JsonValue properties;
            String extension;
            block3: {
                extension = file.extension().toLowerCase();
                if (!extension.equals("tmx")) break block3;
                XmlReader.Element root2 = this.xmlReader.parse(file);
                XmlReader.Element properties2 = root2.getChildByName("properties");
                if (properties2 == null) break block4;
                for (XmlReader.Element property : properties2.getChildrenByName("property")) {
                    String name = property.getAttribute("name", "");
                    if (!"atlas".equals(name)) continue;
                    return true;
                }
                break block4;
            }
            if (extension.equals("tmj") && (properties = (root = this.jsonReader.parse(file)).get("properties")) != null) {
                for (JsonValue property : properties) {
                    String name = property.getString("name", "");
                    if (!"atlas".equals(name)) continue;
                    return true;
                }
            }
        }
        return false;
    }
}

