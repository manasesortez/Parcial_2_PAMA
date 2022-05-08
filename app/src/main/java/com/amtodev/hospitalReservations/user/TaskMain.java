package com.amtodev.hospitalReservations.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.amtodev.hospitalReservations.Login;
import com.amtodev.hospitalReservations.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TaskMain extends AppCompatActivity{

    String[] status = {
            "Filtrar por Estatus",
            "Pendiente",
            "En Proceso",
            "Terminada"
    };

    Button btnChangePassword;
    private FirebaseAuth objFirebase;
    private FirebaseAuth.AuthStateListener objFirebaseListener;

    RecyclerView recycleViewHospital;

    private ArrayList<Adaptador> listaTask = new ArrayList<>();

    private FirebaseRecyclerOptions<Adaptador> options;

    listaAdaptadorTasks objListaAdaptadosTask;

    TextView textView2;


    //Base de Datos
    FirebaseDatabase objBaseDatos;
    DatabaseReference dbReference;
    FirebaseUser objUser;
    ImageButton SearchHospital;
    ImageView addCarButton;
    Spinner txtCriterioDoctor;

    private ProgressDialog objDialog;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_main);

        ImageView logoutUser = findViewById(R.id.buttonLoggoutUser);
        logoutUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openDialog(view);
            }
        });
        txtCriterioDoctor = findViewById(R.id.spinnerStatusUpdate);

        ArrayAdapter<String> elAdaptador = new ArrayAdapter<String>(this, R.layout.spinner_item, status);
        txtCriterioDoctor.setAdapter(elAdaptador);

        textView2 = findViewById(R.id.textView2);

        SearchHospital = findViewById(R.id.btnSearchUserHospital);
        SearchHospital.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String Spinners = txtCriterioDoctor.getSelectedItem().toString();
                firebaseTaskFilter(Spinners);
            }
        });

        addCarButton = findViewById(R.id.addCarButton);
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCar();
            }
        });

        recycleViewHospital = findViewById(R.id.recycleViewHospital);

        objDialog = new ProgressDialog(this);

        objFirebase = FirebaseAuth.getInstance();

        objFirebaseListener = new FirebaseAuth.AuthStateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser objUsuario = firebaseAuth.getCurrentUser();
                if (objUsuario != null){
                    textView2.setText("View Tasks Details!");
                }
            }
        };

        FirebaseApp.initializeApp(TaskMain.this);
        objBaseDatos = FirebaseDatabase.getInstance("https://parcial-pama-default-rtdb.firebaseio.com/");
        dbReference = objBaseDatos.getReference().child("tblTarea");
    }

    private void firebaseTaskFilter(String filter){
        String Spinners = txtCriterioDoctor.getSelectedItem().toString();

        dbReference.orderByChild("status").equalTo(filter).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    listaTask.clear();
                    for (DataSnapshot objFilaDatos: dataSnapshot.getChildren()){
                        Adaptador objFilter = objFilaDatos.getValue(Adaptador.class);
                        listaTask.add(objFilter);
                    }
                    objListaAdaptadosTask= new listaAdaptadorTasks(TaskMain.this, listaTask);
                    recycleViewHospital.setAdapter(objListaAdaptadosTask);
                    recycleViewHospital.setHasFixedSize(true);
                    recycleViewHospital.setLayoutManager(new LinearLayoutManager(TaskMain.this));
                }else {
                    Toast.makeText(getApplicationContext(), "Error al filtrar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void CargarTask(){
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTask.clear();
                for (DataSnapshot objFilaDatos : dataSnapshot.getChildren()){
                    Adaptador objVehiculos = objFilaDatos.getValue(Adaptador.class);
                    listaTask.add(objVehiculos);
                }
                objListaAdaptadosTask= new listaAdaptadorTasks(TaskMain.this, listaTask);
                recycleViewHospital.setAdapter(objListaAdaptadosTask);
                recycleViewHospital.setHasFixedSize(true);
                recycleViewHospital.setLayoutManager(new LinearLayoutManager(TaskMain.this));
                objDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void buscar(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        buscar();
        objDialog.setMessage("Obteniendo Datos...");
        objDialog.show();
        CargarTask();
    }

    public void addCar(){
        startActivity(new Intent(getApplicationContext(), AddTask.class));
        finish();
    }

    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void openDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskMain.this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you Sure to Logout");
        builder.setPositiveButton("Yes, Logout", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logoutUser();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(TaskMain.this, "No Logout", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create();
        builder.show();
    }

    protected void onStart() {
        super.onStart();
        objFirebase.addAuthStateListener(objFirebaseListener);
        String Spinners = txtCriterioDoctor.getSelectedItem().toString();
        firebaseTaskFilter(Spinners);
    }

    @Override
    public void onStop() {
        super.onStop();
        TaskMain.this.finish();
        if (objFirebaseListener != null){
            objFirebase.removeAuthStateListener(objFirebaseListener);
        }
    }
}