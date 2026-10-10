package rl;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;

/**
 * Headless no-op particle emitter.
 *
 * Under RLMode.headless there is no render backend, so the normal
 * particle path (GameScene.emitter()/CellEmitter/Blob.use()) never runs and
 * the emitter fields stay null -> NPE the moment game logic pours/bursts.
 *
 * This emitter overrides every render-driving entry point to do nothing,
 * so game logic that manipulates particles never NPEs and wastes no CPU
 * while headless. Emit() (the per-frame render hook) is never invoked
 * headless because there is no render loop.
 */
public class RLNoopEmitter extends BlobEmitter {

    /** Shared singleton for CallEmitter-style use (no per-blob context). */
    private static final RLNoopEmitter SHARED = new RLNoopEmitter(null);

    public static Emitter shared() {
        return SHARED;
    }

    public RLNoopEmitter(Blob blob) {
        super(blob);
    }

    @Override
    public void pos(float x, float y) {
        // no-op
    }

    @Override
    public void pos(PointF p) {
        // no-op
    }

    @Override
    public void pos(float x, float y, float width, float height) {
        // no-op
    }

    @Override
    public void pos(com.watabou.noosa.Visual target) {
        // no-op
    }

    @Override
    public void pos(com.watabou.noosa.Visual target, float x, float y, float width, float height) {
        // no-op
    }

    @Override
    public void burst(Factory factory, int quantity) {
        // no-op
    }

    @Override
    public void pour(Factory factory, float interval) {
        // no-op
    }

    @Override
    public void start(Factory factory, float interval, int quantity) {
        // no-op
    }

    @Override
    public void startDelayed(Factory factory, float interval, int quantity, float delay) {
        // no-op
    }
}
