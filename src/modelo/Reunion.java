package modelo;

import java.io.Serializable;

public class Reunion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String titulo;
    private String asunto;
    private String fecha; 
    private String hora;  
    private String territorio;
    private String municipio;
    private String centro;
    private Profesor profesor; 
    private Users estudiante;   
    // Constructor vacío (necesario para GSON / Hibernate)
    public Reunion() {}

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getTerritorio() { return territorio; }
    public void setTerritorio(String territorio) { this.territorio = territorio; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getCentro() { return centro; }
    public void setCentro(String centro) { this.centro = centro; }

    public Profesor getProfesor() { return profesor; }
    public void setProfesor(Profesor profesor) { this.profesor = profesor; }

    public Users getEstudiante() { return estudiante; }
    public void setEstudiante(Users estudiante) { this.estudiante = estudiante; }
}