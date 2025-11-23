package com.fabricodedev.appvisitashermanos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fabricodedev.appvisitashermanos.api.ApiService;
import com.fabricodedev.appvisitashermanos.api.VersiculoDiario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.fabricodedev.appvisitashermanos.adapters.MiembroAdapter;
import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

import java.util.ArrayList;
import java.util.List;

// ⭐ Imports de Firestore
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MiembrosActivity extends AppCompatActivity implements MiembroAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MiembroAdapter adapter;
    private List<Miembro> miembrosList;
    private TextView tvSaludoLider;
    private TextView tvVersiculoDiario;
    // ⭐ Datos de la API.Bible-API.com
    private static final String BASE_URL_BIBLE_API = "https://bible-api.com/";
    // Versículo a cargar (ejemplo: Juan 3:16, URL-encoded)
    private static final String[] VERSES = new String[]{
            "John 3:16",
            "Psalm 23:1",
            "Romans 8:28",
            "Philippians 4:13",
            "Isaiah 41:10",
            "Matthew 6:33"

    };
    //private static final String BIBLE_VERSION = "RVA";
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
        tvVersiculoDiario = findViewById(R.id.tv_versiculo_diario);

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
        loadDailyVerse();
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
    private void loadDailyVerse() {

        // 1. Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_BIBLE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        // ⭐ 1. Obtener una referencia aleatoria
        String randomVerse = VERSES[new java.util.Random().nextInt(VERSES.length)];
        // 2. Crear la llamada a la API
        Call<VersiculoDiario> call = apiService.getDailyVerse(randomVerse);

        // 3. Ejecutar la llamada de forma asíncrona
        call.enqueue(new Callback<VersiculoDiario>() {
            @Override
            public void onResponse(Call<VersiculoDiario> call, Response<VersiculoDiario> response) {
                if (response.isSuccessful() && response.body() != null) {

                    VersiculoDiario versiculo = response.body();

                    String texto = versiculo.getTexto();
                    String referencia = versiculo.getReferencia();
                    String traduccion = versiculo.getTraduccion();

                    // ⭐ Actualizar la UI
                    String finalVersiculo = "\"" + texto.trim() + "\"\n— " + referencia + " (" + traduccion + ")";
                    tvVersiculoDiario.setText(finalVersiculo);

                } else {
                    tvVersiculoDiario.setText("Error al cargar versículo: Código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VersiculoDiario> call, Throwable t) {
                tvVersiculoDiario.setText("Error de conexión con Bible-API.com: " + t.getMessage());
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