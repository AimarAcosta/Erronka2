package com.example.ElorServer2.controladores;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import modelo.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@RestController
@RequestMapping("/api/android")
public class LoginControlador {

	@PersistenceContext
	private EntityManager entityManager;

	// ==========================================
	// LOGIN (CORREGIDO SEGÚN TU SQL REAL)
	// ==========================================

	@GetMapping(value = "/login/{username}/{password}", produces = "application/json")
	public String UsuariosLogin(@PathVariable String username, @PathVariable String password) {

		try {
			// 1. Login estándar
			String hql = "SELECT u FROM Users u JOIN FETCH u.tipos t WHERE u.username = :username AND u.password = :password";

			Users user = entityManager.createQuery(hql, Users.class).setParameter("username", username)
					.setParameter("password", password).getSingleResult();

			// 2. BUSCAR CICLO Y CURSO
			if (user.getTipos().getId() == 4) { // Si es alumno
				try {
					// SQL SIMPLIFICADO Y CORRECTO:
					// 1. Usamos 'alum_id' (que es como se llama en tu SQL).
					// 2. Unimos directamente 'matriculaciones' con 'ciclos' (sin pasar por
					// modulos).

					String sql = "SELECT c.nombre, mat.curso " + "FROM matriculaciones mat "
							+ "JOIN ciclos c ON mat.ciclo_id = c.id " + "WHERE mat.alum_id = :userId " + // <--- ¡NOMBRE
																											// CORRECTO!
							"LIMIT 1";

					Query query = entityManager.createNativeQuery(sql);
					query.setParameter("userId", user.getId());

					Object[] resultado = (Object[]) query.getSingleResult();

					if (resultado != null) {
						user.setCiclo((String) resultado[0]); // Nombre del ciclo (ej: DAM)
						// Convertimos el curso a Integer de forma segura
						user.setCurso(((Number) resultado[1]).intValue());
					}
				} catch (Exception ex) {
					System.out.println("⚠️ Alumno sin matrícula: " + ex.getMessage());
				}
			}

			// 3. Devolver JSON
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

			return gson.toJson(user);

		} catch (NoResultException e) {
			return "null";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

	// ==========================================
	// POST FOTO (SIN CAMBIOS)
	// ==========================================
	@PostMapping(value = "/usuarios/foto")
	@Transactional
	public String cambiarFoto(@RequestBody FotoDatos datos) {
		try {
			Users usuario = entityManager.find(Users.class, datos.idUsuario);
			if (usuario == null)
				return "Error: Usuario no encontrado";

			usuario.setArgazkiaUrl(datos.urlFoto);
			entityManager.merge(usuario);
			return "OK";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	static class FotoDatos {
		public int idUsuario;
		public String urlFoto;
	}
}