package modelo;

import com.google.gson.annotations.SerializedName;

public class Centro {

    // En tu JSON es "DTERRC" (Castellano) o "DTERRE" (Euskera)
    @SerializedName("DTERRC")
    private String territorio;

    // Este estaba bien
    @SerializedName("DMUNIC")
    private String municipio;

    // ¡AQUÍ ESTABA EL ERROR! En el JSON es "NOM"
    @SerializedName("NOM")
    private String nombre;
    
    // En el JSON no hay "TITULA", hay "DTITUC" (Descripción Titularidad Castellano)
    @SerializedName("DTITUC")
    private String tipo; 

    public String getTerritorio() { return territorio; }
    public String getMunicipio() { return municipio; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }

    @Override
    public String toString() {
        return nombre; 
    }
}