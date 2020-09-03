package com.example.mantenimientohdin2.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mantenimientohdin2.R;
import com.example.mantenimientohdin2.data.TrabajosVO;

import java.util.List;

public class Adaptadortrabajos2 extends RecyclerView.Adapter<Adaptadortrabajos2.UsuariosHolder> implements View.OnClickListener {
    List<TrabajosVO> listaUsuarios;
    String valor;
    private View.OnClickListener listener;

    public Adaptadortrabajos2(List<TrabajosVO> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public UsuariosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista= LayoutInflater.from(parent.getContext()).inflate(R.layout.listatrabajos,parent,false);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);

        vista.setOnClickListener(this);
        return new Adaptadortrabajos2.UsuariosHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosHolder holder, int position) {
        holder.txtDocumento.setText(listaUsuarios.get(position).getDocumento().toString());
        holder.txtNombre.setText(listaUsuarios.get(position).getNombre().toString());

        holder.txtProfesion.setText(listaUsuarios.get(position).getProfesion().toString());
        valor=listaUsuarios.get(position).getRutaImagen().toString();
        if(valor.equals("")){

            holder.imagen.setImageResource(R.drawable.dibujo);
        }else{
            holder.imagen.setImageResource(R.drawable.dibujo2);
        }

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }

    public class UsuariosHolder extends RecyclerView.ViewHolder{

        TextView txtDocumento,txtNombre,txtProfesion;
        ImageView imagen;
        public UsuariosHolder(View itemView) {
            super(itemView);
            txtDocumento= (TextView) itemView.findViewById(R.id.txtDocumento);
            txtNombre= (TextView) itemView.findViewById(R.id.txtNombre);
            txtProfesion= (TextView) itemView.findViewById(R.id.txtProfesion);

            imagen=(ImageView) itemView.findViewById(R.id.idImagen);
        }
    }
}
