/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Point;

public class TextInput
extends Component {
    private Stage stage;
    private Container container;
    private TextField textField;
    private Skin skin;
    private NinePatch bg;

    public TextInput(NinePatch bg, final boolean multiline, final int size) {
        this.bg = bg;
        this.add(bg);
        Viewport viewport = new Viewport(){};
        viewport.setWorldSize(Game.width, Game.height);
        viewport.setScreenBounds(0, 0, Game.width, Game.height);
        viewport.setCamera(new OrthographicCamera());
        SpriteBatch.overrideVertexType = Mesh.VertexDataType.VertexArray;
        this.stage = new Stage(viewport);
        Game.inputHandler.addInputProcessor(this.stage);
        this.container = new Container();
        this.stage.addActor(this.container);
        this.container.setTransform(true);
        this.skin = new Skin(FileUtils.getFileHandle(Files.FileType.Internal, "gdx/textfield.json"));
        TextField.TextFieldStyle style = this.skin.get(TextField.TextFieldStyle.class);
        style.font = Game.platform.getFont(size, "", false, false);
        style.background = null;
        this.textField = multiline ? new TextArea("", style){

            @Override
            public void cut() {
                super.cut();
                TextInput.this.onClipBoardUpdate();
            }

            @Override
            public void copy() {
                super.copy();
                TextInput.this.onClipBoardUpdate();
            }
        } : new TextField("", style){

            @Override
            public void cut() {
                super.cut();
                TextInput.this.onClipBoardUpdate();
            }

            @Override
            public void copy() {
                super.copy();
                TextInput.this.onClipBoardUpdate();
            }
        };
        this.textField.setProgrammaticChangeEvents(true);
        if (!multiline) {
            this.textField.setAlignment(1);
        }
        this.textField.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                BitmapFont f = Game.platform.getFont(size, TextInput.this.textField.getText(), false, false);
                TextField.TextFieldStyle style = TextInput.this.textField.getStyle();
                if (f != style.font) {
                    style.font = f;
                    TextInput.this.textField.setStyle(style);
                }
                TextInput.this.onChanged();
            }
        });
        if (!multiline) {
            this.textField.setTextFieldListener(new TextField.TextFieldListener(){

                @Override
                public void keyTyped(TextField textField, char c) {
                    if (c == '\r' || c == '\n') {
                        TextInput.this.enterPressed();
                    }
                }
            });
        }
        this.textField.setOnscreenKeyboard(new TextField.OnscreenKeyboard(){

            @Override
            public void show(boolean visible) {
                Game.platform.setOnscreenKeyboardVisible(visible, multiline);
            }
        });
        this.container.setActor(this.textField);
        this.stage.setKeyboardFocus(this.textField);
        Game.platform.setOnscreenKeyboardVisible(true, multiline);
    }

    public void enterPressed() {
    }

    public void onChanged() {
    }

    public void onClipBoardUpdate() {
    }

    public void setText(String text) {
        this.textField.setText(text);
        this.textField.setCursorPosition(this.textField.getText().length());
    }

    public void setMaxLength(int maxLength) {
        this.textField.setMaxLength(maxLength);
    }

    public String getText() {
        return this.textField.getText();
    }

    public void copyToClipboard() {
        if (this.textField.getSelection().isEmpty()) {
            this.textField.selectAll();
        }
        this.textField.copy();
    }

    public void pasteFromClipboard() {
        String contents = Gdx.app.getClipboard().getContents();
        if (contents == null) {
            return;
        }
        if (!this.textField.getSelection().isEmpty()) {
            this.textField.cut();
            Gdx.app.getClipboard().setContents(contents);
        }
        String existing = this.textField.getText();
        int cursorIdx = this.textField.getCursorPosition();
        this.textField.setText(existing.substring(0, cursorIdx) + contents + existing.substring(cursorIdx));
        this.textField.setCursorPosition(cursorIdx + contents.length());
    }

    @Override
    protected void layout() {
        super.layout();
        float contX = this.x;
        float contY = this.y;
        float contW = this.width;
        float contH = this.height;
        if (this.bg != null) {
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.bg.size(this.width, this.height);
            contX += (float)this.bg.marginLeft();
            contY += (float)this.bg.marginTop();
            contW -= (float)this.bg.marginHor();
            contH -= (float)this.bg.marginVer();
        }
        float zoom = Camera.main.zoom;
        Camera c = this.camera();
        if (c != null) {
            zoom = c.zoom;
            Point p = c.cameraToScreen(contX, contY);
            contX = (float)p.x / zoom;
            contY = (float)p.y / zoom;
        }
        this.container.align(10);
        this.container.setPosition(contX * zoom, (float)Game.height - contY * zoom);
        this.container.size(contW * zoom, contH * zoom);
    }

    @Override
    public void update() {
        super.update();
        this.stage.act(Game.elapsed);
    }

    @Override
    public void draw() {
        super.draw();
        Quad.releaseIndices();
        Script.unuse();
        Texture.clear();
        this.stage.draw();
        Quad.bindIndices();
        Blending.useDefault();
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (this.stage != null) {
            this.stage.dispose();
            this.skin.dispose();
            Game.inputHandler.removeInputProcessor(this.stage);
            Game.platform.setOnscreenKeyboardVisible(false, false);
            if (!DeviceCompat.isDesktop()) {
                Game.platform.updateSystemUI();
            }
        }
    }
}

