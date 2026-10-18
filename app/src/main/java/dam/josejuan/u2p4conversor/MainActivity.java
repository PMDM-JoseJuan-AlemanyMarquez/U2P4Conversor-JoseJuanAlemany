package dam.josejuan.u2p4conversor;

import android.os.Bundle;
import android.util.Log;
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
    }

    private void setUI() {
        tvInfo = findViewById(R.id.tv_info);
        etValor = findViewById(R.id.et_valor);
        swInvertir = findViewById(R.id.sw_invertir);
        tvResultado = findViewById(R.id.tv_resultado);
        Button btnConvertir = findViewById(R.id.btn_convertir);

        actualizarEtiquetas();

        btnConvertir.setOnClickListener(view -> {
            try {
                tvResultado.setText(convertir(etValor.getText().toString()));
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e.getMessage()));
            }
        });

        // TODO: Activ 1.1 Al cambiar el sentido se actualizan los textos y se borra el resultado anterior
        swInvertir.setOnCheckedChangeListener((boton, invertido) -> {
            actualizarEtiquetas();
            tvResultado.setText("");
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

    // TODO: Activ 1.1 y 1.2 Conversión en ambos sentidos y resultado con dos decimales
    private String convertir(String texto) {
        double valor = Double.parseDouble(texto.replace(',', '.'));
        boolean invertido = swInvertir.isChecked();
        double resultado = invertido ? valor / CM_POR_PULGADA : valor * CM_POR_PULGADA;
        int unidadDestino = invertido ? R.string.unidadPulgadas : R.string.unidadCentimetros;
        return getString(R.string.resultadoFormato, resultado, getString(unidadDestino));
    }
}
