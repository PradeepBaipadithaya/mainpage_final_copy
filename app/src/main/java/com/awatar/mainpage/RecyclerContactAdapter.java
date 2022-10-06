package com.awatar.mainpage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
    Task<Void> databaseReference1;
    Task<Void> databaseReference2;
    Task<Void> databaseReference3;
    ArrayList<ContactModel> arrcont;
    DatabaseReference reff=FirebaseDatabase.getInstance().getReference();
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
                EditText name=dialog.findViewById(R.id.ipemail);
                EditText email=dialog.findViewById(R.id.ipname);
                EditText pass=dialog.findViewById(R.id.ippass);
                TextView textView=dialog.findViewById(R.id.updatetext);
                Button btn=dialog.findViewById(R.id.Done);
                btn.setText("Update");
                textView.setText("Update Conductor");
                name.setText((arrcont.get(pos)).name);
                pass.setVisibility(View.GONE);
                email.setText((arrcont.get(pos)).email);
                pass.setText((arrcont.get(pos)).paswd);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name1=name.getText().toString();
                        String email1=email.getText().toString();
                        String pass1=pass.getText().toString();
                        HashMap conductors=new HashMap();
                        conductors.put("email",email1);
                        conductors.put("name",name1);
                        conductors.put("password",pass1);

                        if(name1.isEmpty()  || pass1.isEmpty()){
                            Toast.makeText(context, "Please Fill The Box To Add", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            try{
                                databaseReference1=FirebaseDatabase.getInstance().getReference().child("conductors").child(pass1).child("phno").setValue(pass1);
                                //databaseReference2=FirebaseDatabase.getInstance().getReference().child("tripcollectorsinfo").child(email1).getKey();
                                databaseReference3=FirebaseDatabase.getInstance().getReference().child("conductors").child(pass1).child("name").setValue(name1);
                                databaseReference2=FirebaseDatabase.getInstance().getReference().child("conductors").child(pass1).child("password").setValue(email1);
                                arrcont.set(pos,new ContactModel(name1,email1,pass1));
                                notifyItemChanged(pos);
                                Toast.makeText(context, "Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }catch (Exception e){
                                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();

                            }


                        }

                    }
                });
                dialog.show();
            }
        });
        holder.llrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(context).setTitle("Delete Conductor").setMessage("Are You Sue  you want to delete").setIcon(R.drawable.ic_person_remove).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nameing=arrcont.get(pos).paswd;
                        DatabaseReference myReff = FirebaseDatabase.getInstance().getReference().child("conductors");
                        Query email=myReff.child(nameing);
                        myReff.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data:snapshot.getChildren()) {
                                    email.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted From Conductors: "+nameing, Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("TAG","On cancelled",error.toException());
                            }

                        });
                        Toast.makeText(context, nameing, Toast.LENGTH_SHORT).show();
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