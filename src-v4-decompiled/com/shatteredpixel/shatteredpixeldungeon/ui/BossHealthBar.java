/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Callback;

public class BossHealthBar
extends Component {
    private Image bar;
    private Image shieldHP;
    private Image hp;
    private Image Dot;
    private BitmapText hpText;
    private Button bossInfo;
    private BuffIndicator buffs;
    private static Mob boss;
    private Image skull;
    private Emitter blood;
    private static String asset;
    private static BossHealthBar instance;
    private static boolean bleeding;
    private boolean large;

    public BossHealthBar() {
        this.active = boss != null;
        this.visible = this.active;
        instance = this;
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (instance == this) {
            instance = null;
        }
        if (this.buffs != null) {
            BuffIndicator.setBossInstance(null);
        }
    }

    @Override
    protected void createChildren() {
        this.large = SPDSettings.interfaceSize() != 0;
        this.bar = this.large ? new Image(asset, 0, 16, 128, 30) : new Image(asset, 0, 0, 64, 16);
        this.add(this.bar);
        this.width = this.bar.width;
        this.height = this.bar.height;
        this.shieldHP = this.large ? new Image(asset, 0, 55, 96, 9) : new Image(asset, 71, 5, 47, 4);
        this.add(this.shieldHP);
        this.hp = this.large ? new Image(asset, 0, 46, 96, 9) : new Image(asset, 71, 0, 47, 4);
        this.add(this.hp);
        this.Dot = this.large ? new Image(asset, 0, 46, 96, 9) : new Image(asset, 71, 0, 47, 4);
        this.Dot.hardlight(0.0f, 0.0f, 0.0f);
        this.Dot.alpha(0.25f);
        this.add(this.Dot);
        this.hpText = new BitmapText(PixelScene.pixelFont);
        this.hpText.alpha(0.6f);
        this.add(this.hpText);
        this.bossInfo = new Button(){

            @Override
            protected void onClick() {
                super.onClick();
                if (boss != null) {
                    GameScene.show(new WndInfoMob(boss));
                }
            }

            @Override
            protected String hoverText() {
                if (boss != null) {
                    return boss.name();
                }
                return super.hoverText();
            }
        };
        this.add(this.bossInfo);
        if (boss != null) {
            this.buffs = new BuffIndicator(boss, this.large);
            BuffIndicator.setBossInstance(this.buffs);
            this.add(this.buffs);
        }
        this.skull = boss != null && this.large ? boss.sprite() : new Image(asset, 64, 0, 6, 6);
        this.add(this.skull);
        this.blood = new Emitter();
        this.blood.pos(this.skull);
        this.blood.pour(BloodParticle.FACTORY, 0.3f);
        this.blood.autoKill = false;
        this.blood.on = false;
        this.add(this.blood);
    }

    @Override
    protected void layout() {
        int paneSize;
        this.bar.x = this.x;
        this.bar.y = this.y;
        this.shieldHP.x = this.Dot.x = this.bar.x + (float)(this.large ? 30 : 15);
        this.hp.x = this.Dot.x;
        this.shieldHP.y = this.Dot.y = this.bar.y + (float)(this.large ? 2 : 3);
        this.hp.y = this.Dot.y;
        if (!this.large) {
            this.hpText.scale.set(PixelScene.align(0.5f));
        }
        this.hpText.x = this.hp.x + (this.large ? (96.0f - this.hpText.width()) / 2.0f : 1.0f);
        this.hpText.y = this.hp.y + (this.hp.height - (this.hpText.baseLine() + this.hpText.scale.y)) / 2.0f;
        this.hpText.y -= 0.001f;
        PixelScene.align(this.hpText);
        this.bossInfo.setRect(this.x, this.y, this.bar.width, this.bar.height);
        if (this.buffs != null) {
            this.buffs.maxBuffs = 12;
            if (this.large) {
                this.buffs.setRect(this.hp.x + 1.0f, this.hp.y + 12.0f, 102.0f, 34.0f);
            } else {
                this.buffs.setRect(this.hp.x, this.hp.y + 5.0f, 47.0f, 16.0f);
            }
        }
        int n = paneSize = this.large ? 30 : 16;
        if (this.skull.height() > 24.0f || this.skull.width() > 24.0f) {
            this.skull.scale.set(this.skull.scale.x * PixelScene.align(24.0f / Math.max(this.skull.width(), this.skull.height())));
        }
        this.skull.x = this.bar.x + ((float)paneSize - this.skull.width()) / 2.0f;
        this.skull.y = this.bar.y + ((float)paneSize - this.skull.height()) / 2.0f;
    }

    @Override
    public void update() {
        super.update();
        if (boss != null) {
            if (!boss.isAlive() || !Dungeon.level.mobs.contains(boss)) {
                boss = null;
                this.active = false;
                this.visible = false;
                if (this.buffs != null) {
                    BuffIndicator.setBossInstance(null);
                    this.remove(this.buffs);
                    this.buffs.destroy();
                    this.buffs = null;
                }
            } else {
                int health = BossHealthBar.boss.HP;
                int shield = boss.shielding();
                int incomingDOT = boss.incomingDOT();
                int max = BossHealthBar.boss.HT;
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
                if (bleeding != this.blood.on) {
                    if (bleeding) {
                        this.skull.tint(0xCC0000, this.large ? 0.3f : 0.6f);
                    } else {
                        this.skull.resetColor();
                    }
                    this.bringToFront(this.blood);
                    this.blood.pos(this.skull);
                    this.blood.on = bleeding;
                }
                if (shield <= 0) {
                    this.hpText.text(health + "/" + max);
                } else {
                    this.hpText.text(health + "+" + shield + "/" + max);
                }
                this.hpText.measure();
                this.hpText.x = this.hp.x + (this.large ? (96.0f - this.hpText.width()) / 2.0f : 1.0f);
            }
        }
    }

    public static void assignBoss(Mob boss) {
        BossHealthBar.assignBoss(boss, false);
    }

    public static void assignBoss(final Mob boss, boolean forceSpriteRefresh) {
        if (BossHealthBar.boss == boss && instance != null) {
            if (forceSpriteRefresh && BossHealthBar.instance.large) {
                ShatteredPixelDungeon.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        instance.remove(BossHealthBar.instance.skull);
                        BossHealthBar.instance.skull.destroy();
                        BossHealthBar.instance.skull = boss.sprite();
                        instance.add(BossHealthBar.instance.skull);
                        instance.layout();
                    }
                });
            }
            return;
        }
        BossHealthBar.boss = boss;
        BossHealthBar.bleed(false);
        if (instance != null) {
            ShatteredPixelDungeon.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    BossHealthBar.instance.active = true;
                    BossHealthBar.instance.visible = true;
                    if (boss != null) {
                        if (BossHealthBar.instance.large) {
                            if (BossHealthBar.instance.skull != null) {
                                instance.remove(BossHealthBar.instance.skull);
                                BossHealthBar.instance.skull.destroy();
                            }
                            BossHealthBar.instance.skull = boss.sprite();
                            instance.add(BossHealthBar.instance.skull);
                        }
                        if (BossHealthBar.instance.buffs != null) {
                            instance.remove(BossHealthBar.instance.buffs);
                            BossHealthBar.instance.buffs.destroy();
                        }
                        BossHealthBar.instance.buffs = new BuffIndicator(boss, BossHealthBar.instance.large);
                        BuffIndicator.setBossInstance(BossHealthBar.instance.buffs);
                        instance.add(BossHealthBar.instance.buffs);
                        instance.layout();
                    }
                }
            });
        }
    }

    public static boolean isAssigned() {
        return boss != null && boss.isAlive() && Dungeon.level.mobs.contains(boss);
    }

    public static void bleed(boolean value) {
        bleeding = value;
    }

    public static boolean isBleeding() {
        return BossHealthBar.isAssigned() && bleeding;
    }

    static {
        asset = "interfaces/boss_hp.png";
    }
}

