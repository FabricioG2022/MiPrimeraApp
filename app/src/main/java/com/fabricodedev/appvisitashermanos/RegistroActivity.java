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
// ⭐ Importaciones de Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Importa el UserManager y User (Asegúrate de que la ruta sea correcta)
//import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

public class RegistroActivity extends AppCompatActivity {

    private EditText etEmail;
    //private EditText etNombreUsuario;
    private EditText etContrasena;
    private EditText etContrasena2; // Para confirmar
    private Button btnRegistro;
    private TextView inicioLink;
    // Instancia de Firebase Auth
    private FirebaseAuth mAuth;

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
        // 1. Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // 2. Obtener referencias de los EditText
        etEmail = findViewById(R.id.et_email);
        //etNombreUsuario = findViewById(R.id.et_email);
        etContrasena = findViewById(R.id.et_contrasenia);
        etContrasena2 = findViewById(R.id.et_contrasenia2);
        btnRegistro = findViewById(R.id.btn_register_button);

        // 3. Lógica del botón de Registro
        btnRegistro.setOnClickListener(v -> handleRegistration());

        // 4. Lógica del enlace de inicio de sesión
        inicioLink = findViewById(R.id.tv_inicia_sesion_aqui);
        inicioLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        //String username = etNombreUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString();
        String passwordConfirm = etContrasena2.getText().toString();

        //1. Validaciones
        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 2. ⭐ REEMPLAZO DE LÓGICA CON FIREBASE AUTH ⭐
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Registro de " + user.getEmail() + " exitoso. ¡Inicia sesión!", Toast.LENGTH_LONG).show();

                        // Navegar a Login
                        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // El registro falló. Firebase proporciona el motivo.
                        String error = "Error: ";
                        if (task.getException() != null) {
                            error += task.getException().getLocalizedMessage();
                        } else {
                            error += "Verifica el formato del email y la seguridad de la contraseña.";
                        }
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    }
                });
        /*// Llamar al Singleton para registrar
        boolean success = MiembrosManager.getInstance().registerUser(email, username, password);

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
        }*/
    }
}