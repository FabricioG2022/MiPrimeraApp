package com.fabricodedev.appvisitashermanos.api;

import com.google.gson.annotations.SerializedName;

public class VersiculoDiario {

    @SerializedName("text")
    private String texto; // El texto del versículo

    @SerializedName("reference")
    private String referencia; // La referencia, ej: "Juan 3:16"

    @SerializedName("translation_name")
    private String traduccion; // Nombre de la versión (opcional)

    // Constructor vacío
    public VersiculoDiario() {}

    // Getters
    public String getTexto() {
        return texto;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getTraduccion() {
        return traduccion;
    }
}