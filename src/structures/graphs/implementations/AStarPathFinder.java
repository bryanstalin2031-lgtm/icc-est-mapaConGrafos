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

public class AStarPathFinder<T> implements PathFinder<T> {
    
    private Heuristic<T> heuristic;

    public AStarPathFinder(Heuristic<T> heuristic) {
        this.heuristic = heuristic;
    }

    private static class NodeWrapper<T> implements Comparable<NodeWrapper<T>> {
        T value;
        double fScore;

        public NodeWrapper(T value, double fScore) {
            this.value = value;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(NodeWrapper<T> other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }

    @Override
    public PathResult<T> find(Graph<T> graph, T start, T end) {
        PriorityQueue<NodeWrapper<T>> openSet = new PriorityQueue<>();
        Set<T> visited = new LinkedHashSet<>();
        Map<Node<T>, Node<T>> parent = new LinkedHashMap<>();
        Map<Node<T>, Double> gScore = new LinkedHashMap<>();

        Node<T> nStart = new Node<>(start);
        
        gScore.put(nStart, 0.0);
        openSet.add(new NodeWrapper<>(start, heuristic.calculate(start, end)));
        visited.add(start);

        while (!openSet.isEmpty()) {
            T current = openSet.poll().value;
            Node<T> nCurrent = new Node<>(current);
            visited.add(current);

            if (current.equals(end)) {
                return new PathResult<>(visited, buildPath(parent, end));
            }

            for (Node<T> vecino : graph.getVecinos(current)) {
                double tentativeGScore = gScore.getOrDefault(nCurrent, Double.MAX_VALUE) + 1.0;

                if (tentativeGScore < gScore.getOrDefault(vecino, Double.MAX_VALUE)) {
                    parent.put(vecino, nCurrent);
                    gScore.put(vecino, tentativeGScore);
                    
                    double fScore = tentativeGScore + heuristic.calculate(vecino.getValue(), end);
                    openSet.add(new NodeWrapper<>(vecino.getValue(), fScore));
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