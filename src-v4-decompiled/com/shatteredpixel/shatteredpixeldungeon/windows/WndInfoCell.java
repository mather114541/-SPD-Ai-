/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;

public class WndInfoCell
extends Window {
    private static final float GAP = 2.0f;
    private static final int WIDTH = 120;

    public static Image cellImage(int cell) {
        int tile = Dungeon.level.map[cell];
        if (Dungeon.level.water[cell]) {
            tile = 29;
        } else if (Dungeon.level.pit[cell]) {
            tile = 0;
        }
        Image customImage = null;
        int x = cell % Dungeon.level.width();
        int y = cell / Dungeon.level.width();
        for (CustomTilemap i : Dungeon.level.customTiles) {
            if (x < i.tileX || x >= i.tileX + i.tileW || y < i.tileY || y >= i.tileY + i.tileH || i.image(x - i.tileX, y - i.tileY) == null) continue;
            customImage = i.image(x - i.tileX, y - i.tileY);
        }
        if (customImage == null) {
            for (CustomTilemap i : Dungeon.level.customTerrain) {
                if (x < i.tileX || x >= i.tileX + i.tileW || y < i.tileY || y >= i.tileY + i.tileH || i.image(x - i.tileX, y - i.tileY) == null) continue;
                customImage = i.image(x - i.tileX, y - i.tileY);
            }
        }
        if (customImage != null) {
            return customImage;
        }
        if (tile == 29) {
            Image water = new Image(Dungeon.level.waterTex());
            water.frame(0, 0, 16, 16);
            return water;
        }
        return DungeonTerrainTilemap.tile(cell, tile);
    }

    public static String cellName(int cell) {
        CustomTilemap customTile = null;
        int x = cell % Dungeon.level.width();
        int y = cell / Dungeon.level.width();
        for (CustomTilemap i : Dungeon.level.customTiles) {
            if (x < i.tileX || x >= i.tileX + i.tileW || y < i.tileY || y >= i.tileY + i.tileH || i.image(x - i.tileX, y - i.tileY) == null) continue;
            customTile = i;
        }
        if (customTile == null) {
            for (CustomTilemap i : Dungeon.level.customTerrain) {
                if (x < i.tileX || x >= i.tileX + i.tileW || y < i.tileY || y >= i.tileY + i.tileH || i.image(x - i.tileX, y - i.tileY) == null) continue;
                customTile = i;
            }
        }
        if (customTile != null) {
            x -= customTile.tileX;
            y -= customTile.tileY;
        }
        if (customTile != null && customTile.name(x, y) != null) {
            return customTile.name(x, y);
        }
        return Dungeon.level.tileName(Dungeon.level.map[cell]);
    }

    public WndInfoCell(int cell) {
        String customDesc;
        CustomTilemap customTile = null;
        int x = cell % Dungeon.level.width();
        int y = cell / Dungeon.level.width();
        for (CustomTilemap i : Dungeon.level.customTiles) {
            if (x < i.tileX || x >= i.tileX + i.tileW || y < i.tileY || y >= i.tileY + i.tileH || i.image(x - i.tileX, y - i.tileY) == null) continue;
            customTile = i;
        }
        if (customTile == null) {
            for (CustomTilemap i : Dungeon.level.customTerrain) {
                if (x < i.tileX || x >= i.tileX + i.tileW || y < i.tileY || y >= i.tileY + i.tileH || i.image(x - i.tileX, y - i.tileY) == null) continue;
                customTile = i;
            }
        }
        if (customTile != null) {
            x -= customTile.tileX;
            y -= customTile.tileY;
        }
        Object desc = "";
        IconTitle titlebar = new IconTitle();
        titlebar.icon(WndInfoCell.cellImage(cell));
        titlebar.label(WndInfoCell.cellName(cell));
        desc = customTile != null ? ((customDesc = customTile.desc(x, y)) != null ? (String)desc + customDesc : (String)desc + Dungeon.level.tileDesc(Dungeon.level.map[cell])) : (String)desc + Dungeon.level.tileDesc(Dungeon.level.map[cell]);
        titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(titlebar);
        RenderedTextBlock info = PixelScene.renderTextBlock(6);
        this.add(info);
        for (Blob blob : Dungeon.level.blobs.values()) {
            if (!Dungeon.level.heroFOV[cell] && !blob.alwaysVisible || blob.volume <= 0 || blob.cur[cell] <= 0 || blob.tileDesc() == null) continue;
            if (((String)desc).length() > 0) {
                desc = (String)desc + "\n\n";
            }
            desc = (String)desc + blob.tileDesc();
        }
        info.text((String)(((String)desc).length() == 0 ? Messages.get(this, "nothing", new Object[0]) : desc));
        info.maxWidth(120);
        info.setPos(titlebar.left(), titlebar.bottom() + 4.0f);
        this.resize(120, (int)info.bottom() + 2);
    }
}

