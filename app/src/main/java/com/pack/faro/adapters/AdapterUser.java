package com.pack.faro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pack.faro.R;
import com.pack.faro.databinding.RowUserListBinding;
import com.pack.faro.filters.FilterUser;
import com.pack.faro.model.ModelUser;

import java.util.ArrayList;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.HolderUser> implements Filterable {

    private Context context;
  public ArrayList<ModelUser> userArrayList, filterList;

    // Elimina la instancia de RowUserListBinding
    // private RowUserListBinding binding;

  public FilterUser filter;

    private static final String TAG = "PDF_ADAPTER_TAG";

    private ProgressBar progressDialog;

    public AdapterUser(Context context, ArrayList<ModelUser> userArrayList){
        this.context = context;
        this.userArrayList = userArrayList;
        this.filterList = new ArrayList<>(userArrayList); // Crea una copia de la lista original para el filtro
    }

    @NonNull
    @Override
    public HolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla la vista específica para cada elemento de la lista
        RowUserListBinding binding = RowUserListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUser holder, int position) {
        ModelUser model = userArrayList.get(position);

        String correo = model.getEmail();
        String nombre = model.getName();
        String habilidad = model.getHabilidad();
        String image = model.getImage();
        long timestamp = model.getTimestamp();

        holder.correotxt.setText(correo);
        holder.nombretxt.setText(nombre);
        holder.habilidadtxt.setText(habilidad);

        // Obtén la referencia al ImageView en tu HolderUser
        ImageView imageView = holder.imageView;

        // Cargar la imagen desde la URL utilizando Glide
        Glide.with(holder.itemView.getContext())
                .load(image)
                .error(R.drawable.sombrerodebufon) // Proporciona un recurso de imagen de marcador de posición
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterUser(filterList, this);
        }
        return filter;
    }

    class HolderUser extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        TextView correotxt, nombretxt, habilidadtxt;
        ImageView imageView;

        public HolderUser(@NonNull View view){
            super(view);

            RowUserListBinding binding = RowUserListBinding.bind(view); // Enlaza la vista específica a RowUserListBinding

            correotxt = binding.correotxt;
            nombretxt = binding.nombretxt;
            habilidadtxt = binding.habilidadtxt;
            imageView = binding.imageperfil;
        }
    }
}

