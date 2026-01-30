package com.example.ElorServer2.controladores;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import modelo.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@RestController
@RequestMapping("/api/android")
public class UsuariosControlador {

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping(value = "/usuarios", produces = "application/json")
	public String getUsuarios() {
		try {
			// 1. Obtener TODOS los usuarios de la base de datos
			List<Users> usuarios = entityManager.createQuery("SELECT u FROM Users u JOIN FETCH u.tipos", Users.class)
					.getResultList();

			// 2. RECORRER LA LISTA PARA RELLENAR DATOS ACADÉMICOS
			for (Users user : usuarios) {

				// Solo si es ALUMNO (id 4), buscamos su matrícula
				if (user.getTipos().getId() == 4) {
					try {
						// Usamos la misma lógica que arreglamos en el Login
						String sql = "SELECT c.nombre, mat.curso " + "FROM matriculaciones mat "
								+ "JOIN ciclos c ON mat.ciclo_id = c.id " + "WHERE mat.alum_id = :userId " + "LIMIT 1";

						Query query = entityManager.createNativeQuery(sql);
						query.setParameter("userId", user.getId());

						Object[] resultado = (Object[]) query.getSingleResult();

						if (resultado != null) {
							user.setCiclo((String) resultado[0]); // Ej: DAM
							user.setCurso(((Number) resultado[1]).intValue()); // Ej: 2
						}
					} catch (Exception ex) {
						// Si el alumno no está matriculado, no pasa nada, se queda null
					}
				}
			}

			// 3. Convertir a JSON
			// Gson incluirá ciclo y curso porque les pusimos @Expose en Users.java
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

			return gson.toJson(usuarios);

		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}
}