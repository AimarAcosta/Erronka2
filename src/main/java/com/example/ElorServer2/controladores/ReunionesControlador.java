package com.example.ElorServer2.controladores;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import modelo.Reuniones;
import modelo.Users;
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/android")
public class ReunionesControlador {

	@PersistenceContext
	private EntityManager entityManager;

	// ==========================================
	// 📝 ENDPOINT PARA CREAR REUNIÓN (POST)
	// ==========================================
	// URL: http://localhost:8080/api/android/reuniones/crear

	@PostMapping(value = "/reuniones/crear")
	@Transactional // ¡OBLIGATORIO! Sin esto no se guardan los datos en la BD
	public String crearReunion(@RequestBody String jsonEntrada) {
		try {
			// 1. Convertimos el JSON que nos envían a un objeto Java temporal
			Gson gson = new Gson();
			ReunionDatos datos = gson.fromJson(jsonEntrada, ReunionDatos.class);

			// 2. Buscamos al Alumno y al Profesor por su ID
			// (Necesitamos los objetos enteros para guardarlos en la reunión)
			Users alumno = entityManager.find(Users.class, datos.alumnoId);
			Users profesor = entityManager.find(Users.class, datos.profesorId);

			if (alumno == null || profesor == null) {
				return "Error: No existe el alumno o el profesor indicado.";
			}

			// 3. Creamos la nueva Reunión y rellenamos datos
			Reuniones nuevaReunion = new Reuniones();

			nuevaReunion.setTitulo(datos.titulo);
			nuevaReunion.setAsunto(datos.asunto);
			nuevaReunion.setAula(datos.aula);

			// Asignamos las relaciones
			nuevaReunion.setUsersByAlumnoId(alumno);
			nuevaReunion.setUsersByProfesorId(profesor);

			// Datos automáticos
			nuevaReunion.setEstado("Pendiente"); // Estado por defecto
			nuevaReunion.setIdCentro("1"); // Ajusta esto si tenéis varios centros

			// Fechas (Timestamp)
			// Convertimos el String de fecha "2026-01-25 10:00:00" a Timestamp
			try {
				nuevaReunion.setFecha(Timestamp.valueOf(datos.fecha));
			} catch (Exception e) {
				return "Error en el formato de fecha. Usa: yyyy-mm-dd hh:mm:ss";
			}

			Timestamp ahora = new Timestamp(System.currentTimeMillis());
			nuevaReunion.setCreatedAt(ahora);
			nuevaReunion.setUpdatedAt(ahora);

			// 4. GUARDAR EN BASE DE DATOS
			entityManager.persist(nuevaReunion);

			return "OK: Reunión creada correctamente con ID: " + nuevaReunion.getIdReunion();

		} catch (Exception e) {
			e.printStackTrace();
			return "Error al crear reunión: " + e.getMessage();
		}
	}

	// ==========================================
	// CLASE AUXILIAR PARA LEER EL JSON
	// ==========================================
	// Esto define qué datos esperamos recibir del móvil
	private static class ReunionDatos {
		String titulo;
		String asunto;
		String aula;
		String fecha; // Formato esperado: "2026-01-30 10:00:00"
		int alumnoId; // ID del usuario alumno (NO el nombre)
		int profesorId; // ID del usuario profesor
	}

	// ==========================================
	// 📅 ENDPOINT: VER REUNIONES (GET)
	// ==========================================
	// 1. Ver TODAS: http://localhost:8080/api/android/reuniones
	// 2. Ver las MÍAS (filtro):
	// http://localhost:8080/api/android/reuniones?idUsuario=2

	@GetMapping(value = "/reuniones/{idUsuario}", produces = "application/json")
	public String obtenerReuniones(@PathVariable("idUsuario") Integer idUsuario) {
		try {
			// Mismo HQL, buscando si es Profe O Alumno
			String hql = "SELECT r.idReunion, r.titulo, r.asunto, r.aula, r.fecha, r.estado, "
					+ "uProfe.nombre, uProfe.apellidos, uAlum.nombre, uAlum.apellidos " + "FROM Reuniones r "
					+ "LEFT JOIN r.usersByProfesorId uProfe " + "LEFT JOIN r.usersByAlumnoId uAlum "
					+ "WHERE (uProfe.id = :id OR uAlum.id = :id) " + // Filtro obligatorio aquí
					"ORDER BY r.fecha DESC";

			var query = entityManager.createQuery(hql, Object[].class);
			query.setParameter("id", idUsuario); // Ya no hace falta el if, el ID viene sí o sí

			List<Object[]> resultados = query.getResultList();

			List<Map<String, Object>> lista = new ArrayList<>();

			for (Object[] fila : resultados) {
				Map<String, Object> item = new HashMap<>();
				item.put("id", fila[0]);
				item.put("titulo", fila[1]);
				item.put("asunto", fila[2]);
				item.put("aula", fila[3]);
				item.put("fecha", fila[4] != null ? fila[4].toString() : null);
				item.put("estado", fila[5]);

				// Control de nulos para evitar "null null"
				String nombreProfe = (fila[6] != null ? fila[6] : "Sin asignar") + " "
						+ (fila[7] != null ? fila[7] : "");
				String nombreAlum = (fila[8] != null ? fila[8] : "Sin asignar") + " "
						+ (fila[9] != null ? fila[9] : "");

				item.put("profesor", nombreProfe.trim());
				item.put("alumno", nombreAlum.trim());

				lista.add(item);
			}

			return new Gson().toJson(lista);

		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Error: " + e.getMessage() + "\" }";
		}
	}

	// ==========================================
	// 🔄 ENDPOINT: ACTUALIZAR ESTADO (Aceptar/Denegar)
	// ==========================================
	// URL: http://localhost:8080/api/android/reuniones/estado
	// METODO: POST (o PUT)

	@PostMapping(value = "/reuniones/estado")
	@Transactional // ¡IMPORTANTE! Para guardar cambios
	public String actualizarEstadoReunion(@RequestBody String jsonEntrada) {
		try {
			Gson gson = new Gson();
			EstadoDatos datos = gson.fromJson(jsonEntrada, EstadoDatos.class);

			// 1. Buscamos la reunión
			Reuniones reunion = entityManager.find(Reuniones.class, datos.idReunion);

			if (reunion == null) {
				return "Error: No existe esa reunión.";
			}

			// 2. Cambiamos el estado
			// (Opcional: Validar que el texto sea correcto)
			reunion.setEstado(datos.nuevoEstado);

			// 3. Actualizamos la fecha de modificación (opcional pero recomendado)
			reunion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

			// 4. Guardamos (al ser @Transactional, el 'merge' es automático al terminar,
			// pero lo ponemos explícito para asegurar).
			entityManager.merge(reunion);

			return "OK: Estado cambiado a " + datos.nuevoEstado;

		} catch (Exception e) {
			e.printStackTrace();
			return "Error al actualizar: " + e.getMessage();
		}
	}

	// Clase para recibir el cambio de estado
	private static class EstadoDatos {
		int idReunion;
		String nuevoEstado; // "Aceptada", "Denegada", etc.
	}
}