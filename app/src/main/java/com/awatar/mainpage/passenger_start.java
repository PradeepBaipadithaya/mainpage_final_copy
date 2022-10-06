package com.awatar.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

public class passenger_start extends AppCompatActivity {
    private static int MY_FINE_LOCATION_REQUEST = 99;
    private static int MY_BACKGROUND_LOCATION_REQUEST = 100;
    public DatabaseReference reference_lat;

    LocationService mLocationService = new LocationService();
    Intent mServiceIntent;
    Intent intent;

    Button startServiceBtn, stopServiceBtn,map_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_start);
//        startServiceBtn = findViewById(R.id.start_service_btn_1);
        stopServiceBtn = findViewById(R.id.stop_map);
        map_start = findViewById(R.id.map_button);
        String conductor_bus_num = getIntent().getStringExtra("conductor_bus_num");
        reference_lat = FirebaseDatabase.getInstance("https://mainpage-1398d-default-rtdb.firebaseio.com/").getReference("Location details").child("Conductor").child(conductor_bus_num);

        map_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(passenger_start.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                            if (ActivityCompat.checkSelfPermission(passenger_start.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {


                                AlertDialog alertDialog = new AlertDialog.Builder(passenger_start.this).create();
                                alertDialog.setTitle("Background permission");
                                alertDialog.setMessage(getString(R.string.background_location_permission_message));

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Start service anyway",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                starServiceFunc();
                                                dialog.dismiss();
                                            }
                                        });

                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Grant background Permission",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestBackgroundLocationPermission();
                                                dialog.dismiss();
                                            }
                                        });

                                alertDialog.show();


                            } else if (ActivityCompat.checkSelfPermission(passenger_start.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                starServiceFunc();
                            }
                        } else {
                            starServiceFunc();
                        }

                    } else if (ActivityCompat.checkSelfPermission(passenger_start.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(passenger_start.this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                            AlertDialog alertDialog = new AlertDialog.Builder(passenger_start.this).create();
                            alertDialog.setTitle("ACCESS_FINE_LOCATION");
                            alertDialog.setMessage("Location permission required");

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestFineLocationPermission();
                                            dialog.dismiss();
                                        }
                                    });


                            alertDialog.show();

                        } else {
                            requestFineLocationPermission();
                        }
                    }


                    Intent intent = new Intent(passenger_start.this, passenger_locate_.class);
                    intent.putExtra("conductor_bus_num", conductor_bus_num);
                    startActivity(intent);



                } catch (Exception e) {
                    Toast.makeText(passenger_start.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopServiceFunc();
            }
        });

    }

        private void starServiceFunc(){
            mLocationService = new LocationService();
            mServiceIntent = new Intent(this, mLocationService.getClass());
            if (!Util.isMyServiceRunning(mLocationService.getClass(), this)) {
                Intent intent = getIntent();
//                String id = intent.getStringExtra("conductor_email");
                String id = "testing";
                mServiceIntent.putExtra("conductor_email",id);
                mServiceIntent.putExtra("role","Passenger");
                startService(mServiceIntent);
                Toast.makeText(this, getString(R.string.service_start_successfully), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.service_already_running), Toast.LENGTH_SHORT).show();
            }
        }

        private void stopServiceFunc(){
            mLocationService = new LocationService();
            mServiceIntent = new Intent(this, mLocationService.getClass());
            if (Util.isMyServiceRunning(mLocationService.getClass(), this)) {
                stopService(mServiceIntent);
                Toast.makeText(this, "Service stopped!!", Toast.LENGTH_SHORT).show();
                //saveLocation(); // explore it by your self
            } else {
                Toast.makeText(this, "Service is already stopped!!", Toast.LENGTH_SHORT).show();
            }
        }

        private void requestBackgroundLocationPermission() {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    MY_BACKGROUND_LOCATION_REQUEST);
        }

        private void requestFineLocationPermission() {
            ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, MY_FINE_LOCATION_REQUEST);
        }

        public void saveLocation(){
            File dir = new File(this.getFilesDir(), "trickyworld");
            if(!dir.exists()){
                dir.mkdir();
            }

            try {
                File userLocation = new File(dir, "user_location.txt");
                FileWriter writer = new FileWriter(userLocation);
                writer.append(LocationService.locationArrayList.toString());
                writer.flush();
                writer.close();
                LocationService.locationArrayList.clear();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

}