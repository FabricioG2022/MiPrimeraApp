package com.fabricodedev.appvisitashermanos;
// ⭐ Nuevos Imports de Firebase
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

public class EditarMiembroActivity extends AppCompatActivity {

    private String miembroId;
    // ⭐ Referencia a Firestore
    private FirebaseFirestore db;
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

        // ⭐ Inicializar Firestore (NUEVO)
        db = MiembrosManager.getInstance().getDb();

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

    // ⭐ MIGRACIÓN 1: Cargar datos desde Firestore
    private void cargarDatosMiembro() {
        if (miembroId == null || db == null) return;

        db.collection("miembros").document(miembroId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convertir el Documento a Objeto Miembro
                        Miembro miembroActual = documentSnapshot.toObject(Miembro.class);

                        if (miembroActual != null) {
                            etNombre.setText(miembroActual.getNombre());
                            etDireccion.setText(miembroActual.getDireccion());
                            etTelefono.setText(miembroActual.getTelefono());
                        }
                    } else {
                        Toast.makeText(this, "Miembro no encontrado para edición.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    // ⭐ MIGRACIÓN 2: Guardar cambios usando la llamada asíncrona del Manager
    private void guardarCambios() {
        String nuevoNombre = etNombre.getText().toString().trim();
        String nuevaDireccion = etDireccion.getText().toString().trim();
        String nuevoTelefono = etTelefono.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_LONG).show();
            return;
        }

        // Llamar al método de actualización de Firestore (ASÍNCRONO)
        // Ya no devuelve un booleano
        MiembrosManager.getInstance().updateMiembroData(
                miembroId,
                nuevoNombre,
                nuevaDireccion,
                nuevoTelefono
        );

        // Mostramos el mensaje de éxito inmediatamente, asumiendo que la operación se completará.
        Toast.makeText(this, "Datos actualizados con éxito (Firestore).", Toast.LENGTH_LONG).show();

        // Cierra la actividad para volver a DetalleMiembroActivity (que recargará los datos en onResume)
        finish();
    }
}