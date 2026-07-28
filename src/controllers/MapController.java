package controllers;

import models.MapPoint;
import models.VisualizationMode;
import persistence.FileGraphRepository;
import persistence.GraphRepository;
import structures.graphs.Graph;
import structures.graphs.PathFinder;
import structures.graphs.PathResult;
import structures.graphs.implementations.AStarPathFinder;
import structures.graphs.implementations.BFSPathFinder;
import structures.graphs.implementations.DFSPathFinder;
import structures.graphs.implementations.DijkstraPathFinder;
import structures.graphs.implementations.Heuristic;

public class MapController {
    private Graph<MapPoint> graph;
    private GraphRepository repository;
    private VisualizationMode currentMode;

    public MapController(){
        this.graph = new Graph<>();
        this.repository = new FileGraphRepository();
        this.currentMode = VisualizationMode.EXPLORATION;
    }

    public void addPoint(String id, int x, int y){
        MapPoint point = new MapPoint(id, x, y);
        graph.add(point);
    }
    
    public void addConnection(MapPoint p1, MapPoint p2, boolean isBidirectional){
        if(isBidirectional){
            graph.addEdge(p1, p2);
        }else{
            graph.addEdgeUni(p1, p2);
        }
    }

    public PathResult<MapPoint> executeSearch(String algorithm, MapPoint start, MapPoint end){
        PathFinder<MapPoint> pathFinder;
             if (algorithm.equalsIgnoreCase("BFS")) {
            pathFinder = new BFSPathFinder<>();
        } else if (algorithm.equalsIgnoreCase("DFS")) {
            pathFinder = new DFSPathFinder<>();
        } else if (algorithm.equalsIgnoreCase("DIJKSTRA")) {
            pathFinder = new DijkstraPathFinder<>();
        } else if (algorithm.equalsIgnoreCase("ASTAR")) {
            // Heurística de distancia euclidiana (línea recta)
            pathFinder = new AStarPathFinder<>(new Heuristic<MapPoint>() {
                @Override
                public double calculate(MapPoint current, MapPoint goal) {
                    double dx = current.getX() - goal.getX();
                    double dy = current.getY() - goal.getY();
                    return Math.sqrt(dx * dx + dy * dy);
                }
            });
        } else {
            pathFinder = new BFSPathFinder<>();
        }
        
        return pathFinder.find(graph, start, end);
    }

    public void saveConfiguration(String path) throws Exception {
        repository.save(graph, path);
    }

    public void loadConfiguration(String path) throws Exception {
        this.graph = repository.load(path);
    }

    public Graph<MapPoint> getGraph() {
        return graph;
    }

    public void setVisualizationMode(VisualizationMode mode) {
        this.currentMode = mode;
    }

    public VisualizationMode getVisualizationMode() {
        return currentMode;
    }
}


