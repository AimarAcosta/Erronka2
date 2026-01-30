package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorDB {

	private static final String URL = "jdbc:mysql://localhost:3306/eduelorrieta";
	private static final String USER = "root";
	private static final String PASS = "";

	private Connection conectar() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}

	// login
	public Map<String, Object> login(String user, String pass) {
		String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND tipo_id != 4";

		try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, user);
			ps.setString(2, pass);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Map<String, Object> usuario = new HashMap<>();
				usuario.put("id", rs.getInt("id"));
				usuario.put("email", rs.getString("email"));
				usuario.put("username", rs.getString("username"));
				usuario.put("password", rs.getString("password"));
				usuario.put("nombre", rs.getString("nombre"));
				usuario.put("apellidos", rs.getString("apellidos"));
				usuario.put("dni", rs.getString("dni"));
				usuario.put("direccion", rs.getString("direccion"));
				usuario.put("telefono1", rs.getString("telefono1"));
				usuario.put("telefono2", rs.getString("telefono2"));
				usuario.put("tipo_id", rs.getInt("tipo_id"));
				usuario.put("argazkia_url", rs.getString("argazkia_url"));
				
				return usuario;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Sacar horarios
	
	public List<Map<String, String>> obtenerHorarios(int idProfesor) {
		List<Map<String, String>> listaHorarios = new ArrayList<>();

		String sql = "SELECT h.dia, h.hora, m.id AS moduloID, m.nombre AS modulo, h.aula " + "FROM horarios h "
				+ "JOIN modulos m ON h.modulo_id = m.id " + "WHERE h.profe_id = ? " + 
				
				"ORDER BY FIELD(h.dia, 'LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES'), h.hora ASC";
		
		
		

		try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, idProfesor);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Map<String, String> fila = new HashMap<>();
				fila.put("dia", rs.getString("dia"));
				fila.put("hora", rs.getString("hora"));
				fila.put("moduloID", rs.getString("moduloID"));
				fila.put("modulo", rs.getString("modulo"));
				fila.put("aula", rs.getString("aula"));
				listaHorarios.add(fila);	
			}
			return listaHorarios;
		} catch (Exception e) {
			System.err.println("Error de SQL en obtenerHorarios");
			e.printStackTrace();
		}
		return listaHorarios;
	}

	// sacar estudiantes
		public List<Map<String, String>> obtenerEstudiantes() {
			List<Map<String, String>> lista = new ArrayList<>();

			String sql = "SELECT u.id, u.nombre, u.apellidos, u.username, u.email, c.nombre AS nombre_ciclo, m.curso"
					+ " FROM users u"
					+ " LEFT JOIN matriculaciones m ON u.id = m.alum_id"
					+ " LEFT JOIN ciclos c ON m.ciclo_id = c.id"
					+ " WHERE u.tipo_id = 4"; 
			try (Connection conn = conectar();
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					Map<String, String> estudiante = new HashMap<>();
					estudiante.put("id", String.valueOf(rs.getInt("u.id"))); 
					estudiante.put("nombre", rs.getString("nombre") + " " + rs.getString("apellidos"));
					estudiante.put("username", rs.getString("username"));
					estudiante.put("email", rs.getString("email"));
					
					String ciclo = rs.getString("nombre_ciclo");
					estudiante.put("ciclo", ciclo != null ? ciclo : "Sin asignar");
					
					int curso = rs.getInt("curso");
					estudiante.put("curso", curso != 0 ? String.valueOf(curso) : "");
					
					lista.add(estudiante);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return lista;
		}


	// Saca Profesores

	public List<Map<String, Object>> obtenerProfesores() {
	    List<Map<String, Object>> lista = new ArrayList<>();
	    // Solo tipo_id = 3 (Profesores)
	    String sql = "SELECT id, nombre, apellidos FROM users WHERE tipo_id = 3";

	    try (Connection conn = conectar(); 
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Map<String, Object> prof = new HashMap<>();
	            prof.put("id", rs.getInt("id"));
	            prof.put("nombreCompleto", rs.getString("nombre") + " " + rs.getString("apellidos"));
	            lista.add(prof);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return lista;
	}
	
	// Sacar las reuniones del profesor

	public List<Map<String, String>> obtenerReunionesProfesor(int idProfesor) {
		List<Map<String, String>> lista = new ArrayList<>();

		String sql = "SELECT r.id, r.estado, r.fecha, r.aula_id, u.nombre, u.apellidos " + "FROM reuniones r "
				+ "JOIN users u ON r.alumno_id = u.id " + "WHERE r.profesor_id = ? " + "ORDER BY r.fecha DESC";

		try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idProfesor);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Map<String, String> reunion = new HashMap<>();
				reunion.put("id", String.valueOf(rs.getInt("id")));
				reunion.put("estado", rs.getString("estado"));
				reunion.put("fecha", rs.getString("fecha"));
				reunion.put("aula", String.valueOf(rs.getInt("aula_id")));
				reunion.put("alumno", rs.getString("nombre") + rs.getString("apellidos"));
				lista.add(reunion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	// Sacar los modulos

	public List<Map<String, String>> obtenerModulos() {
		List<Map<String, String>> lista = new ArrayList<>();
		String sql = "SELECT m.id, m.nombre, m.curso, c.nombre AS nombre_ciclo " + 
					 "FROM modulos m " + 
					 "JOIN ciclos c ON m.ciclo_id = c.id " + 
					 "ORDER BY c.nombre, m.curso, m.nombre";

		try (Connection conn = conectar();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Map<String, String> modulo = new HashMap<>();
				modulo.put("id", String.valueOf(rs.getInt("id")));
				modulo.put("nombre", rs.getString("nombre"));
				modulo.put("curso", String.valueOf(rs.getInt("curso"))); 
				modulo.put("ciclo", rs.getString("nombre_ciclo"));   
				
				lista.add(modulo);
			}
			return lista;
		} catch (SQLException e) {
			System.err.println("Error obteniendo módulos: " + e.getMessage());
			e.printStackTrace();
		}
		return lista;
	}
}