package dam.josejuan.u2p4conversor;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** TODO: Activ 5.2 Pantalla de ayuda: explica cómo usar la aplicación y los mensajes de error. */
public class HelpActivity extends AppCompatActivity {

    private static final String TAG = "LogsConversor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.help_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnVolver = findViewById(R.id.btn_volver);
        btnVolver.setOnClickListener(view -> {
            Log.i(TAG, "Botón pulsado: Volver (ayuda)");
            finish();
        });
        Log.d(TAG, "HelpActivity onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HelpActivity onDestroy");
    }
}
