/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShadowBox;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LostBackpack;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.gltextures.TextureCache;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.BArray;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;
import com.watabou.utils.Signal;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class InterlevelScene
extends PixelScene {
    private static final float SLOW_FADE = 1.0f;
    private static final float NORM_FADE = 0.67f;
    private static final float FAST_FADE = 0.5f;
    private static float fadeTime;
    public static Mode mode;
    public static LevelTransition curTransition;
    public static int returnDepth;
    public static int returnBranch;
    public static int returnPos;
    public static boolean fallIntoPit;
    private Phase phase;
    private float timeLeft;
    public Image background;
    private RenderedTextBlock loadingText;
    private RenderedTextBlock storyMessage;
    private ShadowBox storyBG;
    private StyledButton btnContinue;
    private IconButton btnHideStory;
    private static Thread thread;
    private static Exception error;
    private float waitingTime;
    public static int lastRegion;
    private RectF insets;
    private int dots;
    private boolean textFadingIn;

    public InterlevelScene() {
        this.inGameScene = true;
        this.dots = 0;
        this.textFadingIn = true;
    }

    @Override
    public void create() {
        String loadingAsset;
        int loadingDepth;
        super.create();
        fadeTime = 0.67f;
        long seed = Dungeon.seed;
        switch (mode.ordinal()) {
            default: {
                loadingDepth = Dungeon.depth;
                break;
            }
            case 2: {
                loadingDepth = GamesInProgress.check((int)GamesInProgress.curSlot).depth;
                seed = GamesInProgress.check((int)GamesInProgress.curSlot).seed;
                break;
            }
            case 0: {
                if (Dungeon.hero == null) {
                    loadingDepth = 1;
                    fadeTime = 1.0f;
                    break;
                }
                loadingDepth = curTransition != null ? InterlevelScene.curTransition.destDepth : Dungeon.depth;
                if (Statistics.deepestFloor >= loadingDepth) {
                    fadeTime = 0.5f;
                    break;
                }
                if (loadingDepth != 6 && loadingDepth != 11 && loadingDepth != 16 && loadingDepth != 21 && loadingDepth != 26) break;
                fadeTime = 1.0f;
                break;
            }
            case 5: {
                loadingDepth = Dungeon.depth;
                break;
            }
            case 1: {
                fadeTime = 0.5f;
                if (curTransition != null) {
                    loadingDepth = InterlevelScene.curTransition.destDepth;
                    break;
                }
                loadingDepth = Dungeon.depth;
                break;
            }
            case 4: {
                loadingDepth = returnDepth;
            }
        }
        final int region = (int)Math.ceil((float)loadingDepth / 5.0f);
        if (region != lastRegion) {
            TextureCache.clear();
            TitleBackground.reset();
            lastRegion = region;
        }
        int loadingCenter = 400;
        Random.pushGenerator(seed + (long)lastRegion);
        block7 : switch (lastRegion) {
            case 1: {
                loadingAsset = "splashes/sewers.jpg";
                switch (Random.Int(2)) {
                    case 0: {
                        loadingCenter = 180;
                        break;
                    }
                    case 1: {
                        loadingCenter = 485;
                    }
                }
                break;
            }
            case 2: {
                loadingAsset = "splashes/prison.jpg";
                switch (Random.Int(3)) {
                    case 0: {
                        loadingCenter = 190;
                        break;
                    }
                    case 1: {
                        loadingCenter = 402;
                    }
                }
                break;
            }
            case 3: {
                loadingAsset = "splashes/caves.jpg";
                switch (Random.Int(3)) {
                    case 0: {
                        loadingCenter = 340;
                        break;
                    }
                    case 1: {
                        loadingCenter = 625;
                    }
                }
                break;
            }
            case 4: {
                loadingAsset = "splashes/city.jpg";
                switch (Random.Int(3)) {
                    case 0: {
                        loadingCenter = 275;
                        break;
                    }
                    case 1: {
                        loadingCenter = 485;
                    }
                }
                break;
            }
            default: {
                loadingAsset = "splashes/halls.jpg";
                switch (Random.Int(3)) {
                    case 0: {
                        loadingCenter = 145;
                        break block7;
                    }
                    case 1: {
                        loadingCenter = 400;
                    }
                }
            }
        }
        Random.popGenerator();
        if (DeviceCompat.isDebug()) {
            fadeTime = 0.0f;
        }
        this.insets = Game.platform.getSafeInsets(1).scale(1.0f / (float)defaultZoom);
        int w = (int)((float)Camera.main.width - this.insets.left - this.insets.right);
        int h = (int)((float)Camera.main.height - this.insets.top - this.insets.bottom);
        this.background = new Image(loadingAsset);
        this.background.scale.set((float)Camera.main.height / this.background.height);
        if ((float)Camera.main.width >= this.background.width()) {
            this.background.x = ((float)Camera.main.width - this.background.width()) / 2.0f;
        } else {
            this.background.x = (float)Camera.main.width / 2.0f - (float)loadingCenter * this.background.scale.x;
            this.background.x = GameMath.gate((float)Camera.main.width - this.background.width(), this.background.x, 0.0f);
        }
        this.background.y = ((float)Camera.main.height - this.background.height()) / 2.0f;
        PixelScene.align(this.background);
        this.add(this.background);
        Image fadeLeft = new Image(TextureCache.createGradient(-16777216, -16777216, 0));
        fadeLeft.x = this.background.x - 2.0f;
        fadeLeft.scale.set(3.0f, this.background.height());
        fadeLeft.visible = this.background.x > 0.0f;
        this.add(fadeLeft);
        Image fadeRight = new Image(fadeLeft);
        fadeRight.x = this.background.x + this.background.width() + 2.0f;
        fadeRight.y = this.background.y + this.background.height();
        fadeRight.angle = 180.0f;
        fadeRight.visible = fadeLeft.visible;
        this.add(fadeRight);
        final Image im = new Image(TextureCache.createGradient(0, 0, 0, 0, 0, -16777216)){

            @Override
            public void update() {
                super.update();
                if (lastRegion == 6) {
                    this.aa = 1.0f;
                } else if (InterlevelScene.this.phase == Phase.FADE_IN) {
                    this.aa = Math.max(0.0f, 2.0f * (InterlevelScene.this.timeLeft - (fadeTime - 0.333f)));
                } else if (InterlevelScene.this.phase == Phase.FADE_OUT) {
                    this.aa = Math.max(0.0f, 2.0f * (0.333f - InterlevelScene.this.timeLeft));
                }
            }
        };
        im.angle = 90.0f;
        im.x = this.insets.left + (float)w;
        im.scale.x = (float)h / 5.0f;
        im.scale.y = w;
        this.add(im);
        String text = Messages.get(Mode.class, mode.name(), new Object[0]);
        this.loadingText = PixelScene.renderTextBlock(text, 9);
        this.loadingText.setPos(this.insets.left + (float)w - this.loadingText.width() - 12.0f, this.insets.top + (float)h - this.loadingText.height() - 6.0f);
        InterlevelScene.align(this.loadingText);
        this.add(this.loadingText);
        if (mode == Mode.DESCEND && lastRegion <= 5 && !DeviceCompat.isDebug() && (Dungeon.hero == null || loadingDepth > Statistics.deepestFloor && loadingDepth % 5 == 1)) {
            this.storyMessage = PixelScene.renderTextBlock(Document.INTROS.pageBody(region), 6);
            this.storyMessage.maxWidth(PixelScene.landscape() ? 180 : 125);
            this.storyMessage.setPos(this.insets.left + ((float)w - this.storyMessage.width()) / 2.0f, this.insets.top + ((float)h - this.storyMessage.height()) / 2.0f);
            this.storyBG = new ShadowBox();
            this.storyBG.boxRect(this.storyMessage.left() - 10.0f, this.storyMessage.top() - 10.0f, this.storyMessage.width() + 20.0f, this.storyMessage.height() + 20.0f);
            this.storyBG.alpha(0.8f);
            this.add(this.storyBG);
            this.add(this.storyMessage);
            this.btnContinue = new StyledButton(Chrome.Type.TOAST_TR, Messages.get(InterlevelScene.class, "continue", new Object[0]), 9){

                @Override
                protected void onClick() {
                    InterlevelScene.this.phase = Phase.FADE_OUT;
                    InterlevelScene.this.timeLeft = fadeTime;
                    InterlevelScene.this.btnContinue.enable(false);
                    Document.INTROS.readPage(region);
                }
            };
            this.btnContinue.icon(Icons.STAIRS.get());
            this.btnContinue.setSize(this.btnContinue.reqWidth() + 10.0f, 22.0f);
            this.btnContinue.visible = false;
            this.btnContinue.enable(false);
            KeyEvent.addKeyListener(new Signal.Listener<KeyEvent>(){

                @Override
                public boolean onSignal(KeyEvent keyEvent) {
                    if (!keyEvent.pressed && InterlevelScene.this.btnContinue.active) {
                        if (InterlevelScene.this.btnHideStory.active && !InterlevelScene.this.btnHideStory.icon().visible) {
                            InterlevelScene.this.btnHideStory.setRect(InterlevelScene.this.btnContinue.right() + 2.0f, InterlevelScene.this.btnContinue.top(), 20.0f, 21.0f);
                            PixelScene.align(InterlevelScene.this.btnHideStory);
                            InterlevelScene.this.btnHideStory.icon().visible = true;
                            InterlevelScene.this.btnHideStory.parent.add(new Tweener(InterlevelScene.this.parent, 0.5f){

                                @Override
                                protected void updateValues(float progress) {
                                    float uiAlpha = progress;
                                    InterlevelScene.this.btnContinue.alpha(uiAlpha);
                                    InterlevelScene.this.storyBG.alpha(uiAlpha * 0.8f);
                                    InterlevelScene.this.storyMessage.alpha(uiAlpha);
                                    InterlevelScene.this.btnHideStory.icon().alpha(uiAlpha);
                                    InterlevelScene.this.loadingText.alpha(uiAlpha);
                                    im.am = uiAlpha;
                                }
                            });
                        } else {
                            InterlevelScene.this.phase = Phase.FADE_OUT;
                            InterlevelScene.this.timeLeft = fadeTime;
                            InterlevelScene.this.btnContinue.enable(false);
                            Document.INTROS.readPage(region);
                        }
                        return true;
                    }
                    return false;
                }
            });
            this.btnContinue.setPos(this.insets.left + ((float)w - this.btnContinue.width()) / 2.0f, this.storyMessage.bottom() + 10.0f);
            this.add(this.btnContinue);
            this.btnHideStory = new IconButton(Icons.CHEVRON.get()){

                @Override
                protected void onClick() {
                    if (InterlevelScene.this.btnContinue.alpha() != 0.0f && InterlevelScene.this.btnContinue.alpha() != 1.0f) {
                        return;
                    }
                    if (this.icon.visible) {
                        this.enable(false);
                        this.parent.add(new Tweener(this.parent, 0.5f){

                            @Override
                            protected void updateValues(float progress) {
                                float uiAlpha = 1.0f - progress;
                                InterlevelScene.this.btnContinue.alpha(uiAlpha);
                                InterlevelScene.this.storyBG.alpha(uiAlpha * 0.8f);
                                InterlevelScene.this.storyMessage.alpha(uiAlpha);
                                icon.alpha(uiAlpha);
                                InterlevelScene.this.loadingText.alpha(uiAlpha);
                                im.am = uiAlpha;
                            }

                            @Override
                            protected void onComplete() {
                                super.onComplete();
                                this.setRect(0.0f, 0.0f, Camera.main.width, Camera.main.height);
                                this.enable(true);
                                (this).icon.visible = false;
                            }
                        });
                    } else {
                        this.setRect(InterlevelScene.this.btnContinue.right() + 2.0f, InterlevelScene.this.btnContinue.top(), 20.0f, 21.0f);
                        PixelScene.align(this);
                        this.icon.visible = true;
                        this.parent.add(new Tweener(this.parent, 0.5f){

                            @Override
                            protected void updateValues(float progress) {
                                float uiAlpha = progress;
                                InterlevelScene.this.btnContinue.alpha(uiAlpha);
                                InterlevelScene.this.storyBG.alpha(uiAlpha * 0.8f);
                                InterlevelScene.this.storyMessage.alpha(uiAlpha);
                                icon.alpha(uiAlpha);
                                InterlevelScene.this.loadingText.alpha(uiAlpha);
                                im.am = uiAlpha;
                            }
                        });
                    }
                }

                @Override
                protected void onPointerDown() {
                    if (this.icon.visible) {
                        super.onPointerDown();
                    }
                }
            };
            this.btnHideStory.icon().originToCenter();
            this.btnHideStory.icon().angle = 180.0f;
            this.btnHideStory.setRect(this.btnContinue.right() + 2.0f, this.btnContinue.top(), 20.0f, 21.0f);
            InterlevelScene.align(this.btnHideStory);
            this.btnHideStory.enable(false);
            this.add(this.btnHideStory);
            this.btnContinue.alpha(0.0f);
            this.storyBG.alpha(0.0f);
            this.storyMessage.alpha(0.0f);
            this.btnHideStory.icon().alpha(0.0f);
        }
        this.phase = Phase.FADE_IN;
        this.timeLeft = fadeTime;
        if (thread == null) {
            thread = new Thread(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    try {
                        Actor.fixTime();
                        switch (mode.ordinal()) {
                            case 0: {
                                InterlevelScene.this.descend();
                                break;
                            }
                            case 1: {
                                InterlevelScene.this.ascend();
                                break;
                            }
                            case 2: {
                                InterlevelScene.this.restore();
                                break;
                            }
                            case 3: {
                                InterlevelScene.this.resurrect();
                                break;
                            }
                            case 4: {
                                InterlevelScene.this.returnTo();
                                break;
                            }
                            case 5: {
                                InterlevelScene.this.fall();
                                break;
                            }
                            case 6: {
                                InterlevelScene.this.reset();
                            }
                        }
                    }
                    catch (Exception e) {
                        error = e;
                    }
                    Thread thread = InterlevelScene.thread;
                    synchronized (thread) {
                        if (InterlevelScene.this.phase == Phase.STATIC && error == null) {
                            InterlevelScene.this.afterLoading();
                        }
                    }
                }
            };
            thread.start();
        }
        this.waitingTime = 0.0f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update() {
        super.update();
        if (this.btnContinue == null || !this.btnContinue.isActive()) {
            this.waitingTime += Game.elapsed;
        }
        if (mode != Mode.FALL && (double)this.dots != Math.ceil(this.waitingTime / (2.0f * fadeTime / 3.0f))) {
            String text = Messages.get(Mode.class, mode.name(), new Object[0]);
            this.dots = (int)Math.ceil(this.waitingTime / (2.0f * fadeTime / 3.0f)) % 3;
            switch (this.dots) {
                default: {
                    this.loadingText.text(text + ".");
                    break;
                }
                case 2: {
                    this.loadingText.text(text + "..");
                    break;
                }
                case 0: {
                    this.loadingText.text(text + "...");
                }
            }
        }
        int w = (int)((float)Camera.main.width - this.insets.left - this.insets.right);
        int h = (int)((float)Camera.main.height - this.insets.top - this.insets.bottom);
        switch (this.phase.ordinal()) {
            case 0: {
                float f;
                this.loadingText.alpha(Math.max(0.0f, fadeTime - (this.timeLeft - 0.333f)));
                this.timeLeft -= Game.elapsed;
                if (!(f <= 0.0f)) break;
                Thread thread = InterlevelScene.thread;
                synchronized (thread) {
                    if (!InterlevelScene.thread.isAlive() && error == null) {
                        this.afterLoading();
                    } else {
                        this.phase = Phase.STATIC;
                    }
                    break;
                }
            }
            case 2: {
                float f;
                this.background.acc.set(0.0f);
                this.background.speed.set(0.0f);
                this.loadingText.alpha(Math.min(1.0f, this.timeLeft + 0.333f));
                if (this.btnContinue != null) {
                    this.btnContinue.alpha(this.timeLeft / fadeTime);
                    this.storyMessage.alpha(this.btnContinue.alpha());
                    this.storyBG.alpha(this.btnContinue.alpha() * 0.8f);
                    this.btnHideStory.icon().alpha(this.btnContinue.alpha());
                }
                this.timeLeft -= Game.elapsed;
                if (!(f <= 0.0f)) break;
                Game.switchScene(GameScene.class);
                KeyEvent.clearListeners();
                thread = null;
                error = null;
                break;
            }
            case 1: {
                if (this.btnContinue != null && this.textFadingIn) {
                    this.btnContinue.alpha(Math.min(1.0f, this.btnContinue.alpha() + Game.elapsed));
                    this.storyMessage.alpha(this.btnContinue.alpha());
                    this.storyBG.alpha(this.btnContinue.alpha() * 0.8f);
                    this.btnHideStory.icon().alpha(this.btnContinue.alpha());
                    if (this.btnContinue.alpha() == 1.0f) {
                        this.textFadingIn = false;
                        this.btnHideStory.enable(true);
                    }
                }
                if (this.btnContinue != null && !this.textFadingIn && Game.width < Game.height) {
                    if (this.background.speed.isZero() && this.background.acc.isZero()) {
                        this.background.acc.x = this.background.center().x >= (float)w + this.insets.left ? -1.0f : 1.0f;
                    } else {
                        float margin = 25.0f - this.insets.left;
                        this.background.speed.x = GameMath.gate(-10.0f, this.background.speed.x, 10.0f);
                        if (this.background.acc.x > 0.0f && this.background.x >= -margin) {
                            this.background.acc.x = -2.5f;
                        } else if (this.background.acc.x < 0.0f && this.background.x + this.background.width() <= (float)w + margin) {
                            this.background.acc.x = 2.5f;
                        }
                    }
                }
                if (error != null) {
                    String errorMsg;
                    if (error instanceof FileNotFoundException) {
                        errorMsg = Messages.get(this, "file_not_found", new Object[0]);
                    } else if (error instanceof IOException) {
                        errorMsg = Messages.get(this, "io_error", new Object[0]);
                    } else if (error.getMessage() != null && error.getMessage().equals("old save")) {
                        errorMsg = Messages.get(this, "io_error", new Object[0]);
                    } else {
                        throw new RuntimeException("fatal error occurred while moving between floors. Seed:" + Dungeon.seed + " depth:" + Dungeon.depth, error);
                    }
                    this.add(new WndError(errorMsg){

                        @Override
                        public void onBackPressed() {
                            super.onBackPressed();
                            Game.switchScene(StartScene.class);
                        }
                    });
                    thread = null;
                    error = null;
                    break;
                }
                if (thread == null || (int)this.waitingTime != 10) break;
                this.waitingTime = 11.0f;
                Object s = "";
                for (StackTraceElement t : thread.getStackTrace()) {
                    s = (String)s + "\n";
                    s = (String)s + t.toString();
                }
                if (((String)s).contains("FileUtils.bundleToFile")) break;
                ShatteredPixelDungeon.reportException(new RuntimeException("waited more than 10 seconds on levelgen. Seed:" + Dungeon.seed + " depth:" + Dungeon.depth + " trace:" + (String)s));
            }
        }
        if (mode == Mode.FALL) {
            this.loadingText.setPos((float)w + this.insets.left - this.loadingText.width() - 4.0f + 4.0f * (Random.Float(false) - 0.5f), (float)h + this.insets.top - this.loadingText.height() - 6.0f + 4.0f * (Random.Float(false) - 0.5f));
            InterlevelScene.align(this.loadingText);
        }
    }

    private void afterLoading() {
        if (this.btnContinue != null) {
            this.btnContinue.visible = true;
            float alpha = this.btnContinue.alpha();
            this.btnContinue.enable(true);
            this.btnContinue.alpha(alpha);
            this.phase = Phase.STATIC;
        } else {
            this.phase = Phase.FADE_OUT;
            this.timeLeft = fadeTime;
        }
    }

    private void descend() throws IOException {
        if (Dungeon.hero == null) {
            Mob.clearHeldAllies();
            Dungeon.init();
            GameLog.wipe();
            if (DeviceCompat.isDebug()) {
                int trueDepth = Dungeon.depth;
                int trueBranch = Dungeon.branch;
                for (int i = 1; i < trueDepth + (trueBranch == 0 ? 0 : 1); ++i) {
                    if (Dungeon.levelHasBeenGenerated(i, 0)) continue;
                    Dungeon.depth = i;
                    Dungeon.branch = 0;
                    Dungeon.level = Dungeon.newLevel();
                    Dungeon.saveLevel(GamesInProgress.curSlot);
                }
                Dungeon.depth = trueDepth;
                Dungeon.branch = trueBranch;
            }
            Level level = Dungeon.newLevel();
            Dungeon.switchLevel(level, -1);
        } else {
            if (InterlevelScene.curTransition.destBranch == Dungeon.branch || Dungeon.depth < 16 || Dungeon.depth > 20) {
                Mob.holdAllies(Dungeon.level);
            }
            Dungeon.saveAll();
            Dungeon.depth = InterlevelScene.curTransition.destDepth;
            Dungeon.branch = InterlevelScene.curTransition.destBranch;
            Level level = Dungeon.levelHasBeenGenerated(Dungeon.depth, Dungeon.branch) ? Dungeon.loadLevel(GamesInProgress.curSlot) : Dungeon.newLevel();
            LevelTransition destTransition = level.getTransition(InterlevelScene.curTransition.destType);
            curTransition = null;
            Dungeon.switchLevel(level, destTransition.cell());
        }
    }

    private void fall() throws IOException {
        Mob.holdAllies(Dungeon.level);
        Buff.affect(Dungeon.hero, Chasm.Falling.class);
        Dungeon.saveAll();
        Level level = Dungeon.levelHasBeenGenerated(++Dungeon.depth, Dungeon.branch) ? Dungeon.loadLevel(GamesInProgress.curSlot) : Dungeon.newLevel();
        Dungeon.switchLevel(level, level.fallCell(fallIntoPit));
    }

    private void ascend() throws IOException {
        if (InterlevelScene.curTransition.destBranch == Dungeon.branch || Dungeon.depth < 16 || Dungeon.depth > 20) {
            Mob.holdAllies(Dungeon.level);
        }
        Dungeon.saveAll();
        Dungeon.depth = InterlevelScene.curTransition.destDepth;
        Dungeon.branch = InterlevelScene.curTransition.destBranch;
        Level level = Dungeon.levelHasBeenGenerated(Dungeon.depth, Dungeon.branch) ? Dungeon.loadLevel(GamesInProgress.curSlot) : Dungeon.newLevel();
        LevelTransition destTransition = level.getTransition(InterlevelScene.curTransition.destType);
        curTransition = null;
        Dungeon.switchLevel(level, destTransition.cell());
    }

    private void returnTo() throws IOException {
        Mob.holdAllies(Dungeon.level);
        Dungeon.saveAll();
        Dungeon.depth = returnDepth;
        Dungeon.branch = returnBranch;
        Level level = Dungeon.levelHasBeenGenerated(Dungeon.depth, Dungeon.branch) ? Dungeon.loadLevel(GamesInProgress.curSlot) : Dungeon.newLevel();
        Dungeon.switchLevel(level, returnPos);
    }

    private void restore() throws IOException {
        Mob.clearHeldAllies();
        GameLog.wipe();
        Dungeon.loadGame(GamesInProgress.curSlot);
        if (Dungeon.depth == -1) {
            Dungeon.depth = Statistics.deepestFloor;
            Dungeon.switchLevel(Dungeon.loadLevel(GamesInProgress.curSlot), -1);
        } else {
            Level level = Dungeon.loadLevel(GamesInProgress.curSlot);
            Dungeon.switchLevel(level, Dungeon.hero.pos);
        }
    }

    private void resurrect() {
        Level level;
        Mob.holdAllies(Dungeon.level);
        if (Dungeon.level.locked) {
            ArrayList<Item> preservedItems = Dungeon.level.getItemsToPreserveFromSealedResurrect();
            Dungeon.hero.resurrect();
            level = Dungeon.newLevel();
            Dungeon.hero.pos = level.randomRespawnCell(Dungeon.hero);
            if (Dungeon.hero.pos == -1) {
                Dungeon.hero.pos = level.entrance();
            }
            for (Item i : preservedItems) {
                int pos = level.randomRespawnCell(null);
                if (pos == -1) {
                    pos = level.entrance();
                }
                level.drop(i, pos);
            }
            int pos = level.randomRespawnCell(null);
            if (pos == -1) {
                pos = level.entrance();
            }
            level.drop(new LostBackpack(), pos);
            if (Dungeon.hero.buff(SkeletonKey.KeyReplacementTracker.class) != null) {
                Dungeon.hero.buff(SkeletonKey.KeyReplacementTracker.class).clearDepth();
            }
        } else {
            level = Dungeon.level;
            BArray.setFalse(level.heroFOV);
            BArray.setFalse(level.visited);
            BArray.setFalse(level.mapped);
            int invPos = Dungeon.hero.pos;
            int tries = 0;
            do {
                Dungeon.hero.pos = level.randomRespawnCell(Dungeon.hero);
            } while (level.traps.get(Dungeon.hero.pos) != null || level.plants.get(Dungeon.hero.pos) != null && ++tries < 500 || level.trueDistance(invPos, Dungeon.hero.pos) <= (float)(30 - tries / 10));
            if (level.map[Dungeon.hero.pos] == 15 || level.map[Dungeon.hero.pos] == 30) {
                level.map[Dungeon.hero.pos] = 2;
            }
            Dungeon.hero.resurrect();
            level.drop(new LostBackpack(), invPos);
        }
        Notes.add(Notes.Landmark.LOST_PACK);
        Dungeon.switchLevel(level, Dungeon.hero.pos);
    }

    private void reset() throws IOException {
        Mob.holdAllies(Dungeon.level);
        SpecialRoom.resetPitRoom(Dungeon.depth + 1);
        Level level = Dungeon.newLevel();
        Dungeon.switchLevel(level, level.entrance());
    }

    @Override
    protected void onBackPressed() {
    }

    static {
        curTransition = null;
        error = null;
        lastRegion = -1;
    }

    public static enum Mode {
        DESCEND,
        ASCEND,
        CONTINUE,
        RESURRECT,
        RETURN,
        FALL,
        RESET,
        NONE;

    }

    private static enum Phase {
        FADE_IN,
        STATIC,
        FADE_OUT;

    }
}

