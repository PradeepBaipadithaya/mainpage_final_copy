package com.awatar.mainpage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder> {
    Context context;
    private DatabaseReference databaseReference;
//    Task<Void> databaseReference1;
//    Task<Void> databaseReference2;
//    Task<Void> databaseReference3;

    firebase fb =new firebase("conductors");
//    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reff=FirebaseDatabase.getInstance().getReferenceFromUrl("https://mainpage-1398d-default-rtdb.firebaseio.com/").child("conductors");
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mainpage-1398d-default-rtdb.firebaseio.com/");
    ArrayList<ContactModel> arrcont;
//    DatabaseReference reff=FirebaseDatabase.getInstance().getReference();
    RecyclerContactAdapter(Context context, ArrayList<ContactModel> arrcont){
        this.arrcont=arrcont;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(context).inflate(R.layout.contact_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(arrcont.get(position).name);
        int pos=holder.getAdapterPosition();
        holder.email.setText(arrcont.get(pos).email);
        holder.pass.setText(arrcont.get(pos).paswd);
        holder.llrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_layout);

                EditText name, email, pass;
                Button btn;
                email = dialog.findViewById(R.id.ipemail);
                name = dialog.findViewById(R.id.ipname);
                pass = dialog.findViewById(R.id.ippass);
                btn = dialog.findViewById(R.id.Done);
                TextView textView = dialog.findViewById(R.id.updatetext);
                btn.setText("Update");
                textView.setText("Update Conductor");

            pass.setText((arrcont.get(pos)).paswd);
            Toast.makeText(context, ""+arrcont, Toast.LENGTH_SHORT).show();
            name.setText((arrcont.get(pos)).email);
//            pass.setVisibility(View.GONE);
            email.setText((arrcont.get(pos)).name);
//                pass.setText("" + phno_get);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email1 = email.getText().toString();
                        String name1 = name.getText().toString();
                        String pass1 = pass.getText().toString();
                        if (name1.isEmpty() || email1.isEmpty()) {
                            Toast.makeText(context, "Please Fill The Box To Add", Toast.LENGTH_SHORT).show();
                        } else {
                            myRef.child("conductors").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(pass1)) {
                                        Toast.makeText(context, "Conductor's number cannot be changed try deleting account ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        database_handler tcd = new database_handler(email1, name1, pass1);
                                        fb.add(tcd, "" + pass1)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(context, "Record Updated", Toast.LENGTH_SHORT).show();
                                                        ((Activity)context).finish();
//                                                        dialog.cancel();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Email has already registered", Toast.LENGTH_SHORT).show();

                                }

                            });
                        }
                    }
                });
                dialog.show();
            }
        });
        holder.llrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(context).setTitle("Delete Conductor").setMessage("Are You Sure you want to delete").setIcon(R.drawable.ic_person_remove).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nameing = (arrcont.get(pos)).paswd;
                        fb.remove( "" + nameing)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        arrcont.remove(pos);
                        notifyItemRemoved(pos);
                    }

                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrcont.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,email,pass;
        LinearLayout llrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            email=itemView.findViewById(R.id.email);
            pass=itemView.findViewById(R.id.pass);
            llrow=itemView.findViewById(R.id.llrow);
        }
    }

}