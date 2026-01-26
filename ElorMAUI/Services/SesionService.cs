using ElorMAUI.Models;

namespace ElorMAUI.Services
{
    public class SesionService
    {
        // Guardamos al usuario cuando haga login
        public Usuario UsuarioActual { get; set; }

        // Método para saber si hay alguien logueado
        public bool EstaLogueado()
        {
            return UsuarioActual != null && UsuarioActual.id > 0;
        }

        // Método para cerrar sesión
        public void CerrarSesion()
        {
            UsuarioActual = null;
        }
    }
}