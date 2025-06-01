package com.proyecto.facilgimapp.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar una lista de ejercicios en un RecyclerView.
 * Cada elemento presenta el nombre del ejercicio y su imagen asociada.
 * <p>
 * Utiliza Glide para cargar las imágenes de forma asíncrona y manejar placeholders.
 * </p>
 * 
 * @author Francisco Santana
 */
public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.VH> {

    /**
     * Lista interna de elementos {@link EjercicioDTO} que se mostrarán.
     */
    private final List<EjercicioDTO> items = new ArrayList<>();

    /**
     * Reemplaza la lista actual de ejercicios por una nueva lista y notifica al adaptador
     * para que actualice la vista.
     *
     * @param list Lista de {@link EjercicioDTO} que se desea mostrar; puede ser null.
     */
    public void submitList(List<EjercicioDTO> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * Crea y retorna una nueva instancia de {@link VH} inflando el layout de cada ítem.
     *
     * @param parent   Contenedor padre al que se añadirá la vista de cada ejercicio.
     * @param viewType Tipo de vista (no se utiliza en este adaptador, siempre es el mismo layout).
     * @return Nueva instancia de {@link VH}.
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new VH(v);
    }

    /**
     * Vincula los datos de un {@link EjercicioDTO} a las vistas del ViewHolder.
     * <ul>
     *     <li>Establece el nombre del ejercicio en el TextView.</li>
     *     <li>Carga la imagen desde la URL usando Glide, mostrando un placeholder
     *         mientras carga y en caso de error o URL nula.</li>
     * </ul>
     *
     * @param holder   ViewHolder que contiene las vistas de cada ítem.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        EjercicioDTO e = items.get(position);
        holder.tvName.setText(e.getNombre());
        Glide.with(holder.img.getContext())
                .asDrawable()
                .load(e.getImagenUrl())
                .placeholder(R.drawable.placeholder)     // mientras carga
                .error(R.drawable.placeholder)           // si falla la descarga
                .fallback(R.drawable.placeholder)        // si la URL es null
                .centerCrop()
                .into(holder.img);
    }

    /**
     * Retorna la cantidad de ejercicios en la lista interna.
     *
     * @return Número de elementos que el adaptador gestionará.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder que contiene las referencias a las vistas de cada ítem de ejercicio.
     * Se encarga de almacenar el ImageView y el TextView para reutilización eficiente.
     */
    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;

        /**
         * Construye el ViewHolder vinculando las vistas del layout.
         *
         * @param itemView Vista inflada de cada ítem (item_exercise.xml).
         */
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgExercise);
            tvName = itemView.findViewById(R.id.tvExerciseName);
        }
    }
}

