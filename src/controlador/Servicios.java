package controlador;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import modelo.Users;

public class Servicios {
	private static Users loggedUser;

    public static void setLoggedUser(Users user) {
        loggedUser = user;
    }

    public static Users getLoggedUser() {
        return loggedUser;
    }
    
    public static boolean estaEnEstaSemana(Date fecha) {
        LocalDate fechaLocal = fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate hoy = LocalDate.now();

        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(6);

        return !fechaLocal.isBefore(inicioSemana)
            && !fechaLocal.isAfter(finSemana);
    }
}
