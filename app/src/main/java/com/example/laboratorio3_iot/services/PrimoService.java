package com.example.laboratorio3_iot.services;

import com.example.laboratorio3_iot.dto.NumeroPrimo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PrimoService {
    @GET("primeNumbers?len=999&order=1")
    Call<List<NumeroPrimo>> getNumeroPrimo();
}
