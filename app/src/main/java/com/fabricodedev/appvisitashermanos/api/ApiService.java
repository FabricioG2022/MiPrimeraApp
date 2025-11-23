package com.fabricodedev.appvisitashermanos.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ApiService {

    // ⭐ BASE_URL: https://bible-api.com/

    // {referencia} será el versículo, ej: "john 3:16"
    @GET("{referencia}")
    Call<VersiculoDiario> getDailyVerse(
            @Path("referencia") String referencia
            //@Query("translation") String version // ⭐ NUEVO: Parámetro para la traducción
    );
}