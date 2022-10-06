package com.awatar.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class conductor_login extends AppCompatActivity {
    EditText e1,e2;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mainpage-1398d-default-rtdb.firebaseio.com/");
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor_login);
        e1=findViewById(R.id.conductoremail);
        e2=findViewById(R.id.coductorpass);
        b=findViewById(R.id.tripbtn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String emailid = e1.getText().toString();
                    String passwd = e2.getText().toString();
                    myRef.child("conductors").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(emailid)) {
                                final String getpass = snapshot.child(emailid).child("password").getValue(String.class);
                                if (getpass.equals(passwd)) {
                                    Toast.makeText(conductor_login.this, "Loged in", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(conductor_login.this, conductor_location.class);
                                    intent.putExtra("conductor_email", emailid);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(conductor_login.this, "Password is not correct", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(conductor_login.this, "Email id not registered", Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(conductor_login.this, "Email id not registered", Toast.LENGTH_SHORT).show();

                        }

                    });
                } catch (Exception e) {
                    Toast.makeText(conductor_login.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}