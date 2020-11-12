package com.naimur978.forum.BloodDonationMap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naimur978.forum.ChatActivity;
import com.naimur978.forum.R;
import com.naimur978.forum.TheirProfileActivity;

import java.util.ArrayList;

public class DonorList extends AppCompatActivity {
    String city;
    String group;
    ArrayList<String> donorList;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    public static ArrayList<DonorModel> donorInfo;
    Button buttonMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list);

        Bundle extras = getIntent().getExtras();
        city = extras.getString("city");
        group = extras.getString("group");
        Log.i("NAME",city);
        Log.i("NAME",group);

        donorList = new ArrayList<>();
        donorInfo = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_donor);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, donorList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int p, long l) {
                for(int i=0; i<DonorList.donorInfo.size(); i++){
                    final String uid = DonorList.donorInfo.get(i).getUid();
                    //alert dialog to choose chat or post
                    AlertDialog.Builder builder = new AlertDialog.Builder(DonorList.this);
                    builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                //profile clicked
                                Intent intent = new Intent(DonorList.this, TheirProfileActivity.class);
                                intent.putExtra("uid",uid);
                                DonorList.this.startActivity(intent);
                                dialogInterface.cancel();
                            }
                            if(i==1){
                                //chat clicked
                                Intent intent = new Intent(DonorList.this, ChatActivity.class);
                                intent.putExtra("hisUid", uid);
                                DonorList.this.startActivity(intent);
                                dialogInterface.cancel();
                            }

                        }
                    });
                    builder.create().show();
                }
            }
        });








        buttonMap = (Button) findViewById(R.id.Button_mapShow);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonorList.this, MapsActivity.class));
            }
        });



        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("donors");
        myRef.child(city).child(group).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DonorModel donor = dataSnapshot.getValue(DonorModel.class);
                donorInfo.add(donor);
                String donorInfo = donor.name + "   \n" + donor.contuctNumber;
                donorList.add(donorInfo);
                arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
