/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class AboutScene
extends PixelScene {
    @Override
    public void create() {
        super.create();
        float colWidth = AboutScene.landscape() ? 120.0f : 130.0f;
        float fullWidth = colWidth * (float)(AboutScene.landscape() ? 2 : 1);
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        this.add(BG);
        this.add(new ColorBlock(w, h, 0x44000000));
        ScrollPane list = new ScrollPane(new Component());
        this.add(list);
        Component content = list.content();
        content.clear();
        CreditsBlock shpx = new CreditsBlock(true, 0x33BB33, "Shattered Pixel Dungeon", Icons.SHPX.get(), "Developed by: _Evan Debenham_\nBased on Pixel Dungeon's open source", "ShatteredPixel.com", "https://ShatteredPixel.com");
        if (AboutScene.landscape()) {
            shpx.setRect(((float)w - fullWidth) / 2.0f - 6.0f, insets.top + 26.0f, 120.0f, 0.0f);
        } else {
            shpx.setRect(((float)w - fullWidth) / 2.0f, insets.top + 6.0f, 120.0f, 0.0f);
        }
        content.add(shpx);
        CreditsBlock aleks = new CreditsBlock(false, 0x33BB33, "Splash & Dungeon Art:", Icons.ALEKS.get(), "Aleksandar Komitov", "alekskomitov.com", "https://www.alekskomitov.com/");
        aleks.setSize(colWidth / 2.0f, 0.0f);
        if (AboutScene.landscape()) {
            aleks.setPos(shpx.right(), insets.top + 6.0f);
        } else {
            aleks.setPos((float)w / 2.0f - colWidth / 2.0f, shpx.bottom() + 6.0f);
        }
        content.add(aleks);
        CreditsBlock lumine = new CreditsBlock(false, 0x33BB33, "Composer:", Icons.LUMINE.get(), "Lumine Haaristo", "youtube.com/@Lumine...", "https://www.youtube.com/@LumineThomasHaaristo");
        lumine.setRect(aleks.right(), aleks.top(), colWidth / 2.0f, 0.0f);
        content.add(lumine);
        CreditsBlock pumpkin = new CreditsBlock(false, 0x33BB33, "Item Pixel Art:", Icons.PUMPKINVOLT.get(), "PumpkinVolt", null, null);
        pumpkin.setSize(colWidth / 2.0f, 0.0f);
        pumpkin.setRect(aleks.left(), aleks.bottom() + 6.0f, colWidth / 2.0f, 0.0f);
        content.add(pumpkin);
        CreditsBlock celesti = new CreditsBlock(false, 0x33BB33, "Sound Effects:", Icons.CELESTI.get(), "Celesti", "celesti-whispers.itch.io", "https://celesti-whispers.itch.io/");
        celesti.setRect(pumpkin.right(), pumpkin.top(), colWidth / 2.0f, 0.0f);
        content.add(celesti);
        CreditsBlock alastair = new CreditsBlock(false, 0x33BB33, "Additional Pixel Art:", Icons.ALASTAIR.get(), "Alastair Braun", null, null);
        alastair.setSize(colWidth / 2.0f, 0.0f);
        alastair.setRect(pumpkin.left(), celesti.bottom() + 6.0f, colWidth / 2.0f, 0.0f);
        content.add(alastair);
        int WATA_COLOR = 0x55AAFF;
        CreditsBlock wata = new CreditsBlock(true, 0x55AAFF, "Pixel Dungeon", Icons.WATA.get(), "Developed by: _Watabou_\nInspired by Brian Walker's Brogue", "watabou.itch.io", "https://watabou.itch.io/");
        if (AboutScene.landscape()) {
            wata.setRect(shpx.left(), alastair.bottom() + 8.0f, colWidth, 0.0f);
        } else {
            wata.setRect(shpx.left(), alastair.bottom() + 8.0f, colWidth, 0.0f);
        }
        content.add(wata);
        this.addLine(wata.top() - 4.0f, content);
        CreditsBlock cube = new CreditsBlock(false, 0x55AAFF, "Music:", Icons.CUBE_CODE.get(), "Cube Code", null, null);
        cube.setSize(colWidth / 2.0f, 0.0f);
        if (AboutScene.landscape()) {
            cube.setPos(wata.right() + colWidth / 4.0f, wata.top() + (wata.height() - cube.height()) / 2.0f);
        } else {
            cube.setPos(aleks.left() + colWidth / 4.0f, wata.bottom() + 6.0f);
        }
        content.add(cube);
        int GDX_COLOR = 14961980;
        CreditsBlock gdx = new CreditsBlock(true, 14961980, "libGDX", Icons.LIBGDX.get(), "ShatteredPD is powered by _libGDX_!", "libgdx.com", "https://libgdx.com/");
        if (AboutScene.landscape()) {
            gdx.setRect(wata.left(), wata.bottom() + 8.0f, colWidth, 0.0f);
        } else {
            gdx.setRect(wata.left(), cube.bottom() + 8.0f, colWidth, 0.0f);
        }
        content.add(gdx);
        this.addLine(gdx.top() - 4.0f, content);
        CreditsBlock arcnor = new CreditsBlock(false, 14961980, "Pixel Dungeon GDX:", Icons.ARCNOR.get(), "Edu Garc\u00eda", "gamedev.place/@arcnor", "https://mastodon.gamedev.place/@arcnor");
        arcnor.setSize(colWidth / 2.0f, 0.0f);
        if (AboutScene.landscape()) {
            arcnor.setPos(gdx.right(), gdx.top() + (gdx.height() - arcnor.height()) / 2.0f);
        } else {
            arcnor.setPos(aleks.left(), gdx.bottom() + 6.0f);
        }
        content.add(arcnor);
        CreditsBlock purigro = new CreditsBlock(false, 14961980, "Shattered GDX Help:", Icons.PURIGRO.get(), "Kevin MacMartin", "github.com/prurigro", "https://github.com/prurigro/");
        purigro.setRect(arcnor.right() + 2.0f, arcnor.top(), colWidth / 2.0f, 0.0f);
        content.add(purigro);
        CreditsBlock transifex = new CreditsBlock(true, 0xFFFF44, null, null, "ShatteredPD is community-translated via _Transifex_! Thank you so much to all of Shattered's volunteer translators!", "transifex.com/shattered-pixel/...", "https://explore.transifex.com/shattered-pixel/shattered-pixel-dungeon/");
        transifex.setRect(((float)Camera.main.width - colWidth) / 2.0f, purigro.bottom() + 12.0f, colWidth, 0.0f);
        content.add(transifex);
        this.addLine(transifex.top() - 4.0f, content);
        this.addLine(transifex.bottom() + 4.0f, content);
        CreditsBlock freesound = new CreditsBlock(true, 0xFFFF44, null, null, "Shattered Pixel Dungeon uses the following sound samples from _freesound.org_:\n\nCreative Commons Attribution License:\n_SFX ATTACK SWORD 001.wav_ by _JoelAudio_\n_Pack: Slingshots and Longbows_ by _saturdaysoundguy_\n_Cracking/Crunching, A.wav_ by _InspectorJ_\n_Extracting a sword.mp3_ by _Taira Komori_\n_Pack: Uni Sound Library_ by _timmy h123_\n\nCreative Commons Zero License:\n_Pack: Movie Foley: Swords_ by _Black Snow_\n_machine gun shot 2.flac_ by _qubodup_\n_m240h machine gun burst 4.flac_ by _qubodup_\n_Pack: Onomatopoeia_ by _Adam N_\n_Pack: Watermelon_ by _lolamadeus_\n_metal chain_ by _Mediapaja2009_\n_Pack: Sword Clashes Pack_ by _JohnBuhr_\n_Pack: Metal Clangs and Pings_ by _wilhellboy_\n_Pack: Stabbing Stomachs & Crushing Skulls_ by _TheFilmLook_\n_Sheep bleating_ by _zachrau_\n_Lemon,Juicy,Squeeze,Fruit.wav_ by _Filipe Chagas_\n_Lemon,Squeeze,Squishy,Fruit.wav_ by _Filipe Chagas_", "freesound.org", "https://www.freesound.org");
        freesound.setRect(transifex.left() - 10.0f, transifex.bottom() + 8.0f, colWidth + 20.0f, 0.0f);
        content.add(freesound);
        content.setSize(fullWidth, freesound.bottom() + 10.0f + insets.bottom);
        list.setRect(0.0f, 0.0f, w, h);
        list.scrollTo(0.0f, 0.0f);
        ExitButton btnExit = new ExitButton();
        int ofs = PixelScene.landscape() ? 0 : 4;
        btnExit.setPos((float)Camera.main.width - btnExit.width() - (float)ofs, ofs);
        this.add(btnExit);
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchScene(TitleScene.class);
    }

    private void addLine(float y, Group content) {
        ColorBlock line = new ColorBlock(Camera.main.width, 1.0f, -13421773);
        line.y = y;
        content.add(line);
    }

    private static class CreditsBlock
    extends Component {
        boolean large;
        RenderedTextBlock title;
        Image avatar;
        Flare flare;
        RenderedTextBlock body;
        RenderedTextBlock link;
        ColorBlock linkUnderline;
        PointerArea linkButton;

        private CreditsBlock(boolean large, int highlight, String title, Image avatar, String body, String linkText, final String linkUrl) {
            this.large = large;
            if (title != null) {
                this.title = PixelScene.renderTextBlock(title, large ? 8 : 6);
                if (highlight != -1) {
                    this.title.hardlight(highlight);
                }
                this.add(this.title);
            }
            if (avatar != null) {
                this.avatar = avatar;
                this.add(this.avatar);
            }
            if (large && highlight != -1 && this.avatar != null) {
                this.flare = new Flare(7, 24.0f).color(highlight, true).show(this.avatar, 0.0f);
                this.flare.angularSpeed = 20.0f;
            }
            this.body = PixelScene.renderTextBlock(body, 6);
            if (highlight != -1) {
                this.body.setHightlighting(true, highlight);
            }
            if (large) {
                this.body.align(2);
            }
            this.add(this.body);
            if (linkText != null && linkUrl != null) {
                int color = -1;
                if (highlight != -1) {
                    color = 0xFF000000 | highlight;
                }
                this.linkUnderline = new ColorBlock(1.0f, 1.0f, color);
                this.add(this.linkUnderline);
                this.link = PixelScene.renderTextBlock(linkText, 6);
                if (highlight != -1) {
                    this.link.hardlight(highlight);
                }
                this.add(this.link);
                this.linkButton = new PointerArea(0.0f, 0.0f, 0.0f, 0.0f){

                    @Override
                    protected void onClick(PointerEvent event) {
                        ShatteredPixelDungeon.platform.openURI(linkUrl);
                    }
                };
                this.add(this.linkButton);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            float topY = this.top();
            if (this.title != null) {
                this.title.maxWidth((int)this.width());
                this.title.setPos(this.x + (this.width() - this.title.width()) / 2.0f, topY);
                topY += this.title.height() + (float)(this.large ? 2 : 1);
            }
            if (this.large) {
                if (this.avatar != null) {
                    this.avatar.x = this.x + (this.width() - this.avatar.width()) / 2.0f;
                    this.avatar.y = topY;
                    PixelScene.align(this.avatar);
                    if (this.flare != null) {
                        this.flare.point(this.avatar.center());
                    }
                    topY = this.avatar.y + this.avatar.height() + 2.0f;
                }
                this.body.maxWidth((int)this.width());
                this.body.setPos(this.x + (this.width() - this.body.width()) / 2.0f, topY);
                topY += this.body.height() + 2.0f;
            } else if (this.avatar != null) {
                this.avatar.x = this.x;
                this.body.maxWidth((int)(this.width() - this.avatar.width - 1.0f));
                float fullAvHeight = Math.max(this.avatar.height(), 16.0f);
                if (fullAvHeight > this.body.height()) {
                    this.avatar.y = topY + (fullAvHeight - this.avatar.height()) / 2.0f;
                    PixelScene.align(this.avatar);
                    this.body.setPos(this.avatar.x + this.avatar.width() + 1.0f, topY + (fullAvHeight - this.body.height()) / 2.0f);
                    topY += fullAvHeight + 1.0f;
                } else {
                    this.avatar.y = topY + (this.body.height() - fullAvHeight) / 2.0f;
                    PixelScene.align(this.avatar);
                    this.body.setPos(this.avatar.x + this.avatar.width() + 1.0f, topY);
                    topY += this.body.height() + 2.0f;
                }
            } else {
                this.body.maxWidth((int)this.width());
                this.body.setPos(this.x, topY += 1.0f);
                topY += this.body.height() + 2.0f;
            }
            if (this.link != null) {
                if (this.large) {
                    topY += 1.0f;
                }
                this.link.maxWidth((int)this.width());
                this.link.setPos(this.x + (this.width() - this.link.width()) / 2.0f, topY);
                topY += this.link.height() + 2.0f;
                this.linkButton.x = this.link.left() - 1.0f;
                this.linkButton.y = this.link.top() - 1.0f;
                this.linkButton.width = this.link.width() + 2.0f;
                this.linkButton.height = this.link.height() + 2.0f;
                this.linkUnderline.size(this.link.width(), PixelScene.align(0.49f));
                this.linkUnderline.x = this.link.left();
                this.linkUnderline.y = this.link.bottom() + 1.0f;
            }
            this.height = Math.max(this.height, (topY -= 2.0f) - this.top());
        }
    }
}

