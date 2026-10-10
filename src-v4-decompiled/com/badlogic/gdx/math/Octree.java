/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

public class Octree<T> {
    final int maxItemsPerNode;
    final Pool<OctreeNode> nodePool = new Pool<OctreeNode>(){

        @Override
        protected OctreeNode newObject() {
            return new OctreeNode();
        }
    };
    protected OctreeNode root;
    final Collider<T> collider;
    static final Vector3 tmp = new Vector3();

    public Octree(Vector3 minimum, Vector3 maximum, int maxDepth, int maxItemsPerNode, Collider<T> collider) {
        Vector3 realMin = new Vector3(Math.min(minimum.x, maximum.x), Math.min(minimum.y, maximum.y), Math.min(minimum.z, maximum.z));
        Vector3 realMax = new Vector3(Math.max(minimum.x, maximum.x), Math.max(minimum.y, maximum.y), Math.max(minimum.z, maximum.z));
        this.root = this.createNode(realMin, realMax, maxDepth);
        this.collider = collider;
        this.maxItemsPerNode = maxItemsPerNode;
    }

    OctreeNode createNode(Vector3 min, Vector3 max, int level) {
        OctreeNode node = this.nodePool.obtain();
        node.bounds.set(min, max);
        node.level = level;
        node.leaf = true;
        return node;
    }

    public void add(T object) {
        this.root.add(object);
    }

    public void remove(T object) {
        this.root.remove(object);
    }

    public void update(T object) {
        this.root.remove(object);
        this.root.add(object);
    }

    public ObjectSet<T> getAll(ObjectSet<T> resultSet) {
        this.root.getAll(resultSet);
        return resultSet;
    }

    public ObjectSet<T> query(BoundingBox aabb, ObjectSet<T> result) {
        this.root.query(aabb, result);
        return result;
    }

    public ObjectSet<T> query(Frustum frustum, ObjectSet<T> result) {
        this.root.query(frustum, result);
        return result;
    }

    public T rayCast(Ray ray, RayCastResult<T> result) {
        result.distance = result.maxDistanceSq;
        this.root.rayCast(ray, result);
        return result.geometry;
    }

    public ObjectSet<BoundingBox> getNodesBoxes(ObjectSet<BoundingBox> boxes) {
        this.root.getBoundingBox(boxes);
        return boxes;
    }

    protected class OctreeNode {
        int level;
        final BoundingBox bounds = new BoundingBox();
        boolean leaf;
        private OctreeNode[] children;
        private final Array<T> geometries;

        protected OctreeNode() {
            this.geometries = new Array(Math.min(16, Octree.this.maxItemsPerNode));
        }

        private void split() {
            float midx = (this.bounds.max.x + this.bounds.min.x) * 0.5f;
            float midy = (this.bounds.max.y + this.bounds.min.y) * 0.5f;
            float midz = (this.bounds.max.z + this.bounds.min.z) * 0.5f;
            int deeperLevel = this.level - 1;
            this.leaf = false;
            if (this.children == null) {
                this.children = new OctreeNode[8];
            }
            this.children[0] = Octree.this.createNode(new Vector3(this.bounds.min.x, midy, midz), new Vector3(midx, this.bounds.max.y, this.bounds.max.z), deeperLevel);
            this.children[1] = Octree.this.createNode(new Vector3(midx, midy, midz), new Vector3(this.bounds.max.x, this.bounds.max.y, this.bounds.max.z), deeperLevel);
            this.children[2] = Octree.this.createNode(new Vector3(midx, midy, this.bounds.min.z), new Vector3(this.bounds.max.x, this.bounds.max.y, midz), deeperLevel);
            this.children[3] = Octree.this.createNode(new Vector3(this.bounds.min.x, midy, this.bounds.min.z), new Vector3(midx, this.bounds.max.y, midz), deeperLevel);
            this.children[4] = Octree.this.createNode(new Vector3(this.bounds.min.x, this.bounds.min.y, midz), new Vector3(midx, midy, this.bounds.max.z), deeperLevel);
            this.children[5] = Octree.this.createNode(new Vector3(midx, this.bounds.min.y, midz), new Vector3(this.bounds.max.x, midy, this.bounds.max.z), deeperLevel);
            this.children[6] = Octree.this.createNode(new Vector3(midx, this.bounds.min.y, this.bounds.min.z), new Vector3(this.bounds.max.x, midy, midz), deeperLevel);
            this.children[7] = Octree.this.createNode(new Vector3(this.bounds.min.x, this.bounds.min.y, this.bounds.min.z), new Vector3(midx, midy, midz), deeperLevel);
            for (OctreeNode child : this.children) {
                for (Object geometry : this.geometries) {
                    child.add(geometry);
                }
            }
            this.geometries.clear();
        }

