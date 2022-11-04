package com.awatar.mainpage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.File;
import java.io.FileWriter;

public class conductor_location extends AppCompatActivity {
    private static int MY_FINE_LOCATION_REQUEST = 99;
    private static int MY_BACKGROUND_LOCATION_REQUEST = 100;

    LocationService mLocationService = new LocationService();
    Intent mServiceIntent;


    Button startServiceBtn, stopServiceBtn;
    Context context;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor_location);

        startServiceBtn = findViewById(R.id.start_service_btn_1);
        stopServiceBtn = findViewById(R.id.stop_service_btn_1);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception ex){}
        if(!gps_enabled && !network_enabled){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
            /**************/

            startServiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (ActivityCompat.checkSelfPermission(conductor_location.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                            if (ActivityCompat.checkSelfPermission(conductor_location.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {


                                AlertDialog alertDialog = new AlertDialog.Builder(conductor_location.this).create();
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


                            } else if (ActivityCompat.checkSelfPermission(conductor_location.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                starServiceFunc();
                            }
                        } else {
                            starServiceFunc();
                        }

                    } else if (ActivityCompat.checkSelfPermission(conductor_location.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(conductor_location.this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                            AlertDialog alertDialog = new AlertDialog.Builder(conductor_location.this).create();
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

                }
            });

            stopServiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopServiceFunc();
                }
            });

        }



            /***********/


            @Override
            public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults){
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

                Toast.makeText(this, Integer.toString(requestCode), Toast.LENGTH_LONG).show();

                if (requestCode == MY_FINE_LOCATION_REQUEST) {

                    if (grantResults.length != 0 /*grantResults.isNotEmpty()*/ && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            requestBackgroundLocationPermission();
                        }

                    } else {
                        Toast.makeText(this, "ACCESS_FINE_LOCATION permission denied", Toast.LENGTH_LONG).show();
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                 /*   startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", this.getPackageName(), null),),);*/

                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:com.example.locationupdates")
                            ));

                        }
                    }
                    return;

                } else if (requestCode == MY_BACKGROUND_LOCATION_REQUEST) {

                    if (grantResults.length != 0 /*grantResults.isNotEmpty()*/ && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Background location Permission Granted", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Background location permission denied", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }

            private void starServiceFunc () {
                mLocationService = new LocationService();
                mServiceIntent = new Intent(this, mLocationService.getClass());
                if (!Util.isMyServiceRunning(mLocationService.getClass(), this)) {
//                    Intent intent = getIntent();
                    String id = getIntent().getStringExtra("conductor_email");
                    mServiceIntent.putExtra("conductor_email", id);
                    Toast.makeText(conductor_location.this, ""+id, Toast.LENGTH_SHORT).show();
                    mServiceIntent.putExtra("role", "Conductor");
                    startService(mServiceIntent);
                    Toast.makeText(this, getString(R.string.service_start_successfully), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.service_already_running), Toast.LENGTH_SHORT).show();
                }
            }

            private void stopServiceFunc () {
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

            private void requestBackgroundLocationPermission () {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        MY_BACKGROUND_LOCATION_REQUEST);
            }

            private void requestFineLocationPermission () {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, MY_FINE_LOCATION_REQUEST);
            }

            public void saveLocation () {
                File dir = new File(this.getFilesDir(), "trickyworld");
                if (!dir.exists()) {
                    dir.mkdir();
                }

                try {
                    File userLocation = new File(dir, "userlocation.txt");
                    FileWriter writer = new FileWriter(userLocation);
                    writer.append(LocationService.locationArrayList.toString());
                    writer.flush();
                    writer.close();
                    LocationService.locationArrayList.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }




}
