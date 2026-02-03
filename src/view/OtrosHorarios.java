package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
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
import modelo.Reuniones;
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
        		List<Reuniones> reuniones = conectores.ClienteSocket.conseguirReuniones(profesorId);
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
        		for (Reuniones r : reuniones) {

        		    Calendar cal = Calendar.getInstance();
        		    cal.setTime(r.getFecha());

        		    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        		    int columna = -1;

        		    switch (dayOfWeek) {
        		        case Calendar.MONDAY:    columna = 1; break;
        		        case Calendar.TUESDAY:   columna = 2; break;
        		        case Calendar.WEDNESDAY: columna = 3; break;
        		        case Calendar.THURSDAY:  columna = 4; break;
        		        case Calendar.FRIDAY:    columna = 5; break;
        		    }

        		    int hour = cal.get(Calendar.HOUR_OF_DAY);
        		    int fila = -1;

        		    if (hour >= 8 && hour < 9) fila = 0;
        		    else if (hour >= 9 && hour < 10) fila = 1;
        		    else if (hour >= 10 && hour < 11) fila = 2;
        		    else if (hour >= 11 && hour < 12) fila = 3;
        		    else if (hour >= 12 && hour < 13) fila = 4;
        		    else if (hour >= 13 && hour < 14) fila = 5;

        		    if (fila != -1 && columna != -1) {
        		        model.setValueAt("REUNIÓN", fila, columna);
        		        // o r.getTitulo(), o r.getAsunto()
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