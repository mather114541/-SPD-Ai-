/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.FerretTuft;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FloatingText
extends RenderedTextBlock {
    private static final float LIFESPAN = 1.0f;
    private static final float DISTANCE = 16.0f;
    public static final int ICON_WIDTH = 7;
    public static final int ICON_HEIGHT = 8;
    public static TextureFilm iconFilm = new TextureFilm("effects/text_icons.png", 7, 8);
    public static int NO_ICON = -1;
    public static int PHYS_DMG = 0;
    public static int PHYS_DMG_NO_BLOCK = 1;
    public static int MAGIC_DMG = 2;
    public static int PICK_DMG = 3;
    public static int HUNGER = 5;
    public static int BURNING = 6;
    public static int SHOCKING = 7;
    public static int FROST = 8;
    public static int WATER = 9;
    public static int BLEEDING = 10;
    public static int TOXIC = 11;
    public static int CORROSION = 12;
    public static int POISON = 13;
    public static int OOZE = 14;
    public static int DEFERRED = 15;
    public static int CORRUPTION = 16;
    public static int AMULET = 17;
    public static int HEALING = 18;
    public static int SHIELDING = 19;
    public static int EXPERIENCE = 20;
    public static int STRENGTH = 21;
    public static int GOLD = 23;
    public static int ENERGY = 24;
    public static int HIT_WEP = 36;
    public static int HIT_ARM = 37;
    public static int HIT_BLS = 38;
    public static int HIT_HEX = 39;
    public static int HIT_DAZE = 40;
    public static int HIT_ACC = 41;
    public static int HIT_EVA = 42;
    public static int HIT_LIQ = 43;
    public static int HIT_DANCE = 44;
    public static int HIT_SUPR = 45;
    public static int HIT_PRES = 46;
    public static int HIT_MOMEN = 47;
    public static int MISS_WEP = 72;
    public static int MISS_ARM = 73;
    public static int MISS_BLS = 74;
    public static int MISS_HEX = 75;
    public static int MISS_DAZE = 76;
    public static int MISS_ACC = 77;
    public static int MISS_EVA = 78;
    public static int MISS_LIQ = 79;
    public static int MISS_DEF = 80;
    public static int MISS_TUFT = 81;
    public static int MISS_RUN = 82;
    private Image icon;
    private boolean iconLeft;
    private float timeLeft;
    private int key = -1;
    private static final SparseArray<ArrayList<FloatingText>> stacks = new SparseArray();

    public FloatingText() {
        super(9 * PixelScene.defaultZoom);
        this.setHightlighting(false);
    }

    @Override
    public void update() {
        super.update();
        if (this.timeLeft >= 0.0f) {
            float f;
            this.timeLeft -= Game.elapsed;
            if (f <= 0.0f) {
                this.kill();
            } else {
                float p = this.timeLeft / 1.0f;
                this.alpha(p > 0.5f ? 1.0f : p * 2.0f);
                float yMove = 16.0f * Game.elapsed;
                this.y -= yMove;
                for (RenderedText t : this.words) {
                    t.y -= yMove;
                }
                if (this.icon != null) {
                    this.icon.alpha(p > 0.5f ? 1.0f : p * 2.0f);
                    this.icon.y -= yMove;
                }
            }
        }
    }

    @Override
    protected synchronized void layout() {
        super.layout();
        if (this.icon != null) {
            this.icon.x = this.iconLeft ? this.left() : this.left() + this.width() - this.icon.width();
            this.icon.x = PixelScene.align(Camera.main, this.icon.x);
            this.icon.y = PixelScene.align(Camera.main, this.top());
        }
    }

    @Override
    public float width() {
        float width = super.width();
        if (this.icon != null) {
            width += this.icon.width() - 0.5f;
        }
        return width;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void kill() {
        if (this.key != -1) {
            SparseArray<ArrayList<FloatingText>> sparseArray = stacks;
            synchronized (sparseArray) {
                ((ArrayList)stacks.get(this.key)).remove(this);
            }
            this.key = -1;
        }
        super.kill();
    }

    @Override
    public void destroy() {
        this.kill();
        super.destroy();
    }

    public void reset(float x, float y, String text, int color, int iconIdx, boolean left) {
        this.revive();
        this.zoom(1.0f / (float)PixelScene.defaultZoom);
        this.text(text);
        this.hardlight(color);
        if (iconIdx != NO_ICON) {
            this.icon = new Image("effects/text_icons.png");
            this.icon.frame(iconFilm.get(iconIdx));
            this.add(this.icon);
            this.iconLeft = left;
            if (this.iconLeft) {
                this.align(3);
            }
        } else {
            this.icon = null;
        }
        this.setPos(PixelScene.align(Camera.main, x - this.width() / 2.0f), PixelScene.align(Camera.main, y - this.height()));
        this.timeLeft = 1.0f;
    }

    public static void show(float x, float y, String text, int color) {
        FloatingText.show(x, y, -1, text, color, -1, false);
    }

    public static void show(float x, float y, int key, String text, int color) {
        FloatingText.show(x, y, key, text, color, -1, false);
    }

    public static void show(final float x, final float y, final int key, final String text, final int color, final int iconIdx, final boolean left) {
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                FloatingText txt = GameScene.status();
                if (txt != null) {
                    txt.reset(x, y, text, color, iconIdx, left);
                    if (key != -1) {
                        FloatingText.push(txt, key);
                    }
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void push(FloatingText txt, int key) {
        SparseArray<ArrayList<FloatingText>> sparseArray = stacks;
        synchronized (sparseArray) {
            txt.key = key;
            ArrayList<FloatingText> stack = (ArrayList<FloatingText>)stacks.get(key);
            if (stack == null) {
                stack = new ArrayList<FloatingText>();
                stacks.put(key, stack);
            }
            if (stack.size() > 0) {
                FloatingText below = txt;
                int numBelow = 0;
                for (int aboveIndex = stack.size() - 1; aboveIndex >= 0; --aboveIndex) {
                    ++numBelow;
                    FloatingText above = (FloatingText)stack.get(aboveIndex);
                    if (!(above.bottom() + 4.0f > below.top())) break;
                    above.setPos(above.left(), below.top() - above.height() - 4.0f);
                    above.timeLeft = Math.min(above.timeLeft, 1.0f - (float)numBelow / 5.0f);
                    above.timeLeft = Math.max(above.timeLeft, 0.0f);
                    below = above;
                }
            }
            stack.add(txt);
        }
    }

    public static int getHitReasonIcon(Char attacker, float accRoll, Char defender, float defRoll) {
        final HashMap<Integer, Float> hitReasons = new HashMap<Integer, Float>();
        if (defRoll == 0.0f && defender.buff(GuidingLight.Illuminated.class) != null) {
            return HIT_BLS;
        }
        if (accRoll == (float)Char.INFINITE_ACCURACY && attacker.invisible > 0) {
            return HIT_SUPR;
        }
        if (defRoll == 0.0f && defender instanceof Mob && ((Mob)defender).surprisedBy(attacker)) {
            return HIT_SUPR;
        }
        if (accRoll == (float)Char.INFINITE_ACCURACY && attacker.buff(Talent.PreciseAssaultTracker.class) != null) {
            return HIT_PRES;
        }
        if (accRoll == (float)Char.INFINITE_ACCURACY && attacker.buff(Talent.LiquidAgilACCTracker.class) != null) {
            return HIT_LIQ;
        }
        KindOfWeapon wep = null;
        if (attacker instanceof Hero) {
            wep = ((Hero)attacker).belongings.attackingWeapon();
        }
        if (attacker instanceof MirrorImage) {
            wep = Dungeon.hero.belongings.weapon();
        }
        if (attacker instanceof Statue) {
            wep = ((Statue)attacker).weapon();
        }
        if (attacker instanceof DriedRose.GhostHero) {
            wep = ((DriedRose.GhostHero)attacker).weapon();
        }
        Armor arm = null;
        if (defender instanceof Hero) {
            arm = ((Hero)defender).belongings.armor();
        }
        if (defender instanceof PrismaticImage) {
            arm = Dungeon.hero.belongings.armor();
        }
        if (defender instanceof ArmoredStatue) {
            arm = ((ArmoredStatue)defender).armor();
        }
        if (defender instanceof DriedRose.GhostHero) {
            arm = ((DriedRose.GhostHero)defender).armor();
        }
        if (defRoll == 0.0f && arm != null && arm.hasGlyph(Stone.class, defender)) {
            return HIT_ARM;
        }
        if (wep != null && wep.accuracyFactor(attacker, defender) > 1.0f) {
            hitReasons.put(HIT_WEP, Float.valueOf(wep.accuracyFactor(attacker, defender)));
        }
        float blessBoost = 1.0f;
        if (attacker.buff(ChampionEnemy.class) != null && attacker.buff(ChampionEnemy.class).evasionAndAccuracyFactor() > 1.0f) {
            blessBoost *= attacker.buff(ChampionEnemy.class).evasionAndAccuracyFactor();
        }
        if (attacker.buff(Bless.class) != null) {
            blessBoost *= 1.25f;
        }
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.BLESS) && attacker.alignment == Char.Alignment.ALLY) {
            blessBoost *= 1.01f + 0.02f * (float)Dungeon.hero.pointsInTalent(Talent.BLESS);
        }
        if (blessBoost > 1.0f) {
            hitReasons.put(HIT_BLS, Float.valueOf(blessBoost));
        }
        if (RingOfAccuracy.accuracyMultiplier(attacker) > 1.0f) {
            hitReasons.put(HIT_ACC, Float.valueOf(RingOfAccuracy.accuracyMultiplier(attacker)));
        }
        if (attacker.buff(Scimitar.SwordDance.class) != null) {
            hitReasons.put(HIT_DANCE, Float.valueOf(1.5f));
        }
        if (!(wep instanceof MissileWeapon)) {
            if (attacker instanceof Hero && ((Hero)attacker).hasTalent(Talent.PRECISE_ASSAULT) && ((Hero)attacker).heroClass != HeroClass.DUELIST) {
                hitReasons.put(HIT_PRES, Float.valueOf(0.1f * (float)Dungeon.hero.pointsInTalent(Talent.PRECISE_ASSAULT)));
            }
            if (attacker.buff(Talent.PreciseAssaultTracker.class) != null) {
                hitReasons.put(HIT_PRES, Float.valueOf(Dungeon.hero.pointsInTalent(Talent.PRECISE_ASSAULT) == 2 ? 5.0f : 2.0f));
            } else if (attacker.buff(Talent.LiquidAgilACCTracker.class) != null) {
                hitReasons.put(HIT_LIQ, Float.valueOf(3.0f));
            }
        } else if (attacker.buff(Momentum.class) != null && attacker.buff(Momentum.class).freerunning() && ((Hero)attacker).hasTalent(Talent.PROJECTILE_MOMENTUM)) {
            hitReasons.put(HIT_MOMEN, Float.valueOf(1.0f + (float)((Hero)attacker).pointsInTalent(Talent.PROJECTILE_MOMENTUM) / 2.0f));
        }
        if (defender.buff(Hex.class) != null) {
            hitReasons.put(HIT_HEX, Float.valueOf(0.8f));
        }
        if (defender.buff(Daze.class) != null) {
            hitReasons.put(HIT_DAZE, Float.valueOf(0.5f));
        }
        if (RingOfEvasion.evasionMultiplier(defender) < 1.0f) {
            hitReasons.put(HIT_EVA, Float.valueOf(RingOfEvasion.evasionMultiplier(defender)));
        }
        if (arm != null && arm.evasionFactor(defender, 100.0f) < 100.0f) {
            Armor.testingNoArmDefSkill = true;
            int baseDef = defender.defenseSkill(attacker);
            Armor.testingNoArmDefSkill = false;
            hitReasons.put(HIT_ARM, Float.valueOf((float)defender.defenseSkill(attacker) / (float)baseDef));
        }
        if (defender.paralysed > 0) {
            if (defender instanceof Hero) {
                hitReasons.put(HIT_SUPR, Float.valueOf(0.5f));
            } else {
                return HIT_SUPR;
            }
        }
        ArrayList sortedReasons = new ArrayList(hitReasons.keySet());
        Collections.sort(sortedReasons, new Comparator<Integer>(){

            @Override
            public int compare(Integer a, Integer b) {
                float a1 = ((Float)hitReasons.get(a)).floatValue() >= 1.0f ? ((Float)hitReasons.get(a)).floatValue() : 1.0f / ((Float)hitReasons.get(a)).floatValue();
                float b1 = ((Float)hitReasons.get(b)).floatValue() >= 1.0f ? ((Float)hitReasons.get(b)).floatValue() : 1.0f / ((Float)hitReasons.get(b)).floatValue();
                return (int)Math.signum(b1 - a1);
            }
        });
        for (Integer reason : sortedReasons) {
            if (((Float)hitReasons.get(reason)).floatValue() >= 1.0f) {
                accRoll /= ((Float)hitReasons.get(reason)).floatValue();
            } else {
                defRoll /= ((Float)hitReasons.get(reason)).floatValue();
            }
            if (!(accRoll < defRoll)) continue;
            return reason;
        }
        return -1;
    }

    public static int getMissReasonIcon(Char attacker, float accRoll, Char defender, float defRoll) {
        final HashMap<Integer, Float> missReasons = new HashMap<Integer, Float>();
        if (defRoll == (float)Char.INFINITE_EVASION && defender.buff(Talent.LiquidAgilEVATracker.class) != null) {
            return MISS_LIQ;
        }
        KindOfWeapon wep = null;
        if (attacker instanceof Hero) {
            wep = ((Hero)attacker).belongings.attackingWeapon();
        }
        if (attacker instanceof MirrorImage) {
            wep = Dungeon.hero.belongings.weapon();
        }
        if (attacker instanceof Statue) {
            wep = ((Statue)attacker).weapon();
        }
        if (attacker instanceof DriedRose.GhostHero) {
            wep = ((DriedRose.GhostHero)attacker).weapon();
        }
        Armor arm = null;
        if (defender instanceof Hero) {
            arm = ((Hero)defender).belongings.armor();
        }
        if (defender instanceof PrismaticImage) {
            arm = Dungeon.hero.belongings.armor();
        }
        if (defender instanceof ArmoredStatue) {
            arm = ((ArmoredStatue)defender).armor();
        }
        if (defender instanceof DriedRose.GhostHero) {
            arm = ((DriedRose.GhostHero)defender).armor();
        }
        float blessBoost = 1.0f;
        if (defender.buff(ChampionEnemy.class) != null && defender.buff(ChampionEnemy.class).evasionAndAccuracyFactor() > 1.0f) {
            blessBoost *= defender.buff(ChampionEnemy.class).evasionAndAccuracyFactor();
        }
        if (defender.buff(Bless.class) != null) {
            blessBoost *= 1.25f;
        }
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.BLESS) && defender.alignment == Char.Alignment.ALLY) {
            blessBoost *= 1.01f + 0.02f * (float)Dungeon.hero.pointsInTalent(Talent.BLESS);
        }
        if (blessBoost > 1.0f) {
            missReasons.put(MISS_BLS, Float.valueOf(blessBoost));
        }
        if (FerretTuft.evasionMultiplier() > 1.0f) {
            missReasons.put(MISS_TUFT, Float.valueOf(FerretTuft.evasionMultiplier()));
        }
        if (RingOfEvasion.evasionMultiplier(defender) > 1.0f) {
            missReasons.put(MISS_EVA, Float.valueOf(RingOfEvasion.evasionMultiplier(defender)));
        }
        if (defender.buff(Quarterstaff.DefensiveStance.class) != null) {
            missReasons.put(MISS_DEF, Float.valueOf(3.0f));
        }
        if (arm != null && arm.evasionFactor(defender, 100.0f) > 100.0f) {
            Armor.testingNoArmDefSkill = true;
            int baseDef = defender.defenseSkill(attacker);
            Armor.testingNoArmDefSkill = false;
            if (defender.buff(Momentum.class) != null) {
                missReasons.put(MISS_RUN, Float.valueOf((float)defender.defenseSkill(attacker) / (float)baseDef));
            } else {
                missReasons.put(MISS_ARM, Float.valueOf((float)defender.defenseSkill(attacker) / (float)baseDef));
            }
        }
        if (defender.buff(Talent.LiquidAgilEVATracker.class) != null) {
            missReasons.put(MISS_LIQ, Float.valueOf(3.0f));
        }
        if (wep != null && wep.accuracyFactor(attacker, defender) < 1.0f) {
            missReasons.put(MISS_WEP, Float.valueOf(wep.accuracyFactor(attacker, defender)));
        }
        if (attacker.buff(Hex.class) != null) {
            missReasons.put(MISS_HEX, Float.valueOf(0.8f));
        }
        if (attacker.buff(Daze.class) != null) {
            missReasons.put(MISS_DAZE, Float.valueOf(0.5f));
        }
        if (RingOfAccuracy.accuracyMultiplier(attacker) < 1.0f) {
            missReasons.put(MISS_ACC, Float.valueOf(RingOfAccuracy.accuracyMultiplier(attacker)));
        }
        ArrayList sortedReasons = new ArrayList(missReasons.keySet());
        Collections.sort(sortedReasons, new Comparator<Integer>(){

            @Override
            public int compare(Integer a, Integer b) {
                float a1 = ((Float)missReasons.get(a)).floatValue() >= 1.0f ? ((Float)missReasons.get(a)).floatValue() : 1.0f / ((Float)missReasons.get(a)).floatValue();
                float b1 = ((Float)missReasons.get(b)).floatValue() >= 1.0f ? ((Float)missReasons.get(b)).floatValue() : 1.0f / ((Float)missReasons.get(b)).floatValue();
                return (int)Math.signum(b1 - a1);
            }
        });
        for (Integer reason : sortedReasons) {
            if (((Float)missReasons.get(reason)).floatValue() >= 1.0f) {
                defRoll /= ((Float)missReasons.get(reason)).floatValue();
            } else {
                accRoll /= ((Float)missReasons.get(reason)).floatValue();
            }
            if (!(defRoll < accRoll)) continue;
            return reason;
        }
        return -1;
    }
}

