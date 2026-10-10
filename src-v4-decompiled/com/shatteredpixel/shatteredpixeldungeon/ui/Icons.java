/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public enum Icons {
    ENTER,
    GOLD,
    RANKINGS,
    BADGES,
    NEWS,
    CHANGES,
    PREFS,
    SHPX,
    JOURNAL,
    EXIT,
    DISPLAY,
    DISPLAY_LAND,
    DISPLAY_PORT,
    DATA,
    AUDIO,
    LANGS,
    CONTROLLER,
    KEYBOARD,
    STATS,
    CHALLENGE_GREY,
    SCROLL_GREY,
    SEED,
    LEFTARROW,
    RIGHTARROW,
    CALENDAR,
    CHEVRON,
    SHUFFLE,
    TARGET,
    INFO,
    WARNING,
    UNCHECKED,
    CHECKED,
    CLOSE,
    PLUS,
    REPEAT,
    ARROW,
    CHALLENGE_COLOR,
    SCROLL_COLOR,
    COPY,
    PASTE,
    BACKPACK_LRG,
    TALENT,
    MAGNIFY,
    SNAKE,
    BUFFS,
    CATALOG,
    ALCHEMY,
    GRASS,
    STAIRS,
    STAIRS_CHASM,
    STAIRS_WATER,
    STAIRS_GRASS,
    STAIRS_DARK,
    STAIRS_LARGE,
    STAIRS_TRAPS,
    STAIRS_SECRETS,
    WELL_HEALTH,
    WELL_AWARENESS,
    SACRIFICE_ALTAR,
    DISTANT_WELL,
    SKULL,
    BUSY,
    COMPASS,
    SLEEP,
    ALERT,
    LOST,
    INVESTIGATE,
    DEPTH,
    DEPTH_CHASM,
    DEPTH_WATER,
    DEPTH_GRASS,
    DEPTH_DARK,
    DEPTH_LARGE,
    DEPTH_TRAPS,
    DEPTH_SECRETS,
    CHAL_COUNT,
    COIN_SML,
    ENERGY_SML,
    BACKPACK,
    SEED_POUCH,
    SCROLL_HOLDER,
    WAND_HOLSTER,
    POTION_BANDOLIER,
    LIBGDX,
    ALEKS,
    WATA,
    PUMPKINVOLT,
    CELESTI,
    LUMINE,
    CUBE_CODE,
    PURIGRO,
    ARCNOR,
    ALASTAIR;


    public Image get() {
        return Icons.get(this);
    }

    public static Image get(Icons type) {
        Image icon = new Image("interfaces/icons.png");
        switch (type.ordinal()) {
            case 0: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 0.0f, 16.0f, 16.0f));
                break;
            }
            case 1: {
                icon.frame(icon.texture.uvRectBySize(17.0f, 0.0f, 17.0f, 16.0f));
                break;
            }
            case 2: {
                icon.frame(icon.texture.uvRectBySize(34.0f, 0.0f, 17.0f, 16.0f));
                break;
            }
            case 3: {
                icon.frame(icon.texture.uvRectBySize(51.0f, 0.0f, 16.0f, 16.0f));
                break;
            }
            case 4: {
                icon.frame(icon.texture.uvRectBySize(68.0f, 0.0f, 16.0f, 15.0f));
                break;
            }
            case 5: {
                icon.frame(icon.texture.uvRectBySize(85.0f, 0.0f, 15.0f, 15.0f));
                break;
            }
            case 6: {
                icon.frame(icon.texture.uvRectBySize(102.0f, 0.0f, 14.0f, 14.0f));
                break;
            }
            case 7: {
                icon.frame(icon.texture.uvRectBySize(119.0f, 0.0f, 16.0f, 16.0f));
                break;
            }
            case 8: {
                icon.frame(icon.texture.uvRectBySize(136.0f, 0.0f, 17.0f, 15.0f));
                break;
            }
            case 9: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 16.0f, 15.0f, 11.0f));
                break;
            }
            case 10: {
                if (!PixelScene.landscape()) {
                    return Icons.get(DISPLAY_PORT);
                }
                return Icons.get(DISPLAY_LAND);
            }
            case 12: {
                icon.frame(icon.texture.uvRectBySize(16.0f, 16.0f, 12.0f, 16.0f));
                break;
            }
            case 11: {
                icon.frame(icon.texture.uvRectBySize(32.0f, 16.0f, 16.0f, 12.0f));
                break;
            }
            case 13: {
                icon.frame(icon.texture.uvRectBySize(48.0f, 16.0f, 14.0f, 15.0f));
                break;
            }
            case 14: {
                icon.frame(icon.texture.uvRectBySize(64.0f, 16.0f, 14.0f, 14.0f));
                break;
            }
            case 15: {
                icon.frame(icon.texture.uvRectBySize(80.0f, 16.0f, 14.0f, 11.0f));
                break;
            }
            case 16: {
                icon.frame(icon.texture.uvRectBySize(96.0f, 16.0f, 16.0f, 12.0f));
                break;
            }
            case 17: {
                icon.frame(icon.texture.uvRectBySize(112.0f, 16.0f, 15.0f, 12.0f));
                break;
            }
            case 18: {
                icon.frame(icon.texture.uvRectBySize(128.0f, 16.0f, 16.0f, 13.0f));
                break;
            }
            case 19: {
                icon.frame(icon.texture.uvRectBySize(144.0f, 16.0f, 15.0f, 12.0f));
                break;
            }
            case 20: {
                icon.frame(icon.texture.uvRectBySize(160.0f, 16.0f, 15.0f, 14.0f));
                break;
            }
            case 21: {
                icon.frame(icon.texture.uvRectBySize(176.0f, 16.0f, 15.0f, 10.0f));
                break;
            }
            case 22: {
                icon.frame(icon.texture.uvRectBySize(192.0f, 16.0f, 14.0f, 9.0f));
                break;
            }
            case 23: {
                icon.frame(icon.texture.uvRectBySize(208.0f, 16.0f, 14.0f, 9.0f));
                break;
            }
            case 24: {
                icon.frame(icon.texture.uvRectBySize(224.0f, 16.0f, 15.0f, 12.0f));
                break;
            }
            case 25: {
                icon.frame(icon.texture.uvRectBySize(240.0f, 16.0f, 13.0f, 10.0f));
                break;
            }
            case 26: {
                icon.frame(icon.texture.uvRectBySize(240.0f, 32.0f, 15.0f, 12.0f));
                break;
            }
            case 27: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 32.0f, 16.0f, 16.0f));
                break;
            }
            case 28: {
                icon.frame(icon.texture.uvRectBySize(16.0f, 32.0f, 14.0f, 14.0f));
                break;
            }
            case 29: {
                icon.frame(icon.texture.uvRectBySize(32.0f, 32.0f, 14.0f, 14.0f));
                break;
            }
            case 30: {
                icon.frame(icon.texture.uvRectBySize(48.0f, 32.0f, 12.0f, 12.0f));
                break;
            }
            case 31: {
                icon.frame(icon.texture.uvRectBySize(64.0f, 32.0f, 12.0f, 12.0f));
                break;
            }
            case 32: {
                icon.frame(icon.texture.uvRectBySize(80.0f, 32.0f, 11.0f, 11.0f));
                break;
            }
            case 33: {
                icon.frame(icon.texture.uvRectBySize(96.0f, 32.0f, 11.0f, 11.0f));
                break;
            }
            case 34: {
                icon.frame(icon.texture.uvRectBySize(112.0f, 32.0f, 11.0f, 11.0f));
                break;
            }
            case 35: {
                icon.frame(icon.texture.uvRectBySize(128.0f, 32.0f, 11.0f, 11.0f));
                break;
            }
            case 36: {
                icon.frame(icon.texture.uvRectBySize(144.0f, 32.0f, 15.0f, 12.0f));
                break;
            }
            case 37: {
                icon.frame(icon.texture.uvRectBySize(160.0f, 32.0f, 15.0f, 14.0f));
                break;
            }
            case 38: {
                icon.frame(icon.texture.uvRectBySize(176.0f, 32.0f, 13.0f, 13.0f));
                break;
            }
            case 39: {
                icon.frame(icon.texture.uvRectBySize(192.0f, 32.0f, 13.0f, 13.0f));
                break;
            }
            case 40: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 48.0f, 16.0f, 16.0f));
                break;
            }
            case 41: {
                icon.frame(icon.texture.uvRectBySize(16.0f, 48.0f, 13.0f, 13.0f));
                break;
            }
            case 42: {
                icon.frame(icon.texture.uvRectBySize(32.0f, 48.0f, 14.0f, 14.0f));
                break;
            }
            case 43: {
                icon.frame(icon.texture.uvRectBySize(48.0f, 48.0f, 9.0f, 13.0f));
                break;
            }
            case 44: {
                icon.frame(icon.texture.uvRectBySize(64.0f, 48.0f, 16.0f, 15.0f));
                break;
            }
            case 45: {
                icon.frame(icon.texture.uvRectBySize(80.0f, 48.0f, 13.0f, 16.0f));
                break;
            }
            case 46: {
                icon.frame(icon.texture.uvRectBySize(96.0f, 48.0f, 16.0f, 16.0f));
                break;
            }
            case 47: {
                icon.frame(icon.texture.uvRectBySize(112.0f, 48.0f, 16.0f, 16.0f));
                break;
            }
            case 48: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 49: {
                icon.frame(icon.texture.uvRectBySize(16.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 50: {
                icon.frame(icon.texture.uvRectBySize(32.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 51: {
                icon.frame(icon.texture.uvRectBySize(48.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 52: {
                icon.frame(icon.texture.uvRectBySize(64.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 53: {
                icon.frame(icon.texture.uvRectBySize(80.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 54: {
                icon.frame(icon.texture.uvRectBySize(96.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 55: {
                icon.frame(icon.texture.uvRectBySize(112.0f, 64.0f, 15.0f, 16.0f));
                break;
            }
            case 56: {
                icon.frame(icon.texture.uvRectBySize(128.0f, 64.0f, 16.0f, 16.0f));
                break;
            }
            case 57: {
                icon.frame(icon.texture.uvRectBySize(144.0f, 64.0f, 16.0f, 16.0f));
                break;
            }
            case 58: {
                icon.frame(icon.texture.uvRectBySize(160.0f, 64.0f, 16.0f, 16.0f));
                break;
            }
            case 59: {
                icon.frame(icon.texture.uvRectBySize(176.0f, 64.0f, 16.0f, 16.0f));
                break;
            }
            case 60: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 80.0f, 8.0f, 8.0f));
                break;
            }
            case 61: {
                icon.frame(icon.texture.uvRectBySize(8.0f, 80.0f, 8.0f, 8.0f));
                break;
            }
            case 62: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 88.0f, 7.0f, 5.0f));
                break;
            }
            case 63: {
                icon.frame(icon.texture.uvRectBySize(7.0f, 88.0f, 9.0f, 8.0f));
                break;
            }
            case 64: {
                icon.frame(icon.texture.uvRectBySize(16.0f, 80.0f, 8.0f, 8.0f));
                break;
            }
            case 65: {
                icon.frame(icon.texture.uvRectBySize(24.0f, 80.0f, 8.0f, 8.0f));
                break;
            }
            case 66: {
                icon.frame(icon.texture.uvRectBySize(16.0f, 88.0f, 8.0f, 8.0f));
                break;
            }
            case 67: {
                icon.frame(icon.texture.uvRectBySize(32 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 6.0f, 7.0f));
                break;
            }
            case 68: {
                icon.frame(icon.texture.uvRectBySize(40 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 7.0f, 7.0f));
                break;
            }
            case 69: {
                icon.frame(icon.texture.uvRectBySize(48 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 7.0f, 7.0f));
                break;
            }
            case 70: {
                icon.frame(icon.texture.uvRectBySize(56 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 7.0f, 7.0f));
                break;
            }
            case 71: {
                icon.frame(icon.texture.uvRectBySize(64 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 7.0f, 7.0f));
                break;
            }
            case 72: {
                icon.frame(icon.texture.uvRectBySize(72 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 7.0f, 7.0f));
                break;
            }
            case 73: {
                icon.frame(icon.texture.uvRectBySize(80 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 7.0f, 7.0f));
                break;
            }
            case 74: {
                icon.frame(icon.texture.uvRectBySize(88 + Icons.runTypeOfsX(), 80 + Icons.runTypeOfsY(), 7.0f, 7.0f));
                break;
            }
            case 75: {
                icon.frame(icon.texture.uvRectBySize(160.0f, 80.0f, 7.0f, 7.0f));
                break;
            }
            case 76: {
                icon.frame(icon.texture.uvRectBySize(168.0f, 80.0f, 7.0f, 7.0f));
                break;
            }
            case 77: {
                icon.frame(icon.texture.uvRectBySize(168.0f, 88.0f, 8.0f, 7.0f));
                break;
            }
            case 78: {
                icon.frame(icon.texture.uvRectBySize(176.0f, 80.0f, 10.0f, 10.0f));
                break;
            }
            case 80: {
                icon.frame(icon.texture.uvRectBySize(186.0f, 80.0f, 10.0f, 10.0f));
                break;
            }
            case 79: {
                icon.frame(icon.texture.uvRectBySize(196.0f, 80.0f, 10.0f, 10.0f));
                break;
            }
            case 81: {
                icon.frame(icon.texture.uvRectBySize(206.0f, 80.0f, 10.0f, 10.0f));
                break;
            }
            case 82: {
                icon.frame(icon.texture.uvRectBySize(216.0f, 80.0f, 10.0f, 10.0f));
                break;
            }
            case 83: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 96.0f, 16.0f, 13.0f));
                break;
            }
            case 84: {
                icon.frame(icon.texture.uvRectBySize(16.0f, 96.0f, 16.0f, 13.0f));
                break;
            }
            case 85: {
                icon.frame(icon.texture.uvRectBySize(0.0f, 112.0f, 17.0f, 12.0f));
                break;
            }
            case 86: {
                icon.frame(icon.texture.uvRectBySize(17.0f, 112.0f, 14.0f, 15.0f));
                break;
            }
            case 87: {
                icon.frame(icon.texture.uvRectBySize(32.0f, 96.0f, 32.0f, 32.0f));
                icon.scale.set(PixelScene.align(0.49f));
                break;
            }
            case 88: {
                icon.frame(icon.texture.uvRectBySize(64.0f, 96.0f, 32.0f, 32.0f));
                icon.scale.set(PixelScene.align(0.49f));
                break;
            }
            case 91: {
                icon.frame(icon.texture.uvRectBySize(96.0f, 96.0f, 32.0f, 32.0f));
                icon.scale.set(PixelScene.align(0.49f));
                break;
            }
            case 90: {
                icon.frame(icon.texture.uvRectBySize(128.0f, 96.0f, 32.0f, 32.0f));
                icon.scale.set(PixelScene.align(0.49f));
                break;
            }
            case 89: {
                icon.frame(icon.texture.uvRectBySize(160.0f, 96.0f, 27.0f, 30.0f));
                icon.scale.set(PixelScene.align(0.49f));
                break;
            }
            case 92: {
                icon.frame(icon.texture.uvRectBySize(192.0f, 96.0f, 32.0f, 32.0f));
                icon.scale.set(PixelScene.align(0.49f));
            }
        }
        return icon;
    }

    private static int runTypeOfsX() {
        return Dungeon.daily ? 64 : 0;
    }

    private static int runTypeOfsY() {
        if (Dungeon.daily && Dungeon.dailyReplay || !Dungeon.daily && !Dungeon.customSeedText.isEmpty()) {
            return 8;
        }
        return 0;
    }

    public static Image get(HeroClass cl) {
        switch (cl) {
            case WARRIOR: {
                return new ItemSprite(ItemSpriteSheet.SEAL);
            }
            case MAGE: {
                ItemSprite result = new ItemSprite(ItemSpriteSheet.MAGES_STAFF);
                RectF frame = result.frame();
                frame.top += frame.height() / 8.0f;
                result.frame(frame);
                return result;
            }
            case ROGUE: {
                return new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK);
            }
            case HUNTRESS: {
                return new ItemSprite(ItemSpriteSheet.SPIRIT_BOW);
            }
            case DUELIST: {
                return new ItemSprite(ItemSpriteSheet.RAPIER);
            }
            case CLERIC: {
                return new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME);
            }
        }
        return null;
    }

    public static Image get(Level.Feeling feeling) {
        switch (feeling) {
            default: {
                return Icons.get(DEPTH);
            }
            case CHASM: {
                return Icons.get(DEPTH_CHASM);
            }
            case WATER: {
                return Icons.get(DEPTH_WATER);
            }
            case GRASS: {
                return Icons.get(DEPTH_GRASS);
            }
            case DARK: {
                return Icons.get(DEPTH_DARK);
            }
            case LARGE: {
                return Icons.get(DEPTH_LARGE);
            }
            case TRAPS: {
                return Icons.get(DEPTH_TRAPS);
            }
            case SECRETS: 
        }
        return Icons.get(DEPTH_SECRETS);
    }

    public static Image getLarge(Level.Feeling feeling) {
        switch (feeling) {
            default: {
                return Icons.get(STAIRS);
            }
            case CHASM: {
                return Icons.get(STAIRS_CHASM);
            }
            case WATER: {
                return Icons.get(STAIRS_WATER);
            }
            case GRASS: {
                return Icons.get(STAIRS_GRASS);
            }
            case DARK: {
                return Icons.get(STAIRS_DARK);
            }
            case LARGE: {
                return Icons.get(STAIRS_LARGE);
            }
            case TRAPS: {
                return Icons.get(STAIRS_TRAPS);
            }
            case SECRETS: 
        }
        return Icons.get(STAIRS_SECRETS);
    }
}

