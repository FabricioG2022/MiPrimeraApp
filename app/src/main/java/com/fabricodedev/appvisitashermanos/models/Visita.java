package com.fabricodedev.appvisitashermanos.models;

public class Visita {
    private final String fecha;
    private final String nota;
    private final String nuevoEstadoAnimico;
    private final String visitador;

    public Visita(String fecha, String nota, String nuevoEstadoAnimico, String visitador) {
        this.fecha = fecha;
        this.nota = nota;
        this.nuevoEstadoAnimico = nuevoEstadoAnimico;
        this.visitador = visitador;
    }

    // --- Getters
    public String getFecha() { return fecha; }
    public String getNota() { return nota; }
    public String getNuevoEstadoAnimico() { return nuevoEstadoAnimico; }
    public String getVisitador() { return visitador; }

}