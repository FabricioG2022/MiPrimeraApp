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
// ⭐ Importaciones de Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

public class LoginActivity extends AppCompatActivity {
    private EditText etNombreUsuario;
    private EditText etContrasena;
    private TextView registroLink;
    // Instancia de Firebase Auth
    private FirebaseAuth mAuth;

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

        // 1. Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
        String email = etNombreUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu email y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }
        // ⭐ LÓGICA CON FIREBASE AUTH ⭐
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login exitoso
                        Toast.makeText(LoginActivity.this, "¡Bienvenido! Iniciando sesión...", Toast.LENGTH_SHORT).show();
                        navigateToMiembros();
                    } else {
                        // Fallo en el login (ej. credenciales incorrectas, usuario no existe)
                        String error = "Credenciales incorrectas o usuario no registrado.";
                        if (task.getException() != null) {
                            // Puedes usar getLocalizedMessage() para un error más específico si lo deseas
                            // error = task.getException().getLocalizedMessage();
                        }
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToMiembros() {
        Intent intent = new Intent(this, MiembrosActivity.class);
        // Usa FLAG_ACTIVITY_CLEAR_TASK para evitar que el usuario vuelva a Login/Registro con el botón de atrás
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
        /*// Llamar al Singleton para verificar las credenciales
        boolean success = MiembrosManager.getInstance().login(username, password);

        if (success) {
            Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MiembrosActivity.class);
            // ⭐ PASAR EL NOMBRE DE USUARIO USANDO putExtra
            intent.putExtra("EXTRA_USERNAME", username);
            startActivity(intent);
            finish(); // Cierra el Login
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos.", Toast.LENGTH_LONG).show();
        }*/
}