        private void merge() {
            this.clearChildren();
            this.leaf = true;
        }

        private void free() {
            this.geometries.clear();
            if (!this.leaf) {
                this.clearChildren();
            }
            Octree.this.nodePool.free(this);
        }

        private void clearChildren() {
            for (int i = 0; i < 8; ++i) {
                this.children[i].free();
                this.children[i] = null;
            }
        }

        protected void add(T geometry) {
            if (!Octree.this.collider.intersects(this.bounds, geometry)) {
                return;
            }
            if (!this.leaf) {
                for (OctreeNode child : this.children) {
                    child.add(geometry);
                }
            } else if (this.geometries.size >= Octree.this.maxItemsPerNode && this.level > 0) {
                this.split();
                for (OctreeNode child : this.children) {
                    child.add(geometry);
                }
            } else {
                this.geometries.add(geometry);
            }
        }

        protected boolean remove(T object) {
            if (!this.leaf) {
                boolean removed = false;
                for (OctreeNode node : this.children) {
                    removed |= node.remove(object);
                }
                if (removed) {
                    ObjectSet geometrySet = new ObjectSet();
                    for (OctreeNode node : this.children) {
                        node.getAll(geometrySet);
                    }
                    if (geometrySet.size <= Octree.this.maxItemsPerNode) {
                        for (Object geometry : geometrySet) {
                            this.geometries.add(geometry);
                        }
                        this.merge();
                    }
                }
                return removed;
            }
            return this.geometries.removeValue(object, true);
        }

        protected boolean isLeaf() {
            return this.leaf;
        }

        protected void query(BoundingBox aabb, ObjectSet<T> result) {
            if (!aabb.intersects(this.bounds)) {
                return;
            }
            if (!this.leaf) {
                for (OctreeNode node : this.children) {
                    node.query(aabb, result);
                }
            } else {
                for (Object geometry : this.geometries) {
                    if (!Octree.this.collider.intersects(this.bounds, geometry)) continue;
                    result.add(geometry);
                }
            }
        }

        protected void query(Frustum frustum, ObjectSet<T> result) {
            if (!Intersector.intersectFrustumBounds(frustum, this.bounds)) {
                return;
            }
            if (!this.leaf) {
                for (OctreeNode node : this.children) {
                    node.query(frustum, result);
                }
            } else {
                for (Object geometry : this.geometries) {
                    if (!Octree.this.collider.intersects(frustum, geometry)) continue;
                    result.add(geometry);
                }
            }
        }

        protected void rayCast(Ray ray, RayCastResult<T> result) {
            boolean intersect = Intersector.intersectRayBounds(ray, this.bounds, tmp);
            if (!intersect) {
                return;
            }
            float dst2 = tmp.dst2(ray.origin);
            if (dst2 >= result.maxDistanceSq) {
                return;
            }
            if (!this.leaf) {
                for (OctreeNode child : this.children) {
                    child.rayCast(ray, result);
                }
            } else {
                for (Object geometry : this.geometries) {
                    float distance = Octree.this.collider.intersects(ray, geometry);
                    if (result.geometry != null && !(distance < result.distance)) continue;
                    result.geometry = geometry;
                    result.distance = distance;
                }
            }
        }

        protected void getAll(ObjectSet<T> resultSet) {
            if (!this.leaf) {
                for (OctreeNode child : this.children) {
                    child.getAll(resultSet);
                }
            }
            resultSet.addAll(this.geometries);
        }

        protected void getBoundingBox(ObjectSet<BoundingBox> bounds) {
            if (!this.leaf) {
                for (OctreeNode node : this.children) {
                    node.getBoundingBox(bounds);
                }
            }
            bounds.add(this.bounds);
        }
    }

    public static interface Collider<T> {
        public boolean intersects(BoundingBox var1, T var2);

        public boolean intersects(Frustum var1, T var2);

        public float intersects(Ray var1, T var2);
    }

    public static class RayCastResult<T> {
        T geometry;
        float distance;
        float maxDistanceSq = Float.MAX_VALUE;
    }
}

