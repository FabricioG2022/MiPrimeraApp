package com.fabricodedev.appvisitashermanos.utils;
import com.google.firebase.firestore.FirebaseFirestore;

import com.fabricodedev.appvisitashermanos.models.Miembro;
import com.fabricodedev.appvisitashermanos.models.Visita;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID; // Para generar IDs únicos

public class MiembrosManager {
    private static MiembrosManager instance;
    private final FirebaseFirestore db;

    private MiembrosManager() {
        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();
    }
    public static synchronized MiembrosManager getInstance() {
        if (instance == null) {
            instance = new MiembrosManager();
        }
        return instance;
    }
    // ⭐ Getter para obtener la instancia de Firestore (útil para las Activities)
    public FirebaseFirestore getDb() {
        return db;
    }

    // --- Lógica de MIEMBROS

    // 3. Crear un nuevo miembro (Sobrecarga eliminada para simplificar)
    public void addMiembro(Miembro nuevoMiembro) {
        db.collection("miembros")
                .document(nuevoMiembro.getId())
                .set(nuevoMiembro)
                .addOnSuccessListener(aVoid -> {
                    // Éxito. El resultado se maneja en la Activity.
                })
                .addOnFailureListener(e -> {
                    // Error. El resultado se maneja en la Activity.
                });
    }
    /**
     * 4. Lógica para EDITAR/ACTUALIZAR campos básicos.
     */
    public void updateMiembroData(String id, String nombre, String direccion, String telefono) {

        // Un mapa temporal con solo los campos a actualizar
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("direccion", direccion);
        updates.put("telefono", telefono);

        db.collection("miembros")
                .document(id)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Éxito. El resultado se maneja en EditarMiembroActivity.java
                })
                .addOnFailureListener(e -> {
                    // Error. El resultado se maneja en EditarMiembroActivity.java
                });
    }
    /**
     * 5. Eliminar un miembro de la congregación por su ID.
     */
    public void deleteMiembro(String id) {
        db.collection("miembros")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Éxito. El resultado se maneja en DetalleMiembroActivity.java
                })
                .addOnFailureListener(e -> {
                    // Error. El resultado se maneja en DetalleMiembroActivity.java
                });
    }
    /**
     * 6. Registrar nueva visita y actualizar campos principales.
     */
    public void registrarNuevaVisita(String miembroId, Visita nuevaVisita, String nuevoEstado) {

        // Necesitas una referencia al documento
        db.collection("miembros").document(miembroId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Miembro miembro = documentSnapshot.toObject(Miembro.class);
                    if (miembro != null) {
                        // Actualiza el objeto localmente
                        miembro.agregarVisita(nuevaVisita);
                        miembro.setEstadoEspiritual(nuevoEstado);
                        miembro.setUltimaVisita(nuevaVisita.getFecha());

                        // Sube el objeto completo de nuevo
                        db.collection("miembros").document(miembroId).set(miembro)
                                .addOnSuccessListener(aVoid -> {
                                    // Éxito.
                                })
                                .addOnFailureListener(e -> {
                                    // Error de subida.
                                });
                    }
                });
    }
// NOTA: La Activity debe proveer el objeto Visita completo y el nuevo estado.

}