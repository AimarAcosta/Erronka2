package conectores;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

import modelo.Users;
import modelo.Horarios;

public class ConexionDB {

    public static List ConseguirUsuarios() {
        // 1. La URL de TU servidor (Asegúrate de que la IP es correcta)
        String url = "http://10.5.104.124:8080/api/android/usuarios";

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();

                Type listaUsuarios = new TypeToken<List<Users>>(){}.getType();
                List<Users> usuarios = gson.fromJson(response.body(), listaUsuarios);
                
                return usuarios;
            } else {
                System.out.println("❌ Error: El servidor respondió con código " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
		return null;
    }
    
    public static List ConseguirHorarios(String usuario) {
    	final String url = "http://10.5.104.124:8080/api/android/horarios/todos?profesor=" + usuario;
    	
    	try {
    		HttpClient client = HttpClient.newHttpClient();
    		
    		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    		
    		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    		if(response.statusCode() == 200) {
				Gson gson = new Gson();
				
				Type listaHorarios = new TypeToken<List<Horarios>>(){}.getType();
				List<Horarios> horarios = gson.fromJson(response.body(), listaHorarios);
				System.out.println("Horarios cargados: " + horarios.size());
				
				return horarios;
    		}else {
    			//Si no funciona me pego un tiro
    			System.out.println("Error de conexión: Código " + response.statusCode());
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
		return null;
    }
}
