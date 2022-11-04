package com.awatar.mainpage;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class firebase {
    Context context;
    private DatabaseReference databaseReference;

    public firebase(String path) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReferenceFromUrl("https://mainpage-1398d-default-rtdb.firebaseio.com/").child("" + path);//for connectiom
//        DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mainpage-1398d-default-rtdb.firebaseio.com/");
    }

    public Task<Void> add(database_handler atc, String phno) {
        return databaseReference.child("" + phno).setValue(atc);
//        return databaseReference.child(""+phno).setValue(null);
    }

    public Task<Void> updated(String key, HashMap<String, Object> hashMap) {
//        return databaseReference.child(""+key).setValue(hashMap);
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
//        return databaseReference.child(""+key).setValue(hashMap);
        return databaseReference.child(key).removeValue();
    }


}
