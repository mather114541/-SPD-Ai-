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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.BaseTiledMapLoader;
import com.badlogic.gdx.maps.tiled.BaseTmjMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;

public class AtlasTmjMapLoader
extends BaseTmjMapLoader<BaseTiledMapLoader.Parameters> {
    protected Array<Texture> trackedTextures = new Array();
    protected AtlasResolver atlasResolver;

    public AtlasTmjMapLoader() {
        super(new InternalFileHandleResolver());
    }

    public AtlasTmjMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TiledMap load(String fileName) {
        return this.load(fileName, new BaseTiledMapLoader.Parameters());
    }

    public TiledMap load(String fileName, BaseTiledMapLoader.Parameters parameter) {
        FileHandle tmjFile = this.resolve(fileName);
        this.root = this.json.parse(tmjFile);
        FileHandle atlasFileHandle = this.getAtlasFileHandle(tmjFile);
        TextureAtlas atlas = new TextureAtlas(atlasFileHandle);
        this.atlasResolver = new AtlasResolver.DirectAtlasResolver(atlas);
        TiledMap map = this.loadTiledMap(tmjFile, parameter, this.atlasResolver);
        map.setOwnedResources(new Array<TextureAtlas>(new TextureAtlas[]{atlas}));
        this.setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
        return map;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle tmjFile, BaseTiledMapLoader.Parameters parameter) {
        FileHandle atlasHandle = this.getAtlasFileHandle(tmjFile);
        this.atlasResolver = new AtlasResolver.AssetManagerAtlasResolver(manager, atlasHandle.path());
        this.map = this.loadTiledMap(tmjFile, parameter, this.atlasResolver);
    }

    @Override
    public TiledMap loadSync(AssetManager manager, String fileName, FileHandle file, BaseTiledMapLoader.Parameters parameter) {
        if (parameter != null) {
            this.setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
        }
        return this.map;
    }

    @Override
    protected Array<AssetDescriptor> getDependencyAssetDescriptors(FileHandle tmxFile, TextureLoader.TextureParameter textureParameter) {
        Array<AssetDescriptor> descriptors = new Array<AssetDescriptor>();
        FileHandle atlasFileHandle = this.getAtlasFileHandle(tmxFile);
        if (atlasFileHandle != null) {
            descriptors.add(new AssetDescriptor<TextureAtlas>(atlasFileHandle, TextureAtlas.class));
        }
        return descriptors;
    }

    @Override
    protected void addStaticTiles(FileHandle tmjFile, ImageResolver imageResolver, TiledMapTileSet tileSet, JsonValue element, JsonValue tiles, String name, int firstgid, int tilewidth, int tileheight, int spacing, int margin, String source, int offsetX, int offsetY, String imageSource, int imageWidth, int imageHeight, FileHandle image) {
        TextureAtlas atlas = this.atlasResolver.getAtlas();
        String regionsName = name;
        for (Texture texture : atlas.getTextures()) {
            this.trackedTextures.add(texture);
        }
        MapProperties props = tileSet.getProperties();
        props.put("imagesource", imageSource);
        props.put("imagewidth", imageWidth);
        props.put("imageheight", imageHeight);
        props.put("tilewidth", tilewidth);
        props.put("tileheight", tileheight);
        props.put("margin", margin);
        props.put("spacing", spacing);
        if (imageSource != null && imageSource.length() > 0) {
            int lastgid = firstgid + imageWidth / tilewidth * (imageHeight / tileheight) - 1;
            for (TextureAtlas.AtlasRegion region : atlas.findRegions(regionsName)) {
                int tileId;
                if (region == null || (tileId = firstgid + region.index) < firstgid || tileId > lastgid) continue;
                this.addStaticTiledMapTile(tileSet, region, tileId, offsetX, offsetY);
            }
        }
        for (JsonValue tileElement : tiles) {
            JsonValue imageElement;
            int tileId = firstgid + tileElement.getInt("id", 0);
            TiledMapTile tile = tileSet.getTile(tileId);
            if (tile != null || (imageElement = tileElement.get("image")) == null) continue;
            String regionName = imageElement.asString();
            TextureAtlas.AtlasRegion region = atlas.findRegion(regionName = regionName.substring(0, regionName.lastIndexOf(46)));
            if (region == null) {
                throw new GdxRuntimeException("Tileset atlasRegion not found: " + regionName);
            }
            this.addStaticTiledMapTile(tileSet, region, tileId, offsetX, offsetY);
        }
    }

    protected FileHandle getAtlasFileHandle(FileHandle tmjFile) {
        JsonValue properties = this.root.get("properties");
        String atlasFilePath = null;
        if (properties != null) {
            for (JsonValue property : properties) {
                String name = property.getString("name", "");
                if (!name.startsWith("atlas")) continue;
                atlasFilePath = property.getString("value", "");
                break;
            }
        }
        if (atlasFilePath == null || atlasFilePath.isEmpty()) {
            throw new GdxRuntimeException("The map is missing the 'atlas' property");
        }
        FileHandle fileHandle = AtlasTmjMapLoader.getRelativeFileHandle(tmjFile, atlasFilePath);
        if (!fileHandle.exists()) {
            throw new GdxRuntimeException("The 'atlas' file could not be found: '" + atlasFilePath + "'");
        }
        return fileHandle;
    }

    protected void setTextureFilters(Texture.TextureFilter min, Texture.TextureFilter mag) {
        for (Texture texture : this.trackedTextures) {
            texture.setFilter(min, mag);
        }
        this.trackedTextures.clear();
    }

    static String parseRegionName(String name) {
        if (name.contains("atlas_imagelayer")) {
            int lastSlash = name.lastIndexOf(47);
            return lastSlash >= 0 ? name.substring(lastSlash + 1) : name;
        }
        return name;
    }

    protected static interface AtlasResolver
    extends ImageResolver {
        public TextureAtlas getAtlas();

        public static class AssetManagerAtlasResolver
        implements AtlasResolver {
            private final AssetManager assetManager;
            private final String atlasName;

            public AssetManagerAtlasResolver(AssetManager assetManager, String atlasName) {
                this.assetManager = assetManager;
                this.atlasName = atlasName;
            }

            @Override
            public TextureAtlas getAtlas() {
                return this.assetManager.get(this.atlasName, TextureAtlas.class);
            }

            @Override
            public TextureRegion getImage(String name) {
                String regionName = AtlasTmjMapLoader.parseRegionName(name);
                return this.getAtlas().findRegion(regionName);
            }
        }

        public static class DirectAtlasResolver
        implements AtlasResolver {
            private final TextureAtlas atlas;

            public DirectAtlasResolver(TextureAtlas atlas) {
                this.atlas = atlas;
            }

            @Override
            public TextureAtlas getAtlas() {
                return this.atlas;
            }

            @Override
            public TextureRegion getImage(String name) {
                String regionName = AtlasTmjMapLoader.parseRegionName(name);
                return this.atlas.findRegion(regionName);
            }
        }
    }
}

