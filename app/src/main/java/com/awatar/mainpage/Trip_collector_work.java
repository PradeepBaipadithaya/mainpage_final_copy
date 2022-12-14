package com.awatar.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Trip_collector_work extends AppCompatActivity {
    DatabaseReference reference;
    DatabaseReference reference_busnum;
    DatabaseReference reference_conducortname;
    DatabaseReference reference_conducortnum;
    RecyclerView recyclerView;
    static ArrayList<ContactModel> arrcont= new ArrayList<>();


    FloatingActionButton mAddAlarmFab, mAddPersonFab,mupdate;

    firebase fb =new firebase("tripcollectorsinfo");
    ExtendedFloatingActionButton mAddFab;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mainpage-1398d-default-rtdb.firebaseio.com/");

    TextView addAlarmActionText, addPersonActionText,updatetext;

    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_collector_work);




        mAddFab = findViewById(R.id.add_fab);
        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
        mAddPersonFab = findViewById(R.id.add_person_fab);
        mupdate=findViewById(R.id.update_person);
        updatetext=findViewById(R.id.add_person_action_textt);
        addAlarmActionText = findViewById(R.id.add_alarm_action_text);

        addPersonActionText = findViewById(R.id.add_person_action_text);
        mupdate.setVisibility(View.GONE);
        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);
        updatetext.setVisibility(View.GONE);
        isAllFabsVisible = false;
        mAddFab.shrink();


        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isAllFabsVisible) {

                            mAddAlarmFab.show();
                            mupdate.show();
                            mAddPersonFab.show();
                            addAlarmActionText.setVisibility(View.VISIBLE);
                            updatetext.setVisibility(View.VISIBLE);
                            addPersonActionText.setVisibility(View.VISIBLE);
                            mAddFab.extend();


                            isAllFabsVisible = true;
                        } else {


                            mAddAlarmFab.hide();
                            mAddPersonFab.hide();
                            mupdate.hide();
                            addAlarmActionText.setVisibility(View.GONE);
                            addPersonActionText.setVisibility(View.GONE);
                            updatetext.setVisibility(View.GONE);

                            mAddFab.shrink();

                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddPersonFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Trip_collector_work.this, "Person Added", Toast.LENGTH_SHORT).show();
                    }
                });
        mAddAlarmFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Trip_collector_work.this, "Long Press On What You Want To Delete", Toast.LENGTH_SHORT).show();
                    }
                });
        mupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(Trip_collector_work.this, "Click on What You Want To Delete", Toast.LENGTH_SHORT).show();
            }
        });


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyler3);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecyclerContactAdapter_Trip_Collector_work  adapter = new RecyclerContactAdapter_Trip_Collector_work(this,arrcont);
        recyclerView.setAdapter(adapter);
        DatabaseReference myReff = FirebaseDatabase.getInstance().getReference().child("tripcollectorsinfo");
        myReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()) {
                    String abc=data.getKey();
                    Log.d("TAG",abc);
                    String detail[] = new String[3];
                    int i=0;
                    for (DataSnapshot data2:snapshot.child(abc).getChildren()) {
                        String abcd = data2.getValue().toString();
                        detail[i++]=abcd;
                        if(i==3){
                            arrcont.add(new ContactModel(detail[0],detail[1],detail[2]));
                            i=0;
                        }
                    }
                }
                adapter.notifyItemInserted(arrcont.size()-1);
                recyclerView.scrollToPosition(arrcont.size()-1);
                arrcont= new ArrayList<>();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAddPersonFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(Trip_collector_work.this);
                dialog.setContentView(R.layout.add_layout_2);

                EditText name,email,pass;
                Button btn;
                email=dialog.findViewById(R.id.ipname);
                name=dialog.findViewById(R.id.ipemail);
                pass=dialog.findViewById(R.id.ippass);
                btn=dialog.findViewById(R.id.Done);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name1=name.getText().toString();
                        String email1=email.getText().toString();
                        String pass1=pass.getText().toString();
                        String key = name1+"_"+pass1;

                        if(name1.isEmpty() || email1.isEmpty() || pass1.isEmpty()){
                            Toast.makeText(Trip_collector_work.this, "Please Fill The Box To Add", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            myRef.child(""+key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(name1)){
                                        Toast.makeText(Trip_collector_work.this, "This number is already registered ", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        //Updating database
                                        database_handler tcd = new database_handler(name1,email1,pass1);
                                        fb.add(tcd,key).addOnSuccessListener(suc->
                                        {
                                            Toast.makeText(Trip_collector_work.this, "Record Inserted", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }).addOnFailureListener(er->{
                                            Toast.makeText(Trip_collector_work.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Trip_collector_work.this, "Email has already registered", Toast.LENGTH_SHORT).show();

                                }

                            });


                        }

                    }
                });
                dialog.show();

            }
        });
    }
}