package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import modelo.Horario;
import modelo.Reuniones;
import javax.swing.JLabel;
import java.awt.Font;

public class ViewHorario extends JFrame {

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
	public ViewHorario() {
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

		// cargarHorarioDesdeSocket();
		contentPane.setLayout(null);
		btnSalir.setBounds(10, 15, 102, 31);
		contentPane.add(btnSalir);
		List<Horario> horarios = conectores.ClienteSocket.conseguirHorarios(usuario.getId());
		List<Reuniones> reuniones = conectores.ClienteSocket.conseguirReuniones(usuario.getId());

		String[] columns = { "Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes" };
		
		Object[][] data = new Object[][] { 
				{ "8:00-9:00", "", "", "", "", "" }, 
				{ "9:00-10:00", "", "", "", "", "" },
				{ "10:00-11:00", "", "", "", "", "" }, 
				{ "11:30-12:30", "", "", "", "", "" },
				{ "12:00-13:30", "", "", "", "", "" }, 
				{ "13:30-14:30", "", "", "", "", "" } };

		for (Horario c : horarios) {
			if (c.getDia() == null) {
				System.out.println("El valor día es null!");
			} else {
				int hora = c.getHora() - 1;
				switch (c.getDia()) {
				case "LUNES":
					data[hora][1] = c.getModulo();
				case "MARTES":
					data[hora][2] = c.getModulo();
				case "MIERCOLES":
					data[hora][3] = c.getModulo();
				case "JUEVES":
					data[hora][4] = c.getModulo();
				case "VIERNES":
					data[hora][5] = c.getModulo();
				}
			}
		}

		for (Reuniones r : reuniones) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(r.getFecha());

			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			int columna = -1;

			switch (dayOfWeek) {
			case Calendar.MONDAY:
				columna = 1;
				break;
			case Calendar.TUESDAY:
				columna = 2;
				break;
			case Calendar.WEDNESDAY:
				columna = 3;
				break;
			case Calendar.THURSDAY:
				columna = 4;
				break;
			case Calendar.FRIDAY:
				columna = 5;
				break;
			}

			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int fila = -1;

			if (hour >= 8 && hour < 9)
				fila = 0;
			else if (hour >= 9 && hour < 10)
				fila = 1;
			else if (hour >= 10 && hour < 11)
				fila = 2;
			else if (hour >= 11 && hour < 12)
				fila = 3;
			else if (hour >= 12 && hour < 13)
				fila = 4;
			else if (hour >= 13 && hour < 14)
				fila = 5;

			if (fila != -1 && columna != -1) {
				data[fila][columna] = "REUNIÓN";
				// o r.getTitulo(), o r.getAsunto()
			}

			// Create a non-editable table model
			DefaultTableModel model = new DefaultTableModel(data, columns) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

			tableHorario = new JTable(model);
			if (horarios.size() > 0) {
				tableHorario.setShowGrid(true);
			} else {
				tableHorario.setShowGrid(false);
			}
			tableHorario.setFillsViewportHeight(true);
			JScrollPane scrollPane = new JScrollPane(tableHorario);
			scrollPane.setBounds(52, 57, 616, 288);
			contentPane.add(scrollPane);

			JLabel lblHorarioDe = new JLabel("New label");
			lblHorarioDe.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblHorarioDe.setBounds(122, 23, 244, 23);
			contentPane.add(lblHorarioDe);
			lblHorarioDe.setText("Horario de " + usuario.getNombre() + " " + usuario.getApellidos());

		}
	}
}