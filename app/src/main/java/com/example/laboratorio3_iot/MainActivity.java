package com.example.laboratorio3_iot;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laboratorio3_iot.databinding.ActivityMainBinding;
import com.example.laboratorio3_iot.dto.Pelicula;
import com.example.laboratorio3_iot.services.PeliculaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EditText editTextImdbId;
    private Button buttonBuscar;

    private Retrofit retrofit;
    private PeliculaService peliculaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (isConnectedToInternet()) {
            showToast("Success Toast: Conexión a Internet establecida");
        } else {
            showToast("Error Toast: No hay conexión a Internet");
        }

        binding.button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ContadorPrimosActivity.class);
            startActivity(intent);
        });

        editTextImdbId = findViewById(R.id.editTextText);
        buttonBuscar = findViewById(R.id.button2);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        peliculaService = retrofit.create(PeliculaService.class);

        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imdbId = editTextImdbId.getText().toString();
                buscarPelicula(imdbId);
            }
        });
    }

    private void buscarPelicula(String imdbId) {
        Call<Pelicula> call = peliculaService.getPeliculaPorImdbId("bf81d461", imdbId);
        call.enqueue(new Callback<Pelicula>() {
            @Override
            public void onResponse(Call<Pelicula> call, Response<Pelicula> response) {
                if (response.isSuccessful()) {
                    Pelicula pelicula = response.body();
                    if (pelicula != null) {
                        mostrarPelicula(pelicula);
                    } else {
                        Toast.makeText(MainActivity.this, "No se encontró la película", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener la película", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pelicula> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mostrarPelicula(Pelicula pelicula) {
        if (pelicula != null && pelicula.getTitle() != null) {
            Intent intent = new Intent(MainActivity.this, ObtenerPeliculaActivity.class);
            intent.putExtra("pelicula", pelicula);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "id de la pelicula no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}