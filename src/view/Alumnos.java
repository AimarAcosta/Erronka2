package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

public class Alumnos extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<String> modeloLista;
	private List<Map<String, Object>> listaAlumnos;
	private JList<String> listAlumnos;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Alumnos frame = new Alumnos();
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
	public Alumnos() {
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
		btnSalir.setBounds(10, 11, 97, 30);
		contentPane.add(btnSalir);
		
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(168, 168, 168));
		panel.setBounds(68, 71, 305, 262);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNombre = new JLabel("NOMBRE");
		lblNombre.setVerticalAlignment(SwingConstants.TOP);
		lblNombre.setFont(new Font("Tahoma", Font.PLAIN, 21));
		lblNombre.setBounds(10, 146, 285, 43);
		panel.add(lblNombre);
		
		JLabel lblFoto = new JLabel("FOTO");
		lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
		lblFoto.setBounds(10, 11, 95, 101);
		panel.add(lblFoto);
		lblFoto.setIcon(new ImageIcon("C:\\Users\\in2dm3-d.ELORRIETA\\eclipse-workspace\\Erronka2\\src\\view\\unknown-user.jpg"));
		
		JComboBox cbxCiclo = new JComboBox();
		cbxCiclo.setBounds(571, 49, 120, 30);
		contentPane.add(cbxCiclo);
		
		JComboBox cbxCurso = new JComboBox();
		cbxCurso.setBounds(393, 49, 120, 30);
		contentPane.add(cbxCurso);
		
		cbxCiclo.addItem("Todos");
		cbxCiclo.addItem("DAM");
		cbxCiclo.addItem("DAW");
		cbxCiclo.addItem("ASIR");

		cbxCurso.addItem("Todos");
		cbxCurso.addItem("1");
		cbxCurso.addItem("2");
		
		ActionListener filtroAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtrarAlumnos(
					cbxCiclo.getSelectedItem().toString(),
					cbxCurso.getSelectedItem().toString()
				);
			}
		};
		
		cbxCiclo.addActionListener(filtroAction);
		cbxCurso.addActionListener(filtroAction);
		
		modeloLista = new DefaultListModel<>();
		listAlumnos = new JList<String>(modeloLista);
		listAlumnos.setBounds(413, 90, 264, 227);
		contentPane.add(listAlumnos);
		
		listAlumnos.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				String seleccionado = listAlumnos.getSelectedValue();
				if (seleccionado != null) {
					for (Map<String, Object> alumno : listaAlumnos) {
						String nombreCompleto = String.valueOf(alumno.get("nombre"));
						if (nombreCompleto.equals(seleccionado)) {
							lblNombre.setText(nombreCompleto.toUpperCase());
							break;
						}
					}
				}
			}
		});

		cargarEstudiantes();
	}
	
	private void cargarEstudiantes() {
	    try {
	        System.out.println("=== 1. INICIANDO PETICIÓN AL SERVIDOR ===");
	        
	        // Enviamos la petición
	        String respuestaJson = conectores.ClienteSocket.enviarPeticion("GET_ESTUDIANTES", Map.of());
	        
	        // VEMOS LO QUE LLEGA (Esto es lo más importante)
	        System.out.println("=== 2. JSON RECIBIDO: " + respuestaJson);

	        if (respuestaJson == null) {
	            System.err.println("ERROR: La respuesta es NULL (Fallo de conexión)");
	            return;
	        }

	        Gson gson = new Gson();
	        Map<String, Object> respuesta = gson.fromJson(respuestaJson, Map.class);

	        if ("GET_ESTUDIANTES_OK".equals(respuesta.get("tipo"))) {
	            listaAlumnos = (List<Map<String, Object>>) respuesta.get("contenido");
	            
	            int cantidad = (listaAlumnos != null) ? listaAlumnos.size() : 0;
	            System.out.println("=== 3. CANTIDAD DE ALUMNOS ENCONTRADOS: " + cantidad);

	            modeloLista.clear();
	            
	            if (listaAlumnos != null) {
	                for (Map<String, Object> alumno : listaAlumnos) {
	                    // Imprimimos cada alumno para ver qué claves trae (nombre, apellidos, ciclo...)
	                    System.out.println("   -> Procesando alumno: " + alumno);
	                    
	                    // Construimos el nombre con seguridad (por si algo es null)
	                    String nombre = String.valueOf(alumno.get("nombre"));
	                    // Si Aimar ya manda el nombre completo, esto servirá. 
	                    // Si manda "apellidos" aparte, lo añadimos:
	                    if (alumno.containsKey("apellidos")) {
	                        nombre += " " + alumno.get("apellidos");
	                    }
	                    
	                    modeloLista.addElement(nombre);
	                }
	            }
	        } else {
	            System.err.println("ERROR SERVIDOR: Tipo de respuesta no esperado -> " + respuesta.get("tipo"));
	        }
	    } catch (Exception e) {
	        System.err.println("=== EXCEPCIÓN CRÍTICA ===");
	        e.printStackTrace();
	    }
	}
	
	//private void cargarEstudiantes() {
		//try {
          //  String respuestaJson = conectores.ClienteSocket.enviarPeticion("GET_ESTUDIANTES", Map.of());   
            //Gson gson = new Gson();
            //Map<String, Object> respuesta = gson.fromJson(respuestaJson, Map.class);

//            if ("GET_ESTUDIANTES_OK".equals(respuesta.get("tipo"))) {
  //              listaAlumnos = (List<Map<String, Object>>) respuesta.get("contenido");               
    //            modeloLista.clear();
                
      //          for (Map<String, Object> alumno : listaAlumnos) {
        //            String nombreCompleto = String.valueOf(alumno.get("nombre"));
          //          modeloLista.addElement(nombreCompleto);
            //    }
            //}
        //} catch (Exception e) {
          //  e.printStackTrace();
        //}
	//}
	
	private void filtrarAlumnos(String cicloSel, String cursoSel) {
		if (listaAlumnos == null) return;
	    modeloLista.clear();

	    for (Map<String, Object> alumno : listaAlumnos) {
	        String cicloAlu = String.valueOf(alumno.get("ciclo")); 
	        String cursoAlu = String.valueOf(alumno.get("curso"));

	        // Lógica: Si es "Todos" o coincide exactamente
	        boolean coincideCiclo = cicloSel.equals("Todos") || cicloSel.equalsIgnoreCase(cicloAlu);
	        boolean coincideCurso = cursoSel.equals("Todos") || cursoSel.equalsIgnoreCase(cursoAlu);

	        if (coincideCiclo && coincideCurso) {
	            modeloLista.addElement(String.valueOf(alumno.get("nombre")));
	        }
	    }
	}
}
