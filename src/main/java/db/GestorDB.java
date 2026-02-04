package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import modelo.*; 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

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
        // PRUEBA DE FUEGO: Mira la consola.
        // Si pones idProfesor=3 debería salir Roman. Si pones 4, Iker.
        System.out.println(">>> DEBUG: Buscando reuniones en BBDD para id_user: " + idProfesor);

        List<Map<String, String>> lista = new ArrayList<>();

        // EXPLICACIÓN DEL HQL:
        // 1. FROM Reuniones r
        // 2. JOIN r.usersByProfesorId p -> Obliga a Hibernate a unir con la tabla 'users' usando la FK 'profesor_id'
        // 3. LEFT JOIN r.usersByAlumnoId a -> LEFT JOIN para no perder reuniones si el alumno es NULL (que en tu BBDD lo permite)
        // 4. WHERE p.id = :id -> Filtra usando la Primary Key de la tabla Users unida como profesor.
        
        String hql = "SELECT r.idReunion, r.estado, r.fecha, r.aula, a.nombre, a.apellidos, p.nombre " + 
                     "FROM Reuniones r " +
                     "JOIN r.usersByProfesorId p " + 
                     "LEFT JOIN r.usersByAlumnoId a " +
                     "WHERE p.id = :idParam " + 
                     "ORDER BY r.fecha DESC";

        try (Session session = getSession()) {
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("idParam", idProfesor);
            
            List<Object[]> res = query.list();
           // System.out.println(">>> DEBUG: Filas encontradas: " + res.size());

            for (Object[] row : res) {
                Map<String, String> reunion = new HashMap<>();
                reunion.put("id", safeStr(row[0]));
                reunion.put("estado", safeStr(row[1]));
                reunion.put("fecha", safeStr(row[2])); // Formatear si es necesario
                reunion.put("aula", safeStr(row[3]));
                
                // Gestión de Alumno NULL (Tu BBDD permite alumno_id NULL en reuniones)
                String nombreAlum = (row[4] != null) ? row[4].toString() : "Sin";
                String apellAlum = (row[5] != null) ? row[5].toString() : "Asignar";
                reunion.put("alumno", nombreAlum + " " + apellAlum);
                
                // Debug extra: Para verificar que estás trayendo al profesor correcto
                // reunion.put("profesor_nombre", safeStr(row[6])); 

                lista.add(reunion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Helper pequeño para evitar NullPointerException en los .toString()
    private String safeStr(Object o) {
        return o != null ? o.toString() : "";
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
    
    public boolean guardarReunion(Map<String, Object> datos) {
        org.hibernate.Transaction tx = null;
        
        try (Session session = getSession()) {
            tx = session.beginTransaction();

            Reuniones reunion = new Reuniones();

            reunion.setTitulo((String) datos.get("titulo"));
            reunion.setAsunto((String) datos.get("asunto"));
            reunion.setIdCentro((String) datos.get("centro"));
            reunion.setEstado("PENDIENTE");
            reunion.setEstadoEus("ZAIN"); 

            reunion.setAula((String) datos.get("municipio")); 

            String fechaStr = (String) datos.get("fecha"); 
            String horaStr = (String) datos.get("hora");   
            
            if (fechaStr != null && !fechaStr.isEmpty()) {
                if (horaStr == null || horaStr.isEmpty()) {
                    horaStr = "00:00";
                }
                String momentoCompleto = fechaStr + " " + horaStr + ":00";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateParsed = sdf.parse(momentoCompleto);

                reunion.setFecha(new java.sql.Timestamp(dateParsed.getTime()));
            }

            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            reunion.setCreatedAt(ahora);
            reunion.setUpdatedAt(ahora);

            int idProf = ((Number) datos.get("idProfesor")).intValue();
            int idAlum = ((Number) datos.get("idEstudiante")).intValue();

            Users profesor = session.get(Users.class, idProf);
            Users alumno = session.get(Users.class, idAlum);

            reunion.setUsersByProfesorId(profesor);
            reunion.setUsersByAlumnoId(alumno);

            session.save(reunion);
            tx.commit();
            
            System.out.println("Reunión guardada correctamente en la base de datos");
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error en GestorDB.guardarReunion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}