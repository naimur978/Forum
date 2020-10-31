package com.naimur978.forum.BloodDonationMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naimur978.forum.R;

import static com.naimur978.forum.BloodDonationMap.FirstPageActivity.database;

public class DonorForm extends AppCompatActivity {
    Spinner cityChoice;
    Spinner groupChoice;
    String uid;

    TextView Name;
    EditText Mobile;

    Button Save;

    String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_form);

        cityChoice = (Spinner) findViewById(R.id.dropdownCity);

        String[] citis = new String[]{"Barisal","Chittagong", "Dhaka", "Mymensingh","Khulna", "Rajshahi", "Rangpur", "Sylhet"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, citis);
        cityChoice.setAdapter(adapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        groupChoice = (Spinner) findViewById(R.id.dropdownGroup);
        String[] group = new String[]{"O+","O-", "A+", "B+","A-", "B-", "AB+", "AB-"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, group);
        groupChoice.setAdapter(adapter1);


        Name = (TextView) findViewById(R.id.edt_name);
        Mobile = (EditText) findViewById(R.id.edt_mobileNumber);
        Save = (Button) findViewById(R.id.btn_saveDonor);


        //-----------------------------search by uid
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Users").child(uid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                Name.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
        //----------------------------------------------------------------------------------

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String name = Name.getText().toString();*/
/*
                String name = FirebaseDatabase.getInstance().getReference("Users").child("name").toString();
*/
                String city = cityChoice.getSelectedItem().toString();
                String group = groupChoice.getSelectedItem().toString();
                String mobile = Mobile.getText().toString();
                String lat = FirstPageActivity.lat.toString();
                String lng = FirstPageActivity.lng.toString();

                Donor donor = new Donor(uid,name,mobile,group,city,lat, lng);
                DatabaseReference myRef = database.getReference("donors");
                myRef.child(city).child(group).push().setValue(donor);

                finish();
            }
        });

    }
}
