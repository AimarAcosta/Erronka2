package com.example.ElorServer2.controladores;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import modelo.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/android")
public class UsuariosControlador {

    @PersistenceContext
    private EntityManager entityManager;

    // ==========================================
    // 👥 ENDPOINT: OBTENER TODOS LOS USUARIOS
    // ==========================================
    // Opción 1 (Todos): http://localhost:8080/api/android/usuarios
    // Opción 2 (Filtrar): http://localhost:8080/api/android/usuarios?rol=profesor
    
    @GetMapping(value = "/usuarios", produces = "application/json")
    public String obtenerUsuarios(@RequestParam(required = false) String rol) {
        try {
            String hql;
            
            // Si nos piden un rol específico (ej: ?rol=profesor), filtramos
            if (rol != null && !rol.isEmpty()) {
                // Usamos JOIN FETCH para cargar también el Tipo (Profesor/Alumno) de golpe y que sea rápido
                hql = "FROM Users u JOIN FETCH u.tipos t WHERE t.name LIKE :rol ORDER BY u.apellidos";
            } else {
                // Si no piden nada, devolvemos TODOS
                hql = "FROM Users u JOIN FETCH u.tipos ORDER BY u.apellidos";
            }

            // Ejecutamos la consulta
            var query = entityManager.createQuery(hql, Users.class);
            
            if (rol != null && !rol.isEmpty()) {
                query.setParameter("rol", "%" + rol + "%"); // Los % son para que busque "Profesor" o "Profesora"
            }

            List<Users> listaUsuarios = query.getResultList();

            // CONVERSIÓN A JSON (Usando la configuración de tu compañero)
            // Esto es vital para que respete los @Expose que pusiste en el modelo
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation() // Solo lo que tenga @Expose
                    .setPrettyPrinting()
                    .create();
            
            return gson.toJson(listaUsuarios);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al obtener usuarios: " + e.getMessage();
        }
    }
}