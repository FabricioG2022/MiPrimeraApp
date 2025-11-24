package com.fabricodedev.appvisitashermanos;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fabricodedev.appvisitashermanos.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.fabricodedev.appvisitashermanos.adapters.MiembroAdapter;
import com.fabricodedev.appvisitashermanos.api.VersiculoDiario;
import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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
    private static final String BASE_URL_BIBLIA = "https://api.biblia.com/v1/";
    private static final String API_KEY = BuildConfig.BIBLIA_API_KEY;
    private static final String VERSION = "RVA";
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_BIBLIA)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Versículos del día — en inglés (la API solo acepta esto)
        String[] versiculos = {
                "John3.16",
                "Psalm23.1",
                "Philippians4.13",
                "Romans8.28",
                "Proverbs3.5",
                "Matthew5.9",
                "Psalm91.1"
        };

        // Día del año compatible con minSdk24
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);

        String referencia = versiculos[day % versiculos.length];

        Call<VersiculoDiario> call = apiService.getDailyVerse(
                VERSION,
                referencia,
                API_KEY,
                "oneVerse"
        );

        call.enqueue(new Callback<VersiculoDiario>() {
            @Override
            public void onResponse(Call<VersiculoDiario> call, Response<VersiculoDiario> response) {
                if (!response.isSuccessful()) {
                    tvVersiculoDiario.setText("Error: " + response.code());
                    return;
                }

                VersiculoDiario v = response.body();

                if (v == null || v.getTexto() == null) {
                    tvVersiculoDiario.setText("Sin contenido");
                    return;
                }

                // Convertir HTML a texto plano
                String limpio = Html.fromHtml(v.getTexto(), Html.FROM_HTML_MODE_LEGACY)
                        .toString()
                        .trim();

                // Normalizar saltos
                limpio = limpio.replace("\r", "");

                // Quitar líneas vacías dobles o triples
                limpio = limpio.replaceAll("\n{2,}", "\n");

                // Unir todo el versículo en una sola línea
                limpio = limpio.replace("\n", " ").trim();
                // Reemplazar espacios múltiples (tabs, dobles, triples) por un solo espacio
                limpio = limpio.replaceAll("\\s{2,}", " ").trim();


                // Separar por líneas
                String[] lineas = limpio.split("\n");

                String versiculo = null;

                // Buscar el primer texto que no sea título
                for (String l : lineas) {
                    l = l.trim();

                    // Ignorar líneas vacías
                    if (l.isEmpty()) continue;

                    // Ignorar títulos (no tienen números y son textos cortos)
                    boolean esTitulo = !l.matches(".*\\d.*") && l.split(" ").length <= 7;

                    if (!esTitulo) {
                        versiculo = l;
                        break;
                    }
                }

                // Si no encontramos un versículo claro, usar todo el texto limpio
                if (versiculo == null) {
                    versiculo = limpio;
                }

                // Mostrar resultado final
                tvVersiculoDiario.setText(
                        "\"" + versiculo + "\"\n— " + referencia.replace(".", ":")
                );
            }


            @Override
            public void onFailure(Call<VersiculoDiario> call, Throwable t) {
                tvVersiculoDiario.setText("Error: " + t.getMessage());
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