package com.amtodev.hospitalReservations.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.type.DateTime;

import java.io.Serializable;
import java.util.Date;

public class Adaptador implements Parcelable, Serializable {
    private String id_reserva;
    private String tarea;
    private String descripcion;
    private String fecha;
    private String hora;
    private String status;
    private String usuario;

    public Adaptador(){}

    protected Adaptador(Parcel in) {
        id_reserva = in.readString();
        tarea = in.readString();
        descripcion = in.readString();
        fecha = in.readString();
        hora = in.readString();
        status = in.readString();
        usuario = in.readString();
    }

    public static final Creator<Adaptador> CREATOR = new Creator<Adaptador>() {
        @Override
        public Adaptador createFromParcel(Parcel in) {
            return new Adaptador(in);
        }

        @Override
        public Adaptador[] newArray(int size) {
            return new Adaptador[size];
        }
    };

    public void setId_reserva(String id_reserva) {
        this.id_reserva = id_reserva;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getId_reserva() {
        return id_reserva;
    }

    public String getTarea() {
        return tarea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getStatus() {
        return status;
    }

    public String getUsuario() {
        return usuario;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_reserva);
        parcel.writeString(tarea);
        parcel.writeString(descripcion);
        parcel.writeString(fecha);
        parcel.writeString(hora);
        parcel.writeString(status);
        parcel.writeString(usuario);
    }

    @Override
    public String toString() {
        return "Adaptador{" +
                "id_reserva='" + id_reserva + '\'' +
                ", tarea='" + tarea + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", status='" + status + '\'' +
                ", usuario='" + usuario + '\'' +
                '}';
    }

    public Adaptador(String id_reserva, String tarea, String descripcion, String fecha, String hora, String status, String usuario) {
        this.id_reserva = id_reserva;
        this.tarea = tarea;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.status = status;
        this.usuario = usuario;
    }
}
