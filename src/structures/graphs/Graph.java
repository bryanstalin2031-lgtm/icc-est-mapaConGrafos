package structures.graphs;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import structures.node.Node;

public class Graph<T> {
    private Map<Node<T>, Set<Node<T>>> graph;

    public Graph() {
        this.graph = new LinkedHashMap<>();
    }

    public void add(T data) {
        Node<T> node = new Node<>(data);
        graph.putIfAbsent(node, new LinkedHashSet<>());
    }

    public void addEdge(T v1, T v2) {
        Node<T> nv1 = new Node<>(v1);
        Node<T> nv2 = new Node<>(v2);
        add(v1);
        add(v2);
        graph.get(nv1).add(nv2);
        graph.get(nv2).add(nv1);
    }

    public void addEdgeUni(T v1, T v2) {
        Node<T> nv1 = new Node<>(v1);
        Node<T> nv2 = new Node<>(v2);
        add(v1);
        add(v2);
        graph.get(nv1).add(nv2);
    }

    public Set<Node<T>> getNodes() {
        return graph.keySet();
    }

    public Map<Node<T>, Set<Node<T>>> getGraph() {
        return graph;
    }

    public boolean contains(T data) {
        return graph.containsKey(new Node<>(data));
    }

    public void remove(T data) {
        Node<T> nodeToRemove = new Node<>(data);
        for (Set<Node<T>> connections : graph.values()) {
            connections.remove(nodeToRemove);
        }
        graph.remove(nodeToRemove);
    }

    public void removeEdge(T v1, T v2) {
        Node<T> nv1 = new Node<>(v1);
        Node<T> nv2 = new Node<>(v2);
        if (graph.containsKey(nv1)) graph.get(nv1).remove(nv2);
        if (graph.containsKey(nv2)) graph.get(nv2).remove(nv1);
    }

    public void removeEdgeUni(T v1, T v2) {
        Node<T> nv1 = new Node<>(v1);
        Node<T> nv2 = new Node<>(v2);
        if (graph.containsKey(nv1)) graph.get(nv1).remove(nv2);
    }

    public Set<Node<T>> getVecinos(Node<T> nC) {
        return graph.getOrDefault(nC, new LinkedHashSet<>());
    }

    public Set<Node<T>> getVecinos(T value) {
        return graph.getOrDefault(new Node<>(value), new LinkedHashSet<>());
    }

    public void printGraph() {
        for (Map.Entry<Node<T>, Set<Node<T>>> entry : graph.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (Node<T> conexion : entry.getValue()) {
                System.out.print(conexion);
            }
            System.out.println();
        }
    }
}