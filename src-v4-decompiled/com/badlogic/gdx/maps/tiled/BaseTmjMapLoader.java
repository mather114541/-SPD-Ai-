/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextMapObject;
import com.badlogic.gdx.maps.tiled.BaseTiledMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public abstract class BaseTmjMapLoader<P extends BaseTiledMapLoader.Parameters>
extends BaseTiledMapLoader<P> {
    protected JsonReader json = new JsonReader();
    protected JsonValue root;
    protected ObjectMap<String, JsonValue> templateCache;

    public BaseTmjMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle tmjFile, P parameter) {
        this.root = this.json.parse(tmjFile);
        TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
        if (parameter != null) {
            textureParameter.genMipMaps = ((BaseTiledMapLoader.Parameters)parameter).generateMipMaps;
            textureParameter.minFilter = ((BaseTiledMapLoader.Parameters)parameter).textureMinFilter;
            textureParameter.magFilter = ((BaseTiledMapLoader.Parameters)parameter).textureMagFilter;
        }
        return this.getDependencyAssetDescriptors(tmjFile, textureParameter);
    }

    @Override
    protected TiledMap loadTiledMap(FileHandle tmjFile, P parameter, ImageResolver imageResolver) {
        JsonValue properties;
        this.map = new TiledMap();
        this.idToObject = new IntMap();
        this.runOnEndOfLoadTiled = new Array();
        this.templateCache = new ObjectMap();
        if (parameter != null) {
            this.convertObjectToTileSpace = ((BaseTiledMapLoader.Parameters)parameter).convertObjectToTileSpace;
            this.flipY = ((BaseTiledMapLoader.Parameters)parameter).flipY;
            this.loadProjectFile(((BaseTiledMapLoader.Parameters)parameter).projectFilePath);
        } else {
            this.convertObjectToTileSpace = false;
            this.flipY = true;
        }
        String mapOrientation = this.root.getString("orientation", null);
        int mapWidth = this.root.getInt("width", 0);
        int mapHeight = this.root.getInt("height", 0);
        int tileWidth = this.root.getInt("tilewidth", 0);
        int tileHeight = this.root.getInt("tileheight", 0);
        int hexSideLength = this.root.getInt("hexsidelength", 0);
        String staggerAxis = this.root.getString("staggeraxis", null);
        String staggerIndex = this.root.getString("staggerindex", null);
        String mapBackgroundColor = this.root.getString("backgroundcolor", null);
        MapProperties mapProperties = this.map.getProperties();
        if (mapOrientation != null) {
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", mapWidth);
        mapProperties.put("height", mapHeight);
        mapProperties.put("tilewidth", tileWidth);
        mapProperties.put("tileheight", tileHeight);
        mapProperties.put("hexsidelength", hexSideLength);
        if (staggerAxis != null) {
            mapProperties.put("staggeraxis", staggerAxis);
        }
        if (staggerIndex != null) {
            mapProperties.put("staggerindex", staggerIndex);
        }
        if (mapBackgroundColor != null) {
            mapProperties.put("backgroundcolor", mapBackgroundColor);
        }
        this.mapTileWidth = tileWidth;
        this.mapTileHeight = tileHeight;
        this.mapWidthInPixels = mapWidth * tileWidth;
        this.mapHeightInPixels = mapHeight * tileHeight;
        if (mapOrientation != null && "staggered".equals(mapOrientation) && mapHeight > 1) {
            this.mapWidthInPixels += tileWidth / 2;
            this.mapHeightInPixels = this.mapHeightInPixels / 2 + tileHeight / 2;
        }
        if ((properties = this.root.get("properties")) != null) {
            this.loadProperties(this.map.getProperties(), properties);
        }
        JsonValue tileSets = this.root.get("tilesets");
        for (Iterable<JsonValue> element : tileSets) {
            this.loadTileSet((JsonValue)element, tmjFile, imageResolver);
        }
        JsonValue layers = this.root.get("layers");
        for (JsonValue element : layers) {
            this.loadLayer(this.map, this.map.getLayers(), element, tmjFile, imageResolver);
        }
        Array<MapGroupLayer> groups = this.map.getLayers().getByType(MapGroupLayer.class);
        while (groups.notEmpty()) {
            MapGroupLayer group = groups.first();
            groups.removeIndex(0);
            for (MapLayer child : group.getLayers()) {
                child.setParallaxX(child.getParallaxX() * group.getParallaxX());
                child.setParallaxY(child.getParallaxY() * group.getParallaxY());
                if (!(child instanceof MapGroupLayer)) continue;
                groups.add((MapGroupLayer)child);
            }
        }
        for (Runnable runnable : this.runOnEndOfLoadTiled) {
            runnable.run();
        }
        this.runOnEndOfLoadTiled = null;
        return this.map;
    }

    protected void loadLayer(TiledMap map, MapLayers parentLayers, JsonValue element, FileHandle tmjFile, ImageResolver imageResolver) {
        String type;
        switch (type = element.getString("type", "")) {
            case "group": {
                this.loadLayerGroup(map, parentLayers, element, tmjFile, imageResolver);
                break;
            }
            case "tilelayer": {
                this.loadTileLayer(map, parentLayers, element);
                break;
            }
            case "objectgroup": {
                this.loadObjectGroup(map, parentLayers, element, tmjFile);
                break;
            }
            case "imagelayer": {
                this.loadImageLayer(map, parentLayers, element, tmjFile, imageResolver);
            }
        }
    }

    protected void loadLayerGroup(TiledMap map, MapLayers parentLayers, JsonValue element, FileHandle tmjFile, ImageResolver imageResolver) {
        if (element.getString("type", "").equals("group")) {
            JsonValue layers;
            MapGroupLayer groupLayer = new MapGroupLayer();
            this.loadBasicLayerInfo(groupLayer, element);
            JsonValue properties = element.get("properties");
            if (properties != null) {
                this.loadProperties(groupLayer.getProperties(), properties);
            }
            if ((layers = element.get("layers")) != null) {
                for (JsonValue child : layers) {
                    this.loadLayer(map, groupLayer.getLayers(), child, tmjFile, imageResolver);
                }
            }
            for (MapLayer layer : groupLayer.getLayers()) {
                layer.setParent(groupLayer);
            }
            parentLayers.add(groupLayer);
        }
    }

    protected void loadTileLayer(TiledMap map, MapLayers parentLayers, JsonValue element) {
        if (element.getString("type", "").equals("tilelayer")) {
            int width = element.getInt("width", 0);
            int height = element.getInt("height", 0);
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
            this.loadBasicLayerInfo(layer, element);
            int[] ids = BaseTmjMapLoader.getTileIds(element, width, height);
            TiledMapTileSets tileSets = map.getTileSets();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int id = ids[y * width + x];
                    boolean flipHorizontally = (id & Integer.MIN_VALUE) != 0;
                    boolean flipVertically = (id & 0x40000000) != 0;
                    boolean flipDiagonally = (id & 0x20000000) != 0;
                    TiledMapTile tile = tileSets.getTile(id & 0x1FFFFFFF);
                    if (tile == null) continue;
                    TiledMapTileLayer.Cell cell = this.createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
                    cell.setTile(tile);
                    layer.setCell(x, this.flipY ? height - 1 - y : y, cell);
                }
            }
            JsonValue properties = element.get("properties");
            if (properties != null) {
                this.loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
        }
    }

    protected void loadObjectGroup(TiledMap map, MapLayers parentLayers, JsonValue element, FileHandle tmjFile) {
        if (element.getString("type", "").equals("objectgroup")) {
            MapLayer layer = new MapLayer();
            this.loadBasicLayerInfo(layer, element);
            JsonValue properties = element.get("properties");
            if (properties != null) {
                this.loadProperties(layer.getProperties(), properties);
            }
            JsonValue.JsonIterator jsonIterator = element.get("objects").iterator();
            while (jsonIterator.hasNext()) {
                JsonValue objectElement;
                JsonValue elementToLoad = objectElement = (JsonValue)jsonIterator.next();
                if (objectElement.has("template")) {
                    elementToLoad = this.resolveTemplateObject(map, layer, objectElement, tmjFile);
                }
                this.loadObject(map, layer, elementToLoad);
            }
            parentLayers.add(layer);
        }
    }

    protected void loadImageLayer(TiledMap map, MapLayers parentLayers, JsonValue element, FileHandle tmjFile, ImageResolver imageResolver) {
        if (element.getString("type", "").equals("imagelayer")) {
            float x = element.getFloat("offsetx", 0.0f);
            float y = element.getFloat("offsety", 0.0f);
            if (this.flipY) {
                y = (float)this.mapHeightInPixels - y;
            }
            String imageSrc = element.getString("image", "");
            boolean repeatX = element.getInt("repeatx", 0) == 1;
            boolean repeatY = element.getInt("repeaty", 0) == 1;
            TextureRegion texture = null;
            if (!imageSrc.isEmpty()) {
                FileHandle handle = BaseTmjMapLoader.getRelativeFileHandle(tmjFile, imageSrc);
                texture = imageResolver.getImage(handle.path());
                y -= (float)texture.getRegionHeight();
            }
            TiledMapImageLayer layer = new TiledMapImageLayer(texture, x, y, repeatX, repeatY);
            this.loadBasicLayerInfo(layer, element);
            JsonValue properties = element.get("properties");
            if (properties != null) {
                this.loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
        }
    }

    protected void loadBasicLayerInfo(MapLayer layer, JsonValue element) {
        String name = element.getString("name");
        float opacity = element.getFloat("opacity", 1.0f);
        String tintColor = element.getString("tintcolor", "#ffffffff");
        boolean visible = element.getBoolean("visible", true);
        float offsetX = element.getFloat("offsetx", 0.0f);
        float offsetY = element.getFloat("offsety", 0.0f);
        float parallaxX = element.getFloat("parallaxx", 1.0f);
        float parallaxY = element.getFloat("parallaxy", 1.0f);
        layer.setName(name);
        layer.setOpacity(opacity);
        layer.setVisible(visible);
        layer.setOffsetX(offsetX);
        layer.setOffsetY(offsetY);
        layer.setParallaxX(parallaxX);
        layer.setParallaxY(parallaxY);
        layer.setTintColor(Color.valueOf(BaseTmjMapLoader.tiledColorToLibGDXColor(tintColor)));
    }

    protected void loadObject(TiledMap map, MapLayer layer, JsonValue element) {
        this.loadObject(map, layer.getObjects(), element, this.mapHeightInPixels);
    }

    protected void loadObject(TiledMap map, TiledMapTile tile, JsonValue element) {
        this.loadObject(map, tile.getObjects(), element, tile.getTextureRegion().getRegionHeight());
    }

    protected void loadObject(TiledMap map, MapObjects objects, JsonValue element, float heightInPixels) {
        int id;
        String type;
        MapObject object = null;
        float scaleX = this.convertObjectToTileSpace ? 1.0f / (float)this.mapTileWidth : 1.0f;
        float scaleY = this.convertObjectToTileSpace ? 1.0f / (float)this.mapTileHeight : 1.0f;
        float x = element.getFloat("x", 0.0f) * scaleX;
        float y = (this.flipY ? heightInPixels - element.getFloat("y", 0.0f) : element.getFloat("y", 0.0f)) * scaleY;
        float width = element.getFloat("width", 0.0f) * scaleX;
        float height = element.getFloat("height", 0.0f) * scaleY;
        if (element.size > 0) {
            float[] vertices;
            JsonValue child = element.get("polygon");
            if (child != null) {
                vertices = new float[child.size * 2];
                int index = 0;
                for (JsonValue point : child) {
                    vertices[index++] = point.getFloat("x", 0.0f) * scaleX;
                    vertices[index++] = point.getFloat("y", 0.0f) * scaleY * (float)(this.flipY ? -1 : 1);
                }
                Polygon polygon = new Polygon(vertices);
                polygon.setPosition(x, y);
                object = new PolygonMapObject(polygon);
            } else {
                child = element.get("polyline");
                if (child != null) {
                    vertices = new float[child.size * 2];
                    int index = 0;
                    for (JsonValue point : child) {
                        vertices[index++] = point.getFloat("x", 0.0f) * scaleX;
                        vertices[index++] = point.getFloat("y", 0.0f) * scaleY * (float)(this.flipY ? -1 : 1);
                    }
                    Polyline polyline = new Polyline(vertices);
                    polyline.setPosition(x, y);
                    object = new PolylineMapObject(polyline);
                } else if (element.get("ellipse") != null) {
                    object = new EllipseMapObject(x, this.flipY ? y - height : y, width, height);
                } else {
                    child = element.get("point");
                    if (child != null) {
                        object = new PointMapObject(x, this.flipY ? y - height : y);
                    } else {
                        child = element.get("text");
                        if (child != null) {
                            TextMapObject textMapObject = new TextMapObject(x, this.flipY ? y - height : y, width, height, child.getString("text", ""));
                            textMapObject.setFontFamily(child.getString("fontfamily", ""));
                            textMapObject.setPixelSize(child.getInt("pixelSize", 16));
                            textMapObject.setHorizontalAlign(child.getString("halign", "left"));
                            textMapObject.setVerticalAlign(child.getString("valign", "top"));
                            textMapObject.setBold(child.getBoolean("bold", false));
                            textMapObject.setItalic(child.getBoolean("italic", false));
                            textMapObject.setUnderline(child.getBoolean("underline", false));
                            textMapObject.setStrikeout(child.getBoolean("strikeout", false));
                            textMapObject.setWrap(child.getBoolean("wrap", false));
                            textMapObject.setKerning(child.getBoolean("kerning", true));
                            String textColor = child.getString("color", "#000000");
                            textMapObject.setColor(Color.valueOf(BaseTmjMapLoader.tiledColorToLibGDXColor(textColor)));
                            object = textMapObject;
                        }
                    }
                }
            }
        }
        if (object == null) {
            String gid = element.getString("gid", null);
            if (gid != null) {
                int id2 = (int)Long.parseLong(gid);
                boolean flipHorizontally = (id2 & Integer.MIN_VALUE) != 0;
                boolean flipVertically = (id2 & 0x40000000) != 0;
                TiledMapTile tile = map.getTileSets().getTile(id2 & 0x1FFFFFFF);
                TiledMapTileMapObject tiledMapTileMapObject = new TiledMapTileMapObject(tile, flipHorizontally, flipVertically);
                TextureRegion textureRegion = tiledMapTileMapObject.getTextureRegion();
                tiledMapTileMapObject.getProperties().put("gid", id2);
                tiledMapTileMapObject.setX(x);
                tiledMapTileMapObject.setY(this.flipY ? y : y - height);
                float objectWidth = element.getFloat("width", textureRegion.getRegionWidth());
                float objectHeight = element.getFloat("height", textureRegion.getRegionHeight());
                tiledMapTileMapObject.setScaleX(scaleX * (objectWidth / (float)textureRegion.getRegionWidth()));
                tiledMapTileMapObject.setScaleY(scaleY * (objectHeight / (float)textureRegion.getRegionHeight()));
                tiledMapTileMapObject.setRotation(element.getFloat("rotation", 0.0f));
                object = tiledMapTileMapObject;
            } else {
                object = new RectangleMapObject(x, this.flipY ? y - height : y, width, height);
            }
        }
        object.setName(element.getString("name", null));
        String rotation = element.getString("rotation", null);
        if (rotation != null) {
            object.getProperties().put("rotation", Float.valueOf(Float.parseFloat(rotation)));
        }
        if ((type = element.getString("type", null)) != null) {
            object.getProperties().put("type", type);
        }
        if ((id = element.getInt("id", 0)) != 0) {
            object.getProperties().put("id", id);
        }
        object.getProperties().put("x", Float.valueOf(x));
        if (object instanceof TiledMapTileMapObject) {
            object.getProperties().put("y", Float.valueOf(y));
        } else {
            object.getProperties().put("y", Float.valueOf(this.flipY ? y - height : y));
        }
        object.getProperties().put("width", Float.valueOf(width));
        object.getProperties().put("height", Float.valueOf(height));
        object.setVisible(element.getBoolean("visible", true));
        JsonValue properties = element.get("properties");
        if (properties != null) {
            this.loadProperties(object.getProperties(), properties);
        }
        this.loadMapPropertiesClassDefaults(type, object.getProperties());
        this.idToObject.put(id, object);
        objects.add(object);
    }

    protected JsonValue resolveTemplateObject(TiledMap map, MapLayer layer, JsonValue mapElement, FileHandle tmjFile) {
        String tjFileName = mapElement.getString("template");
        JsonValue templateElement = this.templateCache.get(tjFileName);
        if (templateElement == null) {
            try {
                templateElement = this.json.parse(BaseTmjMapLoader.getRelativeFileHandle(tmjFile, tjFileName));
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Error parsing template file: " + tjFileName, e);
            }
            this.templateCache.put(tjFileName, templateElement);
        }
        JsonValue templateObjectElement = templateElement.get("object");
        return this.mergeParentElementWithTemplate(mapElement, templateObjectElement);
    }

    protected JsonValue mergeJsonObject(JsonValue parentObject, JsonValue templateObject) {
        if (templateObject == null) {
            return parentObject;
        }
        if (parentObject == null) {
            return templateObject;
        }
        JsonValue merged = new JsonValue(JsonValue.ValueType.object);
        for (JsonValue child : templateObject) {
            merged.addChild(child.name(), this.cloneElementShallow(child));
        }
        for (JsonValue child : parentObject) {
            merged.setChild(child.name(), this.cloneElementShallow(child));
        }
        return merged;
    }

    protected JsonValue cloneElementShallow(JsonValue src) {
        JsonValue clone;
        switch (src.type()) {
            case stringValue: {
                clone = new JsonValue(src.asString());
                break;
            }
            case doubleValue: {
                clone = new JsonValue(src.asDouble());
                break;
            }
            case longValue: {
                clone = new JsonValue(src.asLong());
                break;
            }
            case booleanValue: {
                clone = new JsonValue(src.asBoolean());
                break;
            }
            case nullValue: {
                clone = new JsonValue((String)null);
                break;
            }
            default: {
                clone = new JsonValue(src);
            }
        }
        clone.setName(src.name());
        return clone;
    }

    protected JsonValue mergeJsonProperties(JsonValue parentProps, JsonValue templateProps) {
        if (templateProps == null) {
            return parentProps;
        }
        if (parentProps == null) {
            return templateProps;
        }
        JsonValue merged = new JsonValue(JsonValue.ValueType.array);
        for (JsonValue property : templateProps) {
            merged.addChild(new JsonValue(property));
        }
        for (JsonValue property : parentProps) {
            String propName = property.getString("name", null);
            if (propName == null) continue;
            for (JsonValue child : merged) {
                if (!propName.equals(child.getString("name", null))) continue;
                child.remove();
                break;
            }
            merged.addChild(new JsonValue(property));
        }
        return merged;
    }

    protected JsonValue mergeParentElementWithTemplate(JsonValue parent, JsonValue template) {
        if (template == null) {
            return parent;
        }
        if (parent == null) {
            return template;
        }
        JsonValue merged = new JsonValue(JsonValue.ValueType.object);
        for (JsonValue child : template) {
            merged.addChild(this.cloneElementShallow(child));
        }
        block9: for (JsonValue child : parent) {
            String key;
            switch (key = child.name()) {
                case "properties": {
                    merged.setChild(key, this.mergeJsonProperties(child, template.get("properties")));
                    continue block9;
                }
                case "text": {
                    merged.setChild(key, this.mergeJsonObject(child, template.get("text")));
                    continue block9;
                }
            }
            merged.setChild(key, this.cloneElementShallow(child));
        }
        return merged;
    }

    private void loadProperties(MapProperties properties, JsonValue element) {
        if (element == null || !"properties".equals(element.name())) {
            return;
        }
        block8: for (JsonValue property : element) {
            String name = property.getString("name", null);
            String value = property.getString("value", null);
            String type = property.getString("type", null);
            if (value == null && !"class".equals(type)) {
                value = property.asString();
            }
            switch (type) {
                case "object": {
                    this.loadObjectProperty(properties, name, value);
                    continue block8;
                }
                case "class": {
                    MapProperties classProperties = new MapProperties();
                    String className = property.getString("propertytype");
                    classProperties.put("type", className);
                    properties.put(name, classProperties);
                    this.loadJsonClassProperties(className, classProperties, property.get("value"));
                    continue block8;
                }
            }
            this.loadBasicProperty(properties, name, value, type);
        }
    }

    public static int[] getTileIds(JsonValue element, int width, int height) {
        int[] ids;
        block15: {
            String encoding;
            block16: {
                JsonValue data;
                block14: {
                    data = element.get("data");
                    encoding = element.getString("encoding", null);
                    if (encoding != null && !encoding.isEmpty() && !encoding.equals("csv")) break block14;
                    ids = data.asIntArray();
                    break block15;
                }
                if (!encoding.equals("base64")) break block16;
                InputStream is = null;
                try {
                    String compression = element.getString("compression", null);
                    byte[] bytes = Base64Coder.decode(data.asString());
                    if (compression == null || compression.isEmpty()) {
                        is = new ByteArrayInputStream(bytes);
                    } else if (compression.equals("gzip")) {
                        is = new BufferedInputStream(new GZIPInputStream((InputStream)new ByteArrayInputStream(bytes), bytes.length));
                    } else if (compression.equals("zlib")) {
                        is = new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes)));
                    } else {
                        throw new GdxRuntimeException("Unrecognised compression (" + compression + ") for TMJ Layer Data");
                    }
                    byte[] temp = new byte[4];
                    ids = new int[width * height];
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            int read;
                            int curr;
                            for (read = is.read(temp); read < temp.length && (curr = is.read(temp, read, temp.length - read)) != -1; read += curr) {
                            }
                            if (read != temp.length) {
                                throw new GdxRuntimeException("Error Reading TMJ Layer Data: Premature end of tile data");
                            }
                            ids[y * width + x] = BaseTmjMapLoader.unsignedByteToInt(temp[0]) | BaseTmjMapLoader.unsignedByteToInt(temp[1]) << 8 | BaseTmjMapLoader.unsignedByteToInt(temp[2]) << 16 | BaseTmjMapLoader.unsignedByteToInt(temp[3]) << 24;
                        }
                    }
                }
                catch (IOException e) {
                    try {
                        throw new GdxRuntimeException("Error Reading TMJ Layer Data - IOException: " + e.getMessage());
                    }
                    catch (Throwable throwable) {
                        StreamUtils.closeQuietly(is);
                        throw throwable;
                    }
                }
                StreamUtils.closeQuietly(is);
                break block15;
            }
            throw new GdxRuntimeException("Unrecognised encoding (" + encoding + ") for TMJ Layer Data");
        }
        return ids;
    }

    /*
     * Unable to fully structure code
     */
    protected void loadTileSet(JsonValue element, FileHandle tmjFile, ImageResolver imageResolver) {
        if (element.getString("firstgid") != null) {
            firstgid = element.getInt("firstgid", 1);
            imageSource = "";
            imageWidth = 0;
            imageHeight = 0;
            image = null;
            source = element.getString("source", null);
            if (source != null) {
                tsj = BaseTmjMapLoader.getRelativeFileHandle(tmjFile, source);
                try {
                    element = this.json.parse(tsj);
                    if (!element.has("image")) ** GOTO lbl25
                    imageSource = element.getString("image");
                    imageWidth = element.getInt("imagewidth", 0);
                    imageHeight = element.getInt("imageheight", 0);
                    image = BaseTmjMapLoader.getRelativeFileHandle(tsj, imageSource);
                }
                catch (SerializationException e) {
                    throw new GdxRuntimeException("Error parsing external tileSet.");
                }
            } else if (element.has("image")) {
                imageSource = element.getString("image");
                imageWidth = element.getInt("imagewidth", 0);
                imageHeight = element.getInt("imageheight", 0);
                image = BaseTmjMapLoader.getRelativeFileHandle(tmjFile, imageSource);
            }
lbl25:
            // 5 sources

            name = element.getString("name", null);
            tilewidth = element.getInt("tilewidth", 0);
            tileheight = element.getInt("tileheight", 0);
            spacing = element.getInt("spacing", 0);
            margin = element.getInt("margin", 0);
            offset = element.get("tileoffset");
            offsetX = 0;
            offsetY = 0;
            if (offset != null) {
                offsetX = offset.getInt("x", 0);
                offsetY = offset.getInt("y", 0);
            }
            tileSet = new TiledMapTileSet();
            tileSet.setName(name);
            tileSetProperties = tileSet.getProperties();
            properties = element.get("properties");
            if (properties != null) {
                this.loadProperties(tileSetProperties, properties);
            }
            tileSetProperties.put("firstgid", firstgid);
            tiles = element.get("tiles");
            if (tiles == null) {
                tiles = new JsonValue(JsonValue.ValueType.array);
            }
            this.addStaticTiles(tmjFile, imageResolver, tileSet, element, tiles, name, firstgid, tilewidth, tileheight, spacing, margin, source, offsetX, offsetY, imageSource, imageWidth, imageHeight, image);
            animatedTiles = new Array<AnimatedTiledMapTile>();
            for (JsonValue tileElement : tiles) {
                localtid = tileElement.getInt("id", 0);
                tile = tileSet.getTile(firstgid + localtid);
                if (tile == null) continue;
                animatedTile = this.createAnimatedTile(tileSet, tile, tileElement, firstgid);
                if (animatedTile != null) {
                    animatedTiles.add(animatedTile);
                    tile = animatedTile;
                }
                this.addTileProperties(tile, tileElement);
                this.addTileObjectGroup(tile, tileElement);
            }
            for (AnimatedTiledMapTile animatedTile : animatedTiles) {
                tileSet.putTile(animatedTile.getId(), animatedTile);
            }
            this.map.getTileSets().addTileSet(tileSet);
        }
    }

    protected abstract void addStaticTiles(FileHandle var1, ImageResolver var2, TiledMapTileSet var3, JsonValue var4, JsonValue var5, String var6, int var7, int var8, int var9, int var10, int var11, String var12, int var13, int var14, String var15, int var16, int var17, FileHandle var18);

    private void addTileProperties(TiledMapTile tile, JsonValue tileElement) {
        JsonValue properties;
        String type;
        String probability;
        String terrain = tileElement.getString("terrain", null);
        MapProperties tileProperties = tile.getProperties();
        if (terrain != null) {
            tileProperties.put("terrain", terrain);
        }
        if ((probability = tileElement.getString("probability", null)) != null) {
            tileProperties.put("probability", probability);
        }
        if ((type = tileElement.getString("type", null)) != null) {
            tileProperties.put("type", type);
        }
        if ((properties = tileElement.get("properties")) != null) {
            this.loadProperties(tileProperties, properties);
        }
        this.loadMapPropertiesClassDefaults(type, tileProperties);
    }

    private void addTileObjectGroup(TiledMapTile tile, JsonValue tileElement) {
        JsonValue objectgroupElement = tileElement.get("objectgroup");
        if (objectgroupElement != null) {
            for (JsonValue objectElement : objectgroupElement.get("objects")) {
                this.loadObject(this.map, tile, objectElement);
            }
        }
    }

    protected AnimatedTiledMapTile createAnimatedTile(TiledMapTileSet tileSet, TiledMapTile tile, JsonValue tileElement, int firstgid) {
        JsonValue animationElement = tileElement.get("animation");
        if (animationElement != null) {
            Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
            IntArray intervals = new IntArray();
            for (JsonValue frameValue : animationElement) {
                staticTiles.add((StaticTiledMapTile)tileSet.getTile(firstgid + frameValue.getInt("tileid")));
                intervals.add(frameValue.getInt("duration"));
            }
            AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
            animatedTile.setId(tile.getId());
            return animatedTile;
        }
        return null;
    }
}

