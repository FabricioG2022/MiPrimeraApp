package com.fabricodedev.appvisitashermanos.models;

import java.util.ArrayList;
import java.util.List;
public class Miembro {
    // Usaremos un ID único para poder encontrarlo fácilmente en el Map
    private String id;
    private String nombre;
    private String direccion;
    private String telefono;
    // ⭐ Campo para la URL de la foto
    private String fotoUrl;
    private String ultimaVisita; // Almacenado como String (ej: "2025-10-20")
    private String estadoAnimico; // Ej: "Verde", "Amarillo", "Rojo"
    private List<Visita> historialVisitas;
    // El historial se almacenará como una lista de notas
    // (Por simplicidad inicial, lo mantendremos como String,
    // pero idealmente sería una lista de objetos Nota)

    // Constructor vacío para Firestore
    public Miembro() {
        // Asegurarse de inicializar listas para evitar NullPointerExceptions
        this.historialVisitas = new ArrayList<>();
        this.ultimaVisita = "Nunca";
        this.estadoAnimico = "Verde";
    }

    public Miembro(String id, String nombre, String direccion, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.ultimaVisita = "Nunca";
        this.estadoAnimico = "Verde"; // Estado por defecto
        this.historialVisitas = new ArrayList<>(); // Inicializamos la lista
    }

    // --- Getters (Necesarios para leer los datos)
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    // ⭐ Getter para la URL de la foto
    public String getFotoUrl() { return fotoUrl; }
    public String getUltimaVisita() { return ultimaVisita; }
    public String getEstadoAnimico() { return estadoAnimico; }
    public List<Visita> getHistorialVisitas() {
        if (historialVisitas == null) {
            historialVisitas = new ArrayList<>(); // Seguridad extra para Firestore
        }
        return historialVisitas;
    }

    // --- Setters (Necesarios para EDITAR los datos)
    public void setId(String id) {
        this.id = id;
    }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    // ⭐ Setter para la URL de la foto
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public void setUltimaVisita(String ultimaVisita) { this.ultimaVisita = ultimaVisita; }
    public void setEstadoAnimico(String estadoAnimico) { this.estadoAnimico = estadoAnimico; }
    public void agregarVisita(Visita visita) {
        // Asegurarse de que la lista no sea null antes de agregar
        if (this.historialVisitas == null) {
            this.historialVisitas = new ArrayList<>();
        }
        this.historialVisitas.add(visita);
        // Al agregar una visita, actualizamos automáticamente los campos principales
        this.ultimaVisita = visita.getFecha();
        this.estadoAnimico = visita.getNuevoEstadoAnimico();
    }

}