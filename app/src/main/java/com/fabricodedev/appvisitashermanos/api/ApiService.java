package com.fabricodedev.appvisitashermanos.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ApiService {

    @GET("bible/content/{version}.html.json")
    Call<VersiculoDiario> getDailyVerse(
            @Path("version") String version,
            @Query("passage") String passage,
            @Query("key") String apiKey,
            @Query("style") String style
    );
}
