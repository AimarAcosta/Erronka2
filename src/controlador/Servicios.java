package controlador;

import javax.swing.JTextField;

public class Servicios {
	public boolean servicioLogin(String username, String password) {
		boolean login = false;
		if(username.equals("admin") && password.equals("admin123")) {
			login = true;
		}
		return login;
	}
}
