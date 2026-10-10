/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    public static <T extends Node> void setPrice(List<T> nodes, int value) {
        for (Node node : nodes) {
            node.price(value);
        }
    }

    public static <T extends Node> void buildDistanceMap(Collection<T> nodes, Node focus) {
        for (Node node : nodes) {
            node.distance(Integer.MAX_VALUE);
        }
        LinkedList<Node> queue = new LinkedList<Node>();
        focus.distance(0);
        queue.add(focus);
        while (!queue.isEmpty()) {
            Node node;
            node = (Node)queue.poll();
            int distance = node.distance();
            int price = node.price();
            for (Node node2 : node.edges()) {
                if (node2.distance() <= distance + price) continue;
                queue.add(node2);
                node2.distance(distance + price);
            }
        }
    }

    public static <T extends Node> List<T> buildPath(Collection<T> nodes, T from, T to) {
        ArrayList<Node> path = new ArrayList<Node>();
        T room = from;
        while (room != to) {
            int min = room.distance();
            Node next = null;
            Collection<? extends Node> edges = room.edges();
            for (Node node : edges) {
                int distance = node.distance();
                if (distance >= min) continue;
                min = distance;
                next = node;
            }
            if (next == null) {
                return null;
            }
            path.add(next);
            room = next;
        }
        return path;
    }

    public static interface Node {
        public int distance();

        public void distance(int var1);

        public int price();

        public void price(int var1);

        public Collection<? extends Node> edges();
    }
}

