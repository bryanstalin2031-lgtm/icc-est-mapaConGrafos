package structures.graphs;

import java.util.Set;

public class PathResult<T> {
    private final Set<T> visitados;
    private final Set<T> path;
    
    public PathResult(Set<T> visitados, Set<T> path) {
        this.visitados = visitados;
        this.path = path;
    }

    public Set<T> getVisitados() {
        return visitados;
    }

    public Set<T> getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "PathResult\nvisitados=" + visitados + "\n" +
               (!path.isEmpty() ? "path=" + path : "No se encontro un camino entre los nodos");
    }
}