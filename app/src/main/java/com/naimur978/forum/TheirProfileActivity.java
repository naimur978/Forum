package com.naimur978.forum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naimur978.forum.Adapters.AdapterPosts;
import com.naimur978.forum.Models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TheirProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    RecyclerView postsRecyclerView;

    List<ModelPost> postList;
    AdapterPosts adapterPosts;

    String uid;

    ImageView avatarIv, coverIv;
    TextView nameTv,emailTv, phoneTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        avatarIv = findViewById(R.id.avatarIv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        coverIv = findViewById(R.id.coverIv);


        postsRecyclerView = findViewById(R.id.recylerview_posts);

        firebaseAuth=FirebaseAuth.getInstance();


        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("Profile");
       // toolbarTitle.setText("Add New Post");
        //toolbarCounter.setText("forum");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        //find info by email
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot ds : dataSnapshot.getChildren()){

                String name = "" + ds.child("name").getValue();
                String email = "" + ds.child("email").getValue();
                String phone = "" + ds.child("phone").getValue();
                String image = "" + ds.child("image").getValue();
                String cover = "" + ds.child("cover").getValue();

                //set data
                nameTv.setText(name);
                emailTv.setText(email);
                phoneTv.setText(phone);

                try {
                    Picasso.get().load(image).into(avatarIv);
                }catch (Exception e){
                    Picasso.get().load(R.drawable.ic_image_white).into(avatarIv);
                }

                try {
                    Picasso.get().load(cover).into(coverIv);
                }catch (Exception e){

                }

            }
        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(TheirProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

    });
        postList = new ArrayList<>();

        checkUserStatus();
        loadHistPosts();
    }

    private void loadHistPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerView.setLayoutManager(layoutManager);
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    //adapter
                    adapterPosts = new AdapterPosts(TheirProfileActivity.this, postList);
                    //set adapter to reyclerview
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(TheirProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchHistPosts(final String searchQuery){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerView.setLayoutManager(layoutManager);
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    //extra from load posts
                    if(modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(modelPost);
                    }
                    //extra from load posts

                    postList.add(modelPost);

                    //adapter
                    adapterPosts = new AdapterPosts(TheirProfileActivity.this, postList);
                    //set adapter to reyclerview
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(TheirProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            //mProfileTv.setText((user.getEmail()));
        }else{
            startActivity(new Intent(this,DashboardActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_add_post).setVisible(false);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //serach listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search
                if(!TextUtils.isEmpty(s)){
                    searchHistPosts(s);
                }else {
                    loadHistPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called when user press any letter
                if(!TextUtils.isEmpty(s)){
                    searchHistPosts(s);
                }else {
                    loadHistPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


}
