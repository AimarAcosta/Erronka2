package controlador;

import java.util.List;

import javax.swing.JTextField;
import modelo.Users;



public class Servicios {
	private static Users loggedUser;
	public boolean servicioLogin(List<Users> users, String usuario, String contraseña) {
		boolean ok = users.stream()
			    .anyMatch(u -> 
			        u.getUsername().equals(usuario) &&
			        u.getPassword().equals(contraseña)
			    );
		loggedUser = users.stream()
			    .filter(u -> 
			        u.getUsername().equals(usuario) &&
			        u.getPassword().equals(contraseña)
			    )
			    .findFirst()
			    .orElse(null);
		return ok;
	}
	
	public static Users getLoggedUser() {
		return loggedUser;
	}
	
}
