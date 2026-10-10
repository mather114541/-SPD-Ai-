/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.StringTokenizer;

public abstract class BaseTiledMapLoader<P extends Parameters>
extends AsynchronousAssetLoader<TiledMap, P> {
    protected static final int FLAG_FLIP_HORIZONTALLY = Integer.MIN_VALUE;
    protected static final int FLAG_FLIP_VERTICALLY = 0x40000000;
    protected static final int FLAG_FLIP_DIAGONALLY = 0x20000000;
    protected static final int MASK_CLEAR = -536870912;
    protected boolean convertObjectToTileSpace;
    protected boolean flipY = true;
    protected int mapTileWidth;
    protected int mapTileHeight;
    protected int mapWidthInPixels;
    protected int mapHeightInPixels;
    protected TiledMap map;
    protected IntMap<MapObject> idToObject;
    protected Array<Runnable> runOnEndOfLoadTiled;
    protected ObjectMap<String, Array<ProjectClassMember>> projectClassInfo;

    public BaseTiledMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    protected abstract Array<AssetDescriptor> getDependencyAssetDescriptors(FileHandle var1, TextureLoader.TextureParameter var2);

    protected abstract TiledMap loadTiledMap(FileHandle var1, P var2, ImageResolver var3);

    @Null
    public IntMap<MapObject> getIdToObject() {
        return this.idToObject;
    }

    protected Object castProperty(String name, String value, String type) {
        if (type == null || "string".equals(type) || "file".equals(type)) {
            return value;
        }
        if (type.equals("int")) {
            return Integer.valueOf(value);
        }
        if (type.equals("float")) {
            return Float.valueOf(value);
        }
        if (type.equals("bool")) {
            return Boolean.valueOf(value);
        }
        if (type.equals("color")) {
            return Color.valueOf(BaseTiledMapLoader.tiledColorToLibGDXColor(value));
        }
        throw new GdxRuntimeException("Wrong type given for property " + name + ", given : " + type + ", supported : string, file, bool, int, float, color");
    }

    protected TiledMapTileLayer.Cell createTileLayerCell(boolean flipHorizontally, boolean flipVertically, boolean flipDiagonally) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        if (flipDiagonally) {
            if (flipHorizontally && flipVertically) {
                cell.setFlipHorizontally(true);
                cell.setRotation(3);
            } else if (flipHorizontally) {
                cell.setRotation(3);
            } else if (flipVertically) {
                cell.setRotation(1);
            } else {
                cell.setFlipVertically(true);
                cell.setRotation(3);
            }
        } else {
            cell.setFlipHorizontally(flipHorizontally);
            cell.setFlipVertically(flipVertically);
        }
        return cell;
    }

    protected static int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    protected static FileHandle getRelativeFileHandle(FileHandle file, String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        FileHandle result = file.parent();
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.equals("..")) {
                result = result.parent();
                continue;
            }
            result = result.child(token);
        }
        return result;
    }

    protected void addStaticTiledMapTile(TiledMapTileSet tileSet, TextureRegion textureRegion, int tileId, float offsetX, float offsetY) {
        StaticTiledMapTile tile = new StaticTiledMapTile(textureRegion);
        tile.setId(tileId);
        tile.setOffsetX(offsetX);
        tile.setOffsetY(this.flipY ? -offsetY : offsetY);
        tileSet.putTile(tileId, tile);
    }

    protected void loadObjectProperty(final MapProperties properties, final String name, String value) {
        try {
            final int id = Integer.parseInt(value);
            Runnable fetch = new Runnable(){

                @Override
                public void run() {
                    MapObject object = BaseTiledMapLoader.this.idToObject.get(id);
                    properties.put(name, object);
                }
            };
            this.runOnEndOfLoadTiled.add(fetch);
        }
        catch (Exception exception) {
            throw new GdxRuntimeException("Error parsing property [\" + name + \"] of type \"object\" with value: [" + value + "]", exception);
        }
    }

    protected void loadBasicProperty(MapProperties properties, String name, String value, String type) {
        Object castValue = this.castProperty(name, value, type);
        properties.put(name, castValue);
    }

    protected void loadProjectFile(String projectFilePath) {
        this.projectClassInfo = new ObjectMap();
        if (projectFilePath == null || projectFilePath.trim().isEmpty()) {
            return;
        }
        FileHandle projectFile = this.resolve(projectFilePath);
        JsonValue projectRoot = new JsonReader().parse(projectFile);
        JsonValue propertyTypes = projectRoot.get("propertyTypes");
        if (propertyTypes == null) {
            return;
        }
        for (JsonValue propertyType : propertyTypes) {
            if (!"class".equals(propertyType.getString("type"))) continue;
            String className = propertyType.getString("name");
            JsonValue members = propertyType.get("members");
            if (members.isEmpty()) continue;
            Array<ProjectClassMember> projectClassMembers = new Array<ProjectClassMember>();
            this.projectClassInfo.put(className, projectClassMembers);
            for (JsonValue member : members) {
                ProjectClassMember projectClassMember = new ProjectClassMember();
                projectClassMember.name = member.getString("name");
                projectClassMember.type = member.getString("type");
                projectClassMember.propertyType = member.getString("propertyType", null);
                projectClassMember.defaultValue = member.get("value");
                projectClassMembers.add(projectClassMember);
            }
        }
    }

    protected void loadJsonClassProperties(String className, MapProperties classProperties, JsonValue classElement) {
        if (this.projectClassInfo == null) {
            throw new GdxRuntimeException("No class information loaded to support class properties. Did you set the 'projectFilePath' parameter?");
        }
        if (this.projectClassInfo.isEmpty()) {
            throw new GdxRuntimeException("No class information available. Did you set the correct Tiled project path in the 'projectFilePath' parameter?");
        }
        Array<ProjectClassMember> projectClassMembers = this.projectClassInfo.get(className);
        if (projectClassMembers == null) {
            throw new GdxRuntimeException("There is no class with name '" + className + "' in given Tiled project file.");
        }
        block8: for (ProjectClassMember projectClassMember : projectClassMembers) {
            String value;
            String propName = projectClassMember.name;
            JsonValue classProp = classElement.get(propName);
            switch (projectClassMember.type) {
                case "object": {
                    value = classProp == null ? projectClassMember.defaultValue.asString() : classProp.asString();
                    this.loadObjectProperty(classProperties, propName, value);
                    continue block8;
                }
                case "class": {
                    if (classProp == null) {
                        classProp = projectClassMember.defaultValue;
                    }
                    MapProperties nestedClassProperties = new MapProperties();
                    String nestedClassName = projectClassMember.propertyType;
                    nestedClassProperties.put("type", nestedClassName);
                    classProperties.put(propName, nestedClassProperties);
                    this.loadJsonClassProperties(nestedClassName, nestedClassProperties, classProp);
                    continue block8;
                }
            }
            value = classProp == null ? projectClassMember.defaultValue.asString() : classProp.asString();
            this.loadBasicProperty(classProperties, propName, value, projectClassMember.type);
        }
    }

    public static String tiledColorToLibGDXColor(String tiledColor) {
        String alpha = tiledColor.length() == 9 ? tiledColor.substring(1, 3) : "ff";
        String color = tiledColor.length() == 9 ? tiledColor.substring(3) : tiledColor.substring(1);
        return color + alpha;
    }

    protected void loadMapPropertiesClassDefaults(String className, MapProperties mapProperties) {
        if (this.projectClassInfo == null) {
            Gdx.app.log("TiledMapLoader", "WARN: There is at least one property of type class or an object with a class defined. Use the 'projectFilePath' parameter to correctly load the default values of a class.");
            this.projectClassInfo = new ObjectMap();
            return;
        }
        if (className == null || !this.projectClassInfo.containsKey(className)) {
            return;
        }
        Array<ProjectClassMember> classMembers = this.projectClassInfo.get(className);
        for (ProjectClassMember classMember : classMembers) {
            String propName = classMember.name;
            if (mapProperties.containsKey(propName)) continue;
            String value = classMember.defaultValue.asString();
            if ("object".equals(classMember.type)) {
                this.loadObjectProperty(mapProperties, propName, value);
                continue;
            }
            this.loadBasicProperty(mapProperties, propName, value, classMember.type);
        }
    }

    protected static class ProjectClassMember {
        public String name;
        public String type;
        public String propertyType;
        public JsonValue defaultValue;

        protected ProjectClassMember() {
        }

        public String toString() {
            return "ProjectClassMember{name='" + this.name + "', type='" + this.type + "', propertyType='" + this.propertyType + "', defaultValue=" + this.defaultValue + "}";
        }
    }

    public static class Parameters
    extends AssetLoaderParameters<TiledMap> {
        public boolean generateMipMaps = false;
        public Texture.TextureFilter textureMinFilter = Texture.TextureFilter.Nearest;
        public Texture.TextureFilter textureMagFilter = Texture.TextureFilter.Nearest;
        public boolean convertObjectToTileSpace = false;
        public boolean flipY = true;
        public String projectFilePath = null;
        public boolean forceTextureFilters = false;
    }
}

