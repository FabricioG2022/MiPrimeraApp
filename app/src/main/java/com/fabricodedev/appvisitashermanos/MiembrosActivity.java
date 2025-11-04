package com.fabricodedev.appvisitashermanos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabricodedev.appvisitashermanos.adapters.MiembroAdapter;
import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

// ⭐ Imports de Firestore
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MiembrosActivity extends AppCompatActivity implements MiembroAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MiembroAdapter adapter;
    private List<Miembro> miembrosList;
    private TextView tvSaludoLider;
    // ⭐ Referencia a Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miembros);

        // ⭐ Inicializar Firestore (NUEVO)
        db = MiembrosManager.getInstance().getDb();

        recyclerView = findViewById(R.id.rv_miembros_list);
        tvSaludoLider = findViewById(R.id.tv_saludo_lider);

        // Obtener el nombre de usuario que viene del Login
        String username = getIntent().getStringExtra("EXTRA_USERNAME");
        if (username != null) {
            tvSaludoLider.setText("Hola, " + username + ". Miembros a visitar:");
        }

        // Configurar la lista
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // ⭐ Inicializar la lista
        miembrosList = new ArrayList<>();

        // ⭐ Inicializar el adaptador
        adapter = new MiembroAdapter(miembrosList, this);
        recyclerView.setAdapter(adapter);

        // Configurar el FAB para añadir un nuevo miembro
        findViewById(R.id.fab_add_miembro).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMiembroActivity.class);
            startActivity(intent);
        });
    }

    // ⭐ EL MÉTODO QUE REEMPLAZA A getAllMiembros()
    private void loadMiembrosFromFirestore() {
        if (db == null) return;

        db.collection("miembros")
                .get() // Pide los datos una sola vez
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        miembrosList.clear(); // Limpiar la lista existente
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convertir el documento a tu objeto Miembro
                            Miembro miembro = document.toObject(Miembro.class);
                            miembro.setId(document.getId()); // Asegúrate de asignar el ID del documento
                            miembrosList.add(miembro);
                        }

                        // Opcional: Ordenar la lista alfabéticamente si es necesario
                        // Collections.sort(listaMiembros, (m1, m2) -> m1.getNombre().compareTo(m2.getNombre()));

                        adapter.notifyDataSetChanged(); // Notificar al adaptador para que actualice la UI
                    } else {
                        Toast.makeText(this, "Error al cargar miembros: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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
        // Recargar la lista cada vez que volvemos a esta actividad
        loadMiembrosFromFirestore();
    }
}