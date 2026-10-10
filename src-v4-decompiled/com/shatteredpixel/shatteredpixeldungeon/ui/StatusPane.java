/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CircleArc;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BusyIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.Compass;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;

public class StatusPane
extends Component {
    private NinePatch bg;
    private Image avatar;
    private Button heroInfo;
    public static float talentBlink;
    private float warning;
    public static final float FLASH_RATE = 4.712389f;
    private int lastTier = 0;
    private Image shieldHP;
    private Image hp;
    private Image Dot;
    private BitmapText hpText;
    private Button heroInfoOnBar;
    private Image exp;
    private BitmapText expText;
    private int lastLvl = -1;
    private BitmapText level;
    private BuffIndicator buffs;
    private Compass compass;
    private BusyIndicator busy;
    private CircleArc counter;
    private boolean large;
    public static float heroPaneExtraWidth;
    private NinePatch heroPaneCutout;
    public static int hpBarMaxWidth;
    private Image hpCutout;
    public static float[] buffBarRowMaxWidths;
    public static float[] buffBarRowAdjusts;
    private static final int[] warningColors;
    private int oldHP = 0;
    private int oldShield = 0;
    private int oldMax = 0;

    public StatusPane(boolean large) {
        String asset = "interfaces/status_pane.png";
        this.large = large;
        this.bg = large ? new NinePatch(asset, 0, 64, 41, 39, 33, 0, 4, 0) : new NinePatch(asset, 0, 0, 82, 38, 32, 0, 5, 0);
        this.add(this.bg);
        this.heroPaneCutout = new NinePatch(asset, 0, 0, 5, 36, 4, 0, 0, 0);
        this.heroPaneCutout.visible = false;
        this.add(this.heroPaneCutout);
        this.hpCutout = new Image(asset, 90, 0, 12, 9);
        this.hpCutout.visible = false;
        this.add(this.hpCutout);
        this.heroInfo = new Button(){

            @Override
            protected void onClick() {
                Camera.main.panTo(Dungeon.hero.sprite.center(), 5.0f);
                GameScene.show(new WndHero());
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.HERO_INFO;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info", new Object[0]));
            }
        };
        this.add(this.heroInfo);
        this.avatar = HeroSprite.avatar(Dungeon.hero);
        this.add(this.avatar);
        talentBlink = 0.0f;
        this.compass = new Compass(Statistics.amuletObtained ? Dungeon.level.entrance() : Dungeon.level.exit());
        this.add(this.compass);
        this.shieldHP = large ? new Image(asset, 0, 112, 128, 9) : new Image(asset, 0, 44, 50, 4);
        this.add(this.shieldHP);
        this.hp = large ? new Image(asset, 0, 103, 128, 9) : new Image(asset, 0, 40, 50, 4);
        this.add(this.hp);
        this.Dot = large ? new Image(asset, 0, 103, 128, 9) : new Image(asset, 0, 40, 50, 4);
        this.Dot.hardlight(0.0f, 0.0f, 0.0f);
        this.Dot.alpha(0.25f);
        this.add(this.Dot);
        this.hpText = new BitmapText(PixelScene.pixelFont);
        this.hpText.alpha(0.6f);
        this.add(this.hpText);
        this.heroInfoOnBar = new Button(){

            @Override
            protected void onClick() {
                Camera.main.panTo(Dungeon.hero.sprite.center(), 5.0f);
                GameScene.show(new WndHero());
            }
        };
        this.add(this.heroInfoOnBar);
        this.exp = large ? new Image(asset, 0, 121, 128, 7) : new Image(asset, 0, 48, 17, 4);
        this.add(this.exp);
        this.expText = new BitmapText(PixelScene.pixelFont);
        this.expText.hardlight(0xFFFFAA);
        this.expText.alpha(0.6f);
        this.add(this.expText);
        this.level = new BitmapText(PixelScene.pixelFont);
        this.level.hardlight(0xFFFFAA);
        this.add(this.level);
        this.buffs = new BuffIndicator(Dungeon.hero, large);
        this.add(this.buffs);
        this.busy = new BusyIndicator();
        this.add(this.busy);
        this.counter = new CircleArc(18, 4.25f);
        this.counter.color(0x808080, true);
        this.counter.show(this, this.busy.center(), 0.0f);
    }

    @Override
    protected void layout() {
        this.height = this.large ? 39.0f : 38.0f;
        float heroPaneWidth = 30.0f + heroPaneExtraWidth;
        this.bg.x = this.x + heroPaneExtraWidth;
        this.bg.y = this.y;
        if (this.large) {
            this.bg.size(160.0f, this.bg.height);
        } else {
            this.bg.size(hpBarMaxWidth + 32, this.bg.height);
        }
        this.avatar.x = this.bg.x - this.avatar.width / 2.0f + 15.0f;
        this.avatar.y = this.bg.y - this.avatar.height / 2.0f + 16.0f;
        PixelScene.align(this.avatar);
        this.heroInfo.setRect(this.x, this.y, heroPaneWidth, this.large ? 40.0f : 36.0f);
        this.compass.x = this.avatar.x + this.avatar.width / 2.0f - this.compass.origin.x;
        this.compass.y = this.avatar.y + this.avatar.height / 2.0f - this.compass.origin.y;
        PixelScene.align(this.compass);
        if (this.large) {
            this.exp.x = this.x + 30.0f;
            this.exp.y = this.y + 30.0f;
            this.shieldHP.x = this.Dot.x = this.x + 30.0f;
            this.hp.x = this.Dot.x;
            this.shieldHP.y = this.Dot.y = this.y + 19.0f;
            this.hp.y = this.Dot.y;
            this.hpText.x = this.hp.x + (128.0f - this.hpText.width()) / 2.0f;
            this.hpText.y = this.hp.y + 1.0f;
            PixelScene.align(this.hpText);
            this.expText.x = this.exp.x + (128.0f - this.expText.width()) / 2.0f;
            this.expText.y = this.exp.y;
            PixelScene.align(this.expText);
            this.heroInfoOnBar.setRect(this.heroInfo.right(), this.y + 19.0f, 130.0f, 20.0f);
            this.buffs.setRect(this.x + 31.0f, this.y, 142.0f, 16.0f);
            this.busy.x = this.x + this.bg.width + 1.0f;
            this.busy.y = this.y + this.bg.height - 9.0f;
        } else {
            this.exp.x = this.x + 2.0f;
            this.exp.y = this.y + 30.0f;
            if (heroPaneExtraWidth > 0.0f) {
                this.heroPaneCutout.visible = true;
                this.heroPaneCutout.x = this.x;
                this.heroPaneCutout.y = this.y;
                this.heroPaneCutout.size(heroPaneExtraWidth + 4.0f, this.heroPaneCutout.height);
            }
            float hpleft = this.x + heroPaneWidth;
            if (hpBarMaxWidth < 82) {
                int hpWidth = hpBarMaxWidth;
                if (hpWidth <= 41) {
                    hpWidth += 9;
                    this.hpCutout.visible = true;
                    this.hpCutout.x = (hpleft -= 9.0f) - 2.0f;
                    this.hpCutout.y = this.y;
                }
                this.hp.frame(50 - hpWidth, 40, 50, 4);
                this.shieldHP.frame(50 - hpWidth, 44, 50, 4);
            }
            this.shieldHP.x = this.Dot.x = hpleft;
            this.hp.x = this.Dot.x;
            this.shieldHP.y = this.Dot.y = this.y + 2.0f;
            this.hp.y = this.Dot.y;
            this.hpText.scale.set(PixelScene.align(0.5f));
            this.hpText.x = this.hp.x + 1.0f;
            this.hpText.y = this.hp.y + (this.hp.height - (this.hpText.baseLine() + this.hpText.scale.y)) / 2.0f;
            this.hpText.y -= 0.001f;
            PixelScene.align(this.hpText);
            this.expText.scale.set(PixelScene.align(0.5f));
            this.expText.x = this.exp.x + 1.0f;
            this.expText.y = this.exp.y + (this.exp.height - (this.expText.baseLine() + this.expText.scale.y)) / 2.0f;
            this.expText.y -= 0.001f;
            PixelScene.align(this.expText);
            this.heroInfoOnBar.setRect(this.heroInfo.right(), this.y, 50.0f, 9.0f);
            if (buffBarRowMaxWidths != null) {
                this.buffs.rowWidthLimits = buffBarRowMaxWidths;
            }
            if (buffBarRowAdjusts != null) {
                this.buffs.rowHeightAdjusts = buffBarRowAdjusts;
            }
            this.buffs.setRect(this.x + heroPaneWidth + 1.0f, this.y + 8.0f, 55.0f, 16.0f);
            this.busy.x = this.x + 1.0f;
            this.busy.y = this.y + 37.0f;
        }
        this.counter.point(this.busy.center());
    }

    @Override
    public void update() {
        int tier;
        super.update();
        int health = Dungeon.hero.HP;
        int shield = Dungeon.hero.shielding();
        int incomingDOT = Dungeon.hero.incomingDOT();
        int max = Dungeon.hero.HT;
        if (!Dungeon.hero.isAlive()) {
            this.avatar.tint(0, 0.5f);
        } else if ((float)health / (float)max < 0.334f) {
            this.warning += Game.elapsed * 5.0f * (0.4f - (float)health / (float)max);
            this.warning %= 1.0f;
            this.avatar.tint(ColorMath.interpolate(this.warning, warningColors), 0.5f);
        } else if (talentBlink > 0.33f) {
            this.avatar.tint(1.0f, 1.0f, 0.0f, (float)Math.abs(Math.cos((talentBlink -= Game.elapsed) * 4.712389f)) / 2.0f);
        } else {
            this.avatar.resetColor();
        }
        float healthPercent = (float)health / (float)max;
        float shieldPercent = (float)shield / (float)max;
        float DOTPercent = (float)incomingDOT / (float)max;
        if (healthPercent + shieldPercent > 1.0f) {
            float excess = healthPercent + shieldPercent;
            healthPercent /= excess;
            shieldPercent /= excess;
            DOTPercent /= excess;
        }
        this.hp.scale.x = healthPercent;
        this.shieldHP.scale.x = healthPercent + shieldPercent;
        this.Dot.scale.x = Math.min(DOTPercent, this.shieldHP.scale.x);
        this.Dot.x = this.shieldHP.x + this.shieldHP.width() - this.Dot.width();
        if (this.oldHP != health || this.oldShield != shield || this.oldMax != max) {
            if (shield <= 0) {
                this.hpText.text(health + "/" + max);
            } else {
                this.hpText.text(health + "+" + shield + "/" + max);
            }
            this.oldHP = health;
            this.oldShield = shield;
            this.oldMax = max;
        }
        if (this.large) {
            this.exp.scale.x = 128.0f / this.exp.width * (float)Dungeon.hero.exp / (float)Dungeon.hero.maxExp();
            this.hpText.measure();
            this.hpText.x = this.hp.x + (128.0f - this.hpText.width()) / 2.0f;
            this.expText.text(Dungeon.hero.exp + "/" + Dungeon.hero.maxExp());
            this.expText.measure();
            this.expText.x = this.hp.x + (128.0f - this.expText.width()) / 2.0f;
        } else {
            this.exp.scale.x = (17.0f + heroPaneExtraWidth) / this.exp.width * (float)Dungeon.hero.exp / (float)Dungeon.hero.maxExp();
            this.expText.text(Dungeon.hero.exp + "/" + Dungeon.hero.maxExp());
        }
        if (Dungeon.hero.lvl != this.lastLvl) {
            if (this.lastLvl != -1) {
                this.showStarParticles();
            }
            this.lastLvl = Dungeon.hero.lvl;
            if (this.large) {
                this.level.text("lv. " + this.lastLvl);
                this.level.measure();
                this.level.x = this.x + (30.0f - this.level.width()) / 2.0f;
                this.level.y = this.y + 33.0f - this.level.baseLine() / 2.0f;
            } else {
                this.level.text(Integer.toString(this.lastLvl));
                this.level.measure();
                this.level.x = this.x + heroPaneExtraWidth + 25.5f - this.level.width() / 2.0f;
                this.level.y = this.y + 31.0f - this.level.baseLine() / 2.0f;
            }
            PixelScene.align(this.level);
        }
        if ((tier = Dungeon.hero.tier()) != this.lastTier) {
            this.lastTier = tier;
            this.avatar.copy(HeroSprite.avatar(Dungeon.hero));
        }
        this.counter.setSweep((1.0f - Actor.now() % 1.0f) % 1.0f);
    }

    public void updateAvatar() {
        this.avatar.copy(HeroSprite.avatar(Dungeon.hero));
    }

    public void alpha(float value) {
        value = GameMath.gate(0.0f, value, 1.0f);
        this.bg.alpha(value);
        this.heroPaneCutout.alpha(value);
        this.hpCutout.alpha(value);
        this.avatar.alpha(value);
        this.shieldHP.alpha(value);
        this.hp.alpha(value);
        this.hpText.alpha(0.6f * value);
        this.exp.alpha(value);
        if (this.expText != null) {
            this.expText.alpha(0.6f * value);
        }
        this.level.alpha(value);
        this.compass.alpha(value);
        this.busy.alpha(value);
        this.counter.alpha(value);
    }

    public void showStarParticles() {
        Emitter emitter = (Emitter)this.recycle(Emitter.class);
        emitter.revive();
        emitter.pos(this.avatar.center());
        emitter.burst(Speck.factory(1), 12);
    }

    static {
        heroPaneExtraWidth = 0.0f;
        hpBarMaxWidth = 50;
        warningColors = new int[]{0x660000, 0xCC0000, 0x660000};
    }
}

