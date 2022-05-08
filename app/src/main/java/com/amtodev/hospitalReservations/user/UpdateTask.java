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
import java.util.HashMap;
import java.util.UUID;

public class UpdateTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String[] status = {
            "Estatus de la Tarea",
            "Pendiente",
            "En Proceso",
            "Terminada"
    };

    ImageView next_activityMain;
    EditText txtTareaUpdate, txtDescripcionUpdate, txtFechaUpdate, txtHoraUpdate;
    Spinner txtStatusUpdate;
    Button btnSaveUpdate;
    int t1Hora, t1Minute;
    DAOAdaptador dao = new DAOAdaptador();

    private FirebaseAuth objFirebase;
    private FirebaseAuth.AuthStateListener objFirebaseListener;

    //database
    FirebaseDatabase objDataBase;
    DatabaseReference dbReference;

    FirebaseUser objUsuario;
    private ProgressDialog objDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        txtTareaUpdate = findViewById(R.id.txtTareaUpdate);
        txtDescripcionUpdate = findViewById(R.id.txtDescripcionUpdate);
        txtFechaUpdate = findViewById(R.id.txtFechaUpdate);
        txtHoraUpdate = findViewById(R.id.txtHoraUpdate);
        txtStatusUpdate = findViewById(R.id.spinnerStatusUpdate);

        ArrayAdapter<String> elAdaptador = new ArrayAdapter<String>(this, R.layout.spinner_item, status);
        txtStatusUpdate.setAdapter(elAdaptador);

        txtFechaUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePicket();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        txtHoraUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        UpdateTask.this,
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
                                    txtHoraUpdate.setText(f12Hours.format(date));
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

        //objDialog = new ProgressDialog(this);
        next_activityMain = findViewById(R.id.next_activityMain);
        next_activityMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMain();
            }
        });

        btnSaveUpdate = findViewById(R.id.btnSaveUpdate);
        btnSaveUpdate.setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Adaptador adap_edit = (Adaptador)getIntent().getSerializableExtra("edit");
            String Spinners = txtStatusUpdate.getSelectedItem().toString();
            if(adap_edit != null){
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("tarea", txtTareaUpdate.getText().toString());
                hashMap.put("descripcion", txtDescripcionUpdate.getText().toString());
                hashMap.put("fecha", txtFechaUpdate.getText().toString());
                hashMap.put("hora", txtHoraUpdate.getText().toString());
                hashMap.put("status", Spinners.toString());
                hashMap.put("usuario", auth.getCurrentUser().getEmail().toString());

                dao.update(adap_edit.getId_reserva(), hashMap).addOnSuccessListener(suc ->{
                    Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(error ->{
                    Toast.makeText(this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

        Adaptador adap_edit = (Adaptador)getIntent().getSerializableExtra("edit");
        if(adap_edit != null){
            btnSaveUpdate.setText("Actualizar");
            txtTareaUpdate.setText(adap_edit.getTarea());
            txtDescripcionUpdate.setText(adap_edit.getDescripcion());
            txtFechaUpdate.setText(adap_edit.getFecha());
            txtHoraUpdate.setText(adap_edit.getHora());
        }

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

        FirebaseApp.initializeApp(UpdateTask.this);
        objDataBase = FirebaseDatabase.getInstance("https://parcial-pama-default-rtdb.firebaseio.com/");
        dbReference = objDataBase.getReference();
    }

    public void openMain(){
        startActivity(new Intent(getApplicationContext(), TaskMain.class));
        finish();
    }

    public void logoutUser(){
        Intent objVentana = new Intent(UpdateTask.this, TaskMain.class);
        startActivity(objVentana);
        this.finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        txtFechaUpdate.setText(currentDateString);
    }
}