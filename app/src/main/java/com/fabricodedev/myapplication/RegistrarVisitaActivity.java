package com.fabricodedev.myapplication;

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

import com.fabricodedev.myapplication.models.Miembro;
import com.fabricodedev.myapplication.utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarVisitaActivity extends AppCompatActivity {

    private String miembroId;
    private TextView tvNombreMiembro, tvFechaVisita;
    private RadioGroup rgEstadoEspiritual;
    private EditText etNotaVisita;
    private Button btnGuardarVisita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_visita);

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

    private void cargarDatosIniciales() {
        Miembro miembro = UserManager.getInstance().getMiembroById(miembroId);
        if (miembro != null) {
            tvNombreMiembro.setText(miembro.getNombre());
        }

        // Establecer la fecha actual por defecto (formato DD-MM-YYYY)
        String fechaActual = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tvFechaVisita.setText(fechaActual);
    }

    private void guardarVisita() {
        // Obtener valores de la UI
        String fecha = tvFechaVisita.getText().toString();
        String nota = etNotaVisita.getText().toString().trim();

        if (nota.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa una nota o comentario.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Determinar el Estado Espiritual seleccionado
        int selectedId = rgEstadoEspiritual.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String estado = selectedRadioButton.getText().toString(); // Será "Verde", "Amarillo" o "Rojo"

        // 2. El Visitador. Usamos un placeholder por ahora (con el login real, se usara el nombre del líder logueado)
        String visitador = "Líder Principal";

        // 3. Llamar al método de registro en el Manager
        boolean success = UserManager.getInstance().registrarNuevaVisita(
                miembroId,
                fecha,
                nota,
                estado,
                visitador
        );

        if (success) {
            Toast.makeText(this, "¡Visita registrada con éxito! El estado de " + tvNombreMiembro.getText().toString() + " es ahora " + estado + ".", Toast.LENGTH_LONG).show();
            // Cerrar la actividad para volver a DetalleMiembroActivity (que se recargará en onResume)
            finish();
        } else {
            Toast.makeText(this, "Error al registrar la visita. Miembro no encontrado.", Toast.LENGTH_LONG).show();
        }
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