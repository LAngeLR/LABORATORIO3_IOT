package com.example.laboratorio3_iot;

import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laboratorio3_iot.dto.Pelicula;
import com.example.laboratorio3_iot.dto.Ratings;

import java.util.List;

public class ObtenerPeliculaActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private Button botonRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_pelicula);

        Pelicula pelicula = (Pelicula) getIntent().getSerializableExtra("pelicula");
        if (pelicula != null) {
            TextView textViewTitle = findViewById(R.id.textView9);
            TextView textViewPlot = findViewById(R.id.textView20);
            TextView textViewDirector = findViewById(R.id.textView19);
            TextView textViewActores = findViewById(R.id.textView15);
            TextView textViewFecha = findViewById(R.id.textView18);
            TextView textViewGeneros = findViewById(R.id.textView17);
            TextView textViewEscritores = findViewById(R.id.textView16);

            TextView textViewFuente1 = findViewById(R.id.textView25);
            TextView textViewValor1 = findViewById(R.id.textView24);
            TextView textViewFuente2 = findViewById(R.id.textView26);
            TextView textViewValor2 = findViewById(R.id.textView23);
            TextView textViewValor3 = findViewById(R.id.textView27);

            checkBox = findViewById(R.id.checkBox);
            botonRegresar = findViewById(R.id.button5);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    botonRegresar.setVisibility(View.VISIBLE);
                } else {
                    botonRegresar.setVisibility(View.GONE);
                }
            });

            botonRegresar.setOnClickListener(v -> {
                finish();
            });

            textViewTitle.setText(pelicula.getTitle());
            textViewPlot.setText(pelicula.getPlot());
            textViewDirector.setText(pelicula.getDirector());
            textViewActores.setText(pelicula.getActors());
            textViewFecha.setText(pelicula.getReleased());
            textViewGeneros.setText(pelicula.getGenre());
            textViewEscritores.setText(pelicula.getWriter());

            List<Ratings> ratings = pelicula.getRatings();
            if (ratings != null && !ratings.isEmpty()) {
                for (int i = 0; i < ratings.size(); i++) {
                    Ratings rating = ratings.get(i);
                    String source = rating.getSource();
                    String value = rating.getValue();
                    if (i == 0) {
                        textViewFuente1.setText(source);
                        textViewValor1.setText(value);
                    } else if (i == 1) {
                        textViewFuente2.setText(source);
                        textViewValor2.setText(value);
                    } else if (i == 2) {
                        textViewValor3.setText(value);
                    }
                }
            }
        }
    }
}
