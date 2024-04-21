package com.example.laboratorio3_iot.services;

import com.example.laboratorio3_iot.dto.Pelicula;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PeliculaService {

    @GET("/")
    Call<Pelicula> getPeliculaPorImdbId(@Query("apikey") String apiKey, @Query("i") String imdbId);
}
