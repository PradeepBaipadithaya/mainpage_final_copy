package com.awatar.mainpage;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.awatar.mainpage.databinding.ActivityPassengerLocate2Binding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

public class passenger_locate_ extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityPassengerLocate2Binding binding;
    public DatabaseReference reference_bus;
    public DatabaseReference reference_user;

    Marker myMarker;
    Marker myMarker_user;
    public double locationLat=12.132;
    public double locationLong=75.8921;
    public double locationLat_user=19.132;
    public double locationLong_user=60.21;

    Polyline polyline;

    public int num;
    float[] value = new float[100];

    ArrayList dist_set = new ArrayList();
    ArrayList time = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPassengerLocate2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String conductor_bus_num = getIntent().getStringExtra("conductor_bus_num");
//        user_lat = getIntent().getStringExtra("user_lat");
//        user_long = getIntent().getStringExtra("user_long");
//
//        Toast.makeText(passenger_locate_.this, ""+user_lat, Toast.LENGTH_SHORT).show();
//        Toast.makeText(passenger_locate_.this, ""+user_long, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, ""+locationLat, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, ""+locationLong, Toast.LENGTH_SHORT).show();

        reference_bus = FirebaseDatabase.getInstance("https://mainpage-1398d-default-rtdb.firebaseio.com/").getReference("Location details").child("Conductor").child(conductor_bus_num);
        reference_user = FirebaseDatabase.getInstance("https://mainpage-1398d-default-rtdb.firebaseio.com/").getReference("Location details").child("Passenger").child("testing");
        try {
            read_user_location();
            read_bus_location();
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void read_user_location() {
        try {
            reference_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String values[] = new String[2];
                    int i=0;
                    for (DataSnapshot data:snapshot.getChildren()) {
                        String abc=data.getValue().toString();
                        values[i++]=abc;
                        if(i==2){
                            locationLat_user = Double.parseDouble(values[0]);
                            locationLong_user = Double.parseDouble(values[1]);
                            myMarker_user.setPosition(new LatLng(locationLat_user, locationLong_user));
//                            Toast.makeText(passenger_locate.this, ""+locationLat, Toast.LENGTH_SHORT).show();
                            i=0;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(passenger_locate_.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(passenger_locate_.this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void read_bus_location() {
        try {
            final boolean[] flag = {true};

            reference_bus.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String values[] = new String[2];
                    int i=0;
                    if(flag[0]){
                        rout();
                        flag[0] =false;
                    }
                    polyline.remove();
                    for (DataSnapshot data:snapshot.getChildren()) {
                        String abc=data.getValue().toString();
                        values[i++]=abc;
                        if(i==2){
                            locationLat = Double.parseDouble(values[0]);
                            locationLong = Double.parseDouble(values[1]);
                            myMarker.setPosition(new LatLng(locationLat, locationLong));
                            rout();
//                            Toast.makeText(passenger_locate.this, ""+locationLat, Toast.LENGTH_SHORT).show();
                            i=0;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(passenger_locate_.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(passenger_locate_.this, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_top));
        markerOptions.anchor((float) 0.5,(float) 0.5);
        LatLng currentLocation = new LatLng(locationLat, locationLong);
        LatLng currentLocation_user = new LatLng(locationLat_user, locationLat_user);
        myMarker = mMap.addMarker(markerOptions.position(currentLocation).title("Bus Location"));
        myMarker_user = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
        googleMap.setOnMarkerClickListener(this);
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(locationLat, locationLong));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
//        read_location();
    }

    private void rout() {
        List<LatLng> path = new ArrayList();
try {
        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAGNT9WwAdNDz1o8P3Xrb0arxptCDVp1gM")
                .build();
//        String lat_user =Double.toString(locationLat_user);
//        String long_user =Double.toString(locationLong_user);
        String bus_loc = ""+locationLat+","+""+locationLong;
        String user_loc = ""+locationLat_user+","+""+locationLong_user;
//        Toast.makeText(this, ""+locationLat_user, Toast.LENGTH_LONG).show();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, ""+user_loc, ""+bus_loc);
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, "12.32421,75.23423", "13.231231,76.23421");
            DirectionsResult res = req.await();
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
//                Toast.makeText(this, ""+route, Toast.LENGTH_SHORT).show();
                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        num = route.legs.length;
                        DirectionsLeg leg = route.legs[i];
                        try {
                            String s = leg.distance.humanReadable;
                            String time_val = leg.duration.humanReadable;
//                            String dist;
                            String[] dist =s.split(" ");
//                            Toast.makeText(this, ""+time_val, Toast.LENGTH_SHORT).show();
                            dist_set.add(i,Double.parseDouble(dist[0]));
                            time.add(i,time_val);
                        }catch (Exception e){
                            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                        if (leg.steps != null) {
//                            Toast.makeText(this, ""+leg.steps.length, Toast.LENGTH_LONG).show();
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
//                                            String s =step.distance.humanReadable;
                                        }
                                    }

                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(7);
            polyline =mMap.addPolyline(opts);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
} catch(Exception ex) {
    Toast.makeText(passenger_locate_.this, ""+ex, Toast.LENGTH_SHORT).show();
}
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.equals(myMarker))
        {
            myMarker.setTitle("Bus Location");
            myMarker.setSnippet("Distance: "+dist_set.get(num)+", "+"Expected Time: "+time.get(num));
        }
        if (marker.equals(myMarker_user))
        {
            myMarker_user.setTitle("Your location");
        }
        return false;
    }
}