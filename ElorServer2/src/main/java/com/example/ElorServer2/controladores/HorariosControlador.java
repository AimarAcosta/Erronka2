package com.example.ElorServer2.controladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import modelo.Users; // Asegúrate de importar esto
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/android")
public class HorariosControlador {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping(value = "/horario/{username}", produces = "application/json")
    public String obtenerHorario(@PathVariable String username) {
        try {
            // PASO 1: Primero averiguamos QUIÉN es este usuario (¿Profe o Alumno?)
            String hqlUser = "FROM Users u WHERE u.username = :user";
            Users usuario = entityManager.createQuery(hqlUser, Users.class)
                    .setParameter("user", username)
                    .getSingleResult();

            String nombreTipo = usuario.getTipos().getName().toLowerCase(); // "alumno" o "profesor"
            String hql = "";

            // PASO 2: Elegimos la consulta según el tipo
            if (nombreTipo.contains("profesor") || nombreTipo.contains("irakasle")) {
                
                // === CONSULTA PARA PROFESORES ===
                // El camino es más corto: Usuario -> Horarios -> Módulos
                hql = "SELECT h.dia, h.hora, m.nombre, h.aula, u.nombre, u.apellidos " +
                      "FROM Users u, Horarios h, Modulos m " +
                      "WHERE u.username = :nombreUsuario " +
                      "AND h.users.id = u.id " +       // El profe está asignado al horario directamente
                      "AND h.modulos.id = m.id " +     // Relación Horario -> Módulo
                      "ORDER BY h.dia, h.hora";
                      
            } else {
                
                // === CONSULTA PARA ALUMNOS (La que ya tenías) ===
                // El camino es largo: Usuario -> Matrícula -> Módulo -> Horario
                hql = "SELECT h.dia, h.hora, m.nombre, h.aula, uProfe.nombre, uProfe.apellidos " +
                      "FROM Users uAlum, Matriculaciones mat, Modulos m, Horarios h, Users uProfe " +
                      "WHERE uAlum.username = :nombreUsuario " +
                      "AND mat.users.id = uAlum.id " +      
                      "AND mat.ciclos.id = m.ciclos.id " +  
                      "AND mat.curso = m.curso " +          
                      "AND h.modulos.id = m.id " +          
                      "AND h.users.id = uProfe.id " +       
                      "ORDER BY h.dia, h.hora";
            }

            // PASO 3: Ejecutamos la consulta elegida
            List<Object[]> resultados = entityManager.createQuery(hql, Object[].class)
                    .setParameter("nombreUsuario", username)
                    .getResultList();

            // PASO 4: Construimos el JSON
            List<Map<String, Object>> listaHorario = new ArrayList<>();

            for (Object[] fila : resultados) {
                Map<String, Object> item = new HashMap<>();
                item.put("dia", fila[0]);
                item.put("hora", fila[1]);
                item.put("modulo", fila[2]);
                item.put("aula", fila[3]);
                item.put("profesor", fila[4] + " " + fila[5]); 
                // Nota: Si soy profe, saldrá mi propio nombre en "profesor", 
                // pero así mantenemos el formato igual para la App.
                
                listaHorario.add(item);
            }

            Gson gson = new Gson();
            return gson.toJson(listaHorario);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}