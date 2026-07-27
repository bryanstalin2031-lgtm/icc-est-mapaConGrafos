package persistence;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.MapPoint;
import structures.graphs.Graph;
import structures.node.Node;

public class FileGraphRepository implements GraphRepository{
    @Override
    public void save(Graph<MapPoint> graph, String filePath) throws Exception {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            Set<Node<MapPoint>> nodes = graph.getNodes();
            for(Node<MapPoint> node : nodes){
                MapPoint p = node.getValue();
                writer.write("NODE," + p.getId() + "," + p.getX() + "," + p.getY());
                writer.newLine();
            }

            Map<Node<MapPoint>, Set<Node<MapPoint>>> adjList = graph.getGraph();
            for(Map.Entry<Node<MapPoint>, Set<Node<MapPoint>>> entry : adjList.entrySet()){
                String fromId = entry.getKey().getValue().getId();
                for(Node<MapPoint> toNode : entry.getValue()){
                    String toId = toNode.getValue().getId();
                    writer.write("EDGE," + fromId + "," + toId + ", true");
                    writer.newLine();
                }
            }
        }
    }

    @Override 
    public Graph<MapPoint> load(String filePath) throws Exception{
        Graph<MapPoint> graph = new Graph<>();
        Map<String, MapPoint> pointDirectory = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line; 
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",");
                if(parts[0].equals("NODE")){
                    MapPoint point = new MapPoint(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                    pointDirectory.put(parts[1], point);
                    graph.add(point);

                }else if(parts[0].equals("EDGE")){
                    MapPoint p1 = pointDirectory.get(parts[1]);
                    MapPoint p2 = pointDirectory.get(parts[2]);
                    if(p1 != null && p2 != null){
                        if(parts[3].equals("true")){
                            graph.addEdge(p1,p2);

                        }else{
                            graph.addEdgeUni(p1, p2);
                        }
                    }
                }
            }
        }
        return graph;
    }


    
}
