/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.ScrollArea;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;

public class CellSelector
extends ScrollArea {
    public Listener listener = null;
    public boolean enabled;
    private float dragThreshold;
    private float mouseZoom;
    private boolean pinching = false;
    private PointerEvent another;
    private float startZoom;
    private float startSpan;
    private boolean dragging = false;
    private PointF lastPos = new PointF();
    private GameAction heldAction1 = SPDAction.NONE;
    private GameAction heldAction2 = SPDAction.NONE;
    private GameAction heldAction3 = SPDAction.NONE;
    private float heldDelay = 0.0f;
    private boolean delayingForRelease = false;
    private Signal.Listener<KeyEvent> keyListener = new Signal.Listener<KeyEvent>(){

        @Override
        public boolean onSignal(KeyEvent event) {
            GameAction action = KeyBindings.getActionForKey(event);
            if (!event.pressed) {
                if (action == SPDAction.ZOOM_IN) {
                    CellSelector.this.zoom(CellSelector.this.camera.zoom + 1.0f);
                    CellSelector.this.mouseZoom = CellSelector.this.camera.zoom;
                    return true;
                }
                if (action == SPDAction.ZOOM_OUT) {
                    CellSelector.this.zoom(CellSelector.this.camera.zoom - 1.0f);
                    CellSelector.this.mouseZoom = CellSelector.this.camera.zoom;
                    return true;
                }
                if (CellSelector.this.heldAction1 != SPDAction.NONE && CellSelector.this.heldAction1 == action) {
                    CellSelector.this.heldAction1 = SPDAction.NONE;
                    if (CellSelector.this.heldAction2 != SPDAction.NONE) {
                        CellSelector.this.heldAction1 = CellSelector.this.heldAction2;
                        CellSelector.this.heldAction2 = SPDAction.NONE;
                        if (CellSelector.this.heldAction3 != SPDAction.NONE) {
                            CellSelector.this.heldAction2 = CellSelector.this.heldAction3;
                            CellSelector.this.heldAction3 = SPDAction.NONE;
                        }
                    }
                } else if (CellSelector.this.heldAction2 != SPDAction.NONE && CellSelector.this.heldAction2 == action) {
                    CellSelector.this.heldAction2 = SPDAction.NONE;
                    if (CellSelector.this.heldAction3 != SPDAction.NONE) {
                        CellSelector.this.heldAction2 = CellSelector.this.heldAction3;
                        CellSelector.this.heldAction3 = SPDAction.NONE;
                    }
                } else if (CellSelector.this.heldAction3 != SPDAction.NONE && CellSelector.this.heldAction3 == action) {
                    CellSelector.this.heldAction3 = SPDAction.NONE;
                }
                if (CellSelector.this.heldDelay > 0.0f && !CellSelector.this.delayingForRelease) {
                    CellSelector.this.heldDelay = 0.0f;
                    CellSelector.this.moveFromActions(action, CellSelector.this.heldAction1, CellSelector.this.heldAction2);
                }
                if (CellSelector.this.heldAction1 == GameAction.NONE && CellSelector.this.heldAction2 == GameAction.NONE) {
                    CellSelector.this.resetKeyHold();
                    return true;
                }
                CellSelector.this.delayingForRelease = true;
                CellSelector.this.heldDelay = CellSelector.initialDelay();
            } else {
                if (!CellSelector.this.directionFromAction(action).isZero()) {
                    Dungeon.hero.resting = false;
                    CellSelector.this.lastCellMoved = -1;
                    if (CellSelector.this.heldAction1 == SPDAction.NONE) {
                        CellSelector.this.heldAction1 = action;
                        CellSelector.this.heldDelay = CellSelector.initialDelay();
                        CellSelector.this.delayingForRelease = false;
                    } else if (CellSelector.this.heldAction2 == SPDAction.NONE) {
                        CellSelector.this.heldAction2 = action;
                    } else {
                        CellSelector.this.heldAction3 = action;
                    }
                    return true;
                }
                if (Dungeon.hero != null && Dungeon.hero.resting) {
                    Dungeon.hero.resting = false;
                    return true;
                }
            }
            return false;
        }
    };
    private GameAction leftStickAction = SPDAction.NONE;
    private int lastCellMoved = 0;

    public CellSelector(DungeonTilemap map) {
        super(map);
        this.camera = map.camera();
        this.dragThreshold = PixelScene.defaultZoom * 16 / 2;
        this.mouseZoom = this.camera.zoom;
        KeyEvent.addKeyListener(this.keyListener);
    }

    @Override
    protected void onScroll(ScrollEvent event) {
        float diff = event.amount / 10.0f;
        diff /= (this.camera.zoom + 1.0f) / this.camera.zoom - 1.0f;
        diff = Math.min(1.0f, diff);
        this.mouseZoom = GameMath.gate(PixelScene.minZoom, this.mouseZoom - diff, PixelScene.maxZoom);
        this.zoom(Math.round(this.mouseZoom));
    }

    @Override
    protected void onClick(PointerEvent event) {
        if (this.dragging) {
            this.dragging = false;
        } else {
            PointF p = Camera.main.screenToCamera((int)event.current.x, (int)event.current.y);
            if (Dungeon.hero.sprite != null && Dungeon.hero.sprite.overlapsPoint(p.x, p.y)) {
                PointF c = DungeonTilemap.tileCenterToWorld(Dungeon.hero.pos);
                if (Math.abs(p.x - c.x) <= 12.0f && Math.abs(p.y - c.y) <= 12.0f) {
                    this.select(Dungeon.hero.pos, event.button);
                    return;
                }
            }
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob.sprite == null || !mob.sprite.overlapsPoint(p.x, p.y)) continue;
                PointF c = DungeonTilemap.tileCenterToWorld(mob.pos);
                if (!(Math.abs(p.x - c.x) <= 12.0f) || !(Math.abs(p.y - c.y) <= 12.0f)) continue;
                this.select(mob.pos, event.button);
                return;
            }
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (heap.sprite == null || !heap.sprite.overlapsPoint(p.x, p.y)) continue;
                PointF c = DungeonTilemap.tileCenterToWorld(heap.pos);
                if (!(Math.abs(p.x - c.x) <= 12.0f) || !(Math.abs(p.y - c.y) <= 12.0f)) continue;
                this.select(heap.pos, event.button);
                return;
            }
            this.select(((DungeonTilemap)this.target).screenToTile((int)event.current.x, (int)event.current.y, true), event.button);
        }
    }

    private float zoom(float value) {
        value = GameMath.gate(PixelScene.minZoom, value, PixelScene.maxZoom);
        SPDSettings.zoom((int)(value - (float)PixelScene.defaultZoom));
        this.camera.zoom(value);
        for (Char c : Actor.chars()) {
            if (c.sprite == null || c.sprite.isMoving) continue;
            c.sprite.point(c.sprite.worldToCamera(c.pos));
        }
        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.sprite == null) continue;
            heap.sprite.point(heap.sprite.worldToCamera(heap.pos));
        }
        return value;
    }

    public void select(int cell, int button) {
        if (this.enabled && Dungeon.hero.ready && !GameScene.interfaceBlockingHero() && this.listener != null && cell != -1) {
            switch (button) {
                default: {
                    this.listener.onSelect(cell);
                    break;
                }
                case 1: {
                    this.listener.onRightClick(cell);
                }
            }
            GameScene.ready();
        } else {
            GameScene.cancel();
        }
    }

    @Override
    protected void onPointerDown(PointerEvent event) {
        this.camera.edgeScroll.set(-1.0f);
        if (event != this.curEvent && this.another == null) {
            if (this.curEvent.type == PointerEvent.Type.UP) {
                this.curEvent = event;
                this.onPointerDown(event);
                return;
            }
            this.pinching = true;
            this.another = event;
            this.startSpan = PointF.distance(this.curEvent.current, this.another.current);
            this.startZoom = this.camera.zoom;
            this.dragging = false;
        } else if (event != this.curEvent) {
            this.reset();
        }
    }

    @Override
    protected void onPointerUp(PointerEvent event) {
        this.camera.edgeScroll.set(1.0f);
        if (this.pinching && (event == this.curEvent || event == this.another)) {
            this.pinching = false;
            this.zoom(Math.round(this.camera.zoom));
            this.dragging = true;
            if (event == this.curEvent) {
                this.curEvent = this.another;
            }
            this.another = null;
            this.lastPos.set(this.curEvent.current);
        }
    }

    @Override
    protected void onDrag(PointerEvent event) {
        if (this.pinching) {
            float curSpan = PointF.distance(this.curEvent.current, this.another.current);
            float zoom = this.startZoom * curSpan / this.startSpan;
            this.camera.zoom(GameMath.gate(PixelScene.minZoom, zoom - zoom % 0.1f, PixelScene.maxZoom));
        } else if (!this.dragging && PointF.distance(event.current, event.start) > this.dragThreshold) {
            this.dragging = true;
            this.lastPos.set(event.current);
        } else if (this.dragging) {
            this.camera.shift(PointF.diff(this.lastPos, event.current).invScale(this.camera.zoom));
            this.lastPos.set(event.current);
        }
    }

    private static float initialDelay() {
        switch (SPDSettings.movementHoldSensitivity()) {
            case 0: {
                return Float.POSITIVE_INFINITY;
            }
            case 1: {
                return 0.13f;
            }
            case 2: {
                return 0.09f;
            }
            default: {
                return 0.06f;
            }
            case 4: 
        }
        return 0.03f;
    }

    @Override
    public void update() {
        super.update();
        GameAction newLeftStick = this.actionFromStick(ControllerHandler.leftStickPosition.x, ControllerHandler.leftStickPosition.y);
        if (newLeftStick == this.leftStickAction && this.leftStickAction == GameAction.NONE && this.heldAction1 == SPDAction.NONE || GameScene.interfaceBlockingHero()) {
            return;
        }
        if (newLeftStick != this.leftStickAction) {
            if (this.leftStickAction == SPDAction.NONE) {
                this.heldDelay = CellSelector.initialDelay();
                Dungeon.hero.resting = false;
            } else if (newLeftStick == SPDAction.NONE && this.heldDelay > 0.0f) {
                this.heldDelay = 0.0f;
                this.moveFromActions(this.leftStickAction);
            }
            this.leftStickAction = newLeftStick;
        }
        if (this.heldDelay > 0.0f) {
            this.heldDelay -= Game.elapsed;
        }
        if ((this.heldAction1 != SPDAction.NONE || this.leftStickAction != SPDAction.NONE) && Dungeon.hero.ready) {
            this.processKeyHold();
        } else if (Dungeon.hero.ready) {
            this.lastCellMoved = -1;
        }
    }

    private boolean moveFromActions(GameAction ... actions) {
        if (Dungeon.hero == null || !Dungeon.hero.ready) {
            return false;
        }
        if (GameScene.cancelCellSelector()) {
            return false;
        }
        Point direction = new Point();
        for (GameAction action : actions) {
            direction.offset(this.directionFromAction(action));
        }
        int cell = Dungeon.hero.pos;
        cell = (int)((float)cell + GameMath.gate(-1.0f, direction.x, 1.0f));
        if ((cell = (int)((float)cell + GameMath.gate(-1.0f, direction.y, 1.0f) * (float)Dungeon.level.width())) != Dungeon.hero.pos && cell != this.lastCellMoved) {
            this.lastCellMoved = cell;
            if (Dungeon.hero.handle(cell)) {
                Dungeon.hero.next();
            }
            return true;
        }
        return false;
    }

    private Point directionFromAction(GameAction action) {
        if (action == SPDAction.N) {
            return new Point(0, -1);
        }
        if (action == SPDAction.NE) {
            return new Point(1, -1);
        }
        if (action == SPDAction.E) {
            return new Point(1, 0);
        }
        if (action == SPDAction.SE) {
            return new Point(1, 1);
        }
        if (action == SPDAction.S) {
            return new Point(0, 1);
        }
        if (action == SPDAction.SW) {
            return new Point(-1, 1);
        }
        if (action == SPDAction.W) {
            return new Point(-1, 0);
        }
        if (action == SPDAction.NW) {
            return new Point(-1, -1);
        }
        return new Point();
    }

    private GameAction actionFromStick(float x, float y) {
        if (x > 0.5f) {
            if (y < -0.5f) {
                return SPDAction.NE;
            }
            if (y > 0.5f) {
                return SPDAction.SE;
            }
            if (x > 0.8f) {
                return SPDAction.E;
            }
        } else if (x < -0.5f) {
            if (y < -0.5f) {
                return SPDAction.NW;
            }
            if (y > 0.5f) {
                return SPDAction.SW;
            }
            if (x < -0.8f) {
                return SPDAction.W;
            }
        } else {
            if (y > 0.8f) {
                return SPDAction.S;
            }
            if (y < -0.8f) {
                return SPDAction.N;
            }
        }
        return SPDAction.NONE;
    }

    public void processKeyHold() {
        if (!this.directionFromAction(this.leftStickAction).isZero() && this.heldDelay < 0.0f) {
            Dungeon.hero.ready = true;
            this.enabled = true;
            Dungeon.observe();
            if (this.moveFromActions(this.leftStickAction)) {
                Dungeon.hero.ready = false;
            }
        } else if (!this.directionFromAction(this.heldAction1).offset(this.directionFromAction(this.heldAction2)).isZero() && this.heldDelay <= 0.0f) {
            Dungeon.hero.ready = true;
            this.enabled = true;
            Dungeon.observe();
            if (this.moveFromActions(this.heldAction1, this.heldAction2)) {
                Dungeon.hero.ready = false;
            }
        }
    }

    public void resetKeyHold() {
        this.heldAction2 = this.heldAction3 = SPDAction.NONE;
        this.heldAction1 = this.heldAction3;
    }

    public void cancel() {
        if (this.listener != null) {
            this.listener.onSelect(null);
        }
        GameScene.ready();
    }

    @Override
    public void reset() {
        super.reset();
        this.another = null;
        if (this.pinching) {
            this.pinching = false;
            this.zoom(Math.round(this.camera.zoom));
        }
    }

    public void enable(boolean value) {
        if (this.enabled != value) {
            this.enabled = value;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        KeyEvent.removeKeyListener(this.keyListener);
    }

    public static abstract class Listener {
        public abstract void onSelect(Integer var1);

        public void onRightClick(Integer cell) {
        }

        public abstract String prompt();
    }
}

