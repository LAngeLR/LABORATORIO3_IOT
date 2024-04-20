package com.example.laboratorio3_iot.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.laboratorio3_iot.dto.NumeroPrimo;
import com.example.laboratorio3_iot.services.PrimoService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrimoWorker extends Worker {

    public PrimoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PrimoService primoService = RetrofitClient.getRetrofitInstance().create(PrimoService.class);
        Call<List<NumeroPrimo>> call = primoService.getNumeroPrimo();
        call.enqueue(new Callback<List<NumeroPrimo>>() {
            @Override
            public void onResponse(Call<List<NumeroPrimo>> call, Response<List<NumeroPrimo>> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<NumeroPrimo>> call, Throwable t) {

            }
        });
        return Result.success();
    }

    private List<NumeroPrimo> parseJson(String json) throws JSONException {
        List<NumeroPrimo> numerosPrimos = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int number = jsonObject.getInt("number");
            int order = jsonObject.getInt("order");
            NumeroPrimo numeroPrimo = new NumeroPrimo(number, order);
            numerosPrimos.add(numeroPrimo);
        }
        return numerosPrimos;
    }
}
