package com.naimur978.forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.naimur978.forum.BloodDonationMap.FirstPageActivity;
import com.naimur978.forum.CustomDrawer.DrawMenuAdapter;
import com.naimur978.forum.Notifications.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DashboardActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener{

    FirebaseAuth firebaseAuth;

    String mUID;

    private TextView mTextMessage;
    BottomNavigationView navigation;
    ViewPager viewPager;


    private DrawMenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;

    private ArrayList<String> mTitles = new ArrayList<>();

    private FragmentManager fm = null;
    private NoNet mNoNet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fm = getSupportFragmentManager();
        mNoNet = new NoNet();
        mNoNet.initNoNet(this, fm);

        firebaseAuth = FirebaseAuth.getInstance();


        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("View Pager", "onPageSelected: "+position );
                switch (position){
                    case 0:
                        navigation.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        navigation.setSelectedItemId(R.id.nav_profile);
                        break;
                    case 2:
                        navigation.setSelectedItemId(R.id.nav_add);
                        break;
                    case 3:
                        navigation.setSelectedItemId(R.id.nav_users);
                        break;

                    case 4:
                        navigation.setSelectedItemId(R.id.nav_chat);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        checkUserStatus();




        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));


        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        goToFragment(new HomeFragment(), false);
        mMenuAdapter.setViewSelected(0, true);
        setTitle(mTitles.get(0));

    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new DrawMenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.add(R.id.viewPager, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {




        // Navigate to the right fragment
        switch (position) {
            case 1:
                startActivity(new Intent(this, FirstPageActivity.class));
                break;


        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }



    @Override
    protected void onResume() {
        checkUserStatus();
        mNoNet.RegisterNoNet();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mNoNet.unRegisterNoNet();
        super.onPause();
    }

    //add token for notification
    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);

    }

    @Override
    public void onFooterClicked() {


            firebaseAuth.signOut();

            startActivity(new Intent(DashboardActivity.this,RegisterActivity.class));
            finish();

    }

    @Override
    public void onHeaderClicked() {
       // Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.nav_profile:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.nav_add:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.nav_users:
                    viewPager.setCurrentItem(3);
                    return true;
                case R.id.nav_chat:
                    viewPager.setCurrentItem(4);
                    return true;
            }
            return false;
        }
    };

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            //mProfileTv.setText((user.getEmail()));
            mUID = user.getUid();

            //save currently logged in user id in sharedpreference
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply();
            }

            updateToken(FirebaseInstanceId.getInstance().getToken());


        }else{
            startActivity(new Intent(DashboardActivity.this,DashboardActivity.class));
            finish();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new ProfileFragment(), "Profile");
        adapter.addFragment(new CreatePostFragment(), "Post");
        adapter.addFragment(new UsersFragment(), "Users");
        adapter.addFragment(new ChatListFragment(), "Chat");

        viewPager.setAdapter(adapter);
    }




    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    public class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        public ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

}
