package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

public class Horario extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tableHorario;

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
}