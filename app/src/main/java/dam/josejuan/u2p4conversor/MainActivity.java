package dam.josejuan.u2p4conversor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LogsConversor";

    // TODO: Activ 1.1 Factor de conversión: 1 pulgada = 2.54 cm
    private static final double CM_POR_PULGADA = 2.54;

    private TextView tvInfo;
    private EditText etValor;
    private SwitchCompat swInvertir;
    private TextView tvResultado;
    private TextView tvError;

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

    private void setUI() {
        tvInfo = findViewById(R.id.tv_info);
        etValor = findViewById(R.id.et_valor);
        swInvertir = findViewById(R.id.sw_invertir);
        tvResultado = findViewById(R.id.tv_resultado);
        tvError = findViewById(R.id.tv_error);
        Button btnConvertir = findViewById(R.id.btn_convertir);

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
    }

    // TODO: Activ 1.1 Título y pista del campo según el sentido elegido
    private void actualizarEtiquetas() {
        boolean invertido = swInvertir.isChecked();
        int origen = invertido ? R.string.unidadCentimetros : R.string.unidadPulgadas;
        int destino = invertido ? R.string.unidadPulgadas : R.string.unidadCentimetros;
        tvInfo.setText(getString(R.string.tituloConversor, getString(origen), getString(destino)));
        etValor.setHint(getString(R.string.hintValor, getString(origen)));
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

    // TODO: Activ 1.1 y 1.2 Conversión en ambos sentidos y resultado con dos decimales
    // TODO: Activ 2.1 Sólo se convierten valores >= 1; en otro caso se lanza una excepción que
    //  captura el único try/catch de setUI()
    private String convertir(String texto) throws Exception {
        if (texto.trim().isEmpty()) {
            throw new Exception(getString(R.string.errorVacio));
        }
        double valor = Double.parseDouble(texto.trim().replace(',', '.'));
        if (valor < 1) {
            throw new Exception(getString(R.string.errorMinimo));
        }
        boolean invertido = swInvertir.isChecked();
        double resultado = invertido ? valor / CM_POR_PULGADA : valor * CM_POR_PULGADA;
        int unidadDestino = invertido ? R.string.unidadPulgadas : R.string.unidadCentimetros;
        return getString(R.string.resultadoFormato, resultado, getString(unidadDestino));
    }
}
