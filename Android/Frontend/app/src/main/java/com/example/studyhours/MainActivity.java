package com.example.studyhours;

import android.content.Intent;
import android.os.Bundle;

import com.example.studyhours.ui.gallery.GalleryFragment;
import com.example.studyhours.ui.home.HomeFragment;
import com.google.android.gms.auth.api.Auth;

import android.view.MenuItem;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.*;

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
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_record, R.id.nav_history)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        System.out.println("menu id: " + id);
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_record){
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, new GalleryFragment())
                    .commit();
        }else if (id == R.id.nav_history){
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, new GalleryFragment())
                    .commit();
        }else if (id == R.id.nav_logout){
            mFirebaseAuth.signOut();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            System.out.println("mFirebaseUser: " + mFirebaseUser);
            mUsername = "ANONYMOUS";
            Intent intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
