package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import modelo.Horario;
import modelo.Profesor;
import modelo.Users;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class OtrosHorarios extends JFrame {

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
                    ViewHorario frame = new ViewHorario();
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
    public OtrosHorarios() {
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
        contentPane.setLayout(null);
        btnSalir.setBounds(10, 15, 102, 31);
        contentPane.add(btnSalir);
        
        List<Profesor> profesores = conectores.ClienteSocket.conseguirProfesores();
        
        String[] columns = {"Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes" };

        // Six time slots (modify as needed)
        Object[][] data = new Object[][] {
            {"8:00-9:00", "", "", "", "", "" },
            {"9:00-10:00", "", "", "", "", "" },
            {"10:00-11:00", "", "", "", "", "" },
            {"11:30-12:30", "", "", "", "", "" },
            {"12:00-13:30", "", "", "", "", "" },
            {"13:30-14:30", "", "", "", "", "" }
        };
        
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
        
        JComboBox comboBox = new JComboBox();
        comboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String profesorSelect = comboBox.getSelectedItem().toString();
        		int profesorId = 0;
        		for(Profesor p : profesores) {
        			if(p.getNombreCompleto().equals(profesorSelect)) {
        				profesorId = p.getId();
        				break;
        			}
        		}
        		
        		for (int i = 0; i < model.getRowCount(); i++) {
        	        for (int j = 1; j < model.getColumnCount(); j++) {
        	            model.setValueAt("", i, j);
        	        }
        	    }
        		
        		List<Horario> horarios = conectores.ClienteSocket.conseguirHorarios(profesorId);
  
        		model.fireTableDataChanged();
        		for(Horario h : horarios) {
                	if(h.getDia() == null) {
                		System.out.println("El valor día es null!");
                	}else {
                		int fila = h.getHora() - 1;
                        int columna = -1;

                        switch (h.getDia()) {
                            case "LUNES": columna = 1; break;
                            case "MARTES": columna = 2; break;
                            case "MIERCOLES": columna = 3; break;
                            case "JUEVES": columna = 4; break;
                            case "VIERNES": columna = 5; break;
                        }
                        if (columna != -1 && fila >= 0 && fila < model.getRowCount()) {
                            model.setValueAt(h.getModulo(), fila, columna);
                        }
                	}
                }
        	}
        });
        comboBox.setBounds(256, 19, 208, 22);
        contentPane.add(comboBox);
        for(Profesor u : profesores){
        	comboBox.addItem(u.getNombreCompleto());
        }
        
    }
}