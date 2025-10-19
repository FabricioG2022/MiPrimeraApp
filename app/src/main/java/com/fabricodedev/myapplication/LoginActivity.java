package com.fabricodedev.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private EditText etNombreUsuario;
    private EditText etContrasena;
    private TextView registroLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Ajuste de los bordes de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener las referencias de los EditText por su ID
        etNombreUsuario = findViewById(R.id.et_nombre_usuario);
        etContrasena = findViewById(R.id.et_contrasenia);

        // Lógica del botón de login
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreUsuario = etNombreUsuario.getText().toString();
                String contrasena = etContrasena.getText().toString();
                // Aquí va la lógica de autenticación (ej. ir a HomeActivity)
            }
        });

        // Lógica del enlace de registro
        registroLink = findViewById(R.id.registro_link);
        registroLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

    }
}