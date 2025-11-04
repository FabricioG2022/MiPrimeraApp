package com.fabricodedev.appvisitashermanos;
// ⭐ Importaciones de Firebase
import com.fabricodedev.appvisitashermanos.models.Visita;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.utils.MiembrosManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarVisitaActivity extends AppCompatActivity {

    private String miembroId;
    private TextView tvNombreMiembro, tvFechaVisita;
    private RadioGroup rgEstadoEspiritual;
    private EditText etNotaVisita;
    private Button btnGuardarVisita;

    // ⭐ Referencia a Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_visita);

        // ⭐ Inicializar Firestore (NUEVO)
        db = MiembrosManager.getInstance().getDb();

        // 1. Obtener el ID del Intent
        miembroId = getIntent().getStringExtra("MIEMBRO_ID");
        if (miembroId == null) {
            Toast.makeText(this, "Error: ID de miembro requerido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Inicializar Vistas
        tvNombreMiembro = findViewById(R.id.tv_miembro_nombre_visita);
        tvFechaVisita = findViewById(R.id.tv_fecha_visita);
        rgEstadoEspiritual = findViewById(R.id.rg_estado_espiritual);
        etNotaVisita = findViewById(R.id.et_nota_visita);
        btnGuardarVisita = findViewById(R.id.btn_guardar_visita);

        // Obtener referencias a los RadioButtons
        RadioButton rbVerde = findViewById(R.id.rb_estado_verde);
        RadioButton rbAmarillo = findViewById(R.id.rb_estado_amarillo);
        RadioButton rbRojo = findViewById(R.id.rb_estado_rojo);

        // ⭐ Aplicar los colores a los círculos de los RadioButtons

        setRadioButtonColor(rbVerde, Color.GREEN);
        setRadioButtonColor(rbAmarillo, Color.YELLOW);
        setRadioButtonColor(rbRojo, Color.RED);

        // 3. Cargar Nombre del Miembro y Fecha por defecto
        cargarDatosIniciales();

        // 4. Configurar Listener
        btnGuardarVisita.setOnClickListener(v -> guardarVisita());
    }

    // ⭐ MIGRACIÓN 1: Cargar datos iniciales desde Firestore
    private void cargarDatosIniciales() {

        if (miembroId == null || db == null) return;

        // 1. Obtener el nombre del miembro (ASÍNCRONO)
        db.collection("miembros").document(miembroId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Miembro miembro = documentSnapshot.toObject(Miembro.class);
                        if (miembro != null) {
                            tvNombreMiembro.setText(miembro.getNombre());
                        }
                    } else {
                        Toast.makeText(this, "Miembro no encontrado.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos iniciales.", Toast.LENGTH_SHORT).show();
                    finish();
                });

        // 2. Establecer la fecha actual (SÍNCRONO, se mantiene igual)
        String fechaActual = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tvFechaVisita.setText(fechaActual);
    }

    // ⭐ MIGRACIÓN 2: Guardar visita usando la llamada asíncrona del Manager
    private void guardarVisita() {
        // Obtener valores de la UI (se mantiene igual)
        String fecha = tvFechaVisita.getText().toString();
        String nota = etNotaVisita.getText().toString().trim();

        if (nota.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa una nota o comentario.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Determinar el Estado Espiritual seleccionado
        int selectedId = rgEstadoEspiritual.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Selecciona el estado espiritual.", Toast.LENGTH_LONG).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String nuevoEstado = selectedRadioButton.getText().toString(); // Será "Verde", "Amarillo" o "Rojo"

        // 2. El Visitador.
        // En una app real, obtendrías esto de FirebaseAuth.getInstance().getCurrentUser().getEmail()
        String visitador = "Líder Principal";

        // 3. Crear el objeto Visita
        Visita nuevaVisita = new Visita(fecha, nota, nuevoEstado, visitador);

        // 4. Llamar al método de registro en el Manager (ASÍNCRONO)
        // Ya no devuelve 'boolean success', solo inicia la operación.
        MiembrosManager.getInstance().registrarNuevaVisita(
                miembroId,
                nuevaVisita, // Usamos el objeto Visita
                nuevoEstado // Pasamos el nuevo estado
        );

        // 5. Dar feedback y cerrar (asumiendo que la operación es exitosa)
        Toast.makeText(this, "¡Visita registrada con éxito! El estado se actualizará en Detalle.", Toast.LENGTH_LONG).show();
        finish();

        // NOTA: Para un feedback 100% preciso, deberías añadir Listeners a la llamada
        // en MiembrosManager y usar una Interfaz de Callback, pero esto funciona para la migración.
    }

    private void setRadioButtonColor(RadioButton radioButton, int color) {
        // Para versiones modernas, usamos ColorStateList para cambiar el color
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // Estado no seleccionado
                        new int[]{android.R.attr.state_checked}  // Estado seleccionado
                },
                new int[]{
                        Color.GRAY, // Color cuando no está seleccionado
                        color       // Color cuando está seleccionado (el que quieres)
                }
        );

        // Asignar el ColorStateList al botón
        // IMPORTANTE: Asegúrate de tener la dependencia androidx.core en tu build.gradle
        androidx.core.widget.CompoundButtonCompat.setButtonTintList(radioButton, colorStateList);
    }
}