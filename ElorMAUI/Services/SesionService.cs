using ElorMAUI.Models;

namespace ElorMAUI.Services
{
    public class SesionService
    {
        // Guardamos al usuario cuando haga login
        public Usuario UsuarioActual { get; set; }

        public bool EstaLogueado()
        {
            return UsuarioActual != null && UsuarioActual.id > 0;
        }
        public void CerrarSesion()
        {
            UsuarioActual = null;
        }
    }
}