package modelo;

import com.google.gson.annotations.SerializedName;

public class Centro {
    // @SerializedName porque en el JSON las claves son raras
	
    @SerializedName("DTERRE")
    private String territorio;

    @SerializedName("DMUNIC")
    private String municipio;

    @SerializedName("DENCAS")
    private String nombre;
    
    @SerializedName("TITULA")
    private String tipo; // P para Público, C para Privado

    public String getTerritorio() { return territorio; }
    public String getMunicipio() { return municipio; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }

    @Override
    public String toString() {
        return nombre; 
    }
}