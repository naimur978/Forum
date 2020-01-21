package com.naimur978.forum.BloodDonationMap;


import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naimur978.forum.ChatActivity;
import com.naimur978.forum.R;
import com.naimur978.forum.ThereProfileActivity;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static Double lat=0.0;
    public static Double lng=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Chittagong, Bangladesh.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        ///////////////////

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // I suppressed the missing-permission warning because this wouldn't be executed in my
        // case without location services being enabled
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double userLat = lastKnownLocation.getLatitude();
        double userLong = lastKnownLocation.getLongitude();

        //////////////////////


        // Add a marker in Sydney and move the camera
        LatLng you = new LatLng(userLat, userLong);
        mMap.addMarker(new MarkerOptions().position(you).title("Your Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(you , 15.0f) );





        for(int i=0; i<DonorList.donorInfo.size(); i++){
            Log.d("Donor", String.valueOf(i));


            Donor donor = DonorList.donorInfo.get(i);


            Double lat = new Double(donor.lat);
            Double lng = new Double(donor.lan);

            Log.d("Donor", donor.lat);
            Log.d("Donor", donor.lan);


            LatLng donar = new LatLng(lat, lng);
            String donorName = donor.name+ " " + donor.contuctNumber;
            mMap.addMarker(new MarkerOptions().position(donar).title(donorName));

        }






    }
}
