package modelo;

public class Horario {
	private String aula;
	private int hora;
	private int moduloID;
	private String dia;
	private String modulo;
	
	public String toString() {
		return "Horario [aula=" + aula + ", hora=" + hora + ", ModuloID=" + moduloID + ", dia=" + dia + ", modulo="
				+ modulo + "]";
	}
	
	public Horario(String aula, int hora, int moduloID, String dia, String modulo) {
		super();
		this.aula = aula;
		this.hora = hora;
		this.moduloID = moduloID;
		this.dia = dia;
		this.modulo = modulo;
	}
	
	public String getAula() {
		return aula;
	}
	public void setAula(String aula) {
		this.aula = aula;
	}
	public int getHora() {
		return hora;
	}
	public void setHora(int hora) {
		this.hora = hora;
	}
	public int getModuloID() {
		return moduloID;
	}
	public void setModuloID(int moduloID) {
		this.moduloID = moduloID;
	}
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
}
