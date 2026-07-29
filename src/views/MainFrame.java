package views;

import controllers.MapController;
import models.MapPoint;
import models.VisualizationMode;
import structures.graphs.PathResult;
import structures.node.Node;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class MainFrame extends JFrame {
    private MapController controlador;
    private MapPanel panelMapa;

    private JComboBox<MapPoint> cbNodoInicio;
    private JComboBox<MapPoint> cbNodoDestino;
    private JComboBox<String> cbAlgoritmo;
    private JComboBox<VisualizationMode> cbModo;

    public MainFrame(MapController controlador) {
        this.controlador = controlador;
        this.setTitle("Sistema de Búsqueda de Rutas - Universidad");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        inicializarBarraMenu();

        panelMapa = new MapPanel(controlador, this);
        this.add(panelMapa, BorderLayout.CENTER);

        inicializarControles();
        this.setLocationRelativeTo(null);
    }

    private void inicializarBarraMenu() {
        JMenuBar barraMenu = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemGuardar = new JMenuItem("Guardar Configuración...");
        JMenuItem itemCargar = new JMenuItem("Cargar Configuración...");

        itemGuardar.addActionListener(e -> {
            try {
                controlador.saveConfiguration("config_mapa.csv");
                JOptionPane.showMessageDialog(this, "Mapa guardado con éxito.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
            }
        });

        itemCargar.addActionListener(e -> {
            try {
                controlador.loadConfiguration("config_mapa.csv");
                actualizarSelectoresNodos();
                panelMapa.repaint();
                JOptionPane.showMessageDialog(this, "Mapa cargado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
            }
        });

        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemCargar);

        JMenu menuEdicion = new JMenu("Edición Interactiva");
        JMenuItem itemAgregarNodo = new JMenuItem("Modo: Agregar Nodos (Clic en mapa)");
        JMenuItem itemConectarNodos = new JMenuItem("Modo: Conectar Nodos (Clic en 2 nodos)");
        JMenuItem itemEliminarNodo = new JMenuItem("Modo: Eliminar Nodo (Clic en nodo)");
        JMenuItem itemSalirEdicion = new JMenuItem("Salir de Modo Edición");

        itemAgregarNodo.addActionListener(e -> {
            panelMapa.setEditMode(MapPanel.EditMode.ADD_NODE);
            JOptionPane.showMessageDialog(this, "Haz clic en el mapa para crear un nodo.");
        });

        itemConectarNodos.addActionListener(e -> {
            panelMapa.setEditMode(MapPanel.EditMode.ADD_EDGE_START);
            JOptionPane.showMessageDialog(this, "Haz clic en el nodo de inicio y luego en el de destino.");
        });

        itemEliminarNodo.addActionListener(e -> {
            panelMapa.setEditMode(MapPanel.EditMode.DELETE_NODE);
            JOptionPane.showMessageDialog(this, "Haz clic sobre cualquier nodo para eliminarlo junto con sus conexiones.");
        });

        itemSalirEdicion.addActionListener(e -> {
            panelMapa.setEditMode(MapPanel.EditMode.NONE);
        });

        menuEdicion.add(itemAgregarNodo);
        menuEdicion.add(itemConectarNodos);
        menuEdicion.add(itemEliminarNodo);
        menuEdicion.addSeparator();
        menuEdicion.add(itemSalirEdicion);

        barraMenu.add(menuArchivo);
        barraMenu.add(menuEdicion);
        this.setJMenuBar(barraMenu);
    }

    private void inicializarControles() {
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelControl.setBorder(BorderFactory.createEtchedBorder());

        cbNodoInicio = new JComboBox<>();
        cbNodoDestino = new JComboBox<>();
        actualizarSelectoresNodos();

        cbAlgoritmo = new JComboBox<>(new String[] { "BFS", "DFS", "DIJKSTRA", "ASTAR" });
        cbModo = new JComboBox<>(VisualizationMode.values());

        JButton btnIniciar = new JButton("Iniciar Recorrido");
        JButton btnLimpiar = new JButton("Limpiar Mapa");

        btnIniciar.addActionListener(e -> ejecutarBusqueda());
        btnLimpiar.addActionListener(e -> panelMapa.limpiarCamino());

        panelControl.add(new JLabel("Inicio:"));
        panelControl.add(cbNodoInicio);
        panelControl.add(new JLabel("Destino:"));
        panelControl.add(cbNodoDestino);
        panelControl.add(new JLabel("Algoritmo:"));
        panelControl.add(cbAlgoritmo);
        panelControl.add(new JLabel("Visualización:"));
        panelControl.add(cbModo);
        panelControl.add(btnIniciar);
        panelControl.add(btnLimpiar);

        this.add(panelControl, BorderLayout.SOUTH);
    }

    public void actualizarSelectoresNodos() {
        Vector<MapPoint> puntos = new Vector<>();
        for (Node<MapPoint> nodo : controlador.getGraph().getNodes()) {
            puntos.add(nodo.getValue());
        }
        cbNodoInicio.setModel(new DefaultComboBoxModel<>(puntos));
        cbNodoDestino.setModel(new DefaultComboBoxModel<>(puntos));
    }

    public void setAlgorithmSelection(String algoritmo) {
        cbAlgoritmo.setSelectedItem(algoritmo);
    }

    private void ejecutarBusqueda() {
        MapPoint inicio = (MapPoint) cbNodoInicio.getSelectedItem();
        MapPoint destino = (MapPoint) cbNodoDestino.getSelectedItem();
        String algoritmo = (String) cbAlgoritmo.getSelectedItem();
        VisualizationMode modo = (VisualizationMode) cbModo.getSelectedItem();

        if (inicio == null || destino == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un nodo de inicio y destino.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        controlador.setVisualizationMode(modo);

        long inicioTiempo = System.nanoTime();
        PathResult<MapPoint> resultado = controlador.executeSearch(algoritmo, inicio, destino);
        long finTiempo = System.nanoTime();
        
        double duracionSegundos = (finTiempo - inicioTiempo) / 1_000_000_000.0;
        int cantidadAristas = 0;
        if (resultado.getPath() != null && !resultado.getPath().isEmpty()) {
            cantidadAristas = resultado.getPath().size() - 1; 
        }

        int nodosVisitados = 0;
        if (resultado.getVisitados() != null) {
            nodosVisitados = resultado.getVisitados().size();
        }

        if (resultado.getPath().isEmpty() && !inicio.equals(destino)) {
            JOptionPane.showMessageDialog(this, "No se encontró una ruta entre los nodos seleccionados.", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            
            JOptionPane.showMessageDialog(this, 
                    "Algoritmo: " + algoritmo + 
                    "\nTiempo: " + duracionSegundos + " s" +
                    "\nNodos visitados: " + nodosVisitados + 
                    "\nCantidad de aristas: " + cantidadAristas, 
                    "Métricas de Rendimiento", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
        panelMapa.iniciarAnimacion(resultado, modo);
    }
}
