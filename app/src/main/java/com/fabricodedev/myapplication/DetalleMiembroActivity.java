package com.fabricodedev.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabricodedev.myapplication.adapters.VisitaAdapter;
import com.fabricodedev.myapplication.models.Miembro;
import com.fabricodedev.myapplication.utils.UserManager;

public class DetalleMiembroActivity extends AppCompatActivity {

    private String miembroId;
    private Miembro miembroActual;

    private TextView tvNombre, tvEstado, tvTelefono, tvDireccion;
    private RecyclerView rvHistorial;
    private Button btnRegistrarVisita, btnEditarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_miembro);

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
        miembroActual = UserManager.getInstance().getMiembroById(miembroId);

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
}