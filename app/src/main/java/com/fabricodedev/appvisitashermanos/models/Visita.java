package com.fabricodedev.appvisitashermanos.models;

public class Visita {
    private String fecha;
    private String nota;
    private String nuevoEstadoAnimico;
    private String visitador;

    public Visita() {
        // Este constructor debe existir y ser público
    }
    public Visita(String fecha, String nota, String nuevoEstadoAnimico, String visitador) {
        this.fecha = fecha;
        this.nota = nota;
        this.nuevoEstadoAnimico = nuevoEstadoAnimico;
        this.visitador = visitador;
    }
    // --- Getters (Requeridos por Firestore)
    public String getFecha() { return fecha; }
    public String getNota() { return nota; }
    public String getNuevoEstadoAnimico() { return nuevoEstadoAnimico; }
    public String getVisitador() { return visitador; }

    // --- ⭐ Setters (Recomendados para que Firestore pueda deserializar)
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setNota(String nota) { this.nota = nota; }
    public void setNuevoEstadoAnimico(String nuevoEstadoAnimico) { this.nuevoEstadoAnimico = nuevoEstadoAnimico; }
    public void setVisitador(String visitador) { this.visitador = visitador; }

}