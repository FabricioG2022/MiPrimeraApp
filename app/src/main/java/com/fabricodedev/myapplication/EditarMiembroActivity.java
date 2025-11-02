package com.fabricodedev.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fabricodedev.myapplication.models.Miembro;
import com.fabricodedev.myapplication.utils.UserManager;

public class EditarMiembroActivity extends AppCompatActivity {

    private String miembroId;
    private Miembro miembroActual;

    private EditText etNombre, etDireccion, etTelefono;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_miembro);

        // 1. Obtener el ID del Intent
        miembroId = getIntent().getStringExtra("MIEMBRO_ID");
        if (miembroId == null) {
            Toast.makeText(this, "Error: Miembro no identificado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Referencias a vistas
        etNombre = findViewById(R.id.et_editar_nombre);
        etDireccion = findViewById(R.id.et_editar_direccion);
        etTelefono = findViewById(R.id.et_editar_telefono);
        btnGuardar = findViewById(R.id.btn_guardar_edicion);

        // 3. Cargar datos actuales del miembro en los EditText
        cargarDatosMiembro();

        // 4. Configurar Listener
        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatosMiembro() {
        miembroActual = UserManager.getInstance().getMiembroById(miembroId);

        if (miembroActual != null) {
            etNombre.setText(miembroActual.getNombre());
            etDireccion.setText(miembroActual.getDireccion());
            etTelefono.setText(miembroActual.getTelefono());
        } else {
            Toast.makeText(this, "Miembro no encontrado para edición.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void guardarCambios() {
        String nuevoNombre = etNombre.getText().toString().trim();
        String nuevaDireccion = etDireccion.getText().toString().trim();
        String nuevoTelefono = etTelefono.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_LONG).show();
            return;
        }

        // Llamar al método de actualización del Singleton
        boolean success = UserManager.getInstance().updateMiembroData(
                miembroId,
                nuevoNombre,
                nuevaDireccion,
                nuevoTelefono
        );

        if (success) {
            Toast.makeText(this, "Datos de " + nuevoNombre + " actualizados con éxito.", Toast.LENGTH_LONG).show();
            // Cierra la actividad para volver a DetalleMiembroActivity (que recargará los datos en onResume)
            finish();
        } else {
            Toast.makeText(this, "Error al guardar los cambios.", Toast.LENGTH_LONG).show();
        }
    }
}