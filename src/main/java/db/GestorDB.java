package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import modelo.*; // Importamos todas las entidades del paquete modelo

public class GestorDB {

    private Session getSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    // login (Corregido según tu modelo Users y Tipos)
    public Map<String, Object> login(String user, String pass) {
        String hql = "FROM Users u WHERE u.username = :user AND u.password = :pass AND u.tipos.id != 4";

        try (Session session = getSession()) {
            Query<Users> query = session.createQuery(hql, Users.class);
            query.setParameter("user", user);
            query.setParameter("pass", pass);
            Users u = query.uniqueResult();

            if (u != null) {
                Map<String, Object> usuario = new HashMap<>();
                usuario.put("id", u.getId());
                usuario.put("email", u.getEmail());
                usuario.put("username", u.getUsername());
                usuario.put("password", u.getPassword());
                usuario.put("nombre", u.getNombre());
                usuario.put("apellidos", u.getApellidos());
                usuario.put("dni", u.getDni());
                usuario.put("direccion", u.getDireccion());
                usuario.put("telefono1", u.getTelefono1());
                usuario.put("telefono2", u.getTelefono2());
                usuario.put("tipo_id", u.getTipos().getId()); 
                usuario.put("argazkia_url", u.getArgazkiaUrl());
                return usuario;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // Sacar horarios (Corregido: Usa h.users.id y h.modulos)
    public List<Map<String, String>> obtenerHorarios(int idProfesor) {
        List<Map<String, String>> listaHorarios = new ArrayList<>();
        // En tu clase Horarios, el profesor es el objeto "users"
        String hql = "SELECT h.dia, h.hora, m.id, m.nombre, h.aula " +
                     "FROM Horarios h JOIN h.modulos m " +
                     "WHERE h.users.id = :id " +
                     "ORDER BY h.dia, h.hora ASC"; 

        try (Session session = getSession()) {
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("id", idProfesor);
            List<Object[]> res = query.list();

            for (Object[] row : res) {
                Map<String, String> fila = new HashMap<>();
                fila.put("dia", String.valueOf(row[0]));
                fila.put("hora", String.valueOf(row[1]));
                fila.put("moduloID", String.valueOf(row[2]));
                fila.put("modulo", String.valueOf(row[3]));
                fila.put("aula", String.valueOf(row[4]));
                listaHorarios.add(fila);    
            }
        } catch (Exception e) { e.printStackTrace(); }
        return listaHorarios;
    }

    // Sacar las reuniones del profesor (Corregido: Usa r.usersByProfesorId.id)
    public List<Map<String, String>> obtenerReunionesProfesor(int idProfesor) {
        List<Map<String, String>> lista = new ArrayList<>();
        // En tu clase Reuniones, el profesor es "usersByProfesorId" y el alumno "usersByAlumnoId"
        String hql = "SELECT r.idReunion, r.estado, r.fecha, r.aula, u.nombre, u.apellidos " + 
                     "FROM Reuniones r JOIN r.usersByAlumnoId u " +
                     "WHERE r.usersByProfesorId.id = :id ORDER BY r.fecha DESC";

        try (Session session = getSession()) {
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("id", idProfesor);
            List<Object[]> res = query.list();

            for (Object[] row : res) {
                Map<String, String> reunion = new HashMap<>();
                reunion.put("id", String.valueOf(row[0]));
                reunion.put("estado", String.valueOf(row[1]));
                reunion.put("fecha", String.valueOf(row[2]));
                reunion.put("aula", String.valueOf(row[3]));
                reunion.put("alumno", row[4] + " " + row[5]);
                lista.add(reunion);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
    
    // Sacar los modulos (Corregido: Usa m.ciclos)
    public List<Map<String, String>> obtenerModulos() {
        List<Map<String, String>> lista = new ArrayList<>();
        // En tu clase Modulos, la relación es "ciclos" (en plural según tu hbm2java)
        String hql = "SELECT m.id, m.nombre, m.curso, c.nombre " + 
                     "FROM Modulos m JOIN m.ciclos c " + 
                     "ORDER BY c.nombre, m.curso, m.nombre";

        try (Session session = getSession()) {
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> res = query.list();

            for (Object[] row : res) {
                Map<String, String> modulo = new HashMap<>();
                modulo.put("id", String.valueOf(row[0]));
                modulo.put("nombre", String.valueOf(row[1]));
                modulo.put("curso", String.valueOf(row[2]));
                modulo.put("ciclo", String.valueOf(row[3]));
                lista.add(modulo);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
    
 // sacar estudiantes
    public List<Map<String, String>> obtenerEstudiantes() {
        List<Map<String, String>> lista = new ArrayList<>();

        // Navegamos: u.tipos.id para el filtro
        // Para ciclo y curso, unimos con matriculacioneses (m) y de ahí a ciclos (c)
        String hql = "SELECT u.id, u.nombre, u.apellidos, u.username, u.email, c.nombre, m.curso " +
                     "FROM Users u " +
                     "LEFT JOIN u.matriculacioneses m " + // 'matriculacioneses' es el nombre en tu POJO Users
                     "LEFT JOIN m.ciclos c " +             // 'ciclos' es el nombre en tu POJO Matriculaciones (asumido por convención)
                     "WHERE u.tipos.id = 4"; 

        try (Session session = getSession()) {
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> res = query.list();

            for (Object[] row : res) {
                Map<String, String> est = new HashMap<>();
                est.put("id", String.valueOf(row[0])); 
                est.put("nombre", row[1] + " " + row[2]);
                est.put("username", String.valueOf(row[3]));
                est.put("email", String.valueOf(row[4]));
                est.put("ciclo", row[5] != null ? String.valueOf(row[5]) : "Sin asignar");
                est.put("curso", row[6] != null ? String.valueOf(row[6]) : "");
                lista.add(est);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Saca Profesores
    public List<Map<String, Object>> obtenerProfesores() {
        List<Map<String, Object>> lista = new ArrayList<>();
        // Filtramos por u.tipos.id = 3
        String hql = "SELECT u.id, u.nombre, u.apellidos FROM Users u WHERE u.tipos.id = 3";

        try (Session session = getSession()) {
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> res = query.list();

            for (Object[] row : res) {
                Map<String, Object> prof = new HashMap<>();
                prof.put("id", row[0]);
                prof.put("nombreCompleto", row[1] + " " + row[2]);
                lista.add(prof);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}