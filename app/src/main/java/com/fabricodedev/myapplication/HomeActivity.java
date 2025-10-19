package com.fabricodedev.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBienvenida = findViewById(R.id.tv_bienvenida);
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);

        // 1. OBTENER EL NOMBRE DE USUARIO del Intent
        String username = getIntent().getStringExtra("EXTRA_USERNAME");

        if (username != null && !username.isEmpty()) {
            tvBienvenida.setText("¡Bienvenido, " + username + "!");
        } else {
            tvBienvenida.setText("¡Bienvenido!");
        }

        // 2. Lógica del botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(v -> {
            // Regresar al Login y limpiar todas las actividades anteriores
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);

            // Estas flags aseguran que el usuario no pueda volver a Home con el botón 'Atrás'
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish(); // Finaliza HomeActivity
        });
    }
}