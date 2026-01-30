package conectores;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import modelo.Horario;
import modelo.Horarios;
import modelo.Modulos;
import modelo.Profesor;
import modelo.Reuniones;
import modelo.Users;

public class ClienteSocket {

	class Respuesta<T> {
		private String tipo;
		private T contenido;

		public String getTipo() {
			return tipo;
		}

		public T getContenido() {
			return contenido;
		}
	}

	private static Socket socket;
	private static ObjectOutputStream out;
	private static ObjectInputStream in;
	private static PublicKey serverPublicKey;
	private static Gson gson = new Gson();
	private static List<Map<String, Object>> listaHorarios;
	private static List<Map<String, Object>> listaModulos;

	private static final String IP_SERVIDOR = "10.5.104.124"; 

    private static final int PUERTO = 9000;


    public static void conectar() throws Exception {

        if (socket == null || socket.isClosed()) {
            socket = new Socket(IP_SERVIDOR, PUERTO);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Conectado al servidor");
        }
    }

    public static Users realizarLogin(String username, String password) {

        try {

            conectar();
            // Enviamos la contraseña 
            Map<String, Object> peticion = Map.of(
                "tipo", "LOGIN",
                "contenido", Map.of("username", username, "password", password)
            );
            // Enviamos al servidor
            out.writeObject(gson.toJson(peticion));
            out.flush();

            // Recibimos la respuesta
            String respuestaJson = (String) in.readObject();
            Map<String, Object> respuesta = gson.fromJson(respuestaJson, Map.class);
            if ("LOGIN_OK".equals(respuesta.get("tipo"))) {
                String userJson = gson.toJson(respuesta.get("contenido"));
                return gson.fromJson(userJson, Users.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

	public static String enviarPeticion(String tipo, Map<String, Object> contenido) throws Exception {

		Map<String, Object> peticion = Map.of("tipo", tipo, "contenido", contenido);
		out.writeObject(gson.toJson(peticion));
		out.flush();
		return (String) in.readObject();
	}

	public static List conseguirHorarios(int id) {
		try {
			conectar();
			Map<String, Object> datosEnviar = Map.of("idProfesor", id);
			String respuestaJson = enviarPeticion("GET_HORARIOS", datosEnviar);
			
			//System.out.println(respuestaJson);

			Gson gson = new Gson();
			
			Type tipoRespuesta = new TypeToken<Respuesta<List<Horario>>>() {}.getType();
			Respuesta<List<Horario>> respuesta = gson.fromJson(respuestaJson, tipoRespuesta);
			
			if ("GET_HORARIOS_OK".equals(respuesta.getTipo())) {
				return respuesta.getContenido();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List conseguirModulos() {
		try {
			conectar();
			String respuestaJson = enviarPeticion("GET_MODULOS", Map.of());
			Gson gson = new Gson();
			Map<String, Object> respuesta = gson.fromJson(respuestaJson, Map.class);

			if ("GET_MODULOS_OK".equals(respuesta.get("tipo"))) {
				return (List<Map<String, Object>>) respuesta.get("contenido");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List conseguirProfesores() {
		try {
			conectar();
			Map<String, Object> datosEnviar = Map.of();
			String respuestaJson = enviarPeticion("GET_PROFESORES", datosEnviar);			
			System.out.println(respuestaJson);
			Gson gson = new Gson();
			Type tipoRespuesta = new TypeToken<Respuesta<List<Profesor>>>() {}.getType();
			Respuesta<List<Profesor>> respuesta = gson.fromJson(respuestaJson, tipoRespuesta);
			
			if ("GET_PROFESORES_OK".equals(respuesta.getTipo())) {
				return respuesta.getContenido();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List conseguirReuniones(int id) {
		try {
			conectar();
			Map<String, Object> datosEnviar = Map.of("idProfesor", id);
			String respuestaJson = enviarPeticion("GET_REUNIONES_PROFE", datosEnviar);			
			System.out.println(respuestaJson);
			Gson gson = new Gson();
			Type tipoRespuesta = new TypeToken<Respuesta<List<Reuniones>>>() {}.getType();
			Respuesta<List<Reuniones>> respuesta = gson.fromJson(respuestaJson, tipoRespuesta);
			
			if ("GET_REUNIONES_OK".equals(respuesta.getTipo())) {
				return respuesta.getContenido();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
