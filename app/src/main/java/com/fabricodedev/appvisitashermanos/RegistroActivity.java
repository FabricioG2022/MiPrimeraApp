package com.fabricodedev.appvisitashermanos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importa el UserManager y User (Asegúrate de que la ruta sea correcta)
import com.fabricodedev.appvisitashermanos.utils.UserManager;

public class RegistroActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etNombreUsuario;
    private EditText etContrasena;
    private EditText etContrasena2; // Para confirmar
    private Button btnRegistro;
    private TextView inicioLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Obtener referencias de los EditText
        etEmail = findViewById(R.id.et_nombre_usuario); // NombreUsuario en XML parece ser el Email
        etNombreUsuario = findViewById(R.id.et_email);   // Email en XML parece ser el Nombre de Usuario
        etContrasena = findViewById(R.id.et_contrasenia);
        etContrasena2 = findViewById(R.id.et_contrasenia2);
        btnRegistro = findViewById(R.id.btn_register_button);

        // 2. Lógica del botón de Registro
        btnRegistro.setOnClickListener(v -> handleRegistration());

        // 3. Lógica del enlace de inicio de sesión
        inicioLink = findViewById(R.id.tv_inicia_sesion_aqui);
        inicioLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        String username = etNombreUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString();
        String passwordConfirm = etContrasena2.getText().toString();

        // Validaciones
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamar al Singleton para registrar
        boolean success = UserManager.getInstance().registerUser(email, username, password);

        if (success) {
            Toast.makeText(this, "Registro exitoso. ¡Inicia sesión!", Toast.LENGTH_LONG).show();
            // Ir al login o simplemente terminar la actividad
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            // Esto asegura que el Login se muestre limpio
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "El nombre de usuario '" + username + "' ya está registrado.", Toast.LENGTH_LONG).show();
        }
    }
}