package com.example.ElorServer2.controladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    
 // ==========================================
    // 🏫 ENDPOINT: HORARIOS PROFESORES / CENTRO
    // ==========================================
    // 1. Ver todo: http://localhost:8080/api/android/horarios/todos
    // 2. Por Ciclo: http://localhost:8080/api/android/horarios/todos?ciclo=DAM
    // 3. Por Profe: http://localhost:8080/api/android/horarios/todos?profesor=Jonander
    
    @GetMapping(value = "/horarios/todos", produces = "application/json")
    public String obtenerHorariosCentro(
            @RequestParam(required = false) String ciclo,
            @RequestParam(required = false) String profesor) { // <-- NUEVO PARÁMETRO
        try {
            // Base de la consulta
            String hql = "SELECT h.dia, h.hora, m.nombre, h.aula, uProfe.nombre, uProfe.apellidos, c.nombre, m.curso " +
                         "FROM Horarios h " +
                         "JOIN h.modulos m " +
                         "JOIN m.ciclos c " +
                         "JOIN h.users uProfe " + // uProfe es el dueño del horario
                         "WHERE 1=1 "; 

            // Filtro 1: Por nombre del Ciclo (ej: DAM, Marketing)
            if (ciclo != null && !ciclo.isEmpty()) {
                hql += "AND c.nombre LIKE :nombreCiclo ";
            }
            
            // Filtro 2: Por nombre del Profesor (ej: Jonander, Garmendia...)
            if (profesor != null && !profesor.isEmpty()) {
                // Buscamos en Nombre O en Apellidos
                hql += "AND (uProfe.nombre LIKE :nombreProfe OR uProfe.apellidos LIKE :nombreProfe) ";
            }
            
            hql += "ORDER BY c.nombre, m.curso, h.dia, h.hora";

            // Creamos la Query
            var query = entityManager.createQuery(hql, Object[].class);
            
            // Rellenamos los huecos (parámetros) si existen
            if (ciclo != null && !ciclo.isEmpty()) {
                query.setParameter("nombreCiclo", "%" + ciclo + "%");
            }
            if (profesor != null && !profesor.isEmpty()) {
                query.setParameter("nombreProfe", "%" + profesor + "%");
            }

            // Ejecutamos y convertimos a JSON
            List<Object[]> resultados = query.getResultList();
            List<Map<String, Object>> listaHorarios = new ArrayList<>();

            for (Object[] fila : resultados) {
                Map<String, Object> item = new HashMap<>();
                item.put("dia", fila[0]);
                item.put("hora", fila[1]);
                item.put("modulo", fila[2]);
                item.put("aula", fila[3]);
                item.put("profesor", fila[4] + " " + fila[5]);
                item.put("ciclo", fila[6]);
                item.put("curso", fila[7]);
                
                listaHorarios.add(item);
            }

            Gson gson = new Gson();
            return gson.toJson(listaHorarios);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}