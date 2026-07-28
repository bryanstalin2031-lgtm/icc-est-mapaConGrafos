package structures.graphs.implementations;

public interface Heuristic<T> {
    double calculate(T current, T goal);

    
}
