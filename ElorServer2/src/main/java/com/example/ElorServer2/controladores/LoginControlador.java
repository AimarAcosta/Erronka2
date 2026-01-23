package com.example.ElorServer2.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import modelo.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

@RestController
@RequestMapping("/api/android")
public class LoginControlador {

    @PersistenceContext
    private EntityManager entityManager;

    // LOGIN ESTILO COMPAÑERO
    // URL: http://localhost:8080/api/android/login/alumno1/1234
    
    @GetMapping(value = "/login/{username}/{password}", produces = "application/json")
    public String UsuariosLogin(@PathVariable String username, @PathVariable String password) {
        
        try {
            // 1. HQL CON JOIN FETCH (Tal cual lo tiene él)
            // Esto carga el Usuario Y sus Tipos de una tacada.
            String hql = "SELECT u FROM Users u JOIN FETCH u.tipos t WHERE u.username = :username AND u.password = :password";
            
            Users user = entityManager.createQuery(hql, Users.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
            
            // 2. GSON CON EXCLUDE (La magia de tu compañero)
            // Esto dice: "Solo convierte a JSON lo que tenga la etiqueta @Expose"
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting() // (Opcional) Para que se vea bonito
                    .create();
            
            String usrsJson = gson.toJson(user);
            return usrsJson;
            
        } catch (NoResultException e) {
            // Si no encuentra usuario
            return "null";
        } catch (Exception e) {
            // Si peta por otra cosa
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}