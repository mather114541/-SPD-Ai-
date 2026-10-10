/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import java.io.Serializable;

public class OrientedBoundingBox
implements Serializable {
    private static final long serialVersionUID = 3864065514676250557L;
    private static final Vector3[] tempAxes;
    private static final Vector3[] tempVertices;
    private static final Vector3[] tmpVectors;
    private final BoundingBox bounds = new BoundingBox();
    public final Matrix4 transform = new Matrix4();
    private final Matrix4 inverseTransform = new Matrix4();
    private final Vector3[] axes = new Vector3[3];
    private final Vector3[] vertices = new Vector3[8];

    public OrientedBoundingBox() {
        this.bounds.clr();
        this.init();
    }

    public OrientedBoundingBox(BoundingBox bounds) {
        this.bounds.set(bounds.min, bounds.max);
        this.init();
    }

    public OrientedBoundingBox(BoundingBox bounds, Matrix4 transform) {
        this.bounds.set(bounds.min, bounds.max);
        this.transform.set(transform);
        this.init();
    }

    private void init() {
        int i;
        for (i = 0; i < this.axes.length; ++i) {
            this.axes[i] = new Vector3();
        }
        for (i = 0; i < this.vertices.length; ++i) {
            this.vertices[i] = new Vector3();
        }
        this.update();
    }

    public Vector3[] getVertices() {
        return this.vertices;
    }

    public BoundingBox getBounds() {
        return this.bounds;
    }

    public void setBounds(BoundingBox bounds) {
        this.bounds.set(bounds);
        bounds.getCorner000(this.vertices[0]).mul(this.transform);
        bounds.getCorner001(this.vertices[1]).mul(this.transform);
        bounds.getCorner010(this.vertices[2]).mul(this.transform);
        bounds.getCorner011(this.vertices[3]).mul(this.transform);
        bounds.getCorner100(this.vertices[4]).mul(this.transform);
        bounds.getCorner101(this.vertices[5]).mul(this.transform);
        bounds.getCorner110(this.vertices[6]).mul(this.transform);
        bounds.getCorner111(this.vertices[7]).mul(this.transform);
    }

    public Matrix4 getTransform() {
        return this.transform;
    }

    public void setTransform(Matrix4 transform) {
        this.transform.set(transform);
        this.update();
    }

    public OrientedBoundingBox set(BoundingBox bounds, Matrix4 transform) {
        this.setBounds(bounds);
        this.setTransform(transform);
        return this;
    }

    public Vector3 getCorner000(Vector3 out) {
        return out.set(this.vertices[0]);
    }

    public Vector3 getCorner001(Vector3 out) {
        return out.set(this.vertices[1]);
    }

    public Vector3 getCorner010(Vector3 out) {
        return out.set(this.vertices[2]);
    }

    public Vector3 getCorner011(Vector3 out) {
        return out.set(this.vertices[3]);
    }

    public Vector3 getCorner100(Vector3 out) {
        return out.set(this.vertices[4]);
    }

    public Vector3 getCorner101(Vector3 out) {
        return out.set(this.vertices[5]);
    }

    public Vector3 getCorner110(Vector3 out) {
        return out.set(this.vertices[6]);
    }

    public Vector3 getCorner111(Vector3 out) {
        return out.set(this.vertices[7]);
    }

    public boolean contains(Vector3 v) {
        return this.contains(v, this.inverseTransform);
    }

    private boolean contains(Vector3 v, Matrix4 invTransform) {
        Vector3 localV = tmpVectors[0].set(v).mul(invTransform);
        return this.bounds.contains(localV);
    }

    public boolean contains(BoundingBox b) {
        Vector3 tmpVector = tmpVectors[0];
        return this.contains(b.getCorner000(tmpVector), this.inverseTransform) && this.contains(b.getCorner001(tmpVector), this.inverseTransform) && this.contains(b.getCorner010(tmpVector), this.inverseTransform) && this.contains(b.getCorner011(tmpVector), this.inverseTransform) && this.contains(b.getCorner100(tmpVector), this.inverseTransform) && this.contains(b.getCorner101(tmpVector), this.inverseTransform) && this.contains(b.getCorner110(tmpVector), this.inverseTransform) && this.contains(b.getCorner111(tmpVector), this.inverseTransform);
    }

    public boolean contains(OrientedBoundingBox obb) {
        return this.contains(obb.getCorner000(tmpVectors[0]), this.inverseTransform) && this.contains(obb.getCorner001(tmpVectors[0]), this.inverseTransform) && this.contains(obb.getCorner010(tmpVectors[0]), this.inverseTransform) && this.contains(obb.getCorner011(tmpVectors[0]), this.inverseTransform) && this.contains(obb.getCorner100(tmpVectors[0]), this.inverseTransform) && this.contains(obb.getCorner101(tmpVectors[0]), this.inverseTransform) && this.contains(obb.getCorner110(tmpVectors[0]), this.inverseTransform) && this.contains(obb.getCorner111(tmpVectors[0]), this.inverseTransform);
    }

    public boolean intersects(BoundingBox b) {
        Vector3[] aAxes = this.axes;
        OrientedBoundingBox.tempAxes[0] = aAxes[0];
        OrientedBoundingBox.tempAxes[1] = aAxes[1];
        OrientedBoundingBox.tempAxes[2] = aAxes[2];
        OrientedBoundingBox.tempAxes[3] = Vector3.X;
        OrientedBoundingBox.tempAxes[4] = Vector3.Y;
        OrientedBoundingBox.tempAxes[5] = Vector3.Z;
        OrientedBoundingBox.tempAxes[6] = tmpVectors[0].set(aAxes[0]).crs(Vector3.X);
        OrientedBoundingBox.tempAxes[7] = tmpVectors[1].set(aAxes[0]).crs(Vector3.Y);
        OrientedBoundingBox.tempAxes[8] = tmpVectors[2].set(aAxes[0]).crs(Vector3.Z);
        OrientedBoundingBox.tempAxes[9] = tmpVectors[3].set(aAxes[1]).crs(Vector3.X);
        OrientedBoundingBox.tempAxes[10] = tmpVectors[4].set(aAxes[1]).crs(Vector3.Y);
        OrientedBoundingBox.tempAxes[11] = tmpVectors[5].set(aAxes[1]).crs(Vector3.Z);
        OrientedBoundingBox.tempAxes[12] = tmpVectors[6].set(aAxes[2]).crs(Vector3.X);
        OrientedBoundingBox.tempAxes[13] = tmpVectors[7].set(aAxes[2]).crs(Vector3.Y);
        OrientedBoundingBox.tempAxes[14] = tmpVectors[8].set(aAxes[2]).crs(Vector3.Z);
        Vector3[] aVertices = this.getVertices();
        Vector3[] bVertices = this.getVertices(b);
        return Intersector.hasOverlap(tempAxes, aVertices, bVertices);
    }

    public boolean intersects(OrientedBoundingBox obb) {
        Vector3[] aAxes = this.axes;
        Vector3[] bAxes = obb.axes;
        OrientedBoundingBox.tempAxes[0] = aAxes[0];
        OrientedBoundingBox.tempAxes[1] = aAxes[1];
        OrientedBoundingBox.tempAxes[2] = aAxes[2];
        OrientedBoundingBox.tempAxes[3] = bAxes[0];
        OrientedBoundingBox.tempAxes[4] = bAxes[1];
        OrientedBoundingBox.tempAxes[5] = bAxes[2];
        OrientedBoundingBox.tempAxes[6] = tmpVectors[0].set(aAxes[0]).crs(bAxes[0]);
        OrientedBoundingBox.tempAxes[7] = tmpVectors[1].set(aAxes[0]).crs(bAxes[1]);
        OrientedBoundingBox.tempAxes[8] = tmpVectors[2].set(aAxes[0]).crs(bAxes[2]);
        OrientedBoundingBox.tempAxes[9] = tmpVectors[3].set(aAxes[1]).crs(bAxes[0]);
        OrientedBoundingBox.tempAxes[10] = tmpVectors[4].set(aAxes[1]).crs(bAxes[1]);
        OrientedBoundingBox.tempAxes[11] = tmpVectors[5].set(aAxes[1]).crs(bAxes[2]);
        OrientedBoundingBox.tempAxes[12] = tmpVectors[6].set(aAxes[2]).crs(bAxes[0]);
        OrientedBoundingBox.tempAxes[13] = tmpVectors[7].set(aAxes[2]).crs(bAxes[1]);
        OrientedBoundingBox.tempAxes[14] = tmpVectors[8].set(aAxes[2]).crs(bAxes[2]);
        return Intersector.hasOverlap(tempAxes, this.vertices, obb.vertices);
    }

    private Vector3[] getVertices(BoundingBox b) {
        b.getCorner000(tempVertices[0]);
        b.getCorner001(tempVertices[1]);
        b.getCorner010(tempVertices[2]);
        b.getCorner011(tempVertices[3]);
        b.getCorner100(tempVertices[4]);
        b.getCorner101(tempVertices[5]);
        b.getCorner110(tempVertices[6]);
        b.getCorner111(tempVertices[7]);
        return tempVertices;
    }

    public void mul(Matrix4 transform) {
        this.transform.mul(transform);
        this.update();
    }

    private void update() {
        this.bounds.getCorner000(this.vertices[0]).mul(this.transform);
        this.bounds.getCorner001(this.vertices[1]).mul(this.transform);
        this.bounds.getCorner010(this.vertices[2]).mul(this.transform);
        this.bounds.getCorner011(this.vertices[3]).mul(this.transform);
        this.bounds.getCorner100(this.vertices[4]).mul(this.transform);
        this.bounds.getCorner101(this.vertices[5]).mul(this.transform);
        this.bounds.getCorner110(this.vertices[6]).mul(this.transform);
        this.bounds.getCorner111(this.vertices[7]).mul(this.transform);
        this.axes[0].set(this.transform.val[0], this.transform.val[1], this.transform.val[2]).nor();
        this.axes[1].set(this.transform.val[4], this.transform.val[5], this.transform.val[6]).nor();
        this.axes[2].set(this.transform.val[8], this.transform.val[9], this.transform.val[10]).nor();
        this.inverseTransform.set(this.transform).inv();
    }

    static {
        int i;
        tempAxes = new Vector3[15];
        tempVertices = new Vector3[8];
        tmpVectors = new Vector3[9];
        for (i = 0; i < tmpVectors.length; ++i) {
            OrientedBoundingBox.tmpVectors[i] = new Vector3();
        }
        for (i = 0; i < tempVertices.length; ++i) {
            OrientedBoundingBox.tempVertices[i] = new Vector3();
        }
    }
}

