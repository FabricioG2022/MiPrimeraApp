package com.fabricodedev.appvisitashermanos.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fabricodedev.appvisitashermanos.R;
import com.fabricodedev.appvisitashermanos.models.Miembro;
import java.util.List;

public class MiembroAdapter extends RecyclerView.Adapter<MiembroAdapter.MiembroViewHolder> {

    private final List<Miembro> miembrosList;
    private final OnItemClickListener listener;

    // Interfaz para manejar clicks
    public interface OnItemClickListener {
        void onItemClick(Miembro miembro); // Para ver detalles/editar
        void onCallClick(Miembro miembro);  // Para la llamada rápida
    }

    public MiembroAdapter(List<Miembro> miembrosList, OnItemClickListener listener) {
        this.miembrosList = miembrosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MiembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_miembro_card, parent, false);
        return new MiembroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiembroViewHolder holder, int position) {
        Miembro miembro = miembrosList.get(position);
        holder.bind(miembro, listener);
    }

    @Override
    public int getItemCount() {
        return miembrosList.size();
    }

    public static class MiembroViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvUltimaVisita;
        private final TextView tvTelefono;
        private final ImageView ivCallAction;
        private final View vEstadoColor;

        public MiembroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_miembro_nombre);
            tvUltimaVisita = itemView.findViewById(R.id.tv_ultima_visita);
            tvTelefono = itemView.findViewById(R.id.tv_miembro_telefono);
            ivCallAction = itemView.findViewById(R.id.iv_call_action);
            vEstadoColor = itemView.findViewById(R.id.v_estado_color);
        }

        public void bind(final Miembro miembro, final OnItemClickListener listener) {
            tvNombre.setText(miembro.getNombre());
            tvUltimaVisita.setText("Última Visita: " + miembro.getUltimaVisita());
            tvTelefono.setText("Teléfono: " + miembro.getTelefono());

            // ⭐ Lógica para cambiar el color del indicador
            if ("Rojo".equals(miembro.getEstadoEspiritual())) {
                vEstadoColor.getBackground().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            } else if ("Amarillo".equals(miembro.getEstadoEspiritual())) {
                vEstadoColor.getBackground().setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                vEstadoColor.getBackground().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            }

            // Click para ir a Detalle/Edición
            itemView.setOnClickListener(v -> listener.onItemClick(miembro));

            // Click para acción rápida de Llamada
            ivCallAction.setOnClickListener(v -> listener.onCallClick(miembro));
        }
    }
}