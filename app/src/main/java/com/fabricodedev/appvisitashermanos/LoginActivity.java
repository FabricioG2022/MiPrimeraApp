package com.fabricodedev.appvisitashermanos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.fabricodedev.appvisitashermanos.utils.UserManager;

public class LoginActivity extends AppCompatActivity {
    private EditText etNombreUsuario;
    private EditText etContrasena;
    private TextView registroLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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
                handleLogin();
            }
        });

        // Lógica del enlace de registro
        registroLink = findViewById(R.id.registro_link);
        registroLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String username = etNombreUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu usuario y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamar al Singleton para verificar las credenciales
        boolean success = UserManager.getInstance().login(username, password);

        if (success) {
            Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MiembrosActivity.class);
            // ⭐ PASAR EL NOMBRE DE USUARIO USANDO putExtra
            intent.putExtra("EXTRA_USERNAME", username);
            startActivity(intent);
            finish(); // Cierra el Login
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos.", Toast.LENGTH_LONG).show();
        }
    }
}