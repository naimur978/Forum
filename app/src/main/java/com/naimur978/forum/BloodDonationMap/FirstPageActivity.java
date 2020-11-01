package com.naimur978.forum.BloodDonationMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naimur978.forum.R;

import static android.location.LocationManager.GPS_PROVIDER;

public class FirstPageActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static FirebaseDatabase database;

    Button buttonDonor;
    Button buttonInfo;
    Button needBlood;

    public static String donorId="no";
    SharedPreferences sharedPreferences;

    public static Double lat = 22.341900;
    public static Double lng = 91.815536 ;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;

    GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    LocationListener locationListener;

    private FragmentManager fm = null;
    private NoNet mNoNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        fm = getSupportFragmentManager();
        mNoNet = new NoNet();
        mNoNet.initNoNet(this, fm);

        // Connecting to the database
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("donors");

        /**
         * Wiring up every thing
         */


        buttonInfo = (Button) findViewById(R.id.btn_info);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstPageActivity.this, Information.class));
            }
        });

        needBlood = (Button) findViewById(R.id.btn_need_blood);
        needBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstPageActivity.this, NeedBlood.class));
            }
        });


        buttonDonor = (Button) findViewById(R.id.btn_donor_profile);
        if(donorId.toString().equals("no")){
            buttonDonor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FirstPageActivity.this, DonorForm.class));
                }
            });
        } else {

        }

        /**
         * Initializing variable
         */
        try{
            donorId = sharedPreferences.getString("id","no");

        } catch (Exception e){
            e.printStackTrace();
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(GPS_PROVIDER)){
            Toast.makeText(FirstPageActivity.this, "Please Turn on Location", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            FirstPageActivity.this.startActivity(myIntent);
        }


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            /**
             * Crating a location request
             */



            ActivityCompat.requestPermissions(FirstPageActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);



            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 1, locationListener);





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

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
