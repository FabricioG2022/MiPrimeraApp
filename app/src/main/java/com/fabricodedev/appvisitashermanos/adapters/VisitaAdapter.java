package com.fabricodedev.appvisitashermanos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fabricodedev.appvisitashermanos.R;
import com.fabricodedev.appvisitashermanos.models.Visita;
import java.util.List;

public class VisitaAdapter extends RecyclerView.Adapter<VisitaAdapter.VisitaViewHolder> {

    private final List<Visita> historialList;

    public VisitaAdapter(List<Visita> historialList) {
        this.historialList = historialList;
    }

    @NonNull
    @Override
    public VisitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visita_nota, parent, false);
        return new VisitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitaViewHolder holder, int position) {
        Visita visita = historialList.get(position);

        // Formatear la nota para incluir el visitador
        String notaCompleta = visita.getNota() + "\n(Registrado por: " + visita.getVisitador() + ")";

        holder.tvFecha.setText(visita.getFecha() + " (Estado final: " + visita.getNuevoEstadoAnimico() + ")");
        holder.tvNota.setText(notaCompleta);
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    public static class VisitaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFecha;
        private final TextView tvNota;

        public VisitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tv_historial_fecha);
            tvNota = itemView.findViewById(R.id.tv_historial_nota);
        }
    }
}