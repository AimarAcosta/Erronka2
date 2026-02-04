package sockets;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.HashMap; // Necesario si usas HashMap explícito
import db.GestorDB;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HiloCliente extends Thread {

    private Socket socket;
    private GestorDB db;
    private Gson gson;
    private int idUsuarioLogueado = -1;

    public HiloCliente(Socket socket) {
        this.socket = socket;
        this.db = new GestorDB();
        this.gson = new Gson();
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println(" Cliente conectado");

            while (true) {
                // Leemos el JSON como String 
                String jsonRecibido = (String) in.readObject();
                System.out.println(" Cliente: " + jsonRecibido);

                @SuppressWarnings("unchecked")
                Map<String, Object> mensaje = gson.fromJson(jsonRecibido, Map.class);
                String tipo = (String) mensaje.get("tipo");
                String respuesta = "";
                
                @SuppressWarnings("unchecked")
                Map<String, Object> contenidoRecibido = (Map<String, Object>) mensaje.get("contenido");

                // -------------------------------------------------
                // BLOQUE 1: LOGIN
                // -------------------------------------------------
                if ("LOGIN".equals(tipo)) {
                    String user = (String) contenidoRecibido.get("username");
                    String pass = (String) contenidoRecibido.get("password"); 
                    
                    System.out.println("Intentando login con: " + user + " / " + pass);

                    Map<String, Object> usuario = db.login(user, pass);

                    if (usuario != null) {
                        this.idUsuarioLogueado = Integer.parseInt(usuario.get("id").toString());
                        respuesta = gson.toJson(Map.of("tipo", "LOGIN_OK", "contenido", usuario));
                    } else {
                        respuesta = gson.toJson(Map.of("tipo", "LOGIN_ERROR", "contenido", "Usuario o pass incorrectos"));
                    }

                // -------------------------------------------------
                // BLOQUE 2: GET_HORARIOS
                // -------------------------------------------------
                } else if ("GET_HORARIOS".equals(tipo)) {
                    if (this.idUsuarioLogueado != -1) {
                        int idTarget = this.idUsuarioLogueado;
                        if (contenidoRecibido != null && contenidoRecibido.containsKey("idProfesor")) {
                            idTarget = ((Number) contenidoRecibido.get("idProfesor")).intValue();
                        }
                        List<Map<String, String>> horarios = db.obtenerHorarios(idTarget);
                        respuesta = gson.toJson(Map.of("tipo", "GET_HORARIOS_OK", "contenido", horarios));
                    } else {
                        respuesta = gson.toJson(Map.of("tipo", "ERROR", "contenido", "No logueado"));
                    }

                // -------------------------------------------------
                // BLOQUE 3: GET_PROFESORES
                // -------------------------------------------------
                } else if ("GET_PROFESORES".equals(tipo)) {
                    List<Map<String, Object>> profesores = db.obtenerProfesores();
                    respuesta = gson.toJson(Map.of("tipo", "GET_PROFESORES_OK", "contenido", profesores));

                // -------------------------------------------------
                // BLOQUE 4: GET_MODULOS
                // -------------------------------------------------
                } else if ("GET_MODULOS".equals(tipo)) {
                    List<Map<String, String>> modulos = db.obtenerModulos();
                    respuesta = gson.toJson(Map.of("tipo", "GET_MODULOS_OK", "contenido", modulos));
                
                // -------------------------------------------------
                // BLOQUE 5: GET_ESTUDIANTES
                // -------------------------------------------------
                } else if ("GET_ESTUDIANTES".equals(tipo)) {
                    List<Map<String, String>> estudiantes = db.obtenerEstudiantes();
                    respuesta = gson.toJson(Map.of("tipo", "GET_ESTUDIANTES_OK", "contenido", estudiantes));
                
                // -------------------------------------------------
                // BLOQUE 6: AGENDAR_REUNION
                // ------------------------------------------------- 
                } else if ("AGENDAR_REUNION".equals(tipo)) { 
                    
                    boolean guardado = db.guardarReunion(contenidoRecibido);
                    
                    respuesta = gson.toJson(Map.of(
                        "tipo", guardado ? "AGENDAR_REUNION_OK" : "AGENDAR_REUNION_ERROR",
                        "contenido", guardado ? "Reunión guardada con éxito" : "Error al guardar la reunión"
                    ));

                // -------------------------------------------------
                // BLOQUE 7: GET_REUNIONES
                // -------------------------------------------------
                } else if ("GET_REUNIONES_PROFE".equals(tipo)) {
                    if (this.idUsuarioLogueado != -1) {
                        
                        JsonObject jsonObject = gson.toJsonTree(mensaje).getAsJsonObject();
                        JsonObject contenido = jsonObject.get("contenido").getAsJsonObject();
                        
                        int idObjetivo = this.idUsuarioLogueado; 
                        
                        if (contenido.has("idProfesor")) {
                            idObjetivo = contenido.get("idProfesor").getAsInt();
                        }

                        System.out.println("SERVER DEBUG: ID Logueado=" + this.idUsuarioLogueado + " | ID Solicitado=" + idObjetivo);

                        List<Map<String, String>> reuniones = db.obtenerReunionesProfesor(idObjetivo);
                        
                        respuesta = gson.toJson(Map.of("tipo", "GET_REUNIONES_OK", "contenido", reuniones));
                    }  
                } 

                // Escritura final única para mantener el orden
                if (respuesta != null && !respuesta.isEmpty()) {
                    out.writeObject(respuesta);
                    out.flush();
                }
            }

        } catch (EOFException e) {
            System.out.println(" Cliente desconectado.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(); // Recomendado para ver la traza completa al depurar
        } finally {
            try { if (socket != null) socket.close(); } catch (IOException ex) {}
        }
    }
}