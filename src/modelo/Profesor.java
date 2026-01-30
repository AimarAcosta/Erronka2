package modelo;

public class Profesor {
	private int id;
	private String nombreCompleto;
	
	public Profesor(int id, String nombreCompleto) {
		super();
		this.id = id;
		this.nombreCompleto = nombreCompleto;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	@Override
	public String toString() {
		return "Profesor [id=" + id + ", nombreCompleto=" + nombreCompleto + "]";
	}
	
}
