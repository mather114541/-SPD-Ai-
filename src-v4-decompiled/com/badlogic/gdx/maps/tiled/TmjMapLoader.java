/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.BaseTiledMapLoader;
import com.badlogic.gdx.maps.tiled.BaseTmjMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class TmjMapLoader
extends BaseTmjMapLoader<BaseTiledMapLoader.Parameters> {
    public TmjMapLoader() {
        super(new InternalFileHandleResolver());
    }

    public TmjMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TiledMap load(String fileName) {
        return this.load(fileName, new BaseTiledMapLoader.Parameters());
    }

    public TiledMap load(String fileName, BaseTiledMapLoader.Parameters parameter) {
        FileHandle tmjFile = this.resolve(fileName);
        this.root = this.json.parse(tmjFile);
        ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
        Array<FileHandle> textureFiles = this.getDependencyFileHandles(tmjFile);
        for (FileHandle textureFile : textureFiles) {
            Texture texture = new Texture(textureFile, parameter.generateMipMaps);
            texture.setFilter(parameter.textureMinFilter, parameter.textureMagFilter);
            textures.put(textureFile.path(), texture);
        }
        TiledMap map = this.loadTiledMap(tmjFile, parameter, new ImageResolver.DirectImageResolver(textures));
        map.setOwnedResources(textures.values().toArray());
        return map;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle tmjFile, BaseTiledMapLoader.Parameters parameter) {
        this.map = this.loadTiledMap(tmjFile, parameter, new ImageResolver.AssetManagerImageResolver(manager));
    }

    @Override
    public TiledMap loadSync(AssetManager manager, String fileName, FileHandle file, BaseTiledMapLoader.Parameters parameter) {
        return this.map;
    }

    @Override
    protected Array<AssetDescriptor> getDependencyAssetDescriptors(FileHandle tmjFile, TextureLoader.TextureParameter textureParameter) {
        Array<AssetDescriptor> descriptors = new Array<AssetDescriptor>();
        Array<FileHandle> fileHandles = this.getDependencyFileHandles(tmjFile);
        for (FileHandle handle : fileHandles) {
            descriptors.add(new AssetDescriptor<Texture>(handle, Texture.class, textureParameter));
        }
        return descriptors;
    }

    protected Array<FileHandle> getDependencyFileHandles(FileHandle tmjFile) {
        Array<FileHandle> fileHandles = new Array<FileHandle>();
        for (JsonValue tileSet : this.root.get("tilesets")) {
            this.getTileSetDependencyFileHandle(fileHandles, tmjFile, tileSet);
        }
        this.collectImageLayerFileHandles(this.root.get("layers"), tmjFile, fileHandles);
        return fileHandles;
    }

    private void collectImageLayerFileHandles(JsonValue layers, FileHandle tmjFile, Array<FileHandle> fileHandles) {
        if (layers == null) {
            return;
        }
        for (JsonValue layer : layers) {
            String type = layer.getString("type", "");
            if (type.equals("imagelayer")) {
                String source = layer.getString("image", null);
                if (source == null) continue;
                FileHandle handle = TmjMapLoader.getRelativeFileHandle(tmjFile, source);
                fileHandles.add(handle);
                continue;
            }
            if (!type.equals("group")) continue;
            this.collectImageLayerFileHandles(layer.get("layers"), tmjFile, fileHandles);
        }
    }

    protected Array<FileHandle> getTileSetDependencyFileHandle(FileHandle tmjFile, JsonValue tileSet) {
        Array<FileHandle> fileHandles = new Array<FileHandle>();
        return this.getTileSetDependencyFileHandle(fileHandles, tmjFile, tileSet);
    }

    protected Array<FileHandle> getTileSetDependencyFileHandle(Array<FileHandle> fileHandles, FileHandle tmjFile, JsonValue tileSet) {
        FileHandle tsjFile;
        String source = tileSet.getString("source", null);
        if (source != null) {
            tsjFile = TmjMapLoader.getRelativeFileHandle(tmjFile, source);
            tileSet = this.json.parse(tsjFile);
        } else {
            tsjFile = tmjFile;
        }
        if (tileSet.has("image")) {
            String imageSource = tileSet.getString("image");
            FileHandle image = TmjMapLoader.getRelativeFileHandle(tsjFile, imageSource);
            fileHandles.add(image);
        } else {
            JsonValue tiles = tileSet.get("tiles");
            if (tiles != null) {
                for (JsonValue tile : tiles) {
                    String imageSource = tile.getString("image");
                    FileHandle image = TmjMapLoader.getRelativeFileHandle(tsjFile, imageSource);
                    fileHandles.add(image);
                }
            }
        }
        return fileHandles;
    }

    @Override
    protected void addStaticTiles(FileHandle tmjFile, ImageResolver imageResolver, TiledMapTileSet tileSet, JsonValue element, JsonValue tiles, String name, int firstgid, int tilewidth, int tileheight, int spacing, int margin, String source, int offsetX, int offsetY, String imageSource, int imageWidth, int imageHeight, FileHandle image) {
        MapProperties props = tileSet.getProperties();
        if (image != null) {
            TextureRegion texture = imageResolver.getImage(image.path());
            props.put("imagesource", imageSource);
            props.put("imagewidth", imageWidth);
            props.put("imageheight", imageHeight);
            props.put("tilewidth", tilewidth);
            props.put("tileheight", tileheight);
            props.put("margin", margin);
            props.put("spacing", spacing);
            int stopWidth = texture.getRegionWidth() - tilewidth;
            int stopHeight = texture.getRegionHeight() - tileheight;
            int id = firstgid;
            for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
                for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
                    TextureRegion tileRegion = new TextureRegion(texture, x, y, tilewidth, tileheight);
                    int tileId = id++;
                    this.addStaticTiledMapTile(tileSet, tileRegion, tileId, offsetX, offsetY);
                }
            }
        } else {
            for (JsonValue tile : tiles) {
                if (tile.has("image")) {
                    imageSource = tile.getString("image");
                    image = source != null ? TmjMapLoader.getRelativeFileHandle(TmjMapLoader.getRelativeFileHandle(tmjFile, source), imageSource) : TmjMapLoader.getRelativeFileHandle(tmjFile, imageSource);
                }
                TextureRegion texture = imageResolver.getImage(image.path());
                int tileId = firstgid + tile.getInt("id");
                this.addStaticTiledMapTile(tileSet, texture, tileId, offsetX, offsetY);
            }
        }
    }
}

