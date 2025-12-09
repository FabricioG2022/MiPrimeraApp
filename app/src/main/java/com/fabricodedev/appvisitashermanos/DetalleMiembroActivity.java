package com.fabricodedev.appvisitashermanos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabricodedev.appvisitashermanos.adapters.VisitaAdapter;
import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

// ⭐ Importaciones de Firestore
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class DetalleMiembroActivity extends AppCompatActivity {

    private String miembroId;
    private Miembro miembroActual;
    private Toolbar toolbar;
    private TextView tvNombre, tvEstado, tvTelefono, tvDireccion;
    private RecyclerView rvHistorial;
    private Button btnRegistrarVisita, btnEditarDatos;
    // ⭐ Referencia a Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_miembro);

        // ⭐ CONFIGURACIÓN DE LA TOOLBAR
        toolbar = findViewById(R.id.toolbar_detalle);
        setSupportActionBar(toolbar);
        // Opcional: Mostrar el botón de atrás
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalle del Miembro");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 1. Obtener el ID del Intent que viene de MiembrosActivity
        miembroId = getIntent().getStringExtra("MIEMBRO_ID");
        if (miembroId == null) {
            Toast.makeText(this, "Error: Miembro no encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ⭐ 2. Inicializar Firestore
        db = MiembrosManager.getInstance().getDb();

        // 3. Referencias a vistas
        tvNombre = findViewById(R.id.tv_detalle_nombre);
        tvEstado = findViewById(R.id.tv_detalle_estado);
        tvTelefono = findViewById(R.id.tv_detalle_telefono);
        tvDireccion = findViewById(R.id.tv_detalle_direccion);
        rvHistorial = findViewById(R.id.rv_historial_visitas);
        btnRegistrarVisita = findViewById(R.id.btn_registrar_visita);
        btnEditarDatos = findViewById(R.id.btn_editar_datos);

        // 4. Configurar RecyclerView para el historial
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // 5. Configurar listeners de botones
        btnEditarDatos.setOnClickListener(v -> handleEditarDatos());
        btnRegistrarVisita.setOnClickListener(v -> handleRegistrarVisita());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cada vez que la actividad vuelve al primer plano
        loadMiembroData();
    }

    // ⭐ MÉTODO MIGRADO 1: Carga de datos asíncrona desde Firestore
    private void loadMiembroData() {
        if (miembroId == null || db == null) return;

        db.collection("miembros").document(miembroId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            miembroActual = document.toObject(Miembro.class);

                            if (miembroActual != null) {
                                // Actualizar UI con datos del Miembro
                                tvNombre.setText(miembroActual.getNombre());
                                tvEstado.setText("Estado: " + miembroActual.getEstadoAnimico() + " | Última Visita: " + miembroActual.getUltimaVisita());
                                tvTelefono.setText("Teléfono: " + miembroActual.getTelefono());
                                tvDireccion.setText("Dirección: " + miembroActual.getDireccion());

                                // Conectar el historial
                                // Nota: Asegúrate de que getHistorialVisitas() devuelva una lista inicializada (no null)
                                VisitaAdapter adapter = new VisitaAdapter(miembroActual.getHistorialVisitas());
                                rvHistorial.setAdapter(adapter);
                                rvHistorial.scrollToPosition(adapter.getItemCount() > 0 ? adapter.getItemCount() - 1 : 0);

                                // Actualizar el título de la Toolbar
                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().setTitle(miembroActual.getNombre());
                                }
                            }
                        } else {
                            Toast.makeText(this, "Error: Miembro no encontrado en la base de datos.", Toast.LENGTH_SHORT).show();
                            // finish(); // Opcional: Cerrar si el miembro fue eliminado
                        }
                    } else {
                        Toast.makeText(this, "Error de conexión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleEditarDatos() {
        Intent intent = new Intent(this, EditarMiembroActivity.class);
        intent.putExtra("MIEMBRO_ID", miembroId);
        startActivity(intent);
    }

    private void handleRegistrarVisita() {
        Intent intent = new Intent(this, RegistrarVisitaActivity.class);
        intent.putExtra("MIEMBRO_ID", miembroId);
        startActivity(intent);
    }
    // 1. Inflar el menú en la ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_miembro, menu);
        return true;
    }

    // 2. Manejar la selección del ítem del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_eliminar) {
            confirmarEliminacion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 3. Crear la función de eliminación y confirmación
    private void confirmarEliminacion() {
        // Es crucial pedir confirmación antes de una acción destructiva
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Está seguro de que desea eliminar a " + tvNombre.getText().toString() + " de la congregación? Esta acción es irreversible.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Llama al método de eliminación
                    eliminarMiembro(miembroId);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ⭐ MÉTODO MIGRADO 2: Eliminación asíncrona.
    private void eliminarMiembro(String id) {
        // Llamar al método de eliminación de Firestore
        db.collection("miembros").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Miembro eliminado con éxito.", Toast.LENGTH_SHORT).show();
                    // Finaliza la actividad para volver a MiembrosActivity
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}