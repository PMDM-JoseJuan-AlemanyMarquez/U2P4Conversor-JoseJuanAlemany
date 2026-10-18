package dam.josejuan.u2p4conversor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LogsConversor";

    // Claves para guardar el estado de la pantalla al girar el dispositivo
    private static final String KEY_RESULTADO = "resultado";
    private static final String KEY_ERROR = "error";
    private static final String KEY_ERROR_VISIBLE = "errorVisible";
    private static final String KEY_POSICION = "posicionConversion";

    // TODO: Activ 6 Valoración guardada de forma persistente en SharedPreferences
    private static final String PREFS = "u2p4_prefs";
    private static final String KEY_VALORACION = "valoracion";

    // TODO: Activ 5.1 Conversiones disponibles, cada una válida en doble sentido.
    //  El orden coincide con R.array.conversiones (opciones del Spinner).
    private static final Conversion[] CONVERSIONES = {
            new Conversion(R.string.unidadPulgadas, R.string.unidadCentimetros, 2.54),
            new Conversion(R.string.unidadKilogramos, R.string.unidadLibras, 2.2046226218),
            new Conversion(R.string.unidadKilometros, R.string.unidadMillas, 0.6213711922)
    };

    private TextView tvInfo;
    private Spinner spConversion;
    private SwitchCompat swInvertir;
    private EditText etValor;
    private TextView tvResultado;
    private TextView tvError;
    private TextView tvValoracion;
    private RatingBar ratingBar;

    // Última conversión seleccionada, para borrar los mensajes sólo cuando el usuario cambia de opción
    private int posicionAnterior = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUI();
        Log.d(TAG, "onCreate");
    }

    // TODO: Activ 3.1 Mensajes de Log con los estados del ciclo de vida de la Activity
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        cargarValoracion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        guardarValoracion();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    // TODO: Activ 4.1 Respuesta: al girar el dispositivo Android destruye la Activity (onDestroy) y crea
    //  otra nueva (onCreate) porque cambia la configuración. La interfaz se infla de nuevo desde el
    //  layout y los TextView vuelven a su estado inicial: pierden el texto (un TextView no lo guarda por
    //  defecto) y el TextView de error vuelve a estar oculto. Las variables de la Activity también se pierden.
    //  Los EditText, el Switch y el Spinner con id sí recuperan su estado solos.
    // TODO: Activ 4.2 Solución: guardar el estado en onSaveInstanceState y recuperarlo en onRestoreInstanceState
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_RESULTADO, tvResultado.getText().toString());
        outState.putString(KEY_ERROR, tvError.getText().toString());
        outState.putBoolean(KEY_ERROR_VISIBLE, tvError.getVisibility() == View.VISIBLE);
        outState.putInt(KEY_POSICION, posicionAnterior);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Se restaura después de super: el Switch recuperado limpia los mensajes y aquí se vuelven a poner
        posicionAnterior = savedInstanceState.getInt(KEY_POSICION, 0);
        tvResultado.setText(savedInstanceState.getString(KEY_RESULTADO, ""));
        tvError.setText(savedInstanceState.getString(KEY_ERROR, ""));
        tvError.setVisibility(savedInstanceState.getBoolean(KEY_ERROR_VISIBLE) ? View.VISIBLE : View.GONE);
        Log.d(TAG, "onRestoreInstanceState");
    }

    private void setUI() {
        tvInfo = findViewById(R.id.tv_info);
        spConversion = findViewById(R.id.sp_conversion);
        swInvertir = findViewById(R.id.sw_invertir);
        etValor = findViewById(R.id.et_valor);
        tvResultado = findViewById(R.id.tv_resultado);
        tvError = findViewById(R.id.tv_error);
        tvValoracion = findViewById(R.id.tv_valoracion);
        ratingBar = findViewById(R.id.rating_bar);
        Button btnConvertir = findViewById(R.id.btn_convertir);
        ImageButton ibAyuda = findViewById(R.id.ib_ayuda);

        // TODO: Activ 5.1 El Spinner permite elegir la conversión
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.conversiones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConversion.setAdapter(adapter);
        spConversion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != posicionAnterior) {
                    posicionAnterior = position;
                    limpiarMensajes();
                }
                actualizarEtiquetas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada que hacer
            }
        });

        actualizarEtiquetas();

        btnConvertir.setOnClickListener(view -> {
            // TODO: Activ 3.2 Log del botón pulsado
            Log.i(TAG, "Botón pulsado: Convertir");
            try {
                limpiarMensajes();
                tvResultado.setText(convertir(etValor.getText().toString()));
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e.getMessage()));
                mostrarError(e);
            }
        });

        // TODO: Activ 1.1 Al cambiar el sentido se actualizan los textos y se borra el resultado anterior
        swInvertir.setOnCheckedChangeListener((boton, invertido) -> {
            Log.i(TAG, "Botón pulsado: Invertir sentido (" + (invertido ? "activado" : "desactivado") + ")");
            actualizarEtiquetas();
            limpiarMensajes();
        });

        // TODO: Activ 5.2 El botón de ayuda abre la Activity con las instrucciones (Intent explícito)
        ibAyuda.setOnClickListener(view -> {
            Log.i(TAG, "Botón pulsado: Ayuda");
            startActivity(new Intent(this, HelpActivity.class));
        });
    }

    // TODO: Activ 6 Al mostrarse la Activity: si ya hay valoración guardada se enseña y se bloquea;
    //  si no, el usuario puede valorar (y cambiar la valoración) mientras la Activity siga en primer plano
    private void cargarValoracion() {
        float guardada = getSharedPreferences(PREFS, MODE_PRIVATE).getFloat(KEY_VALORACION, 0f);
        if (guardada > 0f) {
            ratingBar.setRating(guardada);
            ratingBar.setIsIndicator(true);
            tvValoracion.setText(R.string.valoracionGracias);
        } else {
            ratingBar.setIsIndicator(false);
            tvValoracion.setText(R.string.valoracionPide);
        }
    }

    // TODO: Activ 6 Al dejar de verse la Activity (segundo plano o cierre) se guarda la valoración hecha.
    //  Un giro de pantalla no cuenta: la Activity se recrea y el usuario sigue en primer plano.
    private void guardarValoracion() {
        if (isChangingConfigurations() || ratingBar.isIndicator()) {
            return;
        }
        float valoracion = ratingBar.getRating();
        if (valoracion > 0f) {
            getSharedPreferences(PREFS, MODE_PRIVATE)
                    .edit()
                    .putFloat(KEY_VALORACION, valoracion)
                    .apply();
            Log.d(TAG, "Valoración guardada: " + valoracion);
        }
    }

    private Conversion conversionActual() {
        return CONVERSIONES[Math.max(0, spConversion.getSelectedItemPosition())];
    }

    // TODO: Activ 1.1 y 5.1 Título y pista del campo según la conversión y el sentido elegidos
    private void actualizarEtiquetas() {
        Conversion conversion = conversionActual();
        boolean invertido = swInvertir.isChecked();
        String origen = getString(conversion.getOrigen(invertido));
        String destino = getString(conversion.getDestino(invertido));
        tvInfo.setText(getString(R.string.tituloConversor, origen, destino));
        etValor.setHint(getString(R.string.hintValor, origen));
    }

    // TODO: Activ 2.2 Oculta el error y borra el resultado anterior
    private void limpiarMensajes() {
        tvResultado.setText("");
        tvError.setVisibility(View.GONE);
    }

    // TODO: Activ 2.2 Muestra el mensaje de error en el TextView (sólo visible cuando hay error)
    private void mostrarError(Exception e) {
        String mensaje = (e instanceof NumberFormatException)
                ? getString(R.string.errorFormato)
                : e.getMessage();
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
    }

    // TODO: Activ 1.1, 1.2, 5.1 Conversión en ambos sentidos con el resultado a dos decimales
    // TODO: Activ 2.1 Sólo se convierten valores >= 1; en otro caso se lanza una excepción que
    //  captura el único try/catch de setUI(). Lo comparten todas las conversiones.
    private String convertir(String texto) throws Exception {
        if (texto.trim().isEmpty()) {
            throw new Exception(getString(R.string.errorVacio));
        }
        double valor = Double.parseDouble(texto.trim().replace(',', '.'));
        if (valor < 1) {
            throw new Exception(getString(R.string.errorMinimo));
        }
        Conversion conversion = conversionActual();
        boolean invertido = swInvertir.isChecked();
        double resultado = conversion.convertir(valor, invertido);
        return getString(R.string.resultadoFormato, resultado, getString(conversion.getDestino(invertido)));
    }
}
