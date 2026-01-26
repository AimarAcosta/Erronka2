namespace ElorMAUI.Models
{
    public class Usuario
    {
        public int id { get; set; }
        public string username { get; set; }
        public string email { get; set; }
        public string nombre { get; set; }
        public string apellidos { get; set; }
        public string dni { get; set; }

        public TipoUsuario tipos { get; set; }
    }

    public class TipoUsuario
    {
        public int id { get; set; }
        public string name { get; set; }
    }
}