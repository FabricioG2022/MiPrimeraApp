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

public class DetalleMiembroActivity extends AppCompatActivity {

    private String miembroId;
    private Miembro miembroActual;
    private Toolbar toolbar;
    private TextView tvNombre, tvEstado, tvTelefono, tvDireccion;
    private RecyclerView rvHistorial;
    private Button btnRegistrarVisita, btnEditarDatos;

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

        // 2. Referencias a vistas
        tvNombre = findViewById(R.id.tv_detalle_nombre);
        tvEstado = findViewById(R.id.tv_detalle_estado);
        tvTelefono = findViewById(R.id.tv_detalle_telefono);
        tvDireccion = findViewById(R.id.tv_detalle_direccion);
        rvHistorial = findViewById(R.id.rv_historial_visitas);
        btnRegistrarVisita = findViewById(R.id.btn_registrar_visita);
        btnEditarDatos = findViewById(R.id.btn_editar_datos);

        // 3. Configurar RecyclerView para el historial
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // 4. Configurar listeners de botones
        btnEditarDatos.setOnClickListener(v -> handleEditarDatos());
        btnRegistrarVisita.setOnClickListener(v -> handleRegistrarVisita());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cada vez que la actividad vuelve al primer plano
        loadMiembroData();
    }

    private void loadMiembroData() {
        miembroActual = MiembrosManager.getInstance().getMiembroById(miembroId);

        if (miembroActual != null) {
            // Actualizar UI con datos del Miembro
            tvNombre.setText(miembroActual.getNombre());
            tvEstado.setText("Estado: " + miembroActual.getEstadoEspiritual() + " | Última Visita: " + miembroActual.getUltimaVisita());
            tvTelefono.setText("Teléfono: " + miembroActual.getTelefono());
            tvDireccion.setText("Dirección: " + miembroActual.getDireccion());

            // Conectar el historial
            VisitaAdapter adapter = new VisitaAdapter(miembroActual.getHistorialVisitas());
            rvHistorial.setAdapter(adapter);

            // Asegurar que se muestre el historial más reciente primero (opcional)
            rvHistorial.scrollToPosition(adapter.getItemCount() - 1);
        }
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

    private void eliminarMiembro(String id) {
        boolean eliminado = MiembrosManager.getInstance().deleteMiembro(id);

        if (eliminado) {
            Toast.makeText(this, "Miembro eliminado con éxito.", Toast.LENGTH_SHORT).show();

            // Finaliza la actividad para volver a MiembrosActivity
            finish();
        } else {
            Toast.makeText(this, "Error: No se pudo encontrar el miembro para eliminar.", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}