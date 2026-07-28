package structures.graphs.implementations;

import java.util.LinkedHashSet;
import java.util.Set;

import structures.graphs.Graph;
import structures.graphs.PathFinder;
import structures.graphs.PathResult;
import structures.node.Node;

public class DFSPathFinder<T> implements PathFinder<T> {
    @Override
    public PathResult<T> find(Graph<T> graph, T start, T end) {
        Set<T> visited = new LinkedHashSet<>();
        Set<T> path = new LinkedHashSet<>();
        boolean encontrado = dfs(graph, start, end, visited, path);
        
        if (!encontrado) {
            path.clear();
        }
        return new PathResult<>(visited, path);
    }

    private boolean dfs(Graph<T> graph, T current, T end, Set<T> visited, Set<T> path) {
        visited.add(current);
        path.add(current);
        
        Node<T> nC = new Node<>(current);
        Node<T> nE = new Node<>(end);
        
        if (nC.equals(nE)) {
            return true;
        }
        
        for (Node<T> vecino : graph.getVecinos(nC)) {
            if (!visited.contains(vecino.getValue())) {
                boolean encontrado = dfs(graph, vecino.getValue(), end, visited, path);
                if (encontrado) {
                    return true;
                }
            }
        }
        
        path.remove(current);
        return false;
    }
}