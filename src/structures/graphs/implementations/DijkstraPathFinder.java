package structures.graphs.implementations;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import structures.graphs.Graph;
import structures.graphs.PathFinder;
import structures.graphs.PathResult;
import structures.node.Node;

public class DijkstraPathFinder<T> implements PathFinder<T> {

    private static class NodeCost<T> implements Comparable<NodeCost<T>> {
        T value;
        double cost;

        public NodeCost(T value, double cost) {
            this.value = value;
            this.cost = cost;
        }

        @Override
        public int compareTo(NodeCost<T> other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    @Override
    public PathResult<T> find(Graph<T> graph, T start, T end) {
        PriorityQueue<NodeCost<T>> queue = new PriorityQueue<>();
        Set<T> visited = new LinkedHashSet<>();
        Map<Node<T>, Node<T>> parent = new LinkedHashMap<>();
        Map<Node<T>, Double> distances = new LinkedHashMap<>();
        
        Node<T> nStart = new Node<>(start);

        distances.put(nStart, 0.0);
        queue.add(new NodeCost<>(start, 0.0));
        visited.add(start);
        while (!queue.isEmpty()) {
            T current = queue.poll().value;
            Node<T> nCurrent = new Node<>(current);
            visited.add(current);
            if (current.equals(end)) {
                return new PathResult<>(visited, buildPath(parent, end));
            }
            for (Node<T> vecino : graph.getVecinos(current)) {
                double newDist = distances.getOrDefault(nCurrent, Double.MAX_VALUE) + 1.0;

                if (newDist < distances.getOrDefault(vecino, Double.MAX_VALUE)) {
                    distances.put(vecino, newDist);
                    parent.put(vecino, nCurrent);
                    queue.add(new NodeCost<>(vecino.getValue(), newDist));
                }
            }
        }
        return new PathResult<>(visited, new LinkedHashSet<>());
    }
    private Set<T> buildPath(Map<Node<T>, Node<T>> parent, T end) {
        Set<T> path = new LinkedHashSet<>();
        Node<T> nEnd = new Node<>(end);
        Stack<T> pila = new Stack<>();

        for (Node<T> at = nEnd; at != null; at = parent.get(at)) {
            pila.push(at.getValue());
        }

        while (!pila.isEmpty()) {
            path.add(pila.pop());
        }
        return path;
    }
}