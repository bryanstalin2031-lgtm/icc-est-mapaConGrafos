package persistence;

import structures.graphs.Graph;
import models.MapPoint;

public interface GraphRepository {
    void save(Graph<MapPoint> graph, String filePath) throws Exception;
    Graph<MapPoint> load(String filePath) throws Exception;
}