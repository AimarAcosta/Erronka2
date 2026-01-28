package conectores;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Cipher;
import com.google.gson.Gson;

import modelo.Users;

public class ClienteSocket {

	private static Socket socket;
	private static ObjectOutputStream out;
	private static ObjectInputStream in;
	private static PublicKey serverPublicKey;
	private static Gson gson = new Gson();
	
	public static void conectar() throws Exception {
		if (socket == null || socket.isClosed()) {
			socket = new Socket("10.5.104.124", 9000); // IP de la Maquina Virtual
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			
			// Recibir la clave publica
			String rawKey = (String) in.readObject();
			if (rawKey.startsWith("PUBLIC_KEY:")) {
				String keyStr = rawKey.substring(11);
				byte[] publicBytes = Base64.getDecoder().decode(keyStr);
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                serverPublicKey = keyFactory.generatePublic(keySpec);
                System.out.println("Bikain, Clave recibida");
			}
		}
	}
	
	// Metodo de Login y el metodo que cifra la contraseña
    public static Users realizarLogin(String username, String password) {
        try {
            conectar();

            // Cifrar la contraseña
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] bytesCifrados = cipher.doFinal(password.getBytes());
            String passCifrada = Base64.getEncoder().encodeToString(bytesCifrados);

            Map<String, Object> peticion = Map.of(
                "tipo", "LOGIN",
                "contenido", Map.of("username", username, "password", passCifrada)
            );

            // Enviamos al servidor
            out.writeObject(gson.toJson(peticion));
            out.flush();

            // Recibimos la respuesta
            String respuestaJson = (String) in.readObject();
            Map<String, Object> respuesta = gson.fromJson(respuestaJson, Map.class);

            if ("LOGIN_OK".equals(respuesta.get("tipo"))) {
                //Convertimos el contenido a un objeto User si el login va maquina
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
}
