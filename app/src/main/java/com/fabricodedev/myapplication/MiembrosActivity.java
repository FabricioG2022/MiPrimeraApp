package com.fabricodedev.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabricodedev.myapplication.adapters.MiembroAdapter;
import com.fabricodedev.myapplication.models.Miembro;
import com.fabricodedev.myapplication.utils.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiembrosActivity extends AppCompatActivity implements MiembroAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MiembroAdapter adapter;
    private List<Miembro> miembrosList;
    private TextView tvSaludoLider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miembros); // Usamos el nuevo layout

        recyclerView = findViewById(R.id.rv_miembros_list);
        tvSaludoLider = findViewById(R.id.tv_saludo_lider);

        // Obtener el nombre de usuario que viene del Login
        String username = getIntent().getStringExtra("EXTRA_USERNAME");
        if (username != null) {
            tvSaludoLider.setText("Hola, " + username + ". Miembros a visitar:");
        }

        // Configurar la lista
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar datos
        loadMiembrosData();

        // Configurar el FAB para añadir un nuevo miembro
        findViewById(R.id.fab_add_miembro).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMiembroActivity.class);
            startActivity(intent);
        });
    }

    // Método que carga los datos desde el Singleton
    private void loadMiembrosData() {
        // Convertir el Map de Miembros a una List para el RecyclerView
        Map<String, Miembro> miembrosMap = UserManager.getInstance().getAllMiembros();
        miembrosList = new ArrayList<>(miembrosMap.values());

        adapter = new MiembroAdapter(miembrosList, this);
        recyclerView.setAdapter(adapter);
    }

    // ⭐ Manejo del click en la Tarjeta (Ir a Detalle/Edición/Registro de Visita)
    @Override
    public void onItemClick(Miembro miembro) {
        // Crear y lanzar DetalleMiembroActivity, pasando miembro.getId()
        Intent intent = new Intent(this, DetalleMiembroActivity.class);
        intent.putExtra("MIEMBRO_ID", miembro.getId());
        startActivity(intent);
    }

    // ⭐ Manejo del click en el Icono de Llamada
    @Override
    public void onCallClick(Miembro miembro) {
        String telefono = miembro.getTelefono();

        // Validación básica
        if (telefono == null || telefono.isEmpty()) {
            Toast.makeText(this, "El miembro no tiene un teléfono registrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Crear el Intent de llamada
        // ACTION_DIAL solo prepara la llamada, no la inicia directamente.
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);

        // 2. Establecer el URI del teléfono (formato tel:1234567)
        dialIntent.setData(Uri.parse("tel:" + telefono));

        // 3. Iniciar la actividad
        try {
            startActivity(dialIntent);
        } catch (Exception e) {
            // En caso de que no haya ninguna app de teléfono disponible (raro, pero posible)
            Toast.makeText(this, "No se pudo iniciar la aplicación de teléfono.", Toast.LENGTH_SHORT).show();
        }
    }

    // Es buena práctica recargar los datos cuando se vuelve a esta actividad (después de editar o registrar visita)
    @Override
    protected void onResume() {
        super.onResume();
        loadMiembrosData();
    }
}