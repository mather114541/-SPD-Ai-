/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class BuffIndicator
extends Component {
    public static final int NONE = 127;
    public static final int MIND_VISION = 0;
    public static final int LEVITATION = 1;
    public static final int FIRE = 2;
    public static final int POISON = 3;
    public static final int PARALYSIS = 4;
    public static final int HUNGER = 5;
    public static final int STARVATION = 6;
    public static final int TIME = 7;
    public static final int OOZE = 8;
    public static final int AMOK = 9;
    public static final int TERROR = 10;
    public static final int ROOTS = 11;
    public static final int INVISIBLE = 12;
    public static final int SHADOWS = 13;
    public static final int WEAKNESS = 14;
    public static final int FROST = 15;
    public static final int BLINDNESS = 16;
    public static final int COMBO = 17;
    public static final int FURY = 18;
    public static final int HERB_HEALING = 19;
    public static final int ARMOR = 20;
    public static final int HEART = 21;
    public static final int LIGHT = 22;
    public static final int CRIPPLE = 23;
    public static final int BARKSKIN = 24;
    public static final int IMMUNITY = 25;
    public static final int BLEEDING = 26;
    public static final int MARK = 27;
    public static final int DEFERRED = 28;
    public static final int DROWSY = 29;
    public static final int MAGIC_SLEEP = 30;
    public static final int THORNS = 31;
    public static final int FORESIGHT = 32;
    public static final int VERTIGO = 33;
    public static final int RECHARGING = 34;
    public static final int LOCKED_FLOOR = 35;
    public static final int CORRUPT = 36;
    public static final int BLESS = 37;
    public static final int RAGE = 38;
    public static final int SACRIFICE = 39;
    public static final int BERSERK = 40;
    public static final int HASTE = 41;
    public static final int PREPARATION = 42;
    public static final int WELL_FED = 43;
    public static final int HEALING = 44;
    public static final int WEAPON = 45;
    public static final int VULNERABLE = 46;
    public static final int HEX = 47;
    public static final int DEGRADE = 48;
    public static final int PINCUSHION = 49;
    public static final int UPGRADE = 50;
    public static final int MOMENTUM = 51;
    public static final int ANKH = 52;
    public static final int NOINV = 53;
    public static final int TARGETED = 54;
    public static final int IMBUE = 55;
    public static final int ENDURE = 56;
    public static final int INVERT_MARK = 57;
    public static final int NATURE_POWER = 58;
    public static final int AMULET = 59;
    public static final int DUEL_CLEAVE = 60;
    public static final int DUEL_GUARD = 61;
    public static final int DUEL_SPIN = 62;
    public static final int DUEL_EVASIVE = 63;
    public static final int DUEL_DANCE = 64;
    public static final int DUEL_BRAWL = 65;
    public static final int DUEL_XBOW = 66;
    public static final int CHALLENGE = 67;
    public static final int MONK_ENERGY = 68;
    public static final int DUEL_COMBO = 69;
    public static final int DAZE = 70;
    public static final int DISGUISE = 71;
    public static final int WAND = 72;
    public static final int HOLY_WEAPON = 73;
    public static final int HOLY_ARMOR = 74;
    public static final int SPELL_FOOD = 75;
    public static final int LIGHT_SHIELD = 76;
    public static final int HOLY_SIGHT = 77;
    public static final int GLYPH_RECALL = 78;
    public static final int ASCEND = 79;
    public static final int PROT_AURA = 80;
    public static final int ILLUMINATED = 81;
    public static final int TRINITY_FORM = 82;
    public static final int MANY_POWER = 83;
    public static final int SEAL_SHIELD = 84;
    public static final int THROWN_WEP = 85;
    public static final int SIZE_SMALL = 7;
    public static final int SIZE_LARGE = 16;
    private static BuffIndicator heroInstance;
    private static BuffIndicator bossInstance;
    private LinkedHashMap<Buff, BuffButton> buffButtons = new LinkedHashMap();
    private boolean needsRefresh;
    private Char ch;
    private boolean large = false;
    public float[] rowWidthLimits = new float[9];
    public float[] rowHeightAdjusts = new float[9];
    private boolean buffsHidden = false;
    public int maxBuffs = 14;

    public BuffIndicator(Char ch, boolean large) {
        this.ch = ch;
        this.large = large;
        if (ch == Dungeon.hero) {
            heroInstance = this;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this == heroInstance) {
            heroInstance = null;
        }
    }

    @Override
    public synchronized void update() {
        super.update();
        if (this.needsRefresh) {
            this.needsRefresh = false;
            this.layout();
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected void layout() {
        ArrayList<Buff> newBuffs = new ArrayList<Buff>();
        for (Buff buff : this.ch.buffs()) {
            if (buff.icon() == 127) continue;
            newBuffs.add(buff);
        }
        int size = this.large ? 16 : 7;
        for (Buff buff : this.buffButtons.keySet().toArray(new Buff[0])) {
            if (newBuffs.contains(buff)) continue;
            Image icon = this.buffButtons.get((Object)buff).icon;
            icon.originToCenter();
            icon.alpha(0.6f);
            this.add(icon);
            this.add(new AlphaTweener(icon, 0.0f, 0.6f){

                @Override
                protected void updateValues(float progress) {
                    super.updateValues(progress);
                    this.image.scale.set(1.0f + 5.0f * progress);
                }

                @Override
                protected void onComplete() {
                    this.image.killAndErase();
                }
            });
            this.buffButtons.get(buff).destroy();
            this.remove(this.buffButtons.get(buff));
            this.buffButtons.remove(buff);
        }
        for (Buff buff : newBuffs) {
            if (this.buffButtons.containsKey(buff)) continue;
            BuffButton icon = new BuffButton(buff, this.large);
            this.add(icon);
            this.buffButtons.put(buff, icon);
        }
        boolean bl = true;
        float f = 0.0f;
        int pos = 0;
        float lastIconRight = 0.0f;
        int total = 0;
        for (BuffButton icon : this.buffButtons.values()) {
            void var3_8;
            if (total >= this.maxBuffs) {
                icon.visible = false;
                continue;
            }
            icon.visible = true;
            icon.topOffset = var3_8 > true && !this.large ? -1 : 0;
            icon.updateIcon();
            icon.setRect(this.x + (float)(pos * (size + 1)), this.y + f - (float)icon.topOffset, size + 1, size + (this.large ? 0 : 5));
            PixelScene.align(icon);
            ++pos;
            lastIconRight = icon.right() - 1.0f;
            if (f + (float)(2 * size) + 2.0f <= this.height && (float)(pos * (size + 1) + size) > this.width || this.rowWidthLimits[var3_8] != 0.0f && (float)(pos * (size + 1) + size) > this.rowWidthLimits[var3_8]) {
                f += (float)(size + 1) + this.rowHeightAdjusts[++var3_8];
                pos = 0;
            }
            ++total;
        }
        this.buffsHidden = false;
        float excessWidth = lastIconRight - this.right();
        if (excessWidth > 0.0f) {
            ArrayList<BuffButton> buttons = new ArrayList<BuffButton>();
            float lastRowY = PixelScene.align(this.y + f);
            int i = 1;
            for (BuffButton button : this.buffButtons.values()) {
                if (i > this.maxBuffs) {
                    button.visible = false;
                    this.buffsHidden = true;
                    continue;
                }
                if (button.top() + (float)button.topOffset == lastRowY) {
                    buttons.add(button);
                }
                ++i;
            }
            float leftAdjust = excessWidth / (float)(buttons.size() - 1);
            if (leftAdjust >= (float)size * 0.48f) {
                leftAdjust = (float)size * 0.5f;
            }
            float cumulativeAdjust = leftAdjust * (float)(buttons.size() - 1);
            Collections.reverse(buttons);
            for (BuffButton icon : buttons) {
                icon.setPos(icon.left() - cumulativeAdjust, icon.top());
                boolean bl2 = icon.visible = icon.right() <= this.right() + 1.0f;
                if (!icon.visible) {
                    this.buffsHidden = true;
                }
                PixelScene.align(icon);
                this.bringToFront(icon);
                icon.givePointerPriority();
                cumulativeAdjust -= leftAdjust;
            }
        }
        if (this == heroInstance && this.buffButtons.size() >= 10) {
            Badges.validateManyBuffs();
        }
    }

    public boolean allBuffsVisible() {
        return !this.buffsHidden;
    }

    public static void refreshHero() {
        if (heroInstance != null) {
            BuffIndicator.heroInstance.needsRefresh = true;
        }
    }

    public static void refreshBoss() {
        if (bossInstance != null) {
            BuffIndicator.bossInstance.needsRefresh = true;
        }
    }

    public static void setBossInstance(BuffIndicator boss) {
        bossInstance = boss;
    }

    private static class BuffButton
    extends IconButton {
        private Buff buff;
        private boolean large;
        private int topOffset = 0;
        public Image grey;
        public BitmapText text;

        public BuffButton(Buff buff, boolean large) {
            super(new BuffIcon(buff, large));
            this.buff = buff;
            this.large = large;
            this.bringToFront(this.grey);
            this.bringToFront(this.text);
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.grey = new Image(TextureCache.createSolid(-865704346));
            this.add(this.grey);
            this.text = new BitmapText(PixelScene.pixelFont);
            this.add(this.text);
        }

        public void updateIcon() {
            ((BuffIcon)this.icon).refresh(this.buff);
            if (!this.large || this.buff.iconTextDisplay().isEmpty()) {
                float zoom;
                this.text.visible = false;
                this.grey.visible = true;
                float fadeHeight = GameMath.gate(0.0f, this.buff.iconFadePercent(), 1.0f) * this.icon.height();
                float f = zoom = this.camera() != null ? this.camera().zoom : 1.0f;
                if (fadeHeight < this.icon.height() / 2.0f) {
                    this.grey.scale.set(this.icon.width(), (float)Math.ceil(zoom * fadeHeight) / zoom);
                } else {
                    this.grey.scale.set(this.icon.width(), (float)Math.floor(zoom * fadeHeight) / zoom);
                }
            } else if (!this.buff.iconTextDisplay().isEmpty()) {
                this.text.visible = true;
                this.grey.visible = false;
                if (this.buff.type == Buff.buffType.POSITIVE) {
                    this.text.hardlight(65280);
                } else if (this.buff.type == Buff.buffType.NEGATIVE) {
                    this.text.hardlight(0xFF0000);
                }
                this.text.alpha(0.7f);
                this.text.text(this.buff.iconTextDisplay());
                this.text.measure();
            }
        }

        @Override
        protected void layout() {
            super.layout();
            this.grey.x = this.icon.x = this.x + (float)(!this.large ? 1 : 0);
            this.grey.y = this.icon.y = this.y + (float)(this.large ? 0 : 2) + (float)this.topOffset;
            if (this.text.width > this.width()) {
                this.text.scale.set(PixelScene.align(0.5f));
            } else {
                this.text.scale.set(1.0f);
            }
            this.text.x = this.x + this.width() - this.text.width() - 1.0f;
            this.text.y = this.y + this.width() - this.text.baseLine() - 2.0f;
        }

        @Override
        protected void onClick() {
            if (this.buff.icon() != 127) {
                GameScene.show(new WndInfoBuff(this.buff));
            }
        }

        @Override
        protected void onPointerDown() {
            Sample.INSTANCE.play("sounds/click.mp3");
        }

        @Override
        protected void onPointerUp() {
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(this.buff.name());
        }
    }
}

