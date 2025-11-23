package com.fabricodedev.appvisitashermanos.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
// ⭐ Importación de Glide
import com.bumptech.glide.Glide;
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

        // El método bind establece todos los campos, incluyendo la foto
        holder.bind(miembro, listener);
        // ⭐ Lógica de GLIDE movida al método bind() o se mantiene aquí, pero usando holder.ivFotoPerfil
        if (miembro.getFotoUrl() != null && !miembro.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()) // Contexto de la vista
                    .load(miembro.getFotoUrl())         // La URL de la foto
                    .placeholder(R.drawable.ic_user_placeholder)
                    .error(R.drawable.ic_user_error)
                    .circleCrop()
                    .into(holder.ivFotoPerfil); // ⭐ Usa la referencia del holder
        } else {
            holder.ivFotoPerfil.setImageResource(R.drawable.ic_user_placeholder);
        }
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
        public final ImageView ivFotoPerfil;
        public MiembroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_miembro_nombre);
            tvUltimaVisita = itemView.findViewById(R.id.tv_ultima_visita);
            tvTelefono = itemView.findViewById(R.id.tv_miembro_telefono);
            ivCallAction = itemView.findViewById(R.id.iv_call_action);
            vEstadoColor = itemView.findViewById(R.id.v_estado_color);
            ivFotoPerfil = itemView.findViewById(R.id.iv_foto_perfil);
        }

        public void bind(final Miembro miembro, final OnItemClickListener listener) {
            tvNombre.setText(miembro.getNombre());
            tvUltimaVisita.setText("Última Visita: " + miembro.getUltimaVisita());
            tvTelefono.setText("Teléfono: " + miembro.getTelefono());

            // ⭐ Lógica para cambiar el color del indicador
            if ("Rojo".equals(miembro.getEstadoAnimico())) {
                vEstadoColor.getBackground().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            } else if ("Amarillo".equals(miembro.getEstadoAnimico())) {
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