package com.fabricodedev.appvisitashermanos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

public class AddMiembroActivity extends AppCompatActivity {

    private EditText etNombre, etDireccion, etTelefono;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_miembro);

        // 1. Referencias a vistas
        etNombre = findViewById(R.id.et_add_nombre);
        etDireccion = findViewById(R.id.et_add_direccion);
        etTelefono = findViewById(R.id.et_add_telefono);
        btnGuardar = findViewById(R.id.btn_guardar_nuevo);

        // 2. Configurar Listener
        btnGuardar.setOnClickListener(v -> guardarNuevoMiembro());
    }

    private void guardarNuevoMiembro() {
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        // Validación básica
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        // Llamar al método de creación del Singleton
        MiembrosManager.getInstance().addMiembro(nombre, direccion, telefono);

        Toast.makeText(this, nombre + " ha sido añadido a la congregación.", Toast.LENGTH_LONG).show();

        // Cerrar la actividad para volver a la lista (MiembrosActivity se recargará en onResume)
        finish();
    }
}