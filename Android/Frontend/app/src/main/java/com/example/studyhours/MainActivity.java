package com.example.studyhours;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;

import com.example.studyhours.ui.gallery.GalleryFragment;
import com.example.studyhours.ui.home.HomeFragment;
import com.example.studyhours.ui.sistersHours.SistersHoursFragment;
import com.google.android.gms.auth.api.Auth;

import android.util.Pair;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView navName;
    private TextView navEmail;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private HomeFragment homeFragment = new HomeFragment();
    private GalleryFragment galleryFragment = new GalleryFragment();
    private SistersHoursFragment sistersHoursFragment = new SistersHoursFragment();
    private Fragment fragment;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mUsername = "ANONYMOUS";

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        System.out.println("mFirebaseUser: " + mFirebaseUser);
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, EmailPasswordActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            System.out.println("username: " + mUsername);
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        //TODO: Name and email does not appear on the navigation bar
//        setContentView(R.layout.nav_header_navigation);
//        navName = findViewById(R.id.nav_name);
//        navEmail = findViewById(R.id.nav_email);
//        navName.setText(getString(R.string.nav_header_title,
//                "Sister name"));
//        navEmail.setText(getString(R.string.nav_header_subtitle,
//                "SisterEmail@test.com"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Fragment newFragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, newFragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

//        System.out.println("navigationView.findViewById(R.id.nav_sisters_hours)" + navigationView.findViewById(R.id.nav_sisters_hours));
        navigationView.setNavigationItemSelectedListener(this);
        //TODO: disable sister Hours MenuItem for non-admin users.
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference usersRef = database.getReference("users/"+mFirebaseUser.getUid()+"/admin");
//        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue(Boolean.class)){
//                    NavigationView navigationView = findViewById(R.id.nav_view);
//                    MenuItem sistersHours = navigationView.findViewById(R.id.nav_sisters_hours);
//                    sistersHours.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_record, R.id.nav_history)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        displayView(R.id.nav_record);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.nav_logout){
            mFirebaseAuth.signOut();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            System.out.println("mFirebaseUser: " + mFirebaseUser);
            mUsername = "ANONYMOUS";
            Intent intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }else{
            displayView(id);
        }
        return true;
    }

    public void displayView(int id) {

        String title = "";

        if (id == R.id.nav_record){
            title  = "Record Hours";
            fragment = homeFragment;
        }else if (id == R.id.nav_history) {
            title = "Hours History";
            fragment = galleryFragment;
        }else if (id == R.id.nav_sisters_hours){
            title = "All Sisters Hours";
//            final FirebaseDatabase database = FirebaseDatabase.getInstance();
//            final DatabaseReference usersRef = database.getReference("users/"+mFirebaseUser.getUid()+"/admin");
//            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    System.out.println("dataSnapshot.getKey()" + dataSnapshot.getKey());
//                    System.out.println("dataSnapshot.getVale()" + dataSnapshot.getValue());
//                    if(dataSnapshot.getValue(Boolean.class)){
//                        fragment = sistersHoursFragment;
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
            fragment = sistersHoursFragment;
        }

        if (fragment != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
