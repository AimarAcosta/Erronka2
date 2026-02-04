package view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import modelo.Centro;
import modelo.Users;
import javax.swing.JTextField;
import com.toedter.calendar.JDateChooser;

public class Reuniones extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Users usuario = controlador.Servicios.getLoggedUser();

	private JComboBox<String> cbxTerritorio;
	private JComboBox<String> cbxMunicipio;
	private JComboBox<String> cbxCentro;
	private JComboBox<modelo.Profesor> cbxIDProfesor;
	private JComboBox<modelo.Users> cbxIDEstudiante;
	
	private JDateChooser dateChooser;

	private List<Centro> listaCentros;
	private JLabel lblMunicipio;
	private JLabel lblCentro;
	private JTextField txtTitulo;
	private JTextField txtAsunto;
	private JTextField txtHora;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Reuniones frame = new Reuniones();
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
	public Reuniones() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 736, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.Menu frame = new view.Menu();
				frame.setVisible(true);
				dispose();
			}
		});
		contentPane.setLayout(null);
		btnSalir.setBounds(10, 11, 83, 24);
		contentPane.add(btnSalir);

//List<modelo.Reuniones> reuniones = conectores.ClienteSocket.conseguirReuniones(usuario.getId());

// Territorio
		JLabel lblTerritorio = new JLabel("Territorio");
		lblTerritorio.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblTerritorio.setBounds(55, 268, 64, 24);
		contentPane.add(lblTerritorio);

		cbxTerritorio = new JComboBox<>();
		cbxTerritorio.setBounds(117, 268, 135, 24);
		contentPane.add(cbxTerritorio);

		// Municipio
		lblMunicipio = new JLabel("Municipio");
		lblMunicipio.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblMunicipio.setBounds(287, 268, 64, 24);
		contentPane.add(lblMunicipio);

		cbxMunicipio = new JComboBox<>();
		cbxMunicipio.setBounds(349, 268, 130, 24);
		cbxMunicipio.setEnabled(false);
		contentPane.add(cbxMunicipio);

		// Centro
		lblCentro = new JLabel("Centro");
		lblCentro.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCentro.setBounds(55, 329, 64, 24);
		contentPane.add(lblCentro);

		cbxCentro = new JComboBox<>();
		cbxCentro.setBounds(127, 326, 300, 30);
		cbxCentro.setEnabled(false);
		contentPane.add(cbxCentro);

		// BOTÓN AGENDAR
		JButton btnAgendar = new JButton("Agendar Reunión");
		btnAgendar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// --- 1. Validaciones básicas ---
				String titulo = txtTitulo.getText().trim();
				if (titulo.isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "El título es obligatorio");
					return;
				}

				String asunto = txtAsunto.getText().trim();
				if (asunto.isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "El asunto es obligatorio");
					return;
				}

				java.util.Date fecha = dateChooser.getDate();
				if (fecha == null) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Selecciona una fecha");
					return;
				}

				String hora = txtHora.getText().trim();
				if (!hora.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Hora inválida (HH:MM)");
					return;
				}

				// --- 2. Validar combos ---
				if (cbxTerritorio.getSelectedIndex() <= 0) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Selecciona un territorio");
					return;
				}
				if (cbxMunicipio.getSelectedIndex() <= 0) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Selecciona un municipio");
					return;
				}
				if (cbxCentro.getSelectedIndex() < 0) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Selecciona un centro");
					return;
				}
				if (cbxIDProfesor.getSelectedItem() == null) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Selecciona un profesor");
					return;
				}
				if (cbxIDEstudiante.getSelectedItem() == null) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Selecciona un estudiante");
					return;
				}

				// --- 3. Obtener datos de los combos ---
				String territorio = (String) cbxTerritorio.getSelectedItem();
				String municipio = (String) cbxMunicipio.getSelectedItem();
				String centro = (String) cbxCentro.getSelectedItem();
				modelo.Profesor profesor = (modelo.Profesor) cbxIDProfesor.getSelectedItem();
				modelo.Users estudiante = (modelo.Users) cbxIDEstudiante.getSelectedItem();

				// --- 4. Formatear fecha ---
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String fechaStr = sdf.format(fecha);

				// --- 5. Crear objeto reunión ---
				modelo.Reunion reunion = new modelo.Reunion();
				reunion.setTitulo(titulo);
				reunion.setAsunto(asunto);
				reunion.setFecha(fechaStr);
				reunion.setHora(hora);
				reunion.setTerritorio(territorio);
				reunion.setMunicipio(municipio);
				reunion.setCentro(centro);
				reunion.setProfesor(profesor);
				reunion.setEstudiante(estudiante);

				// --- 6. Enviar al servidor ---
				boolean exito = conectores.ClienteSocket.agendarReunion(reunion);
				if (exito) {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Reunión agendada correctamente");
					// Limpiar formulario opcional
					txtTitulo.setText("");
					txtAsunto.setText("");
					txtHora.setText("");
					dateChooser.setDate(null);
					cbxTerritorio.setSelectedIndex(0);
					cbxMunicipio.removeAllItems();
					cbxMunicipio.setEnabled(false);
					cbxCentro.removeAllItems();
					cbxCentro.setEnabled(false);
				} else {
					javax.swing.JOptionPane.showMessageDialog(Reuniones.this, "Error al agendar la reunión");
				}
			}
		});
		btnAgendar.setBackground(new Color(135, 206, 235));
		btnAgendar.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnAgendar.setBounds(531, 307, 164, 39);
		contentPane.add(btnAgendar);

		JLabel lblTitulo = new JLabel("Titulo:");
		lblTitulo.setBounds(55, 46, 58, 26);
		contentPane.add(lblTitulo);

		txtTitulo = new JTextField();
		txtTitulo.setBounds(105, 46, 246, 26);
		contentPane.add(txtTitulo);
		txtTitulo.setColumns(10);

		JLabel lblAsunto = new JLabel("Asunto:");
		lblAsunto.setBounds(55, 83, 46, 14);
		contentPane.add(lblAsunto);

		txtAsunto = new JTextField();
		txtAsunto.setColumns(10);
		txtAsunto.setBounds(105, 83, 393, 46);
		contentPane.add(txtAsunto);

		JLabel lblFecha = new JLabel("Fecha (YYYY-MM-DD):");
		lblFecha.setBounds(55, 148, 122, 14);
		contentPane.add(lblFecha);

		JLabel lblHora = new JLabel("Hora (HH:MM)");
		lblHora.setBounds(325, 146, 83, 19);
		contentPane.add(lblHora);

		txtHora = new JTextField();
		txtHora.setBounds(423, 140, 75, 30);
		contentPane.add(txtHora);
		txtHora.setColumns(10);

		JLabel lblProfesor = new JLabel("Profesor (Id):");
		lblProfesor.setBounds(55, 197, 75, 14);
		contentPane.add(lblProfesor);

		JLabel lblEstudiante = new JLabel("Estudiante (Id):");
		lblEstudiante.setBounds(55, 231, 93, 14);
		contentPane.add(lblEstudiante);

		cbxIDProfesor = new JComboBox<>();
		cbxIDProfesor.setBounds(181, 193, 192, 22);
		contentPane.add(cbxIDProfesor);

		cbxIDEstudiante = new JComboBox<>();
		cbxIDEstudiante.setBounds(181, 227, 192, 22);
		contentPane.add(cbxIDEstudiante);

		dateChooser = new JDateChooser();
		dateChooser.setBounds(184, 140, 103, 30);
		contentPane.add(dateChooser);

		// --- LÓGICA DE CARGA Y EVENTOS ---
		cargarDatosJson();
		configurarEventos();
		cargarUsuarios();
	}

	private void cargarDatosJson() {
		try (Reader reader = new java.io.InputStreamReader(new java.io.FileInputStream("EuskadiLatLon.json"),
				"UTF-8")) {
			Gson gson = new Gson();

			JsonElement elementoRaiz = JsonParser.parseReader(reader);

			if (elementoRaiz.isJsonArray()) {
				listaCentros = gson.fromJson(elementoRaiz, new TypeToken<List<Centro>>() {
				}.getType());
			} else if (elementoRaiz.isJsonObject()) {
				JsonObject objeto = elementoRaiz.getAsJsonObject();
				JsonArray arrayEncontrado = null;

				for (String clave : objeto.keySet()) {
					JsonElement valor = objeto.get(clave);
					if (valor.isJsonArray()) {
						arrayEncontrado = valor.getAsJsonArray();
						break;
					}
				}
				if (arrayEncontrado != null) {
					listaCentros = gson.fromJson(arrayEncontrado, new TypeToken<List<Centro>>() {
					}.getType());
				} else {
					System.out.println("Error tipos distintos");
					listaCentros = new ArrayList<>();
				}
			}

			if (listaCentros != null) {
				cbxTerritorio.addItem("Selecciona");
				List<String> territorios = listaCentros.stream().map(Centro::getTerritorio).filter(t -> t != null) // Evitar
																													// nulos
						.distinct().sorted().collect(Collectors.toList());

				for (String t : territorios) {
					cbxTerritorio.addItem(t);
				}
				System.out.println("Centros cargados " + listaCentros.size());
			}

		} catch (Exception e) {
			System.err.println("Error cargando JSON: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void configurarEventos() {
		// 1. EVENTO TERRITORIO -> CARGA MUNICIPIOS
		cbxTerritorio.addActionListener(e -> {
			if (cbxTerritorio.getSelectedItem() == null || cbxTerritorio.getSelectedIndex() <= 0) {
				cbxMunicipio.setEnabled(false);
				cbxCentro.setEnabled(false);
				return;
			}

			String terSel = (String) cbxTerritorio.getSelectedItem();
			cbxMunicipio.removeAllItems();
			cbxMunicipio.addItem("Selecciona...");

			List<String> municipios = listaCentros.stream()
					.filter(c -> c.getTerritorio() != null && c.getTerritorio().equals(terSel))
					.map(Centro::getMunicipio).filter(m -> m != null && !m.isEmpty()).distinct().sorted()
					.collect(Collectors.toList());

			for (String m : municipios) {
				cbxMunicipio.addItem(m);
			}

			cbxMunicipio.setEnabled(true);
			cbxCentro.removeAllItems();
			cbxCentro.setEnabled(false);
		});

		// 2. EVENTO MUNICIPIO -> CARGA CENTROS
		cbxMunicipio.addActionListener(e -> {
			String munSel = (String) cbxMunicipio.getSelectedItem();
			if (munSel == null || munSel.equals("Selecciona..."))
				return;

			cbxCentro.removeAllItems();

			// Convertimos la selección a algo "limpio" (sin espacios y en mayúsculas)
			String munBusqueda = munSel.trim().toUpperCase();

			List<String> centros = listaCentros.stream().filter(c -> {
				if (c.getMunicipio() == null)
					return false;
				// Comparamos limpiando ambos lados
				return c.getMunicipio().trim().toUpperCase().equals(munBusqueda);
			}).map(Centro::getNombre).distinct().sorted().collect(Collectors.toList());

			if (centros.isEmpty()) {
				// DEBUG: Esto te dirá en consola qué tiene el primer centro de la lista
				// para que veas por qué no coincide.
				System.out.println("DEBUG: Buscando '" + munBusqueda + "'");
				if (!listaCentros.isEmpty()) {
					System.out.println("DEBUG: Ejemplo en lista: '" + listaCentros.get(0).getMunicipio() + "'");
				}
			}

			for (String c : centros) {
				cbxCentro.addItem(c);
			}
			cbxCentro.setEnabled(cbxCentro.getItemCount() > 0);
		});

	}

	private void cargarUsuarios() {
		// 1. Cargar Profesores
		List<modelo.Profesor> profes = conectores.ClienteSocket.conseguirProfesores();
		if (profes != null) {
			for (modelo.Profesor p : profes) {
				cbxIDProfesor.addItem(p);
			}
		}

		// 2. Cargar Estudiantes
		List<modelo.Users> alumnos = conectores.ClienteSocket.conseguirEstudiantes();
		if (alumnos != null) {
			for (modelo.Users a : alumnos) {
				cbxIDEstudiante.addItem(a);
			}
		}
	}
}