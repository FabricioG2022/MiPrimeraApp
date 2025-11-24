package com.fabricodedev.appvisitashermanos.api;

import com.google.gson.annotations.SerializedName;

public class VersiculoDiario {
    @SerializedName("text")
    private String texto;

    public String getTexto() {
        return texto;
    }
}
