package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;

import modelo.Users;
import modelo.Horarios;

public class Horario extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tableHorario;
	private Users usuario = controlador.Servicios.getLoggedUser();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Horario frame = new Horario();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Horario() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 736, 424);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                view.Menu frame = new view.Menu();
                frame.setVisible(true);
                dispose();
            }
        });
        
        List<Horarios> horarios = new ArrayList<>();
        cargarHorarioDesdeSocket();
        contentPane.setLayout(null);
        btnSalir.setBounds(10, 15, 102, 31);
        contentPane.add(btnSalir);

        // Column names: first column is the hour label, then Monday..Friday
        String[] columns = { "Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes" };

        // Six time slots (modify as needed)
        Object[][] data = new Object[][] {
            { "08:00 - 09:00", "", "", "", "", "" },
            { "09:00 - 10:00", "", "", "", "", "" },
            { "10:00 - 11:00", "", "", "", "", "" },
            { "11:00 - 12:00", "", "", "", "", "" },
            { "12:00 - 13:00", "", "", "", "", "" },
            { "13:00 - 14:00", "", "", "", "", "" }
        };

        // Create a non-editable table model
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHorario = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableHorario);
        scrollPane.setBounds(52, 57, 616, 288);
        contentPane.add(scrollPane);
    }
    
    private void cargarHorarioDesdeSocket() {
        try {
            // 1. Pedimos los horarios usando el método genérico que pusimos en ClienteSocket
            String respuestaJson = conectores.ClienteSocket.enviarPeticion("GET_HORARIOS", Map.of());
            
            Gson gson = new Gson();
            Map<String, Object> respuesta = gson.fromJson(respuestaJson, Map.class);

            if ("GET_HORARIOS_OK".equals(respuesta.get("tipo"))) {
                // El servidor nos manda una lista de mapas (id, dia, hora, nombre_modulo...)
                List<Map<String, String>> datos = (List<Map<String, String>>) respuesta.get("contenido");

                // 2. Rellenamos la tabla (JTable)
                for (Map<String, String> filaHorario : datos) {
                    String dia = filaHorario.get("dia");
                    // Aimar manda la hora como 1, 2, 3... restamos 1 para que coincida con la fila del array (0, 1, 2...)
                    int filaIndex = Integer.parseInt(String.valueOf(filaHorario.get("hora"))) - 1;
                    int colIndex = obtenerColumnaDia(dia);

                    if (colIndex != -1 && filaIndex >= 0 && filaIndex < tableHorario.getRowCount()) {
                        tableHorario.setValueAt(filaHorario.get("nombre_modulo"), filaIndex, colIndex);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando horario: " + e.getMessage());
        }
    }

    // Método auxiliar para saber en qué columna va cada día
    private int obtenerColumnaDia(String dia) {
        switch (dia.toLowerCase()) {
            case "lunes": return 1;
            case "martes": return 2;
            case "miércoles": case "miercoles": return 3;
            case "jueves": return 4;
            case "viernes": return 5;
            default: return -1;
        }
    }
}