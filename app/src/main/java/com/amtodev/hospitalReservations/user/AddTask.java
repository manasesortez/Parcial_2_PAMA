package com.amtodev.hospitalReservations.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amtodev.hospitalReservations.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String[] status = {
            "Estatus de la Tarea",
            "Pendiente",
            "En Proceso",
            "Terminada"
    };

    ImageView next_activity;
    EditText txtTarea, txtDescripcion, txtFecha, txtHora;
    Spinner txtStatus;
    Button btnSave;
    int t1Hora, t1Minute;

    private FirebaseAuth objFirebase;
    private FirebaseAuth.AuthStateListener objFirebaseListener;

    //database
    FirebaseDatabase objDataBase;
    DatabaseReference dbReference;

    FirebaseUser objUsuario;
    private ProgressDialog objDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        txtTarea = findViewById(R.id.txtTarea);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        txtStatus = findViewById(R.id.spinnerStatus);

        ArrayAdapter<String> elAdaptador = new ArrayAdapter<String>(this, R.layout.spinner_item, status);
        txtStatus.setAdapter(elAdaptador);

        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePicket();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        txtHora.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddTask.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int HourOfDay, int minute) {
                                t1Hora = HourOfDay;
                                t1Minute = minute;

                                String time = t1Hora + ":" + t1Minute;
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);

                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "hh:mm:aa"
                                    );
                                    txtHora.setText(f12Hours.format(date));
                                }catch(ParseException e){
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(t1Hora, t1Minute);
                timePickerDialog.show();
            }
        });

        btnSave = findViewById(R.id.btnSave);

        objDialog = new ProgressDialog(this);

        next_activity = findViewById(R.id.next_activity);
        next_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMain();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarVehiculo();
            }
        });

        objFirebase = FirebaseAuth.getInstance();

        objFirebaseListener = new FirebaseAuth.AuthStateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                objUsuario = firebaseAuth.getCurrentUser();
                if (objUsuario == null){
                    logoutUser();
                }
            }
        };

        FirebaseApp.initializeApp(AddTask.this);
        objDataBase = FirebaseDatabase.getInstance("https://parcial-pama-default-rtdb.firebaseio.com/");
        dbReference = objDataBase.getReference();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        txtFecha.setText(currentDateString);
    }

    private void registrarVehiculo() {
        objDialog.setMessage("Registrando Datos...");
        objDialog.show();
        String Spinners = txtStatus.getSelectedItem().toString();
        Adaptador objVehiculo = new Adaptador();
        objVehiculo.setId_reserva(UUID.randomUUID().toString());
        objVehiculo.setTarea(txtTarea.getText().toString());
        objVehiculo.setDescripcion(txtDescripcion.getText().toString());
        objVehiculo.setFecha(txtFecha.getText().toString());
        objVehiculo.setHora(txtHora.getText().toString());
        objVehiculo.setStatus(Spinners.toString());
        objVehiculo.setUsuario(objUsuario.getEmail());
        dbReference.child("tblTarea").child(objVehiculo.getId_reserva()).setValue(objVehiculo);
        objDialog.dismiss();
        Toast.makeText(AddTask.this, "Datos Registrados correctament", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void openMain(){
        startActivity(new Intent(getApplicationContext(), TaskMain.class));
        finish();
    }

    public void logoutUser(){
       Intent objVentana = new Intent(AddTask.this, TaskMain.class);
       startActivity(objVentana);
       this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        objFirebase.addAuthStateListener(objFirebaseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (objFirebaseListener != null){
            objFirebase.removeAuthStateListener(objFirebaseListener);
        }
    }
}