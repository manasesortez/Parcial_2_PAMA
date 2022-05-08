package com.amtodev.hospitalReservations.user;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amtodev.hospitalReservations.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class listaAdaptadorTasks extends RecyclerView.Adapter<listaAdaptadorTasks.DataViewHolder> {
    Context contexto;
    ArrayList<Adaptador> arregloTask = new ArrayList<>();
    LayoutInflater inflaterAdaptador;
    Adaptador objVehiculoModelo;


    public listaAdaptadorTasks(Context contexto, ArrayList<Adaptador> arregloTask){
        this.contexto = contexto;
        this.arregloTask = arregloTask;
    }

    public listaAdaptadorTasks(TaskMain contexto, FirebaseRecyclerOptions<Adaptador> options) {
        super();
    }

    @NonNull
    @Override
    public listaAdaptadorTasks.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewdoctor_info, parent, false);
        return new listaAdaptadorTasks.DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull listaAdaptadorTasks.DataViewHolder holder, int position) {
        holder.viewBind(arregloTask.get(position), position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return arregloTask.size();
    }

    public class DataViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        TextView TaskTarea, TaskDescripcion, TaskFecha, TaskHora, TaskStatus,  TaskUsuario, TaskOption;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            TaskTarea = itemView.findViewById(R.id.TareaModel);
            TaskDescripcion = itemView.findViewById(R.id.DescripcionModel);
            TaskFecha = itemView.findViewById(R.id.FechaModel);
            TaskHora = itemView.findViewById(R.id.txtHora);
            TaskStatus= itemView.findViewById(R.id.StatusModel);
            TaskUsuario = itemView.findViewById(R.id.Usuario);
            TaskOption = itemView.findViewById(R.id.txtOption);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }

        @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
        public void viewBind(Adaptador adaptador, int position) {
            TaskTarea.setText("Tarea: " + adaptador.getTarea());
            TaskDescripcion.setText("Descripcion: " + adaptador.getDescripcion());
            TaskFecha.setText("Fecha: " + adaptador.getFecha());
            TaskHora.setText("Hora: " + adaptador.getHora());
            TaskStatus.setText("Estatus: " + adaptador.getStatus());
            TaskUsuario.setText("Usuario: " + adaptador.getUsuario());

            TaskOption.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(contexto, TaskOption);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()){
                        case R.id.menu_edit:
                            Intent intent = new Intent(contexto, UpdateTask.class);
                            intent.putExtra("edit", (Parcelable) adaptador);
                            contexto.startActivity(intent);
                            break;
                        case R.id.menu_delete:
                            DAOAdaptador dao = new DAOAdaptador();
                            dao.remove(adaptador.getId_reserva()).addOnSuccessListener(suc ->{
                                Toast.makeText(contexto, "Record is Remove", Toast.LENGTH_SHORT).show();
                                notifyItemRemoved(position);
                                arregloTask.remove(adaptador);
                            }).addOnFailureListener(error -> {
                                Toast.makeText(contexto,""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                            break;
                    }
                    return  false;
                });
                popupMenu.show();
            });

        }
    }

}
