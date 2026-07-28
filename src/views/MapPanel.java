package views;

import controllers.MapController;
import models.MapPoint;
import models.VisualizationMode;
import structures.graphs.PathResult;
import structures.node.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapPanel extends JPanel {
    private final MapController controller;
    private final MainFrame parentFrame;
    private BufferedImage backgroundImage;
    
    private Timer timer;
    private List<MapPoint> visitedList = new ArrayList<>();
    private List<MapPoint> pathList = new ArrayList<>();
    private int visitedIndex = 0;
    private int pathIndex = 0;
    
    public enum EditMode { NONE, ADD_NODE, ADD_EDGE_START, ADD_EDGE_END, DELETE_NODE }
    private EditMode currentEditMode = EditMode.NONE;
    private MapPoint tempEdgeStart = null;

    public MapPanel(MapController controller, MainFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;

        try {
            backgroundImage = ImageIO.read(new File("/Users/bryan/Documents/icc-est-mapaConGrafos/src/resources/maps/map.png"));
            System.out.println("¡Imagen de fondo cargada con éxito!");
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gestionarClicMapa(e);
            }
        });
    }

    private void gestionarClicMapa(MouseEvent e) {
        if (currentEditMode == EditMode.ADD_NODE) {
            String nombre = JOptionPane.showInputDialog(MapPanel.this, "Nombre del lugar o esquina:");
            if (nombre != null && !nombre.trim().isEmpty()) {
                controller.addPoint(nombre.trim(), e.getX(), e.getY());
                if (parentFrame != null) {
                    parentFrame.actualizarSelectoresNodos();
                }
                repaint();
            }
        } else if (currentEditMode == EditMode.ADD_EDGE_START) {
            tempEdgeStart = encontrarNodoEn(e.getX(), e.getY());
            if (tempEdgeStart != null) {
                currentEditMode = EditMode.ADD_EDGE_END;
                JOptionPane.showMessageDialog(MapPanel.this, 
                        "Nodo inicial: " + tempEdgeStart.getId() + ". Haz clic en el nodo destino.", 
                        "Conectar Nodos", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (currentEditMode == EditMode.ADD_EDGE_END) {
            MapPoint tempEdgeEnd = encontrarNodoEn(e.getX(), e.getY());
            if (tempEdgeEnd != null && !tempEdgeEnd.equals(tempEdgeStart)) {
                controller.addConnection(tempEdgeStart, tempEdgeEnd, true);
                currentEditMode = EditMode.ADD_EDGE_START;
                tempEdgeStart = null;
                repaint();
                JOptionPane.showMessageDialog(MapPanel.this, "¡Calle conectada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else if (tempEdgeEnd == null) {
                currentEditMode = EditMode.ADD_EDGE_START;
                tempEdgeStart = null;
            }
        } else if (currentEditMode == EditMode.DELETE_NODE) {
            MapPoint nodeToDelete = encontrarNodoEn(e.getX(), e.getY());
            if (nodeToDelete != null) {
                int confirm = JOptionPane.showConfirmDialog(MapPanel.this, 
                        "¿Deseas eliminar el nodo '" + nodeToDelete.getId() + "' y todas sus calles?", 
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.removePoint(nodeToDelete);
                    if (parentFrame != null) {
                        parentFrame.actualizarSelectoresNodos();
                    }
                    repaint();
                    JOptionPane.showMessageDialog(MapPanel.this, "Nodo eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    public void setEditMode(EditMode mode) {
        this.currentEditMode = mode;
        this.tempEdgeStart = null;
    }

    private MapPoint encontrarNodoEn(int x, int y) {
        if (controller == null || controller.getGraph() == null) return null;

        for (Node<MapPoint> node : controller.getGraph().getNodes()) {
            MapPoint p = node.getValue();
            if (Math.hypot(p.getX() - x, p.getY() - y) < 20) {
                return p;
            }
        }
        return null;
    }

    public void iniciarAnimacion(PathResult<MapPoint> result, VisualizationMode mode) {
        detenerAnimacion();
        this.visitedList = new ArrayList<>(result.getVisitados());
        this.pathList = new ArrayList<>(result.getPath());
        this.visitedIndex = 0;
        this.pathIndex = 0;

        timer = new Timer(500, e -> {
            if (visitedIndex < visitedList.size()) {
                visitedIndex++;
            } else if (pathIndex < pathList.size()) {
                pathIndex++;
            } else {
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        timer.start();
    }

    public void limpiarCamino() {
        detenerAnimacion();
        visitedList.clear();
        pathList.clear();
        visitedIndex = 0;
        pathIndex = 0;
        repaint();
    }

    private void detenerAnimacion() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        if (controller == null || controller.getGraph() == null) return;

        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(3));
        Map<Node<MapPoint>, Set<Node<MapPoint>>> graphMap = controller.getGraph().getGraph();
        for (Map.Entry<Node<MapPoint>, Set<Node<MapPoint>>> entry : graphMap.entrySet()) {
            MapPoint p1 = entry.getKey().getValue();
            for (Node<MapPoint> neighbor : entry.getValue()) {
                MapPoint p2 = neighbor.getValue();
                g2d.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
        }

        g2d.setColor(new Color(255, 140, 0, 200));
        for (int i = 0; i < visitedIndex; i++) {
            MapPoint p = visitedList.get(i);
            g2d.fillOval(p.getX() - 15, p.getY() - 15, 30, 30);
        }

        if (pathIndex > 0) {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(5));
            for (int i = 0; i < pathIndex - 1; i++) {
                MapPoint p1 = pathList.get(i);
                MapPoint p2 = pathList.get(i + 1);
                g2d.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
            g2d.setColor(Color.GREEN);
            // Nota: Color verde personalizado seguro abajo
            g2d.setColor(new Color(50, 205, 50));
            for (int i = 0; i < pathIndex; i++) {
                MapPoint p = pathList.get(i);
                g2d.fillOval(p.getX() - 12, p.getY() - 12, 24, 24);
            }
        }

        for (Node<MapPoint> node : controller.getGraph().getNodes()) {
            MapPoint p = node.getValue();
            g2d.setColor(Color.BLACK);
            g2d.fillOval(p.getX() - 6, p.getY() - 6, 12, 12);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            g2d.setColor(Color.WHITE);
            g2d.drawString(p.getId(), p.getX() + 11, p.getY() - 9);
            g2d.setColor(Color.BLACK);
            g2d.drawString(p.getId(), p.getX() + 10, p.getY() - 10);
        }
    }
}