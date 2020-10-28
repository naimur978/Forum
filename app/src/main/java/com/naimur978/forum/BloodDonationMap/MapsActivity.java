package com.naimur978.forum.BloodDonationMap;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
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
    public static Double lat = 0.0;
    public static Double lng = 0.0;

    private FragmentManager fm = null;
    private NoNet mNoNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fm = getSupportFragmentManager();
        mNoNet = new NoNet();
        mNoNet.initNoNet(this, fm);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        mNoNet.RegisterNoNet();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mNoNet.unRegisterNoNet();
        super.onPause();
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
        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        double userLat = location.getLatitude();
        double userLong = location.getLongitude();

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
