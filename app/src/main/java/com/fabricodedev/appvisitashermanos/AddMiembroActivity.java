package com.fabricodedev.appvisitashermanos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.UUID;
import androidx.appcompat.app.AppCompatActivity;

import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

public class AddMiembroActivity extends AppCompatActivity {

    private EditText etNombre, etDireccion, etTelefono, etFotoUrl;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_miembro);

        // 1. Referencias a vistas
        etNombre = findViewById(R.id.et_add_nombre);
        etDireccion = findViewById(R.id.et_add_direccion);
        etTelefono = findViewById(R.id.et_add_telefono);
        etFotoUrl = findViewById(R.id.et_foto_url);
        btnGuardar = findViewById(R.id.btn_guardar_nuevo);

        // 2. Configurar Listener
        btnGuardar.setOnClickListener(v -> guardarNuevoMiembro());
    }

    private void guardarNuevoMiembro() {
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String fotoUrl = etFotoUrl.getText().toString().trim();

        // Validación básica
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }
// 1. Generar un ID único para Firestore
        String newId = UUID.randomUUID().toString();

        // 2. Crear el objeto Miembro
        // El constructor de Miembro debe ser compatible con: (id, nombre, direccion, telefono)
        Miembro nuevoMiembro = new Miembro(newId, nombre, direccion, telefono);
        if (!fotoUrl.isEmpty()) {
            nuevoMiembro.setFotoUrl(fotoUrl);
        }
        // Inicializar campos de Firestore
        // Esto asegura que el objeto tenga las listas y campos necesarios para Firestore
        nuevoMiembro.setUltimaVisita("N/A");
        nuevoMiembro.setEstadoAnimico("Verde");
        // Si la lista de historialVisitas es null, inicialízala en el constructor de Miembro.

        // 3. ⭐ LLAMADA CORREGIDA: Pasar el objeto Miembro completo
        MiembrosManager.getInstance().addMiembro(nuevoMiembro);

        Toast.makeText(this, nombre + " ha sido añadido a la congregación (Firestore).", Toast.LENGTH_LONG).show();
        finish();
    }
}