package com.fabricodedev.appvisitashermanos.models;

public class Visita {
    private final String fecha;
    private final String nota;
    private final String nuevoEstadoEspiritual;
    private final String visitador;

    public Visita(String fecha, String nota, String nuevoEstadoEspiritual, String visitador) {
        this.fecha = fecha;
        this.nota = nota;
        this.nuevoEstadoEspiritual = nuevoEstadoEspiritual;
        this.visitador = visitador;
    }

    // --- Getters
    public String getFecha() { return fecha; }
    public String getNota() { return nota; }
    public String getNuevoEstadoEspiritual() { return nuevoEstadoEspiritual; }
    public String getVisitador() { return visitador; }

}