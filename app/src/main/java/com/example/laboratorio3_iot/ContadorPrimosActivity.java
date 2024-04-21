package com.example.laboratorio3_iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.laboratorio3_iot.databinding.ActivityContadorPrimoBinding;
import com.example.laboratorio3_iot.dto.NumeroPrimo;
import com.example.laboratorio3_iot.services.PrimoService;
import com.example.laboratorio3_iot.workers.PrimoWorker;
import com.example.laboratorio3_iot.workers.RetrofitClient;

import java.util.List;

import retrofit2.*;


public class ContadorPrimosActivity extends AppCompatActivity {

    private EditText ordenEditText;
    private TextView primoTextView;
    private TextView estadoTextView;
    private boolean isCounting = false;
    private boolean isAscending = true;
    private boolean isDescending = false;
    private Button iniciarDetenerButton;
    private Button cambiarDireccionButton;
    private int contador = 0;
    private List<NumeroPrimo> numerosPrimos;
    private int ultimoIndicePausa = 0;
    private Thread contadorThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador_primo);

        ordenEditText = findViewById(R.id.editTextText2);
        primoTextView = findViewById(R.id.textView4);
        iniciarDetenerButton = findViewById(R.id.button3);
        iniciarDetenerButton.setText("PAUSAR");
        cambiarDireccionButton = findViewById(R.id.button4);
        cambiarDireccionButton.setText("DESCENDER");
        estadoTextView = findViewById(R.id.textView7);

        Button buscarButton = findViewById(R.id.buscarOrden);
        buscarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarNumeroPrimo();
                obtenerNumerosPrimos();
            }
        });
        iniciarDetenerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCounting) {
                    detenerContador();
                } else {
                    reiniciarContador();
                }
            }
        });
        cambiarDireccionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarDireccionConteo();
            }
        });

        cambiarDireccionButton.setVisibility(View.GONE);
        estadoTextView.setText("");
    }
    private void obtenerNumerosPrimos() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(PrimoWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState() == androidx.work.WorkInfo.State.SUCCEEDED) {
                        PrimoService primoService = RetrofitClient.getRetrofitInstance().create(PrimoService.class);
                        Call<List<NumeroPrimo>> call = primoService.getNumeroPrimo();
                        call.enqueue(new Callback<List<NumeroPrimo>>() {
                            @Override
                            public void onResponse(Call<List<NumeroPrimo>> call, Response<List<NumeroPrimo>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    numerosPrimos = response.body();
                                    iniciarContador();
                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<List<NumeroPrimo>> call, Throwable t) {

                            }
                        });
                    }
                });
    }
    public void buscarNumeroPrimo() {
        int orden = Integer.parseInt(ordenEditText.getText().toString());
        PrimoService primoService = RetrofitClient.getRetrofitInstance().create(PrimoService.class);
        Call<List<NumeroPrimo>> call = primoService.getNumeroPrimo();
        call.enqueue(new Callback<List<NumeroPrimo>>() {
            @Override
            public void onResponse(Call<List<NumeroPrimo>> call, Response<List<NumeroPrimo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NumeroPrimo> numerosPrimos = response.body();
                    int primoEncontrado = encontrarNumeroPrimo(numerosPrimos, orden);
                    primoTextView.setText(String.valueOf(primoEncontrado));
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<NumeroPrimo>> call, Throwable t) {

            }
        });
    }
    private int encontrarNumeroPrimo(List<NumeroPrimo> numerosPrimos, int orden) {
        for (NumeroPrimo numeroPrimo : numerosPrimos) {
            if (numeroPrimo.getOrder() == orden) {
                return numeroPrimo.getNumber();
            }
        }
        return -1;
    }
    private void iniciarContador() {
        isCounting = true;
        iniciarDetenerButton.setText("PAUSAR");
        cambiarDireccionButton.setVisibility(View.VISIBLE);
        contador = encontrarIndicePrimo(numerosPrimos, Integer.parseInt(ordenEditText.getText().toString()));
        if (contador != -1) {
            contadorThread = new Thread(() -> {
                while (isCounting && contador >= 0 && contador < numerosPrimos.size()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(() -> {
                            primoTextView.setText(String.valueOf(numerosPrimos.get(contador).getNumber()));
                        });
                        if (isDescending) {
                            contador--;
                        } else {
                            contador++;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
            contadorThread.start();
        } else {
        }
    }
    private int encontrarIndicePrimo(List<NumeroPrimo> numerosPrimos, int orden) {
        for (int i = 0; i < numerosPrimos.size(); i++) {
            if (numerosPrimos.get(i).getOrder() == orden) {
                return i;
            }
        }
        return -1;
    }
    private void detenerContador() {
        isCounting = false;
        ultimoIndicePausa = contador;
        iniciarDetenerButton.setText("REINICIAR");
        cambiarDireccionButton.setVisibility(View.GONE);
        estadoTextView.setText("El contador actualmente está en pausa");
        if (contadorThread != null) {
            contadorThread.interrupt();
        }
        WorkManager.getInstance(this).cancelAllWork();
    }
    public void cambiarDireccionConteo() {
        isDescending = !isDescending;
        Button cambiarDireccionButton = findViewById(R.id.button4);
        if (isDescending) {
            cambiarDireccionButton.setText("ASCENDER");
            estadoTextView.setText("El contador actualmente está descendiendo");
        } else {
            cambiarDireccionButton.setText("DESCENDER");
            estadoTextView.setText("El contador actualmente está ascendiendo");
        }
    }
    private void reiniciarContador() {
        isCounting = true;
        iniciarDetenerButton.setText("PAUSAR");
        cambiarDireccionButton.setVisibility(View.VISIBLE);
        if (contador != -1 && contador < numerosPrimos.size()) {
            contador = ultimoIndicePausa;
            if (contadorThread != null) {
                contadorThread.interrupt();
            }
            contadorThread = new Thread(() -> {
                while (isCounting && contador >= 0 && contador < numerosPrimos.size()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(() -> {
                            primoTextView.setText(String.valueOf(numerosPrimos.get(contador).getNumber()));
                        });
                        if (isDescending) {
                            contador--;
                        } else {
                            contador++;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
            contadorThread.start();
            if (isDescending) {
                estadoTextView.setText("El contador actualmente está descendiendo");
            } else {
                estadoTextView.setText("El contador actualmente está ascendiendo");
            }
        } else {
            iniciarContador();
        }
    }
}
