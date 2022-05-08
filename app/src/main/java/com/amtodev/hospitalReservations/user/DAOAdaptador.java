package com.amtodev.hospitalReservations.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DAOAdaptador {

    private DatabaseReference databaseReference;

    public DAOAdaptador(){
        FirebaseDatabase db =FirebaseDatabase.getInstance("https://parcial-pama-default-rtdb.firebaseio.com/");
        databaseReference = db.getReference(Adaptador.class.getSimpleName());
    }

    public Task<Void> add(Adaptador adapter)
    {
        return databaseReference.push().setValue(adapter);
    }


    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

}
