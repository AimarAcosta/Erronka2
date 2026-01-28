package controlador;

import modelo.Users;

public class Servicios {
	private static Users loggedUser;

    public static void setLoggedUser(Users user) {
        loggedUser = user;
    }

    public static Users getLoggedUser() {
        return loggedUser;
    }
}
